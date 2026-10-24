# Noki App — especificación de la versión implementada

**SPEC 4.3 · Android 0.7.7 (15) · 21 de octubre de 2026**

## 1. Autoridad y alcance

Este documento especifica el comportamiento implementado del código entregado. No es una lista de aspiraciones ni una certificación de ausencia de defectos. Cambiar una regla funcional exige actualizar su descripción y pruebas.

- Documento vigente: `docs/Noki-App-SPEC.md`; `SPEC.md` en la raíz del ZIP es una copia idéntica.
- [MATRIZ-SPEC-CODIGO.md](MATRIZ-SPEC-CODIGO.md) vincula cada apartado con implementación y pruebas, e inventaría los archivos de producción en sentido inverso.
- [PENDIENTES.md](PENDIENTES.md) conserva funciones solicitadas o previstas que **no** están implementadas, diferencias conocidas y verificaciones pendientes. No amplía las funciones disponibles.
- `docs/archive/` conserva documentos anteriores; no son requisitos vigentes ni evidencia del estado actual de servicios externos.
- [VERIFICACION.md](VERIFICACION.md) recoge los resultados comprobados. Pruebas anteriores no se presentan como ensayos nuevos.

Noki es **Android nativo, Kotlin y Jetpack Compose**, no Flutter. El núcleo Kotlin Multiplatform no implica que existan interfaces instalables iOS o Windows. La web administrativa es otro cliente y proyecto. Este SPEC describe la app y su contrato con el servicio, no atribuye a la app funciones exclusivas de la web.

## 2. Propósito y permisos

### APP-01 · Propósito y límites

Noki orienta sobre aparatos eléctricos/electrónicos: conservar, reparar, reutilizar o entregar. Permite consultar lugares, guardar preparación, solicitar ser receptor y registrar entregas con confirmación del receptor. El contenido y directorio inicial se enfocan en Medellín; el formulario de lugar admite dirección y ciudad sin geocerca obligatoria.

No realiza diagnóstico técnico, certificación ambiental, transporte, reservas, pagos ni garantía absoluta de seguridad del establecimiento. Una recepción registrada no demuestra reciclaje ni tratamiento final. La aprobación de una solicitud no verifica biométricamente la identidad o autenticidad documental.

### APP-02 · Actores

| Actor | Acceso | Límites |
|---|---|---|
| Visitante | Inicio, orientación, Aprende, directorio/mapa/rutas, información de receptores, accesibilidad y documentos legales | No guarda aparatos, envía solicitudes ni genera entregas reales |
| Cuenta autenticada | Perfil; con consentimiento vigente, perfil cargado y alias, aparatos, solicitudes y entregas propias | No aprueba receptores ni consulta datos privados ajenos |
| Solicitante | Presenta lugar, consulta estado y corrige cuando se solicitan cambios | Enviar no concede el rol |
| Receptor aprobado | Mantiene funciones personales; accede a Recepciones, valida código y confirma recepción asignada | No se entrega a sí mismo; escanear no completa |
| Administración externa | Revisión y decisiones mediante web y permisos de servidor | No existe autoaprobación ni panel administrativo en la app |

Una cuenta puede entregar y recibir sin registrar otra identidad. El rol procede del servidor, no de un selector libre. Aprobar/suspender recepción no borra la cuenta personal.

## 3. Navegación, identidad y accesibilidad

### APP-03 · Entrada e Inicio

Al abrir se presenta animación de símbolo, arcos y partículas durante unos 1.450 ms y transición. Se omite con movimiento reducido. No necesita descargar primero el directorio.

La primera bienvenida explica responder, elegir y guardar/continuar. Ofrece «Entrar o crear cuenta» y «Explorar sin cuenta». Haber visto la bienvenida se guarda localmente. Inicio mantiene una explicación breve de Noki, accesos a orientación, puntos, educación y recepción.

«Preparar entrega» aparece en Inicio y Mis aparatos; con entrega activa ofrece continuarla. Cuando hay aparatos cargados muestra cantidades pendientes/historial, no tres registros recientes. Si hay orientación pendiente tras autenticación, ofrece retomarla.

### APP-04 · Destinos y retroceso

| Destino | Contenido | Continuidad |
|---|---|---|
| Inicio | Propósito, preparar, orientar, puntos, Aprende y recepción | Raíz pública |
| Mis aparatos | Pendientes/historial, búsqueda y entrega activa | Detalle, preparar o cuenta si falta acceso |
| Puntos | Mapa/lista, buscador de entrega, ficha y ruta | Conserva contexto de aparatos/entrega al entrar desde preparación |
| Aprende | Cinco temas desplegables con fuentes | Orientación o navegador externo |
| Mi cuenta | Acceso o perfil, contraseña, foto, receptor | Inicio al completar acceso, no salto obligatorio a Mis aparatos |
| Menú superior | Mi cuenta, accesibilidad, términos/privacidad y Acerca de | Retorno a navegación previa |

Las cuatro raíces usan instancia única y restauración de estado. Orientación, detalle, preparación, entrega, recepción y solicitud se abren sobre ellas. Cerrar ficha o volver no cancela entrega ni borra datos.

En orientación Atrás vuelve a la pregunta anterior; desde resultado vuelve a funcionamiento si correspondía o a seguridad. «Orientar otro aparato» limpia respuestas e identificador de borrador. El formulario de receptor tiene botón para el paso anterior; no se promete persistencia de un formulario abandonado.

### APP-05 · Cabecera y avisos

Sin sesión, cuenta muestra «Registrarse o iniciar sesión», con desplazamiento limitado a dos repeticiones. Con letra grande o movimiento reducido usa texto estático de dos líneas. Con sesión muestra foto privada si está cargada o icono de cuenta. Inicio saluda con alias, ajustando distribución si es largo o la letra está ampliada.

Al completar acceso/registro y alias muestra confirmación verde con chulo durante unos 2,4 segundos, ampliables por recomendaciones de accesibilidad del sistema, y abre Inicio. Si autenticó pero falló cargar perfil, mantiene sesión y ofrece reintento, no declara completado el perfil.

Avisos de operación: éxito verde y chulo, error rojo y X. Las tarjetas explicativas/precauciones usan su presentación informativa, no equivalen a éxito. Los fallos del servicio se traducen; no se imprimen credenciales ni códigos de acceso.

### APP-06 · Distribución y ajustes

La actividad solicita orientación vertical. Android puede imponer reglas propias de ventana en pantallas grandes; no se garantiza bloqueo absoluto en todo equipo/multiventana.

La página común tiene desplazamiento vertical, sin efecto de rebote de sobrepaso en ese contenedor, margen para teclado, 24 dp horizontales, separación de 16 dp y máximo 640 dp de contenido. Mapas ampliados tienen distribución propia y áreas seguras. No se fuerza justificación completa de párrafos.

Desde 700 dp de ancho hay barra lateral. En menor ancho hay navegación inferior; por debajo de 360 dp o desde escala 1,3 se distribuyen las cuatro opciones en dos filas. Botones principales: altura mínima 56 dp; otras acciones usan sus propios componentes.

Accesibilidad permite apariencia del sistema/clara/oscura; texto del sistema/grande 1,15/más grande 1,3; mayor contraste; reducir movimiento. La escala efectiva es el máximo entre sistema y Noki, no su producto. Preferencias locales persistentes; restablecer no modifica Android. Hay descripciones de acciones, selección semántica, encabezados y avisos accesibles en los componentes implementados; no supone certificación integral de accesibilidad.

### APP-07 · Diseño y transiciones

Marca y tipografía Poppins incluidas. Claro: verde `#10661E`, azul `#0F2167`, fondo `#F5F4F0`, menta `#DCEADD`. Oscuro: fondo verdoso `#1B2420`, superficie `#242F29`, acento `#9CDD9D`, no negro puro.

Colores cambian gradualmente en 650 ms. Navegación: entrada 380 ms, salida 260 ms, retorno 340/300 ms, desvanecimiento y desplazamiento corto. Preguntas tienen transición propia de hasta 380 ms. Botón principal: escala 0,985 al pulsar, 100 ms, además de respuesta Material. Movimiento reducido suprime esos desplazamientos/duraciones; no reactiva animaciones deshabilitadas por Android.

## 4. Cuenta, sesión y consentimiento

### APP-08 · Entrada por contraseña

Entrada principal con correo/contraseña y mostrar/ocultar. Supabase Auth valida y el cliente exige correo confirmado. Error no crea otra identidad ni sobrescribe una sesión válida.

Al entrar no se aplica retroactivamente el mínimo de creación: una contraseña existente se envía sin recortar ni normalizar. Se distinguen credenciales inválidas, no confirmado, límites y fallos de servicio. No se guarda contraseña localmente. No hay botones operativos de Google, Apple/iCloud ni otro proveedor social.

### APP-09 · Registro, código y recuperación

Crear cuenta, entrar con código y recuperar acceso usan OTP por correo de Supabase. La contraseña de aplicación del correo emisor no es código de usuario. Entrega y plantilla del correo dependen del servicio.

El formulario admite 6–10 dígitos; el servidor decide validez/vencimiento. Impide reenvío por 60 segundos, además de límites remotos. Cambiar correo en el formulario reinicia el intento; no modifica el correo de una cuenta existente.

La solicitud OTP usa actualmente `create_user: true`: un correo desconocido puede crear identidad incluso por «Recuperar acceso». No hay circuito separado que garantice recuperación únicamente de identidades existentes. Si ya existe, el código accede a la misma cuenta. La mejora está en PENDIENTES, no se declara resuelta.

Tras registro/recuperación se ofrece establecer contraseña; puede omitirse y continuar por código. Perfil permite establecerla después. Con contraseña válida no se exige OTP en cada entrada.

### APP-10 · Alias y perfil

Completar registro requiere alias único de 3–24 caracteres: letras ASCII, números o guion bajo. Servidor valida disponibilidad e intervalo: cambio una vez cada 30 días; interfaz muestra fecha disponible. Nombre personal es distinto, opcional; edición exige 2–80 caracteres.

Mi cuenta presenta correo, alias, nombre/foto opcionales, estado de receptor, contraseña, actualizar perfil, Mis aparatos y salida. Si falla perfil puede reconsultarse sin sustituir cuenta. No hay cambio de correo ni eliminación automática de identidad desde pantalla.

### APP-11 · Contraseña y fotografía

Crear/cambiar contraseña: mínimo 12 caracteres, máximo 72 bytes UTF-8 y confirmación coincidente. No se trunca ni conserva; operación autenticada del servicio.

Foto: selector de imágenes del sistema sin permiso general de archivos. Encuadre manual por arrastre/zoom 1×–4× y controles horizontal/vertical; «Mostrar la foto completa» restablece. Vista circular muestra el resultado; conservar imagen completa puede dejar fondo. Guardar confirma, cancelar no publica ni modifica original.

Se genera JPEG 512×512, máximo 512 KiB, sin metadatos originales. Storage privado por titular. Se puede cambiar/quitar; descarga valida tamaño y dimensiones. No se usa para reconocimiento facial ni aprobar receptor.

### APP-12 · Sesión y salida

Sesión local contiene usuario, correo, tokens y vencimiento cifrados con AES-GCM y clave Android Keystore. No guarda contraseñas ni credenciales administrativas. Al operar se renueva cuando faltan aproximadamente 90 segundos para vencimiento.

Fallo de red/perfil/renovación conserva estado recuperable y muestra error; no convierte cualquier fallo en cierre forzoso. Salir limpia primero sesión y datos personales en memoria, reinicia navegación privada e intenta logout remoto de alcance local. Si no lo confirma, avisa. No borra cuenta/registros del servidor.

Operaciones privadas se serializan y se bloquea repetición mientras están ocupadas. Un identificador de generación evita aplicar respuestas tardías de otra sesión. No sustituye la autorización del servidor.

### APP-13 · Términos y privacidad

Antes de guardar información o usar código se solicitan dos aceptaciones desmarcadas: términos y tratamiento necesario. Leer no acepta. Rechazar conserva acceso público y no habilita funciones privadas «bajo propia responsabilidad».

Versión general incorporada: `2026-10-09.1`, con complemento de directorio/contacto fechado en `LegalDocuments`. Hay constancia local cifrada y aceptación de cuenta/versiones/fecha en servidor. No se descarga texto legal futuro automáticamente: cambios remotos deben coordinarse con actualización del cliente.

Responsables: Jeffry Giovanny Caro Arango y Luis Miguel Castañeda. Contacto: `nokiappcorporation@gmail.com`. Términos, privacidad, complemento y enlaces de proveedores accesibles en la app.

«Retirar permiso en este teléfono» confirma, cierra sesión y detiene uso personal local; intenta registrar revocación remota. No borra todo ni garantiza sincronización sin red. Supresión, exportación, reclamaciones y correcciones no disponibles se solicitan por correo. No hay exportador ni borrado automático de cuenta. La retención/eliminación de fotos descrita en privacidad requiere gestión humana: no existe tarea automática que certifique su cumplimiento.

## 5. Orientación y educación

### APP-14 · Preguntas

No exige registro. Categoría, seguridad y, solo con seguridad normal, funcionamiento. Nunca pide encender/cargar/desmontar para responder.

Categorías: teléfono/tableta, computador/periféricos, audio/video, accesorios/cables, pequeño electrodoméstico, otro. Seguridad: normal/atención/desconocida. Funcionamiento: funciona/fallas/desconocido. Seguridad no normal omite funcionamiento y lo trata como desconocido.

### APP-15 · Recomendación y continuidad

| Respuesta | Orientación |
|---|---|
| Atención o seguridad desconocida | Revisar seguridad |
| Normal y funciona | Segunda vida |
| Normal y fallas/desconocido | Revisar reparación |

Resultado incluye explicación, precaución, tres pasos concretos y aviso de no diagnóstico. Seguridad indica no usar/cargar/abrir, consultar antes de transporte y ayuda de emergencia si hay peligro inmediato.

Cambiar seguridad limpia funcionamiento; cambiar respuestas conserva recorrido, comenzar otro lo reinicia. Resultado ofrece guardar/preparar o continuar entrega según entrada, y consultar puntos. Guardar requiere cuenta habilitada y mantiene contexto para retomarlo. No convierte orientación en recepción realizada.

### APP-16 · Aprende

Cinco temas: RAEE, antes de descartar, proteger información, daño/calentamiento y antes de ir a un punto. Un tema desplegado cada vez, texto/fuente/fecha editorial/enlace externo. Si no hay aplicación para enlace avisa. Contenido incluido legible sin red; fuentes externas necesitan conexión. No hay gestor remoto de artículos ni cuestionario adicional.

## 6. Aparatos, preparación e historial

### APP-17 · Alta y listado

Desde orientación se crea pendiente propio con identificador estable de borrador, descripción 2–120 caracteres y respuestas. No hay marca/modelo/serie/foto/precio. Entrada por Preparar entrega propone entregar; orientación general deja decisión sin elegir.

Mis aparatos: Pendientes/Historial, búsqueda por descripción y actualizar. Historial incluye resultado personal, retirado y entrega confirmada, además de constancias de cancelación. Orden de carga por actualización. No hay filtro por tipo de resultado. Un pendiente guardado no es entrega realizada.

### APP-18 · Detalle y decisiones

Detalle muestra orientación, decisión, preparación y resultado; incorpora etapa de entrega cuando corresponde. Decisiones: seguir usando, revisar/reparar, otro uso sin entregar, entregar/donar. Orientación recomienda, no fuerza decisión. Un estado de atención puede entregar bajo APP-25, no bloqueo absoluto.

Tras decidir aparece «Pasar a preparación». Cambios se guardan remotamente. Editar pendiente no bloqueado permite descripción, seguridad y funcionamiento, no categoría. Seguridad no normal fuerza funcionamiento desconocido y recalcula orientación.

Un aparato en entrega no cancelada queda bloqueado para cambios incompatibles. Entrega completada no se reabre ni se convierte en resultado personal por edición.

### APP-19 · Cinco preparaciones

Respaldo, cuentas, medios extraíbles, borrado seguro y destino. Cada aspecto: pendiente/hecho/no aplica. No aplica es declaración, no verificación automática. No se exige encender un equipo inseguro.

Generar entrega requiere las cinco resueltas (hecho/no aplica). Una referencia guardada no asigna por sí sola receptor. Conservar/reparar/reutilizar explica que no habrá QR/código. En esos casos se puede registrar resultado sin resolver las cinco; no se marcan hechas automáticamente.

### APP-20 · Resultado personal y retirada

Para decisión sin entrega se confirma resultado ya ocurrido: conservado, reparado, reutilizado u otro, con fecha e historial. No hay nota del resultado. La etiqueta heredada de reutilización menciona «entregué a alguien»; no genera constancia verificada ni resultado `delivered` de Noki.

Resultado personal se puede reabrir tras confirmación: quita resultado/fecha, conserva preparación y vuelve a pendientes. Retirar un pendiente lo archiva como «Retirado por ti», sin contar entregado ni borrar físicamente datos. Un retirado no ofrece restauración automática.

### APP-21 · Persistencia y conflictos

Registros reales remotos; editar envía ID y versión. Un conflicto no sobreescribe silenciosamente. Alta usa ID estable para recuperar intentos ambiguos. Solo se presenta guardado tras respuesta satisfactoria.

No hay cola de escrituras sin conexión ni base offline de aparatos reales. Estado de formulario/navegación no es borrador cifrado durable de siete días; lo no enviado puede perderse al abandonar/cerrar proceso. Actualizar al reconectar consulta servicio.

## 7. Entrega, código y recepción

### APP-22 · Recorrido completo

`Preparar entrega → orientación → continuar → guardar pendiente → decisión entregar → preparación → elegir destino habilitado → generar QR/código → ruta opcional → receptor valida y confirma → Resultado/Historial`.

La ficha de destino puede generar directamente. No se emite código antes de asignar receptor. Elegir lugar abre ficha; aún requiere confirmar «Generar QR y código» o acción equivalente de crear entrega.

Se conservan IDs exactos al ir/regresar del mapa. Marcador/ruta no crean otra entrega. Si hay entrega pendiente, la continuidad ofrece mostrar su QR/código. Ruta no valida recepción.

### APP-23 · Requisitos de creación

- Cuenta verificada, consentimiento, alias y perfil cargado.
- Entre 1 y 20 aparatos distintos, propios, pendientes, sin archivar, decisión entregar y cinco preparaciones resueltas.
- Ningún aparato vinculado a otra entrega no cancelada.
- Ninguna otra entrega activa del emisor; el estado heredado recibido pendiente de cierre también cuenta como activo.
- Receptor habilitado y distinto de la propia cuenta, no solo referencia.
- Disponibilidad consultada correctamente y, si corresponde, declaración de contacto.

Creación conserva ID para reintentar y lista agrupada; servidor valida permisos/estados. Si crea pero falla descarga de código, informa que quedó pendiente y ofrece recuperarlo, no crear otra. Fallo de red no se presenta como éxito.

### APP-24 · QR y código estables

QR y manual identifican la misma entrega. QR: `NOKI1:<identificador UUID>:<credencial hexadecimal de 64 caracteres>`. Manual: 10 caracteres hexadecimales. Se muestran al emisor autenticado y son utilizables por receptor asignado. El campo de lectura manual admite hasta 160 caracteres para permitir también introducir el contenido del QR.

**Permanecen iguales mientras esté pendiente. No se renuevan cada nueve/diez minutos, por calcular ruta, reabrir app o consultar desde otra sesión del titular.** Servidor reproduce el par con clave privada de derivación, nunca incluida en cliente; guarda hashes y estado. Cliente interpreta `valid_until: delivery_closed`, sin cuenta regresiva.

Completar/cancelar invalida ambos; no sirven para otra entrega. QR no contiene correo/contraseña ni lista completa de aparatos. Recuperarlo si no está en memoria requiere red; no se promete QR offline tras reinicio.

### APP-25 · Contacto y aparatos con atención

Si cualquier aparato no tiene seguridad normal o categoría anunciada por receptor, muestra explicación y WhatsApp público autorizado. Persona marca que confirmó que puede llevarlo; se habilita generación si cumple lo demás.

Sin contacto válido no puede cumplirse esta condición con ese receptor; se debe elegir otro o resolver contacto. Cambiar receptor/aparatos reinicia declaración. Se registra fecha/número, no se lee WhatsApp ni se prueba la conversación. Abrir WhatsApp no envía mensaje automáticamente. Declarar no garantiza aceptación o transporte seguro; siguen precauciones.

### APP-26 · Recepción

Receptor aprobado usa Recepciones con misma cuenta. Introduce manual o abre lector QR; cámara se solicita al leer, no al iniciar. Denegar cámara conserva código manual. Validación obtiene vista previa con lista y condiciones.

**Escanear/escribir no completa.** Receptor revisa y confirma explícitamente que recibió, mediante diálogo. Servidor comprueba asignación, aprobación, estado, validación previa y límites de intentos. Nueva entrega pasa de pendiente a completada sin segunda confirmación del emisor.

La validación de código está limitada en servidor a cinco intentos dentro de una ventana de quince minutos por receptor; una validación correcta reinicia el contador. El bloqueo de intentos no cambia el código de la entrega ni borra sus aparatos. No debe confundirse este límite con la duración del QR, que sigue siendo la de la entrega pendiente.

Pantalla de entrega del emisor consulta aproximadamente cada cinco segundos visible/activa; al ver completada presenta Resultado. Mis aparatos/historial reflejan constancia al consultar. No hay push ni garantía instantánea sin red. Recepciones tiene también actualización manual.

Recepciones lista las entregas dirigidas a esa cuenta, ordenadas por creación descendente y con su estado. No añade pestañas separadas de pendientes/historial en esa pantalla. La persona puede volver a gestionar sus propios aparatos sin cerrar sesión.

Compatibilidad: entregas antiguas `received` conservan cierre por emisor; no es flujo de nuevas entregas.

### APP-27 · Estados y cancelación

| Estado | Significado | Continuidad |
|---|---|---|
| `pending` | Creada, no confirmada | Emisor muestra código/ruta o cancela; receptor revisa/confirma |
| `completed` | Receptor confirmó | Constancia/resultado/historial, sin edición unilateral |
| `cancelled` | Cancelada antes de recepción | Código inválido, historial; aparatos pueden prepararse de nuevo |
| `received` | Estado heredado | Cierre final por emisor; no flujo nuevo |

Cancelar exige motivo 5–300 caracteres y confirmación. No borra aparatos/cuenta. Completada no tiene cancelación unilateral. Discrepancias por soporte, sin chat de disputa. Constancia/fecha no equivalen a certificado de reciclaje.

## 8. Directorio, mapa y rutas

### APP-28 · Datos y distinción de lugares

Carga puntos activos del mismo servicio usado por administración. Ficha: nombre/dirección/coordenadas, categorías/detalles, fuente y revisión; horario/teléfono opcionales con aviso de ausencia, WhatsApp autorizado cuando existe.

Distingue referencia que no confirma, receptor habilitado, lugar propio no destinatario propio y lugar publicado cuya disponibilidad debe consultarse. No afirma convenio ni revisión de receptor para toda referencia.

Visibilidad pública no depende de lista privada de destinatarios: referencias y lugar propio siguen visibles. No desaparecen todos porque no haya receptores elegibles. Sin receptor habilitado no genera QR de Noki y lo explica.

### APP-29 · Buscar y ficha

Buscador «Buscar lugar de entrega», acompañado de explicación de búsqueda por nombre o dirección y resultados desplegables. Admite hasta 180 caracteres, sin distinguir mayúsculas en la coincidencia. Categoría solo restringe al activar «Solo categorías publicadas». «Ver todos los lugares y actualizar» limpia filtros.

Marcador/resultado abre ficha, condiciones, fuente y acciones. Revisión mayor de 90 días muestra advertencia. Ofrece llamada si hay teléfono, WhatsApp si hay contacto, fuente original para referencia y otra app de mapas. Fallar apertura externa deja los datos disponibles.

### APP-30 · Interacción y origen

Mapa incrustado de 330 dp no captura desplazamiento de página; «Ampliar y mover el mapa» abre mapa interactivo con zoom/desplazamiento. Lista alternativa si falla; ciclo de vida administra pausa/reanudación/destrucción.

Origen: ubicación actual con permiso, marca manual, o dirección con elección de coincidencia. Geocodificador de Android recibe 6–180 caracteres y devuelve hasta cinco coincidencias. Buscar origen no equivale a buscar lugar de entrega.

Ubicación aproximada admitida; denegar permiso/GPS no bloquea alternativas. Usa ubicación reciente (menos de dos minutos) o espera nueva hasta unos 18 segundos. Sin seguimiento de fondo ni origen en cuenta. Muestra tipo de origen, no precisión numérica en metros. Cambiar/quitar origen invalida ruta previa.

### APP-31 · Ruta opcional

Con origen/destino, calcular en carro solicita confirmación para enviar coordenadas a OSRM. Abre mapa ampliado y dibuja línea con km/minutos aproximados; sin tráfico, giro a giro ni modo peatonal. Puede cancelar; cambiar selección cancela operación anterior y no mantiene ruta de otro destino como vigente.

Fallo ofrece reintentar/lista/mapa externo. Límites cliente: OSRM 18 s; geocodificación 20 s; mapa avisa tras 20 s sin renderizado. No garantizan exactitud/disponibilidad del proveedor.

### APP-32 · Publicación y sincronización

Aprobar solicitud con marcador confirmado publica/actualiza punto asociado; suspender lo retira del catálogo activo. App no aprueba al consultar ni duplica manualmente lugares.

App y web comparten Supabase; Firebase aloja web, no segunda base sincronizada por cable. Puntos consulta al entrar/reanudar y cada 10 s visible; componentes de actualización de entrega/receptor usan 5 s. Se detienen fuera de estado activo; hay actualización manual. Los cambios tardan intervalo más red, no son instantáneos garantizados.

Antes de la primera respuesta remota hay dos referencias incorporadas en el cliente (El Tesoro y Monterrey, fechadas en el catálogo incluido); no incluyen aprobación de receptor. Si falla descarga, usa última copia cifrada del directorio cuando existe, o conserva esas referencias, con advertencia. Sin datos ofrece reintento. Una copia antigua no autoriza entrega: servidor comprueba disponibilidad. Caché de teselas no es función de mapas descargables offline.

## 9. Solicitud y rol de receptor

### APP-33 · Información previa

Inicio/perfil ofrecen «Quiero recibir aparatos». Invitación de dos páginas manuales, sin rotación automática. Explica cuenta, lugar/fotos, revisión y uso. Dos casillas antes de solicitar: comprensión y aceptación de condiciones/fotos; se indica cuál falta.

No pide cédula, fotos de documento, selfies, prueba de vida ni verificación de pago. Informa de visitas ocasionales posibles sin prometer visita inicial/frecuencia. No hay comisiones actuales.

### APP-34 · Formulario y reglas exactas

| Paso | Campo | Regla |
|---|---|---|
| Responsable | Nombre completo | 5–120 caracteres tras recortar espacios exteriores |
| Responsable | Correo | Muestra sesión verificada, no editable aquí |
| Responsable | Mayoría de edad y autorización | Casilla obligatoria |
| Lugar | Nombre | 3–100 caracteres |
| Lugar | Dirección y ciudad | Un campo 10–240 caracteres |
| Lugar | Ubicación confirmada | Latitud finita −90…90; longitud finita −180…180 |
| Lugar | Horario | Texto 5–200 caracteres |
| Lugar | WhatsApp | 10–15 dígitos, sin cero inicial; ayuda solicita país |
| Lugar | Publicación comercial y contacto | Casilla obligatoria separada |
| Lugar | Categorías | Al menos una de seis |
| Lugar | Condiciones | 10–500 caracteres |
| Fotos/revisión | Fachada, recepción, almacenamiento | Tres selecciones, una por apartado |
| Fotos/revisión | Veracidad y revisión de fotos | Casilla obligatoria |

Avanzar no queda silenciosamente deshabilitado por datos incompletos: salvo operación ocupada, pulsarlo muestra errores y desplaza al primer requisito sin avanzar/enviar. Resumen enumera pendientes y permite revisarlos. Ayuda/contador/error próximos, etiquetas externas para letra ampliada. Una regla compartida decide resumen, errores y avance.

Cambiar dirección borra coordenadas y exige reconfirmarlas. Volver al paso anterior conserva formulario activo. No se valida autenticidad civil, propiedad, existencia real de WhatsApp ni horarios.

### APP-35 · Fotos y envío

Selector del sistema; se pide no incluir personas/documentos/datos privados. Corrige orientación, limita entrada a 100 millones de píxeles, reduce por muestreo y recodifica JPEG calidad 85 sin EXIF/GPS, máximo 3 MiB cada una. No comprueba que sean distintas o auténticas.

Solo «Enviar solicitud» inicia operaciones: borrador, contacto autorizado, ubicación, tres archivos y envío a revisión. Fallo conserva lo alcanzado remotamente, consulta estado e informa, no aprueba. Rutas privadas por titular/solicitud; no se publican en directorio. Saneamiento del cliente no equivale a validación profunda del contenido en servidor.

### APP-36 · Revisión y estados

| Estado | Presentación/acciones |
|---|---|
| Sin solicitud / `draft` | Presentar/continuar datos disponibles; seleccionar de nuevo fotos/consentimientos cuando corresponda |
| `pending` | En revisión, consultar estado, sin recibir aún |
| `needs_changes` | Nota y corregir/reenviar |
| `approved` | «Perfil revisado», cambiar a receptor; completar marcador ausente en registros antiguos |
| `suspended` | Aviso/nota, sin nuevas recepciones habilitadas |
| `withdrawn` | Compatible con servidor, sin botón de retirada automática en esta app |

La web administrativa existente revisa datos/fotos con permisos y registra decisión/nota; backend conserva revisor/fecha. Decisión negativa operativa: pedir cambios o suspender; no se inventa estado `rejected` en pantalla.

Titular puede actualizar WhatsApp y autorizar publicación. Aprobado no dispone de editor general de dirección/horario/fotos; cambios no disponibles usan soporte. Distintivo indica aprobación de solicitud, no certificación civil/ambiental ni garantía absoluta.

## 10. Datos y contratos — APP-37

| Entidad | Información |
|---|---|
| Sesión | Usuario, correo, tokens, vencimiento; cifrada localmente |
| Perfil | Alias/fecha de cambio, nombre opcional, foto privada, consentimiento |
| Aparato | ID/titular, descripción/categoría/estados, recomendación derivada, decisión, punto, cinco preparaciones, resultado/fecha, versión/actualización/archivo |
| Punto | ID, nombre/dirección/coordenadas, fuente/URL/revisión, categorías/detalles, horario/teléfono, nota, solicitud asociada/WhatsApp opcionales |
| Solicitud | Titular/responsable, lugar/contacto, ubicación, categorías/condiciones, fotos privadas, aceptaciones, estado/revisión |
| Entrega | ID/emisor/receptor/lugar, estado, lista de aparatos y descripción/categoría/seguridad, fechas, contacto declarado/constancia |
| Código | ID entrega, QR/manual y vigencia pendiente; no token de Auth |
| Preferencias | Apariencia/escala/contraste/movimiento/bienvenida locales |

Servidor es autoridad de titularidad, aprobación, versiones, unicidad, intervalos, transiciones y fechas; un booleano del cliente no concede permisos.

### Valores del modelo intercambiado

| Dato | Valores usados |
|---|---|
| Categoría | `phone`, `computer`, `audio_video`, `accessories`, `appliance`, `other` |
| Seguridad | `normal`, `attention`, `unknown` |
| Funcionamiento | `works`, `faults`, `unknown` |
| Recomendación derivada | `second_life`, `repair_review`, `safety_review` |
| Decisión | `undecided`, `keep`, `repair`, `reuse`, `deliver` |
| Preparación: claves | `backup`, `accounts`, `media`, `erasure`, `destination` |
| Preparación: valores por clave | `pending`, `done`, `not_applicable` |
| Resultado | Sin resultado (`null`) o `kept`, `repaired`, `reused`, `delivered`, `other` |
| Archivo | `archived_at` nulo o fecha; retirar no crea resultado entregado |
| Entrega | `pending`, `received` heredado, `completed`, `cancelled` |
| Solicitud | `draft`, `pending`, `needs_changes`, `approved`, `suspended`, `withdrawn` |

El cliente envía descripción y estados con sus códigos, `selected_point_id` opcional y las cinco claves de preparación. Las fechas/versiones se leen del servidor. Los nombres legibles de interfaz no se usan para resolver la identidad de un receptor: se utilizan los identificadores asociados al punto/solicitud.

### Operaciones remotas

| Contrato | Uso |
|---|---|
| Supabase Auth | Contraseña, renovación, OTP, verificación, cambiar contraseña, logout local |
| `noki_client_config` | Disponibilidad del código por correo |
| `noki_profile_action` | Perfil, aceptar/revocar, alias, nombre, cambios de foto |
| `noki_devices` / `noki_archive_device` | Registros del titular y archivo versionado |
| `noki_collection_points` | Puntos activos públicos |
| `noki_native_action` | Estado, borrador/contacto/ubicación/envío de solicitud, crear entrega, obtener credencial, vista previa, recibir, cancelar y cierre heredado |
| Storage privado | Foto de perfil y fotos de solicitud mediante sesión/rutas permitidas |
| Android Geocoder | Direcciones de origen/establecimiento |
| MapLibre/OpenStreetMap | Renderizado/teselas con atribución |
| OSRM | Ruta en carro entre coordenadas consentidas |

Tablas privadas `receiver_requests`, `handoffs`, `handoff_items`, `handoff_tokens`, `handoff_code_key` y auditorías no son API de escritura directa móvil. Las migraciones entregadas son historial de evolución, no exportación de datos/secretos ni restauración completa del servicio.

La operación nativa se expresa con `op` y `args`: `state`, `request_draft`, `request_contact`, `request_location`, `request_submit`, `create`, `token`, `preview`, `receive`, `cancel` y `complete` heredado. No existe operación móvil para aprobar. Una respuesta de error no se interpreta como una escritura exitosa.

## 11. Protección, conectividad y errores — APP-38

URL/clave publicable de Supabase son configuración visible, no secreto administrativo. Auth/RLS/funciones autorizan. No se incluyen SMTP, clave de servicio ni firma privada en fuentes. Copia de seguridad de la app y tráfico sin cifrar deshabilitados. Cámara/ubicación por función, no al arrancar.

Privacidad permite operación administrativa necesaria, no publicidad/venta ni publicación de fotos privadas. Texto legal no acredita cumplimiento operativo; revisión jurídica, retención y auditoría siguen documentadas aparte.

| Caso | Respuesta actual |
|---|---|
| Acceso rechazado | Mensaje, sin sesión ficticia |
| OTP inválido/vencido/límites | Mensaje, reintento/reenvío controlado |
| Autenticó pero perfil falló | Conserva sesión y reintenta |
| Solicitud incompleta | Resumen, error local y primer campo visible |
| Directorio vacío | Limpiar filtros/actualizar, sin inventar lugares |
| Mapa falla | Lista/dirección, reintento/mapa externo |
| Permiso rechazado | Origen manual/dirección o código manual |
| Receptor propio/referencia/no habilitado | Explicación y elegir otro, sin QR inválido |
| Entrega activa | Continuar/cancelar antes de otra |
| Creación exitosa, QR falla | Mantiene pendiente y recupera mismo código |
| Conflicto o red al guardar | No declara éxito; consultar/reintentar |
| Completada/cancelada | Código deja de ser utilizable; constancia conserva estado |

HTTP de cuenta/datos: llamada 20 s, conexión 10 s. Orientación/educación/documentos incorporados. Directorio puede usar copia avisada. Guardar, cuentas, solicitudes, recepción y recuperar código necesitan servicio; no existe cola privada de fondo ni dependencia de USB para compartir datos.

## 12. Proyecto y distribución — APP-39

Paquete `co.noki.app`, versión 0.7.7, código 15; mínimo Android API 26, compilación/objetivo 36. `app`: interfaz y cliente. `shared`: orientación, aparatos/entregas, solicitud y encuadre. Targets iOS del núcleo no son aplicación iOS terminada.

JDK 17, Gradle Wrapper 8.13, Android Gradle Plugin 8.13.2, Kotlin 2.2.21. Incluye fuentes, recursos, tipografías/licencias, Wrapper, dependencias declaradas, pruebas y reglas. IDE/SDK/JDK y bibliotecas descargables se instalan aparte.

`debug` es variante entregada con práctica deshabilitada; `release` también, con reducción de código pero sin firma de producción. `qa` es desarrollo aislado con ID distinto y simulación local, no otra app que deba instalar el usuario ni sustituto de pruebas reales. Su código/pruebas se conservan.

APK no se actualiza automáticamente: otra APK compatible, misma firma y aprobación de instalación, o futura distribución en tienda. Actualizar directorio no cambia binario. No hay actualizador ni publicación en tienda. Firma privada no se incluye; compilar en otro equipo puede generar firma diferente e impedir actualizar instalación previa.

## 13. Criterios y mantenimiento — APP-40

La revisión de esta línea base comprueba: acceso existente; registro/código/alias; consentimiento; orientación; validación visible del receptor; persistencia/conflictos; referencia frente a receptor; continuidad de selección/ruta; una entrega activa; código estable; vista previa sin completar; confirmación/cancelación; contraste/texto/movimiento; extracción y compilación.

Las suites automatizan parte mediante respuestas controladas. No sustituyen entrega física, TalkBack, revisión legal, disponibilidad de proveedores o examen exhaustivo de seguridad. La matriz distingue implementación de prueba específica, sin afirmar cobertura total.

Cada cambio requiere actualizar APP afectado, matriz, pruebas proporcionales y pendientes/versionado. Modelos de teléfonos de ensayo, instalaciones, sesiones, claves y logs no son requisitos funcionales del producto: pertenecen a operación/evidencia, no al SPEC.

