-- Receiver's explicit acknowledgement completes a validated handoff atomically.
-- Existing accounts/devices/receipts are not rewritten; "complete" remains for legacy clients.
do $migration$
declare
 definition text:=pg_get_functiondef('noki_private.native_action(text,jsonb)'::regprocedure);
 old_transition text:='update noki_private.handoffs set status=''received'',received_at=now() where id=h.id;';
 new_transition text:='update noki_private.handoffs set status=''completed'',received_at=now(),completed_at=now() where id=h.id;';
begin
 if position(old_transition in definition)=0 or position('2026-10-08.2' in definition)=0 then
   raise exception 'Unexpected native_action version: inspect before migration';
 end if;
 definition:=replace(definition,old_transition,new_transition);
 definition:=replace(definition,'2026-10-08.2','2026-10-09.1');
 execute definition;
 update public.noki_client_config set legal_version='2026-10-09.1' where id=true;
end $migration$;
