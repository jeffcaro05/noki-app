# Noki Android

Android nativo · Kotlin / Jetpack Compose · versión 0.7.7 (15) · SPEC 4.3.

Abre **esta carpeta** en Android Studio: contiene settings.gradle.kts y los módulos app/shared. JDK 17, Android SDK 36, Gradle Wrapper 8.13; mínimo Android 8/API 26. Descarga de dependencias por Internet.

## Documentos vigentes

- [SPEC de la implementación](docs/Noki-App-SPEC.md)
- [Correspondencia SPEC/código/pruebas](docs/MATRIZ-SPEC-CODIGO.md)
- [Guía para abrir, compilar y conservar la instalación](docs/GUIA-ENTREGA.md)
- [Plan técnico](docs/PLAN.md) y [tareas](docs/TASKS.md)
- [Pendientes reales](docs/PENDIENTES.md) y [verificación](docs/VERIFICACION.md)

La web administrativa ya está vinculada mediante el mismo Supabase. Firebase aloja la web, no los registros de la app. El código web es otro proyecto. No reapliques migraciones ni restablezcas datos para abrir Android Studio.

## Estructura

- app/src/main: actividad, interfaz Compose, cliente de servicio, recursos y manifiesto.
- shared/src/commonMain: reglas de orientación, preparación, entregas, receptor y encuadre.
- app/src/test y shared/src/commonTest: pruebas locales.
- app/src/androidTest y app/src/qa: ensayos instrumentados/variante separada de desarrollo, no instalar sobre cuentas reales.
- gradle/wrapper: herramienta de construcción.
- supabase/migrations: historial SQL, no respaldo completo ni configuración automática.
- docs: documentación vigente; archive contiene historia, no requisitos actuales.
- tools: exportación y comprobación de integridad del ZIP.
- licenses: licencia de tipografía.

No es Flutter y no incluye una interfaz iOS/Windows. Los targets iOS del núcleo compartido no constituyen una app iOS.

## Compilación local

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :shared:jvmTest :app:lintDebug --no-daemon
```

Variante de usuario/debug: co.noki.app; práctica deshabilitada. Firma privada y configuración local del SDK no se distribuyen. No hay cuenta ni clave administrativa en el código: la clave publicable identifica al cliente; los permisos los impone el servidor.
