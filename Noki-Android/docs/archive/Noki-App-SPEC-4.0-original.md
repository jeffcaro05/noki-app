# Noki — Especificación definitiva de requisitos del producto

**Versión:** 4.0  
**Fecha:** 15 de octubre de 2026  
**Producto:** Noki App  
**Responsables del producto:** Jeffry Giovanny Caro Arango y Luis Miguel Castañeda  
**Idioma de la experiencia:** español  
**Territorio inicial del directorio:** Medellín, Colombia  
**Tipo de documento:** especificación normativa de comportamiento, reglas, interfaces y aceptación.

## 0. Uso y autoridad del documento

Este SPEC define qué debe hacer Noki, quién puede realizar cada acción, qué datos necesita, cómo debe responder ante errores y con qué criterios se acepta su funcionamiento. Es la línea base del alcance descrito aquí; una modificación funcional posterior requiere una nueva revisión del documento.

«Debe» expresa un requisito obligatorio. «Puede» identifica una opción permitida. «No debe» establece una prohibición. Los identificadores RF, RNF, RN y HU son estables y permiten relacionar desarrollo, pruebas y decisiones con el requisito correspondiente.

Un requisito escrito no acredita por sí solo que esté implementado o probado. El estado de implementación, los resultados de pruebas, las incidencias y la aprobación de una versión se registran fuera del SPEC. Una función obligatoria ausente es una brecha, no una función que pueda darse por cumplida o eliminarse sin una decisión de alcance.

No pertenecen a este documento los equipos particulares usados durante el desarrollo, instalaciones realizadas, registros de compilación, capturas de pruebas, credenciales, instrucciones de conexión, paquetes de entrega ni conversaciones del equipo.

### Índice

1. [Propósito, alcance y límites](#1-propósito-alcance-y-límites).
2. [Actores, permisos y vocabulario](#2-actores-permisos-y-vocabulario).
3. [Estructura de navegación y recorrido principal](#3-estructura-de-navegación-y-recorrido-principal).
4. [Inicio y acceso a funciones](#4-inicio-y-acceso-a-funciones).
5. [Registro, autenticación y sesión](#5-registro-autenticación-y-sesión).
6. [Perfil y administración de la cuenta](#6-perfil-y-administración-de-la-cuenta).
7. [Educación y orientación](#7-educación-y-orientación).
8. [Aparatos, preparación e historial](#8-aparatos-preparación-e-historial).
9. [Directorio, mapa y destino](#9-directorio-mapa-y-destino).
10. [Solicitud y rol de receptor](#10-solicitud-y-rol-de-receptor).
11. [Entregas, códigos y recepción](#11-entregas-códigos-y-recepción).
12. [Diseño de interacción y accesibilidad](#12-diseño-de-interacción-y-accesibilidad).
13. [Privacidad, consentimiento y acceso a datos](#13-privacidad-consentimiento-y-acceso-a-datos).
14. [Instalación, conectividad y actualizaciones](#14-instalación-conectividad-y-actualizaciones).
15. [Reglas de negocio y máquinas de estados](#15-reglas-de-negocio-y-máquinas-de-estados).
16. [Modelo de información y contratos](#16-modelo-de-información-y-contratos).
17. [Arquitectura e integraciones](#17-arquitectura-e-integraciones).
18. [Requisitos no funcionales](#18-requisitos-no-funcionales).
19. [Historias de usuario y criterios de aceptación](#19-historias-de-usuario-y-criterios-de-aceptación).
20. [Estados de interfaz y recuperación de errores](#20-estados-de-interfaz-y-recuperación-de-errores).
21. [Configuración y condiciones operativas](#21-configuración-y-condiciones-operativas).
22. [Aceptación del producto, métricas y riesgos](#22-aceptación-del-producto-métricas-y-riesgos).

## 1. Propósito, alcance y límites

### 1.1 Problema y propuesta

Las personas pueden conservar aparatos eléctricos y electrónicos en desuso sin saber si conviene seguir usándolos, repararlos, darles otro uso o entregarlos. También pueden desconocer los lugares que los reciben y la preparación necesaria antes de trasladarlos.

Noki debe ayudar a comprender esas opciones, tomar una decisión informada, preparar los aparatos, encontrar lugares y registrar la recepción cuando ambas partes utilizan el servicio. No sustituye un diagnóstico técnico ni el trabajo de un gestor ambiental.

El público prioritario son personas adultas de 40 a 60 años. Esta prioridad orienta el lenguaje, la legibilidad y la facilidad de uso; no obliga a recopilar la edad de quienes consultan funciones públicas.

| Necesidad | Respuesta del producto |
| --- | --- |
| N1. Comprender el manejo responsable de aparatos y RAEE | Aprende, fuentes identificadas y recomendaciones comprensibles. |
| N2. Decidir qué hacer con un aparato | Orientación basada en categoría, condición visible y funcionamiento declarado. |
| N3. Encontrar opciones locales | Directorio y mapa con datos públicos, fuente, fecha y condiciones. |
| N4. Continuar hasta una acción concreta | Pendientes, decisión, preparación, entrega cuando corresponda y resultado claramente identificado. |

### 1.2 Alcance obligatorio de Noki App

- Inicio público, explicación del propósito, orientación y contenido educativo.
- Registro y acceso por correo verificado; contraseña como entrada principal para cuentas que la tengan y código como alternativa o recuperación.
- Perfil, alias obligatorio, foto opcional, preferencias, cambio de contraseña y gestión de cuenta y privacidad.
- Directorio público, búsqueda, fichas, mapa, alternativas de punto de partida y rutas opcionales.
- Aparatos propios, pendientes, decisiones, preparación, resultados personales e historial.
- Solicitud de receptor, revisión autorizada, consulta de estado y cambio entre contexto personal y recepción.
- Entrega de uno o varios aparatos, QR y código manual, confirmación del receptor, cancelación y constancia.
- Accesibilidad, temas claro/oscuro, recuperación de errores, conservación de contexto y consulta pública limitada sin conexión.
- Contratos de datos y permisos necesarios para interoperar con administración, sin incorporar un panel administrativo dentro de la app.

La cuenta común no requiere documento de identidad, dirección de residencia, teléfono ni biometría. La solicitud voluntaria de receptor requiere información del responsable y del lugar según RF-67, sin fotografías de documentos ni reconocimiento facial.

### 1.3 Plataformas y ampliaciones separadas

| Área | Alcance de esta línea base | Condición para ofrecerla |
| --- | --- | --- |
| Android, teléfonos y tabletas | Cliente nativo; Android 8.0/API 26 o superior. | Aceptación funcional, de seguridad y accesibilidad de la versión distribuida. |
| iOS e iPadOS | Ampliación prevista; objetivo de compatibilidad iOS/iPadOS 16 o superior. | Cliente nativo, permisos, almacenamiento, firma y recorridos completos aceptados por separado. |
| Escritorio Windows | Ampliación prevista; objetivo Windows 10/11 de 64 bits. | Cliente adaptado a ventanas, teclado y tecnologías de asistencia; validación independiente. |
| Google y Apple | Proveedores sociales previstos, no sustituyen el acceso por correo. | Configuración y aceptación completas; no mostrar botones operativos antes de habilitarlos. |
| Web administrativa | Producto separado, conectado al mismo servicio autorizado. | Especificación propia de revisores, permisos, auditoría y operación. |
| Comisiones o pagos | Fuera del alcance vigente. | Nueva decisión de producto, requisitos, información y aceptación antes de cobrar. |

El núcleo compartido no equivale a disponibilidad de una app en todas las plataformas. La app de escritorio de usuario no debe conceder funciones administrativas. La app no debe presentar un cliente web embebido como una interfaz nativa.

### 1.4 Límites del servicio

Noki no ofrece diagnóstico, reparación propia, transporte o recogida domiciliaria, compraventa, reservas ni cupos garantizados, pesaje, certificación de reciclaje, certificados ambientales, recompensas, chat interno, mensajes automáticos por WhatsApp/SMS, notificaciones push, seguimiento continuo, tráfico en tiempo real ni navegación giro a giro.

No debe calcular beneficios ambientales a partir del número de aparatos. No debe afirmar convenios, visitas, controles de identidad o ausencia de fraude sin evidencia real. Consultar un lugar no equivale a reservar ni a comunicarle una visita.

El directorio inicial se centra en Medellín. Consultar la app desde otro territorio no debe crear automáticamente una oferta local inexistente. Las demás piezas del proyecto transmedia no forman parte de este alcance.

## 2. Actores, permisos y vocabulario

### 2.1 Actores

- **Visitante:** persona sin sesión. Consulta contenido público y puede iniciar el registro.
- **Usuario registrado:** cuenta con correo verificado, sesión válida, consentimiento vigente y alias configurado para utilizar funciones privadas.
- **Solicitante de receptor:** usuario que presenta información del lugar. La solicitud no le concede permiso de recepción.
- **Receptor aprobado:** usuario con permiso adicional, otorgado por el servidor tras revisión. Conserva sus funciones personales.
- **Operador autorizado:** persona con atribuciones administrativas explícitas y auditadas. No obtiene ese rol registrándose como receptor.
- **Lugar de referencia:** establecimiento incluido por fuentes públicas, sin acceso automático a las entregas de Noki.
- **Proveedor técnico:** servicio externo de identidad, almacenamiento, correo, cartografía, direcciones o rutas; no decide por sí solo la aprobación de receptores.

### 2.2 Matriz de permisos

| Acción | Visitante | Usuario | Receptor aprobado | Operador autorizado |
| --- | --- | --- | --- | --- |
| Consultar Inicio, Aprende, orientación, lugares y documentos | Sí | Sí | Sí | Sí |
| Solicitar ubicación o ruta de forma voluntaria | Sí | Sí | Sí | Sí |
| Guardar aparatos, preparación y resultados personales | No | Solo propios | Solo propios | No por el mero rol administrativo |
| Consultar o modificar perfil y foto | No | Solo propios | Solo propios | Únicamente si una función administrativa autorizada lo justifica |
| Crear y cancelar una entrega pendiente | No | Solo como remitente y con las reglas de entrega | Igual que usuario; nunca hacia sí mismo | No como sustituto de una parte |
| Consultar una entrega | No | Si es remitente | Si es el receptor asignado o remitente | Solo dentro de un caso o función autorizados y auditados |
| Escanear/escribir código y confirmar recepción | No | No | Solo entregas asignadas, con aprobación vigente | No sustituye la recepción física |
| Presentar solicitud de receptor | No | Sí | Según estado de su solicitud existente | No aprueba por actuar como usuario |
| Aprobar, suspender o retirar un receptor | No | No | No | Sí, según atribuciones |
| Publicar o corregir una referencia del directorio | No | No | No directamente | Sí, con fuente y auditoría |
| Leer fotos privadas de una solicitud | No | Solo las de su solicitud | Solo las propias | Solo las asignadas a su revisión |

Una petición autorizada debe comprobar identidad, propiedad, consentimiento y estado en el servidor. Ocultar un botón no sustituye esos controles.

### 2.3 Vocabulario oficial

| Término | Significado |
| --- | --- |
| Aparato | Registro personal de un equipo o accesorio; equivale a «dispositivo» en contratos técnicos. |
| Mis aparatos | Nombre visible del módulo privado. |
| Orientación | Guía no diagnóstica basada en respuestas declaradas. |
| Decisión | Intención del usuario, no acción realizada. |
| Preparación | Revisión de cinco tareas; no demuestra seguridad física ni recepción. |
| Pendiente | Aparato cuyo proceso no ha terminado; puede tener o no una entrega creada. |
| Lugar de referencia | Ficha pública que no puede confirmar códigos por el solo hecho de aparecer en el directorio. |
| Receptor habilitado | Lugar asociado a una cuenta aprobada y disponible para recibir mediante Noki. |
| Perfil revisado | Revisión de la información del lugar; no certificación de identidad civil, licencia ambiental o ausencia de riesgo. |
| Lugar guardado | Referencia asociada a un aparato, sin reserva ni entrega automática. |
| Entrega | Grupo concreto de aparatos propios dirigido a un único receptor. |
| Código/QR de entrega | Credencial temporal que permite al receptor asignado consultar y confirmar esa entrega. |
| Resultado personal | Acción declarada por el propietario sin constancia de recepción de Noki. |
| Constancia de recepción | Registro de confirmación explícita del receptor; no certifica tratamiento posterior. |
| Retirar un pendiente | Archivarlo como no entregado; no borrar sus datos del servidor. |
| Supresión | Procedimiento de eliminación o anonimización de datos según política aplicable; distinto del archivo operativo. |
| Borrador | Información en preparación, todavía no enviada para su acción final; puede conservarse localmente o guardarse de forma privada. |
| RAEE | Residuos de aparatos eléctricos y electrónicos; un aparato en desuso no se considera automáticamente residuo. |

## 3. Estructura de navegación y recorrido principal

### 3.1 Destinos principales

La navegación persistente ofrece **Inicio, Mis aparatos, Puntos y Aprende**. Mi cuenta se accede desde la cabecera de Inicio y desde el menú. La selección de un destino principal no debe acumular copias de ese destino.

Los recorridos contextuales conservan una acción de volver al punto de entrada. Atrás retrocede un nivel; desde un destino principal regresa a Inicio y desde Inicio permite salir. No debe revelar una pantalla privada utilizable después de cerrar sesión.

### 3.2 Entrega: secuencia normativa

| Paso | Acción o información | Condición para continuar |
| --- | --- | --- |
| 1. Preparar entrega | Disponible en Inicio y Mis aparatos. | Si ya existe una entrega activa, abrirla en vez de iniciar otra. |
| 2. Orientación y datos | Describir el aparato, conocer la recomendación y guardarlo como pendiente. | Acceso válido para guardar; sin cuenta se conserva el contexto hasta autenticarse. |
| 3. Decisión | Elegir conservar, reparar, dar otro uso o entregar/donar/recolectar. | Una decisión distinta de «Aún no he decidido». |
| 4. Preparación | Revisar las cinco tareas aplicables. | Cada tarea está en Hecho o No aplica; no se exige una maniobra insegura. |
| 5. Aparatos y destino | Elegir 1–20 aparatos y un lugar habilitado. | Propiedad, preparación, disponibilidad, ausencia de autoentrega y de otra entrega activa. |
| 6. Contacto, cuando corresponda | Mostrar condiciones y contacto; confirmar que se habló con el receptor. | Declaración explícita si hay señales de atención, incertidumbre o categoría no anunciada. |
| 7. Generar QR y código | Crear la entrega y abrir su credencial real. | Respuesta confirmada del servidor; la ruta no es un requisito. |
| 8. Ruta opcional | Elegir partida y calcular; volver mediante Mostrar QR y código. | Puede omitirse, cancelarse o fallar sin crear otra entrega. |
| 9. Recepción | Receptor asignado consulta el código, revisa la lista y confirma que recibió. | Aprobación vigente, código válido y confirmación explícita. Escanear no basta. |
| 10. Resultado | Mostrar la recepción completada y actualizar historial. | Estado confirmado por el servidor; no pedir otra confirmación al remitente. |

Seleccionar un marcador para consultar su ficha no debe crear una entrega accidentalmente. La acción explícita «Generar QR y código» se presenta en esa misma ficha cuando el recorrido y la selección lo permiten.

### 3.3 Recorrido sin entrega

Conservar, reparar o dar otro uso sin entregarlo permite registrar un resultado personal. La app debe indicar que esa decisión no genera código ni QR. No debe obligar a buscar un receptor o crear una entrega ficticia para terminar ese recorrido.

### 3.4 Acceso desde una tarea

Un visitante puede consultar orientación, lugares y Aprende. Si intenta guardar o entregar, debe acceder a su cuenta. Tras el acceso, la confirmación precede a Inicio; allí una acción explícita permite continuar el progreso pendiente, sin repetir preguntas ni guardar por el usuario.

## 4. Inicio y acceso a funciones

### RF-01 — Inicio público

Debe mostrar marca, propósito breve, Preparar entrega, orientación, Puntos, Aprende y acceso a registro/inicio de sesión. No debe reducir toda la primera experiencia a un formulario de acceso ni exigir registro para consultar información pública.

### RF-02 — Inicio con sesión

Debe mostrar el saludo con el alias, acceso a Mi cuenta, las funciones públicas y los accesos personales. Debe permitir retomar una entrega activa o un pendiente. Cuando muestre resumen de aparatos recientes, debe limitarlo a tres y ofrecer Ver todos; un fallo de ese resumen no bloquea la navegación.

### RF-03 — Navegación consistente

Cada pantalla debe identificar su tarea, ofrecer una salida visible y mantener una jerarquía predecible. Las etiquetas de navegación son Inicio, Mis aparatos, Puntos y Aprende. Las acciones principales utilizan verbo y objeto, no iconos aislados ni «Aceptar» sin contexto.

### RF-57 — Bienvenida y entrada contextual

La primera bienvenida explica describir el aparato, consultar orientación/lugares y continuar con la preparación. Ofrece Entrar o crear cuenta y Explorar sin cuenta. Se recuerda localmente y no se repite al cambiar de sección. Cerrar el acceso contextual conserva las respuestas; no crea cuenta, aparato ni entrega.

### RF-75 — Propósito permanente

Inicio mantiene una explicación breve, incluso después de la bienvenida: Noki ayuda a decidir qué hacer con los aparatos eléctricos y electrónicos, encontrar lugares en Medellín y preparar una entrega paso a paso. Debe explicar utilidad y siguiente acción sin prometer recogida, convenio o recepción garantizada.

### RF-79 — Cabecera y confirmación de acceso

Sin sesión, la esquina superior derecha ofrece Registrarse o iniciar sesión. El texto puede desplazarse suavemente un máximo de dos recorridos; con movimiento reducido permanece estático y con texto grande puede ocupar dos líneas. Con sesión y aceptación vigentes muestra la foto privada o icono alternativo, con etiqueta accesible Mi cuenta.

Después de completar acceso/registro se muestra una confirmación temporal de 2,4 segundos de base, ampliable por preferencias de accesibilidad, y se abre Inicio, no Mis aparatos. No se muestra éxito antes de confirmar la operación. La confirmación no debe impedir retomar un contexto guardado.

### RF-83 — Entrada directa y avisos inequívocos

Preparar entrega está en Inicio y Mis aparatos. Este recorrido puede preseleccionar Entregar, donar o llevar a recolección como intención editable, sin dar por realizada la entrega. El guardado crea un pendiente.

Los avisos positivos emplean superficie verde, chulo y texto; errores o rechazos, superficie roja, X y explicación. Los avisos informativos o preventivos no deben simular éxito. El mismo error de formulario no se duplica en dos recuadros simultáneos.

## 5. Registro, autenticación y sesión

### RF-04 — Alta por correo y configuración de contraseña

El registro debe solicitar correo real, aceptación de términos y autorización necesaria de datos. El correo se confirma mediante código antes de habilitar datos privados. El alias obligatorio se configura conforme a RF-78; el nombre personal y la foto son opcionales.

Tras verificar el correo, el usuario puede definir una contraseña de Noki o continuar con código. Al crear o cambiar contraseña se exigen mínimo 12 caracteres, máximo 72 bytes UTF-8 y repetición coincidente. No se recorta, normaliza, trunca ni modifica la contraseña. Una entrada con una contraseña existente no se rechaza localmente por aplicar retroactivamente los mínimos de creación.

El correo se valida por formato, se eliminan espacios externos y se normaliza para el servicio de identidad. No se añade un dominio ficticio. La cuenta común no solicita nombre civil obligatorio, documento, dirección, edad exacta ni teléfono.

### RF-05 — Verificación del correo

Solicitar un código no inicia sesión ni acredita que el mensaje llegó. Solo una respuesta válida del servicio de identidad confirma acceso al correo. La pantalla muestra destinatario, campo compatible con pegar, Verificar y continuar, Cambiar correo y Reenviar código. Cambiar destinatario limpia el código introducido.

Un código inválido, usado o vencido no debe activar funciones privadas. El usuario conserva una salida y la opción de solicitar otro. Los códigos no se generan ni validan localmente, no se guardan en preferencias, estado restaurable, registros o analítica.

### RF-06 — Reenvío controlado

Debe impedir solicitudes simultáneas, mostrar una espera mínima de 60 segundos y respetar límites adicionales del servidor. El mensaje de solicitud es neutral respecto a la existencia de una cuenta. No se reenvía automáticamente en bucle ni se promete recepción en la bandeja de entrada.

### RF-07 — Inicio de sesión

Correo y contraseña son la entrada principal para quien tenga contraseña. Credenciales correctas y correo confirmado permiten acceder sin exigir un nuevo código en cada entrada. Se ofrece Entrar con un código como alternativa y Olvidé mi contraseña como recuperación.

Los errores distinguen credenciales incorrectas de indisponibilidad del servicio, sin revelar innecesariamente si existe una cuenta. Un error conserva el correo y no publica detalles técnicos. Una cuenta sin contraseña debe poder acceder con código y configurarla, sin pedir la contraseña de su proveedor de correo.

### RF-08 — Mostrar u ocultar contraseña

Los campos sensibles ofrecen un control accesible para mostrar u ocultar el valor sin modificarlo. La contraseña no debe persistir al abandonar el recorrido ni serializarse para restaurar una pantalla.

### RF-09 — Recuperar acceso

Olvidé mi contraseña solicita un correo y un código de verificación. Debe responder de forma neutral, por ejemplo: «Si puedes recibir mensajes en ese correo, revisa las instrucciones para continuar». No debe revelar públicamente la existencia de la cuenta ni reemplazar una sesión ajena abierta.

### RF-10 — Establecer una nueva contraseña

Después de verificar un código válido para la cuenta, debe ofrecer nueva contraseña y confirmación, con las reglas de RF-04. Debe respetar la reautenticación que exija el servidor. Solo confirma el cambio tras respuesta satisfactoria; un fallo permite reintentar sin anunciar que la contraseña fue cambiada.

### RF-11 — Mantener y recuperar sesión

Debe guardar de forma segura una sesión autenticada y renovarla mediante el servicio de identidad. Una caída de red, una foto que no cargó o un fallo al leer perfil/aparatos no equivale a credenciales inválidas y no debe borrar la sesión válida.

Cuando el servicio confirme que la sesión está revocada o vencida sin posibilidad de renovación, se bloquean las operaciones privadas y se solicita acceso. No deben mostrarse datos de una cuenta anterior a una nueva cuenta. Los borradores autorizados se identifican por titular.

### RF-12 — Cierre de sesión

Debe limpiar sesión local, datos privados de memoria, foto, borradores y navegación privada, y mostrar Inicio público. Debe intentar revocar la sesión remota; si no puede confirmarlo, informa esa diferencia sin mantener acceso local. No borra los registros del servidor por cerrar sesión.

### RF-56 — Código alternativo y proveedores sociales

Gmail, iCloud, Outlook y otros dominios se tratan como direcciones de correo. Tener una dirección de Google o Apple no equivale a usar sus botones de acceso social.

El código sirve para alta, acceso alternativo y recuperación. Los proveedores sociales, cuando se habiliten, deben usar sus flujos oficiales y validación de identidad. Google debe permitir entrada directa, con el código de correo como alternativa independiente, no como segundo paso obligatorio. Noki nunca solicita la contraseña de Google o Apple.

Cancelar una autorización social conserva el contexto. No se fusionan cuentas solo porque coincidan direcciones sin prueba válida de control de identidad. Clientes, redirecciones y secretos deben estar configurados y probados antes de habilitar los botones.

### RF-73 — Acceso contextual estable

Entrar o crear cuenta se presenta en un contenedor fijo con un único desplazamiento vertical. No debe combinar desplazamiento del contenido y arrastre de hoja de manera que produzca rebote continuo al llegar al final. Teclado, márgenes seguros y Volver sin perder mi progreso permanecen accesibles.

### RF-77 — Disponibilidad de acceso y consentimiento

La disponibilidad del envío por correo se consulta al iniciar y al abrir el acceso. Si no puede confirmarse, debe permitir Comprobar disponibilidad, sin fingir envíos. Un indicador de disponibilidad no concede permisos ni demuestra recepción del correo.

Las plantillas de alta y acceso contienen el código generado por el servicio, nunca un código fijo ni una contraseña de envío. Las cuentas existentes conservan su método de entrada. Antes de usar registros privados se valida y registra la aceptación vigente en servidor; una aceptación local no sustituye esa comprobación.

### RF-78 — Alias obligatorio

El registro se completa con un nombre de usuario de 3–24 caracteres: letras ASCII, números o guion bajo. Debe ser único sin distinguir mayúsculas. Una cuenta anterior sin alias completa este paso conservando su identidad y registros.

El servidor limita los cambios a uno cada 30 días desde la elección o cambio anterior. Reenviar el mismo valor es idempotente y no reinicia el plazo; cambiar solo mayúsculas cuenta como cambio. Perfil muestra la fecha de próximo cambio. El nombre personal opcional no sustituye el alias, no altera esta regla ni concede roles.

Inicio muestra Bienvenido, [alias]; puede ir debajo del logo cuando el ancho o tamaño de texto lo requiera. El alias no se presenta como identidad civil verificada. La restricción de cambios no anula solicitudes de rectificación de datos personales.

### RF-82 — Continuidad de identidad y acceso

Una actualización no debe sustituir la cuenta, borrar la sesión válida ni perder registros confirmados. La credencial autenticada se conserva antes de cargar recursos secundarios. Un perfil que no pudo consultarse no se interpreta como un nuevo usuario sin nombre: se ofrece actualizar.

La app debe conservar compatibilidad de identidad, almacenamiento seguro y migraciones locales. No debe usar una cuenta ficticia o un espacio de práctica como sustituto de una sesión real ni resolver errores de acceso eliminando datos sin autorización.

## 6. Perfil y administración de la cuenta

### RF-41 — Consultar Mi cuenta

Debe mostrar alias, nombre opcional, correo, estado de verificación, foto o icono alternativo y datos básicos de la cuenta. Debe ofrecer ajustes, actualización de perfil, gestión de contraseña, documentos legales, cierre de sesión y acceso a los procedimientos de cambio de correo y supresión.

Las funciones que dependan de aprobación de receptor se muestran según el estado real. Un error de consulta conserva contexto y permite reintentar, sin rellenar datos de ejemplo.

### RF-42 — Editar nombre personal

El nombre personal es opcional. Si se guarda, debe tener 2–80 caracteres después de recortar espacios externos. Guardar requiere confirmación del servidor; cancelar conserva el valor anterior. Cambiarlo no altera el alias, el correo, la titularidad ni la aprobación del receptor.

### RF-43 — Cambiar correo de la cuenta

Debe permitir solicitar un correo nuevo mediante una operación autenticada y explicar el procedimiento de confirmación. La dirección vigente no se sustituye por una escrita en el formulario hasta que el servicio de identidad confirme el cambio.

Debe preservar la misma cuenta y sus registros, gestionar expiración/reenvío y respetar la reautenticación exigida. No debe confundir Cambiar correo en un formulario de código con cambiar el correo de una cuenta ya existente. Si el cambio no está disponible, debe comunicarlo y ofrecer un canal de gestión, sin mostrar un cambio ficticio como realizado.

### RF-44 — Crear o cambiar contraseña desde Perfil

Una cuenta con sesión válida puede iniciar la operación con las reglas de RF-04. Para cuentas sin contraseña, el control debe decir Crear o cambiar contraseña. Debe respetar reautenticación, comprobar coincidencia y confirmar éxito solo después del servidor.

### RF-45 — Supresión y eliminación de cuenta

Debe existir un procedimiento accesible desde la cuenta y desde la política de privacidad. La ejecución de una eliminación exige confirmar identidad, reautenticación reciente o prueba equivalente, consecuencias y voluntad explícita. Una solicitud recibida no debe etiquetarse como eliminación completada.

El servidor debe revocar acceso, eliminar o anonimizar los datos correspondientes conforme a la política vigente y eliminar la identidad cuando proceda. Debe contemplar perfil, foto, borradores, aparatos, solicitudes, fotos de revisión y las relaciones con entregas. Los datos mínimos de constancias de dos partes o casos abiertos solo pueden conservarse si existe finalidad y plazo definidos; no se establece conservación perpetua por defecto.

Debe comprobarse también la eliminación de objetos, no solo sus filas de metadatos. Solo tras confirmar el estado final debe limpiarse el contexto local y mostrarse éxito. Un error parcial requiere recuperación identificable, no la afirmación de que todo se borró. La eliminación automática, si se ofrece, debe funcionar de extremo a extremo; no se presume implementada por existir este requisito.

### RF-76 — Foto privada de perfil

La foto es opcional. Añadir o cambiar abre el selector del sistema, con acceso solo a la imagen elegida. Cancelar no modifica el perfil ni inicia una carga. La imagen se orienta correctamente y presenta encuadre circular con arrastre, zoom 1×–4× y controles alternativos de posición y zoom.

Mostrar la foto completa encaja el original dentro del círculo con fondo neutro; ampliar o desplazar solo recorta lo que el usuario ve fuera del encuadre. Vista previa y archivo guardado deben usar la misma transformación.

La salida es JPEG de 512 × 512 píxeles, máximo 512 KiB, sin metadatos de ubicación. El original no se modifica. Guardar requiere acción explícita; la operación impide doble envío y conserva el valor anterior si falla. Quitar foto exige confirmación y elimina el objeto remoto, no el original de la galería.

Solo el titular autorizado accede a la foto. No se publica una URL libre ni se usa para reconocimiento facial o aprobación de receptor. La lectura limita descarga a 512 KiB y dimensiones a 1024 × 1024. Al salir se elimina de memoria; una respuesta tardía de otra sesión no puede aparecer en la cuenta actual.

## 7. Educación y orientación

### RF-13 — Contenido de Aprende

Aprende explica qué son los residuos de aparatos eléctricos y electrónicos (RAEE), conservación, reparación, reutilización, preparación de datos personales, señales que requieren precaución y consulta de lugares. Debe utilizar títulos concretos, párrafos breves y acciones comprensibles. Los términos técnicos se explican al primer uso. El contenido esencial se incluye con la app para consulta sin conexión.

### RF-14 — Afirmaciones sustentadas

Toda cifra, obligación o afirmación ambiental específica debe identificar fuente, fecha y ámbito territorial. No se deben presentar recomendaciones como diagnósticos, opiniones como certificaciones ni cifras de impacto sin método verificable. Las referencias externas deben abrirse por acción explícita y mostrar un error recuperable si no están disponibles.

### RF-15 — Orientación sin cuenta

El visitante puede iniciar, completar y repetir la orientación. Las respuestas permanecen en el contexto temporal del recorrido; no crean registros privados por sí solas. Solo Guardar o Continuar con mi entrega solicita acceso cuando sea necesario. Al regresar de autenticación, se ofrece retomar exactamente ese contexto, sin repetir preguntas ya contestadas.

### RF-16 — Categoría del aparato

La primera pregunta permite elegir una categoría: teléfono o tableta; computador o periférico; audio o video; accesorios; pequeño electrodoméstico; otro aparato. Cada opción incluye una explicación o ejemplo breve. Debe poder cambiarse la selección antes de continuar. La categoría no debe interpretarse como declaración de aceptación por un receptor.

### RF-17 — Condición visible y precauciones

La segunda pregunta distingue: sin señales visibles de atención; con señales como daño, hinchazón, fuga, calentamiento u olor inusual; y no estoy seguro. Las dos últimas respuestas conducen a revisión de seguridad y omiten cualquier pregunta que invite a encender o probar el aparato.

La orientación indica no encender, cargar ni desmontar un aparato con señales de riesgo. Antes de trasladarlo se debe consultar al receptor. Si hay peligro inmediato, la interfaz indica no transportarlo y buscar asistencia competente; marcar una casilla no convierte esa situación en segura.

### RF-18 — Funcionamiento declarado

Solo cuando no se declaran señales de atención se pregunta si funciona, presenta fallas o no se sabe. No se exige realizar una prueba para responder. Esta información es declarativa: Noki no examina el aparato ni certifica su estado.

### RF-19 — Recomendación determinista

| Condición visible | Funcionamiento | Recomendación |
| --- | --- | --- |
| Atención o desconocida | No se solicita | Revisión de seguridad antes de manipular o trasladar. |
| Sin señales de atención | Funciona | Explorar conservación, segundo uso o entrega responsable. |
| Sin señales de atención | Fallas o desconocido | Consultar posibilidades de revisión o reparación antes de decidir. |

El resultado explica por qué se muestra y ofrece tres pasos prácticos adecuados al caso. La recomendación no obliga a conservar ni impide por sí sola preparar una entrega; las condiciones de contacto de RF-85 regulan los casos especiales. Deben distinguirse claramente recomendación, decisión personal y recepción confirmada.

### RF-20 — Cambiar respuestas sin contradicciones

Volver permite revisar la respuesta anterior sin reiniciar todo. Cambiar una respuesta invalida las posteriores incompatibles y recalcula la recomendación. No debe conservarse una respuesta de funcionamiento si la nueva condición exige omitirla. Pulsaciones rápidas no deben saltar preguntas ni abrir varias pantallas.

### RF-21 — Guardar la orientación

Guardar requiere cuenta habilitada y descripción del aparato de 2 a 120 caracteres. Se guardan categoría, condición, funcionamiento cuando corresponda y recomendación derivada, nunca una recomendación arbitraria enviada por el cliente. Marca y modelo son opcionales, de hasta 60 caracteres cada uno.

La operación es idempotente: reintentar la misma solicitud tras una respuesta incierta no crea dos aparatos. El éxito solo se muestra tras confirmación del servidor. El registro inicial queda pendiente; guardarlo no representa una entrega ni un resultado final.

### RF-55 — Orientación que conduce a una acción

Al terminar se muestran una explicación breve y una acción principal: Continuar con mi entrega cuando ese sea el objetivo, o Guardar y decidir. Consultar lugares es una alternativa identificada, no una salida que haga perder la orientación. Si el usuario decide conservar, reparar o dar otro uso sin entregar, se explica que no necesita QR ni código de entrega.

## 8. Aparatos, preparación e historial

### RF-29 — Mis aparatos

Mis aparatos muestra exclusivamente los registros del titular, con dos ámbitos claramente identificados: Pendientes e Historial. Cada tarjeta incluye descripción, estado comprensible y siguiente acción. Un aparato recién guardado aparece como pendiente, no como entregado ni como resultado terminado. Una entrega activa tiene acceso visible a su QR y código.

El estado vacío ofrece Preparar entrega y explica qué aparecerá después. Carga, vacío y error son estados diferentes; un fallo no debe borrar visualmente datos previamente confirmados ni afirmar que no existen registros.

### RF-30 — Crear un aparato desde el recorrido

Preparar entrega, disponible en Inicio y Mis aparatos, inicia la orientación o permite retomar un pendiente. Añadir otro aparato utiliza las mismas preguntas, validaciones y reglas; no crea un segundo flujo contradictorio. Descripción: 2–120 caracteres; marca y modelo: opcionales, máximo 60 cada uno. Los espacios exteriores se eliminan; una cadena de espacios no es válida.

### RF-31 — Editar un pendiente

El titular puede editar descripción, datos opcionales, categoría y respuestas de un pendiente no bloqueado. Cambiar condición o funcionamiento recalcula la recomendación y exige revisar decisiones o preparación incompatibles. La edición utiliza una versión del registro para detectar modificaciones concurrentes; un conflicto no sobrescribe silenciosamente cambios más recientes.

No se pueden modificar los aparatos incluidos en una entrega activa para alterar lo que el receptor aceptará. Debe cancelarse primero esa entrega. Una constancia de recepción confirmada conserva su contenido histórico.

### RF-32 — Retirar un pendiente y solicitar supresión

Retirar un pendiente exige confirmación y lo archiva con fecha del servidor y estado «Retirado por ti · sin entrega». Deja de aparecer entre pendientes y permanece en Historial sin presentarse como entregado. Si pertenece a una entrega activa, primero debe cancelarse esta.

Retirar no equivale a borrar definitivamente datos personales. La supresión se gestiona según RF-45 y la política de conservación. No se elimina una constancia compartida de recepción mediante el botón para retirar un pendiente.

### RF-33 — Detalle del aparato

Debe reunir descripción, respuestas relevantes, recomendación, decisión, preparación, lugar asociado cuando exista y estado actual. Se muestra una acción principal coherente con la etapa y accesos secundarios a edición o historial cuando estén permitidos. No se repite la misma explicación completa en varias tarjetas consecutivas.

### RF-34 — Decisión explícita

| Valor de dominio | Texto y significado |
| --- | --- |
| `undecided` | Aún no he decidido. No habilita un resultado final. |
| `keep` | Conservar el aparato. No implica transferencia. |
| `repair` | Intentar repararlo o consultar reparación. No certifica que ya esté reparado. |
| `reuse` | Darle otro uso sin entregarlo. |
| `deliver` | Entregarlo: donar, regalar o llevar a un lugar de recepción, según sus condiciones. |

Una donación o regalo con transferencia a un receptor sigue el flujo de entrega, no el resultado de uso personal. Las etiquetas antiguas no deben reinterpretar automáticamente registros históricos como recepciones verificadas.

### RF-35 — Coherencia entre decisión y recomendación

La recomendación permanece visible como orientación, no como imposición. Si la decisión difiere, debe explicar las precauciones aplicables sin bloquear arbitrariamente. Para conservar o reutilizar sin entrega: «Como vas a conservar el aparato, no se generará un QR ni un código de entrega». Para entregar: indicar qué falta antes de generar el código.

### RF-36 — Preparación en cinco aspectos

| Clave | Aspecto | Regla |
| --- | --- | --- |
| `backup` | Copia de información importante | Confirmar preparación o que no aplica. |
| `accounts` | Cuentas personales | Orientar sobre cierre o desvinculación cuando proceda. |
| `media` | Tarjetas y medios extraíbles | Revisar SIM, memorias y otros elementos personales cuando existan. |
| `erasure` | Datos personales | Explicar que Noki no borra el aparato; no pedir operaciones inseguras. |
| `destination` | Condiciones del destino | Recordar aceptación, horario y consulta de condiciones antes del traslado. |

Cada aspecto admite Pendiente, Hecho y No aplica. Para generar una entrega, los cinco deben estar en Hecho o No aplica. No aplica no exige encender, desmontar ni dañar el aparato. La preparación no certifica borrado de información ni reserva un cupo.

### RF-37 — Conservar la preparación

Cada cambio confirmado debe pertenecer al aparato y a su titular. Al volver, cambiar de vista o reabrir la cuenta se recupera el último estado guardado. Un fallo conserva el cambio pendiente para reintentar e informa qué no se guardó; no muestra éxito anticipado.

### RF-38 — Resultado personal o recepción confirmada

Conservar, reparar o dar otro uso permiten registrar un resultado personal explícito. Decidir reparar no marca automáticamente Reparado. Esos resultados se identifican como declaraciones del usuario, no certificaciones de Noki.

El resultado Entregado solo se crea a partir de una recepción confirmada por el receptor autorizado. Guardar el aparato, elegir un lugar, calcular una ruta, generar el QR o escanearlo para vista previa no habilita ese resultado. No se exige una segunda confirmación del emisor después de la confirmación válida del receptor.

### RF-39 — Corregir un resultado personal

El titular puede corregir un resultado personal y su nota, conservando la fecha de modificación. No puede transformarlo directamente en Entregado. Las constancias confirmadas por un receptor no se reescriben con esta función: un desacuerdo requiere un caso de revisión identificable y trazable.

### RF-40 — Historial comprensible

Historial distingue resultados personales, recepciones confirmadas y pendientes retirados. Ordena por fecha del evento más reciente; permite buscar por descripción y filtrar por tipo de resultado. Una cancelación de entrega se conserva como evento cancelado, no como recepción. Los aparatos liberados por cancelación vuelven a pendientes con su preparación preservada.

### RF-74 — Continuidad entre etapas

Cada etapa presenta su acción siguiente al final del contenido: Pasar a decisión, Pasar a preparación y Elegir lugar de entrega, según el contexto. Si falta información, el botón explica qué falta y lleva al campo correspondiente. Los pasos anteriores siguen accesibles sin duplicar registros.

Resultado permanece bloqueado para una entrega hasta confirmación del receptor. La pantalla debe indicar «Pendiente de recepción» y ofrecer Mostrar QR y código, Calcular ruta o Cancelar entrega. Para una decisión personal, la acción permite registrar ese resultado sin código.

### RF-80 — Pendientes y cierre del recorrido

Solo puede existir una entrega activa por emisor. Puede contener varios aparatos preparados; no se crea otra hasta completar o cancelar la existente. Si se intenta iniciar una nueva, se abre la pendiente y se explica la condición.

Una recepción confirmada actualiza automáticamente todos los aparatos incluidos, abre Resultado si el recorrido está visible y los incorpora a Historial. Si la app estaba cerrada, la siguiente consulta recupera el estado real del servidor. No depende de que ambas personas mantengan abiertas sus pantallas al mismo tiempo.

## 9. Directorio, mapa y destino

### RF-22 — Lugares publicados y sus datos

El directorio público carga todos los lugares activos publicados. Cada ficha requiere identificador estable, nombre, dirección, ciudad, coordenadas confirmadas, procedencia de los datos y fecha de revisión. Horarios, categorías, condiciones, teléfono, WhatsApp y sitio web se muestran solo cuando están disponibles y autorizados; nunca se inventan.

Un lugar de referencia identifica su fuente pública. Un lugar procedente de una solicitud aprobada identifica esa revisión sin inventar un convenio o una certificación oficial. La app distingue datos no disponibles, lista vacía y fallo de conexión.

### RF-23 — Alcance y confianza sin promesas falsas

La ficha indica si se trata de una referencia informativa o de un receptor con solicitud revisada en Noki. Debe recordar revisar horarios, condiciones y aceptación antes de salir. No se debe afirmar «todos los lugares están verificados», «puedes ir sin riesgo» o «recepción garantizada» para el conjunto del directorio.

Si la última revisión supera 90 días, mostrar «Información pendiente de volver a verificar». Este aviso no inventa que el lugar cerró ni elimina su historial.

### RF-24 — Buscar y filtrar destinos

El campo se denomina «Buscar lugar o dirección de entrega». Debe explicar que busca destinos del directorio, no el punto de partida. Al escribir, una lista desplegable presenta coincidencias con nombre y dirección; seleccionar abre la ficha correspondiente.

Los filtros por categoría son explícitos, visibles y reversibles. Una respuesta de orientación no debe activar un filtro oculto. Si no hay coincidencias, ofrecer Quitar filtros y Actualizar lugares. El filtro de receptores capaces de generar código no debe existir como condición para ver el directorio.

### RF-25 — Seleccionar e inspeccionar un lugar

Tocar un marcador o resultado abre la ficha del mismo identificador. Debe mostrar dirección, condiciones, contacto disponible y acciones pertinentes. La selección no crea una entrega, no reserva, no envía mensajes y no notifica una visita.

En un recorrido de preparación, la ficha ofrece la acción de generar la entrega con ese destino cuando es elegible. En consulta general, ofrece preparar entrega o calcular ruta. Un lugar informativo conserva su ficha y rutas aunque no pueda registrar recepciones dentro de Noki; esa limitación se explica sin ocultarlo.

### RF-26 — Ubicación voluntaria

«Partir desde mi ubicación» solicita permiso en el momento de pulsarlo. Se acepta ubicación aproximada cuando permita orientar; se muestra su precisión sin fingir exactitud. No se solicita ubicación en segundo plano ni al abrir la app.

Denegar permiso, desactivar la ubicación o agotar el tiempo de espera no bloquea directorio ni entrega: se ofrece elegir punto en el mapa o escribir dirección. La ubicación personal solo se utiliza durante el cálculo solicitado y no se guarda en el perfil, historial o registros de seguimiento.

### RF-27 — Ruta opcional

Calcular ruta requiere destino y punto de partida confirmados. Debe informar que se enviarán esas coordenadas al proveedor de rutas. Presenta distancia, tiempo estimado y trazado cuando el servicio responda; no ofrece tráfico en tiempo real ni navegación giro a giro.

La ruta es orientativa y no determina que un aparato pueda trasladarse con seguridad. Si falla, se mantienen ficha, dirección, código existente y opción de abrir una aplicación externa de mapas. Calcular ruta nunca es requisito para generar, mostrar o confirmar una entrega.

### RF-28 — Referencia de destino del aparato

El titular puede asociar un lugar publicado como referencia de un pendiente. La asociación utiliza el identificador estable y no equivale a una entrega. Si el lugar se retira, la referencia histórica se conserva con aviso y no puede seleccionarse para una entrega nueva hasta estar habilitado.

Cambiar una referencia no cambia el receptor de una entrega ya creada. Para ello debe cancelarse la entrega y crear una nueva tras confirmar el destino.

### RF-60 — Mapa sin conflicto de gestos

La vista previa dentro de una pantalla desplazable no debe capturar accidentalmente el desplazamiento vertical. Abrir mapa muestra una vista dedicada, navegable con zoom y desplazamiento, controles accesibles y alternativa en lista.

Actualizar los lugares no recentra ni mueve la cámara arbitrariamente. Volver conserva selección y contexto. Cambiar destino elimina la ruta anterior para no mostrar un trazado hacia otro lugar. La atribución cartográfica debe permanecer visible y no quedar tapada por controles.

### RF-61 — Tres formas de elegir punto de partida

El panel «Elegir punto de partida» ofrece, en la misma vista, Partir desde mi ubicación, Elegir en el mapa y Escribir dirección. La elección manual requiere Confirmar punto de partida; Cancelar restaura el anterior.

La búsqueda de dirección admite 6–180 caracteres y se ejecuta al pulsar Buscar, no con cada tecla. Muestra hasta cinco coincidencias con dirección completa; no elige automáticamente la primera. Sin resultados o ante error, permite corregir o elegir manualmente. No se guarda historial de direcciones personales.

### RF-65 — Acciones de destino fáciles de localizar

La ficha y el panel de ruta no deben abrir pasos vacíos o pantallas repetitivas. Una vez confirmado el origen, Calcular ruta abre el mapa con el resultado o un error accionable. Durante una entrega activa, debe permanecer visible Mostrar QR y código.

Si el lugar todavía no puede recibir entregas mediante Noki, se explica en la propia ficha y se permite consultar otros lugares sin perder los aparatos preparados. No se debe usar «Elegir receptor con código» ni «Receptores con código» como etiquetas de navegación.

### RF-84 — Publicación automática tras aprobación

Aprobar una solicitud con datos públicos completos y coordenadas confirmadas debe publicar exactamente un punto vinculado a esa solicitud. Aprobar de nuevo o reintentar no crea duplicados. Suspender o retirar inhabilita nuevas entregas y actualiza la publicación, sin borrar las referencias históricas.

El cliente actualiza el directorio al entrar, al volver al primer plano y, mientras la vista esté visible, a intervalos de hasta 10 segundos, además de ofrecer actualización manual. Una cuenta no debe tener que cerrar sesión o instalar otra versión para ver un punto recién publicado. La actualización depende de conectividad; un dato en caché se identifica como tal.

No se colocan coordenadas aproximadas deducidas solo del nombre. El responsable u operador debe confirmar el punto. Cambios materiales de dirección o lugar requieren revisión antes de publicar los nuevos datos.

### RF-86 — Contexto continuo entre preparación, destino y código

Los identificadores de los aparatos preparados y de la entrega activa deben sobrevivir a la navegación al mapa, selección de origen y retorno. La ausencia de aparatos se representa como tal; nunca se sustituye silenciosamente por todos los de la cuenta.

Desde una entrega creada, Calcular ruta abre su destino preseleccionado. Mostrar QR y código vuelve a esa misma entrega, sin crear otra ni acumular pantallas. Inspeccionar otro lugar no cambia el destino de una entrega activa. Si la recepción se completa mientras se consulta el mapa, la acción pasa a Ver resultado.

### RF-87 — Todos los destinos visibles; generación desde la ficha

Mapa, lista y buscador utilizan el mismo conjunto de lugares públicos. La carga de permisos privados de recepción no debe vaciar el directorio. Los puntos del propio usuario también son visibles, aunque no pueda enviarse una entrega a sí mismo.

Después de preparar los aparatos, elegir un destino abre su ficha y habilita «Crear entrega y generar QR y código» cuando se cumplen las condiciones. Si falta preparación, contacto o elegibilidad, se explica exactamente qué falta y se ofrece resolverlo ahí o volver al paso pertinente. No se obliga a volver a buscar al receptor en otro catálogo.

El botón crea la entrega una sola vez y muestra inmediatamente el QR y el código recibidos del servidor. Calcular ruta se ofrece después como acción opcional. Un error permanece visible en el panel donde ocurrió, con reintento seguro y sin perder la selección.

## 10. Solicitud y rol de receptor

### RF-66 — Descubrir y comprender la recepción

Inicio y Perfil ofrecen «Quiero recibir aparatos». Una pieza informativa breve puede invitar a «Aprende a recibir aparatos», sin interrumpir tareas ni reproducirse indefinidamente. La información explica requisitos, responsabilidades, publicación del lugar, revisión y condiciones del servicio; no expone decisiones internas sobre costes de proveedores o tecnologías descartadas.

Antes de iniciar se presentan dos casillas independientes y desmarcadas: «He leído los requisitos y entiendo cómo funciona la recepción» y aceptación de las condiciones específicas y tratamiento de las fotografías del lugar. Solo entonces se habilita Continuar solicitud; enviar datos requiere sesión válida. No se presenta la aceptación como una garantía de aprobación.

### RF-67 — Solicitud remota con información del lugar

El recorrido se organiza en tres etapas: Responsable; Lugar y contacto; Fotografías y confirmación. Los grupos de datos de la tabla no requieren pantallas adicionales.

| Paso | Datos | Validación |
| --- | --- | --- |
| Responsable | Nombre completo, correo verificado de la cuenta, declaración de mayoría de edad y autorización para operar el lugar | Nombre de 5–120 caracteres; declaraciones explícitas. |
| Lugar | Nombre, dirección y ciudad, coordenadas confirmadas, horarios, categorías y condiciones | Nombre de 3–100; dirección y ciudad de 10–240 en su representación conjunta; horarios de 5–200; una o más categorías; condiciones de 10–500 caracteres. |
| Contacto público | WhatsApp que se autoriza publicar, cuando se proporciona | Número internacional de 10–15 dígitos, sin cero inicial; normalización y validación antes de publicar. No se copia automáticamente del perfil. Sin contacto se aplican las limitaciones de RF-85. |
| Fotografías | Fachada o entrada, área de recepción y área de almacenamiento | Tres imágenes reales, una por categoría, cargadas y vinculadas al borrador. |
| Revisión y envío | Resumen editable, condiciones específicas y autorización de publicación | Versión vigente y fecha registradas en servidor. |

La cuenta personal no debe exigir teléfono por este requisito; el contacto público pertenece al lugar receptor. Si una solicitud histórica no tiene contacto autorizado, la app informa la carencia y aplica RF-85 sin publicar el correo de inicio de sesión.

El receptor puede corregir su WhatsApp desde el perfil mediante una operación autenticada, con autorización explícita de publicación y auditoría de titular y fecha. Ese cambio no modifica su aprobación ni autoriza a cambiar materialmente el lugar sin revisión. La declaración de contacto de una entrega conserva el número vigente en el momento de aceptarla y es visible para ambas partes autorizadas.

No se solicitan fotos de cédula, reconocimiento facial, prueba de vida ni documentos de identidad. Las fotos del lugar no deben incluir personas identificables, documentos, placas u otra información ajena a la revisión. Antes de seleccionarlas se explica finalidad y acceso.

Se utiliza el selector limitado del sistema. Cada imagen se orienta, reduce y convierte a JPEG de hasta 3 MiB, sin metadatos EXIF de ubicación. El servidor debe comprobar formato, contenido decodificable y tamaño, no confiar solo en extensión o MIME declarado. Los objetos son privados, vinculados al titular y a su solicitud.

La aceptación específica se registra antes de cargar fotografías. Un fallo conserva el borrador y permite reintentar la imagen afectada. Enviar exige las tres imágenes existentes en almacenamiento; no basta con guardar sus nombres. Reemplazar imágenes solo está permitido en estados editables y no debe dejar objetos antiguos accesibles indefinidamente.

La revisión inicial es remota y manual. Se informa que podrán coordinarse visitas presenciales ocasionales para comprobar existencia y funcionamiento, por personal autorizado e identificado. Esto no afirma que cada punto ya haya sido visitado ni establece visitas frecuentes obligatorias.

### RF-68 — Revisión, decisiones y distintivo

Los estados son Borrador, En revisión, Requiere cambios, Aprobada, Suspendida y Retirada. El usuario consulta su estado, fecha y siguiente acción. Requiere cambios incluye motivos concretos y permite corregir y reenviar. Una denegación o suspensión ofrece un canal de revisión; la app no promete una resolución instantánea.

Solo un operador autorizado puede aprobar, solicitar cambios o suspender. Cada decisión registra actor, fecha, motivo y versión revisada. El cliente no puede aprobarse mediante campos de perfil, cambios locales, aceptación de condiciones o subida de fotos.

El distintivo debe denominarse «Perfil revisado» o equivalente inequívoco. Significa que Noki revisó la solicitud del lugar, no que certificó identidad civil, permisos ambientales, ausencia de fraude o recepción de cualquier aparato. La aprobación habilita el rol y la publicación conforme a RF-84.

### RF-69 — Una cuenta, dos contextos

Una cuenta aprobada conserva sus aparatos y puede alternar entre «Cambiar a receptor» y «Cambiar a mis aparatos». El contexto receptor incluye recepciones pendientes, escaneo o ingreso de código, detalle, confirmación e historial de sus recepciones. No da acceso al resto de aparatos o datos privados de otras personas.

El permiso se verifica en el servidor al consultar y actuar. Si se suspende, la app informa el estado y bloquea nuevas confirmaciones; no conserva privilegios por tener una pantalla abierta o una copia local de Aprobada. El historial autorizado permanece sujeto a la política de acceso y conservación.

## 11. Entregas, códigos y recepción

### RF-70 — Creación y cierre de entrega

**Precondiciones para crear:** sesión válida, correo verificado, consentimiento vigente, alias configurado, entre 1 y 20 aparatos distintos del titular, decisión Entregar, preparación completa, registros no archivados ni completados, ninguna otra entrega activa y un receptor aprobado distinto del emisor.

El servidor revalida todas las condiciones en una transacción. La creación usa un identificador idempotente, bloqueos y restricciones de unicidad para impedir duplicados desde dos pantallas o dispositivos. Guarda una instantánea de los aparatos, destino y condiciones relevantes. Una selección del cliente no sustituye la comprobación de propiedad.

Después de crear, la pantalla debe mostrar:

- Estado «Pendiente de recepción», lugar y aparatos incluidos.
- QR legible y código manual seleccionable cuando sea seguro copiarlo.
- Explicación: «Muestra este QR o código al receptor cuando entregues los aparatos. Solo el receptor elegido puede confirmar la recepción».
- Vigencia, Renovar código, Calcular ruta y Cancelar entrega.

El QR contiene identificador de entrega y secreto aleatorio de al menos 256 bits, sin nombres, correos, direcciones ni datos de los aparatos. El código manual tiene 10 caracteres hexadecimales, con 40 bits de entropía. Ambos vencen a los 10 minutos, se invalidan al renovar, cancelar o completar y solo pueden utilizarse para la entrega asociada. El servidor conserva hashes, no secretos recuperables; el cliente mantiene los secretos únicamente durante el contexto necesario para mostrarlos, sin registros ni cachés persistentes.

Si la entrega se creó pero la emisión del código falló, se recupera esa misma entrega y se permite emitir nuevamente; nunca se crea otra para resolverlo. Una respuesta incierta exige consultar por el identificador idempotente antes de reintentar.

El receptor aprobado y asignado puede escanear o escribir el código. La cámara se solicita solo al pulsar Escanear; denegar permiso mantiene la alternativa manual. Una lectura válida abre una vista previa con aparatos y destino. **Escanear no confirma recepción.** El receptor debe comprobar físicamente y pulsar «Confirmar recepción» con una confirmación explícita «Sí, los recibí».

El servidor exige receptor correcto, permiso vigente, entrega pendiente y validación temporal aún válida con credenciales no vencidas ni consumidas. Completa en una sola transacción, registra fecha, invalida códigos y actualiza los aparatos. Repetir la confirmación devuelve el resultado ya confirmado, sin duplicar constancias. No se pide confirmación adicional al emisor.

Mientras el recorrido esté visible y autenticado, se consulta el estado a intervalos de hasta 5 segundos y al volver al primer plano. Al completarse, se abre Resultado automáticamente una sola vez y se actualiza Historial. No se confunde esta consulta periódica con una garantía de entrega instantánea sin red.

**Cancelación:** solo el emisor puede cancelar una entrega pendiente, con confirmación y motivo de 5–300 caracteres. Se invalidan los códigos, se libera la preparación de los aparatos y se conserva el evento cancelado. Una recepción ya confirmada no se cancela con este botón. Ante confirmación y cancelación simultáneas, solo una transición puede ganar; la otra recibe el estado final real.

### RF-72 — Activación real, límites y fallos

La recepción no se habilita con datos simulados. Cada acción depende de sus precondiciones reales: aprobación, configuración, sesión y permisos. Una indisponibilidad de códigos no debe hacer desaparecer el directorio público.

Tras cinco intentos fallidos de código por receptor dentro de 15 minutos se limita temporalmente el acceso y se informa cuándo reintentar. El contador debe persistir incluso cuando la operación devuelve error; no puede revertirse junto con la transacción fallida. Antes de apertura masiva debe existir además limitación de abuso por origen y observabilidad sin guardar secretos.

Código incorrecto, vencido, renovado, ya consumido, receptor distinto y entrega cancelada tienen respuestas recuperables sin exponer datos a terceros. En un servicio indisponible se permite actualizar o reintentar sin fabricar una recepción local. No se envían automáticamente mensajes ni códigos a terceros por WhatsApp.

El detalle de una entrega permite reportar un problema de forma privada, con referencia de entrega y descripción. Si se ofrece un formulario, debe generar un caso real y confirmación identificable; si se utiliza contacto de soporte, debe indicarse que el envío ocurre fuera de Noki. Nunca se presenta un reporte no enviado como recibido.

### RF-85 — Contacto previo para aparatos que requieren atención

No se bloquea permanentemente una entrega por estado del aparato o discrepancia con las categorías publicadas. Si hay señales de atención, condición desconocida o categorías fuera de las anunciadas, se muestra el contacto público autorizado y una declaración desmarcada:

> Confirmé con el receptor que puede recibir todos estos aparatos en su estado actual.

La ficha debe ofrecer Abrir WhatsApp y mostrar el número como alternativa. Abrir WhatsApp no marca la casilla ni envía mensajes automáticamente. El usuario puede confirmar una conversación realizada por ese canal, pero Noki no afirma haberla observado.

Para habilitar la generación en estos casos se exige contacto público válido y declaración explícita. Cambiar receptor, lista de aparatos o datos relevantes reinicia la declaración. El servidor revalida la condición y registra fecha, número de contacto y contexto aceptado. Sin contacto disponible se explica la carencia y se ofrece otro lugar o reintentar tras su actualización, conservando la preparación.

El texto aclara que el receptor puede rechazar aparatos que no cumplan lo acordado y que la declaración no reserva capacidad ni certifica seguridad. No elimina las precauciones ni autoriza el traslado de un aparato en peligro inmediato.

### RF-81 — Confianza basada en hechos

Los mensajes de confianza se sitúan en la ficha, preparación y código cuando aporten contexto: «Noki revisó la solicitud de este receptor. Consulta sus horarios y condiciones y muestra el código solo al entregar». Deben corresponder a una revisión real y vigente.

No se deben repetir anuncios de confianza en cada paso ni utilizar términos absolutos de seguridad. En lugares de referencia se comunica la procedencia de la información, no una revisión de receptor que no existe.

## 12. Diseño de interacción y accesibilidad

### RF-54 — Adaptación de interfaz

La app Android es nativa y adaptable: navegación inferior en ventanas menores de 700 dp y lateral desde 700 dp cuando haya espacio. El contenido de lectura tiene ancho máximo de 640 dp; mapas o composiciones de varias columnas pueden ocupar más ancho manteniendo controles accesibles.

Debe funcionar a 320, 360, 393, 600 y 840 dp de ancho, con teclado visible y texto ampliado. Se respetan barras del sistema, recortes de pantalla y áreas seguras. Las tarjetas y avisos crecen con el contenido; no usan alturas rígidas que recorten texto. El diseño en tableta no consiste en estirar una columna de texto hasta llenar la pantalla.

### RF-58 — Menú de accesibilidad

Desde Inicio y Perfil se ofrece un menú desplegable accesible con apariencia del sistema, clara u oscura; texto normal, 115 % o 130 %; contraste reforzado; reducción de movimiento; y restablecimiento de las preferencias de Noki. Restablecer no cambia ajustes del sistema ni elimina datos.

El tamaño efectivo no debe reducir la preferencia del sistema: se aplica al menos el mayor de los factores elegidos. Se debe soportar ampliación del sistema al 200 % sin pérdida de acciones o información. Las preferencias persisten localmente y se aplican a todas las pantallas, diálogos y formularios.

### RF-59 — Identidad y respuesta visual

La tipografía principal es Poppins, distribuida con licencia y disponible sin conexión. Referencias de tamaño: títulos 28–32 sp, subtítulos 22 sp, cuerpo 18 sp, auxiliar 16 sp y texto no crítico 14 sp. La legibilidad prevalece sobre un tamaño fijo. Los pesos ligeros no se usan en instrucciones esenciales.

Márgenes de referencia: 24 dp y 16 dp en composiciones compactas; espaciado basado en 4, 8, 12, 16 y 24 dp; radios de tarjetas de 16–24 dp. Los títulos deben nombrar una tarea o estado, sin frases publicitarias vagas. No se justifica forzosamente el texto si genera huecos; se prioriza alineación de lectura y separación clara.

Todo botón responde visualmente a presión, foco y deshabilitación. Puede utilizar color y una variación sutil de escala; la activación ocurre al completar la pulsación, no en cada cuadro de animación. Durante una operación no se aceptan envíos duplicados. El área táctil mínima es 48 × 48 dp; el botón principal parte de 56 dp de alto y crece si el texto lo requiere.

Las transiciones entre vistas duran 260–380 ms, mantienen dirección coherente y no se acumulan. La animación inicial dura aproximadamente 1450 ms, utiliza el isotipo con nodos u órbitas discretas, no parpadea ni bloquea por solicitudes de red. No se repite al volver entre pantallas. Con movimiento reducido, se sustituye por presentación estática o transición inmediata.

### RF-62 — Apariencia clara y oscura

| Uso | Tema claro | Tema oscuro |
| --- | --- | --- |
| Fondo | `#F5F4F0` | `#1B2420` |
| Superficie principal | Blanco o superficie clara contrastada | `#242F29` |
| Texto principal | Tono oscuro con contraste suficiente | `#E7EDE5` |
| Acción principal | `#10661E` | `#9CDD9D` con texto oscuro |
| Secundario de marca | `#0F2167` | Variante contrastada, no azul oscuro sobre verde oscuro |
| Información y acentos | `#DCEADD`, `#AAAED1` | Variantes ajustadas y comprobadas por contraste |
| Bordes | `#E5E5E5` o tono más contrastado cuando comunique un límite esencial | Tono distinguible de la superficie |

La paleta es una referencia de marca, no una excepción al contraste exigido. Los logos cambian a su variante adecuada, sin deformación. El mapa mantiene colores y atribución del proveedor; no se invierte como una imagen para simular tema oscuro.

Cambiar apariencia interpola durante aproximadamente 650 ms sin reiniciar navegación, perder formularios ni emitir destellos de una pantalla completa. Con movimiento reducido el cambio es inmediato. La elección Sistema sigue la preferencia del dispositivo; una elección explícita se conserva.

### RF-63 — Texto, navegación y orientación

La app solicita orientación vertical en teléfonos. Si el sistema impone ventanas redimensionables o ignora el bloqueo en pantallas grandes, la interfaz debe adaptarse en lugar de recortar contenido. No se promete impedir toda rotación en cualquier sistema operativo.

«Mis aparatos» puede ocupar dos líneas centradas; las cuatro opciones conservan alineación y área táctil uniforme. Con ancho menor de 360 dp o texto ampliado desde 130 %, se permite una composición accesible de dos filas y dos columnas. No se parte una palabra ni se oculta el nombre de la sección.

Los avisos positivos usan verde y símbolo de confirmación; los errores o rechazos usan rojo y X, siempre con texto explicativo. El icono de cerrar, si existe, se distingue del icono de estado mediante posición y etiqueta accesible. El mismo error no aparece simultáneamente como dos bloques repetidos.

El acceso por correo utiliza un contenedor de desplazamiento estable. Al llegar al borde no se produce un ciclo de rebote, cambio automático de tamaño, recuperación repetida de foco ni reapertura de panel. El teclado no oculta campos, errores o la acción siguiente.

## 13. Privacidad, consentimiento y acceso a datos

### RF-46 — Política de privacidad accesible

La política es pública, legible sin sesión y disponible en su versión incluida sin conexión. Debe identificar a Jeffry Giovanny Caro Arango y Luis Miguel Castañeda como responsables declarados de Noki y el contacto `nokiappcorporation@gmail.com`, así como categorías de datos, finalidades, destinatarios técnicos, derechos, conservación y procedimientos de atención.

Debe separar cuenta personal, ubicación temporal, solicitudes de receptor, fotografías privadas, datos públicos del lugar y constancias de entrega. La publicación de un contacto de soporte no autoriza a publicar correos de acceso de los usuarios. Las condiciones de proveedores, transferencias y obligaciones aplicables deben revisarse antes de publicar el servicio; este SPEC no sustituye esa revisión ni certifica cumplimiento jurídico.

### RF-47 — Términos claros y alcance del servicio

Los términos explican orientación no diagnóstica, datos de referencia, responsabilidad de las declaraciones, preparación, contacto previo, cancelación, confirmación del receptor, reportes y límites de disponibilidad. No prometen ausencia de fraude ni aceptación garantizada.

Rechazar términos no significa aceptar una exoneración general «bajo su propia responsabilidad». La consecuencia es no habilitar las funciones privadas sujetas a aceptación, conservando consultas públicas. Los términos no deben atribuir al usuario toda responsabilidad por fallos propios del servicio ni sustituir controles de seguridad.

### RF-48 — Registro versionado de aceptación

Se requieren dos casillas independientes, inicialmente desmarcadas: aceptación de términos y autorización del tratamiento descrito en privacidad. El servidor registra identidad autenticada, versión vigente y fecha propia; no acepta identidad o fecha arbitrarias del cliente. Repetir la aceptación de la misma versión es idempotente.

Una sesión con aceptación antigua recibe los cambios pertinentes y debe aceptar la versión requerida antes de nuevas operaciones privadas. La configuración pública informa la versión vigente; una copia local no sustituye la comprobación del servidor. Leer un documento y volver conserva el formulario, pero no marca casillas automáticamente.

### RF-64 — Aceptar, rechazar o retirar

Aceptar habilita funciones privadas solo si también se cumplen las demás condiciones de acceso. Rechazar vuelve al contexto público sin perder innecesariamente las respuestas de orientación temporal.

Retirar el consentimiento cierra el contexto privado local inmediatamente y solicita registrar la revocación en el servicio. Si no hay conexión se informa de lo pendiente y se impiden nuevas escrituras; no se afirma que el servidor ya registró la solicitud. Retirar consentimiento no equivale a eliminar todos los datos. Se ofrece el procedimiento de supresión y se explican las conservaciones justificadas.

### RF-71 — Minimización, separación y permisos

No se recopilan cédulas, fotografías de documentos, biometría, datos bancarios, contactos del teléfono ni seguimiento de ubicación. No se requieren número de serie o fotografías del aparato para la orientación ordinaria. Las coordenadas públicas de un lugar receptor son datos de publicación autorizada, distintos de la ubicación temporal del visitante.

Los datos se separan en públicos, privados del titular y compartidos estrictamente para una entrega. El operador solo accede a lo necesario para una revisión, soporte o seguridad autorizados; no se concede acceso general a toda información por trabajar en Noki. Toda decisión administrativa sensible debe tener trazabilidad.

Los roles y aprobaciones proceden de controles del servidor, nunca de metadatos de usuario editables. Las operaciones críticas verifican sesión activa, identidad, consentimiento, propiedad y estado. Los tokens se protegen mediante almacenamiento seguro del sistema; no se incluyen en copias de seguridad, diagnósticos o mensajes.

Las fotos de perfil y del lugar usan almacenamiento privado y políticas por propietario y revisor autorizado. No se publican enlaces permanentes libres. Solo se publica la información aprobada del lugar y los medios de contacto expresamente autorizados. Un cambio de cuenta descarta respuestas tardías, imágenes y datos temporales de la anterior.

La política de conservación debe definir, por categoría, finalidad, responsable, plazo o criterio concreto, evento de inicio, excepciones justificadas y procedimiento de eliminación de filas, objetos y copias. No se permite conservación indefinida por omisión. Los plazos deben quedar aprobados y comunicados antes de recoger los datos afectados; no se inventan en una implementación ni se presentan propuestas internas como condiciones ya aceptadas.

## 14. Instalación, conectividad y actualizaciones

### RF-49 — Identidad de aplicación e instalación

El cliente Android utiliza la identidad `co.noki.app`, icono y nombre Noki. La distribución debe estar firmada y mantener la identidad y cadena de firma autorizadas para actualizaciones compatibles. No se exige una segunda app de demostración para utilizar funciones reales.

Instalar una actualización compatible debe conservar cuenta y datos según las garantías del almacenamiento utilizado. No se debe resolver una actualización solicitando borrar datos, desinstalar sin explicación o desactivar protecciones del sistema.

### RF-50 — Funciones públicas sin conexión

Inicio, orientación, Aprende, fuentes esenciales, logos, tipografía y documentos incluidos deben poder consultarse sin red. El directorio puede mostrar datos incluidos o una caché confirmada, con fecha y aviso de falta de actualización. No se promete cartografía ni rutas completas sin conexión.

Los enlaces externos y servicios de dirección o rutas informan que necesitan conexión. Los datos en caché no se presentan como disponibilidad confirmada de recepción.

### RF-51 — Escrituras y borradores sin simulación

Registro, autenticación, aprobación, guardado remoto, creación y confirmación de entrega requieren respuesta válida del servicio. Sin red no se fabrican identidades, permisos, códigos o constancias. Una sesión temporalmente sin conexión no se trata automáticamente como contraseña incorrecta.

Cuando se conserven borradores locales privados, deben estar cifrados, separados por cuenta, excluidos de copias del sistema y limitados a siete días. Se eliminan tras éxito, cierre de sesión o vencimiento. Recuperarlos requiere la cuenta correspondiente y una acción comprensible; no se sincronizan automáticamente datos obsoletos sobre cambios remotos.

### RF-52 — Actualización de aplicación y contenido

Una instalación mediante APK no se actualiza sola por tener sesión. Una nueva versión de la app requiere el mecanismo de distribución configurado y la autorización del sistema. Las actualizaciones de lugares o estados en el servidor no sustituyen la actualización del código instalado.

Si se ofrece Buscar actualización, debe consultar un canal HTTPS real con versión, compatibilidad mínima y enlace autorizado; para descarga directa se comprueba además integridad mediante hash publicado por un canal confiable y firma de aplicación. Debe indicar al usuario qué ocurrirá y conservar la app actual si falla. No se muestra un botón ficticio ni se anuncia actualización automática sin mecanismo operativo.

La distribución por tienda es una ampliación de canal y se rige por el sistema de actualización de esa tienda. No forma parte de este requisito crear una tienda o publicar una versión sin autorización.

### RF-53 — Separación del producto y las pruebas

Los usuarios reales no deben recibir textos de depuración, interruptores de permisos simulados o datos de demostración presentados como reales. Las pruebas automatizadas utilizan entornos y datos aislados; no alteran cuentas existentes ni aprueban receptores reales para satisfacer una prueba.

Resultados de pruebas, instalaciones y comprobaciones de equipos pertenecen a documentación de calidad separada. Esta separación no reduce los requisitos de validación de la app distribuida.

## 15. Reglas de negocio y máquinas de estados

### 15.1 Reglas transversales

| ID | Regla normativa |
| --- | --- |
| RN-01 | Una identidad autenticada corresponde a un único perfil Noki; recuperar acceso no crea una cuenta nueva. |
| RN-02 | El sistema de autenticación es la fuente de verdad del correo y su verificación; las copias de perfil no lo sustituyen. |
| RN-03 | Las funciones privadas requieren correo verificado y sesión válida. |
| RN-04 | Una cuenta utilizable requiere aceptación vigente de términos y privacidad; la consulta pública no. |
| RN-05 | El usuario solo modifica sus propios datos; los permisos de receptor y operador tienen alcance explícito y limitado. |
| RN-06 | La eliminación de cuenta revoca acceso y aplica supresión o anonimización conforme a una política comunicada; una solicitud pendiente no equivale a eliminación ejecutada. |
| RN-07 | La orientación no diagnostica ni certifica seguridad, reparabilidad o reciclabilidad. |
| RN-08 | La condición de atención o desconocida prevalece sobre preguntas de funcionamiento. |
| RN-09 | La orientación pública no se guarda automáticamente como registro de cuenta. |
| RN-10 | Guardar orientación exige sesión y una operación idempotente sobre un aparato propio. |
| RN-11 | Publicar un lugar requiere nombre, dirección, coordenadas confirmadas, procedencia y fecha de revisión. |
| RN-12 | Publicación no equivale a alianza, convenio ni certificación de identidad. |
| RN-13 | Seleccionar o guardar referencia de lugar no reserva, agenda ni notifica una visita. |
| RN-14 | La aceptación y disponibilidad deben confirmarse con el lugar; los supuestos especiales exigen RF-85. |
| RN-15 | La ubicación temporal del usuario no se persiste; las coordenadas públicas autorizadas de un lugar sí forman parte del directorio. |
| RN-16 | Cada aparato pertenece a una sola cuenta. |
| RN-17 | El ciclo de un aparato distingue pendiente, resultado personal, entrega activa, recepción confirmada y retirado; no se reducen todos a una etiqueta indistinta. |
| RN-18 | Decisión expresa intención; resultado personal expresa una declaración; constancia de recepción expresa una confirmación del receptor. |
| RN-19 | Entregado exige confirmación válida del receptor asignado; no certifica reciclaje o tratamiento posterior. |
| RN-20 | Una referencia de lugar es opcional y editable en un pendiente libre; no cambia el destinatario de una entrega activa. |
| RN-21 | Cada aspecto de preparación admite No aplica; no obliga a una acción imposible o peligrosa. |
| RN-22 | Ante señales de atención no se instruye encender, cargar ni desmontar el aparato. |
| RN-23 | Retirar un pendiente es archivo funcional; supresión de datos es un procedimiento distinto y no existe conservación perpetua por defecto. |
| RN-24 | No se deducen peso, material recuperado o beneficio ambiental del número de aparatos. |

También son invariantes obligatorias: un máximo de una entrega activa por emisor; 1–20 aparatos por entrega; receptor diferente del emisor; QR y código no reutilizables; aprobación nunca autoconcedida; alias único con cambio cada 30 días; y ninguna escritura presentada como éxito antes de ser confirmada.

### 15.2 Estado de acceso

| Estado | Puede hacer | Salida válida |
| --- | --- | --- |
| Visitante | Consultar funciones públicas y orientar | Solicitar registro o iniciar sesión. |
| Código pendiente | Introducir código, cambiar correo del formulario o reenviar con límite | Validación del servicio; cancelar vuelve a público. |
| Identidad verificada, perfil incompleto | Completar alias y aceptaciones requeridas | Cuenta habilitada tras confirmación de servidor. |
| Sesión habilitada | Funciones privadas según propiedad y rol | Cierre voluntario, revocación o vencimiento no recuperable. |
| Sesión sin conexión | Ver contexto permitido y conservar borradores protegidos | Revalidar al recuperar conexión; no inventar operaciones privadas. |
| Consentimiento desactualizado | Consultar información y decidir sobre nueva versión | Aceptar o volver al contexto público. |

No se debe interpretar una demora de perfil, foto o listado como fallo de autenticación si el acceso ya fue confirmado. Primero se conserva la sesión; las dependencias secundarias muestran su propio estado recuperable.

### 15.3 Ciclo del aparato

| Estado conceptual | Entrada | Acciones permitidas | Salida |
| --- | --- | --- | --- |
| Pendiente | Guardado de orientación o alta manual | Editar, decidir, preparar, retirar | Resultado personal, entrega activa o retirado. |
| Preparado para entrega | Decisión Entregar y cinco aspectos resueltos | Elegir destino, contacto cuando aplique | Entrega activa confirmada por servidor. |
| En entrega | Incluido en entrega pendiente | Ver código, ruta, cancelar entrega | Entregado o pendiente tras cancelación. |
| Resultado personal | Confirmación explícita de conservación, reparación u otro uso | Consultar y corregir declaración | Resultado personal corregido; no recepción fabricada. |
| Entregado | Confirmación del receptor | Consultar constancia y reportar problema | Sin reapertura ordinaria desde la app. |
| Retirado | Archivo confirmado de pendiente libre | Consultar evento y ejercer derechos de datos | No se trata como entrega. |

Los estados conceptuales pueden derivarse de varios campos de persistencia. La interfaz no debe permitir combinaciones imposibles, como archivado y participante de una nueva entrega activa, o Entregado sin constancia.

### 15.4 Ciclo de entrega

| Estado | Transición permitida | Actor y condición |
| --- | --- | --- |
| Preparación local | Crear → Pendiente | Emisor habilitado y todas las precondiciones revalidadas. |
| Pendiente | Renovar credenciales → Pendiente | Emisor propietario; invalida las anteriores. |
| Pendiente | Validar código → Pendiente con vista previa temporal | Receptor asignado y habilitado; no representa recepción. |
| Pendiente | Confirmar → Completada | Receptor asignado tras verificación válida y recepción física declarada. |
| Pendiente | Cancelar → Cancelada | Emisor propietario; gana atómicamente frente a confirmación concurrente. |
| Completada | Consultar / reportar | Partes autorizadas; no hay segunda confirmación del emisor. |
| Cancelada | Consultar | Sin códigos válidos; aparatos libres para una nueva entrega. |

Compatibilidad de datos: si existe un estado heredado `received` que esperaba cierre del emisor, no debe generarse en entregas nuevas. Mientras se resuelve por su transición de compatibilidad autorizada, sigue contando como entrega activa. No se reescriben registros históricos ni se liberan aparatos automáticamente por desconocer ese estado.

### 15.5 Ciclo de solicitud de receptor

| Estado | Quién puede actuar | Transiciones |
| --- | --- | --- |
| Borrador | Titular | Editar, retirar o enviar completo a revisión. |
| En revisión | Operador decide; titular consulta o solicita retiro | Requiere cambios, Aprobada o Retirada. |
| Requiere cambios | Titular | Corregir y reenviar, o retirar. |
| Aprobada | Operador y titular dentro de permisos distintos | Suspender, retirar o presentar modificación material a revisión. |
| Suspendida | Operador revisa; titular consulta y aporta correcciones | Restablecer tras revisión o retirar. |
| Retirada | Consulta histórica conforme a conservación | No otorga permisos ni publicación activa. |

Una aprobación requiere todos los datos y evidencias exigidos. Publicación y autorización deben corresponder a la misma versión revisada. No se admiten periodos en que una solicitud suspendida siga habilitando nuevas recepciones por una copia local.

## 16. Modelo de información y contratos

### 16.1 Principios del modelo

Los nombres siguientes describen entidades y responsabilidades; no obligan a renombrar tablas existentes. Los adaptadores deben mantener una correspondencia documentada con estos conceptos. Identificadores estables, fechas del servidor y relaciones verificables prevalecen sobre nombres visibles.

No deben registrarse campos privados «por si acaso». Campos opcionales ausentes se representan como ausencia, no datos inventados. Los textos introducidos se muestran como texto seguro. Fechas se almacenan con zona inequívoca y se presentan de manera comprensible; el territorio inicial utiliza hora de Bogotá cuando corresponde.

### 16.2 Entidades mínimas

| Entidad | Información esencial | Visibilidad y restricciones |
| --- | --- | --- |
| Identidad | ID, correo, verificación, métodos de acceso y estado de sesión | Administrada por Auth; contraseñas nunca pertenecen al perfil de la app. |
| Perfil | ID de usuario, alias, fecha de cambio de alias, nombre opcional, referencia privada de foto, actualización | Solo titular y operaciones autorizadas; alias único sin distinguir mayúsculas. |
| Consentimiento | Usuario, versión de términos y privacidad, aceptación y revocación | Propiedad derivada de sesión; fechas del servidor; no aceptación retroactiva fabricada. |
| Configuración pública | Versión legal vigente, disponibilidad real de métodos de acceso y servicios publicados | Lectura pública limitada; no contiene secretos ni otorga roles. |
| Aparato | ID, titular, descripción, marca/modelo opcionales, categoría, respuestas, recomendación derivada, decisión, preparación, referencia de lugar, versión, fechas, archivo y resultado | Acceso del titular; la copia compartida se limita a una entrega. |
| Lugar público | ID, nombre, dirección, ciudad, coordenadas, fuente/revisión, categorías, condiciones, horarios/contacto autorizados, estado de publicación y vínculo de solicitud | Lectura pública de activos; no incluye datos privados del responsable o fotografías privadas. |
| Solicitud de receptor | ID, titular, responsable, datos propuestos del lugar, estado, consentimientos específicos, versión y fechas | Titular y revisión autorizada; el titular no asigna aprobación. |
| Foto de solicitud | Solicitud, propietario, categoría de imagen, objeto privado, tamaño y fecha | Tres categorías obligatorias; acceso restringido; eliminación coordinada de objeto y metadatos. |
| Revisión | Solicitud y versión revisada, operador, decisión, motivo y fecha | Auditoría autorizada; no editable por solicitante. |
| Entrega | ID, emisor, receptor/solicitud/lugar, estado, idempotencia, fechas, motivo de cancelación e instantánea de condiciones | Partes asignadas y soporte autorizado; una activa por emisor. |
| Ítem de entrega | Entrega, aparato, versión e instantánea de datos necesarios | 1–20 únicos, propiedad revalidada, sin duplicación simultánea en entregas activas. |
| Contacto confirmado | Entrega o intención idempotente, contacto autorizado, contexto y fecha de declaración | Evidencia de declaración del emisor, no de conversación comprobada. |
| Credencial de entrega | Hashes, vigencia, invalidación y consumo | Solo servicio; no almacenar el secreto en claro. |
| Intento y validación temporal | Receptor, ventana de intentos, contador y referencia de vista previa válida | Controles de seguridad persistentes; no exponer a otras cuentas. |
| Constancia | Entrega completada, receptor confirmante, instante e ítems | No equivale a certificado ambiental; no modificable como resultado personal. |
| Caso de soporte | Referencia, solicitante, descripción, estado y eventos de atención | Privado; solo si el canal está habilitado realmente. |

La app no necesita copiar todos los datos a una «base de la web». App y futura administración deben operar sobre contratos comunes y permisos diferenciados. Una confirmación administrativa válida modifica la fuente autorizada; los clientes consultan ese cambio según su política de actualización.

Los registros históricos procedentes de otros clientes no se fusionan, sobrescriben ni reclasifican automáticamente. Cualquier integración requiere correspondencia de identidad, estados y permisos, y una migración explícita compatible con los datos existentes.

### 16.3 Contratos mínimos de operación

| Operación | Entrada relevante | Verificaciones del servidor | Resultado requerido |
| --- | --- | --- | --- |
| Autenticar / verificar código | Credencial correspondiente, sin enviarla a registros | Validez, límites y estado de cuenta | Sesión auténtica o error clasificado. |
| Aceptar versión legal | Versiones requeridas y acción del titular | Identidad, versión vigente y dos aceptaciones | Registro idempotente con fecha propia. |
| Guardar perfil | Campos permitidos y versión cuando aplique | Propiedad, alias único y plazo de 30 días | Perfil confirmado; conflicto sin sobrescritura. |
| Guardar/editar/archivar aparato | ID idempotente o versión esperada, campos permitidos | Propiedad, validación, derivaciones y bloqueo de entrega | Registro confirmado o conflicto recuperable. |
| Enviar solicitud | Borrador y versión, referencias de tres fotos | Consentimiento, contenido completo, objetos reales y propiedad | Estado En revisión sin aprobación automática. |
| Decidir solicitud | ID, versión, decisión y motivo | Rol administrativo y coherencia de datos | Revisión auditada y publicación consistente cuando corresponda. |
| Listar lugares | Filtros explícitos | Solo proyección pública autorizada | Activos con fuentes y fechas, independiente de sesión privada. |
| Crear entrega | ID idempotente, ítems, destino y declaración especial cuando aplique | Todas las condiciones de RF-70 y RF-85, atómicamente | Entrega pendiente única. |
| Emitir/renovar código | ID de entrega | Emisor, estado y sesión | Par QR/código vigente; invalidación del anterior. |
| Verificar código | Credencial y receptor autenticado | Destino, rol, vigencia, consumo y límites | Vista previa temporal, no recepción. |
| Confirmar recepción | Entrega y validación temporal | Receptor, estado actual y vigencia | Completada, constancia e invalidación en una transacción. |
| Cancelar | Entrega y motivo | Emisor y estado actual | Cancelada e ítems liberados, o estado ganador de una operación concurrente. |
| Consultar estado | ID de entidad | Permiso de lectura | Estado real y versión; sin acceso por conocer un ID ajeno. |
| Solicitar supresión / ejecutar eliminación | Petición del titular y comprobación reforzada cuando proceda | Identidad, política, relaciones y permisos mínimos | Seguimiento identificable; éxito final solo tras ejecución confirmada. |

### 16.4 Concurrencia e idempotencia

Las altas usan un identificador estable por intención; un reintento conserva ese identificador. Una nueva intención explícita obtiene otro. Ediciones usan versión esperada o control equivalente de concurrencia. La app no reintenta automáticamente una escritura destructiva con datos más recientes sin revisión del usuario.

Una operación de entrega debe verificar y modificar sus relaciones dentro de la misma transacción. Los estados temporales de interfaz no son fuente de verdad. Cada respuesta se vincula a la sesión y petición que la originaron; al cambiar de cuenta se descartan resultados tardíos.

## 17. Arquitectura e integraciones

### 17.1 Responsabilidades

| Capa | Responsabilidad | No debe hacer |
| --- | --- | --- |
| Interfaz Android nativa | Jetpack Compose, navegación, accesibilidad y presentación de estados | Aprobar roles, inventar confirmaciones o almacenar secretos de servicio. |
| Estado de presentación | Conservar contexto, coordinar solicitudes y evitar repeticiones | Convertir fallos en éxito o mezclar sesiones. |
| Dominio compartido Kotlin Multiplatform | Reglas puras de orientación, preparación y modelos portables | Depender de una pantalla concreta para aplicar reglas. |
| Repositorios y adaptadores | Traducir contratos, errores y almacenamiento local protegido | Saltarse comprobaciones remotas usando datos de demostración. |
| Servicio autorizado | Identidad, autorización, reglas críticas, transacciones y auditoría | Confiar en propietario, rol o fecha enviados libremente por el cliente. |

La base técnica es Kotlin con interfaz Android nativa y núcleo compartido, no Flutter. El núcleo puede reutilizarse en futuras plataformas, pero sus interfaces, permisos y distribución requieren implementación y aceptación propias.

### 17.2 Servicios externos

| Servicio | Uso | Tratamiento ante fallo |
| --- | --- | --- |
| Supabase Auth | Identidad, correo verificado, contraseña, códigos y sesiones | Error comprensible; recuperación segura; no cuenta local alternativa. |
| Postgres y políticas de acceso | Datos propios, estados, directorio y transacciones | Consultas acotadas por permisos; reintento idempotente y conflictos explícitos. |
| Storage privado | Fotos de perfil y de solicitud | Cargas controladas, acceso limitado y limpieza de objetos. |
| Correo SMTP configurado en servidor | Códigos y mensajes de acceso | Límite de reenvío, seguimiento operativo y mensajes sin revelar existencia de cuenta. |
| MapLibre Native y cartografía OpenStreetMap | Visualización y atribución | Lista y dirección siguen disponibles; cumplir atribución y política del proveedor de mapas. |
| Geocodificación del sistema | Búsqueda voluntaria de punto de partida | Elegir punto manualmente si no responde o no hay coincidencias. |
| OSRM o servicio de rutas configurado | Trayecto estimado tras acción explícita | Mantener destino y código; ofrecer mapas externos. |
| Aplicaciones externas | WhatsApp para contacto y mapas para indicaciones | Comprobar disponibilidad y ofrecer número/dirección; no enviar automáticamente. |

Los correos usan plantillas con variables dinámicas de código, no credenciales fijas. La contraseña de aplicación del correo solo se configura en el servicio de envío. No se integra en el APK, SPEC, repositorio, archivos públicos o capturas de soporte.

### 17.3 Seguridad del servicio

Las políticas de acceso por fila deben cubrir datos y relaciones; los objetos privados requieren políticas equivalentes. Las funciones con privilegios elevados deben limitar operaciones, fijar su contexto de ejecución y verificar explícitamente identidad y autorización. Una clave pública de cliente no sustituye esos controles; una clave administrativa nunca se distribuye en la app.

La administración de revisiones requiere autenticación reforzada, permisos nominativos y registro de consultas de fotografías, además de auditoría de decisiones. Un rol de operador no concede permiso permanente e indiscriminado sobre todos los archivos privados.

Las operaciones críticas deben reconocer revocación y estado activo de sesión. Eliminar un usuario o cerrar la interfaz no basta si una credencial previamente emitida aún puede acceder al servicio. Debe probarse la denegación efectiva tras revocación según el mecanismo de sesión aplicado.

### 17.4 Ciclo de vida y sincronización

Las consultas periódicas se ejecutan solo mientras la pantalla pertinente esté visible y la sesión lo permita. Al salir o pasar a segundo plano se detienen; al regresar se consulta inmediatamente. No deben solaparse consultas idénticas ni competir múltiples renovaciones de sesión.

Las escrituras confirmadas actualizan el estado local; los cambios externos se incorporan mediante consulta y versión. Un cambio de selección no debe provocar una cadena de navegación automática sin fin. El futuro panel administrativo debe cumplir los mismos estados y contratos, no mantener una aprobación paralela incompatible.

## 18. Requisitos no funcionales

| ID | Requisito verificable |
| --- | --- |
| RNF-01 | Tráfico de producción por HTTPS; no aceptar certificados inválidos ni degradar a conexiones inseguras. |
| RNF-02 | Sin contraseñas, códigos de acceso, tokens privados o claves administrativas en cliente distribuido, repositorio o registros. |
| RNF-03 | El aislamiento de datos impide leer, modificar o eliminar registros y objetos ajenos, incluidas relaciones indirectas. |
| RNF-04 | Propietario y actor de operaciones se obtienen de sesión validada, no de campos editables enviados por el cliente. |
| RNF-05 | Eliminación y demás operaciones sensibles se ejecutan con permisos mínimos en servidor y dejan un resultado comprobable. |
| RNF-06 | Texto de referencia 18/16/14 sp según jerarquía; al 200 % se conserva contenido y todas las acciones. |
| RNF-07 | Objetivos táctiles mínimos de 48 × 48 dp; acciones principales desde 56 dp de alto y crecimiento con texto. |
| RNF-08 | Color nunca es el único indicador; texto normal con contraste mínimo 4,5:1, texto grande y componentes esenciales 3:1, como objetivo WCAG 2.2 AA. |
| RNF-09 | Funciones esenciales operables mediante teclado; alternativa textual completa a selección gráfica del mapa. |
| RNF-10 | Semántica accesible para títulos, etiquetas, selección, errores y acciones; orden de foco coherente y anuncios relevantes no interruptivos. |
| RNF-11 | Sin recorte ni desplazamiento horizontal involuntario a 320/360/393/600/840 dp, con teclado y áreas seguras; adaptación si el sistema impone otra orientación. |
| RNF-12 | Transiciones ordinarias de 260–380 ms; tema alrededor de 650 ms y apertura alrededor de 1450 ms; respetar reducción/desactivación de animación, sin destellos. |
| RNF-13 | Escrituras con estado de carga, acción única y protección frente a doble pulsación. |
| RNF-14 | Una respuesta perdida exige reconciliación o reintento seguro; no se comunica éxito por suposición. |
| RNF-15 | Errores técnicos traducidos a causa comprensible y siguiente acción, sin secretos ni trazas internas. |
| RNF-16 | Toda ficha identifica revisión; más de 90 días activa aviso de revisión pendiente, sin inventar cierre del lugar. |
| RNF-17 | Ubicación personal solo por acción voluntaria, sin seguimiento en segundo plano ni persistencia; no confundir con coordenadas públicas autorizadas del lugar. |
| RNF-18 | Ningún logotipo, distintivo o texto sugiere convenio o verificación no autorizados. |
| RNF-19 | Textos del usuario se tratan como datos; no se ejecutan como HTML, instrucciones o enlaces arbitrarios privilegiados. |
| RNF-20 | Tokens y borradores privados cifrados con protección del sistema y excluidos de copias de seguridad; no guardar contraseñas ni secretos QR en persistencia local. |
| RNF-21 | Cerrar sesión limpia contexto privado temporal, imágenes y borradores de esa cuenta, incluso si la revocación remota falla. |
| RNF-22 | Matriz de aceptación Android API 26, 30, 35 y 36, con pruebas automatizadas y comprobación física representativa; funciones públicas sin dependencia obligatoria de Google Play Services. |
| RNF-23 | Fallos de mapa, geocodificación o rutas conservan lista, dirección y acciones independientes, incluido código de una entrega activa. |
| RNF-24 | Ninguna cifra de capacidad, usuarios, impacto o disponibilidad sin medición y fuente. |
| RNF-25 | Español claro, frases directas y terminología uniforme: aparato, punto de partida, lugar de entrega, receptor y resultado. |
| RNF-26 | Espera máxima por intento: operaciones autenticadas 20 s, ubicación 12 s, ruta 15 s y búsqueda de dirección 20 s; después, estado recuperable y nunca carga infinita. |

Los tiempos son límites de interfaz por intento, no garantía de disponibilidad del proveedor. Un vencimiento de espera en una escritura no prueba que el servidor no la ejecutó; se aplica reconciliación antes de repetir.

## 19. Historias de usuario y criterios de aceptación

Cada fila expresa un objetivo del actor y sus criterios mínimos observables. Se conservan los identificadores de historia para trazabilidad; los requisitos RF y RNF completan sus precondiciones, límites y casos negativos. Estas tablas definen qué comprobar, no resultados de pruebas ya realizadas.

### 19.1 Inicio y acceso

| Historia | Objetivo | Criterios de aceptación | Requisitos |
| --- | --- | --- | --- |
| HU-HOME-01 | Como visitante, comprender para qué sirve Noki | Al abrir, se explica orientación, lugares y preparación; se puede explorar sin registro; no se promete recogida. | RF-01, RF-57, RF-75 |
| HU-HOME-02 | Como usuario, retomar mi actividad | Se muestra alias y pendiente/entrega propia; el resumen no supera tres aparatos; un error de resumen no bloquea Inicio. | RF-02, RF-79 |
| HU-HOME-03 | Como usuario recurrente, recordar el propósito | La explicación breve sigue presente después de la bienvenida, sin obligar a repetirla. | RF-75 |
| HU-AUTH-01 | Crear una cuenta con mi correo | Formato válido, dos aceptaciones explícitas, código real y alias obligatorio; no se requiere nombre civil ni teléfono; no hay éxito antes de verificar. | RF-04, RF-48, RF-78 |
| HU-AUTH-02 | Verificar que controlo el correo | Un código válido confirma acceso; incorrecto, vencido o usado no habilita datos; pegar y cambiar destinatario funcionan sin conservar el código anterior. | RF-05 |
| HU-AUTH-03 | Solicitar otro código | Espera visible de al menos 60 s y límites del servidor; sin solicitudes paralelas ni revelación de existencia de cuenta. | RF-06 |
| HU-AUTH-04 | Entrar con mi contraseña | Credenciales válidas abren la misma cuenta sin código obligatorio; fallo de red no se etiqueta como contraseña incorrecta; no se aplican mínimos nuevos a una contraseña anterior. | RF-07, RF-11, RF-82 |
| HU-AUTH-05 | Recuperar acceso olvidado | La solicitud ofrece código y mensaje neutral; no modifica otra sesión ni crea otra identidad para la cuenta existente. | RF-09 |
| HU-AUTH-06 | Establecer nueva contraseña | Se exige verificación autorizada, coincidencia y límites de creación; solo respuesta confirmada comunica cambio. | RF-10 |
| HU-AUTH-07 | Cerrar sesión | Se vuelve a Inicio público y desaparecen datos temporales; Atrás no recupera una pantalla privada utilizable; fallo remoto no mantiene acceso local. | RF-12 |
| HU-AUTH-08 | Entrar con código como alternativa | Se usa el correo real, se verifica en servidor y se conserva identidad; no se solicita contraseña de Gmail ni se confunde con acceso social. | RF-05, RF-56 |
| HU-AUTH-09 | Saber que entré y continuar | Confirmación accesible y temporal seguida de Inicio; el contexto pendiente puede retomarse sin repetir orientación ni duplicar registros. | RF-73, RF-79 |

### 19.2 Perfil y privacidad

| Historia | Objetivo | Criterios de aceptación | Requisitos |
| --- | --- | --- | --- |
| HU-PRO-01 | Consultar mi cuenta | Se muestran solo mis datos y estado real; un fallo ofrece actualizar, no datos de ejemplo. | RF-41 |
| HU-PRO-02 | Cambiar mi nombre personal | Nombre opcional validado; cancelar conserva anterior; guardar no cambia alias ni rol y requiere confirmación. | RF-42 |
| HU-PRO-03 | Cambiar correo conservando mi cuenta | La operación verifica identidad y nueva dirección; antes de confirmación sigue vigente el correo anterior; cambiar el formulario OTP no se presenta como cambio de cuenta. | RF-43 |
| HU-PRO-04 | Crear o cambiar contraseña desde Perfil | Se respeta reautenticación y validación; un error no muestra éxito; una cuenta con código puede definir contraseña. | RF-44 |
| HU-PRO-05 | Solicitar eliminación de mi cuenta | Procedimiento accesible, consecuencias y confirmación explícitas; solicitud y ejecución se distinguen; incluye datos y objetos según política. | RF-45 |
| HU-PRO-06 | Poner o quitar mi foto | Selector limitado, carga solo al confirmar, privacidad y eliminación del objeto al quitar; no se borra el original de la galería. | RF-76 |
| HU-PRO-07 | Controlar consentimiento | Documentos públicos y casillas independientes; rechazar conserva acceso público; retirar no se confunde con supresión completada. | RF-46, RF-48, RF-64 |
| HU-PRO-08 | Elegir mi alias y cambiarlo cuando corresponda | Único sin distinguir mayúsculas, 3–24 caracteres permitidos; servidor aplica 30 días y muestra próxima fecha; cuenta antigua conserva sus registros. | RF-78 |
| HU-PRO-09 | Encuadrar manualmente mi foto | Arrastre y controles alternativos, zoom 1×–4× y opción de foto completa; vista previa coincide con salida; cancelar no cambia nada. | RF-76 |
| HU-LEGAL-01 | Comprender el tratamiento de datos | Política accesible sin sesión, con versión, responsables, contacto, finalidades y conservación; sin promesas jurídicas no verificadas. | RF-46, RF-48, RF-71 |
| HU-LEGAL-02 | Comprender las condiciones de uso | Términos explican límites, entrega y reclamación; leer no marca aceptación; rechazar no equivale a una exoneración. | RF-47, RF-64 |

### 19.3 Orientación y educación

| Historia | Objetivo | Criterios de aceptación | Requisitos |
| --- | --- | --- | --- |
| HU-EDU-01 | Entender opciones responsables | Contenido claro, fuentes para afirmaciones específicas y consulta esencial sin red; sin impacto inventado. | RF-13, RF-14 |
| HU-ORI-01 | Orientarme sin registro | Puedo completar preguntas y leer recomendación sin sesión; no se guarda automáticamente. | RF-15 |
| HU-ORI-02 | Identificar la categoría | Opciones comprensibles con ejemplos; cambiar antes de continuar no duplica pasos. | RF-16 |
| HU-ORI-03 | Informar señales de atención o duda | Se omite funcionamiento, se muestra precaución y no se pide encender/cargar/desmontar. | RF-17 |
| HU-ORI-04 | Indicar funcionamiento conocido | Solo se pregunta sin señales de atención; No sé no exige probar el aparato. | RF-18 |
| HU-ORI-05 | Comprender recomendación y siguiente acción | La tabla de RF-19 produce el resultado esperado y tres pasos útiles; se distingue recomendación de decisión. | RF-19, RF-55 |
| HU-ORI-06 | Corregir una respuesta | Se invalidan respuestas incompatibles y se recalcula; no queda una respuesta oculta contradictoria. | RF-20 |
| HU-ORI-07 | Guardar para continuar | Acceso contextual conserva respuestas; descripción válida y respuesta remota crean un solo pendiente, nunca una entrega finalizada. | RF-21, RF-30 |

### 19.4 Lugares y mapa

| Historia | Objetivo | Criterios de aceptación | Requisitos |
| --- | --- | --- | --- |
| HU-DIR-01 | Ver lugares disponibles en el directorio | Mapa y lista muestran los mismos activos, incluidos puntos propios; fallo de datos privados no los oculta; caché y vacío son distinguibles. | RF-22, RF-84, RF-87 |
| HU-DIR-02 | Buscar un destino | Campo identifica dirección de entrega; desplegable muestra nombre/dirección; filtros visibles se pueden quitar y no provienen ocultamente de orientación. | RF-24, RF-65 |
| HU-DIR-03 | Revisar ficha y procedencia | Fuente y fecha presentes; referencia no se presenta como convenio; información antigua muestra aviso; datos ausentes no se inventan. | RF-22, RF-23 |
| HU-DIR-04 | Usar mi ubicación voluntariamente | Solo se pide al pulsar; denegación o tiempo agotado ofrece mapa/dirección y no bloquea entrega. | RF-26, RF-61 |
| HU-DIR-05 | Consultar una ruta estimada | Origen y destino confirmados; resultado conserva código activo; error ofrece alternativa y no genera entrega adicional. | RF-27, RF-60, RF-86 |
| HU-DIR-06 | Guardar un lugar como referencia | Se usa su ID, no se reserva ni notifica; cambio de referencia no cambia destinatario de entrega ya creada. | RF-28 |
| HU-DIR-07 | Elegir destino y generar sin perderme | La ficha explica elegibilidad y ofrece generación directa con aparatos preparados; QR aparece tras éxito; ruta es posterior y opcional; no hay catálogo oculto de receptores. | RF-65, RF-86, RF-87 |

### 19.5 Aparatos e historial

| Historia | Objetivo | Criterios de aceptación | Requisitos |
| --- | --- | --- | --- |
| HU-DEV-01 | Añadir un aparato | Usa las mismas validaciones y orientación; doble toque o reintento genera un solo pendiente propio. | RF-30 |
| HU-DEV-02 | Consultar Mis aparatos | Pendientes e Historial separados; estado y siguiente acción visibles; entrega activa tiene acceso a su código. | RF-29, RF-80 |
| HU-DEV-03 | Corregir datos de un pendiente | Cambios recalculan orientación cuando procede; conflictos se muestran; no se editan ítems bloqueados por entrega activa. | RF-31 |
| HU-DEV-04 | Retirar un pendiente que no usaré | Confirmación lo archiva sin marcar Entregado; entrega activa debe cancelarse primero; Historial identifica retiro. | RF-32 |
| HU-DEV-05 | Saber cómo va mi aparato | Detalle reúne datos y etapa con siguiente acción; no repite bloques ni ofrece acciones imposibles. | RF-33, RF-74 |
| HU-DEV-06 | Elegir conservar, reparar, reutilizar o entregar | Intención no marca resultado; conservar explica que no habrá código; transferencia sigue flujo de entrega. | RF-34, RF-35 |
| HU-DEV-07 | Preparar sin maniobras inseguras | Cinco aspectos permiten Hecho/No aplica; faltantes se explican; cambios guardados se recuperan al volver. | RF-36, RF-37 |
| HU-DEV-08 | Registrar lo que ocurrió | Resultado personal requiere declaración; Entregado solo tras receptor; generar QR o ruta no lo habilita. | RF-38, RF-80 |
| HU-DEV-09 | Corregir una declaración personal | No modifica una constancia compartida ni permite fabricar una entrega; conserva fecha de modificación. | RF-39 |
| HU-HIS-01 | Consultar resultados anteriores | Distingue personal, confirmado y retirado; cancelación no se presenta como recepción; orden por evento. | RF-40 |
| HU-HIS-02 | Encontrar un registro anterior | Búsqueda por descripción y filtros de resultado funcionan también con lista vacía, sin mezclar cuentas. | RF-40 |

### 19.6 Receptores, entregas y protección

| Historia | Objetivo | Criterios de aceptación | Requisitos |
| --- | --- | --- | --- |
| HU-REC-01 | Entender cómo recibir aparatos | Entrada visible, explicación breve, dos aceptaciones sin marcar y ninguna solicitud de documentos de identidad. | RF-66 |
| HU-REC-02 | Presentar mi lugar | Campos y tres fotos válidos, consentimiento previo, borrador recuperable y envío real; no aprobación automática. | RF-67 |
| HU-REC-03 | Aportar evidencias del lugar para revisión | Fotos privadas, orientación/tamaño comprobados y explicación de posibles visitas; ninguna foto se presenta como prueba de identidad. | RF-67, RF-71 |
| HU-REC-04 | Conocer la decisión y corregir | Estado, motivo y siguiente acción disponibles; solo operador decide; aprobación completa publica un único punto. | RF-68, RF-84 |
| HU-REC-05 | Alternar entre usuario y receptor | Misma identidad y datos propios; permiso vigente del servidor; suspendido no confirma por tener una pantalla abierta. | RF-69 |
| HU-ENT-01 | Preparar entrega y mostrar código | 1–20 aparatos propios preparados, destino elegible, contacto especial cuando corresponda, una sola activa; después del éxito se muestran QR/código sin exigir ruta. | RF-70, RF-85, RF-87 |
| HU-ENT-02 | Confirmar recepción como receptor | Otro receptor no accede; escaneo válido solo abre vista previa; confirmación explícita completa una vez y actualiza emisor sin segunda aprobación. | RF-70, RF-72 |
| HU-ENT-03 | Corregir o cancelar mi entrega | Antes de crear se cambia selección sin pérdida; después, cancelar exige motivo e invalida códigos; completada no se cancela como pendiente. | RF-70, RF-80 |
| HU-SEG-01 | Usar el servicio con datos protegidos | Pruebas de acceso cruzado, objetos privados, códigos vencidos, sesión revocada y abuso son rechazadas; no hay falsa aprobación ni datos sensibles en registros. | RF-71, RF-72; RNF-01–05, RNF-19–21 |
| HU-ADM-01 | Ver en la app las decisiones autorizadas | Aprobar, suspender o corregir desde un servicio autorizado produce estado coherente en app tras actualizar; no se requiere instalar otra versión ni una aprobación local paralela. | RF-68, RF-84; secciones 16–17 |

HU-ADM-01 define el contrato que la app consume; no incorpora la interfaz del futuro panel administrativo al alcance de este documento.

### 19.7 Distribución y disponibilidad

| Historia | Objetivo | Criterios de aceptación | Requisitos |
| --- | --- | --- | --- |
| HU-AND-01 | Instalar y abrir la app | Identidad y firma válidas, apertura accesible, sin segunda app de práctica y sin perder datos por actualización compatible. | RF-49, RF-53, RF-54 |
| HU-OFF-01 | Consultar lo esencial sin red | Inicio, orientación, Aprende y documentos incluidos funcionan; directorio distingue caché; ninguna escritura privada se simula. | RF-50, RF-51 |
| HU-UPD-01 | Entender y aplicar una actualización | Se distingue contenido remoto de nueva app; un canal habilitado entrega una actualización compatible y confiable, sin afirmar que un APK se actualiza solo. | RF-52 |

## 20. Estados de interfaz y recuperación de errores

### 20.1 Catálogo de vistas lógicas

Estos identificadores representan responsabilidades de interfaz, no obligan a crear una pantalla por fila. Una misma vista puede reunir pasos breves para reducir toques, siempre que conserve jerarquía, validaciones y posibilidad de volver.

| ID | Vista lógica | Acción y salida principales |
| --- | --- | --- |
| UI-01 | Inicio público | Comprender Noki; Preparar entrega, orientar o consultar. |
| UI-02 | Iniciar sesión | Correo y contraseña; alternativa de código y recuperación. |
| UI-03 | Crear cuenta | Correo y aceptaciones; continuar a verificación y alias. |
| UI-04 | Verificar correo | Introducir código, reenviar o corregir destinatario. |
| UI-05 | Recuperar acceso | Solicitar código sin revelar existencia de cuenta. |
| UI-06 | Nueva contraseña | Confirmar contraseña con validación y respuesta real. |
| UI-07 | Inicio con sesión | Saludo, Preparar entrega y retomar pendiente; comparte estructura con Inicio público. |
| UI-08 | Preguntas de orientación | Responder, continuar y volver sin respuestas incompatibles. |
| UI-09 | Recomendación | Comprender motivo y seguir con mi entrega o decisión. |
| UI-10 | Aprende | Consultar temas y fuentes. |
| UI-11 | Puntos de recolección | Buscar destino, mapa/lista y ficha seleccionada. |
| UI-12 | Ficha de lugar | Condiciones, contacto, generación contextual y ruta opcional. |
| UI-13 | Mis aparatos | Pendientes/Historial, Preparar entrega y código activo. |
| UI-14 | Datos del aparato | Crear/editar con contexto de orientación. |
| UI-15 | Detalle y decisión | Revisar estado y pasar al siguiente paso. |
| UI-16 | Preparación | Resolver cinco aspectos y elegir destino cuando corresponde. |
| UI-17 | Resultado | Declaración personal o constancia confirmada, claramente diferenciadas. |
| UI-18 | Mi cuenta | Alias, foto, perfil, contraseña, privacidad, rol y salida. |
| UI-19 | Términos | Leer y volver al contexto, sin aceptación automática. |
| UI-20 | Privacidad | Leer tratamiento y acceder a derechos de datos. |
| UI-21 | Acerca de Noki | Propósito, versión instalada, contacto y documentos. |
| UI-22 | Accesibilidad | Texto, contraste, movimiento y apariencia. |
| UI-23 | Mapa ampliado | Navegar mapa, elegir partida, calcular y mostrar código existente. |
| UI-24 | Dirección de partida | Buscar coincidencias y confirmar una. |
| UI-25 | Quiero recibir aparatos | Leer requisitos y aceptar antes de iniciar solicitud. |
| UI-26 | Solicitud de receptor | Datos del lugar, fotos privadas y resumen de envío. |
| UI-27 | Estado de revisión | Ver decisión, motivos y corrección/retiro según permiso. |
| UI-28 | Recepciones | Consultar asignadas, escanear/introducir código y ver historial. |
| UI-29 | Mi entrega / recepción | Crear y mostrar código al emisor; validar y confirmar al receptor según contexto. |
| UI-30 | Reportar un problema | Canal privado real con referencia de entrega; confirmación solo si se envió. |

### 20.2 Estados comunes

Toda vista con datos remotos debe distinguir carga inicial, contenido confirmado, vacío legítimo, actualización conservando contenido y error recuperable. Un permiso denegado y una sesión vencida no se presentan como listas vacías. Una acción deshabilitada muestra la condición faltante cerca del botón, no solo un color gris.

Los errores de campo se muestran junto al campo y llevan el foco de forma accesible. Los errores generales se presentan una sola vez en el contexto visible. Reintentar conserva entradas no sensibles y selección; no vuelve al inicio del proceso. Tras éxito, el mensaje se anuncia de forma accesible y no bloquea el siguiente paso.

### 20.3 Respuestas esperadas a situaciones frecuentes

| Situación | Mensaje o significado requerido | Recuperación |
| --- | --- | --- |
| Correo o contraseña no válidos | No se pudo acceder con esas credenciales; sin confirmar públicamente existencia de cuenta | Corregir o recuperar acceso. |
| Servicio sin respuesta | No se pudo completar o confirmar la operación | Reintentar; si es escritura, consultar primero su resultado. |
| Código de acceso vencido | Código no válido o vencido | Solicitar otro respetando límite. |
| Alias ocupado o cambio prematuro | Explicar conflicto o próxima fecha | Otro alias o conservar el actual. |
| Datos privados no autorizados | No tienes acceso a esta acción | Volver a contexto permitido; no mostrar datos del recurso. |
| Directorio no disponible | No fue posible actualizar los lugares | Conservar caché identificada y ofrecer actualización. |
| Filtros sin coincidencias | No hay coincidencias con estos filtros | Quitar filtros; no afirmar que no existen receptores. |
| Lugar informativo sin recepción en Noki | Puedes consultar este lugar, pero no registrar una entrega mediante la app aquí | Ruta/contacto o elegir otro sin perder preparación. |
| Destino propio | No puedes enviarte una entrega a ti mismo | Elegir otro; el punto sigue visible. |
| Falta contacto especial | Contacta y confirma aceptación antes de generar | Abrir WhatsApp y declaración; si falta número, otro lugar o actualización. |
| Preparación incompleta | Identificar aspectos pendientes | Ir al primer aspecto faltante. |
| Otra entrega activa | Ya tienes una entrega pendiente | Mostrar su código o cancelarla antes de otra. |
| Código generado pero ruta falla | La entrega sigue disponible; no se pudo calcular la ruta | Mostrar código, dirección y mapa externo. |
| Código de entrega inválido | No se puede validar esa credencial | Reintentar o pedir al emisor renovarla; respetar límites. |
| Entrega ya confirmada o cancelada | Mostrar el estado permitido real | Consultar constancia o volver, sin repetir transición. |
| Confirmación remota llega después de salir | Actualizar datos sin abrir pantallas fuera de contexto | Mostrar resultado al volver a la entrega. |
| Foto no cargada | La imagen no se guardó | Conservar foto anterior y permitir seleccionar/reintentar. |
| Receptor suspendido | Recepción no habilitada actualmente | Consultar motivo/canal de revisión; no confirmar localmente. |

## 21. Configuración y condiciones operativas

### 21.1 Configuración sin secretos en el cliente

Deben estar definidos y controlados: entorno autorizado, credenciales públicas del cliente cuando procedan, métodos de acceso realmente activos, versiones legales, servicio de correo y plantillas dinámicas, almacenes privados y límites de objetos, proveedores de mapas/rutas y su atribución, canal de actualización si se ofrece y contacto de soporte.

Las claves administrativas, contraseñas SMTP, secretos sociales y credenciales de operación permanecen en configuración segura del servidor. Sus valores, IDs de proyectos concretos y cuentas utilizadas en pruebas no pertenecen al SPEC. La ausencia de configuración no se oculta mediante botones que simulan éxito.

### 21.2 Responsabilidades de operación

| Responsabilidad | Condición necesaria |
| --- | --- |
| Revisar receptores | Operadores designados, permisos mínimos, criterios de revisión y decisiones auditadas. |
| Mantener directorio | Fuente y fecha por ficha, revisión periódica y procedimiento para correcciones o retiro. |
| Atender problemas | Canal monitorizado, referencia de caso cuando exista y tratamiento privado de los datos. |
| Conservar y suprimir datos | Política aprobada por categoría, procedimientos de objetos y copias, registro de ejecución y excepciones justificadas. |
| Operar correo | Cuenta remitente autorizada, límites conocidos, protección contra abuso y seguimiento de fallos; no prometer capacidad ilimitada. |
| Publicar versiones | Firma protegida, migraciones compatibles, canal confiable y plan de recuperación sin pérdida de cuentas. |
| Coordinar visitas | Personal autorizado, aviso y finalidad definidos; no declarar inspección que no ocurrió. |

### 21.3 Frontera con el panel administrativo

La app debe estar preparada para consumir aprobación, suspensión, correcciones y respuestas de soporte de la misma fuente autorizada. El panel tendrá una especificación separada de sus pantallas y operación. No se expone una función administrativa al usuario común para compensar que el panel no esté disponible.

Hasta que exista el panel, cualquier operación administrativa permitida sigue requiriendo operador autorizado, validaciones y auditoría del servicio. Las credenciales de usuarios no se comparten con administradores. La sincronización tiene límites definidos por conectividad y actualización de cliente, no una promesa genérica de «tiempo real» sin mecanismo.

## 22. Aceptación del producto, métricas y riesgos

### 22.1 Condiciones para aceptar una versión

Una versión se acepta cuando cumple los requisitos obligatorios de su alcance y existe evidencia verificable de los criterios aplicables. Una ampliación prevista no se anuncia como disponible sin aceptación independiente. Una función obligatoria ausente debe registrarse como brecha; no se resuelve marcándola como futura sin una decisión de alcance.

Como mínimo deben comprobarse:

1. Alta, verificación, contraseña, código alternativo, recuperación, sesión conservada, cierre y cambio de cuenta sin mezclar datos.
2. Propósito de Inicio, navegación, textos ampliados, teclado, lector de pantalla, temas y reducción de movimiento.
3. Todas las combinaciones de orientación, cambio de respuestas, guardado idempotente y recorridos sin entrega.
4. Pendientes, preparación, historial y retiro, sin fabricar resultados ni perder datos al volver.
5. Directorio público independiente del acceso privado; publicación, actualización y retiro coherentes de receptores.
6. Selección de destino, contacto especial, generación real, QR visible, código manual y regreso desde ruta.
7. Recepción entre dos cuentas distintas: escanear no confirma; confirmación válida completa una sola vez y actualiza al emisor.
8. Intentos de autoentrega, otro receptor, código vencido/renovado/usado, límites de intentos, cancelación y confirmación concurrentes.
9. Una entrega activa, agrupación de hasta 20 aparatos, recuperación de respuesta incierta y suspensión de receptor durante el recorrido.
10. Permisos y separación por cuenta en datos, objetos, operaciones y sesiones revocadas; ausencia de secretos en registros y distribución.
11. Fallos de conexión, ubicación, mapas, rutas, correo y almacenamiento, con recuperación sin callejones sin salida.
12. Procedimientos de privacidad y supresión realmente disponibles, distinguibles de promesas o solicitudes todavía pendientes.

Las comprobaciones deben combinar reglas automatizadas, integración del servicio y recorridos de interfaz representativos. Las recepciones se validan con dos identidades autorizadas, no concediendo privilegios al cliente. Los informes de ejecución, dispositivos concretos y resultados se guardan en el registro de calidad, no aquí.

### 22.2 Métricas de aceptación

| ID | Criterio objetivo |
| --- | --- |
| M-01 | Todos los criterios críticos de autenticación, propiedad, estados y entrega del alcance aceptado tienen evidencia satisfactoria. |
| M-02 | Ningún caso de acceso cruzado previsto en la matriz permite leer o modificar datos ajenos. |
| M-03 | Alta, contraseña y recuperación completan su recorrido con una cuenta válida, sin crear identidades duplicadas. |
| M-04 | Todas las fichas publicadas contienen procedencia y fecha, y distinguen referencia de receptor revisado. |
| M-05 | Al menos cuatro de cinco participantes del público prioritario explican el propósito tras consultar Inicio, sin ayuda. |
| M-06 | Al menos cuatro de cinco completan orientación y reconocen el siguiente paso sin intervención. |
| M-07 | Los cinco distinguen que aparecer en el directorio no garantiza convenio ni recepción. |
| M-08 | Al menos cuatro de cinco preparan un aparato y localizan el siguiente paso o código sin ayuda. |
| M-09 | Ninguna acción o información esencial queda recortada o superpuesta en la matriz de tamaños y texto al 200 %. |
| M-10 | Todas las acciones esenciales son accesibles por teclado y lector de pantalla, con alternativa textual al mapa. |
| M-11 | Ninguna prueba de respuesta incierta presenta como confirmado un resultado no comprobado. |
| M-12 | No aparecen textos de depuración, datos simulados o detalles internos de desarrollo en la experiencia pública. |
| M-13 | Ningún envío o edición del cliente concede aprobación de receptor ni acceso público a fotos privadas. |
| M-14 | Escanear, repetir confirmación o usar otro receptor no produce una constancia falsa ni duplicada. |

Las pruebas con cinco participantes son una comprobación formativa mínima, no una estimación estadística de toda la población ni una garantía de ausencia de errores.

### 22.3 Riesgos y controles exigidos

| Riesgo | Control |
| --- | --- |
| Lugar desactualizado o no disponible | Fuente, fecha, aviso de antigüedad, contacto y procedimiento de corrección. |
| Confusión entre revisión y garantía | Etiquetas específicas; no prometer seguridad, convenio o aceptación universal. |
| Manipulación peligrosa de un aparato | Condición preventiva, no pruebas de encendido y consulta previa; una declaración no elimina precauciones. |
| No llega el correo o se abusa del envío | Límite de reenvío, mensajes neutrales, canal de soporte y observación operativa del servicio. |
| Pérdida de cuenta al actualizar | Identidad y firma estables, migraciones compatibles y prueba de conservación de sesión/datos. |
| Generación o confirmación duplicadas | Idempotencia, unicidad, transacciones y reconciliación de respuestas inciertas. |
| Código compartido o intentos repetidos | Destinatario asignado, expiración, invalidación y límites persistentes de intentos. |
| Fotos con información privada | Aviso previo, minimización, eliminación de metadatos y almacenamiento restringido. |
| Fallo de proveedor o conectividad | Contenido público incluido, estados finitos, alternativas y separación entre ruta y entrega. |
| Datos privados accesibles a otra cuenta | Políticas de acceso, validación de sesión/propiedad y descarte de respuestas tardías. |
| Supresión incompleta | Procedimiento que incluye identidad, filas, objetos y copias, con seguimiento de errores parciales. |
| Navegación confusa o texto amontonado | Acción siguiente visible, jerarquía uniforme y pruebas de accesibilidad y comprensión. |

### 22.4 Control de cambios

Toda modificación posterior debe identificar requisito afectado, motivo, impacto en datos/permisos, compatibilidad y criterios de aceptación modificados. Los registros históricos se conservan fuera de la línea base vigente. No se acumulan anexos que contradigan silenciosamente la regla principal.

Esta versión constituye la especificación consolidada del alcance definido. La aprobación de una implementación o de una distribución requiere su evidencia de aceptación independiente.
