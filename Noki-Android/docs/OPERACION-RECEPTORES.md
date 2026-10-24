# Operación de solicitudes y entregas

SPEC 4.3 · Android 0.7.7.

## Servicio compartido

La app envía solicitud/fotos a Supabase. La web administrativa ya existente consulta el mismo servicio con permisos; Firebase Hosting aloja sus archivos. No requiere USB ni copiar manualmente registros de una base a otra.

La revisión se realiza en el apartado de solicitudes de receptor de la web, no mediante autoaprobación en Android. Acceso administrativo lo valida servidor. Aprobar actualiza el punto asociado; suspender lo retira del catálogo activo. No crear un segundo punto manual para la misma solicitud.

## Revisión humana

- Revisar responsable, autorización declarada, lugar/dirección/marcador, horario, categorías, condiciones, WhatsApp y tres fotos.
- No pedir contraseñas, códigos, cédulas ni selfies por canales informales.
- Si falta información usar solicitud de cambios y nota concreta. No aprobar por ausencia de respuesta.
- Aprobar solo después de revisar; servidor registra decisión, revisor y fecha.
- La marca Perfil revisado no es certificación ambiental ni prueba automática de identidad.
- Coordinar visitas ocasionales cuando corresponda; no informar como realizadas visitas inexistentes.

Los estados manejados incluyen borrador, pendiente, necesita cambios, aprobado, suspendido y retirado. No hay estado rejected del flujo normal de revisión. El cliente no ofrece todos los comandos administrativos.

## Fotos y privacidad

Las fotos son privadas; usar sesión autorizada y no reenviarlas. No se publican con la ficha comercial. La política contiene obligaciones de eliminación de fotos retiradas/abandonadas y revisión periódica; no hay tarea automática desplegada que las ejecute. Los responsables deben llevar registro y usar el mecanismo de Storage, no borrar solo metadatos SQL. La revisión jurídica y de seguridad permanece pendiente.

El WhatsApp se publica solo con autorización; no usar automáticamente el correo de login como contacto. La declaración de contacto previo no prueba una conversación ni garantiza aceptación/seguridad del aparato.

## Recepción

El emisor obtiene QR/manual tras seleccionar receptor y completar preparación. El mismo par permanece mientras pendiente, sin renovación por tiempo de traslado. El receptor asignado revisa el código y lista y confirma físicamente recibido. Escaneo solo no completa. El emisor puede cancelar antes de recibir; tras completar no hay borrado/cancelación unilateral.

La clave privada de derivación de códigos permanece únicamente en servidor; conservarla en respaldo protegido. No copiarla al cliente ni regenerarla informalmente: afectaría entregas pendientes.

La app actualiza Puntos cada diez segundos visible y las vistas de seguimiento que lo incorporan cada cinco. No confundir ese sondeo con push en segundo plano o sincronización instantánea.
