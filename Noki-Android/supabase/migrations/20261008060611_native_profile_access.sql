create table public.noki_client_config (
 id boolean primary key default true check(id),
 email_otp_enabled boolean not null default false,
 legal_version text not null
);
alter table public.noki_client_config enable row level security;
revoke all on public.noki_client_config from anon,authenticated;
grant select on public.noki_client_config to anon,authenticated;
create policy "read client capabilities" on public.noki_client_config for select to anon,authenticated using(true);
insert into public.noki_client_config values(true,false,'2026-10-08.1');

create function noki_private.verified_user() returns boolean language sql stable security definer set search_path='' as $$
 select auth.uid() is not null
 and exists(select 1 from auth.users where id=auth.uid() and email_confirmed_at is not null)
 and exists(select 1 from auth.sessions where id=nullif(auth.jwt()->>'session_id','')::uuid and user_id=auth.uid() and (not_after is null or not_after>now()));
$$;
revoke all on function noki_private.verified_user() from public,anon;
grant execute on function noki_private.verified_user() to authenticated;

create table public.noki_profiles (
 user_id uuid primary key references auth.users(id),
 display_name text not null default '' check(display_name='' or char_length(btrim(display_name)) between 2 and 80),
 updated_at timestamptz not null default now()
);
alter table public.noki_profiles enable row level security;
revoke all on public.noki_profiles from anon,authenticated;
grant select on public.noki_profiles to authenticated;
create policy "read own native profile" on public.noki_profiles for select to authenticated
 using(user_id=(select auth.uid()) and (select noki_private.verified_user()));
create table noki_private.account_consents (
 user_id uuid not null references auth.users(id), version text not null,
 accepted_at timestamptz not null default now(), revoked_at timestamptz,
 primary key(user_id,version)
);
alter table noki_private.account_consents enable row level security;
revoke all on noki_private.account_consents from public,anon,authenticated;

create function noki_private.profile_action(op text,args jsonb default '{}'::jsonb) returns jsonb
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
 if op not in ('accept','state','name','photo_changed') then raise exception 'NOKI_INPUT'; end if;
 insert into public.noki_profiles(user_id,display_name)
  select actor,case when char_length(btrim(coalesce(raw_user_meta_data->>'display_name',''))) between 2 and 80 then btrim(raw_user_meta_data->>'display_name') else '' end from auth.users where id=actor
  on conflict(user_id) do nothing;
 if op='name' then
  n:=btrim(args->>'name');
  if n is null or char_length(n) not between 2 and 80 then raise exception 'NOKI_INPUT'; end if;
  update public.noki_profiles set display_name=n,updated_at=now() where user_id=actor;
 elsif op='photo_changed' then update public.noki_profiles set updated_at=now() where user_id=actor;
 end if;
 select * into p from public.noki_profiles where user_id=actor;
 return to_jsonb(p)||jsonb_build_object('has_photo',exists(select 1 from storage.objects where bucket_id='noki-profile-photos' and name=actor::text||'/avatar.jpg'));
end; $$;
revoke all on function noki_private.profile_action(text,jsonb) from public,anon;
grant execute on function noki_private.profile_action(text,jsonb) to authenticated;
create function public.noki_profile_action(op text,args jsonb default '{}'::jsonb) returns jsonb
 language sql security invoker set search_path='' as $$select noki_private.profile_action(op,args);$$;
revoke all on function public.noki_profile_action(text,jsonb) from public,anon;
grant execute on function public.noki_profile_action(text,jsonb) to authenticated;

create function noki_private.own_profile_photo(path text) returns boolean language sql stable security definer set search_path='' as $$
 select noki_private.verified_user() and path=auth.uid()::text||'/avatar.jpg'
 and exists(select 1 from noki_private.account_consents c join public.noki_client_config f on f.id and f.legal_version=c.version where c.user_id=auth.uid() and c.revoked_at is null);
$$;
revoke all on function noki_private.own_profile_photo(text) from public,anon;
grant execute on function noki_private.own_profile_photo(text) to authenticated;
insert into storage.buckets(id,name,public,file_size_limit,allowed_mime_types)
 values('noki-profile-photos','noki-profile-photos',false,524288,array['image/jpeg']);
create policy "own profile photo read" on storage.objects for select to authenticated using(bucket_id='noki-profile-photos' and noki_private.own_profile_photo(name));
create policy "own profile photo insert" on storage.objects for insert to authenticated with check(bucket_id='noki-profile-photos' and noki_private.own_profile_photo(name));
create policy "own profile photo update" on storage.objects for update to authenticated using(bucket_id='noki-profile-photos' and noki_private.own_profile_photo(name)) with check(bucket_id='noki-profile-photos' and noki_private.own_profile_photo(name));
create policy "own profile photo delete" on storage.objects for delete to authenticated using(bucket_id='noki-profile-photos' and noki_private.own_profile_photo(name));

-- The receiver-specific consent must use the same published legal revision.
do $$ declare definition text; begin
 definition:=pg_get_functiondef('noki_private.native_action(text,jsonb)'::regprocedure);
 if position('2026-10-05.2' in definition)=0 then raise exception 'Review the receiver consent version before applying'; end if;
 execute replace(definition,'2026-10-05.2','2026-10-08.1');
end; $$;
