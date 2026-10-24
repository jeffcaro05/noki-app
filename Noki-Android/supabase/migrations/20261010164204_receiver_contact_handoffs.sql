-- Public business contact is explicitly supplied by the owner, never copied from Auth.
alter table noki_private.receiver_requests add column public_whatsapp text
 check (public_whatsapp is null or public_whatsapp ~ '^[1-9][0-9]{9,14}$');
alter table public.noki_collection_points add column whatsapp text
 check (whatsapp is null or whatsapp ~ '^[1-9][0-9]{9,14}$');
alter table noki_private.handoffs
 add column contact_confirmed_at timestamptz,
 add column contact_whatsapp text;

-- Extend the existing safe public projection; no identity, login email or photos.
do $migration$
declare definition text:=pg_get_functiondef('noki_private.publish_receiver_point()'::regprocedure);
begin
 if position('active,receiver_request_id)' in definition)=0 or position('true,new.id)' in definition)=0
 or position('location_note=excluded.location_note,active=true' in definition)=0 then raise exception 'Unexpected publisher'; end if;
 definition:=replace(definition,'active,receiver_request_id)','active,receiver_request_id,whatsapp)');
 definition:=replace(definition,'true,new.id)','true,new.id,new.public_whatsapp)');
 definition:=replace(definition,'location_note=excluded.location_note,active=true','location_note=excluded.location_note,whatsapp=excluded.whatsapp,active=true');
 execute definition;
end $migration$;

do $migration$
declare definition text:=pg_get_functiondef('noki_private.native_action(text,jsonb)'::regprocedure);
 contact_branch text:=$branch$
 elsif op='request_contact' then
   select * into req from noki_private.receiver_requests where user_id=actor for update;
   if not found or req.id is distinct from (args->>'id')::uuid or req.status='withdrawn' then raise exception 'NOKI_REQUEST_LOCKED'; end if;
   if (args->>'publish_contact') is distinct from 'true' or coalesce(args->>'whatsapp','') !~ '^[1-9][0-9]{9,14}$' then raise exception 'NOKI_CONTACT_DETAILS'; end if;
   update noki_private.receiver_requests set public_whatsapp=args->>'whatsapp',updated_at=now() where user_id=actor;
   insert into noki_private.receiver_audit(user_id,reviewer,status,note) values(actor,'Titular autenticado',req.status,'El titular autorizó publicar o actualizar el WhatsApp del establecimiento; no cambia la aprobación.');
 elsif op='request_location' then
$branch$;
 create_branch text:=$branch$
   -- A declaration is not proof of agreement or a safety certificate. The receiver
   -- still reviews the list and must explicitly confirm physical reception.
   if exists(select 1 from public.noki_devices where id=any(ids) and user_id=actor
     and (safety_state<>'normal' or not(category=any(req.categories))))
     and ((args->>'contact_confirmed') is distinct from 'true' or req.public_whatsapp is null) then raise exception 'NOKI_CONTACT'; end if;
   if (args->>'contact_confirmed')='true' and (req.public_whatsapp is null or (args->>'contact_whatsapp') is distinct from req.public_whatsapp) then raise exception 'NOKI_CONTACT_CHANGED'; end if;
   insert into noki_private.handoffs(id,sender_id,receiver_id,receiver_name,address,contact_confirmed_at,contact_whatsapp)
    values((args->>'id')::uuid,actor,target,req.place_name,req.address,
      case when (args->>'contact_confirmed')='true' then now() end,
      case when (args->>'contact_confirmed')='true' then req.public_whatsapp end) returning * into h;
$branch$;
 original_insert text:='   insert into noki_private.handoffs(id,sender_id,receiver_id,receiver_name,address) values((args->>''id'')::uuid,actor,target,req.place_name,req.address) returning * into h;';
begin
 if position('elsif op=''request_location'' then' in definition)=0
 or position('''conditions'',conditions))' in definition)=0
 or position('and category=any(req.categories) and not exists' in definition)=0
 or position(original_insert in definition)=0
 or position('   update noki_private.receiver_requests set status=''pending'',photos=' in definition)=0
 then raise exception 'Unexpected native dispatcher; inspect before migration'; end if;
 definition:=replace(definition,'elsif op=''request_location'' then',contact_branch);
 definition:=replace(definition,'''conditions'',conditions))','''conditions'',conditions,''whatsapp'',public_whatsapp))');
 definition:=replace(definition,'and category=any(req.categories) and not exists','and not exists');
 definition:=replace(definition,original_insert,create_branch);
 definition:=replace(definition,'   update noki_private.receiver_requests set status=''pending'',photos=',
   '   if req.public_whatsapp is null then raise exception ''NOKI_CONTACT_DETAILS''; end if;'||chr(10)||'   update noki_private.receiver_requests set status=''pending'',photos=');
 execute definition;
end $migration$;
-- Existing owner/session checks, one-active constraint, no-self rule, QR expiry,
-- immutable snapshots and receiver-only completion are unchanged.
