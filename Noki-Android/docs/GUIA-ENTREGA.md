# Noki — entrega Android 0.7.7 / SPEC 4.3

21 de octubre de 2026. Proyecto Android nativo Kotlin/Jetpack Compose, no Flutter.

## Qué contiene

- `SPEC.md`: especificación de funciones implementadas, con reglas, estados y límites.
- `MATRIZ-SPEC-CODIGO.md`: correspondencia requisito/código/prueba y recorrido inverso.
- `PENDIENTES.md`: funciones no disponibles y verificaciones pendientes, separadas del SPEC.
- `PLAN.md` y `TASKS.md`: arquitectura, mantenimiento y estado de esta entrega.
- `Noki-Android/`: proyecto completo para abrir; fuentes, recursos, pruebas, Gradle Wrapper, dependencias declaradas, documentos y migraciones de referencia.
- `Instalador/Noki-0.7.7.apk`: compilación debug de la app, no variante de práctica.
- `CONTENIDO-SHA256.txt`: integridad de todos los archivos excepto el propio manifiesto.

Las copias de SPEC, PLAN, TASKS, PENDIENTES, VERIFICACION y esta guía son idénticas a las de `Noki-Android/docs/`. La matriz conserva el mismo contenido y ajusta únicamente enlaces al código en su copia de la raíz. Los documentos históricos están separados en `docs/archive/`; no son el SPEC vigente.

## Abrir en Android Studio

1. Extrae el ZIP completo a una carpeta local.
2. Android Studio → **Open** → selecciona **Noki-Android** (no solo app). Allí están `settings.gradle.kts`, `gradlew.bat`, `app` y `shared`.
3. Usa **JDK 17** como Gradle JDK. Instala **Android SDK Platform 36** y las herramientas que solicite el IDE.
4. Espera la sincronización. El Wrapper usa Gradle 8.13. En un equipo nuevo necesita Internet para descargar herramientas y dependencias.
5. Para ejecutar elige módulo **app**, variante **debug**, y Android 8.0/API 26 o posterior.

No necesitas Flutter ni crear otra cuenta/proyecto Supabase. Android Studio/SDK/JDK no se incluyen; no son archivos de la app. La ruta del SDK se configura en cada equipo; se excluye `local.properties` para no imponer rutas de otro computador.

## Verificar sin teléfono

Con JDK 17 y SDK configurados, abre terminal en Noki-Android:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :shared:jvmTest :app:lintDebug --no-daemon
```

APK generado: `app/build/outputs/apk/debug/app-debug.apk`. Informes: `app/build/reports/tests/testDebugUnitTest/`, `shared/build/reports/tests/jvmTest/` y `app/build/reports/lint-results-debug.html`.

No elijas qa ni ejecutes pruebas instrumentadas sobre una cuenta real. Las pruebas locales de esta entrega no envían correos, fotografías ni entregas reales.

## Servicio y datos existentes

El cliente ya contiene URL y clave **publicable** para conectarse a Noki. Son configuración pública, no permiso de administración. La app y la web usan Supabase para identidad/datos/fotos/operaciones; Firebase aloja la web. El código de la web pertenece a otra entrega y no es necesario para compilar Android.

Abrir/sincronizar/compilar no altera cuentas. Las acciones que ejecutes dentro de la app sí usan el servicio existente. No repitas la configuración de correo ni ejecutes reset/push/migraciones para abrir el proyecto.

El ZIP no es respaldo de la base de datos: no contiene usuarios, fotos privadas, sesiones, contraseña SMTP, claves secretas de administración ni la clave privada del servidor que mantiene los códigos de entrega. Crear un servicio independiente requiere configuración y restauración separadas. Los SQL documentan evolución; no ejecutarlos indiscriminadamente contra producción.

## Instalación y firma

Se conserva `co.noki.app`, versión 0.7.7/código 15. El APK incluido usa la firma de desarrollo existente del equipo de construcción. No es publicación de producción.

Para actualizar una instalación se necesita firma compatible. Un proyecto compilado en otro computador puede usar otra clave debug y no actualizar la instalación previa. No desinstales ni borres datos para solucionar una incompatibilidad de firma: usa APK compatible o coordina la firma privadamente. La clave de firma no se entrega dentro del ZIP.

No se instala nada en ningún teléfono al preparar este paquete. La APK no se actualiza sola. El mapa y los datos remotos sí se consultan por Internet.

## Evidencia y límites

Consulta `Noki-Android/docs/VERIFICACION.md` para pruebas de esta entrega, advertencias y límites. El SPEC describe lo implementado; los pendientes no se presentan como funcionalidades. No se certifica ausencia absoluta de errores ni una entrega física por haber pasado pruebas automatizadas.
