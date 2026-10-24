-- Stable link to the already-public place ID. No names/addresses used as keys,
-- no new public user identifiers, grants, sessions or authorization changes.
do $migration$
declare definition text:=pg_get_functiondef('noki_private.native_action(text,jsonb)'::regprocedure);
 old_projection text:='''conditions'',conditions,''whatsapp'',public_whatsapp))';
begin
 if position(old_projection in definition)=0 then
   raise exception 'Unexpected receiver projection; inspect before migration';
 end if;
 definition:=replace(definition,old_projection,
   '''conditions'',conditions,''whatsapp'',public_whatsapp,''request_id'',id))');
 execute definition;
end $migration$;
