-- Additive administrative API. Does not modify native Auth or handoff functions.
create table noki_private.admin_members (
 user_id uuid primary key references auth.users(id) on delete cascade,
 email text not null unique, name text not null, active boolean not null default true,
 created_at timestamptz not null default now()
);
alter table noki_private.admin_members enable row level security;
revoke all on noki_private.admin_members from public, anon, authenticated;
insert into noki_private.admin_members(user_id,email,name)
select id,lower(email),case lower(email) when 'caroajeffry@gmail.com' then 'Jeffry' else 'Luis Miguel' end
from auth.users where lower(email) in ('caroajeffry@gmail.com','oykmelissa@gmail.com') and email_confirmed_at is not null;
do $$ begin
 if (select count(*) from noki_private.admin_members) <> 2 then raise exception 'Both confirmed owner accounts are required'; end if;
end $$;

create table noki_private.admin_sessions (
 session_id uuid primary key references auth.sessions(id) on delete cascade,
 user_id uuid not null references noki_private.admin_members(user_id),
 opened_at timestamptz not null default now(), last_active_at timestamptz not null default now(),
 expires_at timestamptz not null default now()+interval '8 hours', closed_at timestamptz
);
create index admin_sessions_user_idx on noki_private.admin_sessions(user_id);
alter table noki_private.admin_sessions enable row level security;
revoke all on noki_private.admin_sessions from public, anon, authenticated;

create table noki_private.admin_audit (
 id bigint generated always as identity primary key,
 actor_id uuid not null, actor_email text not null, action text not null,
 target_id text, note text, details jsonb not null default '{}'::jsonb,
 created_at timestamptz not null default now()
);
create index admin_audit_created_idx on noki_private.admin_audit(created_at desc,id desc);
alter table noki_private.admin_audit enable row level security;
revoke all on noki_private.admin_audit from public, anon, authenticated;

create function noki_private.admin_allowed() returns boolean
language sql stable security definer set search_path='' as $$
 select noki_private.verified_user() and exists(
 select 1 from noki_private.admin_members m join auth.users u on u.id=m.user_id
 join noki_private.admin_sessions s on s.user_id=m.user_id
 where m.user_id=auth.uid() and m.active and lower(u.email)=m.email
 and s.session_id=nullif(auth.jwt()->>'session_id','')::uuid
 and s.closed_at is null and s.expires_at>now() and s.last_active_at>now()-interval '30 minutes');
$$;
revoke all on function noki_private.admin_allowed() from public,anon,authenticated;
grant execute on function noki_private.admin_allowed() to authenticated;

create table public.noki_admin_sync (
 topic text primary key, revision bigint not null default 1, changed_at timestamptz not null default now()
);
alter table public.noki_admin_sync enable row level security;
revoke all on public.noki_admin_sync from public,anon,authenticated;
grant select on public.noki_admin_sync to authenticated;
create policy admin_sync_read on public.noki_admin_sync for select to authenticated
 using ((select noki_private.admin_allowed()));
insert into public.noki_admin_sync(topic) values('users'),('receivers'),('points'),('deliveries'),('devices');
create function noki_private.admin_notify() returns trigger
language plpgsql security definer set search_path='' as $$
begin
 insert into public.noki_admin_sync(topic) values(tg_argv[0])
 on conflict(topic) do update set revision=noki_admin_sync.revision+1,changed_at=clock_timestamp();
 return null;
end $$;
revoke all on function noki_private.admin_notify() from public,anon,authenticated;
create trigger admin_profiles_changed after insert or update or delete on public.noki_profiles
 for each statement execute function noki_private.admin_notify('users');
create trigger admin_devices_changed after insert or update or delete on public.noki_devices
 for each statement execute function noki_private.admin_notify('devices');
create trigger admin_receivers_changed after insert or update or delete on noki_private.receiver_requests
 for each statement execute function noki_private.admin_notify('receivers');
create trigger admin_points_changed after insert or update or delete on public.noki_collection_points
 for each statement execute function noki_private.admin_notify('points');
create trigger admin_handoffs_changed after insert or update or delete on noki_private.handoffs
 for each statement execute function noki_private.admin_notify('deliveries');
alter publication supabase_realtime add table public.noki_admin_sync;

create function noki_private.admin_photo_allowed(bucket text,path text) returns boolean
language sql stable security definer set search_path='' as $$
 select noki_private.admin_allowed() and (
  (bucket='noki-receiver-places' and exists(select 1 from noki_private.receiver_requests r
    where r.status<>'draft' and path=any(r.photos)))
  or (bucket='noki-profile-photos' and exists(select 1 from public.noki_profiles p
    where path=p.user_id::text||'/avatar.jpg')));
$$;
revoke all on function noki_private.admin_photo_allowed(text,text) from public,anon,authenticated;
grant execute on function noki_private.admin_photo_allowed(text,text) to authenticated;
create policy noki_admin_photo_read on storage.objects for select to authenticated
 using (noki_private.admin_photo_allowed(bucket_id,name));

create function noki_private.admin_action(op text,args jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare
 person uuid:=auth.uid(); sid uuid:=nullif(auth.jwt()->>'session_id','')::uuid;
 actor noki_private.admin_members; sess noki_private.admin_sessions;
 result jsonb; rows_json jsonb; total bigint; item uuid;
 q text:=left(trim(coalesce(args->>'q','')),120);
 filter text:=coalesce(args->>'status','');
 page_size integer:=25; off integer:=greatest(0,least(coalesce((args->>'offset')::integer,0),100000));
 req noki_private.receiver_requests; point public.noki_collection_points;
 decision text; note text; old_value jsonb;
begin
 if person is null or not noki_private.verified_user() then raise exception 'NOKI_ADMIN_AUTH'; end if;
 select m.* into actor from noki_private.admin_members m join auth.users u on u.id=m.user_id
 where m.user_id=person and m.active and lower(u.email)=m.email;
 if not found then raise exception 'NOKI_ADMIN_DENIED'; end if;
 if op='open' then
  select * into sess from noki_private.admin_sessions where session_id=sid;
  if found then
   if not noki_private.admin_allowed() then raise exception 'NOKI_ADMIN_EXPIRED'; end if;
  else
   if not exists(select 1 from auth.mfa_amr_claims a where a.session_id=sid and a.authentication_method='otp'
      and a.created_at>now()-interval '10 minutes') then raise exception 'NOKI_ADMIN_OTP_REQUIRED'; end if;
   insert into noki_private.admin_sessions(session_id,user_id) values(sid,person);
   insert into noki_private.admin_audit(actor_id,actor_email,action) values(person,actor.email,'session.open');
  end if;
  return jsonb_build_object('id',person,'email',actor.email,'name',actor.name,'idleMinutes',30);
 end if;
 if op='close' then
  update noki_private.admin_sessions set closed_at=now() where session_id=sid and user_id=person and closed_at is null;
  return jsonb_build_object('ok',true);
 end if;
 if not noki_private.admin_allowed() then raise exception 'NOKI_ADMIN_EXPIRED'; end if;
 if op='touch' then
  update noki_private.admin_sessions set last_active_at=now() where session_id=sid;
  return jsonb_build_object('ok',true);
 elsif op='overview' then
  select jsonb_build_object(
   'users',(select count(*) from auth.users where deleted_at is null),
   'confirmedUsers',(select count(*) from auth.users where email_confirmed_at is not null and deleted_at is null),
   'receivers',(select count(*) from noki_private.receiver_requests where status='approved'),
   'pendingReviews',(select count(*) from noki_private.receiver_requests where status='pending'),
   'points',(select count(*) from public.noki_collection_points where active),
   'pendingDeliveries',(select count(*) from noki_private.handoffs where status in ('pending','received')),
   'completedDeliveries',(select count(*) from noki_private.handoffs where status='completed'),
   'cancelledDeliveries',(select count(*) from noki_private.handoffs where status='cancelled'),
   'devices',(select count(*) from public.noki_devices where archived_at is null),
   'deliveredDevices',(select count(*) from noki_private.handoff_items i join noki_private.handoffs h on h.id=i.handoff_id where h.status='completed'),
   'checkedAt',now(),
   'byMonth',(select coalesce(jsonb_agg(x),'[]'::jsonb) from (
     select to_char(bucket,'YYYY-MM') as "month",
       (select count(*) from noki_private.handoffs h where h.status='completed' and h.completed_at>=bucket and h.completed_at<bucket+interval '1 month') completed
     from generate_series(date_trunc('month',now())-interval '5 months',date_trunc('month',now()),interval '1 month') as months(bucket)
   ) x)
  ) into result;
  return result;
 elsif op='users' then
  select count(*) into total from auth.users u left join public.noki_profiles p on p.user_id=u.id
   where u.deleted_at is null and (q='' or concat_ws(' ',u.email,p.username,p.display_name) ilike '%'||q||'%');
  select coalesce(jsonb_agg(x),'[]'::jsonb) into rows_json from (
   select u.id,u.email,u.created_at,u.last_sign_in_at,(u.email_confirmed_at is not null) confirmed,
    p.username,p.display_name,r.status receiver_status,
    (select count(*) from public.noki_devices d where d.user_id=u.id and d.archived_at is null) devices
   from auth.users u left join public.noki_profiles p on p.user_id=u.id
   left join noki_private.receiver_requests r on r.user_id=u.id
   where u.deleted_at is null and (q='' or concat_ws(' ',u.email,p.username,p.display_name) ilike '%'||q||'%')
   order by u.created_at desc,u.id limit page_size offset off
  ) x;
 elsif op='receivers' then
  select count(*) into total from noki_private.receiver_requests r join auth.users u on u.id=r.user_id
   where (filter='' or r.status=filter) and (q='' or concat_ws(' ',r.place_name,r.full_name,u.email,r.address) ilike '%'||q||'%');
  select coalesce(jsonb_agg(x),'[]'::jsonb) into rows_json from (
   select r.id,r.user_id,r.place_name,r.full_name,r.address,r.status,r.created_at,r.updated_at,u.email
   from noki_private.receiver_requests r join auth.users u on u.id=r.user_id
   where (filter='' or r.status=filter) and (q='' or concat_ws(' ',r.place_name,r.full_name,u.email,r.address) ilike '%'||q||'%')
   order by (r.status='pending') desc,r.updated_at desc,r.id limit page_size offset off
  ) x;
 elsif op='deliveries' then
  select count(*) into total from noki_private.handoffs h join auth.users u on u.id=h.sender_id
   where (filter='' or h.status=filter) and (q='' or concat_ws(' ',h.id,h.receiver_name,u.email) ilike '%'||q||'%');
  select coalesce(jsonb_agg(x),'[]'::jsonb) into rows_json from (
   select h.id,h.receiver_name,h.address,h.status,h.created_at,h.completed_at,u.email sender_email,
    (select count(*) from noki_private.handoff_items i where i.handoff_id=h.id) devices
   from noki_private.handoffs h join auth.users u on u.id=h.sender_id
   where (filter='' or h.status=filter) and (q='' or concat_ws(' ',h.id,h.receiver_name,u.email) ilike '%'||q||'%')
   order by h.created_at desc,h.id limit page_size offset off
  ) x;
 elsif op='points' then
  select count(*) into total from public.noki_collection_points p
   where (filter='' or p.active=(filter='active')) and (q='' or concat_ws(' ',p.name,p.address,p.city) ilike '%'||q||'%');
  select coalesce(jsonb_agg(x),'[]'::jsonb) into rows_json from (
   select p.* from public.noki_collection_points p
   where (filter='' or p.active=(filter='active')) and (q='' or concat_ws(' ',p.name,p.address,p.city) ilike '%'||q||'%')
   order by p.active desc,p.name,p.id limit page_size offset off
  ) x;
 elsif op='audit' then
  select count(*) into total from noki_private.admin_audit a
   where q='' or concat_ws(' ',a.actor_email,a.action,a.note,a.target_id) ilike '%'||q||'%';
  select coalesce(jsonb_agg(x),'[]'::jsonb) into rows_json from (
   select a.id,a.actor_email,a.action,a.target_id,a.note,a.created_at from noki_private.admin_audit a
   where q='' or concat_ws(' ',a.actor_email,a.action,a.note,a.target_id) ilike '%'||q||'%'
   order by a.created_at desc,a.id desc limit page_size offset off
  ) x;
 elsif op in ('user_detail','receiver_detail','delivery_detail') then
  item:=(args->>'id')::uuid;
  if op='user_detail' then
   select jsonb_build_object('id',u.id,'email',u.email,'username',p.username,'display_name',p.display_name,
    'created_at',u.created_at,'last_sign_in_at',u.last_sign_in_at,'confirmed',u.email_confirmed_at is not null,
    'devices',(select coalesce(jsonb_agg(x),'[]'::jsonb) from (
      select d.id,d.description,d.category,d.decision,d.outcome,d.archived_at,d.updated_at from public.noki_devices d where d.user_id=u.id order by d.updated_at desc limit 100) x),
    'receiver_status',(select status from noki_private.receiver_requests r where r.user_id=u.id)) into result
   from auth.users u left join public.noki_profiles p on p.user_id=u.id where u.id=item and u.deleted_at is null;
  elsif op='receiver_detail' then
   select to_jsonb(r)||jsonb_build_object('email',u.email) into result
   from noki_private.receiver_requests r join auth.users u on u.id=r.user_id where r.id=item;
  else
   select to_jsonb(h)||jsonb_build_object('sender_email',u.email,'receiver_email',v.email,
    'items',(select coalesce(jsonb_agg(to_jsonb(i)),'[]'::jsonb) from noki_private.handoff_items i where i.handoff_id=h.id)) into result
   from noki_private.handoffs h join auth.users u on u.id=h.sender_id join auth.users v on v.id=h.receiver_id where h.id=item;
  end if;
  if result is null then raise exception 'NOKI_ADMIN_NOT_FOUND'; end if;
  insert into noki_private.admin_audit(actor_id,actor_email,action,target_id) values(person,actor.email,op,item::text);
  return result;
 elsif op='review_receiver' then
  item:=(args->>'id')::uuid; decision:=args->>'decision'; note:=trim(args->>'note');
  select * into req from noki_private.receiver_requests where id=item for update;
  if not found then raise exception 'NOKI_ADMIN_NOT_FOUND'; end if;
  if req.user_id=person then raise exception 'NOKI_ADMIN_SELF_REVIEW'; end if;
  if args->>'expected_updated_at' is null or req.updated_at<>(args->>'expected_updated_at')::timestamptz then raise exception 'NOKI_ADMIN_CONFLICT'; end if;
  if decision is null or decision not in ('approved','needs_changes','suspended') or coalesce(length(note),0) not between 10 and 1000 then raise exception 'NOKI_ADMIN_VALIDATION'; end if;
  if decision='approved' and (req.status not in ('pending','suspended') or cardinality(req.photos)<3 or req.public_whatsapp is null) then raise exception 'NOKI_ADMIN_REVIEW_INCOMPLETE'; end if;
  if decision='needs_changes' and req.status<>'pending' then raise exception 'NOKI_ADMIN_VALIDATION'; end if;
  if decision='suspended' and req.status<>'approved' then raise exception 'NOKI_ADMIN_VALIDATION'; end if;
  perform noki_private.review_receiver(req.user_id,decision,actor.email,note);
  insert into noki_private.admin_audit(actor_id,actor_email,action,target_id,note,details)
   values(person,actor.email,'receiver.'||decision,item::text,note,jsonb_build_object('previous',req.status));
  return jsonb_build_object('ok',true);
 elsif op='save_point' then
  note:=trim(args->>'note');
  if coalesce(length(note),0) not between 10 and 1000 then raise exception 'NOKI_ADMIN_VALIDATION'; end if;
  item:=nullif(args->>'id','')::uuid;
  if item is not null then
   select * into point from public.noki_collection_points where id=item for update;
   if not found then raise exception 'NOKI_ADMIN_NOT_FOUND'; end if;
   if point.receiver_request_id is not null then raise exception 'NOKI_ADMIN_LINKED_POINT'; end if;
   old_value:=to_jsonb(point);
   if args->'expected' is null or args->'expected'<>old_value then raise exception 'NOKI_ADMIN_CONFLICT'; end if;
  end if;
  if coalesce(length(trim(args->>'name')),0) not between 3 and 100
   or coalesce(length(trim(args->>'address')),0) not between 10 and 240
   or coalesce(length(trim(args->>'hours_text')),0) not between 5 and 200
   or coalesce(length(trim(args->>'accepted_details')),0) not between 10 and 500
   or coalesce(args->>'source_url','') !~ '^https://[^ /]+[^ ]*$'
   or coalesce(length(trim(args->>'source_name')),0) not between 3 and 120
   or args->>'latitude' is null or args->>'longitude' is null
   or args->>'verified_at' is null or (args->>'verified_at')::date>current_date
   or jsonb_typeof(args->'accepted_categories') is distinct from 'array'
  then raise exception 'NOKI_ADMIN_VALIDATION'; end if;
  if jsonb_array_length(args->'accepted_categories') not between 1 and 6 or exists(
   select 1 from jsonb_array_elements_text(args->'accepted_categories') c where c not in ('phone','computer','audio_video','accessories','appliance','other')) then raise exception 'NOKI_ADMIN_VALIDATION'; end if;
  if item is null then
   item:=gen_random_uuid();
   insert into public.noki_collection_points(id,slug,name,address,city,latitude,longitude,source_name,source_url,verified_at,
    accepted_categories,accepted_details,hours_text,phone,whatsapp,location_note,active)
   values(item,'reference-'||item,trim(args->>'name'),trim(args->>'address'),left(trim(args->>'city'),100),
    (args->>'latitude')::double precision,(args->>'longitude')::double precision,trim(args->>'source_name'),trim(args->>'source_url'),(args->>'verified_at')::date,
    array(select jsonb_array_elements_text(args->'accepted_categories')),trim(args->>'accepted_details'),trim(args->>'hours_text'),
    left(trim(args->>'phone'),40),nullif(trim(args->>'whatsapp'),''),left(trim(args->>'location_note'),500),coalesce((args->>'active')::boolean,true));
  else
   update public.noki_collection_points set name=trim(args->>'name'),address=trim(args->>'address'),city=left(trim(args->>'city'),100),
    latitude=(args->>'latitude')::double precision,longitude=(args->>'longitude')::double precision,
    source_name=trim(args->>'source_name'),source_url=trim(args->>'source_url'),verified_at=(args->>'verified_at')::date,
    accepted_categories=array(select jsonb_array_elements_text(args->'accepted_categories')),accepted_details=trim(args->>'accepted_details'),
    hours_text=trim(args->>'hours_text'),phone=left(trim(args->>'phone'),40),whatsapp=nullif(trim(args->>'whatsapp'),''),
    location_note=left(trim(args->>'location_note'),500),active=coalesce((args->>'active')::boolean,true) where id=item;
  end if;
  insert into noki_private.admin_audit(actor_id,actor_email,action,target_id,note,details)
   values(person,actor.email,'point.save',item::text,note,jsonb_build_object('previous',old_value));
  return jsonb_build_object('ok',true,'id',item);
 elsif op='photo_access' then
  if not noki_private.admin_photo_allowed(args->>'bucket',args->>'path') then raise exception 'NOKI_ADMIN_DENIED'; end if;
  insert into noki_private.admin_audit(actor_id,actor_email,action,target_id)
   values(person,actor.email,'photo.view',args->>'path');
  return jsonb_build_object('ok',true);
 elsif op='report_export' then
  insert into noki_private.admin_audit(actor_id,actor_email,action,note) values(person,actor.email,'report.export','Resumen agregado, sin datos personales');
  return noki_private.admin_action('overview','{}'::jsonb);
 else raise exception 'NOKI_ADMIN_OPERATION';
 end if;
 return jsonb_build_object('items',rows_json,'total',total,'offset',off,'pageSize',page_size,'checkedAt',now());
end $$;
revoke all on function noki_private.admin_action(text,jsonb) from public,anon,authenticated;
grant execute on function noki_private.admin_action(text,jsonb) to authenticated;

create function public.noki_admin_action(op text,args jsonb default '{}'::jsonb) returns jsonb
language sql security invoker set search_path='' as $$ select noki_private.admin_action(op,args); $$;
revoke all on function public.noki_admin_action(text,jsonb) from public,anon,authenticated;
grant execute on function public.noki_admin_action(text,jsonb) to authenticated;
