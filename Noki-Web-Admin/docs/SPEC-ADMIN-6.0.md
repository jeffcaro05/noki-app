# Noki — Especificación de administración web

Versión 6.0 · Revisión visual: 21 de octubre de 2026 · Implementación web 2.0.0.

Este documento sustituye, para el panel conectado, la especificación del prototipo 5.0. No describe dispositivos de prueba, contraseñas de ejemplo ni funciones simuladas como si estuvieran disponibles.

## 1. Propósito y alcance

Permitir al equipo autorizado revisar las solicitudes de recepción creadas en Noki, consultar las cuentas y sus aparatos, mantener el directorio de lugares, seguir las entregas y consultar la actividad administrativa. Las decisiones relevantes se guardan en el servidor con identificación del administrador.

La web es administrativa, no una segunda app de entregas. No genera QR, no confirma recepciones en nombre del receptor y no cambia el estado de una entrega manualmente. Tampoco es un portal de inscripción pública.

Se utiliza React, TypeScript y Vite para el frontend, Supabase Auth/Postgres/Storage/Realtime para los servicios compartidos y Firebase Hosting para la publicación estática. No se crea otra base de datos en Firebase ni se usa Firebase Auth.

## 2. Identidad y permisos

### 2.1 Administradores

Las únicas membresías iniciales son las cuentas confirmadas existentes de caroajeffry@gmail.com y oykmelissa@gmail.com. Se vinculan por UUID de Auth y correo normalizado; la coincidencia se verifica en cada operación. No se asignan roles a partir de `user_metadata`, de datos editables por el usuario, de parámetros del navegador ni de nombres de perfil.

La interfaz puede rechazar un correo no autorizado antes del envío por comodidad. La autorización real ocurre en Postgres, incluso si alguien omite la interfaz y llama directamente a la API. No existe un formulario para crear administradores o cambiar las membresías desde este panel.

### 2.2 Entrada y salida

1. El administrador escribe su correo autorizado.
2. Auth envía un código con `shouldCreateUser: false`, reutilizando el envío de correo ya configurado para Noki.
3. El navegador verifica el código mediante `verifyOtp`, tipo `email`.
4. El servidor comprueba: usuario confirmado, sesión de Auth existente y vigente, membresía activa y correo coincidente.
5. La primera apertura administrativa de esa sesión requiere un evento OTP registrado por Auth durante los diez minutos anteriores. Una sesión de contraseña de la app no basta.
6. El panel aparece solamente si la validación administrativa responde correctamente.

Los diez minutos del punto anterior corresponden a la apertura de la sesión administrativa; **no son un vencimiento del QR móvil**, cuyo comportamiento permanece sin cambios.

El código tiene las reglas de caducidad, longitud, intentos y límites de envío configuradas en Auth. La interfaz acepta entre seis y diez dígitos, evita un segundo envío durante 60 segundos y nunca registra el código en auditoría. No muestra un envío como correcto cuando el servidor lo rechaza.

La sesión administrativa permanece en `sessionStorage`, separada de la app y de otros sitios. Se cierra tras 30 minutos de inactividad o, como máximo, ocho horas desde su apertura. El servidor comprueba ambos límites; la interfaz avisa a los 28 minutos. Solo interacción real genera el latido de actividad, limitado a una solicitud por 30 segundos. La consulta automática de datos no renueva la actividad.

Cerrar sesión cierra el registro administrativo y la sesión web local de Auth, sin cerrar las sesiones de Android. Recargar la página restaura el panel únicamente si el servidor autoriza la sesión. Revocar una membresía o eliminar su sesión de Auth impide consultas posteriores aunque quede un JWT sin vencer.

## 3. Fuente de verdad y sincronización

| Información | Fuente real compartida | Uso administrativo |
| --- | --- | --- |
| Cuenta y perfil | `auth.users`, `public.noki_profiles` | Consulta limitada de cuenta, nombre, verificación y actividad |
| Aparatos | `public.noki_devices` | Consulta del perfil y seguimiento, sin alterar resultados |
| Solicitud de receptor | `noki_private.receiver_requests` | Revisar, aprobar, solicitar cambios o suspender |
| Lugares de la app | `public.noki_collection_points` | Consulta de todos; mantenimiento de referencias independientes |
| Entregas e ítems | `noki_private.handoffs`, `handoff_items` | Consulta de estados, participantes, fechas y aparatos |
| Fotografías | Buckets privados `noki-receiver-places`, `noki-profile-photos` | Consulta autorizada y registrada |

Las tablas antiguas `noki_receivers`, `noki_points`, `noki_slots`, `noki_deliveries` y `noki_items` no se utilizan como fuente de verdad de la app nativa ni del panel nuevo. No se migran sobre ellas los registros reales.

### 3.1 De la app a la web

La app guarda en Supabase a través de sus servicios existentes. Los cambios en perfiles, aparatos, solicitudes, puntos y entregas incrementan revisiones por tema en `public.noki_admin_sync`. Esta tabla no contiene fotografías, correos, nombres ni códigos. Realtime comunica el cambio y el panel vuelve a consultar su API autorizada.

El panel agrupa notificaciones cercanas durante 350 ms. Mientras está visible también consulta cada 15 segundos y al recuperar conexión o visibilidad. Esto cubre interrupciones de Realtime y altas de Auth que todavía no hayan generado un perfil. Sin conexión conserva la última lectura, indica que puede estar desactualizada y no confirma escrituras.

### 3.2 De la web a la app

La revisión actualiza la solicitud original. El mecanismo ya existente de publicación crea o actualiza un único lugar vinculado al ID de la solicitud aprobada. La suspensión lo oculta como destino activo. Las notas y el estado se consultan desde la cuenta del solicitante.

Android consulta el directorio al entrar/reanudar y cada diez segundos mientras esa vista está activa. Las pantallas de recepción/solicitud consultan cada cinco segundos cuando corresponde. Por tanto, la sincronización es automática y eventual; no se promete actualización de pantalla instantánea mientras la app esté cerrada o sin Internet.

No hay dependencia de un cable USB, de una computadora concreta ni de una red Wi-Fi compartida. Los dispositivos necesitan Internet y una versión de Noki configurada con este proyecto. Los datos de una demostración local no se convierten automáticamente en cuentas reales.

## 4. Navegación y módulos

Menú persistente: Resumen, Usuarios, Receptores, Lugares de entrega, Entregas, Informes y Actividad del equipo. El encabezado conserva accesibilidad, identidad de quien administra y cierre de sesión. El estado del módulo se refleja en el fragmento de la URL, permitiendo navegación atrás/adelante sin recargar toda la aplicación.

Los listados muestran 25 registros por página, total, búsqueda, filtros cuando corresponden y fecha de la última consulta correcta. No se sustituyen errores por listas vacías ni por ejemplos. Las consultas de detalle requieren permiso y quedan auditadas.

### 4.1 Resumen e informes

Conteos reales de usuarios, correos confirmados, receptores aprobados, solicitudes pendientes, lugares activos, entregas pendientes/completadas/canceladas, aparatos y aparatos recibidos. Las entregas completadas se agrupan por mes UTC durante seis meses; las fechas visibles se presentan en hora de Colombia.

Los indicadores permiten abrir el módulo relacionado. Los informes se descargan mediante acción explícita en CSV, con fecha de consulta, cifras agregadas y meses. No contienen correos, contraseñas, fotos ni códigos. Las celdas se escapan para impedir ejecución de fórmulas al abrir el archivo. No se inventan kilogramos, emisiones evitadas ni tratamientos ambientales.

### 4.2 Usuarios y roles

Incluye las cuentas registradas y distingue la verificación del correo del rol operativo. Todos conservan su rol de usuario. Quien tiene solicitud aprobada aparece como «Usuario y receptor»; una solicitud pendiente, suspendida, retirada o con cambios no equivale a recepción habilitada.

Detalle: correo, nombre de perfil, nombre de usuario, fechas de registro y último acceso, estado de receptor, foto de perfil si existe y hasta cien aparatos recientes con decisión/resultado. No se muestran hashes de contraseña, claves, códigos OTP ni credenciales de entrega. El panel no cambia contraseñas, elimina cuentas ni modifica resultados personales.

### 4.3 Solicitudes de receptor

Las solicitudes creadas desde Android aparecen en Receptores. Hay accesos rápidos a todas, pendientes y aprobadas, además del filtro completo. Se distingue un borrador que aún no fue enviado de una solicitud pendiente de revisión.

El detalle contiene: nombre de la persona responsable, correo de la cuenta, nombre del lugar o establecimiento, dirección, coordenadas, enlace de mapa, WhatsApp público, horarios, categorías recibidas, condiciones, tres fotos del lugar, versión/fecha de aceptación y revisión anterior. No se solicitan ni visualizan cédulas, selfies o reconocimiento facial.

Las fotos se cargan por acción explícita, permanecen privadas y se entregan mediante URLs firmadas de 60 segundos. Su consulta requiere sesión administrativa vigente, pertenencia del archivo a un registro permitido y una entrada en auditoría. Una imagen ya vista puede permanecer en la memoria del navegador; una URL firmada no borra copias previamente obtenidas. Los administradores no deben descargar ni compartir información fuera de su finalidad.

Decisiones válidas:

- Pendiente → aprobada: requiere ubicación, tres fotos y WhatsApp, además de nota y confirmación de revisión. Publica el lugar mediante el mecanismo existente.
- Pendiente → cambios solicitados: nota clara de al menos diez caracteres. El responsable puede corregir y reenviar desde la app.
- Aprobada → suspendida: nota obligatoria y confirmación de que el lugar dejará de estar activo.
- Suspendida → aprobada: nueva revisión y nota, manteniendo la misma solicitud y lugar.
- Borrador, cambios solicitados o retirada: no se aprueban directamente. Se espera el envío correspondiente.

La persona revisora no puede aprobar ni suspender su propia solicitud. El otro administrador debe hacerlo. El servidor deriva la identidad del revisor de la membresía, bloquea la fila y comprueba `updated_at`; si otro administrador cambió el registro, rechaza la revisión obsoleta y pide recargar.

«Aprobado» o «Perfil revisado» significa revisión manual del lugar, no identidad certificada, convenio comercial ni garantía absoluta. La web no confirma una recepción física por el hecho de aprobar un lugar.

### 4.4 Lugares

Se conservan los puntos reales existentes de la app, incluidos Express.arte, El Tesoro y Monterrey. Se distinguen:

- Receptor Noki: lugar enlazado a una solicitud, con entregas por código según la habilitación del responsable. Sus datos no se duplican ni se editan como referencia independiente. Desde su detalle puede abrirse la solicitud.
- Lugar de referencia: información pública de recolección con fuente y fecha. No representa un receptor registrado ni permite confirmar códigos dentro de Noki. Permanece consultable en el directorio según su visibilidad.

Visible/oculto es el estado de publicación en el directorio, no una afirmación de que el establecimiento esté físicamente abierto en ese momento. Los estados de aprobación de solicitudes se muestran en Receptores, no se inventan para organizaciones sin cuenta.

La administración puede crear y editar referencias con nombre, dirección, ciudad, latitud/longitud, horarios, categorías, condiciones, fuente HTTPS, fecha de comprobación no futura, contacto, indicaciones y visibilidad. Requiere nota y confirmación. La edición comprueba que la versión original no haya cambiado y registra el estado anterior en auditoría. No hay borrado destructivo: se oculta el punto si deja de estar disponible.

Los puntos y usuarios de ejemplo de Quito y los correos ficticios del prototipo se conservan únicamente en el archivo histórico del código. No se presentan como solicitudes reales ni se publican sin comprobación.

### 4.5 Entregas

Consulta por destinatario, correo del remitente o identificador, con estados pendiente, recepción registrada, completada y cancelada. El detalle muestra los participantes, destino, fechas, contacto previo declarado, motivo de cancelación y la lista exacta de aparatos incluidos.

No muestra el QR/código ni permite alterarlo, regenerarlo o confirmar una entrega desde el panel. La validación y confirmación del receptor asignado en Android sigue siendo la operación que completa la entrega. Se conserva el código estable mientras la entrega siga pendiente; esta integración no modifica ese contrato.

### 4.6 Actividad

Registro inalterable desde el cliente de apertura administrativa, consultas de detalle, acceso a fotografías, decisiones de revisión, actualizaciones de lugares y descarga de informes. Cada entrada identifica actor, acción, destino, nota y fecha. No guarda contraseñas, códigos ni claves. Las consultas periódicas de las tablas no generan una cascada de notificaciones de auditoría.

## 5. Usabilidad y accesibilidad

Identidad: logos oficiales, Poppins local, verde Noki, fondos crema y oscuro verde suave. No se cargan fuentes desde terceros. Pantallas separadas con encabezados claros y acciones descriptivas; las aprobaciones no se confunden con recepción física.

### 5.1 Lenguaje visual y entrada

El acceso combina azul profundo y verde bosque con acentos verde claro. El formulario usa superficies claras cálidas u oscuras verde suave, según la preferencia de apariencia. El símbolo y la tipografía son los oficiales de Noki; no se sustituyen por una marca generada. Los espacios, bordes y tamaños deben distinguir título, explicación, campo y acción sin saturar la pantalla.

La ilustración de entrada representa conexiones entre un aparato, un lugar y una entrega. Es decorativa: no representa datos reales, conexión del servidor, carga ni progreso de una solicitud. Se implementa con SVG/CSS y recursos locales, sin servicios externos, vídeo, permisos adicionales ni consultas adicionales al backend.

La animación se reproduce una sola vez al montar el acceso, dura como máximo cuatro segundos y termina en una composición estática. No bloquea el formulario ni retrasa su uso. No tiene destellos, bucle permanente, audio ni interacción obligatoria. Se omite con la preferencia del sistema o de Noki de reducir movimiento. La ilustración queda fuera del árbol de accesibilidad y no intercepta clics ni foco.

El indicador «Tu correo / Tu código» refleja el paso real del acceso mediante `aria-current="step"`; no es un menú ni añade pasos de autenticación. Se conservan el envío, la verificación, la espera de reenvío y todos los mensajes de error existentes. Cambiar de apariencia nunca envía un código ni abre una sesión.

En el panel, la misma composición aparece estática. Los indicadores, tablas y acciones conservan sus nombres, destinos y datos. Las solicitudes de receptor siguen requiriendo las mismas comprobaciones y confirmaciones, independientemente de su presentación.

### 5.2 Adaptación y accesibilidad

Modo claro, oscuro y sistema; cambio gradual; tamaño de texto 100/125/150/200 %; contraste alto; reducción de movimiento y respeto a `prefers-reduced-motion`. Las preferencias no contienen datos de cuenta. Si el almacenamiento de preferencias no está disponible, se deben conservar valores seguros.

El diseño adapta navegación lateral a menú móvil, reorganiza indicadores con texto grande y permite desplazamiento horizontal dentro de las tablas, no de toda la página. Los formularios tienen etiquetas, límites, estados de carga y mensajes de validación. Los diálogos gestionan foco con el elemento nativo `dialog` y se cierran con Escape cuando no hay una escritura en curso.

El acceso usa dos columnas cuando hay espacio y una columna en teléfono. Con texto al 150 % en pantallas de hasta 1100 píxeles, y al 200 % en cualquier ancho, reorganiza el contenido en una columna y retira la ilustración cuando resta espacio a la lectura. El formulario conserva todas sus etiquetas y controles. Se comprueba el contraste de texto, el foco visible y el uso con teclado; el color no sustituye las indicaciones de estado. Los modos de contraste forzado e impresión omiten la ilustración.

Éxito: recuadro verde, símbolo de confirmación y anuncio `status`. Error: recuadro rojo, X y `alert`. El significado no depende únicamente del color. Los resultados y estados vacíos no se simulan. Los enlaces externos son HTTPS, abren una pestaña identificada y usan `noopener noreferrer`.

## 6. Seguridad y operación

Tablas administrativas privadas con RLS activado, sin permisos directos de lectura/escritura para clientes. La API pública es una función `SECURITY INVOKER`; el acceso privilegiado se limita a una función en esquema privado con `search_path` vacío y autorización explícita. Las políticas de Storage y Realtime exigen sesión administrativa válida.

La lista de miembros no es editable desde el cliente. La clave publicable del frontend no es un secreto ni concede por sí sola acceso a registros. No hay service_role ni credenciales SMTP en el repositorio. Los datos personales se consultan únicamente después de autenticar y autorizar; no se guardan en caché del servicio web ni en una base local simulada.

Firebase distribuye solo los archivos compilados. Se configuran CSP, bloqueo de frames, `nosniff`, `no-referrer`, desactivación de permisos de cámara/micrófono/ubicación y `Cache-Control: no-store`. El hosting no contiene contraseñas ni datos de usuarios. No hay rastreadores publicitarios ni métricas externas añadidas.

Las advertencias preexistentes sobre funciones antiguas y protección de contraseñas de Supabase se documentan aparte; no se cambian globalmente funciones móviles o ajustes de autenticación para silenciarlas.

## 7. Límites explícitos

No se implementa en esta versión: CMS compartido de Aprende, calendarios/cupos simulados, exportaciones masivas de datos personales, pagos/comisiones, certificados de reciclaje, eliminación de cuentas desde el panel, mensajería automática ni verificación biométrica. El contenido Aprende existente en Android permanece intacto. Estos módulos del prototipo no se muestran como funciones operativas del panel real.

La publicación y la entrada real por correo se comprueban separadamente de las pruebas con datos controlados. La evidencia vigente está en `VERIFICACION.md`; una compilación correcta no demuestra por sí sola el acceso de los dos propietarios ni una publicación en Internet.

## 8. Criterios de aceptación

1. Los dos propietarios pueden solicitar y verificar un código; una cuenta común no puede abrir el panel por API.
2. No se duplican cuentas ni cambian contraseñas/sesiones de Android.
3. Las solicitudes muestran toda la información enviada por la app y sus fotos privadas.
4. Aprobar publica el mismo lugar; suspender lo oculta; una revisión concurrente obsoleta falla sin sobrescribir la decisión reciente.
5. Un administrador no puede revisar su propia solicitud.
6. Los perfiles distinguen usuario y usuario/receptor; los lugares existentes permanecen con tipo y visibilidad claros.
7. Las entregas muestran registros reales y no permiten eludir la confirmación móvil.
8. Los cambios se reflejan mediante eventos y consultas automáticas sin cable ni red local compartida.
9. Se verifica teclado, modo oscuro, teléfono y texto al 200 %, sin solapamientos del contenido principal.
10. Fallos de red, códigos erróneos, sesiones vencidas, formularios incompletos y estados vacíos se explican sin inventar éxitos.
11. La entrada permite usar el correo desde el primer momento; su animación termina en cuatro segundos como máximo y respeta ambas opciones de movimiento reducido.
12. El acceso no presenta desplazamiento horizontal de página a 320 píxeles y texto al 200 %, ni a 820 píxeles y texto al 150 %. Las preferencias continúan accesibles.
13. Una revisión exclusivamente visual no modifica las APIs, membresías, políticas de datos, contratos móviles ni configuración de publicación.
