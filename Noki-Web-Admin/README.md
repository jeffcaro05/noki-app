# Noki · Administración web 2.0

Panel administrativo conectado al mismo Supabase que la app Android. No usa cuentas simuladas ni una base de datos separada.

Sitio publicado: **https://noki-89c7b.web.app**. Jeffry confirmó el acceso por código y la visualización de datos reales el 20 de octubre de 2026.

Revisión visual publicada el 21 de octubre de 2026 en la misma dirección. Conserva el acceso y la integración con Android; no requiere reinstalar la app.

## Empezar

Requiere Node.js 24 y pnpm 11. Desde esta carpeta:

```text
pnpm install --frozen-lockfile
pnpm dev
```

Abre la dirección indicada por Vite. Para producción: `pnpm build`. La carpeta publicable es `dist/`.

La configuración publicable del proyecto está en `src/admin/api.ts`; `.env.example` permite reemplazarla. **Nunca introducir una contraseña de Gmail, clave secreta o service_role en el frontend.** La clave publicable no concede permisos administrativos: el servidor valida la identidad y la sesión en cada operación.

## Acceso

- Jeffry: caroajeffry@gmail.com
- Luis Miguel: oykmelissa@gmail.com

Usan sus cuentas existentes, mediante un código enviado por Supabase Auth. No se crean cuentas nuevas desde la web y no se cambia la contraseña de la app. El permiso reside en el servidor, no en la lista de correos de la pantalla.

## Documentación vigente

- `docs/SPEC-ADMIN-6.0.md`: comportamiento y contratos de la web conectada.
- `docs/PLAN-INTEGRACION.md`: orden de trabajo y límites.
- `docs/TASKS-INTEGRACION.md`: tareas y comprobaciones pendientes.
- `docs/VERIFICACION.md`: evidencia de pruebas y limitaciones.
- `docs/PUBLICACION.md`: publicación y recuperación en Firebase.

## Código activo

`src/main.tsx` carga únicamente `src/admin/`. Cada pantalla está separada de la API y de los componentes comunes. Las fuentes y logos se comparten con la app y se incluyen localmente en `public/brand/`.

La revisión visual del 21 de octubre de 2026 se concentra en `src/admin/identity.css` y la ilustración decorativa `BrandCircuit.tsx`, con integración de presentación en Acceso y Resumen. No cambia la autenticación, las operaciones administrativas ni la base de datos. La animación de entrada termina en cuatro segundos y respeta movimiento reducido. Las pruebas de esta capa están en `tests/browser/identity.spec.ts`.

El material de Luis Miguel se conserva en `archive/prototype-v1/` y en el RAR original. Sus usuarios, puntos de Quito, cifras, contraseñas de muestra, módulos y pruebas simuladas **no forman parte del panel activo**. Los lugares reales existentes de la app se conservan en Supabase.

## Comprobaciones

```text
pnpm test
pnpm test:browser
pnpm build
pnpm audit --prod
node scripts/check-published.mjs
```

Las pruebas de navegador usan Edge y datos aislados, sin enviar códigos ni modificar Supabase. Los contratos reales de permisos, revisión y publicación están en `supabase/tests/`; se ejecutan dentro de transacciones que se revierten. Nunca eliminan cuentas ni completan entregas reales.

`scripts/check-published.mjs` comprueba el sitio HTTPS real, cabeceras, carga de recursos, vista móvil, rechazo local de correo no autorizado y rechazo de la API sin sesión. No inicia sesiones, envía correos ni modifica registros.

Las migraciones administrativas ya aplicadas están en `supabase/migrations/`. Son complementarias a las migraciones de la app; este repositorio no contiene una reconstrucción completa de toda la base de datos móvil. No reapliques estas migraciones manualmente en el proyecto existente.
