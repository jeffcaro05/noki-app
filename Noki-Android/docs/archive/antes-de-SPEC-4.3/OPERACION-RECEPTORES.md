# Recepción de aparatos

## Preparación del servicio

Los responsables de Noki deben designar revisores y un canal de atención antes de admitir solicitudes del público. La app no aprueba fotografías ni concede permisos automáticamente. No hay cobros, reconocimiento facial ni validación civil automatizada.

## Revisión remota

1. Revisar solicitud pendiente y su consentimiento específico.
2. Abrir únicamente las tres fotos asociadas, mediante Storage privado y una sesión administrativa autorizada. No descargar imágenes de solicitudes ajenas a la revisión ni reenviarlas por chat.
3. Contrastar fachada, recepción y almacenamiento; coherencia de dirección, ciudad, nombre, horarios, categorías y condiciones. Una foto no basta para demostrar autorización sobre el lugar: solicitar aclaración por el canal oficial si es necesario.
4. Comprobar que quien solicita declara mayoría de edad y autorización. No solicitar contraseñas, códigos de acceso ni fotos de cédulas por ese canal.
5. Si existen dudas, pedir cambios con instrucciones concretas. No aprobar por ausencia de respuesta.
6. Aprobar solo tras la comprobación y dejar nombre del revisor, fecha y motivo. La función interna `noki_private.review_receiver` registra la decisión y su auditoría; no está disponible a los clientes móviles. Las operaciones desde consola quedan limitadas a responsables con acceso administrativo, no a las cuentas comunes de la app.
7. Coordinar visita ocasional cuando la información no pueda confirmarse a distancia o existan reportes. No anunciar una visita no realizada ni garantizar una frecuencia inexistente.

## Seguimiento y protección de fotos

- Responder solicitudes de corrección, retiro, apelación y privacidad en el correo oficial.
- Fotos retiradas o abandonadas: controlar el plazo máximo de treinta días y eliminarlas por la API de Storage, no borrando solo metadatos SQL. Registrar fecha y responsable de eliminación. No hay tarea automática desplegada para hacerlo.
- Fotos de perfiles aprobados: revisar su necesidad al menos anualmente. No conservar por tiempo indefinido sin finalidad.
- No publicar fotos de revisión ni el nombre civil del responsable en el catálogo.
- Antes de apertura masiva: validación de archivos en servidor, acceso nominativo/asignado, auditoría de lecturas y comprobación de respaldos y borrado.

## Incidencias de entrega

### Códigos estables — 0.7.6

El QR y el código manual no caducan por el tiempo de traslado. Consultar otra vez una entrega pendiente devuelve el mismo par; no se debe pedir al usuario renovarlo cada diez minutos. El receptor asignado valida la lista y confirma recepción; el escaneo por sí solo no completa. Cancelar o completar invalida ambos formatos. Los límites de intentos y los controles de sesión, titularidad y aprobación permanecen vigentes.

La clave de derivación se mantiene exclusivamente en la tabla privada `noki_private.handoff_code_key`, sin permisos para clientes y sin valores incluidos en archivos. Debe conservarse en los respaldos protegidos del servicio y no regenerarse en despliegues ordinarios. No copiarla a la app, registros o documentación. Un incidente sobre esa clave requiere respuesta de seguridad coordinada; no cambiarla informalmente, porque alteraría las credenciales pendientes.

### Directorio y WhatsApp — 0.7.3

La aprobación requiere un marcador confirmado. El backend publica automáticamente los datos comerciales y el WhatsApp autorizado; suspender oculta el punto. Los cambios aparecerán al consultar o en el siguiente sondeo visible (10 segundos en Puntos). No crear manualmente un segundo punto desde la web: usar la solicitud y su operación interna de revisión auditada. La web administrativa sigue pendiente; los roles del navegador nunca reciben claves de servicio.

El propietario puede actualizar su WhatsApp desde la app con autorización pública explícita. Revisar que pueda atender consultas. No copiar correos privados ni presentar un WhatsApp como línea de llamadas. En Express.arte el titular autorizó el 3233784234, Colombia, solo WhatsApp.

Ante categoría no anunciada o estado que necesita atención, la app exige que quien entrega declare haber acordado la recepción. Esto permite crear una entrega y su QR, no obliga al receptor a aceptar aparatos ni certifica que el transporte sea seguro. El receptor comprueba lista, estado y acuerdo antes de confirmar físicamente. La constancia de contacto registra una declaración, no una verificación realizada por Noki.

Antes de recepción, el dueño puede cancelar en la app y crear otra entrega. Después de que el receptor confirme, no se borra ni cancela unilateralmente. Ante discrepancias, conservar la constancia, escuchar a ambas partes y gestionar rectificación mediante un procedimiento documentado. No afirmar entrega completada ni reciclaje si falta evidencia.

La aprobación operativa se realizará en la web administrativa, pendiente de integrar cuando se entregue su proyecto. La app presenta solicitudes y consulta decisiones del mismo backend, pero no incorpora un botón de autoaprobación. La web requerirá cuentas nominativas de revisores, autenticación reforzada, permisos por caso y registro de decisiones y consultas. La sincronización de cambios deberá validar permisos y volver a consultar al reconectar; no se presenta como tiempo real mientras no esté implementada y probada. Estos pasos describen la operación que deben asumir los responsables; el software no demuestra por sí mismo que haya personal disponible para ejecutarla.
