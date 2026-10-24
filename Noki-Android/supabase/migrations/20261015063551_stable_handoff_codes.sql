-- Stable credentials for pending native deliveries. Auth and receipt transitions
-- are unchanged. No credential is stored in plaintext or returned to a receiver.
-- Refuse a lossy migration if an old hash-only credential is still in use.
lock table noki_private.handoff_tokens in share row exclusive mode;
do $guard$
begin
 if exists(select 1 from noki_private.handoff_tokens t join noki_private.handoffs h on h.id=t.handoff_id where h.status='pending') then
  raise exception 'Pending legacy delivery credentials exist; preserve them before migrating';
 end if;
end $guard$;

create table noki_private.handoff_code_key (
 id boolean primary key default true check(id),
 secret bytea not null check(octet_length(secret)=32)
);
alter table noki_private.handoff_code_key enable row level security;
revoke all on noki_private.handoff_code_key from public, anon, authenticated;
-- Intentionally no client RLS policy: only the existing authorized dispatcher
-- reads this server-only key. Preserve the key across deployments/backups.
insert into noki_private.handoff_code_key(id,secret) values(true,extensions.gen_random_bytes(32));

do $migration$
declare
 definition text:=pg_get_functiondef('noki_private.native_action(text,jsonb)'::regprocedure);
 previous text:=$old$
   token:=encode(extensions.gen_random_bytes(32),'hex'); short_code:=upper(encode(extensions.gen_random_bytes(5),'hex'));
   insert into noki_private.handoff_tokens(handoff_id,qr_hash,code_hash,expires_at) values(h.id,extensions.digest(token,'sha256'),extensions.digest(short_code,'sha256'),now()+interval '10 minutes')
    on conflict(handoff_id) do update set qr_hash=excluded.qr_hash,code_hash=excluded.code_hash,expires_at=excluded.expires_at,previewed_at=null;
   return jsonb_build_object('id',h.id,'qr','NOKI1:'||h.id::text||':'||token,'code',short_code,'expires_at',now()+interval '10 minutes');
$old$;
 replacement text:=$new$
   -- Domain-separated HMACs reproduce the same pair after app restart, retries
   -- or another authorized sender session, without persisting a plaintext QR.
   select encode(extensions.hmac(convert_to('noki:qr:v1:'||h.id::text,'UTF8'),k.secret,'sha256'),'hex'),
          upper(substr(encode(extensions.hmac(convert_to('noki:manual:v1:'||h.id::text,'UTF8'),k.secret,'sha256'),'hex'),1,10))
     into token,short_code from noki_private.handoff_code_key k where k.id;
   if token is null or short_code is null then raise exception 'NOKI_STATE'; end if;
   insert into noki_private.handoff_tokens(handoff_id,qr_hash,code_hash,expires_at)
    values(h.id,extensions.digest(token,'sha256'),extensions.digest(short_code,'sha256'),'infinity'::timestamptz)
    on conflict(handoff_id) do nothing;
   -- Returning an existing code must not reset a receiver's validated preview.
   -- The finite ISO sentinel keeps older clients able to parse expires_at;
   -- new clients use valid_until, never a countdown or a renewal timer.
   return jsonb_build_object('id',h.id,'qr','NOKI1:'||h.id::text||':'||token,'code',short_code,
     'expires_at','9999-12-31T23:59:59Z','valid_until','delivery_closed');
$new$;
begin
 if position(previous in definition)=0 then raise exception 'Unexpected token dispatcher; inspect before migrating'; end if;
 execute replace(definition,previous,replacement);
end $migration$;

-- Existing pending-only, assigned-receiver, approval, rate-limit and session
-- checks remain intact. Confirm/cancel delete the token row, preventing reuse.
