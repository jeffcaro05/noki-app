insert into public.noki_collection_points(slug,name,address,latitude,longitude,source_name,source_url,verified_at,accepted_categories,accepted_details,hours_text,phone,location_note)
values ('monterrey-ambiental','Monterrey · Centro de Acopio Ambiental','Carrera 48 # 10-45, Medellín',6.2142673,-75.5767871,'Centro Comercial Monterrey','https://monterrey.com.co/_servicios/ambiental-monterrey/','2026-10-03','{}','La fuente anuncia recepción de residuos tecnológicos sin detallar categorías, tamaños ni condiciones. Llama para confirmar tu aparato.','6:00 p. m. a 8:00 p. m. La fuente no especifica los días. Confirma antes de ir.','+576046048912','Segundo piso, rampa de parqueaderos. Marcador aproximado del edificio, no del contenedor. Coordenadas: OpenStreetMap, elemento way/155205082, consultado el 3 de octubre de 2026.');

alter table public.noki_devices drop constraint preparation_shape;
alter table public.noki_devices add constraint preparation_shape check(coalesce(
 jsonb_typeof(preparation)='object' and preparation ?& array['backup','accounts','media','erasure','destination'] and
 preparation - array['backup','accounts','media','erasure','destination'] = '{}'::jsonb and
 preparation->>'backup' in ('pending','done','not_applicable') and preparation->>'accounts' in ('pending','done','not_applicable') and
 preparation->>'media' in ('pending','done','not_applicable') and preparation->>'erasure' in ('pending','done','not_applicable') and preparation->>'destination' in ('pending','done','not_applicable'), false));
