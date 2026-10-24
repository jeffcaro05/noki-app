# Noki App — TASKS

Actualizado: 21 de octubre de 2026. Línea base: SPEC 4.2; Android 0.7.7. Estados: Pendiente, En curso, Verificada, Pospuesta. Los cierres verifican el alcance indicado, no garantizan todo el producto sin defectos.

## Resumen vigente

- Último cambio funcional: requisitos pendientes visibles en los tres pasos de solicitud de receptor, errores junto al campo, casilla o foto, y acceso al primer dato faltante. Los títulos tienen espacio propio para envolver el texto sin recortes.
- Acceso, cuentas y QR/código estables no se modifican; la confirmación del receptor o cancelación siguen invalidando las credenciales de entrega. La evidencia de 0.7.6 se conserva en `verification/qr-estable-v076.md`; T-78 y T-79 documentan la corrección actual.
- Verificación 0.7.7: 115 pruebas aprobadas (88 Android y 27 compartidas), cero fallos, errores u omitidas; análisis estático con cero errores y 30 advertencias existentes. Actualización sobre la misma app con datos protegidos intactos. Evidencia: `verification/receptor-validacion-v077.md`.
- El envío y acceso real por código fueron confirmados por el titular; no se consideran deshabilitados por estar pendientes matrices ampliadas.
- La web administrativa está publicada; su verificación se registra en su propio proyecto. Quedan validaciones humanas y requisitos abiertos: recepción física completa, accesibilidad y dispositivos adicionales, eliminación/cambio de correo, operación de revisión y privacidad, firma de distribución y actualización y clientes no Android.
- T-63 corresponde al paquete histórico; T-77 registra el ZIP actual. Las cifras de versiones anteriores son evidencia histórica, no sustituyen el resultado actual.

## Tareas y trazabilidad

| ID | Trabajo | Historias | Dependencia | Evidencia requerida | Estado |
| --- | --- | --- | --- | --- | --- |
| T-01 | Instalar entorno y herramientas en Windows | HU-AND-01 | Ninguna | Studio, SDK, JDK y conexión comprobados | Verificada |
| T-02 | Crear estructura y compilación reproducible | HU-AND-01 | T-01 | assembleDebug y prueba de instalación | Verificada |
| T-03 | Tema, fuentes, icono y navegación | HU-HOME-01, HU-AND-01 | T-02 | Revisión visual y escalado | En curso |
| T-04 | Implementar orientación y revisión de respuestas | HU-ORI-01–06 | T-03 | Pruebas de las tres ramas y Atrás | En curso |
| T-05 | Incorporar Aprende con fuentes y fecha | HU-EDU-01 | T-03 | Lectura offline y enlaces de fuentes | En curso |
| T-06 | Crear esquema aditivo y reglas RLS | HU-AUTH-01, HU-PRO-01, HU-DEV-01–09 | T-02 | Directorio, aparatos, perfil, fotos y aceptación comprobados con rollback; revisión histórica pendiente | En curso |
| T-07 | Documentos legales y aceptaciones versionadas | HU-LEGAL-01–02, HU-AUTH-01 | T-06 | Constancia local y aceptación remota con sesión/versionado comprobadas; revisión jurídica pendiente | En curso |
| T-08 | Registro, verificación, reenvío y App Links | HU-AUTH-01–03 | T-06, T-07 | Correo recibido y acceso OTP real en app confirmados; sesión/correo comprobados en servidor; reingreso y matriz de errores pendientes | En curso |
| T-09 | Acceso, renovación, cifrado y cierre de sesión | HU-AUTH-04, HU-AUTH-07, HU-HOME-02 | T-06 | Reinicio, caducidad, cierre offline y limpieza | En curso |
| T-10 | Recuperar y establecer contraseña | HU-AUTH-05–06 | T-08 | Recuperación por código confirmada por el titular; establecer/cambiar contraseña implementado y probado con respuestas controladas; falta matriz real ampliada | En curso |
| T-11 | Perfil, nombre, correo y contraseña | HU-PRO-01–04 | T-09 | Nombre ligado a la cuenta y foto implementados; recorrido real y cambios de correo/contraseña pendientes | En curso |
| T-12 | Eliminación segura de cuenta | HU-PRO-05 | T-06, T-09 | Reautenticación, revocación y datos eliminados | Pendiente |
| T-13 | Directorio y fichas verificadas | HU-DIR-01–03 | T-06 | Fuentes, filtros, vacío, antigüedad y copia local | En curso |
| T-14 | Mapa nativo, ubicación y rutas | HU-DIR-04–05 | T-13 | Permiso concedido/denegado, origen manual y fallo del proveedor | En curso |
| T-15 | Crear, listar, editar y borrar dispositivos | HU-DEV-01–05 | T-06, T-09 | CRUD real e intentos de acceso ajeno | En curso |
| T-16 | Guardar orientación y recuperar intención tras acceso | HU-ORI-07 | T-04, T-15 | Respuestas preservadas; una sola creación | En curso |
| T-17 | Decisión y lugar de referencia | HU-DEV-06, HU-DIR-06 | T-13, T-15 | Cambiar y quitar referencia sin reserva | En curso |
| T-18 | Preparación y sincronización | HU-DEV-07 | T-17 | Cinco ítems, No aplica y aviso preventivo | En curso |
| T-19 | Resultado, corrección e historial | HU-DEV-08–09, HU-HIS-01–02 | T-15, T-18 | Declaración personal, fechas, búsquedas y filtros | En curso |
| T-20 | Contenido offline y borradores cifrados | HU-OFF-01 | T-05, T-13, T-15 | Sin red, caducidad y separación de cuentas | Pendiente |
| T-21 | Firma e instalación de actualización | HU-UPD-01, HU-AND-01 | T-02 | APK de versión superior conserva datos | Pendiente |
| T-22 | Pruebas integrales y accesibilidad | Todas | T-03–T-21 | Matriz de Android, TalkBack y resultados reproducibles | En curso |
| T-23 | Prueba con cinco personas de 40–60 años | Todas las funciones principales | T-22 | Observaciones y métricas del SPEC | Pendiente |
| T-24 | Verificar alcance y preparar distribución | Todas | T-23 | Sin criterios pendientes; APK/AAB firmado | Pendiente |
| T-25 | Separar reglas en Kotlin Multiplatform | RF-54 | T-02 | Módulo shared compila para JVM y pruebas comunes aprobadas; binarios Apple requieren Mac | En curso |
| T-26 | Espacio local de prueba sin escrituras remotas | RF-53 | T-15–T-19 | Ciclo completo, cifrado, separación de sesión y reinicio | En curso |
| T-27 | Cliente iOS/iPadOS y firma | RF-54 | T-25, acceso a Mac/Xcode | iPhone 11 e iPad, VoiceOver, permisos y matriz funcional | Pendiente |
| T-28 | Cliente Windows e instalador | RF-54 | T-25 | Ventana, teclado, escala, almacenamiento seguro y matriz funcional | Pendiente |
| T-29 | Salidas útiles de cada orientación | RF-55 | T-04, T-13, T-16 | Tres pasos coherentes por rama; directorio contextual y retorno sin reiniciar | En curso |
| T-30 | Bienvenida y acceso contextual sin perder la orientación | RF-57 | T-03, T-16 | Primera apertura y cancelación aprobadas en VIVO; retorno al guardado probado en espacio local, pendiente con cuenta real | En curso |
| T-31 | Preferencias accesibles y menú desplegable | RF-58 | T-03 | Tamaño, contraste, movimiento y recreación aprobados en VIVO; revisión TalkBack y otros tamaños pendiente | En curso |
| T-32 | Entrada de marca y respuesta de pulsación | RF-59 | T-03, T-31 | Revisión visual, movimiento reducido y ausencia de doble acción | En curso |
| T-33 | Mapa dedicado y gestos separados del documento | RF-60 | T-14 | Mapa ampliado, origen y ruta real aprobados en VIVO; matriz de gestos, permisos y fallos pendiente | En curso |
| T-34 | Servicio de correo y alta mediante código | RF-56, RF-77 | T-07, elección de remitente y servicio | Gmail SMTP, plantillas, envío, recepción y acceso OTP desde VIVO confirmados; varios dominios y protección pública pendientes | En curso |
| T-35 | Google y Apple como proveedores de acceso | RF-56 | Configuración en consolas Google y Apple | Google pospuesto por el titular: entrada directa, código como alternativa; sin cambios del proveedor actual | Pospuesta |
| T-36 | Punto de partida por mapa, ubicación y dirección | RF-61 | T-33 | Tres entradas en mapa ampliado, permiso opcional, dirección confirmada, cero resultados y fallos | En curso |
| T-37 | Apariencia clara, oscura y del sistema | RF-62 | T-31 | Interpolación, logos, contraste, persistencia y movimiento reducido | En curso |
| T-38 | Disposición de navegación y vertical en teléfonos | RF-63 | T-03 | Etiquetas centradas 100/125/130/200 %, vertical y límites de plataformas | En curso |
| T-39 | Términos, privacidad y decisión explícita | RF-64 | T-15 | Documentos públicos, casillas independientes, rechazo, constancia cifrada y retirada local | En curso |
| T-40 | Revisión jurídica y constancia centralizada de aceptación | RF-46–48, RF-64, RF-77 | T-39, datos operativos de responsables | Constancia centralizada con sesión, versión y revocación probadas; revisión jurídica y operación pendientes | En curso |
| T-41 | Selector de lugar de entrega y acceso directo a partida desde ficha | RF-65, HU-DIR-07 | T-13, T-36 | Desplegable, búsqueda vacía/sin resultados, ficha, retorno y 200 % | En curso |
| T-42 | Información de receptor en Inicio y Perfil | RF-66, HU-REC-01 | T-03 | Carrusel manual, mismo destino, retorno, 200 %, sin datos sensibles ni elevación de rol | En curso |
| T-43 | Solicitud remota y fotos privadas del lugar | RF-67, RF-72 | Equipo revisor y protocolo operativo | Formulario, tres fotos, bucket privado y control de rutas; validación de contenido en servidor y borrado operativo pendientes | En curso |
| T-44 | Política específica y atención de derechos | RF-67, RF-71 | T-40, T-43 | Condiciones de revisión remota y visitas ocasionales; revisión jurídica y operación humana pendientes | En curso |
| T-45 | Solicitudes, estados y aprobación manual de receptor | RF-68, HU-REC-02–04 | T-34, T-40, T-43, T-44 | RPC privado, estado en servidor, bloqueo de autoaprobación y auditoría; asignación operativa y apelación pendientes | En curso |
| T-46 | Perfil receptor y cambio de contexto | RF-69, HU-REC-05 | T-45 | Interfaz de Recepciones y Mis aparatos; catálogo de aprobados separado del directorio; recorrido real pendiente | En curso |
| T-47 | Recepción confirmada y constancia | RF-70, HU-ENT-01–02 | T-46 | Servidor probado con transacción revertida: aislamiento, código estable, confirmación explícita, cancelación, invalidación al cierre e idempotencia; falta recepción física completa | En curso |
| T-48 | Reportes privados, permisos de revisión y borrado | RF-71, HU-ENT-03, HU-SEG-01 | T-45, T-47 | Casos asignados, mínimo privilegio, auditoría y eliminación efectiva de objetos y temporales | Pendiente |
| T-49 | Verificación integral de recepción antes de habilitar | RF-72 | T-43–T-48 | Matriz funcional, seguridad, accesibilidad y operación humana aprobadas | Pendiente |
| T-50 | Evitar rebote del acceso contextual | RF-73 | T-30 | VIVO 0.5.0: ocho deslizamientos rápidos, estabilidad final y conservación de orientación aprobados; ui-v050-final.txt | Verificada |
| T-51 | Recorrido por etapas y resultado protegido | RF-74, RF-70 | T-47 | Práctica completa aprobada en VIVO; una activa y grupo de dos aprobados en SQL; faltan dos cuentas reales y matriz de errores/categorías | En curso |
| T-52 | Ruta directa desde destino | RF-65 | T-41 | Ruta real abre mapa ampliado en VIVO; búsqueda y selección aprobadas; fallos de proveedor y permisos pendientes | En curso |
| T-53 | Resumen del propósito en Inicio | RF-75, HU-HOME-03 | T-03 | Inicio y texto 200 % sin recortes en VIVO | En curso |
| T-54 | Foto privada de perfil | RF-76, HU-PRO-06–07 | T-09, T-40 | Siete pruebas de contrato/imagen, permisos SQL y sesión revocada; recorrido real de selector, subida y borrado pendiente | En curso |
| T-55 | Registro real y activación centralizada | RF-77, HU-AUTH-08 | T-34, T-40 | Indicador activo, correo recibido, acceso OTP confirmado por titular y sesión/perfil/aceptación verificados en servidor; reingreso y edición real pendientes | En curso |
| T-56 | Contrato de aprobación desde administración | HU-ADM-01 | Proyecto web, T-45 | Decisión autorizada, auditoría y notificación/reconsulta de estado; implementación web posterior | Pendiente |
| T-57 | Nombre único y cambio cada 30 días | RF-78, HU-PRO-08 | T-55 | SQL de formato, unicidad, propiedad, plazo e idempotencia; completar con la cuenta del titular | En curso |
| T-58 | Cabecera y éxito hacia Inicio | RF-79, HU-AUTH-09 | T-57 | Texto grande, foto, acceso sin sesión, contexto y transición real | En curso |
| T-59 | Encuadre manual de foto | RF-76, HU-PRO-09 | T-54 | Geometría común, zoom/posición/restablecer; selector y carga real pendientes | En curso |
| T-60 | Pendientes e historial inequívocos | RF-32, RF-80, HU-DEV-05 | T-47 | Código automático, bloqueo de resultado, archivo protegido y cancelación separada | En curso |
| T-61 | Avisos de confianza por estado real | RF-81 | T-46 | Referencia pública, receptor revisado y ejemplo diferenciados | En curso |
| T-62 | Preservar sesión y retirar práctica del APK entregado | RF-53, RF-82 | T-55 | Diez pruebas de regresión y actualización real con archivo cifrado intacto; el recorrido manual del perfil sigue en T-58 | Verificada |
| T-63 | ZIP reproducible para Android Studio | RF-82 | T-62 | Wrapper, fuentes, recursos, pruebas y documentación; sin secretos ni cachés; compilación limpia del contenido extraído con 44 pruebas aprobadas | Verificada |

| T-64 | Preparación directa y resultado automático | RF-83, RF-70 | T-60 | Inicio/Mis aparatos, QR, confirmación del receptor e historial derivado; pruebas UI/servidor aprobadas; falta prueba física con receptor aprobado | En curso |
| T-65 | Contraseña recuperable y avisos diferenciados | RF-83 | T-62 | 58 pruebas aprobadas, instalación final conserva sesión; pendiente contraseña real introducida por el titular | En curso |

Los estados de esta tabla no sustituyen la evidencia registrada en VERIFICACION.md. No hay issues de GitHub asignadas en este documento.

## Cierre de correcciones 0.7.3

| ID | Trabajo | Evidencia | Estado |
| --- | --- | --- | --- |
| T-66 | Publicar receptores aprobados en mapa y lista automáticamente | Trigger idempotente, coordenadas confirmadas, suspensión/retirada y privacidad probados con rollback; actualización visible 10 s; Express.arte publicado | Verificada |
| T-67 | Contacto y QR para aparatos que requieren consulta | WhatsApp autorizado, declaración específica, validación servidor, QR y confirmación del receptor; aislamiento y estado desconocido probados | Verificada |
| T-68 | Evitar botones deshabilitados sin explicación | Mensajes de selección/preparación/contacto, plan conservar sin QR, controles a 200 %, creación comprobada desde interfaz con red simulada | Verificada |
| T-69 | SPEC y operación de receptores actualizados | SPEC 3.11, RF-84/85, complemento informativo, contratos de administración y evidencias | Verificada |

T-64 conserva pendiente el ensayo físico de entrega con personas y aparatos reales. No se crean entregas reales en sus cuentas para simular esa evidencia. T-63 documenta el paquete histórico 0.7.2; la solicitud del 15 de octubre autoriza el nuevo paquete T-77. Spec Kit no apareció en la búsqueda del catálogo de complementos, por lo que no se instaló una integración distinta ni se sobrescribió el SPEC existente.

## Continuidad de navegación 0.7.4

| ID | Trabajo | Evidencia | Estado |
| --- | --- | --- | --- |
| T-70 | Unir elección de receptor en mapa, confirmación y QR sin perder aparatos | Acciones contextuales en ficha/directorio/mapa; selección agrupada conservada; ruta opcional; referencia pública con alternativa; pruebas de interfaz aprobadas | Verificada en pruebas automatizadas |
| T-71 | Relación inequívoca entre punto publicado y receptor | Migración aditiva `delivery_map_continuity`; cuatro suites SQL con rollback aprobadas, sin cambios de permisos/Auth | Verificada |
| T-72 | Documentar recorrido y actualizar la misma app | SPEC 3.12 RF-86; 83 pruebas aprobadas, lint sin errores/30 advertencias; 0.7.4 instalada, archivo de sesión idéntico antes/después | Verificada |

## Regresión de destinos 0.7.5

T-70 no cubría el directorio contextual desde la cuenta del único receptor aprobado. El titular detectó que se ocultaban los destinos; RF-87 sustituye esa navegación y retira el filtro. T-71 y las garantías del servidor no cambian.

| ID | Trabajo | Evidencia | Estado |
| --- | --- | --- | --- |
| T-73 | Separar visibilidad pública y destinatarios permitidos | Pruebas de propietario, tres destinos, referencias y cambio de disponibilidad; suite total de 94 aprobada | Verificada en pruebas automatizadas |
| T-74 | Generar QR desde la ficha, conservando el grupo | Selección explícita, generación directa, errores visibles, reintento del código sin duplicados y captura 200 % revisada | Verificada en pruebas automatizadas |
| T-75 | Conservar acceso e instalación única | SPEC 3.13 RF-87; 0.7.5/código 13 instalada con Success; archivo cifrado idéntico antes/después y una sola app | Verificada |

## Código estable y entrega del proyecto 0.7.6

| ID | Trabajo | Evidencia | Estado |
| --- | --- | --- | --- |
| T-76 | Conservar QR/código de la entrega pendiente | SPEC 4.1, RF-70/72; servidor con pruebas revertidas, reapertura y segunda sesión, receptor asignado, límites, confirmación y cancelación; 100 pruebas aprobadas, misma app actualizada | Verificada en el alcance automatizado y de instalación; recepción física pendiente en T-64 |
| T-77 | ZIP completo con documentación separada y proyecto Android Studio | 138 archivos con SHA-256, APK compatible, fuentes extraídas compilan; 100 pruebas aprobadas y análisis con cero errores/30 advertencias. Informe: verification/paquete-v076.md | Verificada |

## Solicitud de receptor comprensible — 0.7.7

| ID | Trabajo | Evidencia | Estado |
| --- | --- | --- | --- |
| T-78 | Explicar cada requisito pendiente en los tres pasos de receptor | SPEC 4.2 RF-67; nueve pruebas de reglas y seis de interfaz, regresiones de cuenta/entrega y captura al 200 %; 115 pruebas aprobadas en total | Verificada en pruebas automatizadas y revisión de captura; evaluación humana ampliada en T-22 |
| T-79 | Mantener sesión y actualizar la instalación existente | Misma firma y paquete `co.noki.app`, 0.7.7/código 15; instalación correcta, huella del archivo protegido idéntica antes/después y arranque sin error observado. Solo dispositivo autorizado; informe `verification/receptor-validacion-v077.md` | Verificada en instalación y arranque; no se operaron cuentas ni solicitudes para la prueba |
