alter table public.noki_profiles add column username text,
 add column username_changed_at timestamptz;
alter table public.noki_profiles add constraint native_username_format
 check(username is null or username ~ '^[A-Za-z0-9_]{3,24}$');
create unique index noki_profile_username_unique on public.noki_profiles(lower(username)) where username is not null;

create or replace function noki_private.profile_action(op text,args jsonb default '{}'::jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare actor uuid:=auth.uid(); current_version text; p public.noki_profiles; n text;
begin
 if not noki_private.verified_user() then raise exception 'NOKI_SESSION'; end if;
 select legal_version into current_version from public.noki_client_config where id;
 perform pg_advisory_xact_lock(hashtextextended(actor::text,51008));
 if op='accept' then
  if args->>'version' is distinct from current_version or args->>'terms' is distinct from 'true' or args->>'privacy' is distinct from 'true' then raise exception 'NOKI_CONSENT'; end if;
  insert into noki_private.account_consents(user_id,version) values(actor,current_version)
   on conflict(user_id,version) do update set accepted_at=case when noki_private.account_consents.revoked_at is null then noki_private.account_consents.accepted_at else now() end,revoked_at=null;
 elsif op='revoke' then
  update noki_private.account_consents set revoked_at=now() where user_id=actor and revoked_at is null;
  return '{}'::jsonb;
 elsif not exists(select 1 from noki_private.account_consents where user_id=actor and version=current_version and revoked_at is null) then raise exception 'NOKI_CONSENT';
 end if;
 if op not in ('accept','state','name','username','photo_changed') then raise exception 'NOKI_INPUT'; end if;
 insert into public.noki_profiles(user_id,display_name)
  select actor,case when char_length(btrim(coalesce(raw_user_meta_data->>'display_name',''))) between 2 and 80 then btrim(raw_user_meta_data->>'display_name') else '' end from auth.users where id=actor
  on conflict(user_id) do nothing;
 select * into p from public.noki_profiles where user_id=actor for update;
 if op='username' then
  n:=btrim(args->>'username');
  if n is null or n !~ '^[A-Za-z0-9_]{3,24}$' then raise exception 'NOKI_USERNAME_FORMAT'; end if;
  if n is distinct from p.username then
   if p.username_changed_at is not null and now()<p.username_changed_at+interval '30 days' then raise exception 'NOKI_USERNAME_WAIT'; end if;
   begin
    update public.noki_profiles set username=n,username_changed_at=now(),updated_at=now() where user_id=actor;
   exception when unique_violation then raise exception 'NOKI_USERNAME_TAKEN'; end;
  end if;
 elsif op='name' then
  n:=btrim(args->>'name');
  if n is null or char_length(n) not between 2 and 80 then raise exception 'NOKI_INPUT'; end if;
  update public.noki_profiles set display_name=n,updated_at=now() where user_id=actor;
 elsif op='photo_changed' then update public.noki_profiles set updated_at=now() where user_id=actor;
 end if;
 select * into p from public.noki_profiles where user_id=actor;
 return to_jsonb(p)||jsonb_build_object('has_photo',exists(select 1 from storage.objects where bucket_id='noki-profile-photos' and name=actor::text||'/avatar.jpg'),
  'username_change_available_at',case when p.username_changed_at is null then null else p.username_changed_at+interval '30 days' end,
  'can_change_username',p.username_changed_at is null or now()>=p.username_changed_at+interval '30 days');
end; $$;
revoke all on function noki_private.profile_action(text,jsonb) from public,anon;
grant execute on function noki_private.profile_action(text,jsonb) to authenticated;

alter table public.noki_devices add column archived_at timestamptz;
-- Removal from the pending list preserves its history. Only the archive operation can set the server date.
revoke delete on public.noki_devices from authenticated;
create function noki_private.archive_device(device_id uuid,expected_version integer) returns jsonb
language plpgsql security definer set search_path='' as $$
declare actor uuid:=auth.uid(); d public.noki_devices;
begin
 if not noki_private.verified_user() then raise exception 'NOKI_SESSION'; end if;
 if not exists(select 1 from noki_private.account_consents c join public.noki_client_config f on f.id and f.legal_version=c.version where c.user_id=actor and c.revoked_at is null) then raise exception 'NOKI_CONSENT'; end if;
 perform pg_advisory_xact_lock(hashtextextended(actor::text,51005));
 select * into d from public.noki_devices where id=device_id and user_id=actor for update;
 if not found then raise exception 'NOKI_INPUT'; end if;
 if d.archived_at is not null then return to_jsonb(d); end if;
 if d.version is distinct from expected_version then raise exception 'NOKI_VERSION'; end if;
 if d.outcome is not null or exists(select 1 from noki_private.handoff_items i join noki_private.handoffs h on h.id=i.handoff_id where i.device_id=d.id and h.status<>'cancelled') then raise exception 'NOKI_LOCKED'; end if;
 update public.noki_devices set archived_at=now() where id=d.id returning * into d;
 return to_jsonb(d);
end; $$;
revoke all on function noki_private.archive_device(uuid,integer) from public,anon;
grant execute on function noki_private.archive_device(uuid,integer) to authenticated;
create function public.noki_archive_device(device_id uuid,expected_version integer) returns jsonb
language sql security invoker set search_path='' as $$select noki_private.archive_device(device_id,expected_version);$$;
revoke all on function public.noki_archive_device(uuid,integer) from public,anon;
grant execute on function public.noki_archive_device(uuid,integer) to authenticated;

create function noki_private.guard_archived_device() returns trigger language plpgsql security invoker set search_path='' as $$
begin
 if old.archived_at is not null then raise exception 'NOKI_ARCHIVED'; end if;
 return new;
end; $$;
revoke all on function noki_private.guard_archived_device() from public,anon,authenticated;
create trigger noki_archived_guard before update on public.noki_devices for each row execute function noki_private.guard_archived_device();

-- Archived drafts must never be included in a new handoff, even through an outdated client.
do $$ declare definition text; begin
 definition:=pg_get_functiondef('noki_private.native_action(text,jsonb)'::regprocedure);
 if position('and outcome is null and decision=' in definition)=0 then raise exception 'Review create-delivery predicate'; end if;
 execute replace(replace(definition,'and outcome is null and decision=','and archived_at is null and outcome is null and decision='),'2026-10-08.1','2026-10-08.2');
end; $$;

-- Username and archive semantics are disclosed before personal functions resume.
update public.noki_client_config set legal_version='2026-10-08.2' where id;
