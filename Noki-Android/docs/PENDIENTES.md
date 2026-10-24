# Noki — pendientes fuera del alcance disponible

21 de octubre de 2026 · acompaña SPEC 4.3 / Android 0.7.7.

Este documento no describe funciones disponibles. Conserva diferencias detectadas al contrastar el SPEC anterior con el cliente. No se han cambiado el login, las entregas, las cuentas ni el servicio para preparar el paquete.

## Funciones previstas que no deben atribuirse a esta app

| Referencia anterior / área | Estado comprobado y tratamiento |
|---|---|
| RF-43: cambiar correo | No existe pantalla/operación en el cliente. Cambiar el correo escrito antes de pedir OTP no cambia una cuenta. |
| RF-45: eliminar cuenta | No hay autoservicio de borrado. Privacidad remite a soporte. Automatizarlo exige decidir retenciones y constancias. |
| RF-09: recuperación solo de cuenta existente | El OTP común usa create_user=true; puede crear identidad desconocida. Separar recuperación sin romper las cuentas por código. |
| RF-21/RF-30: marca/modelo | No están en DeviceRecord ni formulario. El nombre descriptivo sí existe. |
| RF-31: editar categoría | El editor cambia nombre/seguridad/funcionamiento, no categoría. |
| RF-39: nota del resultado | No hay campo ni propiedad. Reapertura de resultado personal sí está. |
| RF-40: filtro de resultado | Solo Pendientes/Historial y búsqueda por nombre. |
| RF-02: tres recientes en Inicio | Hay resumen de cantidades y acceso a Mis aparatos, no tres tarjetas recientes. |
| RF-26: precisión GPS numérica | Se avisa de ubicación aproximada, no muestra metros de precisión. |
| RF-51: borrador privado durable de siete días | No existe caché de aparatos reales/cola offline con ese plazo. Estado de pantalla no es garantía durable. |
| RF-67/RNF: inspección profunda de imágenes en servidor | La app procesa JPEG y Storage restringe acceso/tamaño/tipo; falta validar y sanear contenido contra clientes modificados. |
| RF-68: retiro de solicitud | El servidor reconoce withdrawn; no hay botón de retirada automática en app. |
| RF-68: revisor asignado por expediente | Administración autorizada no equivale a asignación individual exclusiva por solicitud. |
| PLAN anterior: App Links | Manifest no registra enlaces profundos verificados. |
| RF-56: Google/Apple | Pospuestos; correo/contraseña y OTP siguen siendo los accesos disponibles. |
| RF-52: actualización automática | No hay tienda ni actualizador integrado. Distribución/firma productiva pendiente. |
| Multiplataforma / Flutter | El cliente es Android nativo. shared tiene targets iOS, no interfaz iOS. No Windows ni Flutter. |

## Diferencias menores de interfaz y operación detectadas

- La etiqueta heredada de resultado reutilizado aún menciona entrega a otra persona. No equivale a recepción con QR y está descrita expresamente en APP-20; conviene armonizar el texto en una futura corrección.
- El límite de WhatsApp valida forma (10–15 dígitos, sin cero inicial), no existencia ni país real. La ayuda solicita prefijo internacional; falta comprobación adicional si se decide exigirla.
- El requisito Android de orientación puede ser interpretado por el sistema en tabletas/multiventana. No prometer bloqueo universal.
- El User-Agent del mapa conserva una versión textual antigua; no altera la versión instalada. Actualización de metadatos pendiente.
- No se impide elegir la misma foto en los tres apartados; la revisión humana debe detectarlo.
- No hay editor general del lugar aprobado ni chat de incidencias. Se utiliza soporte.
- No hay presentación de notificaciones push ni carga de datos privados garantizada sin conexión.
- La base incluida del directorio son dos referencias históricas; los lugares actuales se obtienen del servicio.

## Requisitos operativos, no funcionalidades certificadas

1. Revisión legal de términos, complemento y tratamiento de datos antes de lanzamiento comercial.
2. Ejecutar y documentar eliminación/retención de fotos exigida por política; no existe tarea automática. Verificar copias de seguridad y supresión efectiva.
3. Revisión integral de RLS, permisos de funciones heredadas y avisos de seguridad, sin asumir que clave publicable visible equivale a vulnerabilidad ni que ocultarla protege datos.
4. Límites y capacidad del SMTP y servicios públicos de mapas/rutas; no se garantiza volumen comercial con la configuración de demostración.
5. Firma de producción, recuperación protegida de claves y publicación/actualización en tienda.
6. Verificar accesibilidad con personas y lectores de pantalla, todas las configuraciones objetivo, permisos, consumo/red y una entrega física entre dos cuentas autorizadas.
7. Mantener un procedimiento de reclamaciones, visitas, revisión de establecimientos y contacto actualizado. La aprobación técnica no acredita trabajo humano no realizado.
8. Preparar restauración completa del backend en entorno separado si se solicita: las migraciones del cliente no son un respaldo de datos, Storage, SMTP y secretos.

## No son pendientes de esta versión

La web administrativa ya existe y comparte servicio con la app. Aprobación/publicación automática y sincronización por Internet no requieren cable. El QR estable y la confirmación del receptor ya son parte del comportamiento implementado.

## Conservación de requisitos anteriores

El SPEC 4.2 original, PLAN y TASKS previos se conservan en `docs/archive/antes-de-SPEC-4.3/`. Los identificadores RF/HU/RNF de esos documentos son históricos; los identificadores APP-01…APP-40 describen la línea base entregada. No se eliminan las aspiraciones: se separan de lo que realmente se puede usar.
