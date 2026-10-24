# Noki

Noki orienta sobre qué hacer con aparatos eléctricos y electrónicos en desuso. Permite conservar una decisión, preparar una entrega a un receptor habilitado y consultar la recepción confirmada. La administración de solicitudes se realiza en una web conectada al mismo servicio.

## Entrega actual

| Carpeta o archivo | Contenido |
|---|---|
| [Noki-Android](Noki-Android/) | Aplicación nativa Kotlin y Jetpack Compose 0.7.7, código de versión 15. Abrir esta carpeta en Android Studio. |
| [SPEC de app](Noki-Android/docs/Noki-App-SPEC.md) | Versión 4.3 de lo implementado, límites y comportamiento. |
| [Matriz de app](Noki-Android/docs/MATRIZ-SPEC-CODIGO.md) | Correspondencia funcional e inventario inverso del código. |
| [Noki-Web-Admin](Noki-Web-Admin/) | Panel React, TypeScript y Vite 2.0.0. Abrir esta carpeta en VS Code. |
| [SPEC web](Noki-Web-Admin/SPEC-WEB.md) | Versión 6.1 del contrato y comportamiento administrativo. |
| [Documentación académica](docs/academico/) | Plan de cierre, requisitos, evidencias exploratorias y pruebas. |
| [Bitácora de IA](bitacora_IA.md) | Solicitudes, decisiones humanas, resultados, verificaciones y límites. |
| [Backlog](docs/academico/BACKLOG.md) | Ocho historias enlazadas a issues; estado del cierre. |

Panel publicado: **https://noki-89c7b.web.app**. Requiere una cuenta administrativa autorizada y código por correo. El sitio no ofrece acceso público a los datos personales.

## Cómo revisar el código

En Android Studio abrir `Noki-Android`, no la raíz de este repositorio. El punto de entrada está en `app/src/main/java/co/noki/app/MainActivity.kt`; la interfaz está en `ui/`, el acceso a datos en `data/` y las reglas compartidas en `shared/src/commonMain`. Para compilar se necesita JDK 17, SDK Android 36 y las dependencias de Gradle; el repositorio no instala ni incluye esas herramientas.

En VS Code abrir `Noki-Web-Admin`. El código se puede leer sin ejecutar la web. Para ejecutarla se necesitan Node y pnpm compatibles con el proyecto, instalar las dependencias fijadas en el lockfile y seguir su README. No se incluyen `node_modules`, compilados ni sesiones.

Firebase Hosting sirve los archivos del panel. **Supabase es la fuente compartida** de identidad, datos y fotos, con controles de autorización en el servidor. Las migraciones conservan cambios históricos, no un respaldo de usuarios ni una instalación automática de toda la infraestructura. No ejecutarlas indiscriminadamente sobre producción.

La URL y clave publicable de Supabase son configuración pública del cliente, no un permiso administrativo. Claves privadas, contraseñas de envío de correo, sesiones y claves de firma quedan fuera del repositorio.

## Estado y antecedentes

Consolidación del **24 de octubre de 2026** autorizada por Jeffry. No cambia la app instalada, el sitio publicado ni la base de datos. Las pruebas y sus límites se documentan en [VERIFICACION-CIERRE.md](docs/academico/VERIFICACION-CIERRE.md).

Los HTML/JS de la raíz y el historial anterior al cierre corresponden al **prototipo web previo**, conservado para no romper sus referencias. No son el código de la app Android. Su enlace histórico es https://jeffcaro05.github.io/noki-app/.

No hay cliente Flutter/iOS, acceso social activado, cobros, actualización automática Android ni verificación biométrica. Las pruebas técnicas no certifican ausencia absoluta de defectos o fraude.

Equipo: **Jeffry Giovanny Caro Arango (@jeffcaro05)** y **Luis Miguel Castañeda (@jewens05)**. Luis desarrolló la identidad y la propuesta web inicial; Jeffry desarrolló la app, la integración y los ajustes finales de la web. La importación de cierre asistida por IA no reconstruye ni atribuye commits históricos a Luis.
