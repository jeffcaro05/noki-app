-- Native-app reception. Independent of the historical web application's tables.
create schema if not exists noki_private;
revoke all on schema noki_private from public, anon, authenticated;
grant usage on schema noki_private to authenticated;

create table noki_private.receiver_requests (
 user_id uuid primary key references auth.users(id) on delete cascade,
 id uuid not null unique default gen_random_uuid(),
 full_name text not null check(length(trim(full_name)) between 5 and 120),
 place_name text not null check(length(trim(place_name)) between 3 and 100),
 address text not null check(length(trim(address)) between 10 and 240),
 hours text not null check(length(trim(hours)) between 5 and 200),
 categories text[] not null check(cardinality(categories) between 1 and 6),
 conditions text not null check(length(trim(conditions)) between 10 and 500),
 photos text[] not null default '{}',
 status text not null default 'draft' check(status in ('draft','pending','needs_changes','approved','suspended','withdrawn')),
 review_note text, reviewed_at timestamptz, reviewed_by text,
 legal_version text not null, accepted_at timestamptz not null default now(),
 created_at timestamptz not null default now(), updated_at timestamptz not null default now()
);
create table noki_private.handoffs (
 id uuid primary key, sender_id uuid not null references auth.users(id),
 receiver_id uuid not null references noki_private.receiver_requests(user_id),
 receiver_name text not null, address text not null,
 status text not null default 'pending' check(status in ('pending','received','completed','cancelled')),
 created_at timestamptz not null default now(), received_at timestamptz, completed_at timestamptz,
 cancelled_at timestamptz, cancel_reason text,
 check(sender_id<>receiver_id)
);
create unique index one_native_active_delivery on noki_private.handoffs(sender_id) where status in ('pending','received');
create index native_receiver_deliveries on noki_private.handoffs(receiver_id,created_at desc);
create index native_sender_history on noki_private.handoffs(sender_id,created_at desc);
create table noki_private.handoff_items (
 handoff_id uuid not null references noki_private.handoffs(id),
 device_id uuid not null references public.noki_devices(id),
 description text not null, category text not null, safety_state text not null,
 primary key(handoff_id,device_id)
);
create index native_item_device on noki_private.handoff_items(device_id);
create table noki_private.handoff_tokens (
 handoff_id uuid primary key references noki_private.handoffs(id),
 qr_hash bytea not null unique, code_hash bytea not null unique,
 expires_at timestamptz not null, previewed_at timestamptz
);
create table noki_private.code_attempts (
 user_id uuid primary key references auth.users(id) on delete cascade,
 started_at timestamptz not null, attempts integer not null
);
create table noki_private.receiver_audit (
 id bigint generated always as identity primary key, user_id uuid not null,
 reviewer text not null, status text not null, note text not null, created_at timestamptz not null default now()
);
alter table noki_private.receiver_requests enable row level security;
alter table noki_private.handoffs enable row level security;
alter table noki_private.handoff_items enable row level security;
alter table noki_private.handoff_tokens enable row level security;
alter table noki_private.code_attempts enable row level security;
alter table noki_private.receiver_audit enable row level security;
revoke all on all tables in schema noki_private from public, anon, authenticated;

-- Privilege is restricted to this audited dispatcher; clients cannot change role/status/table rows.
create function noki_private.native_action(op text, args jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare
 actor uuid:=auth.uid(); req noki_private.receiver_requests; h noki_private.handoffs;
 ids uuid[]; target uuid; token text; short_code text; result jsonb; found_id uuid; tries integer;
begin
 if actor is null or not exists(select 1 from auth.users u where u.id=actor and u.email_confirmed_at is not null)
 or not exists(select 1 from auth.sessions s where s.id=nullif(auth.jwt()->>'session_id','')::uuid and s.user_id=actor and (s.not_after is null or s.not_after>now()))
 then raise exception 'NOKI_SESSION'; end if;
 -- Serializes writes from two phones of the same person; the partial unique index is a second guard.
 perform pg_advisory_xact_lock(hashtextextended(actor::text, 51005));
 if op='state' then
   return jsonb_build_object(
    'application',(select to_jsonb(r)-'reviewed_by' from noki_private.receiver_requests r where user_id=actor),
    'receivers',coalesce((select jsonb_agg(jsonb_build_object('id',user_id,'name',place_name,'address',address,'hours',hours,'categories',categories,'conditions',conditions)) from noki_private.receiver_requests where status='approved' and user_id<>actor),'[]'::jsonb),
    'deliveries',coalesce((select jsonb_agg(to_jsonb(d)||jsonb_build_object('items',(select jsonb_agg(to_jsonb(i)) from noki_private.handoff_items i where handoff_id=d.id))) from noki_private.handoffs d where sender_id=actor or receiver_id=actor),'[]'::jsonb));
 elsif op='request_draft' then
   if (args->>'legal_version') is distinct from '2026-10-05.2' or (args->>'accepted') is distinct from 'true' or (args->>'adult_authorized') is distinct from 'true' then raise exception 'NOKI_CONSENT'; end if;
   select * into req from noki_private.receiver_requests where user_id=actor for update;
   if found and req.status not in ('draft','needs_changes','withdrawn') then raise exception 'NOKI_REQUEST_LOCKED'; end if;
   if jsonb_typeof(args->'categories')<>'array' then raise exception 'NOKI_INPUT'; end if;
   insert into noki_private.receiver_requests(user_id,full_name,place_name,address,hours,categories,conditions,legal_version)
    values(actor,trim(args->>'full_name'),trim(args->>'place_name'),trim(args->>'address'),trim(args->>'hours'),array(select jsonb_array_elements_text(args->'categories')),trim(args->>'conditions'),args->>'legal_version')
    on conflict(user_id) do update set full_name=excluded.full_name,place_name=excluded.place_name,address=excluded.address,hours=excluded.hours,categories=excluded.categories,conditions=excluded.conditions,
     status='draft',legal_version=excluded.legal_version,accepted_at=now(),updated_at=now()
    returning * into req;
   if not req.categories <@ array['phone','computer','audio_video','accessories','appliance','other'] then raise exception 'NOKI_INPUT'; end if;
   return jsonb_build_object('id',req.id);
 elsif op='request_submit' then
   select * into req from noki_private.receiver_requests where user_id=actor for update;
   if not found or req.status<>'draft' then raise exception 'NOKI_REQUEST_LOCKED'; end if;
   for tries in 1..3 loop
    if not exists(select 1 from storage.objects where bucket_id='noki-receiver-places' and name=actor::text||'/'||req.id::text||'/'||tries||'.jpg') then raise exception 'NOKI_PHOTOS'; end if;
   end loop;
   update noki_private.receiver_requests set status='pending',photos=array[actor::text||'/'||req.id||'/1.jpg',actor::text||'/'||req.id||'/2.jpg',actor::text||'/'||req.id||'/3.jpg'],updated_at=now() where user_id=actor;
 elsif op='create' then
   if exists(select 1 from noki_private.handoffs where id=(args->>'id')::uuid and sender_id=actor) then return jsonb_build_object('id',(args->>'id')::uuid); end if;
   if exists(select 1 from noki_private.handoffs where sender_id=actor and status in ('pending','received')) then raise exception 'NOKI_ACTIVE'; end if;
   target:=(args->>'receiver_id')::uuid;
   select * into req from noki_private.receiver_requests where user_id=target and status='approved' for share;
   if not found or target=actor then raise exception 'NOKI_RECEIVER'; end if;
   select array_agg(distinct x::uuid) into ids from jsonb_array_elements_text(args->'device_ids') x;
   if cardinality(ids) is null or cardinality(ids) not between 1 and 20 then raise exception 'NOKI_ITEMS'; end if;
   -- Lock the devices so edits cannot race with the immutable handoff snapshot.
   perform 1 from public.noki_devices where id=any(ids) order by id for update;
   if (select count(*) from public.noki_devices d where id=any(ids) and user_id=actor and outcome is null and decision='deliver'
      and category=any(req.categories) and not exists(select 1 from jsonb_each_text(d.preparation) where value='pending')
      and not exists(select 1 from noki_private.handoff_items i join noki_private.handoffs a on a.id=i.handoff_id where i.device_id=d.id and a.status<>'cancelled')) <> cardinality(ids) then raise exception 'NOKI_ITEMS'; end if;
   if exists(select 1 from noki_private.handoffs where sender_id=actor and status in ('pending','received')) then raise exception 'NOKI_ACTIVE'; end if;
   insert into noki_private.handoffs(id,sender_id,receiver_id,receiver_name,address) values((args->>'id')::uuid,actor,target,req.place_name,req.address) returning * into h;
   insert into noki_private.handoff_items select h.id,id,description,category,safety_state from public.noki_devices where id=any(ids);
   return jsonb_build_object('id',h.id);
 elsif op='token' then
   select * into h from noki_private.handoffs where id=(args->>'id')::uuid and sender_id=actor for update;
   if not found or h.status<>'pending' then raise exception 'NOKI_STATE'; end if;
   if not exists(select 1 from noki_private.receiver_requests where user_id=h.receiver_id and status='approved') then raise exception 'NOKI_RECEIVER'; end if;
   token:=encode(extensions.gen_random_bytes(32),'hex'); short_code:=upper(encode(extensions.gen_random_bytes(5),'hex'));
   insert into noki_private.handoff_tokens(handoff_id,qr_hash,code_hash,expires_at) values(h.id,extensions.digest(token,'sha256'),extensions.digest(short_code,'sha256'),now()+interval '10 minutes')
    on conflict(handoff_id) do update set qr_hash=excluded.qr_hash,code_hash=excluded.code_hash,expires_at=excluded.expires_at,previewed_at=null;
   return jsonb_build_object('id',h.id,'qr','NOKI1:'||h.id::text||':'||token,'code',short_code,'expires_at',now()+interval '10 minutes');
 elsif op='preview' then
   if not exists(select 1 from noki_private.receiver_requests where user_id=actor and status='approved') then raise exception 'NOKI_RECEIVER'; end if;
   insert into noki_private.code_attempts values(actor,now(),1) on conflict(user_id) do update set
    started_at=case when code_attempts.started_at<now()-interval '15 minutes' then now() else code_attempts.started_at end,
    attempts=case when code_attempts.started_at<now()-interval '15 minutes' then 1 else code_attempts.attempts+1 end returning attempts into tries;
   if tries>5 then return jsonb_build_object('error','NOKI_RATE'); end if;
   token:=trim(args->>'code');
   if token ~ '^NOKI1:[0-9a-f-]{36}:[0-9a-f]{64}$' then
    select d.id into found_id from noki_private.handoffs d join noki_private.handoff_tokens t on t.handoff_id=d.id where d.receiver_id=actor and d.status='pending' and t.expires_at>now() and d.id::text=split_part(token,':',2) and t.qr_hash=extensions.digest(split_part(token,':',3),'sha256') for update of d;
   elsif upper(token) ~ '^[0-9A-F]{10}$' then
    select d.id into found_id from noki_private.handoffs d join noki_private.handoff_tokens t on t.handoff_id=d.id where d.receiver_id=actor and d.status='pending' and t.expires_at>now() and t.code_hash=extensions.digest(upper(token),'sha256') for update of d;
   end if;
   if found_id is null then return jsonb_build_object('error','NOKI_CODE'); end if;
   update noki_private.handoff_tokens set previewed_at=now() where handoff_id=found_id;
   delete from noki_private.code_attempts where user_id=actor;
   return jsonb_build_object('id',found_id);
 elsif op in ('receive','complete','cancel') then
   select * into h from noki_private.handoffs where id=(args->>'id')::uuid and (sender_id=actor or receiver_id=actor) for update;
   if not found then raise exception 'NOKI_STATE'; end if;
   if op='receive' then
    if h.receiver_id<>actor or not exists(select 1 from noki_private.receiver_requests where user_id=actor and status='approved') then raise exception 'NOKI_RECEIVER'; end if;
    if h.status in ('received','completed') then return '{}'::jsonb; end if;
    if h.status<>'pending' or not exists(select 1 from noki_private.handoff_tokens where handoff_id=h.id and previewed_at is not null and expires_at>now()) then raise exception 'NOKI_CODE'; end if;
    update noki_private.handoffs set status='received',received_at=now() where id=h.id;
    delete from noki_private.handoff_tokens where handoff_id=h.id;
   elsif op='complete' then
    if h.sender_id<>actor then raise exception 'NOKI_STATE'; end if;
    if h.status='completed' then return '{}'::jsonb; end if;
    if h.status<>'received' then raise exception 'NOKI_WAIT'; end if;
    update noki_private.handoffs set status='completed',completed_at=now() where id=h.id;
   else
    if h.sender_id<>actor or h.status<>'pending' then raise exception 'NOKI_CANNOT_CANCEL'; end if;
    if length(trim(args->>'reason')) not between 5 and 300 then raise exception 'NOKI_INPUT'; end if;
    update noki_private.handoffs set status='cancelled',cancelled_at=now(),cancel_reason=trim(args->>'reason') where id=h.id;
    delete from noki_private.handoff_tokens where handoff_id=h.id;
   end if;
 else raise exception 'NOKI_INPUT'; end if;
 return '{}'::jsonb;
end $$;
revoke all on function noki_private.native_action(text,jsonb) from public, anon;
grant execute on function noki_private.native_action(text,jsonb) to authenticated;
create function public.noki_native_action(op text,args jsonb default '{}'::jsonb) returns jsonb
language sql security invoker set search_path='' as $$ select noki_private.native_action(op,args); $$;
revoke all on function public.noki_native_action(text,jsonb) from public, anon;
grant execute on function public.noki_native_action(text,jsonb) to authenticated;

-- Enforces results and immutable handoffs even against a modified client.
create function noki_private.guard_device_handoff() returns trigger language plpgsql security definer set search_path='' as $$
declare device uuid; owner_id uuid;
begin
 if tg_op='INSERT' then device:=new.id; owner_id:=new.user_id; else device:=old.id; owner_id:=old.user_id; end if;
 if auth.uid() is distinct from owner_id then raise exception 'NOKI_SESSION'; end if;
 if exists(select 1 from noki_private.handoff_items i join noki_private.handoffs h on h.id=i.handoff_id where i.device_id=device and h.status in ('pending','received')) then raise exception 'NOKI_LOCKED'; end if;
 if tg_op='DELETE' then
  if exists(select 1 from noki_private.handoff_items where device_id=device) then raise exception 'NOKI_HISTORY'; end if;
  return old;
 end if;
 if tg_op='UPDATE' and exists(select 1 from noki_private.handoff_items i join noki_private.handoffs h on h.id=i.handoff_id where i.device_id=device and h.status='completed') and
  (new.decision is distinct from old.decision or new.description is distinct from old.description or new.category is distinct from old.category or new.selected_point_id is distinct from old.selected_point_id or new.safety_state is distinct from old.safety_state or new.function_state is distinct from old.function_state or new.preparation is distinct from old.preparation or new.outcome is null) then raise exception 'NOKI_HISTORY'; end if;
 if new.outcome is not null and (new.decision='deliver' or new.outcome='delivered') and not exists(select 1 from noki_private.handoff_items i join noki_private.handoffs h on h.id=i.handoff_id where i.device_id=device and h.sender_id=owner_id and h.status='completed') then raise exception 'NOKI_WAIT'; end if;
 if new.decision='deliver' and new.outcome is not null and new.outcome<>'delivered' then raise exception 'NOKI_INPUT'; end if;
 return new;
end $$;
revoke all on function noki_private.guard_device_handoff() from public, anon, authenticated;
create trigger noki_handoff_guard before insert or update or delete on public.noki_devices for each row execute function noki_private.guard_device_handoff();

-- Photos only, no identity document or biometric collection.
insert into storage.buckets(id,name,public,file_size_limit,allowed_mime_types) values('noki-receiver-places','noki-receiver-places',false,3145728,array['image/jpeg']);
create function noki_private.own_place_photo(path text,writing boolean) returns boolean language sql stable security definer set search_path='' as $$
 select auth.uid() is not null and exists(select 1 from auth.sessions s where s.id=nullif(auth.jwt()->>'session_id','')::uuid and s.user_id=auth.uid() and (s.not_after is null or s.not_after>now())) and exists(select 1 from noki_private.receiver_requests r where r.user_id=auth.uid() and path in (r.user_id::text||'/'||r.id||'/1.jpg',r.user_id::text||'/'||r.id||'/2.jpg',r.user_id::text||'/'||r.id||'/3.jpg') and (not writing or r.status='draft'));
$$;
revoke all on function noki_private.own_place_photo(text,boolean) from public, anon;
grant execute on function noki_private.own_place_photo(text,boolean) to authenticated;
create policy "native place photo read own" on storage.objects for select to authenticated using(bucket_id='noki-receiver-places' and noki_private.own_place_photo(name,false));
create policy "native place photo insert own draft" on storage.objects for insert to authenticated with check(bucket_id='noki-receiver-places' and noki_private.own_place_photo(name,true));
create policy "native place photo update own draft" on storage.objects for update to authenticated using(bucket_id='noki-receiver-places' and noki_private.own_place_photo(name,true)) with check(bucket_id='noki-receiver-places' and noki_private.own_place_photo(name,true));

-- Only a trusted server-side operator can record a manual decision. Never exposed as client RPC.
create function noki_private.review_receiver(person uuid,decision text,reviewer text,note text) returns void
language plpgsql security invoker set search_path='' as $$
begin
 if decision not in ('approved','needs_changes','suspended') or length(trim(reviewer))<5 or length(trim(note))<10 then raise exception 'Incomplete review'; end if;
 update noki_private.receiver_requests set status=decision,reviewed_by=reviewer,review_note=note,reviewed_at=now(),updated_at=now() where user_id=person and status in ('pending','approved','suspended');
 if not found then raise exception 'Request not reviewable'; end if;
 insert into noki_private.receiver_audit(user_id,reviewer,status,note) values(person,reviewer,decision,note);
end $$;
revoke all on function noki_private.review_receiver(uuid,text,text,text) from public, anon, authenticated;
