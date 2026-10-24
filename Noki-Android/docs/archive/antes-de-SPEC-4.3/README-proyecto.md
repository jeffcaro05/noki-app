# Noki — proyecto para Android Studio

Versión 0.7.7 · Kotlin y Jetpack Compose · Android nativo.

**Especificación vigente:** [Noki App — SPEC 4.2](docs/Noki-App-SPEC.md). La reorganización de requisitos y la conservación del original se documentan en [Revisión del SPEC](docs/REVISION-SPEC-4.0.md). Las referencias a versiones del SPEC en las notas históricas siguientes corresponden a su momento, no a la línea base actual.

Actualización 0.7.7: solicitud de receptor con requisitos pendientes, mínimos visibles, errores junto al campo o casilla y acceso al primer dato faltante. Continuar con datos incompletos explica qué corregir sin enviar la solicitud ni perder respuestas. Cambiar la dirección avisa que hay que confirmar nuevamente el mapa. No modifica sesiones, contraseñas, QR, aprobación ni contratos de servidor.

Actualización 0.7.6: QR y código estables durante la entrega pendiente, sin cuenta regresiva ni regeneración al volver a consultarlos. Confirmar recepción o cancelar los invalida. El acceso por correo/contraseña, los códigos de login y las demás reglas de entrega no cambian.

Actualización 0.7.5: se corrigió el filtro que ocultaba los destinos en 0.7.4. Todos los puntos publicados siguen visibles, incluido el lugar propio, y la ficha del destino permite generar el QR/código directamente. La ruta es opcional y posterior. Se mantienen las restricciones de autoentrega, referencias externas sin recepción Noki, contacto y una entrega activa. La entrega actual 0.7.6 sí incluye ZIP por solicitud del titular.

**No está hecho con Flutter.** `app` contiene la interfaz Android; `shared` contiene modelos y reglas en Kotlin Multiplatform. Las interfaces iOS/iPadOS y Windows todavía no están construidas. No se usa WebView ni PWA como interfaz.

## Abrir el ZIP

1. Extrae el ZIP completo en una carpeta de trabajo. No abras el proyecto dentro del archivo comprimido.
2. En Android Studio selecciona **Open** y abre la carpeta `Noki-Android`, que contiene `settings.gradle.kts`.
3. Instala Android SDK 36 y selecciona **JDK 17** en Settings → Build, Execution, Deployment → Build Tools → Gradle. El IDE puede usar su Java integrado, pero Gradle necesita la versión indicada.
4. Espera la sincronización. El Wrapper descarga Gradle 8.13 y comprueba su huella. Se necesita Internet la primera vez.
5. Selecciona el módulo **app**, variante **debug**, y el teléfono. Pulsa **Run**.

Android mínimo: 8.0 / API 26. Compilación y objetivo: API 36. Android Studio crea `local.properties` con la ubicación del SDK del computador. No se incluye ese archivo porque sus rutas no son portables.

### Conservar la app instalada y sus datos

El identificador sigue siendo `co.noki.app`. Actualiza sin desinstalar, borrar almacenamiento ni cambiar ese identificador. El APK de esta entrega usa la firma de desarrollo existente en el computador de Jeffry. Las claves de firma **no viajan en el ZIP**.

En este mismo equipo, Android Studio utiliza la firma de desarrollo ya existente. En otro equipo, una firma distinta puede causar `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. **No desinstales Noki para resolverlo**: usa el APK compatible entregado o configura la firma original de forma privada. La firma definitiva de distribución y publicación en tiendas siguen pendientes.

No instales la variante `qa` ni ejecutes los recorridos instrumentados históricos sobre la cuenta real del VIVO. La copia de QA anterior se retiró por solicitud del titular. Las pruebas de sesión de esta entrega se ejecutan en el computador con datos sintéticos.

## Compilar y verificar

Desde la carpeta extraída, con JDK 17 y SDK configurados:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :shared:jvmTest :app:lintDebug --no-daemon
```

APK: `app/build/outputs/apk/debug/app-debug.apk`. Las pruebas unitarias no usan cuentas reales. Las suites SQL en `verification` requieren un entorno autorizado; sus fixtures se revierten y no aprueban receptores reales. Las migraciones ya aplicadas al proyecto de Noki **no deben repetirse manualmente**.

El ayudante `dev.ps1` del computador original no se distribuye porque fija rutas locales. El ZIP incluye el Wrapper y no depende de ese ayudante. `tools/Export-Project.ps1` permite exportar otra copia sin secretos ni cachés.

## Funciones incluidas

- Preparar entrega desde Inicio y Mis aparatos; código tras preparar y elegir receptor aprobado. Sin receptores habilitados, conserva el pendiente y explica cómo continuar.
- Éxitos verdes con chulo y errores rojos con X, sin aviso duplicado en el formulario.
- Inicio con explicación breve de Noki; orientación, Aprende y directorio consultables sin cuenta.
- Correo/contraseña como entrada principal; código como alternativa y recuperación. Se puede crear o cambiar contraseña sobre la misma cuenta. No se han cambiado SMTP ni plantillas.
- Nombre de usuario obligatorio, saludo, cambio permitido cada 30 días en servidor, foto privada y encuadre manual.
- Confirmación de acceso seguida de Inicio, cabecera con acceso o perfil y recuperación de orientación pendiente.
- Pendientes e historial separados; preparar un aparato no lo marca como entregado. Una sola entrega activa, con uno o varios aparatos, código estable mientras esté pendiente, recepción autorizada y confirmación explícita del receptor para completar. Resultado e historial se actualizan al consultar el estado, sin otra confirmación del emisor.
- Solicitud de receptor con fotos privadas del lugar. Registrarse no aprueba el sitio: la revisión humana y la futura web administrativa siguen siendo necesarias.
- Mapa nativo, selección de destino y partida por mapa, permiso de ubicación o dirección; rutas estimadas con consentimiento.
- Tema claro/oscuro/sistema, transiciones, márgenes adaptables, texto ampliable y movimiento reducido.
- En la app entregada no hay botón ni franja de práctica. La lógica técnica de fixtures queda bloqueada en debug/release y solo existe para desarrollo aislado.

Google queda para después, según lo solicitado. Se acordó entrada directa con Google y código de correo como alternativa, no código adicional. No hay OAuth activado ni botones que simulen su disponibilidad.

## Estructura

- `app/`: aplicación Android, recursos y pruebas.
- `shared/`: modelos, reglas y pruebas de dominio.
- `supabase/migrations/`: cambios versionados de base de datos.
- `docs/Noki-App-SPEC.md`, `PLAN.md`, `TASKS.md`: requisitos y seguimiento.
- `docs/VERIFICACION.md`: resultados comprobados y límites pendientes por versión.
- `docs/CONFIGURACION-ACCESO.md`: correo y proveedores futuros.
- `verification/`: evidencia y pruebas SQL, sin sesiones ni códigos reales.
- `licenses/`: licencias de recursos incluidos.

## Seguridad y pendientes

La clave publicable de Supabase identifica el proyecto; no es una credencial administrativa. Los datos privados dependen de autorización del servidor, no de esconder botones. El paquete no incluye contraseñas SMTP, tokens de usuario, `service_role`, secretos OAuth ni claves de firma.

No se promete que todos los lugares estén verificados: el directorio de fuentes públicas se distingue de receptores aprobados que admiten códigos. No se autoaprueban sitios para hacer demostraciones.

Siguen pendientes pruebas reales del perfil actualizado y entrega entre dos cuentas con receptor aprobado, web administrativa, eliminación completa de cuenta, revisión jurídica/operativa, medidas adicionales para apertura pública, firma de distribución, actualización automática y clientes de otras plataformas. Ver `docs/TASKS.md`; una compilación aprobada no significa producto certificado al 100 %.


