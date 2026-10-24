create table public.noki_collection_points (
 id uuid primary key default gen_random_uuid(), slug text not null unique,
 name text not null, address text not null, city text not null default 'Medellín',
 latitude double precision not null check(latitude between -90 and 90),
 longitude double precision not null check(longitude between -180 and 180),
 source_name text not null, source_url text not null check(source_url like 'https://%'),
 verified_at date not null, accepted_categories text[] not null default '{}',
 accepted_details text not null, hours_text text, phone text, location_note text not null,
 active boolean not null default true, created_at timestamptz not null default now()
);
alter table public.noki_collection_points enable row level security;
revoke all on public.noki_collection_points from anon, authenticated;
grant select on public.noki_collection_points to anon, authenticated;
create policy "published locations" on public.noki_collection_points for select to anon, authenticated using(active);

create table public.noki_devices (
 id uuid primary key, user_id uuid not null references auth.users(id) on delete cascade,
 description text not null check(char_length(trim(description)) between 2 and 120),
 category text not null check(category in ('phone','computer','audio_video','accessories','appliance','other')),
 safety_state text not null check(safety_state in ('normal','attention','unknown')),
 function_state text not null check(function_state in ('works','faults','unknown')),
 recommendation_code text generated always as (
 case when safety_state <> 'normal' then 'safety_review' when function_state = 'works' then 'second_life' else 'repair_review' end
 ) stored,
 decision text not null default 'undecided' check(decision in ('undecided','keep','repair','reuse','deliver')),
 selected_point_id uuid references public.noki_collection_points(id) on delete set null,
 preparation jsonb not null default '{"backup":"pending","accounts":"pending","media":"pending","erasure":"pending","destination":"pending"}',
 outcome text check(outcome in ('kept','repaired','reused','delivered','other')),
 completed_at timestamptz, version integer not null default 1,
 created_at timestamptz not null default now(), updated_at timestamptz not null default now(),
 constraint preparation_shape check(jsonb_typeof(preparation)='object' and preparation ?& array['backup','accounts','media','erasure','destination'] and
 preparation - array['backup','accounts','media','erasure','destination'] = '{}'::jsonb and
 preparation->>'backup' in ('pending','done','not_applicable') and preparation->>'accounts' in ('pending','done','not_applicable') and
 preparation->>'media' in ('pending','done','not_applicable') and preparation->>'erasure' in ('pending','done','not_applicable') and preparation->>'destination' in ('pending','done','not_applicable')),
 constraint completed_pair check((outcome is null) = (completed_at is null))
);
create index noki_devices_owner_updated on public.noki_devices(user_id, updated_at desc);
create index noki_devices_selected_point on public.noki_devices(selected_point_id);
alter table public.noki_devices enable row level security;
revoke all on public.noki_devices from anon, authenticated;
grant select, delete on public.noki_devices to authenticated;
grant insert(id,user_id,description,category,safety_state,function_state,decision,selected_point_id,preparation,outcome) on public.noki_devices to authenticated;
grant update(description,category,safety_state,function_state,decision,selected_point_id,preparation,outcome) on public.noki_devices to authenticated;
create policy "owner reads" on public.noki_devices for select to authenticated using((select auth.uid())=user_id);
create policy "owner creates" on public.noki_devices for insert to authenticated with check((select auth.uid())=user_id);
create policy "owner updates" on public.noki_devices for update to authenticated using((select auth.uid())=user_id) with check((select auth.uid())=user_id);
create policy "owner deletes" on public.noki_devices for delete to authenticated using((select auth.uid())=user_id);

create function public.noki_device_timestamps() returns trigger language plpgsql security invoker set search_path='' as $$
begin
 if tg_op='INSERT' then
   new.version:=1; new.created_at:=now();
   new.completed_at:=case when new.outcome is null then null else now() end;
 else
   new.version:=old.version+1; new.created_at:=old.created_at;
   new.completed_at:=case when new.outcome is null then null when old.outcome is null then now() else old.completed_at end;
 end if;
 new.updated_at:=now(); return new;
end;
$$;
revoke all on function public.noki_device_timestamps() from public, anon, authenticated;
create trigger noki_device_timestamps before insert or update on public.noki_devices for each row execute function public.noki_device_timestamps();

insert into public.noki_collection_points(slug,name,address,latitude,longitude,source_name,source_url,verified_at,accepted_categories,accepted_details,hours_text,phone,location_note)
values ('el-tesoro-respel','El Tesoro · Recolección Respel','Carrera 25A # 1A Sur–45, Medellín',6.1975427,-75.5588351,'El Tesoro Parque Comercial','https://eltesoro.com.co/servicios/recoleccion-respel/','2026-10-03',array['phone','computer','audio_video','appliance'],'La fuente incluye computadores, telefonía, equipos de oficina, audio y video, electrodomésticos, herramientas y juguetes electrónicos. Confirma accesorios, tamaño y equipos dañados.','Lunes a domingo, 7:00 a. m. a 9:00 p. m.','+576043211010','Marcador de referencia del centro comercial. Acceso por la Transversal Superior, al costado derecho de la vía interna. No es la posición exacta del contenedor.');
