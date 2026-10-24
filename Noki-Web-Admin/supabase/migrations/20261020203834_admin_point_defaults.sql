-- Preserve the original migration; normalize optional location notes in the API.
do $migration$
declare definition text;
begin
 definition:=pg_get_functiondef('noki_private.admin_action(text,jsonb)'::regprocedure);
 if position('left(trim(args->>''location_note''),500)' in definition)=0 then
  raise exception 'Expected admin API version not found';
 end if;
 definition:=replace(definition,'left(trim(args->>''location_note''),500)',
   'left(trim(coalesce(args->>''location_note'','''')),500)');
 execute definition;
end $migration$;
