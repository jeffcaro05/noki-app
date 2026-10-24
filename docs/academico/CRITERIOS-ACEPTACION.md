# Criterios de aceptación del cierre

24/10/2026. Estos criterios resumen el comportamiento del producto; los SPEC precisan condiciones y límites. Su vinculación con código y pruebas está en [TRAZABILIDAD.md](TRAZABILIDAD.md). No se presentan como resultados de tareas medidos con participantes.

## CA01

Dado un aparato y las respuestas de orientación, cuando se completa el recorrido, entonces se muestra una recomendación y un siguiente paso identificable.

## CA02

Dada la decisión de conservar, reparar o reutilizar personalmente, cuando se guarda, entonces no se crea un código de entrega y se explica que no habrá recepción.

## CA03

Dada una cuenta confirmada y contraseña válida, cuando se inicia sesión, entonces se accede a esa cuenta sin exigir un código en cada entrada; credenciales inválidas producen un error comprensible.

## CA04

Dado un acceso por código o recuperación, cuando se valida el código enviado al correo correspondiente, entonces se completa el flujo previsto; no se reutiliza como código la contraseña de aplicación del servicio de correo.

## CA05

Dada una cuenta sin entrega activa, aparatos preparados y receptor habilitado, cuando se crea la entrega, entonces aparecen su código y QR y queda pendiente, no completada.

## CA06

Dada una entrega pendiente, cuando se consulta de nuevo o transcurre el traslado, entonces conserva su código; no se permite generar una segunda entrega activa. Cancelarla permite preparar otra.

## CA07

Dado el código de una entrega dirigida a un receptor, cuando este lo escanea o ingresa, entonces puede revisar los aparatos; el escaneo por sí solo no completa la recepción.

## CA08

Dada una recepción revisada, cuando el receptor autorizado confirma que recibió, entonces la entrega se completa y el remitente puede ver Resultado e historial tras consultar el estado actualizado.

## CA09

Dada una solicitud de receptor incompleta, cuando se intenta avanzar, entonces se indican los requisitos faltantes; una solicitud enviada queda en revisión y no habilita el rol automáticamente.

## CA10

Dado un administrador autorizado y una solicitud revisable, cuando aprueba con las validaciones exigidas, entonces el estado y el punto publicado se reflejan en los clientes conectados. No se permite autoaprobar la propia solicitud.

## CA11

Dado un usuario sin autorización administrativa, cuando intenta acceder a operaciones de revisión, entonces el servidor rechaza el acceso aunque se omita la interfaz.

## CA12

Dado un destino seleccionado, cuando se calcula ruta desde una ubicación permitida o una dirección indicada, entonces el mapa muestra el recorrido o comunica el error sin perder la entrega preparada.

## CA13

Dado un perfil autenticado, cuando se cambia la imagen, entonces puede encuadrarse antes de guardarla; el cambio de nombre respeta la restricción de 30 días definida por la app.

## CA14

Dada la consulta de contenido, cuando se cambian opciones de presentación disponibles, entonces se conserva la navegación y se muestran textos e indicadores comprensibles. La evaluación humana de accesibilidad integral permanece abierta.

## CA15

Dado un panel autorizado y una actualización relevante, cuando llega un aviso o se realiza la siguiente consulta, entonces se obtiene el estado vigente; sin conexión no se muestra una escritura no realizada como exitosa.

