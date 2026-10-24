# Publicar sin duplicar la configuración

Se reutiliza el proyecto Firebase `noki-89c7b` incluido en el trabajo de Luis Miguel. El usuario autorizó elegir el camino que evitara repetir configuración. Firebase aloja la interfaz; todos los datos y la autenticación continúan en Supabase.

## Estado publicado

El 20 de octubre de 2026 se publicó **https://noki-89c7b.web.app**, únicamente el destino Hosting. Se verificó la carga HTTPS, cabeceras de seguridad, recursos y móvil. Jeffry confirmó entrada por correo y datos reales. La conexión local usada para publicar se cerró por seguridad al finalizar; el sitio permanece disponible sin mantener esta computadora conectada.

El 21 de octubre de 2026 se publicó la revisión visual en esa misma dirección, después de renovar personalmente la autorización de Firebase. Se desplegaron únicamente los diez archivos de `dist/` a Hosting; no se desplegaron bases de datos, reglas, funciones ni servicios de autenticación. La comprobación HTTP devuelve 200 y el HTML publicado referencia `index-D6a3N_iy.js` e `index-DSvKMLS7.css`, correspondientes a esta compilación. La evidencia de comprobación final está en `VERIFICACION.md`.

## Autorización del propietario

El propietario ejecuta `scripts/conectar-firebase.mjs` con Node.js y autoriza personalmente la cuenta con acceso al proyecto en el navegador. El asistente no pide códigos por chat ni los recopila: usa la devolución local del navegador a Firebase CLI. Firebase administra sus credenciales en su ubicación propia. No se modifica la política de ejecución de PowerShell.

El asistente localiza Node mediante el ejecutable con el que se abrió y lo añade solo al entorno de sus procesos hijos, por lo que también funciona si `node` no está configurado en el PATH de Windows. Utiliza `login --interactive --reauth`: el modo interactivo explícito evita que la detección del entorno de agente lo convierta en una secuencia automática de copiar códigos. No recoge códigos de autorización ni los pasa como argumentos. Comprueba la conexión guardada antes de anunciar éxito, y mantiene el resultado visible hasta pulsar Enter. Para verificar únicamente el inicio de Firebase, sin abrir una autorización, usar el argumento `--check`.

Si la página de Firebase informa un error OAuth, no reutilizar enlaces o códigos anteriores. La captura de ese error no permite determinar por sí sola si hubo caducidad, un enlace incompleto u otra causa. Es un fallo de autorización de la herramienta de publicación, no del acceso de usuarios a Noki. En Windows se abre el asistente con Node desde CMD y `/c`, sin ejecutar archivos PowerShell ni cambiar la política de ejecución: al cerrar el asistente no queda un intérprete donde pegar códigos accidentalmente. El cierre de una ventana no equivale a autorización correcta; se comprueba la cuenta y el acceso al proyecto por separado.

No mostrar la salida JSON de la lista de cuentas de Firebase CLI: incluye credenciales. El asistente la examina en memoria y solo informa si existe una conexión.

## Validar y publicar

```text
pnpm install --frozen-lockfile
pnpm test
pnpm test:browser
pnpm build
pnpm dlx firebase-tools@15.34.0 projects:list
pnpm dlx firebase-tools@15.34.0 hosting:sites:list --project noki-89c7b
pnpm dlx firebase-tools@15.34.0 deploy --only hosting --project noki-89c7b
```

Verificar el sitio realmente retornado por Firebase antes de compartirlo. No desplegar reglas, Functions, Firestore ni cambiar el proyecto Supabase. El directorio publicable es únicamente `dist/`; no el repositorio, su archivo histórico ni los informes de pruebas.

Después de publicar: abrir el sitio HTTPS, revisar CSP/caché, probar entrada por correo, datos reales, consulta de una solicitud con fotos, publicación del lugar tras aprobación y actualización desde otro dispositivo. No usar cuentas de prueba permanentes ni falsear entregas reales.

## Recuperación

Firebase Hosting conserva versiones de despliegue. Si se detecta un problema, restaurar la versión anterior desde el historial de Hosting; no revertir el proyecto Supabase, borrar cuentas o cambiar claves para resolver un problema visual. Las migraciones administrativas son aditivas y no cambian los códigos móviles.

La copia original de la web se conserva en el RAR y `archive/prototype-v1/`. Las bases de datos simuladas de esa copia no deben importarse sobre la base real.

Guía oficial: [probar y desplegar con Firebase Hosting](https://firebase.google.com/docs/hosting/test-preview-deploy).
