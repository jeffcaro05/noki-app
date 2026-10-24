# Verificación — 20 de octubre de 2026

## Backend real

Migraciones aplicadas: `admin_web_integration` y `admin_point_defaults`. Se añadieron objetos administrativos y disparadores de notificación; no se sustituyeron las funciones de login, perfiles ni entrega nativas.

`supabase/tests/admin_security.sql`: comprobados allowlist real, rechazo de metadata editable, exigencia OTP, todas las consultas, ausencia de códigos de entrega, rechazo de autorrevisión, expiración por inactividad/absoluta, cierre y privilegios.

`supabase/tests/admin_publication.sql`: comprobados aprobación/publicación del mismo lugar, suspensión/ocultamiento, revisión de notificaciones, conflictos de edición, alta de referencia, revocación de membresía, API con rol authenticated, RLS y revocación de sesión. Todo dentro de una transacción con rollback; los lugares y cambios de prueba no permanecen publicados.

No se usaron códigos OTP de usuarios, claves de firma ni secretos de entrega para probar. Las sesiones SQL fueron sintéticas y se revirtieron. No constituyen evidencia de recepción real de correo en este frontend.

## Frontend

Pasada final, después de los ajustes de roles y filtros: 28 pruebas de componentes correctas. Se comprobó rechazo de correos ajenos, envío sin creación de cuentas, verificación OTP, error de código, fallo de envío, enlaces seguros, CSV sin fórmulas, semántica de avisos, filtros, actualización y paginación.

Navegador Edge: las 3 pruebas completas pasaron. Incluyen navegación por todos los módulos, revisión de receptor, enlace desde lugar a solicitud, consulta de entrega, descarga CSV, modo oscuro, teclado y teléfono con texto 200 %. Se corrigió un enlace de salto que invadía el encabezado al crecer el texto. La prueba móvil corregida pasó, incluyendo análisis automático de accesibilidad.

Las pruebas de navegador interceptan todo el tráfico Supabase y bloquean WebSocket externos. Solo utilizan datos controlados; no envían correos, no aprueban receptores reales ni alteran entregas. Capturas y resultados están en carpetas ignoradas por Git y no se publican.

Compilación de producción correcta con comprobación estricta de TypeScript. Auditoría de dependencias de producción sin vulnerabilidades conocidas en la ejecución realizada. La batería completa se repitió después de los últimos ajustes de roles/filtros.

El asistente de conexión a Firebase se comprobó con un PATH de proceso que solo incluía Windows y System32: inició Firebase CLI 15.34.0 correctamente. Añade la ubicación de su propio Node únicamente al entorno de sus procesos hijos; no modifica el PATH de usuario ni la política de ejecución de Windows. Esta prueba técnica no equivale a una autorización de la cuenta.

## Seguridad existente

Los avisos de Supabase sobre `rls_auto_enable`, cinco funciones de entregas/slots antiguas y protección de contraseñas filtradas desactivada ya existían antes de esta integración. No se cambiaron ajustes globales ni APIs antiguas para evitar regresiones de la app. Las nuevas funciones privilegiadas están en esquema privado y la API pública es invoker.

Los avisos informativos de RLS sin políticas en las tres tablas administrativas privadas son intencionales: no tienen grants a clientes ni acceso directo; solo la API autorizada las utiliza.

Referencias oficiales: [funciones privilegiadas](https://supabase.com/docs/guides/database/database-linter?lint=0029_authenticated_security_definer_function_executable), [protección de contraseñas](https://supabase.com/docs/guides/auth/password-security#password-strength-and-leaked-password-protection).

## Publicación y acceso real

Publicado únicamente Firebase Hosting del proyecto existente `noki-89c7b`: **https://noki-89c7b.web.app**. No se desplegaron Firestore, reglas, Functions ni Firebase Authentication. El build desplegado incorpora la corrección visual del símbolo de marca y conserva los contratos de la app.

La prueba `scripts/check-published.mjs` pasó contra el sitio real: HTTPS 200, CSP, no-store, nosniff, DENY, no-referrer, recursos y logo cargados, ausencia de errores JavaScript/CSP, vista móvil sin desbordamiento horizontal, rechazo local de correo no autorizado sin solicitud OTP y rechazo HTTP de la API administrativa sin sesión.

Jeffry confirmó personalmente que pudo entrar con su código y consultar los datos reales en el sitio publicado. No compartió el código con el agente. Las pruebas transaccionales de aprobación/publicación y permisos ya pasaron; sigue pendiente el repaso humano de una solicitud nueva desde otro dispositivo, incluida la revisión visual de sus fotos y el reflejo final en la app.

## Conexión de publicación

La consulta JSON de cuentas de Firebase CLI devolvió credenciales sensibles en la salida de la herramienta. No se copiaron al código ni al sitio. Se informó al propietario y, al terminar los despliegues, se ejecutó el cierre de esa cuenta con el comando oficial, que solicita la revocación del token. Informó cierre correcto; el proceso terminó después con una aserción nativa de Windows. Una consulta posterior de proyectos confirmó que la herramienta ya no estaba autenticada. La web y las sesiones Supabase no dependen de esa conexión local.

El asistente captura esa consulta únicamente en memoria para comprobar un booleano, sin imprimir cuentas ni tokens. Tras el cierre del 20 de octubre fue necesario autorizar de nuevo Firebase. El propietario completó esa autorización el 21 de octubre mediante el modo interactivo oficial y retorno local del navegador; se comprobó el acceso al proyecto antes del despliegue. El asistente actual no recopila códigos, mantiene el resultado visible y no considera el cierre de una ventana como prueba de éxito. Nunca adjuntar registros de autenticación ni códigos al chat.

## Revisión visual — 21 de octubre de 2026

Alcance: presentación del acceso y del panel; no se ejecutaron migraciones, cambios de autenticación ni operaciones administrativas reales para esta revisión. Se mantuvieron las dependencias y los logos/fuentes locales.

Resultados locales:

- 28 pruebas de componentes correctas y comprobación estricta de TypeScript sin errores.
- 10 pruebas de navegador correctas: los tres recorridos administrativos anteriores y siete comprobaciones de identidad/accesibilidad. Todo el tráfico externo queda interceptado; los códigos, envíos y revisiones usados en las pruebas son ficticios y no llegan a Supabase.
- Acceso comprobado en claro y oscuro a 390 píxeles; oscuro con texto 150 % a 820 píxeles; oscuro con alto contraste y texto 200 % a 320 píxeles. Sin desplazamiento horizontal de la página en estos casos, incluso al abrir las preferencias.
- Dashboard comprobado a 1440 píxeles y a 390 píxeles con texto 200 %, modo oscuro, navegación móvil y diálogo de receptor.
- Formulario usable durante la animación a 1366 × 768; acción de envío visible sin desplazamiento en el estado inicial. Ilustración decorativa, una repetición, duración máxima de cuatro segundos y detención efectiva comprobadas. Movimiento desactivado tanto por preferencia del sistema como por la opción del panel.
- Indicador de paso, foco en el código, espera de reenvío y retorno al correo comprobados. No se alteró el proceso OTP.
- Análisis automático de accesibilidad sin infracciones reportadas en las vistas ensayadas. Se corrigió el contraste del indicador secundario; los bordes de los campos del acceso tienen contraste superior a 3:1 con el fondo tanto claro como oscuro. Este análisis no equivale a una certificación integral de accesibilidad.
- Capturas revisadas de acceso de escritorio, teléfono oscuro, tablet con texto ampliado y resumen administrativo. Las capturas e informes permanecen en `test-results/` y `playwright-report/`, fuera de `dist/`.
- Compilación de producción correcta. La actualización utiliza SVG/CSS y activos locales; no incorpora servicios de animación, fuentes remotas ni rastreadores.

Se compararon las huellas SHA-256 previas y posteriores de `api.ts`, `Records.tsx`, `PointForm.tsx`, `types.ts`, `firebase.json` y las dos migraciones administrativas: todos coinciden. Esta comprobación asegura que esos archivos no cambiaron; no sustituye una auditoría del backend.

La especificación, el plan y las tareas incluyen los criterios visuales y sus límites. Esta revisión se publicó correctamente el 21 de octubre de 2026, únicamente en Firebase Hosting de `noki-89c7b`, tras comprobar la autorización y el acceso al proyecto. La compilación publicada referencia `index-D6a3N_iy.js` e `index-DSvKMLS7.css`. No se desplegaron funciones, reglas ni migraciones; los siete archivos de contratos y configuración comparados mantienen las mismas huellas SHA-256.

La comprobación `scripts/check-published.mjs` pasó contra **https://noki-89c7b.web.app**: nuevo encabezado e ilustración cargados, indicador de acceso, desactivación del movimiento por preferencia del sistema, HTTPS 200, cabeceras de protección/caché, recursos correctos, teléfono sin desbordamiento, correo ajeno rechazado sin pedir un OTP, API administrativa sin sesión rechazada y ausencia de errores JavaScript/CSP. Las capturas finales están en `test-results/published/` y se revisaron visualmente. No se enviaron correos ni se aprobaron solicitudes reales en esta comprobación; el acceso personal confirmado el 20 de octubre y los ensayos aislados se distinguen de esta validación de publicación.
