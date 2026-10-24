-- Public directory is a safe projection, not direct access to receiver applications.
alter table noki_private.receiver_requests
 add column latitude double precision,
 add column longitude double precision,
 add constraint receiver_location_pair check (
  (latitude is null and longitude is null) or
  (latitude is not null and longitude is not null and latitude between -90 and 90 and longitude between -180 and 180));
alter table public.noki_collection_points
 add column receiver_request_id uuid unique references noki_private.receiver_requests(id) on delete set null;

create function noki_private.publish_receiver_point() returns trigger
language plpgsql security invoker set search_path='' as $fn$
begin
 if tg_op='DELETE' then
  update public.noki_collection_points set active=false where receiver_request_id=old.id;
  return old;
 end if;
 if new.status='approved' and new.latitude is not null and new.longitude is not null then
  insert into public.noki_collection_points(id,slug,name,address,city,latitude,longitude,source_name,source_url,verified_at,
    accepted_categories,accepted_details,hours_text,location_note,active,receiver_request_id)
  values(new.id,'receiver-'||new.id,new.place_name,new.address,'',new.latitude,new.longitude,'Receptor habilitado en Noki',
    'https://www.openstreetmap.org/?mlat='||new.latitude||'&mlon='||new.longitude,
    coalesce(new.reviewed_at,now())::date,new.categories,new.conditions,new.hours,
    'Ubicación indicada por el responsable del lugar. Confirma horario y condiciones antes de ir.',true,new.id)
  on conflict(receiver_request_id) do update set name=excluded.name,address=excluded.address,
    latitude=excluded.latitude,longitude=excluded.longitude,source_name=excluded.source_name,source_url=excluded.source_url,
    verified_at=excluded.verified_at,accepted_categories=excluded.accepted_categories,accepted_details=excluded.accepted_details,
    hours_text=excluded.hours_text,location_note=excluded.location_note,active=true;
 else
  update public.noki_collection_points set active=false where receiver_request_id=new.id;
 end if;
 return new;
end $fn$;
revoke all on function noki_private.publish_receiver_point() from public,anon,authenticated;
create trigger receiver_directory_update after insert or update on noki_private.receiver_requests
 for each row execute function noki_private.publish_receiver_point();
create trigger receiver_directory_delete before delete on noki_private.receiver_requests
 for each row execute function noki_private.publish_receiver_point();

-- Preserve the existing dispatcher/session checks and add an owner-only location operation.
do $migration$
declare definition text:=pg_get_functiondef('noki_private.native_action(text,jsonb)'::regprocedure);
 location_branch text:=$branch$
 elsif op='request_location' then
   select * into req from noki_private.receiver_requests where user_id=actor for update;
   if not found or req.id is distinct from (args->>'id')::uuid then raise exception 'NOKI_REQUEST_LOCKED'; end if;
   if req.status not in ('draft','needs_changes') and not (req.status='approved' and req.latitude is null) then raise exception 'NOKI_REQUEST_LOCKED'; end if;
   if jsonb_typeof(args->'latitude') is distinct from 'number' or jsonb_typeof(args->'longitude') is distinct from 'number'
     or (args->>'latitude')::double precision not between -90 and 90 or (args->>'longitude')::double precision not between -180 and 180
     or (args->>'confirmed') is distinct from 'true' then raise exception 'NOKI_LOCATION'; end if;
   update noki_private.receiver_requests set latitude=(args->>'latitude')::double precision,
     longitude=(args->>'longitude')::double precision,updated_at=now() where user_id=actor;
   insert into noki_private.receiver_audit(user_id,reviewer,status,note) values(actor,'Titular autenticado',req.status,'Ubicación del establecimiento confirmada por su titular; no modifica la aprobación.');
 elsif op='request_draft' then
$branch$;
begin
 if position('elsif op=''request_draft'' then' in definition)=0 or position('return jsonb_build_object(''id'',req.id);' in definition)=0 then
  raise exception 'Unexpected native dispatcher; inspect before migration';
 end if;
 definition:=replace(definition,'elsif op=''request_draft'' then',location_branch);
 definition:=replace(definition,'status=''draft'',legal_version=excluded.legal_version','latitude=null,longitude=null,status=''draft'',legal_version=excluded.legal_version');
 definition:=replace(definition,'   update noki_private.receiver_requests set status=''pending'',photos=',
   '   if req.latitude is null or req.longitude is null then raise exception ''NOKI_LOCATION''; end if;'||chr(10)||'   update noki_private.receiver_requests set status=''pending'',photos=');
 execute definition;
end $migration$;

create or replace function noki_private.review_receiver(person uuid,decision text,reviewer text,note text) returns void
language plpgsql security invoker set search_path='' as $fn$
declare r noki_private.receiver_requests;
begin
 if decision not in ('approved','needs_changes','suspended') or coalesce(length(trim(reviewer)),0)<5 or coalesce(length(trim(note)),0)<10 then raise exception 'Incomplete review'; end if;
 select * into r from noki_private.receiver_requests where user_id=person for update;
 if not found or r.status not in ('pending','approved','suspended') then raise exception 'Request not reviewable'; end if;
 if decision='approved' and (r.latitude is null or r.longitude is null) then raise exception 'NOKI_LOCATION'; end if;
 update noki_private.receiver_requests set status=decision,reviewed_by=reviewer,review_note=note,reviewed_at=now(),updated_at=now() where user_id=person;
 insert into noki_private.receiver_audit(user_id,reviewer,status,note) values(person,reviewer,decision,note);
end $fn$;
revoke all on function noki_private.review_receiver(uuid,text,text,text) from public,anon,authenticated;
-- No guessed coordinates or changes to existing approvals. Legacy approved owners can
-- confirm their missing location once; future approvals require it before publishing.
