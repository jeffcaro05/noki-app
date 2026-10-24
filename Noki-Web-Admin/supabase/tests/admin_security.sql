-- Synthetic SQL sessions only; no login tokens or emails. All changes roll back.
begin;
do $$
declare owner_id uuid; outsider_id uuid; sid uuid:=gen_random_uuid(); outsider_session uuid:=gen_random_uuid();
 payload jsonb; r record; expected boolean; request_id uuid; other_owner uuid; stamp timestamptz; target uuid;
begin
 select user_id into owner_id from noki_private.admin_members where email='caroajeffry@gmail.com';
 select user_id into other_owner from noki_private.admin_members where email='oykmelissa@gmail.com';
 select id into outsider_id from auth.users where id not in (owner_id,other_owner) and email_confirmed_at is not null limit 1;
 if owner_id is null or outsider_id is null then raise exception 'Test identities unavailable'; end if;
 insert into auth.sessions(id,user_id,created_at,updated_at) values(sid,owner_id,now(),now()),(outsider_session,outsider_id,now(),now());
 perform set_config('request.jwt.claims',jsonb_build_object('sub',outsider_id,'session_id',outsider_session,'role','authenticated','user_metadata',jsonb_build_object('role','admin'))::text,true);
 expected:=false;
 begin perform public.noki_admin_action('open'); exception when others then expected:=sqlerrm='NOKI_ADMIN_DENIED'; end;
 if not expected then raise exception 'FAIL: non-admin metadata escalation'; end if;
 perform set_config('request.jwt.claims',jsonb_build_object('sub',owner_id,'session_id',sid,'role','authenticated')::text,true);
 expected:=false;
 begin perform public.noki_admin_action('open'); exception when others then expected:=sqlerrm='NOKI_ADMIN_OTP_REQUIRED'; end;
 if not expected then raise exception 'FAIL: OTP required'; end if;
 insert into auth.mfa_amr_claims(id,session_id,created_at,updated_at,authentication_method) values(gen_random_uuid(),sid,now(),now(),'otp');
 payload:=public.noki_admin_action('open');
 if payload->>'email'<>'caroajeffry@gmail.com' then raise exception 'FAIL: admin open'; end if;
 foreach target in array array[owner_id,other_owner] loop
   payload:=public.noki_admin_action('user_detail',jsonb_build_object('id',target));
   if payload->>'email' is null then raise exception 'FAIL: user detail'; end if;
 end loop;
 for r in select unnest(array['overview','users','receivers','points','deliveries','audit','report_export']) op loop
  payload:=public.noki_admin_action(r.op);
  if payload is null then raise exception 'FAIL: read %',r.op; end if;
 end loop;
 for r in select id from noki_private.receiver_requests loop
  payload:=public.noki_admin_action('receiver_detail',jsonb_build_object('id',r.id));
 end loop;
 for r in select id from noki_private.handoffs loop
  payload:=public.noki_admin_action('delivery_detail',jsonb_build_object('id',r.id));
  if payload ? 'code' or payload ? 'token' then raise exception 'FAIL: handoff credential exposure'; end if;
 end loop;
 select id,updated_at into request_id,stamp from noki_private.receiver_requests where user_id=owner_id;
 if request_id is not null then
  expected:=false;
  begin perform public.noki_admin_action('review_receiver',jsonb_build_object('id',request_id,'decision','suspended','note','Synthetic rollback test','expected_updated_at',stamp));
  exception when others then expected:=sqlerrm='NOKI_ADMIN_SELF_REVIEW'; end;
  if not expected then raise exception 'FAIL: self review'; end if;
 end if;
 update noki_private.admin_sessions set last_active_at=now()-interval '31 minutes' where session_id=sid;
 expected:=false;
 begin perform public.noki_admin_action('overview'); exception when others then expected:=sqlerrm='NOKI_ADMIN_EXPIRED'; end;
 if not expected then raise exception 'FAIL: idle expiry'; end if;
 update noki_private.admin_sessions set last_active_at=now(),expires_at=now()-interval '1 second' where session_id=sid;
 if noki_private.admin_allowed() then raise exception 'FAIL: absolute expiry'; end if;
 update noki_private.admin_sessions set expires_at=now()+interval '1 hour' where session_id=sid;
 perform public.noki_admin_action('close');
 if noki_private.admin_allowed() then raise exception 'FAIL: closed session'; end if;
 if has_function_privilege('anon','public.noki_admin_action(text,jsonb)','EXECUTE') then raise exception 'FAIL: anonymous grant'; end if;
 if has_table_privilege('authenticated','noki_private.admin_members','SELECT') then raise exception 'FAIL: direct membership access'; end if;
 if has_table_privilege('authenticated','noki_private.admin_audit','UPDATE') then raise exception 'FAIL: mutable audit'; end if;
end $$;
rollback;
select 'PASS: identity allowlist, user metadata ignored, OTP, all read APIs, no QR exposure, self-review, idle and absolute expiry, closed session and privileges' as result;
