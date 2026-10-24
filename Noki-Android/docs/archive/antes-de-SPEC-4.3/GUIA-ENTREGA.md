# Noki — entrega completa 0.7.6

15 de octubre de 2026 · SPEC 4.1 · Android nativo, Kotlin y Jetpack Compose.

## Contenido del ZIP

- `SPEC.md`: especificación vigente del producto.
- `PLAN.md`: organización técnica y siguientes etapas.
- `TASKS.md`: tareas, evidencias y pendientes.
- `Noki-Android/`: proyecto para Android Studio, módulos app/shared, recursos, Wrapper, pruebas, migraciones, licencias y documentación ampliada.
- `Instalador/Noki-0.7.6.apk`: APK de desarrollo ya compilado con la firma existente; no es una app de QA.
- `CONTENIDO-SHA256.txt`: huellas de todos los archivos, excepto el propio manifiesto.

Los tres documentos principales también están dentro de `Noki-Android/docs`, idénticos a las copias de la raíz. Los SPEC anteriores están en `docs/archive`, identificados como históricos. El código de la web administrativa no forma parte de esta entrega porque todavía no se ha incorporado su proyecto.

## Abrir en Android Studio

1. Extrae el ZIP completo a una carpeta local.
2. En Android Studio elige **Open** y selecciona **Noki-Android**, donde están `settings.gradle.kts`, `gradlew.bat`, `app` y `shared`.
3. Configura **JDK 17** como Gradle JDK e instala **Android SDK Platform 36** y las herramientas solicitadas por el IDE.
4. Espera la sincronización. El Wrapper descarga Gradle 8.13; las dependencias necesitan Internet en un equipo nuevo.
5. Selecciona el módulo **app**, variante **debug**, y un dispositivo Android autorizado o emulador de desarrollo.

Android mínimo: 8.0 / API 26. Android Studio, JDK, SDK y cachés de bibliotecas se instalan por separado; no están en el ZIP. Android Studio configura la ruta local del SDK. No copies `local.properties` de otro computador. No es Flutter; el núcleo compartido no significa que exista ya una interfaz iOS o Windows.

## Conservar la instalación y los datos

El identificador sigue siendo `co.noki.app`. No desinstales Noki ni borres su almacenamiento para actualizar. El APK incluido es la versión 0.7.6/código 14 ya instalada en el dispositivo de prueba; allí no hace falta reinstalarlo.

En el equipo original, Android Studio puede usar la firma de desarrollo existente. En otro equipo, una firma distinta impide actualizar la instalación anterior. Ante `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, **no desinstales para resolverlo**: utiliza el APK compatible incluido o coordina privadamente la firma original. Las claves privadas de firma no se adjuntan. La firma definitiva y publicación en tienda siguen pendientes.

No elijas la variante `qa` ni ejecutes pruebas instrumentadas sobre una cuenta real. Para verificar en el computador, desde `Noki-Android`:

```powershell
.\gradlew.bat :shared:jvmTest :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --no-daemon
```

El APK generado aparece en `app/build/outputs/apk/debug/app-debug.apk`.

## Servicio y datos

El cliente conserva la URL y clave **publicable** del proyecto Noki. No son credenciales administrativas. Abrir y compilar no cambia cuentas ni entregas; las acciones reales dentro de la app sí utilizan el servicio existente.

No necesitas crear de nuevo usuarios ni configurar otra vez el correo. No ejecutes `db reset`, `db push` ni reapliques migraciones sobre el servicio activo para abrir Android Studio. El SQL se incluye como historial de mantenimiento: algunos identificadores locales y remotos difieren y deben reconciliarse antes de futuros despliegues.

Este ZIP **no es una copia de la base de datos**. No incluye usuarios, fotos privadas, sesiones, contraseñas SMTP, claves administrativas, secretos OAuth ni la clave privada del servidor que conserva los códigos de entrega. Permanecen en su servicio protegido. Un servicio independiente necesita configuración y validación separadas; no se promete restaurarlo completo solo con este ZIP.

## Estado y evidencia

El QR/código permanece igual mientras la entrega esté pendiente; confirmar recepción o cancelar lo invalida. Escanear permite revisar antes de confirmar. El acceso por correo no cambia.

Hay 100 pruebas automatizadas aprobadas y análisis estático sin errores, con 30 advertencias conservadas. Consulta `Noki-Android/verification/qr-estable-v076.md`, `verification/paquete-v076.md` y `docs/VERIFICACION.md`. No se simula una recepción física para darla por probada.

Las pruebas SQL son para un entorno de ensayo autorizado. Algunas verifican contratos históricos; no ejecutarlas indiscriminadamente contra producción. Logs brutos, capturas locales y respaldos de trabajo no necesarios para compilar permanecen fuera del ZIP.

Google/Apple, la web administrativa, otras plataformas y las validaciones pendientes siguen en TASKS. Empaquetar no activa ni da por terminadas esas funciones.
