# Términos y privacidad de Noki

Responsables: Jeffry Giovanny Caro Arango y Luis Miguel Castañeda. Contacto: nokiappcorporation@gmail.com. Versión de documentos de la app: 2026-10-09.1. Revisión de implementación: 9 de octubre de 2026.

Los documentos son públicos dentro de la app. Aceptación de términos y autorización de datos son casillas separadas, inicialmente desmarcadas. Rechazar conserva orientación, Aprende y directorio; no constituye aceptación tácita ni exención general de responsabilidad.

## Fuentes

- [Ley 1581 de 2012](https://cancilleria.gov.co/normograma/compilacion/docs/ley_1581_2012.htm): finalidad, autorización, acceso restringido, derechos y atención.
- [SIC: autorización previa, expresa e informada](https://sedeelectronica.sic.gov.co/publicaciones/boletin-juridico/concepto/para-el-adecuado-tratamiento-de-los-datos-es-indispensable-la-autorizacion-expresa-previa-e-informada).
- [Supabase: acceso por fila](https://supabase.com/docs/guides/database/postgres/row-level-security).
- [Supabase: archivos privados](https://supabase.com/docs/guides/storage/security/access-control).
- [Privacidad de Supabase](https://supabase.com/privacy) y [OpenStreetMap](https://osmfoundation.org/wiki/Privacy_Policy).

## Tratamientos y límites

0.7.3 añade un aviso complementario fechado el 14 de octubre de 2026, separado de la aceptación general 2026-10-09.1. Explica la publicación de datos comerciales, marcador y WhatsApp sin iniciar sesión. El formulario pide autorización específica no preseleccionada; no toma el correo de Auth como contacto público. La operación guarda una auditoría por titular y fecha. El número se puede corregir; solicitudes de retiro siguen el canal de privacidad. El consentimiento de contacto declarado para una entrega conserva fecha y número, no la conversación. La app no envía mensajes automáticamente ni garantiza seguridad de transporte o aceptación. Revisar jurídicamente este complemento antes de una apertura comercial masiva.

La versión 0.7.2 informa que el receptor asignado valida el código y confirma la recepción física para completar la entrega. No pide una segunda confirmación del emisor ni interpreta un escaneo como entrega. El resultado se deriva de la constancia, visible para las partes. No se amplían las categorías de datos ni se presenta como certificado de reciclaje.

La aceptación general conserva un recibo local cifrado y una constancia centralizada por cuenta y versión. La operación exige ambas aceptaciones, correo confirmado y sesión vigente; registra fecha del servidor y retirada. Perfil y fotos requieren consentimiento actual en servidor. Retirar permiso local funciona sin red; si falla la operación remota se informa y remite al contacto, sin prometer eliminación. La solicitud de receptor registra también aceptación específica, versión y fecha. Ninguna de estas constancias es una firma digital certificada.

La foto personal es opcional y privada, separada de las fotografías del lugar. Solo el titular accede desde el cliente. Se vuelve a codificar sin EXIF, se limita a JPEG de 512 KiB y se elimina mediante Storage al confirmar «Quitar mi foto». No se usa como reconocimiento facial, evidencia de identidad ni aprobación. Las cuentas no pueden concederse roles cambiando nombre o foto.

La solicitud contiene nombre del responsable, lugar, dirección, horario, categorías, condiciones y tres fotos del establecimiento. No solicita cédula, número de documento, selfie, reconocimiento facial ni prueba de vida. No se contrata proveedor de identidad.

Las fotografías son privadas. El cliente las vuelve a codificar, conserva orientación y elimina EXIF/GPS. Storage limita MIME, tamaño y tres rutas ligadas al titular; sigue pendiente validar y sanear el contenido en servidor contra clientes modificados. No se afirma autenticidad documental ni comprobación automática de identidad.

La revisión inicial es remota. Los términos permiten coordinar visitas ocasionales, sin afirmar que se realizan todas, que haya visita inicial obligatoria ni una frecuencia garantizada. «Perfil revisado» indica decisión manual sobre información presentada, no licencia ambiental ni garantía absoluta contra fraude.

Las entregas comparten solo los aparatos incluidos con el receptor asignado. El QR no contiene datos personales; secreto cifrado en tránsito y hash en servidor. La confirmación de recepción del receptor habilita el resultado. Una entrega confirmada no puede borrarse unilateralmente; los derechos de rectificación y supresión requieren respuesta operativa, no un rechazo automático.

## Pendientes antes de apertura pública

1. Validación jurídica de identificación de responsables, alcance de autorizaciones, transferencias, retención, derechos y atención de controversias.
2. Designación de revisores y protocolo remoto: autorización de operación del lugar, comprobación de dirección/horarios, criterios, corrección y apelación.
3. Visitas: responsables, coordinación y criterios para decidir cuándo son necesarias. No prometer una operación que no existe.
4. Retención: eliminación real de fotos retiradas/abandonadas dentro de treinta días; revisión anual de necesidad para aprobadas; plazos de constancias y respaldos. No hay eliminación automática desplegada. El equipo debe operar y verificar estos plazos antes de recibir solicitudes reales.
5. Herramienta de revisión con permisos nominativos, autenticación reforzada y registro de consultas de fotografías. La web de administración no se construyó en esta entrega.
6. Correo: el titular configuró Gmail SMTP y las plantillas de alta/acceso; la primera entrega a Gmail fue confirmada. La habilitación de la app se controla en servidor. Falta completar verificación en app, varios dominios, recuperación, protección contra abuso y capacidad de envío antes de distribución pública.
7. Retiro de solicitudes, cuenta y autorización remota: proceso real de soporte y pruebas; no anunciar eliminación automática.

Los cambios de esquema y permisos nuevos están separados de las tablas históricas. Las pruebas de servidor usan identidades ficticias dentro de una transacción revertida; no aprueban a usuarios existentes ni envían fotografías personales. Este documento no certifica cumplimiento jurídico.


