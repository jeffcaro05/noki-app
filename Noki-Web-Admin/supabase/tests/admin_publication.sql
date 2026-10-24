-- End-to-end data contract test: synthetic request and point are rolled back.
begin;
do $$
declare owner_id uuid; target_id uuid; sid uuid:=gen_random_uuid(); rid uuid:=gen_random_uuid();
 stamp timestamptz; revision_before bigint; result jsonb; expected boolean; point_id uuid;
begin
 select user_id into owner_id from noki_private.admin_members where email='caroajeffry@gmail.com';
 select u.id into target_id from auth.users u where u.email_confirmed_at is not null
 and not exists(select 1 from noki_private.receiver_requests r where r.user_id=u.id)
 and not exists(select 1 from noki_private.admin_members m where m.user_id=u.id) limit 1;
 if target_id is null then raise exception 'Test account unavailable'; end if;
 insert into auth.sessions(id,user_id,created_at,updated_at) values(sid,owner_id,now(),now());
 insert into auth.mfa_amr_claims(id,session_id,created_at,updated_at,authentication_method) values(gen_random_uuid(),sid,now(),now(),'otp');
 perform set_config('request.jwt.claims',jsonb_build_object('sub',owner_id,'session_id',sid,'role','authenticated')::text,true);
 perform public.noki_admin_action('open');
 insert into noki_private.receiver_requests(user_id,id,full_name,place_name,address,hours,categories,conditions,photos,status,legal_version,accepted_at,latitude,longitude,public_whatsapp)
 values(target_id,rid,'Responsable de prueba','Lugar de prueba transaccional','Dirección ficticia solo de prueba','Solo prueba sin publicación permanente',array['computer'],'Condiciones ficticias para probar el contrato',array[target_id::text||'/'||rid||'/1.jpg',target_id::text||'/'||rid||'/2.jpg',target_id::text||'/'||rid||'/3.jpg'],'pending','test-only',now(),6.2442,-75.5812,'573000000000');
 select updated_at into stamp from noki_private.receiver_requests where id=rid;
 select revision into revision_before from public.noki_admin_sync where topic='points';
 perform public.noki_admin_action('review_receiver',jsonb_build_object('id',rid,'expected_updated_at',stamp,'decision','approved','note','Synthetic transaction test, rolled back'));
 if not exists(select 1 from public.noki_collection_points where receiver_request_id=rid and active and name='Lugar de prueba transaccional') then raise exception 'FAIL: approved receiver missing from app'; end if;
 if (select revision from public.noki_admin_sync where topic='points')<=revision_before then raise exception 'FAIL: notification not updated'; end if;
 expected:=false;
 begin perform public.noki_admin_action('review_receiver',jsonb_build_object('id',rid,'expected_updated_at',stamp-interval '1 second','decision','suspended','note','Synthetic transaction test, rolled back'));
 exception when others then expected:=sqlerrm='NOKI_ADMIN_CONFLICT'; end;
 if not expected then raise exception 'FAIL: stale review accepted'; end if;
 select updated_at into stamp from noki_private.receiver_requests where id=rid;
 perform public.noki_admin_action('review_receiver',jsonb_build_object('id',rid,'expected_updated_at',stamp,'decision','suspended','note','Synthetic transaction test, rolled back'));
 if exists(select 1 from public.noki_collection_points where receiver_request_id=rid and active) then raise exception 'FAIL: suspended receiver still active'; end if;
 result:=public.noki_admin_action('save_point',jsonb_build_object('name','Lugar de referencia de prueba','address','Dirección ficticia de prueba 123','city','Medellín','latitude',6.2442,'longitude',-75.5812,'hours_text','Lunes a viernes','accepted_categories',jsonb_build_array('computer'),'accepted_details','Condiciones de prueba transaccional','source_name','Fuente de prueba','source_url','https://example.com','verified_at',current_date,'note','Prueba transaccional, no persistir','active',true));
 point_id:=(result->>'id')::uuid;
 if not exists(select 1 from public.noki_collection_points where id=point_id and active and receiver_request_id is null) then raise exception 'FAIL: reference save'; end if;
 expected:=false;
 begin perform public.noki_admin_action('save_point',jsonb_build_object('id',point_id,'expected','{}'::jsonb,'note','Prueba de conflicto de actualización')); exception when others then expected:=sqlerrm='NOKI_ADMIN_CONFLICT'; end;
 if not expected then raise exception 'FAIL: stale point accepted'; end if;
 update noki_private.admin_members set active=false where user_id=owner_id;
 if noki_private.admin_allowed() then raise exception 'FAIL: revoked membership still authorized'; end if;
 update noki_private.admin_members set active=true where user_id=owner_id;
 perform set_config('noki.test_session',sid::text,true);
end $$;
set local role authenticated;
do $$ declare result jsonb; expected boolean; begin
 result:=public.noki_admin_action('overview');
 if result->>'users' is null then raise exception 'FAIL: invoker wrapper cannot read'; end if;
 if (select count(*) from public.noki_admin_sync)=0 then raise exception 'FAIL: authorized realtime RLS'; end if;
 expected:=false;
 begin perform * from noki_private.admin_members; exception when insufficient_privilege then expected:=true; end;
 if not expected then raise exception 'FAIL: direct private table read'; end if;
end $$;
set local role postgres;
delete from auth.sessions where id=current_setting('noki.test_session')::uuid;
do $$ begin if noki_private.admin_allowed() then raise exception 'FAIL: deleted auth session authorized'; end if; end $$;
set local role authenticated;
do $$ begin if (select count(*) from public.noki_admin_sync)<>0 then raise exception 'FAIL: revoked session sees events'; end if; end $$;
set local role postgres;
rollback;
select 'PASS: publication, suspension, notification, concurrency, reference points, membership revocation, authenticated wrapper, RLS and auth-session revocation; all test writes rolled back' as result;
