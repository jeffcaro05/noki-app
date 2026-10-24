# Noki App — SPEC

**Metodología:** Spec-Driven Development (SDD)  
**Tipo de documento:** Especificación de requisitos.  
**Versión del documento:** 3.13.
**Fecha:** 15 de octubre de 2026.  
**Equipo:** Luis Miguel Castañeda y Jeffry Giovanny Caro Arango.  
**Público objetivo principal:** adultos de 40 a 60 años de Medellín.  
**Plataformas objetivo:** Android, iPhone, iPad y escritorio Windows.  
**Lenguaje e interfaz:** núcleo Kotlin Multiplatform; interfaces nativas por plataforma. Android utiliza Jetpack Compose.  
**Entorno de desarrollo:** Android Studio.  
**Identificador de aplicación:** `co.noki.app`.

La aplicación permite aprender sobre aparatos en desuso, recibir orientación, consultar lugares de recolección y administrar registros personales. Las funciones privadas sincronizadas requieren una cuenta con correo verificado. Las funciones públicas no requieren registro. La app entregada no ofrece un espacio de simulación ni presenta entregas ficticias como reales.

---

## 1. Problema

### 1.1 Situación que atiende Noki

Personas adultas de Medellín conservan aparatos eléctricos y electrónicos que ya no utilizan y pueden tener dudas sobre si todavía sirven, si conviene repararlos, reutilizarlos, entregarlos a otra persona o llevarlos a un lugar que gestione este tipo de residuos.

El problema de Noki no se reduce a “encontrar dónde reciclar”. La aplicación debe ayudar a comprender el problema, tomar una decisión informada, preparar el dispositivo de manera responsable y consultar opciones locales sin hacer promesas que dependan de terceros.

### 1.2 Necesidades oficiales

| ID | Necesidad | Respuesta obligatoria de la app |
| --- | --- | --- |
| N1 | Falta de comprensión sobre las consecuencias del manejo inadecuado de residuos electrónicos. | Módulo “Aprende” con información clara, fuentes visibles, recomendaciones de manejo y diferencia entre reutilizar, reparar y gestionar como RAEE. |
| N2 | Carencia de criterios para determinar el destino adecuado de los dispositivos en desuso. | Orientación paso a paso según tipo de equipo, condición visible y funcionamiento declarado. |
| N3 | Falta de conocimiento sobre las opciones para la disposición responsable de RAEE en Medellín. | Directorio público de lugares consultados en fuentes verificables, con mapa, datos de contacto, fuente y fecha de última verificación. |
| N4 | Carencia de orientación durante el proceso de disposición de dispositivos en desuso. | “Mis dispositivos”, decisión personal, lista de preparación, asociación opcional con un lugar y registro personal del resultado final. |

### 1.3 Principio de honestidad operativa

Noki distingue referencias de fuentes públicas y receptores habilitados tras revisión. Aparecer en el mapa no implica convenio, cupo reservado ni aceptación garantizada. Una referencia externa no confirma códigos; un receptor habilitado sí puede validar un código y confirmar la recepción física dentro de la app.

Conservar, reparar o dar otro uso sin entrega puede generar un resultado personal declarado. Entregar a un receptor de Noki exige su confirmación y produce una constancia de recepción, no una certificación de reciclaje o tratamiento ambiental.

---

## 2. Objetivo y alcance

### 2.1 Objetivo general

Ofrecer una aplicación instalada, dirigida principalmente a adultos de 40 a 60 años de Medellín, que permita comprender opciones responsables para dispositivos electrónicos en desuso, recibir orientación no diagnóstica, consultar lugares de recolección con información verificable y gestionar un registro personal de dispositivos y decisiones. La interfaz debe adaptarse a teléfono, tableta y computador sin depender de una página web embebida.

### 2.2 Alcance funcional obligatorio

La aplicación final debe incluir:

1. Inicio público.
2. Registro con correo real y código de verificación; acceso con Google o Apple cuando el proveedor esté configurado. Acceso con contraseña para cuentas que tengan una.
3. Verificación de correo.
4. Inicio y cierre de sesión.
5. Recuperación y cambio de contraseña.
6. Perfil de usuario.
7. Cambio de nombre y correo.
8. Eliminación de cuenta.
9. Módulo público “Aprende”.
10. Cuestionario público de orientación.
11. Resultado de orientación claro y no diagnóstico.
12. Opción de guardar el resultado en una cuenta.
13. Directorio público de lugares de recolección.
14. Mapa con selección de lugar.
15. Fuente y fecha de verificación por lugar.
16. Búsqueda y filtros del directorio.
17. Geolocalización solo bajo solicitud expresa.
18. Ruta estimada en carro y alternativa externa.
19. Módulo privado “Mis dispositivos”.
20. Creación, edición y eliminación de registros personales.
21. Estado declarado del dispositivo.
22. Decisión personal: conservar, reparar, reutilizar/entregar a otra persona, llevar a un lugar de recolección o aún no decidir.
23. Asociación opcional de un lugar del directorio a un dispositivo.
24. Lista de preparación antes de entregar o trasladar un dispositivo.
25. Registro personal del resultado final.
26. Historial de dispositivos completados.
27. Estados vacíos, de carga, error y recuperación.
28. Instalación Android y consulta sin conexión de los contenidos incluidos y del último directorio descargado.
29. Accesibilidad y adaptación a teléfonos y tabletas Android.
30. Políticas públicas de privacidad y términos.
31. Separación de pruebas de desarrollo y cuentas reales; sin espacio de simulación en la app entregada.
32. Adaptación de navegación y presentación a las plataformas objetivo.
33. Información pública sobre cómo recibir aparatos y acceso desde Inicio y Perfil.
34. Solicitud, verificación, aprobación y suspensión del perfil receptor, con las condiciones de activación de la sección 20.
35. Entregas acordadas entre dos cuentas, confirmación del receptor tras validar el código y atención de incidencias, conforme a la sección 20.

### 2.3 Distribución y compatibilidad

- El producto se instala como un paquete Android firmado (APK). Para distribución mediante Google Play se genera un Android App Bundle (AAB).
- Android utiliza Jetpack Compose. El núcleo compartido Kotlin Multiplatform contiene categorías, estados, validaciones y reglas de orientación, sin dependencias de Android. No se utiliza WebView para mostrar una aplicación web, ni se empaqueta una PWA.
- El acceso desde el icono abre directamente Noki, sin iniciar un navegador.
- Los enlaces a fuentes, correo y navegación externa pueden abrir aplicaciones del sistema después de una acción del usuario.
- Android admite API 26 o superior, en teléfonos y tabletas. iOS e iPadOS tienen como objetivo la versión 16 o superior, con interfaz SwiftUI y el núcleo Kotlin compartido. Windows tiene como objetivo Windows 10/11 de 64 bits, con interfaz Compose Desktop y el mismo núcleo. Cada interfaz debe cubrir las mismas funciones y estados antes de distribuirse.
- La compilación y firma de iOS requieren macOS y Xcode. Un iPhone conectado a Windows no sustituye ese entorno. La validación incluye un iPhone 11 y un iPad; Android incluye el VIVO Y39 5G y una tableta. La aceptación de una plataforma no valida automáticamente las demás.
- Los navegadores y el panel web administrativo son productos separados. La aplicación de escritorio ofrece las funciones del usuario, no privilegios de administración.
- La actualización requiere el mismo identificador de aplicación y la misma firma, con un `versionCode` superior. No debe exigir desinstalar ni borrar datos.

### 2.4 Territorio

Medellín es el territorio principal de contenido y pruebas. La orientación, el directorio y la cuenta de usuario común no solicitarán documento de identidad, edad exacta ni dirección de residencia. La solicitud voluntaria de perfil receptor tiene requisitos de identidad y mayoría de edad definidos en la sección 20; no restringe el uso público de Noki. Una persona fuera de Medellín puede abrir la aplicación, pero el directorio y la información local de esta versión se centran en Medellín.

### 2.5 Relación con Noki Transmedia

Noki App es una pieza funcional del universo Noki, pero este SPEC cubre únicamente la aplicación. No obliga a implementar en la app todas las piezas narrativas, redes sociales, activaciones físicas o productos adicionales del proyecto transmedia.

---

## 3. Usuarios y actores

### 3.1 Visitante

Persona que no ha iniciado sesión.

Puede:

- consultar Inicio;
- usar Orientación;
- consultar Aprende;
- consultar Directorio y mapa;
- crear una cuenta;
- iniciar sesión;
- consultar términos y privacidad.

No puede:

- guardar dispositivos;
- guardar resultados de orientación;
- asociar puntos a un registro personal;
- consultar historial;
- modificar perfil.

Estas restricciones corresponden a los registros de cuenta. El visitante conserva orientación, directorio y Aprende, pero no crea aparatos guardados ni entregas sin una cuenta. RF-53 restringe la simulación a pruebas de desarrollo.

### 3.2 Usuario registrado

Persona con cuenta real de Supabase Auth y correo verificado.

Puede:

- usar todas las funciones públicas;
- acceder a su panel personal;
- crear y administrar sus dispositivos;
- guardar resultados de orientación;
- asociar un lugar del directorio;
- completar la lista de preparación;
- registrar un resultado final personal;
- consultar su historial;
- administrar su perfil y cuenta.

### 3.2.1 Receptor aprobado

Es un usuario registrado al que el servidor concede permiso adicional para recibir aparatos. Conserva sus funciones personales y puede cambiar de contexto sin otra cuenta. No obtiene permiso de administrador ni puede aprobarse a sí mismo. Su información y las condiciones del lugar de recepción deben haber sido revisadas; no se certifica su identidad civil. Aparecer en el directorio público no lo convierte en receptor. Reglas, estados y permisos: sección 20.

### 3.3 Equipo administrador

El equipo autorizado mantiene información técnica y contenido desde herramientas de administración separadas. La app pública **no incluye un panel administrativo en este alcance**.

### 3.4 Organizaciones y lugares del directorio

Son entidades externas mencionadas con base en información pública verificable. La existencia de una ficha en Noki:

- no crea una relación contractual;
- no concede permisos a esa organización dentro de Noki;
- no implica integración técnica;
- no implica aceptación garantizada de cualquier dispositivo.

### 3.5 Proveedores técnicos externos

- **Supabase:** autenticación, base de datos y operaciones de cuenta.
- **MapLibre Native y proveedor cartográfico basado en OpenStreetMap:** representación cartográfica nativa, con atribución visible.
- **OSRM:** cálculo de ruta.
- **Servicio de correo configurado con Supabase:** verificación y recuperación de cuenta.

### 3.6 Matriz de permisos

| Acción | Visitante | Usuario registrado |
| --- | ---: | ---: |
| Ver Inicio | Sí | Sí |
| Consultar Aprende | Sí | Sí |
| Usar Orientación | Sí | Sí |
| Consultar directorio y mapa | Sí | Sí |
| Solicitar ruta | Sí | Sí |
| Crear cuenta | Sí | No es necesario |
| Iniciar sesión | Sí | Sesión ya iniciada |
| Guardar orientación | No | Sí |
| Ver Mis dispositivos | No | Sí, solo los propios |
| Crear/editar/retirar un pendiente | No | Sí, solo los propios, sin entrega activa |
| Asociar lugar a un dispositivo | No | Sí |
| Completar lista de preparación | No | Sí |
| Marcar resultado personal | No | Sí |
| Consultar historial propio | No | Sí |
| Editar perfil | No | Sí |
| Eliminar cuenta | No | Sí |
| Modificar fichas públicas del directorio | No | No |
| Ver datos privados de otra cuenta | No | No |

---

## 4. Glosario

| Término | Definición de este SPEC |
| --- | --- |
| RAEE | Residuos de aparatos eléctricos y electrónicos. Un dispositivo en desuso no se considera automáticamente residuo si todavía puede tener una segunda vida. |
| Orientación | Cuestionario de apoyo a la decisión. No es diagnóstico técnico ni certificación. |
| Recomendación | Resultado general generado a partir de las respuestas del usuario. |
| Dispositivo | Registro personal de un aparato o accesorio que el usuario desea gestionar en Noki. |
| Estado funcional | Declaración del usuario: funciona, presenta fallas o no conoce su estado. |
| Condición de atención | Declaración sobre señales visibles inusuales como hinchazón, calentamiento, fuga o daño importante. |
| Decisión | Opción que el usuario planea tomar con el dispositivo. |
| Resultado personal | Acción que el usuario declara haber realizado finalmente. |
| Punto de recolección | Lugar incluido en el directorio a partir de una fuente pública. No equivale a aliado de Noki. |
| Punto seleccionado | Referencia que el usuario asocia a un dispositivo para consultarla después. No es reserva. |
| Fuente | Sitio web o documento público del que se obtuvo la información del lugar. |
| Fecha de verificación | Fecha en la que el equipo de Noki comprobó por última vez la información pública de la ficha. |
| Perfil | Datos básicos de la cuenta gestionados por el usuario. |
| Cuenta verificada | Cuenta cuyo correo fue confirmado mediante el mecanismo de autenticación. |
| Borrador local | Formulario aún no confirmado por el servidor, conservado cifrado en el almacenamiento privado de Android y asociado a una cuenta. |
| APK | Archivo firmado que Android utiliza para instalar o actualizar Noki. |
| AAB | Paquete utilizado para distribuir versiones mediante Google Play. |
| RLS | Reglas de Supabase que limitan qué filas puede consultar o modificar cada usuario. |
| Registro personal | Información introducida por el usuario para su propio seguimiento. No constituye validación de un tercero. |
| Mis aparatos | Nombre visible del módulo de registros personales. “Dispositivo” y “aparato” identifican la misma entidad de datos. |
| Simulación técnica | Fixtures exclusivos de desarrollo, no disponibles en la app entregada. Nunca se importan a una cuenta ni se consideran entregas reales. |

---

## 5. Requisitos funcionales

### 5.1 Inicio, navegación y presentación

#### RF-01 — Inicio público

Al abrir la aplicación desde su icono sin sesión, debe mostrar:

- identidad Noki;
- mensaje breve sobre el propósito de la app;
- CTA principal “Orientar mi dispositivo”;
- acceso a “Puntos de recolección”;
- acceso a “Aprende”;
- “Iniciar sesión”;
- “Crear cuenta”.

No se mostrará un formulario de login como único contenido inicial.

#### RF-02 — Inicio con sesión

Con sesión válida, la página principal debe mostrar:

- saludo con el nombre del usuario;
- acceso a “Mis dispositivos”;
- “Orientar otro dispositivo”;
- “Puntos de recolección”;
- “Aprende”;
- “Mi perfil”;
- “Cerrar sesión”.

Si el usuario tiene dispositivos activos, se mostrará un resumen de hasta tres registros recientes y un enlace “Ver todos”.

#### RF-03 — Navegación consistente

Todas las pantallas deben permitir identificar:

- dónde está el usuario;
- cómo volver al Inicio;
- cómo acceder a las funciones principales;
- cómo volver a la pantalla anterior mediante una acción visible y mediante Atrás de Android.

En teléfonos se muestran cuatro destinos persistentes: Inicio, Mis dispositivos, Puntos y Aprende. Perfil se abre desde Inicio. Orientación se inicia mediante una acción claramente etiquetada. En tabletas, los mismos destinos usan una barra lateral. Atrás vuelve una sola pantalla; cambiar de destino principal no acumula copias de una pantalla.

### 5.2 Registro, autenticación y recuperación

El acceso principal por correo utiliza un código de un solo uso según RF-56. RF-04 y los requisitos de contraseña se aplican a la modalidad con contraseña. Las cuentas creadas con Google o Apple no deben recibir formularios que exijan una contraseña de Noki inexistente.

#### RF-04 — Crear una cuenta con contraseña

El formulario de registro debe solicitar:

- nombre completo;
- correo electrónico;
- contraseña;
- confirmación de contraseña;
- aceptación obligatoria de Términos;
- aceptación obligatoria de Política de privacidad.

Reglas:

- nombre: 2–80 caracteres después de recortar espacios;
- correo: formato de correo válido; se eliminan espacios externos y se normaliza a minúsculas antes de enviarlo a Auth;
- contraseña: mínimo 8 caracteres y máximo 64; la app no elimina espacios ni modifica silenciosamente la contraseña escrita;
- ambas contraseñas deben coincidir;
- no se permite continuar sin aceptar ambos documentos;
- no se solicita documento de identidad, dirección, edad exacta ni teléfono.

#### RF-05 — Verificar correo

Después de que el servidor acepte la solicitud, la app debe indicar que se solicitó un correo de verificación. No debe afirmar que llegó a la bandeja de entrada.

Hasta verificarlo:

- la cuenta puede existir en Auth;
- no se habilitan funciones privadas;
- se puede reenviar el correo;
- se puede volver a Inicio o Login.

Al introducir un código válido o abrir un enlace de verificación válido, el usuario debe regresar a Noki y recibir confirmación de correo verificado. Los proveedores sociales deben aportar una identidad validada; una dirección escrita por el usuario no equivale a verificación.

#### RF-06 — Reenviar verificación

El usuario no verificado debe poder solicitar un nuevo correo de verificación. La interfaz debe:

- impedir múltiples envíos mientras una solicitud está en curso;
- mostrar confirmación neutral, sin revelar información sensible;
- respetar límites del proveedor.

#### RF-07 — Iniciar sesión

El acceso con contraseña debe usar:

- correo real;
- contraseña.

No se añadirá automáticamente ningún dominio ficticio al identificador de acceso; el usuario debe ingresar su correo real.

Con credenciales correctas y correo verificado se abre el panel personal.

#### RF-08 — Mostrar/ocultar contraseña

Los formularios de acceso, registro y cambio de contraseña deben ofrecer un control accesible para mostrar u ocultar la contraseña sin modificar su valor.

#### RF-09 — Recuperar contraseña

“Olvidé mi contraseña” debe solicitar un correo y enviar un enlace de recuperación.

El mensaje visible será neutral:

> “Si existe una cuenta asociada a ese correo, recibirás instrucciones para continuar.”

Esto evita confirmar públicamente si un correo está registrado.

#### RF-10 — Establecer nueva contraseña

Al abrir un enlace de recuperación válido, el usuario podrá registrar una nueva contraseña y confirmarla. Debe cumplir las mismas reglas de RF-04.

Después del cambio se mostrará confirmación y acceso al login o al panel si la sesión de recuperación sigue siendo válida.

#### RF-11 — Mantener y recuperar sesión

La aplicación debe:

- conservar la sesión según la configuración segura de Supabase;
- renovar tokens mediante el mecanismo soportado por Auth;
- detectar sesión caducada;
- redirigir a Login cuando ya no sea válida;
- conservar únicamente borradores locales autorizados y no presentar datos privados de una sesión anterior.

#### RF-12 — Cerrar sesión

Al cerrar sesión:

- se invalida la sesión local;
- se elimina de memoria la información privada;
- se eliminan borradores locales pertenecientes a la cuenta;
- se muestra el Inicio público;
- no se borran registros almacenados en el servidor.

### 5.3 Información educativa

#### RF-13 — Mostrar “Aprende”

La sección pública “Aprende” debe explicar, en lenguaje simple:

1. qué son los RAEE;
2. por qué no conviene tratar todo aparato en desuso como basura común;
3. diferencia entre seguir usando, reparar, reutilizar y gestionar como residuo;
4. protección básica de datos personales antes de entregar un dispositivo;
5. precauciones generales cuando hay daño, hinchazón, fuga o calentamiento inusual;
6. cómo comprobar información de un lugar antes de desplazarse.

Cada bloque debe mostrar sus fuentes o un apartado “Fuentes consultadas” y la fecha de revisión del contenido.

#### RF-14 — Evitar afirmaciones no sustentadas

La app no mostrará cifras de impacto, kilogramos recuperados, emisiones evitadas, tasas de reciclaje ni estadísticas locales sin indicar fuente, año y territorio.

### 5.4 Orientación

#### RF-15 — Iniciar orientación sin cuenta

La orientación debe poder abrirse desde Inicio sin iniciar sesión. Las respuestas públicas permanecerán en memoria de la sesión de navegación y no se enviarán a Supabase mientras el usuario no decida guardarlas.

#### RF-16 — Pregunta 1: tipo de dispositivo

Opciones:

- Teléfono o tableta.
- Computador o periférico.
- Audio o video.
- Accesorios y cables.
- Pequeño electrodoméstico.
- Otro aparato electrónico.

Debe elegirse una opción para continuar.

#### RF-17 — Pregunta 2: condición visible

Pregunta:

**“¿Has notado alguna señal de atención?”**

Texto de apoyo: “Por ejemplo: hinchazón, fuga, calentamiento inusual o daño importante. No lo enciendas ni lo manipules para comprobarlo.”

Opciones:

- Sí, he notado alguna de esas señales.
- No he notado algo así.
- No estoy seguro.

Si responde “Sí” o “No estoy seguro”, se muestra directamente la recomendación preventiva y no se obliga a encender, cargar o manipular el dispositivo para continuar.

#### RF-18 — Pregunta 3: funcionamiento

Solo aparece cuando RF-17 = “No he notado algo así”.

Pregunta:

**“¿Sabes si funciona?”**

Texto de apoyo: “Responde solo con lo que ya sabes. No necesitas probarlo.”

Opciones:

- Sí, funciona.
- Funciona con fallas.
- No lo sé.

#### RF-19 — Generar recomendación

La app debe producir exactamente una de estas familias de resultado:

- `SAFETY_REVIEW`: “Primero, una revisión segura”. El texto indica consultar a una persona capacitada antes de manipular o trasladar el aparato; no se solicita encenderlo, cargarlo ni desmontarlo para completar la orientación.
- `SECOND_LIFE`: “Todavía puede tener una segunda vida.”
- `REPAIR_REVIEW`: “Una revisión puede ayudarte a decidir”. El texto propone valorar una revisión antes de descartar y confirmar las condiciones si se decide llevarlo a recolección.

El resultado debe:

- explicar por qué se muestra;
- indicar opciones siguientes;
- aclarar que no es diagnóstico técnico;
- ofrecer “Cambiar respuestas”;
- ofrecer “Ver puntos de recolección” cuando corresponda;
- ofrecer “Guardar en Mis dispositivos” a usuarios con sesión;
- ofrecer “Crear cuenta para guardar” a visitantes.

#### RF-20 — Retroceder y cambiar respuestas

El usuario debe poder volver a la pregunta anterior. Si modifica una respuesta que invalida una pregunta posterior, la respuesta posterior debe descartarse.

#### RF-21 — Guardar orientación

Un usuario autenticado puede guardar el resultado como un nuevo dispositivo.

Antes de guardar debe completar:

- una descripción breve del dispositivo, 2–120 caracteres;
- marca, opcional, 0–60;
- modelo, opcional, 0–60.

La categoría, condición visible, funcionamiento y código de recomendación se copian desde la orientación.

Mapeo exacto:

- “Sí, he notado alguna de esas señales” → `safety_state='attention'`.
- “No he notado algo así” → `safety_state='normal'`.
- “No estoy seguro” → `safety_state='unknown'`.
- “Sí, funciona” → `function_state='works'`.
- “Funciona con fallas” → `function_state='faults'`.
- “No lo sé” → `function_state='unknown'`.

### 5.5 Directorio y mapa

#### RF-22 — Cargar lugares publicados

El directorio debe consultar registros activos de `noki_collection_points`.

Cada ficha pública debe contener:

- nombre;
- dirección;
- ciudad;
- categorías o tipos de residuos/equipos indicados por la fuente;
- horario, si la fuente lo publica;
- teléfono, si existe;
- sitio web, si existe;
- fuente;
- fecha de última verificación;
- coordenadas.

Una ficha sin fuente o sin fecha de verificación no debe publicarse.

#### RF-23 — Mostrar aviso de alcance

En el directorio y en cada detalle debe mostrarse de forma comprensible:

> “Noki muestra información de consulta. La disponibilidad, horarios y condiciones de recepción dependen de cada lugar. Confirma antes de desplazarte.”

No se utilizará “aliado” salvo que exista un convenio formal y una futura versión del SPEC lo contemple.

#### RF-24 — Buscar y filtrar

El directorio debe permitir:

- búsqueda por nombre o dirección;
- filtro por categoría de dispositivo/residuo cuando la ficha tenga categorías verificadas;
- limpiar filtros.

Si ningún lugar coincide, debe mostrarse un estado vacío con opción para limpiar filtros.

#### RF-25 — Seleccionar un lugar

Seleccionar una ficha o marcador debe:

- destacar el mismo lugar en lista y mapa;
- mostrar su detalle;
- centrar el mapa;
- no crear una reserva;
- no modificar automáticamente ningún dispositivo personal.

#### RF-26 — Solicitar ubicación solo bajo acción

La app no solicitará geolocalización al cargar.

Solo se solicitará al pulsar “Ruta desde mi ubicación”.

Si se rechaza o falla, la app debe permitir:

- marcar un origen en el mapa;
- abrir la dirección en un servicio externo.

La ubicación no se almacenará en tablas de Noki.

#### RF-27 — Calcular ruta

Con origen y destino válidos, OSRM calculará una ruta en carro y la app mostrará:

- línea de ruta;
- distancia aproximada;
- tiempo estimado.

No se prometerá tráfico en vivo ni navegación giro a giro.

Si OSRM falla, se ofrecerá abrir el destino en Google Maps u otro enlace externo configurado.

#### RF-28 — Asociar un lugar a un dispositivo

Un usuario autenticado puede guardar un lugar del directorio como referencia de un dispositivo.

La interfaz debe decir claramente:

**“Guardar como lugar de referencia”**

y no “Reservar”, “Agendar” o “Confirmar entrega”.

El usuario puede cambiar o quitar esa referencia.

### 5.6 Mis dispositivos

#### RF-29 — Listar dispositivos propios

“Mis dispositivos” debe mostrar únicamente registros cuyo `user_id` corresponda a la sesión autenticada.

Se separan:

- Activos.
- Historial.

Cada tarjeta activa muestra:

- descripción;
- categoría;
- estado funcional;
- decisión actual;
- última actualización;
- lugar de referencia, si existe.

#### RF-30 — Crear dispositivo manualmente

El formulario debe solicitar:

- categoría;
- descripción;
- marca opcional;
- modelo opcional;
- estado funcional: “Funciona”, “Presenta fallas” o “No conozco su estado”;
- condición de atención: “He notado una condición que requiere atención”, “No he notado algo así” o “No estoy seguro”.

Mapeo técnico:

- estado funcional → `works`, `faults`, `unknown`;
- condición de atención → `attention`, `normal`, `unknown`.

Al crear:

- decisión = `undecided`;
- ciclo = `active`;
- no se asigna un punto automáticamente;
- no se genera QR ni código de entrega.

#### RF-31 — Editar dispositivo

El propietario puede editar:

- categoría;
- descripción;
- marca;
- modelo;
- estado funcional;
- condición de atención.

La edición debe actualizar la recomendación sugerida cuando los campos que la determinan cambien.

#### RF-32 — Retirar un pendiente y solicitar supresión

El propietario puede retirar un aparato pendiente sin entrega activa después de una confirmación explícita. Se conserva en Historial como «Retirado por ti · sin entrega»; no se describe como borrado del servidor ni como resultado realizado. Los registros confirmados no pueden reabrirse desde este control. La supresión de datos personales o rectificación de constancias se solicita por el canal de privacidad; no se impone una conservación perpetua. Cancelar una entrega conserva su constancia y permite preparar otra con los aparatos no retirados.

La eliminación:

- borra el dispositivo y su checklist asociado;
- no afecta otros dispositivos;
- no modifica el directorio público;
- no puede ser ejecutada por otra cuenta.

#### RF-33 — Mostrar detalle

El detalle debe reunir en una sola pantalla:

- datos del dispositivo;
- recomendación vigente;
- decisión;
- lugar de referencia;
- lista de preparación;
- resultado final si está completado;
- acciones disponibles según estado.

#### RF-34 — Registrar decisión

Opciones:

- `undecided` — Aún no he decidido.
- `keep` — Seguir utilizándolo.
- `repair` — Revisar o reparar.
- `reuse` — Entregarlo a otra persona o reutilizarlo.
- `deliver` — Llevarlo a un lugar de recolección.

Cambiar la decisión no certifica que la acción ya se realizó.

#### RF-35 — Mostrar recomendación coherente

A partir de condición visible y funcionamiento:

- condición de atención = «He notado una condición que requiere atención» o «No estoy seguro» → `SAFETY_REVIEW`;
- sin condición de atención + funciona → `SECOND_LIFE`;
- sin condición de atención + falla/no sabe → `REPAIR_REVIEW`.

La recomendación puede sugerir alternativas, pero la decisión final la toma el usuario.

#### RF-36 — Lista de preparación

Para decisiones que implican mover, entregar o transferir el dispositivo, debe existir una lista de preparación con cinco ítems:

1. Revisar si es posible respaldar la información importante.
2. Cerrar sesiones o cuentas personales cuando sea posible.
3. Retirar SIM o memoria removible si aplica y puede hacerse sin desmontar el equipo.
4. Borrar datos personales o restablecer el equipo si es seguro y posible.
5. Confirmar directamente con el lugar las condiciones de recepción antes de desplazarse.

Cada ítem permite:

- Pendiente.
- Hecho.
- No aplica.

La pantalla debe recordar que no se debe desmontar el dispositivo y que, ante hinchazón, fuga, daño severo o calentamiento inusual, no debe forzarse su encendido o carga para completar la lista.

#### RF-37 — Conservar checklist

El checklist se guarda por dispositivo y puede continuarse en otra sesión o dispositivo después de sincronizar con Supabase.

#### RF-38 — Marcar resultado final

El usuario puede marcar un dispositivo como completado eligiendo exactamente una opción:

- `kept` — Lo sigo utilizando.
- `repaired` — Fue revisado o reparado.
- `reused` — Lo reutilicé o entregué a otra persona.
- `delivered` — Lo llevé a un lugar de recolección.
- `other` — Tomé otra decisión responsable.

`delivered` solo está disponible después de una entrega nativa confirmada por emisor y receptor según RF-70. No se sustituye esa confirmación seleccionando una ficha de referencia o «Otro lugar». Los resultados de conservación, reparación y reutilización siguen siendo declaraciones personales; ninguno certifica reciclaje.

#### RF-39 — Editar resultado personal

El usuario puede corregir un resultado personal sin entrega vinculada. Una constancia de entrega no se reabre ni se elimina desde este control; las discrepancias se tramitan por soporte conservando el registro de ambas partes.

#### RF-40 — Historial

El historial debe permitir:

- ver dispositivos completados por fecha descendente;
- buscar por descripción;
- filtrar por resultado;
- abrir detalle;
- eliminar un registro personal.

No se mostrarán métricas ambientales calculadas a partir del número de dispositivos.

### 5.7 Perfil y cuenta

#### RF-41 — Ver perfil

Debe mostrar:

- nombre completo;
- correo actual;
- estado de verificación;
- fecha de creación de la cuenta;
- accesos para editar nombre, cambiar correo, cambiar contraseña cuando corresponda al método de acceso, eliminar cuenta y ajustar accesibilidad.

#### RF-42 — Editar nombre

El usuario puede cambiar su nombre completo con la misma regla de 2–80 caracteres.

#### RF-43 — Cambiar correo

El usuario puede solicitar un nuevo correo.

La cuenta no debe considerarlo definitivo hasta completar el mecanismo de confirmación requerido por Supabase.

La app debe explicar a qué dirección se envió el enlace y qué hacer si el cambio no se completa.

#### RF-44 — Cambiar contraseña desde perfil

Una sesión válida puede iniciar el cambio de contraseña. La nueva contraseña debe cumplir RF-04 y confirmarse dos veces.

#### RF-45 — Eliminar cuenta

La eliminación debe requerir:

- sesión válida;
- confirmación explícita;
- reautenticación reciente mediante el método de acceso de la cuenta: contraseña, nuevo código o proveedor social. No se solicita una contraseña inexistente a cuentas sin contraseña.

Al confirmar exitosamente:

- se eliminan `noki_profiles`, `noki_devices` (incluida su preparación) y `noki_legal_acceptances` pertenecientes a la cuenta;
- se elimina la identidad de Auth mediante una operación segura del servidor;
- se limpia almacenamiento local;
- se cierra la sesión;
- se muestra Inicio público.

Una eliminación fallida no debe mostrar éxito.

### 5.8 Privacidad y documentos legales

#### RF-46 — Política de privacidad

Debe existir una página pública que explique como mínimo:

- qué datos se solicitan;
- para qué se usan;
- dónde se almacenan;
- servicios externos utilizados;
- uso puntual de geolocalización;
- cómo eliminar la cuenta;
- responsables: Luis Miguel Castañeda y Jeffry Giovanny Caro Arango; canal de contacto: nokiappcorporation@gmail.com.

#### RF-47 — Términos de uso

Debe existir una página pública que explique:

- alcance informativo de Noki;
- que Orientación no es diagnóstico;
- que el directorio no constituye convenio ni garantía;
- que los resultados personales son declaraciones del usuario;
- límites de responsabilidad razonables.

#### RF-48 — Registrar aceptación

Al crear cuenta se almacena:

- usuario;
- tipo de documento;
- versión;
- fecha/hora de aceptación.

Una nueva versión sustancial de términos o privacidad puede requerir nueva aceptación antes de continuar con funciones privadas.

### 5.9 Instalación, conexión y actualización

#### RF-49 — Instalación Android

El APK debe declarar `co.noki.app`, nombre Noki, icono adaptativo, API mínima 26 y versión visible en Perfil. Al instalar y abrir debe presentar Inicio. La instalación no requiere una cuenta Noki. Android gestiona el permiso para instalar desde una fuente externa; la app no lo activa ni instala paquetes silenciosamente. No se solicitan cámara, contactos, SMS, micrófono ni acceso general a archivos.

#### RF-50 — Contenido público sin conexión

Inicio, Orientación, contenido educativo inicial, tipografías, iconos y documentos legales se incluyen en el paquete y funcionan desde la primera apertura sin red. El paquete incluye un directorio inicial con fuentes y fechas; una descarga correcta sustituye esa copia y conserva la versión pública consultada. Si la actualización falla, la aplicación identifica que muestra una copia y permite reintentar. Nunca se presenta un horario almacenado como disponibilidad en tiempo real.

No se promete cartografía completa ni rutas sin conexión.

#### RF-51 — No simular escrituras privadas offline

Crear, editar, eliminar o completar dispositivos de una cuenta requiere confirmación del servidor. No existe una alternativa local de simulación para usuarios de la app entregada.

Si no hay conexión:

- el formulario puede conservar temporalmente un borrador local;
- la app no mostrará “Guardado” hasta recibir respuesta del servidor;
- al volver la conexión se ofrece reintentar.

Reglas del borrador privado:

- almacenamiento: archivo privado cifrado por cuenta, excluido de copias de seguridad y transferencia entre teléfonos;
- contiene solo campos del formulario y `updated_at` local;
- caduca después de 7 días;
- se elimina después de un guardado confirmado;
- se elimina al cerrar sesión;
- no se sincroniza entre dispositivos;
- si está vencido, la app lo descarta y muestra un aviso breve.

#### RF-52 — Actualizar la aplicación

Perfil muestra la versión instalada y la acción “Buscar actualización”. La consulta HTTPS de un manifiesto de versiones compara `versionCode`, API mínima, URL HTTPS del APK y SHA-256. Una versión superior ofrece “Ver actualización” y “Más tarde”. La descarga e instalación se confirman en Android; no se fuerza el cierre mientras se edita un formulario. Sin conectividad se muestra “No pudimos comprobar las actualizaciones. Inténtalo de nuevo”. Un paquete con firma distinta no es una actualización válida. El contenido público remoto puede actualizarse sin sustituir el APK; la lógica y pantallas cambian mediante una nueva versión firmada.

---

#### RF-53 — Pruebas separadas del producto

Desde 0.7.1, debug y release de `co.noki.app` deshabilitan `PRACTICE_ENABLED`: no muestran “Probar sin cuenta”, la franja de práctica ni permiten entrar a ese modo mediante el modelo. Las entregas de usuarios requieren una cuenta real y receptor aprobado; no basta con ocultar el aviso de una simulación activa.

El código conserva fixtures y una variante técnica `qa` para desarrollo aislado. No forma parte del APK entregado, no se instalará nuevamente en el VIVO del titular por su instrucción y no comparte el almacenamiento de la app real. Las pruebas de regresión de sesión usan respuestas controladas y almacenamiento en memoria en el computador.

Los ejemplos locales de versiones anteriores no se importan a una cuenta, no se sincronizan ni se borran como parte de esta actualización. No se cambia el identificador de paquete, el almacén cifrado ni la clave de Android Keystore de las cuentas. Actualizar no requiere desinstalar ni borrar datos. No se muestran contraseñas ni tokens.

#### RF-54 — Interfaz por tamaño y plataforma

Los destinos principales son Inicio, Mis aparatos, Puntos y Aprende. En ventanas de menos de 700 dp se utiliza una barra inferior con icono y texto por destino. Desde 700 dp se utiliza una barra lateral; los contenidos mantienen un ancho de lectura máximo de 640 dp. El botón Mi cuenta permanece accesible desde Inicio. Cambiar de sección no duplica pantallas. Un directorio abierto desde una orientación o un aparato ofrece Volver al contexto de origen.

Android, iOS/iPadOS y Windows comparten las reglas de orientación y validación. Cada cliente integra su almacenamiento seguro, permisos, enlaces y ciclo de vida nativo. Los controles deben admitir teclado, lector de pantalla y escalado del texto. No se presenta como compatible una plataforma por el solo hecho de que compile el núcleo compartido.

#### RF-55 — Recomendación orientada a una acción

Cada resultado incluye tres pasos concretos coherentes con el estado declarado, “Guardar y preparar mi aparato” y acceso directo al directorio con la categoría ya seleccionada. No obliga a repetir la orientación ni envía al usuario a una explicación genérica como única salida.

La rama preventiva prioriza no usar ni cargar el equipo y contactar antes de trasladarlo. La rama de segunda vida prioriza conservar o reutilizar y proteger la información. La rama de revisión explica qué consultar a un servicio técnico: diagnóstico, presupuesto, repuestos, garantía y datos. Ninguna rama afirma que el equipo sea seguro, reparable o aceptado por un punto.

#### RF-56 — Código de acceso por correo y proveedores sociales

El formulario solicita un correo válido al que el usuario tenga acceso. Gmail, iCloud, Outlook y otros dominios siguen el mismo flujo de correo; no equivalen a autenticación con Google o Apple. Se informa sobre el tratamiento de los datos y se registran las aceptaciones versionadas de RF-46–48 antes de habilitar el alta pública.

“Enviar código” solicita un código al servicio Auth. La cuenta no obtiene sesión ni acceso privado hasta que el servidor valide el código. La pantalla de verificación muestra el correo destinatario, un campo numérico compatible con pegar, “Verificar y continuar”, “Cambiar correo” y “Reenviar código”. El código no se guarda en preferencias, registros ni analítica. Cambiar correo borra el código introducido. La app no genera ni valida códigos localmente.

El reenvío muestra una espera mínima de 60 segundos y respeta los límites adicionales del servidor. Código incorrecto, vencido, ya usado, sin conexión o límite excedido mantienen al usuario en la pantalla con una explicación y una salida. Los mensajes de envío no revelan si una cuenta existe. Solicitar un código no se presenta como una sesión iniciada.

Google y Apple utilizan sus flujos oficiales de autorización y validación mediante Auth; Noki no solicita la contraseña de esos servicios. Cancelar regresa al mismo contexto. No se fusionan cuentas por coincidencia de una dirección sin la validación del proveedor. Los botones de proveedor se habilitan solo después de configurar sus clientes, redirecciones y secretos de servidor y aprobar las pruebas. Los secretos nunca se incluyen en el paquete móvil.

El alta por correo exige un servicio de envío configurado, plantillas con el código, remitente autorizado, protección contra abuso y pruebas de entrega a varios dominios. Una compilación sin esos requisitos informa que el alta no está disponible y no presenta envíos ficticios. Las cuentas existentes conservan su método de acceso.

#### RF-57 — Bienvenida y entrada contextual

La primera apertura explica qué hace Noki, cómo se usa y para qué sirve en tres pasos: describir el estado, consultar orientación y lugares, guardar y continuar. Ofrece “Entrar o crear cuenta” como acción principal y “Explorar sin cuenta” como alternativa. No requiere cuenta para orientación, Aprende y directorio. La bienvenida se recuerda localmente; no se repite después de cada cambio de pantalla.

Cuando un visitante intenta guardar un aparato o preparar su entrega, aparece un contenedor fijo de acceso sobre el contexto actual, con transición breve. Cerrarlo conserva categoría, respuestas y orientación. Después de verificar el acceso y completar el nombre de usuario se muestra confirmación y se abre Inicio. La orientación pendiente se conserva en «Continuar con mi aparato», sin repetir preguntas ni crear registros duplicados. La consulta pública no equivale a acceso a datos privados.

#### RF-58 — Menú de accesibilidad

El menú desplegable de la barra superior ofrece Accesibilidad, Perfil y ajustes, Términos y privacidad y Acerca de Noki. Accesibilidad también está disponible desde Perfil y durante el acceso. Permite usar el tamaño del teléfono, aumentarlo a 115 % o 130 %, reforzar el contraste y reducir movimiento. Se respeta el mayor tamaño entre el ajuste del sistema y el de Noki; no se reduce un texto ampliado por el usuario. Los cambios son inmediatos y se guardan únicamente en este dispositivo.

Reducir movimiento elimina desplazamientos y animación de entrada, sin quitar la indicación visual de pulsación. El ajuste equivalente del sistema también se respeta. Restablecer afecta solo a preferencias de Noki, no a Android ni a los datos de la cuenta. Textos y botones deben expandirse en altura; no se superponen ni recortan para conservar una composición fija. Desde una escala de 130 % o en anchos menores a 360 dp, la navegación inferior distribuye sus cuatro destinos en dos filas legibles.

#### RF-59 — Identidad y respuesta visual

La apertura incluye el isotipo de Noki con aparición, escala, trazos orbitales y nodos de luz durante 1450 ms, sin destellos rápidos. No bloquea solicitudes de red ni se repite al volver desde una ficha. Se omite con movimiento reducido. Los componentes reutilizables de botones, tarjetas, opciones y avisos mantienen espaciado, foco, estados y lenguaje coherentes. Botones utilizan respuesta Material y una variación de escala discreta; no ejecutan acciones al presionar antes de soltar. Las pantallas utilizan márgenes laterales de 24 dp y separación suficiente según jerarquía; no imponen alturas fijas a texto de contenido.

El inicio describe la función real de Noki y ofrece una acción principal de orientación. Los títulos identifican una tarea o pregunta, no expresiones promocionales ambiguas. Las transiciones duran entre 260 y 380 ms, conservan sentido de avance y retroceso y no producen bucles entre orientación y contenido educativo.

#### RF-60 — Mapa sin conflicto con el desplazamiento

La vista previa del directorio no captura gestos de arrastre o zoom; la página puede desplazarse. “Ampliar y mover el mapa” abre una vista dedicada con Atrás visible. En ella los gestos controlan exclusivamente el mapa, no una página que lo contiene. Elegir punto de partida coloca una marca central y una acción de confirmación; mover el mapa no guarda ni envía la ubicación por sí solo. Confirmar regresa al directorio y conserva el origen para calcular una ruta.

La carga muestra un estado visible en lugar de un rectángulo negro sin explicación. Ante demora o fallo se ofrece reintentar y se mantiene accesible la lista. Las rutas solicitan consentimiento antes de compartir origen y destino con OSRM. Un cálculo real muestra distancia, duración estimada y trazado, sin afirmar que incluya tráfico en tiempo real ni navegación paso a paso.


#### RF-61 — Punto de partida por ubicación, mapa o dirección

La vista ampliada presenta «Elegir punto de partida», «Partir desde mi ubicación» y «Escribir dirección». Las mismas alternativas están disponibles en el directorio. Ninguna obtiene ubicación automáticamente al abrir la pantalla.

«Elegir punto de partida» activa una marca central; confirmar usa el centro visible y cancelar conserva la selección anterior. «Partir desde mi ubicación» explica el uso puntual antes de pedir el permiso de Android; se admite ubicación aproximada. Denegar, cancelar, desactivar la ubicación o agotar el tiempo de espera muestra una explicación y mantiene las alternativas manuales. No se accede a ubicación en segundo plano.

«Escribir dirección» abre un formulario con calle/carrera, número y ciudad, de 6 a 180 caracteres. La búsqueda se inicia únicamente al pulsar Buscar, no al escribir. Se informa que el texto se envía al servicio de direcciones de Android. Se muestran hasta cinco coincidencias, selección explícita y «Usar esta dirección». No se toma automáticamente la primera coincidencia ni se promete exactitud del predio. Se puede volver sin cambiar el punto. La ausencia del servicio, falta de red, cero coincidencias y espera superior a 20 segundos ofrecen recuperación manual.

Cambiar el punto de partida cancela el cálculo anterior, quita el trazado obsoleto y centra el mapa en la nueva selección. Calcular una ruta requiere volver a confirmar el envío a OSRM. Direcciones y coordenadas no se guardan en la cuenta ni en un historial local de búsqueda.

#### RF-62 — Apariencia clara y oscura

Accesibilidad permite elegir «Como el teléfono», «Claro» u «Oscuro» y recuerda la preferencia local. El tema oscuro utiliza fondo gris verdoso `#1B2420`, superficies `#242F29`, texto `#E7EDE5` y acento verde `#9CDD9D`; no se invierten los logos ni se usa negro puro como superficie general. Los controles, fichas, menús, avisos y formularios utilizan colores semánticos del tema. La cartografía conserva los colores del proveedor.

El cambio interpola colores durante 650 ms, sin reiniciar la pantalla, perder campos ni añadir una espera obligatoria. Reducir movimiento aplica el tema sin animación. El contraste reforzado funciona en ambos temas. Las barras del sistema mantienen iconos distinguibles.

#### RF-63 — Lectura, navegación inferior y orientación vertical

Las cuatro etiquetas inferiores permanecen centradas bajo sus iconos, con espacio equivalente de hasta dos líneas. «Mis aparatos» puede ocupar dos líneas completas, sin alineación irregular ni corte dentro de una palabra. Desde 130 % de escala o por debajo de 360 dp, se utiliza una disposición de dos por dos destinos, conservando el tamaño solicitado. El destino activo se distingue por color y semántica de selección.

En teléfonos Android la actividad solicita orientación vertical fija. No se cambian los ajustes globales de giro del dispositivo. En tabletas, equipos plegables y ventanas donde el sistema ignore esta solicitud, el contenido sigue siendo adaptable, desplazable y con ancho de lectura limitado; no se promete impedir una decisión del sistema operativo. Se respetan barras del sistema y teclado. Los textos se alinean al inicio, no se justifican forzando grandes huecos entre palabras; acciones breves se centran. Las separaciones responden a una escala de 4, 8, 12, 16 y 24 dp, sin añadir rellenos para simular contenido.

#### RF-64 — Decisión explícita sobre términos y privacidad

Los documentos se consultan sin cuenta desde el menú y desde Perfil. Identifican a Jeffry Giovanny Caro Arango y Luis Miguel Castañeda como responsables, con contacto en nokiappcorporation@gmail.com, fecha y versión. Explican finalidades, servicios externos, ubicación, derechos, conservación, solicitudes y límites reales de las funciones.

Antes de activar funciones personales se presentan dos casillas independientes y sin selección inicial: aceptación de términos y autorización del tratamiento necesario de datos. «Aceptar y continuar» se habilita solo con ambas. Leer los documentos y volver conserva la selección; rechazar o cerrar no equivale a aceptar, no ejecuta la operación pendiente y permite continuar con orientación, directorio y Aprende. No se ofrece una renuncia general de responsabilidad como alternativa.

La sesión restaurada sin aceptación vigente no carga registros personales hasta completar esta decisión. La constancia local se cifra y asocia al identificador de cuenta, versión y fecha del dispositivo. Existe además una constancia de servidor para cuentas, definida en RF-77; la local no la sustituye, no sustituye RLS y no es una firma digital. Para apertura pública sigue pendiente completar la revisión jurídica y operativa de documentos y tratamiento.

Retirar el permiso en este teléfono cierra sesión, bloquea funciones personales y conserva los registros existentes. La pantalla distingue esta acción de solicitar la revocación del tratamiento o supresión de la cuenta por el canal de contacto; nunca anuncia un borrado remoto que no haya realizado. Un cambio sustancial de versión requiere una nueva decisión.

## 6. Requisitos no funcionales

| ID | Requisito verificable |
| --- | --- |
| RNF-01 | Todo tráfico de producción debe utilizar HTTPS. |
| RNF-02 | Ninguna contraseña, service role key o token privado debe estar publicado en el repositorio o cliente Android. |
| RNF-03 | Las políticas RLS deben impedir que una cuenta lea, modifique o elimine dispositivos, checklist o aceptaciones legales de otra cuenta. |
| RNF-04 | Los identificadores de propietario deben obtenerse de la sesión validada, no de campos editables del cliente Android. |
| RNF-05 | Las operaciones sensibles de eliminación de cuenta deben ejecutarse en servidor con permisos mínimos necesarios. |
| RNF-06 | El texto principal usa 18 sp; texto auxiliar 16 sp; metadatos no esenciales 14 sp. La interfaz conserva todas las acciones con escala de fuente de Android al 200 %. |
| RNF-07 | Los controles táctiles tienen al menos 48 × 48 dp; los botones principales, altura mínima de 56 dp. Su altura crece cuando el texto ocupa más líneas. |
| RNF-08 | Ningún estado crítico debe comunicarse solo mediante color. |
| RNF-09 | Toda función esencial debe ser operable con teclado salvo la interacción gráfica específica del mapa; el directorio debe ofrecer alternativa textual al mapa. |
| RNF-10 | Compose debe exponer etiquetas, encabezados, selección y errores mediante Semantics para TalkBack. Los cambios relevantes se anuncian con LiveRegionMode.Polite. El foco sigue el orden visual. |
| RNF-11 | La app funciona sin desplazamiento horizontal involuntario en anchos de 320, 360, 393, 600 y 840 dp, en vertical y horizontal, con teclado abierto y respetando las barras del sistema. |
| RNF-12 | Las transiciones duran entre 120 y 240 ms y respetan la escala de animación del sistema, incluida la desactivación. No se utilizan animaciones obligatorias ni destellos. |
| RNF-13 | Las acciones que escriben datos deben mostrar estado de carga e impedir doble envío accidental. |
| RNF-14 | Una pérdida de respuesta no debe convertirse automáticamente en éxito; la app debe consultar o reintentar de forma segura. |
| RNF-15 | Los errores técnicos de Supabase, red, OSRM o cliente Android deben traducirse a mensajes comprensibles. |
| RNF-16 | El directorio debe mostrar fecha de verificación. Una ficha con más de 90 días sin revisión debe marcarse “Información pendiente de volver a verificar” hasta que el equipo la confirme. |
| RNF-17 | La geolocalización debe solicitarse únicamente después de una acción explícita y no almacenarse en la base de datos. |
| RNF-18 | Las fichas públicas no deben usar logotipos o lenguaje que sugieran convenio sin autorización correspondiente. |
| RNF-19 | Las descripciones introducidas por usuarios deben renderizarse como texto seguro, nunca como HTML ejecutable. |
| RNF-20 | Tokens y borradores privados se cifran mediante una clave de Android Keystore, en almacenamiento privado excluido de backups. No se guardan contraseñas ni datos privados en logs. |
| RNF-21 | Al cerrar sesión deben limpiarse datos privados temporales del cliente Android asociados a la cuenta. |
| RNF-22 | Debe verificarse instalación y funcionamiento en API 26, 30, 35 y 36, en teléfono físico y pruebas automatizadas. La falta de Google Play Services no debe impedir las funciones públicas. |
| RNF-23 | Si un proveedor cartográfico o de rutas falla, la aplicación debe conservar el directorio textual y ofrecer la dirección/enlace externo. |
| RNF-24 | No se anunciarán cifras de capacidad, impacto, usuarios o disponibilidad sin medición y fuente. |
| RNF-25 | El lenguaje debe estar escrito para público general: frases cortas, verbos directos y explicación de cualquier término técnico necesario. |
| RNF-26 | Las peticiones autenticadas a Supabase deben tener un límite de espera de 20 s; geolocalización 12 s; rutas OSRM 15 s. Al excederlo se muestra un error recuperable, nunca un estado de carga infinito. |

---

## 7. Reglas de negocio y estados

### 7.1 Reglas de cuenta

| ID | Regla |
| --- | --- |
| RN-01 | Cada cuenta de Auth corresponde a un único perfil Noki. |
| RN-02 | El correo es administrado por Supabase Auth y no se duplicará como fuente de verdad en `noki_profiles`. |
| RN-03 | Solo un correo verificado puede acceder a funciones privadas. |
| RN-04 | La aceptación de términos y privacidad es obligatoria para crear una cuenta utilizable. |
| RN-05 | El usuario solo puede modificar su propio perfil y sus propios registros. |
| RN-06 | Eliminar una cuenta elimina el acceso y sus datos privados definidos por esta versión. |

### 7.2 Reglas de orientación

| ID | Regla |
| --- | --- |
| RN-07 | La orientación no es diagnóstico y no debe afirmar que un equipo es seguro, reparable o reciclable con certeza. |
| RN-08 | Si existe condición visible de atención o incertidumbre, la recomendación preventiva prevalece sobre la pregunta de funcionamiento. |
| RN-09 | La orientación pública no se guarda automáticamente. |
| RN-10 | Guardar una orientación requiere sesión y crea o actualiza un dispositivo personal. |

### 7.3 Reglas del directorio

| ID | Regla |
| --- | --- |
| RN-11 | Un lugar solo se publica si tiene nombre, dirección, coordenadas, fuente y fecha de verificación. |
| RN-12 | Un lugar publicado no se considera aliado de Noki por estar en el directorio. |
| RN-13 | Seleccionar o guardar un lugar no crea reserva, cita ni aviso al establecimiento. |
| RN-14 | La disponibilidad final debe confirmarse directamente con el lugar. |
| RN-15 | La ubicación del usuario no se persiste. |

### 7.4 Reglas de dispositivos

| ID | Regla |
| --- | --- |
| RN-16 | Cada dispositivo pertenece exactamente a una cuenta. |
| RN-17 | Un dispositivo puede estar `active` o `completed`. |
| RN-18 | Una decisión representa intención; un resultado representa lo que el usuario declara haber hecho. |
| RN-19 | `delivered` exige confirmación del receptor tras validar el código de la entrega; no certifica reciclaje posterior. |
| RN-20 | Un lugar de referencia es opcional y puede cambiarse. |
| RN-21 | El checklist no obliga a realizar acciones inseguras o imposibles; cada ítem admite “No aplica”. |
| RN-22 | Si la condición requiere atención, la app no debe instruir al usuario a encender, cargar o desmontar el equipo. |
| RN-23 | El usuario puede eliminar sus registros personales; no existe un historial obligatorio perpetuo. |
| RN-24 | Noki no calcula peso, material recuperado ni beneficio ambiental a partir de la cantidad de dispositivos. |

### 7.5 Máquina de estados de un dispositivo

| Estado inicial | Acción | Estado final | Efecto |
| --- | --- | --- | --- |
| No existe | Crear dispositivo | `active` | Se crea registro personal. |
| `active` | Editar datos | `active` | Se actualiza recomendación si corresponde. |
| `active` | Cambiar decisión | `active` | Se registra nueva intención. |
| `active` | Guardar lugar de referencia | `active` | Se enlaza un lugar; no hay reserva. |
| `active` | Completar checklist | `active` | Se guardan estados de preparación. |
| `active` | Registrar resultado final | `completed` | Se guarda resultado y fecha. |
| `completed` | Corregir resultado | `completed` | Se actualiza declaración personal. |
| `active`, sin entrega activa | Retirar de pendientes | `archived` | Se conserva en historial como retirado, no entregado. |

---

## 8. Fuera de alcance

El alta de receptores y las entregas con recepción confirmada se regulan en la sección 20 y permanecen inactivas mientras no cumplan sus condiciones de activación. Quedan fuera de alcance:

- recepción o identidad presentadas como verificadas sin revisión real;
- puntos internos de operación sin respaldo real;
- reservas con establecimientos que solo son referencias del directorio;
- gestión de cupos o calendario avanzado;
- convenios automáticos con establecimientos;
- recolección domiciliaria;
- compra o venta de equipos;
- pagos;
- recompensas monetarias;
- servicio propio de reparación;
- diagnóstico técnico;
- certificación de reciclaje;
- certificado de disposición;
- pesaje de equipos;
- cálculo de CO₂, kilos reciclados o materiales recuperados;
- firma digital de un establecimiento;
- fotografías obligatorias del equipo;
- chat en tiempo real;
- notificaciones push;
- WhatsApp o SMS automáticos;
- panel administrativo dentro de Noki App;
- geolocalización continua;
- navegación giro a giro;
- tráfico en tiempo real;
- cartografía completamente offline;
- soporte multiciudad en esta versión;
- verificación de edad o residencia para consultar funciones públicas;
- videojuego u otras piezas transmedia.


---

## 9. Historias de usuario y criterios de aceptación

Las historias se mantienen deliberadamente pequeñas. Una historia describe una acción concreta. Sus criterios de aceptación forman parte de la misma historia y no deben separarse de ella.

### HU-HOME-01 — Comprender qué ofrece Noki

**Historia:** Como una persona que abre Noki por primera vez, quiero entender rápidamente para qué sirve la aplicación y acceder a una acción principal, para poder comenzar sin necesitar una explicación externa.

**Precondiciones:** La aplicación está disponible.

**Flujo principal:**
1. Abrir Inicio.
2. Leer propósito breve.
3. Elegir Orientación, Puntos, Aprende, Iniciar sesión o Crear cuenta.

**Datos implicados:** Ningún dato personal.

**Reglas asociadas:** RF-01, RF-03; RNF-25.

**Errores y recuperación:** Si un recurso secundario falla, Inicio debe seguir mostrando navegación básica.

**Resultado esperado:** La persona identifica las funciones principales sin referencias al contexto interno de desarrollo.

**Criterios de aceptación:**
- **CA-HOME-01-01:** Dado que abro Noki sin sesión, cuando se carga Inicio, entonces veo el propósito y las cinco entradas principales.
- **CA-HOME-01-02:** Dado que estoy en Inicio, cuando reviso el lenguaje de la interfaz, entonces no encuentro referencias al contexto interno de desarrollo ni a funciones que no sean reales.
- **CA-HOME-01-03:** Dado que uso teclado, cuando recorro los controles, entonces puedo alcanzar todos los enlaces principales con foco visible.

### HU-AUTH-01 — Crear una cuenta

**Historia:** Como una persona nueva, quiero registrarme con mi nombre, correo y contraseña, para guardar mis dispositivos y acceder a funciones personales.

**Precondiciones:** No existe una sesión iniciada.

**Flujo principal:**
1. Abrir Crear cuenta.
2. Completar nombre, correo, contraseña y confirmación.
3. Aceptar Términos y Privacidad.
4. Enviar formulario.
5. Recibir pantalla de verificación.

**Datos implicados:** Nombre, correo, contraseña solo durante envío, versiones legales aceptadas.

**Reglas asociadas:** RF-04, RF-05, RF-48; RN-01–RN-04.

**Errores y recuperación:** Errores de campos se muestran junto al campo; correo ya existente o límite de proveedor se explica sin borrar el resto del formulario.

**Resultado esperado:** Cuenta creada y pendiente de verificación de correo.

**Criterios de aceptación:**
- **CA-AUTH-01-01:** Dado que completo datos válidos y acepto ambos documentos, cuando pulso Crear cuenta, entonces el sistema crea la cuenta y muestra que debo verificar el correo.
- **CA-AUTH-01-02:** Dado que las contraseñas no coinciden, cuando intento enviar, entonces el registro no se envía y se indica el campo a corregir.
- **CA-AUTH-01-03:** Dado que no acepto Términos o Privacidad, cuando intento enviar, entonces el sistema bloquea el registro y explica qué falta.
- **CA-AUTH-01-04:** Dado que el correo ya está registrado, cuando intento crear otra cuenta, entonces la app muestra un mensaje comprensible y ofrece Iniciar sesión o Recuperar contraseña.

### HU-AUTH-02 — Verificar el correo

**Historia:** Como una persona recién registrada, quiero confirmar mi dirección de correo, para activar las funciones privadas de Noki.

**Precondiciones:** Existe una cuenta pendiente de verificación.

**Flujo principal:**
1. Abrir el correo recibido.
2. Pulsar el enlace válido.
3. Regresar a Noki.
4. Ver confirmación.
5. Continuar a sesión o panel.

**Datos implicados:** Token de verificación gestionado por Supabase.

**Reglas asociadas:** RF-05; RN-03.

**Errores y recuperación:** Enlace vencido o inválido ofrece reenvío, no un error técnico sin salida.

**Resultado esperado:** Correo verificado y cuenta habilitada.

**Criterios de aceptación:**
- **CA-AUTH-02-01:** Dado que mi enlace de verificación es válido, cuando lo abro, entonces Noki confirma la verificación y permite continuar.
- **CA-AUTH-02-02:** Dado que el enlace está vencido o ya no es válido, cuando lo abro, entonces veo una explicación y un botón para reenviar verificación.
- **CA-AUTH-02-03:** Dado que mi correo no está verificado, cuando intento abrir una función privada, entonces se me dirige a completar la verificación.

### HU-AUTH-03 — Reenviar verificación

**Historia:** Como una persona con cuenta no verificada, quiero solicitar otro correo de verificación, para recuperarme si no recibí o venció el anterior.

**Precondiciones:** Cuenta existente no verificada.

**Flujo principal:**
1. Abrir pantalla de verificación.
2. Pulsar Reenviar correo.
3. Esperar respuesta.
4. Leer confirmación.

**Datos implicados:** Correo de la cuenta.

**Reglas asociadas:** RF-06.

**Errores y recuperación:** Límite de envíos muestra espera/reintento sin múltiples solicitudes simultáneas.

**Resultado esperado:** Nueva solicitud de verificación enviada o error recuperable.

**Criterios de aceptación:**
- **CA-AUTH-03-01:** Dado que mi cuenta sigue sin verificar, cuando pulso Reenviar, entonces la app realiza una sola solicitud y confirma el envío.
- **CA-AUTH-03-02:** Dado que hay una solicitud en curso, cuando pulso repetidamente, entonces no se generan solicitudes simultáneas.
- **CA-AUTH-03-03:** Dado que el proveedor impone una espera, cuando solicito otro correo, entonces la app muestra un mensaje comprensible para reintentar después.

### HU-AUTH-04 — Iniciar sesión

**Historia:** Como una persona con cuenta verificada, quiero entrar con correo y contraseña, para acceder a mis datos privados.

**Precondiciones:** Cuenta verificada y conexión disponible.

**Flujo principal:**
1. Abrir Iniciar sesión.
2. Introducir correo y contraseña.
3. Enviar.
4. Validar sesión.
5. Cargar panel personal.

**Datos implicados:** Correo y contraseña durante autenticación; sesión resultante.

**Reglas asociadas:** RF-07, RF-08, RF-11.

**Errores y recuperación:** Credenciales incorrectas, cuenta no verificada, red o servicio no disponible tienen mensajes diferentes.

**Resultado esperado:** Sesión válida y panel personal visible.

**Criterios de aceptación:**
- **CA-AUTH-04-01:** Dado que mis credenciales son correctas y mi correo está verificado, cuando pulso Entrar, entonces accedo al panel con mi nombre.
- **CA-AUTH-04-02:** Dado que la contraseña es incorrecta, cuando pulso Entrar, entonces permanezco en Login y veo un mensaje claro sin borrar el correo.
- **CA-AUTH-04-03:** Dado que el correo no está verificado, cuando intento entrar, entonces la app me dirige a verificar o reenviar el correo.
- **CA-AUTH-04-04:** Dado que Supabase no responde, cuando intento entrar, entonces la app no culpa automáticamente a mi internet y ofrece reintentar.

### HU-AUTH-05 — Solicitar recuperación de contraseña

**Historia:** Como una persona que olvidó su contraseña, quiero pedir un enlace de recuperación, para recuperar el acceso sin intervención manual.

**Precondiciones:** No necesito una sesión iniciada.

**Flujo principal:**
1. Abrir Olvidé mi contraseña.
2. Escribir correo.
3. Enviar.
4. Leer mensaje neutral.

**Datos implicados:** Correo.

**Reglas asociadas:** RF-09.

**Errores y recuperación:** La app no confirma públicamente si el correo existe.

**Resultado esperado:** Solicitud enviada al servicio de autenticación.

**Criterios de aceptación:**
- **CA-AUTH-05-01:** Dado que escribo un correo con formato válido, cuando solicito recuperación, entonces veo el mensaje neutral definido en RF-09.
- **CA-AUTH-05-02:** Dado que escribo un correo con formato inválido, cuando intento enviar, entonces la app señala el formato y no realiza la solicitud.
- **CA-AUTH-05-03:** Dado que el correo no pertenece a una cuenta, cuando envío el formulario, entonces el mensaje visible sigue siendo neutral.

### HU-AUTH-06 — Establecer una nueva contraseña

**Historia:** Como una persona que abrió un enlace válido de recuperación, quiero definir una nueva contraseña, para volver a usar mi cuenta.

**Precondiciones:** Enlace de recuperación válido.

**Flujo principal:**
1. Abrir enlace.
2. Introducir nueva contraseña dos veces.
3. Enviar.
4. Esperar confirmación.
5. Continuar a Noki.

**Datos implicados:** Nueva contraseña gestionada por Auth.

**Reglas asociadas:** RF-10.

**Errores y recuperación:** Token inválido o vencido ofrece solicitar un nuevo enlace.

**Resultado esperado:** Contraseña actualizada.

**Criterios de aceptación:**
- **CA-AUTH-06-01:** Dado que el enlace es válido y las contraseñas coinciden, cuando confirmo el cambio, entonces la contraseña se actualiza y veo confirmación.
- **CA-AUTH-06-02:** Dado que las contraseñas no coinciden, cuando intento guardar, entonces la app bloquea el cambio y señala el error.
- **CA-AUTH-06-03:** Dado que el enlace venció, cuando intento cambiar la contraseña, entonces veo opción para solicitar otro enlace.

### HU-AUTH-07 — Cerrar sesión

**Historia:** Como un usuario autenticado, quiero cerrar mi sesión, para evitar que otra persona que use el dispositivo vea mis datos.

**Precondiciones:** Sesión válida.

**Flujo principal:**
1. Abrir menú/perfil.
2. Pulsar Cerrar sesión.
3. Limpiar contexto privado.
4. Volver a Inicio.

**Datos implicados:** Sesión y borradores locales de la cuenta.

**Reglas asociadas:** RF-12; RNF-20, RNF-21.

**Errores y recuperación:** Si el cierre remoto falla, el contexto local debe cerrarse y mostrarse aviso sin exponer datos.

**Resultado esperado:** No quedan vistas privadas visibles.

**Criterios de aceptación:**
- **CA-AUTH-07-01:** Dado que tengo una sesión activa, cuando pulso Cerrar sesión, entonces regreso a Inicio sin datos privados.
- **CA-AUTH-07-02:** Dado que existía un borrador local asociado a mi cuenta, cuando cierro sesión, entonces ese borrador se elimina del cliente Android.
- **CA-AUTH-07-03:** Dado que vuelvo con el botón Atrás, cuando después de cerrar sesión, entonces no puedo volver a una vista privada utilizable.

### HU-PRO-01 — Consultar mi perfil

**Historia:** Como un usuario autenticado, quiero ver la información principal de mi cuenta, para saber qué datos tiene Noki y administrarlos.

**Precondiciones:** Sesión válida.

**Flujo principal:**
1. Abrir Mi perfil.
2. Cargar perfil y datos de Auth.
3. Mostrar acciones de cuenta.

**Datos implicados:** Nombre, correo, verificación, fecha de cuenta.

**Reglas asociadas:** RF-41.

**Errores y recuperación:** Si falla la consulta, se muestra reintento sin datos de otra sesión.

**Resultado esperado:** Perfil visible.

**Criterios de aceptación:**
- **CA-PRO-01-01:** Dado que tengo sesión válida, cuando abro Mi perfil, entonces veo nombre, correo, estado de verificación y acciones de cuenta.
- **CA-PRO-01-02:** Dado que falla la carga, cuando abro Mi perfil, entonces veo un estado de error con Reintentar.

### HU-PRO-02 — Editar mi nombre

**Historia:** Como un usuario autenticado, quiero cambiar mi nombre visible, para mantener actualizado mi perfil.

**Precondiciones:** Perfil cargado.

**Flujo principal:**
1. Pulsar Editar nombre.
2. Modificar valor.
3. Guardar.
4. Esperar confirmación.

**Datos implicados:** Nombre completo.

**Reglas asociadas:** RF-42; RN-05.

**Errores y recuperación:** Valores vacíos o fuera de 2–80 caracteres no se envían.

**Resultado esperado:** Nombre actualizado en perfil y saludo.

**Criterios de aceptación:**
- **CA-PRO-02-01:** Dado que escribo un nombre válido, cuando guardo, entonces el servidor lo actualiza y la interfaz muestra el nuevo nombre.
- **CA-PRO-02-02:** Dado que dejo el nombre vacío, cuando intento guardar, entonces la app bloquea el envío y explica la regla.
- **CA-PRO-02-03:** Dado que otra cuenta intenta modificar mi perfil, cuando realiza una solicitud no autorizada, entonces el servidor la rechaza.

### HU-PRO-03 — Cambiar mi correo

**Historia:** Como un usuario autenticado, quiero actualizar el correo de mi cuenta, para seguir teniendo acceso con una dirección vigente.

**Precondiciones:** Sesión válida.

**Flujo principal:**
1. Abrir Cambiar correo.
2. Introducir nuevo correo.
3. Enviar solicitud.
4. Leer instrucciones.
5. Confirmar mediante el mecanismo de Auth.

**Datos implicados:** Nuevo correo.

**Reglas asociadas:** RF-43.

**Errores y recuperación:** Correo inválido, duplicado o confirmación vencida se comunica con recuperación.

**Resultado esperado:** Correo actualizado solo tras confirmación requerida.

**Criterios de aceptación:**
- **CA-PRO-03-01:** Dado que escribo un correo válido diferente, cuando solicito el cambio, entonces la app inicia el proceso y me indica que debo confirmarlo.
- **CA-PRO-03-02:** Dado que no completo la confirmación, cuando regreso a Noki, entonces la app no presenta el nuevo correo como definitivo.
- **CA-PRO-03-03:** Dado que el nuevo correo ya está asociado a otra cuenta, cuando intento cambiarlo, entonces el sistema rechaza el cambio con mensaje comprensible.

### HU-PRO-04 — Cambiar mi contraseña desde el perfil

**Historia:** Como un usuario autenticado, quiero establecer una contraseña nueva, para mantener segura mi cuenta.

**Precondiciones:** Sesión válida.

**Flujo principal:**
1. Abrir Cambiar contraseña.
2. Escribir nueva contraseña y confirmación.
3. Guardar.
4. Recibir confirmación.

**Datos implicados:** Nueva contraseña.

**Reglas asociadas:** RF-44.

**Errores y recuperación:** No coinciden o no cumplen longitud: validación local y del proveedor.

**Resultado esperado:** Contraseña actualizada.

**Criterios de aceptación:**
- **CA-PRO-04-01:** Dado que ambas contraseñas válidas coinciden, cuando guardo, entonces el sistema confirma el cambio.
- **CA-PRO-04-02:** Dado que no coinciden, cuando intento guardar, entonces no se envía el cambio.
- **CA-PRO-04-03:** Dado que la sesión dejó de ser válida, cuando intento cambiarla, entonces se solicita volver a iniciar sesión.

### HU-PRO-05 — Eliminar mi cuenta

**Historia:** Como un usuario autenticado, quiero eliminar mi cuenta y mis datos privados, para dejar de utilizar Noki y controlar mi información.

**Precondiciones:** Sesión válida y reautenticación disponible.

**Flujo principal:**
1. Abrir Eliminar cuenta.
2. Leer consecuencias.
3. Confirmar intención.
4. Reautenticar.
5. Ejecutar eliminación segura.
6. Cerrar sesión y volver a Inicio.

**Datos implicados:** Identidad Auth, perfil, dispositivos, checklist, aceptaciones asociadas.

**Reglas asociadas:** RF-45; RN-06.

**Errores y recuperación:** Si falla cualquier paso, no se muestra éxito y la cuenta permanece accesible según estado real.

**Resultado esperado:** Cuenta eliminada y datos privados del alcance eliminados.

**Criterios de aceptación:**
- **CA-PRO-05-01:** Dado que confirmo y me reautentico correctamente, cuando elimino la cuenta, entonces mis datos privados definidos se eliminan y regreso a Inicio.
- **CA-PRO-05-02:** Dado que cancelo en la confirmación, cuando salgo del diálogo, entonces no cambia ningún dato.
- **CA-PRO-05-03:** Dado que la reautenticación falla, cuando intento continuar, entonces la eliminación no se ejecuta.
- **CA-PRO-05-04:** Dado que la operación del servidor falla, cuando esperaba borrar la cuenta, entonces veo un error y no se informa falsamente que fue eliminada.

### HU-EDU-01 — Consultar información sobre RAEE

**Historia:** Como un visitante o usuario, quiero leer una explicación clara sobre RAEE y opciones de segunda vida, para comprender por qué no todo equipo en desuso debe tratarse igual.

**Precondiciones:** Ninguna.

**Flujo principal:**
1. Abrir Aprende.
2. Elegir un tema.
3. Leer contenido y fuentes.
4. Volver o continuar a Orientación/Puntos.

**Datos implicados:** Contenido público versionado.

**Reglas asociadas:** RF-13, RF-14.

**Errores y recuperación:** Si una fuente externa no abre, el contenido sigue disponible y la URL se conserva.

**Resultado esperado:** Información comprensible con fuente y fecha.

**Criterios de aceptación:**
- **CA-EDU-01-01:** Dado que abro Aprende, cuando consulto un tema, entonces veo contenido, fuente y fecha de revisión.
- **CA-EDU-01-02:** Dado que el contenido menciona una cifra, cuando la leo, entonces la misma pieza identifica fuente, año y territorio.
- **CA-EDU-01-03:** Dado que busco una afirmación de reciclaje garantizado, cuando leo Aprende, entonces no se promete un resultado que Noki no pueda comprobar.

### HU-ORI-01 — Iniciar la orientación

**Historia:** Como un visitante o usuario, quiero comenzar una guía sobre mi dispositivo, para obtener un siguiente paso sin registrarme obligatoriamente.

**Precondiciones:** Orientación disponible.

**Flujo principal:**
1. Abrir Orientación.
2. Leer alcance no diagnóstico.
3. Pulsar Comenzar.
4. Ver pregunta 1.

**Datos implicados:** Respuestas temporales.

**Reglas asociadas:** RF-15.

**Errores y recuperación:** Sin recursos suficientes offline se informa que debe conectarse una vez.

**Resultado esperado:** Cuestionario iniciado.

**Criterios de aceptación:**
- **CA-ORI-01-01:** Dado que no tengo cuenta, cuando inicio Orientación, entonces puedo responder sin registrarme.
- **CA-ORI-01-02:** Dado que inicio una nueva orientación, cuando pulso Comenzar, entonces no se reutilizan respuestas de una orientación anterior finalizada.

### HU-ORI-02 — Indicar el tipo de dispositivo

**Historia:** Como una persona en Orientación, quiero seleccionar qué tipo de equipo estoy evaluando, para contextualizar la recomendación.

**Precondiciones:** Orientación iniciada.

**Flujo principal:**
1. Leer categorías.
2. Elegir una.
3. Continuar a condición visible.

**Datos implicados:** Categoría.

**Reglas asociadas:** RF-16.

**Errores y recuperación:** Sin selección no se avanza.

**Resultado esperado:** Categoría registrada temporalmente.

**Criterios de aceptación:**
- **CA-ORI-02-01:** Dado que estoy en la primera pregunta, cuando elijo una categoría, entonces la app conserva la respuesta y avanza.
- **CA-ORI-02-02:** Dado que no selecciono nada, cuando intento continuar, entonces permanezco en la pregunta con indicación clara.

### HU-ORI-03 — Indicar una condición que requiere atención

**Historia:** Como una persona en Orientación, quiero decir si noto daño, hinchazón, fuga o calentamiento inusual, para evitar recomendaciones que me lleven a manipular un equipo potencialmente problemático.

**Precondiciones:** Categoría elegida.

**Flujo principal:**
1. Leer pregunta de condición.
2. Elegir Sí, No o No estoy seguro.
3. Aplicar bifurcación.

**Datos implicados:** Condición de atención.

**Reglas asociadas:** RF-17; RN-08, RN-22.

**Errores y recuperación:** Sí o No estoy seguro omite la pregunta de funcionamiento y muestra recomendación preventiva.

**Resultado esperado:** Ruta de orientación coherente.

**Criterios de aceptación:**
- **CA-ORI-03-01:** Dado que selecciono Sí, cuando respondo, entonces veo recomendación preventiva sin que se me pida encender o cargar el equipo.
- **CA-ORI-03-02:** Dado que selecciono No estoy seguro, cuando respondo, entonces recibo la misma prioridad preventiva.
- **CA-ORI-03-03:** Dado que selecciono No he notado algo así, cuando respondo, entonces avanzo a la pregunta de funcionamiento.

### HU-ORI-04 — Indicar si el dispositivo funciona

**Historia:** Como una persona sin condición visible de atención, quiero declarar si el equipo funciona, falla o no lo sé, para recibir una recomendación más adecuada.

**Precondiciones:** RF-17 respondió sin condición visible.

**Flujo principal:**
1. Leer pregunta.
2. Elegir una opción.
3. Generar resultado.

**Datos implicados:** Estado funcional.

**Reglas asociadas:** RF-18, RF-19.

**Errores y recuperación:** La app no exige pruebas técnicas para responder.

**Resultado esperado:** Resultado SECOND_LIFE o REPAIR_REVIEW.

**Criterios de aceptación:**
- **CA-ORI-04-01:** Dado que indico que funciona, cuando respondo, entonces obtengo la recomendación de segunda vida.
- **CA-ORI-04-02:** Dado que indico que falla, cuando respondo, entonces obtengo la recomendación de revisión/reparación.
- **CA-ORI-04-03:** Dado que indico que no sé, cuando respondo, entonces obtengo la recomendación de revisión/reparación.

### HU-ORI-05 — Comprender el resultado de orientación

**Historia:** Como una persona que terminó las preguntas, quiero ver una recomendación y sus siguientes pasos, para decidir qué hacer sin confundir la guía con un diagnóstico.

**Precondiciones:** Respuestas suficientes.

**Flujo principal:**
1. Mostrar título de resultado.
2. Explicar motivo.
3. Mostrar acciones siguientes.
4. Mostrar nota no diagnóstica.

**Datos implicados:** Código de recomendación y respuestas temporales.

**Reglas asociadas:** RF-19.

**Errores y recuperación:** El resultado nunca afirma certeza técnica.

**Resultado esperado:** Recomendación clara.

**Criterios de aceptación:**
- **CA-ORI-05-01:** Dado que termino el cuestionario, cuando veo el resultado, entonces encuentro título, explicación, acciones y aviso no diagnóstico.
- **CA-ORI-05-02:** Dado que soy visitante, cuando quiero conservarlo, entonces veo Crear cuenta para guardar.
- **CA-ORI-05-03:** Dado que tengo sesión, cuando quiero conservarlo, entonces veo Guardar en Mis dispositivos.

### HU-ORI-06 — Cambiar una respuesta de orientación

**Historia:** Como una persona en el cuestionario, quiero volver y corregir una respuesta, para obtener un resultado basado en información actualizada.

**Precondiciones:** Existe al menos una respuesta.

**Flujo principal:**
1. Pulsar Anterior o Cambiar respuestas.
2. Modificar selección.
3. Descartar respuestas posteriores incompatibles.
4. Continuar.

**Datos implicados:** Respuestas temporales.

**Reglas asociadas:** RF-20.

**Errores y recuperación:** No se mezclan respuestas de ramas anteriores.

**Resultado esperado:** Resultado recalculado.

**Criterios de aceptación:**
- **CA-ORI-06-01:** Dado que vuelvo a condición visible y cambio No por Sí, cuando continúo, entonces la antigua respuesta de funcionamiento se descarta.
- **CA-ORI-06-02:** Dado que cambio una respuesta, cuando termino orientación, entonces el resultado corresponde solo a las respuestas vigentes.

### HU-ORI-07 — Guardar orientación en Mis dispositivos

**Historia:** Como un usuario autenticado, quiero guardar el resultado como un dispositivo personal, para continuar el seguimiento más adelante.

**Precondiciones:** Orientación terminada y sesión válida.

**Flujo principal:**
1. Pulsar Guardar.
2. Completar descripción y campos opcionales.
3. Confirmar.
4. Crear dispositivo.
5. Abrir detalle.

**Datos implicados:** Categoría, descripción, marca, modelo, condición, funcionamiento, recomendación.

**Reglas asociadas:** RF-21, RF-30.

**Errores y recuperación:** Sin conexión se conserva borrador pero no se afirma que fue guardado.

**Resultado esperado:** Nuevo dispositivo activo.

**Criterios de aceptación:**
- **CA-ORI-07-01:** Dado que completo descripción válida, cuando guardo el resultado, entonces se crea un dispositivo con las respuestas y recomendación.
- **CA-ORI-07-02:** Dado que la descripción está vacía, cuando intento guardar, entonces la app solicita completarla.
- **CA-ORI-07-03:** Dado que se pierde la conexión antes de confirmar, cuando intento guardar, entonces no aparece éxito y puedo reintentar con el borrador.

### HU-DIR-01 — Consultar el directorio

**Historia:** Como un visitante o usuario, quiero ver lugares de recolección con información verificable, para identificar opciones locales sin asumir que son aliados de Noki.

**Precondiciones:** Directorio disponible.

**Flujo principal:**
1. Abrir Puntos de recolección.
2. Cargar fichas activas.
3. Ver lista y mapa.
4. Leer aviso de alcance.

**Datos implicados:** Datos públicos de `noki_collection_points`.

**Reglas asociadas:** RF-22, RF-23.

**Errores y recuperación:** Si Supabase falla y existe copia pública, se muestra con fecha; si no, estado de error.

**Resultado esperado:** Directorio consultable.

**Criterios de aceptación:**
- **CA-DIR-01-01:** Dado que existen fichas activas, cuando abro Directorio, entonces veo lista, mapa y aviso de alcance.
- **CA-DIR-01-02:** Dado que una ficha no tiene fuente o fecha, cuando se consulta el catálogo publicado, entonces esa ficha no se presenta como publicación válida.
- **CA-DIR-01-03:** Dado que leo una ficha, cuando busco lenguaje de alianza, entonces no se afirma convenio si no existe.

### HU-DIR-02 — Buscar y filtrar lugares

**Historia:** Como una persona en el directorio, quiero reducir la lista por nombre, dirección o categoría, para encontrar opciones relevantes más rápido.

**Precondiciones:** Fichas cargadas.

**Flujo principal:**
1. Escribir búsqueda o elegir filtro.
2. Actualizar resultados.
3. Limpiar cuando quiera.

**Datos implicados:** Texto de búsqueda y filtro local.

**Reglas asociadas:** RF-24.

**Errores y recuperación:** Cero resultados ofrece limpiar filtros.

**Resultado esperado:** Lista filtrada.

**Criterios de aceptación:**
- **CA-DIR-02-01:** Dado que escribo parte del nombre o dirección, cuando busco, entonces solo veo coincidencias relevantes.
- **CA-DIR-02-02:** Dado que elijo una categoría, cuando aplico el filtro, entonces solo veo fichas que declaran esa categoría.
- **CA-DIR-02-03:** Dado que no hay coincidencias, cuando aplico filtros, entonces veo un estado vacío y Limpiar filtros.

### HU-DIR-03 — Revisar el detalle y la fuente de un lugar

**Historia:** Como una persona interesada en un punto, quiero consultar datos y fuente antes de desplazarme, para poder verificar la información por mi cuenta.

**Precondiciones:** Lugar seleccionado.

**Flujo principal:**
1. Abrir detalle.
2. Leer dirección, horarios, categorías y contacto.
3. Ver fecha.
4. Abrir fuente si lo deseo.

**Datos implicados:** Ficha pública.

**Reglas asociadas:** RF-22, RF-23.

**Errores y recuperación:** Campos no disponibles se muestran como “No informado por la fuente”, no se inventan.

**Resultado esperado:** Información verificable.

**Criterios de aceptación:**
- **CA-DIR-03-01:** Dado que abro un lugar, cuando veo su detalle, entonces encuentro fuente y fecha de última verificación.
- **CA-DIR-03-02:** Dado que la fuente no publica un horario, cuando veo el detalle, entonces la app indica que no está informado en lugar de inventarlo.
- **CA-DIR-03-03:** Dado que pulso la fuente, cuando navego, entonces se abre el enlace público correspondiente.

### HU-DIR-04 — Usar mi ubicación para una ruta

**Historia:** Como una persona en el directorio, quiero compartir mi ubicación solo cuando yo lo decida, para calcular una ruta desde donde estoy.

**Precondiciones:** Lugar seleccionado y Android con servicio de ubicación.

**Flujo principal:**
1. Pulsar Ruta desde mi ubicación.
2. Aceptar o rechazar permiso.
3. Si acepta, obtener coordenadas.
4. Calcular ruta.

**Datos implicados:** Coordenadas temporales.

**Reglas asociadas:** RF-26, RF-27; RN-15.

**Errores y recuperación:** Permiso denegado ofrece origen manual/enlace externo.

**Resultado esperado:** Ruta o alternativa.

**Criterios de aceptación:**
- **CA-DIR-04-01:** Dado que solo estoy navegando el directorio, cuando no pulso Ruta desde mi ubicación, entonces el cliente Android no solicita geolocalización.
- **CA-DIR-04-02:** Dado que acepto permiso, cuando solicito la ruta, entonces las coordenadas se usan temporalmente y no se guardan en Supabase.
- **CA-DIR-04-03:** Dado que rechazo permiso, cuando solicito ruta, entonces veo opciones de origen manual o servicio externo.

### HU-DIR-05 — Consultar una ruta estimada

**Historia:** Como una persona con origen y destino, quiero ver distancia y tiempo aproximados en carro, para planificar mi desplazamiento.

**Precondiciones:** Origen y lugar seleccionados.

**Flujo principal:**
1. Enviar coordenadas a OSRM.
2. Recibir geometría.
3. Dibujar ruta.
4. Mostrar distancia y duración.

**Datos implicados:** Origen/destino temporales.

**Reglas asociadas:** RF-27.

**Errores y recuperación:** Timeout o fallo de OSRM ofrece enlace externo.

**Resultado esperado:** Ruta estimada visible.

**Criterios de aceptación:**
- **CA-DIR-05-01:** Dado que OSRM responde, cuando calculo ruta, entonces veo línea, distancia y tiempo aproximado.
- **CA-DIR-05-02:** Dado que OSRM falla, cuando calculo ruta, entonces la app conserva dirección y ofrece abrir servicio externo.
- **CA-DIR-05-03:** Dado que veo una duración, cuando la interpreto, entonces la interfaz la presenta como estimación y no como tiempo garantizado.

### HU-DIR-06 — Guardar un lugar como referencia

**Historia:** Como un usuario autenticado con un dispositivo, quiero asociar un lugar del directorio a mi dispositivo, para recordar dónde pienso consultar o llevarlo.

**Precondiciones:** Sesión válida y dispositivo propio.

**Flujo principal:**
1. Abrir lugar o detalle del dispositivo.
2. Pulsar Guardar como lugar de referencia.
3. Elegir dispositivo si es necesario.
4. Confirmar.

**Datos implicados:** device.selected_point_id.

**Reglas asociadas:** RF-28; RN-13.

**Errores y recuperación:** Un lugar inactivo previamente guardado permanece como referencia histórica pero se advierte que debe verificarse; no se crea reserva.

**Resultado esperado:** Referencia guardada.

**Criterios de aceptación:**
- **CA-DIR-06-01:** Dado que elijo un lugar y un dispositivo propio, cuando guardo referencia, entonces el dispositivo muestra ese lugar.
- **CA-DIR-06-02:** Dado que guardo un lugar, cuando reviso mensajes, entonces ningún mensaje afirma que existe reserva o aviso al establecimiento.
- **CA-DIR-06-03:** Dado que cambio de opinión, cuando quito o sustituyo la referencia, entonces el dispositivo refleja la nueva selección.

### HU-DEV-01 — Crear un dispositivo manualmente

**Historia:** Como un usuario autenticado, quiero registrar un dispositivo sin pasar por Orientación, para hacer seguimiento de un equipo que ya conozco.

**Precondiciones:** Sesión válida.

**Flujo principal:**
1. Abrir Mis dispositivos.
2. Pulsar Añadir dispositivo.
3. Completar campos.
4. Guardar.
5. Abrir detalle.

**Datos implicados:** Categoría, descripción, marca/modelo opcionales, funcionamiento, condición.

**Reglas asociadas:** RF-30, RF-35.

**Errores y recuperación:** Campos inválidos conservan datos y muestran error.

**Resultado esperado:** Dispositivo activo creado.

**Criterios de aceptación:**
- **CA-DEV-01-01:** Dado que completo campos válidos, cuando pulso Guardar, entonces se crea un registro solo para mi cuenta.
- **CA-DEV-01-02:** Dado que falta categoría o descripción, cuando intento guardar, entonces la app bloquea el envío y señala el campo.
- **CA-DEV-01-03:** Dado que marco una condición de atención, cuando guardo, entonces el detalle muestra recomendación preventiva.

### HU-DEV-02 — Ver mis dispositivos

**Historia:** Como un usuario autenticado, quiero consultar mis registros activos, para retomar decisiones pendientes.

**Precondiciones:** Sesión válida.

**Flujo principal:**
1. Abrir Mis dispositivos.
2. Cargar registros propios.
3. Ver Activos.
4. Elegir uno.

**Datos implicados:** Dispositivos propios.

**Reglas asociadas:** RF-29.

**Errores y recuperación:** Cero registros muestra CTA para orientar o añadir.

**Resultado esperado:** Lista privada visible.

**Criterios de aceptación:**
- **CA-DEV-02-01:** Dado que tengo dispositivos activos, cuando abro Mis dispositivos, entonces veo solo mis registros.
- **CA-DEV-02-02:** Dado que no tengo dispositivos, cuando abro la sección, entonces veo un estado vacío con Orientar y Añadir dispositivo.
- **CA-DEV-02-03:** Dado que otra cuenta tiene registros, cuando abro mi lista, entonces no aparecen datos de esa cuenta.

### HU-DEV-03 — Editar un dispositivo

**Historia:** Como un usuario autenticado, quiero corregir los datos de un dispositivo activo, para mantener mi información actualizada.

**Precondiciones:** Dispositivo propio existente.

**Flujo principal:**
1. Abrir detalle.
2. Pulsar Editar.
3. Modificar campos.
4. Guardar.
5. Recalcular recomendación.

**Datos implicados:** Campos de RF-31.

**Reglas asociadas:** RF-31, RF-35.

**Errores y recuperación:** Conflicto o error conserva formulario y permite recargar.

**Resultado esperado:** Datos actualizados.

**Criterios de aceptación:**
- **CA-DEV-03-01:** Dado que modifico un campo válido, cuando guardo, entonces el detalle muestra el nuevo valor.
- **CA-DEV-03-02:** Dado que cambio condición o funcionamiento, cuando guardo, entonces la recomendación se recalcula con las reglas vigentes.
- **CA-DEV-03-03:** Dado que intento editar un dispositivo ajeno, cuando envío una solicitud, entonces RLS la rechaza.

### HU-DEV-04 — Retirar un pendiente

**Historia:** Como un usuario autenticado, quiero retirar un aparato que no voy a preparar, para mantener ordenados mis pendientes sin confundirlo con una entrega realizada.

**Precondiciones:** Dispositivo propio.

**Flujo principal:**
1. Pulsar Retirar de pendientes.
2. Leer advertencia.
3. Confirmar.
4. Esperar servidor.
5. Regresar a lista.

**Datos implicados:** Dispositivo y checklist.

**Reglas asociadas:** RF-32; RN-23.

**Errores y recuperación:** Cancelar no hace cambios; fallo no muestra éxito.

**Resultado esperado:** Registro conservado en Historial como retirado, sin entrega.

**Criterios de aceptación:**
- **CA-DEV-04-01:** Dado que confirmo la retirada de un pendiente propio sin entrega activa, cuando el servidor responde correctamente, deja Pendientes y aparece en Historial como retirado, sin resultado de entrega.
- **CA-DEV-04-02:** Dado que cancelo, cuando cierro la confirmación, entonces el registro permanece.
- **CA-DEV-04-03:** Dado que falla la solicitud o hay conflicto de versión, el registro no se presenta como retirado y puedo actualizar o reintentar. Los aparatos archivados no se editan ni se incluyen en otra entrega.

### HU-DEV-05 — Consultar el detalle de un dispositivo

**Historia:** Como un usuario autenticado, quiero ver toda la información relevante en un solo lugar, para saber qué me falta por decidir o preparar.

**Precondiciones:** Dispositivo propio.

**Flujo principal:**
1. Abrir tarjeta.
2. Cargar detalle.
3. Mostrar datos, recomendación, decisión, lugar, checklist y resultado según corresponda.

**Datos implicados:** Registro y checklist.

**Reglas asociadas:** RF-33.

**Errores y recuperación:** Campos opcionales vacíos no generan valores ficticios.

**Resultado esperado:** Detalle completo y accionable.

**Criterios de aceptación:**
- **CA-DEV-05-01:** Dado que abro un dispositivo activo, cuando veo detalle, entonces encuentro recomendación, decisión, lugar y preparación.
- **CA-DEV-05-02:** Dado que el dispositivo está completado, cuando veo detalle, entonces encuentro resultado y fecha además de sus datos.

### HU-DEV-06 — Elegir qué quiero hacer con un dispositivo

**Historia:** Como un usuario autenticado, quiero guardar mi decisión actual, para organizar el siguiente paso.

**Precondiciones:** Dispositivo propio activo.

**Flujo principal:**
1. Abrir Decisión.
2. Elegir una de cinco opciones.
3. Guardar.
4. Actualizar acciones sugeridas.

**Datos implicados:** decision.

**Reglas asociadas:** RF-34.

**Errores y recuperación:** La app no confunde intención con acción completada.

**Resultado esperado:** Decisión almacenada.

**Criterios de aceptación:**
- **CA-DEV-06-01:** Dado que elijo Llevarlo a un lugar de recolección, cuando guardo, entonces el dispositivo sigue activo y se me ofrece consultar/guardar un lugar.
- **CA-DEV-06-02:** Dado que cambio de decisión, cuando guardo otra opción, entonces se reemplaza la intención anterior sin crear un resultado final.
- **CA-DEV-06-03:** Dado que solo he guardado una decisión, cuando veo historial, entonces el dispositivo todavía no aparece como completado.

### HU-DEV-07 — Preparar un dispositivo antes de moverlo o entregarlo

**Historia:** Como un usuario autenticado, quiero seguir una lista sencilla de preparación, para proteger mis datos y evitar acciones innecesarias.

**Precondiciones:** Dispositivo propio con una decisión que implique transferencia o entrega.

**Flujo principal:**
1. Abrir Preparación.
2. Revisar cinco ítems.
3. Marcar Pendiente, Hecho o No aplica.
4. Guardar cambios.

**Datos implicados:** Checklist por dispositivo.

**Reglas asociadas:** RF-36, RF-37.

**Errores y recuperación:** Condición de atención activa muestra aviso y no obliga a encender/cargar.

**Resultado esperado:** Checklist sincronizado.

**Criterios de aceptación:**
- **CA-DEV-07-01:** Dado que marco un ítem como Hecho, cuando guardo, entonces el estado se conserva al volver a entrar.
- **CA-DEV-07-02:** Dado que un ítem no aplica, cuando selecciono No aplica, entonces puedo continuar sin marcarlo falsamente como hecho.
- **CA-DEV-07-03:** Dado que el dispositivo tiene condición de atención, cuando abro Preparación, entonces veo el aviso de no forzar encendido, carga o desmontaje.

### HU-DEV-08 — Registrar lo que finalmente hice

**Historia:** Como un usuario autenticado, quiero marcar un resultado final personal, para conservar un historial de mis decisiones.

**Precondiciones:** Dispositivo propio activo.

**Flujo principal:**
1. Abrir Marcar como completado.
2. Elegir resultado.
3. Si delivered, comprobar que la entrega tenga la confirmación del receptor.
4. Leer el alcance del resultado y su constancia.
5. Confirmar.

**Datos implicados:** outcome, completed_at, outcome_point_id opcional.

**Reglas asociadas:** RF-38; RN-19.

**Errores y recuperación:** La falta de confirmación del receptor tras validar el código bloquea recolección; conservar o reparar no requiere inventar una entrega.

**Resultado esperado:** Dispositivo completed.

**Criterios de aceptación:**
- **CA-DEV-08-01:** Dado que elijo un resultado y confirmo, cuando el servidor guarda, entonces el dispositivo pasa al Historial.
- **CA-DEV-08-02:** Dado que falta la confirmación del receptor o del emisor, cuando intento registrar recolección, entonces interfaz y servidor bloquean el resultado.
- **CA-DEV-08-03:** Dado que el receptor asignado validó el código y confirmó la recepción, cuando guardo el resultado, entonces queda vinculado a esa constancia sin atribuir recepción a terceros.

### HU-DEV-09 — Corregir un resultado personal

**Historia:** Como un usuario autenticado, quiero modificar un resultado que registré por error, para mantener mi historial personal correcto.

**Precondiciones:** Dispositivo propio completado mediante un resultado personal, sin constancia de recepción de entrega.

**Flujo principal:**
1. Abrir Historial.
2. Abrir dispositivo.
3. Pulsar Corregir resultado.
4. Elegir opción válida.
5. Guardar.

**Datos implicados:** outcome y referencia opcional.

**Reglas asociadas:** RF-39.

**Errores y recuperación:** No se genera una falsa auditoría de terceros.

**Resultado esperado:** Resultado actualizado.

**Criterios de aceptación:**
- **CA-DEV-09-01:** Dado que registré un resultado incorrecto, cuando lo corrijo, entonces el historial muestra el nuevo resultado.
- **CA-DEV-09-02:** Dado que el aparato tiene una constancia de recepción, cuando intento reabrir o alterar sus datos de entrega, entonces la operación se bloquea y se ofrece contacto de soporte.

### HU-HIS-01 — Consultar mi historial

**Historia:** Como un usuario autenticado, quiero ver dispositivos que marqué como completados, para recordar qué decisión tomé.

**Precondiciones:** Sesión válida.

**Flujo principal:**
1. Abrir Mis dispositivos.
2. Cambiar a Historial.
3. Cargar completados por fecha descendente.
4. Abrir detalle si deseo.

**Datos implicados:** Dispositivos completed.

**Reglas asociadas:** RF-40.

**Errores y recuperación:** Sin datos muestra estado vacío.

**Resultado esperado:** Historial personal visible.

**Criterios de aceptación:**
- **CA-HIS-01-01:** Dado que tengo registros completados, cuando abro Historial, entonces los veo ordenados por fecha más reciente.
- **CA-HIS-01-02:** Dado que no tengo registros completados, cuando abro Historial, entonces veo un estado vacío sin datos inventados.
- **CA-HIS-01-03:** Dado que consulto una entrada delivered, entonces la interfaz permite identificar su constancia y aclara que no certifica reciclaje.

### HU-HIS-02 — Buscar y filtrar mi historial

**Historia:** Como un usuario autenticado con historial, quiero encontrar un dispositivo completado, para consultar registros sin recorrer toda la lista.

**Precondiciones:** Existen registros completados.

**Flujo principal:**
1. Escribir texto o elegir resultado.
2. Actualizar lista.
3. Limpiar filtros.

**Datos implicados:** Búsqueda/filtro en datos propios.

**Reglas asociadas:** RF-40.

**Errores y recuperación:** Cero coincidencias ofrece limpiar.

**Resultado esperado:** Historial filtrado.

**Criterios de aceptación:**
- **CA-HIS-02-01:** Dado que escribo parte de la descripción, cuando busco, entonces veo coincidencias propias.
- **CA-HIS-02-02:** Dado que filtro por resultado, cuando aplico, entonces solo veo registros con ese resultado.
- **CA-HIS-02-03:** Dado que no hay coincidencias, cuando filtro, entonces veo estado vacío y Limpiar filtros.


### HU-HOME-02 — Usar mi panel personal

**Historia:** Como usuario autenticado, quiero ver un resumen de mis acciones y dispositivos recientes, para continuar rápidamente lo que estaba haciendo.

**Precondiciones:** Sesión válida y correo verificado.

**Flujo principal:**
1. Abrir Inicio con sesión.
2. Cargar nombre y hasta tres dispositivos activos recientes.
3. Mostrar accesos a Mis dispositivos, Orientación, Puntos, Aprende y Perfil.
4. Elegir una acción.

**Datos implicados:** Nombre de perfil y resumen de dispositivos propios.

**Reglas asociadas:** RF-02, RF-03; RN-05.

**Errores y recuperación:** Si falla la consulta de dispositivos, el panel conserva la navegación y ofrece Reintentar sin mostrar datos de otra cuenta.

**Resultado esperado:** Panel personal útil y privado.

**Criterios de aceptación:**
- **CA-HOME-02-01:** Dado que tengo sesión válida, cuando abro Inicio, entonces veo mi nombre y las acciones privadas disponibles.
- **CA-HOME-02-02:** Dado que tengo más de tres dispositivos activos, cuando abro Inicio, entonces veo como máximo tres recientes y un enlace “Ver todos”.
- **CA-HOME-02-03:** Dado que falla la carga del resumen, cuando se muestra el panel, entonces sigo pudiendo navegar y aparece una opción para reintentar.

### HU-LEGAL-01 — Consultar la Política de privacidad

**Historia:** Como visitante o usuario, quiero leer cómo Noki trata mis datos, para decidir informadamente si quiero crear o mantener una cuenta.

**Precondiciones:** Ninguna.

**Flujo principal:**
1. Abrir Política de privacidad desde Registro, Perfil o pie de página.
2. Leer categorías de datos, finalidad, servicios externos y eliminación.
3. Volver a la pantalla anterior.

**Datos implicados:** Documento público versionado.

**Reglas asociadas:** RF-46, RF-48.

**Errores y recuperación:** El documento debe formar parte de los recursos públicos de la app; si un enlace externo citado falla, el texto de la política sigue visible.

**Resultado esperado:** Política accesible antes y después del registro.

**Criterios de aceptación:**
- **CA-LEGAL-01-01:** Dado que estoy en Registro, cuando abro Política de privacidad, entonces puedo leerla sin crear una cuenta.
- **CA-LEGAL-01-02:** Dado que leo la política, cuando busco los datos que Noki solicita, entonces encuentro finalidad, almacenamiento, terceros, geolocalización y eliminación de cuenta.
- **CA-LEGAL-01-03:** Dado que termino de leer, cuando vuelvo, entonces regreso al contexto anterior sin perder los campos ya escritos del registro mientras continúe la misma sesión de formulario.

### HU-LEGAL-02 — Consultar los Términos de uso

**Historia:** Como visitante o usuario, quiero leer los límites y condiciones de Noki, para entender qué puede y qué no puede garantizar la aplicación.

**Precondiciones:** Ninguna.

**Flujo principal:**
1. Abrir Términos desde Registro, Perfil o pie de página.
2. Leer alcance de Orientación, Directorio y registros personales.
3. Volver a la pantalla anterior.

**Datos implicados:** Documento público versionado.

**Reglas asociadas:** RF-47, RF-48.

**Errores y recuperación:** El documento debe estar disponible como recurso público de la app.

**Resultado esperado:** Límites del servicio comprensibles.

**Criterios de aceptación:**
- **CA-LEGAL-02-01:** Dado que abro Términos, cuando leo el alcance de Orientación, entonces se indica que no es diagnóstico técnico.
- **CA-LEGAL-02-02:** Dado que leo el apartado del directorio, cuando reviso su alcance, entonces queda claro que una ficha no crea convenio, reserva ni garantía de aceptación.
- **CA-LEGAL-02-03:** Dado que leo el apartado de historial, cuando reviso un resultado personal, entonces queda claro que es una declaración del usuario.

### HU-AND-01 — Instalar y abrir Noki en Android

**Historia:** Como usuario de Android, quiero instalar Noki y abrirla desde su icono, para usarla como una aplicación de mi teléfono.

**Precondiciones:** Android API 26 o superior, almacenamiento suficiente y paquete firmado.

**Flujo principal:**
1. Obtener el paquete desde el canal de distribución de Noki.
2. Confirmar la instalación en Android.
3. Abrir el icono Noki.
4. Consultar Inicio y sus funciones públicas.

**Datos implicados:** Identificador, firma, versionCode, versionName e icono adaptativo.

**Reglas asociadas:** RF-49; RNF-11 y RNF-22.

**Errores y recuperación:** Android informa incompatibilidad, firma incorrecta o falta de espacio; no se pide desactivar Play Protect ni otras protecciones del teléfono.

**Resultado esperado:** App nativa instalada y accesible desde el lanzador.

**Criterios de aceptación:**
- **CA-AND-01-01:** Dado un teléfono compatible, cuando confirmo la instalación, entonces aparece el icono Noki y abre Inicio sin navegador.
- **CA-AND-01-02:** Dado un lanzador con máscara circular o cuadrada, cuando veo Noki, entonces el símbolo queda centrado y sin recortes.
- **CA-AND-01-03:** Dado que abro Noki sin cuenta ni conexión, cuando consulto Inicio u Orientación, entonces puedo usar ambos módulos nativos.

### HU-OFF-01 — Consultar contenido público previamente cargado sin conexión

**Historia:** Como usuario con conexión intermitente, quiero volver a consultar contenido público ya cargado, para no perder completamente el acceso cuando me quede sin red.

**Precondiciones:** Aplicación instalada. Para consultar el directorio sin red debe existir una descarga pública anterior.

**Flujo principal:**
1. Instalar Noki y, cuando exista conexión, consultar el directorio para conservar su última copia pública.
2. Perder conexión.
3. Abrir contenido público almacenado.
4. Ver un aviso cuando un dato pueda estar desactualizado.

**Datos implicados:** Recursos incluidos en el APK y copia pública local del directorio con fecha de descarga.

**Reglas asociadas:** RF-50, RF-51.

**Errores y recuperación:** Mapas, rutas y escrituras privadas pueden no funcionar; la app debe explicarlo.

**Resultado esperado:** Acceso limitado y honesto, no una falsa sincronización.

**Criterios de aceptación:**
- **CA-OFF-01-01:** Dado que instalé Noki, cuando abro por primera vez sin conexión, entonces puedo usar Inicio, Orientación y los contenidos de Aprende incluidos en el APK.
- **CA-OFF-01-02:** Dado que existe una copia previa del directorio, cuando lo abro sin conexión, entonces veo la copia con su fecha y un aviso de que puede no estar actualizada.
- **CA-OFF-01-03:** Dado que estoy sin conexión, cuando intento guardar un cambio privado, entonces la app no muestra “Guardado” sin confirmación del servidor.

### HU-UPD-01 — Recibir una actualización de Noki

**Historia:** Como usuario, quiero comprobar si hay una versión nueva e instalarla conservando mis datos, para mantener Noki actualizada.

**Precondiciones:** Noki instalada y conexión disponible para comprobar nuevas versiones.

**Flujo principal:**
1. Abrir Perfil y Buscar actualización.
2. Comparar la versión instalada con el manifiesto HTTPS publicado.
3. Si existe una versión compatible superior, mostrar versión, tamaño y enlace de descarga.
4. El usuario elige descargar o Más tarde.
5. Android valida la firma y solicita confirmación para instalar.

**Datos implicados:** Identificador de paquete, versionCode, API mínima, URL HTTPS y hash del paquete.

**Reglas asociadas:** RF-52.

**Errores y recuperación:** Si la actualización falla, la versión actual sigue siendo utilizable y se puede intentar después.

**Resultado esperado:** Versión instalada actualizada sin desinstalar ni borrar datos de la cuenta.

**Criterios de aceptación:**
- **CA-UPD-01-01:** Dado que se publicó una versión superior compatible, cuando pulso Buscar actualización, entonces veo su número y las acciones Ver actualización y Más tarde.
- **CA-UPD-01-02:** Dado un APK válido con firma idéntica, cuando confirmo su instalación en Android, entonces Noki conserva los datos y muestra el nuevo número de versión.
- **CA-UPD-01-03:** Dado que la descarga de actualización falla, cuando sigo usando Noki, entonces la versión actual no queda inutilizable.
---

## 10. Flujos y escenarios

### F-01 — Primera visita y registro

**Precondición:** persona sin sesión.

1. Abre Noki.
2. Ve Inicio público, no un login forzado.
3. Puede explorar Orientación, Aprende o Puntos sin cuenta.
4. Elige Crear cuenta.
5. Completa datos y acepta documentos legales.
6. Recibe correo de verificación.
7. Verifica.
8. Inicia sesión.
9. Accede al panel personal.

**Postcondición:** existe una cuenta real y verificada.

### F-02 — Orientación pública completa

1. Abrir Orientación.
2. Elegir categoría.
3. Responder condición visible.
4. Si existe atención o duda, mostrar `SAFETY_REVIEW`.
5. Si no, responder funcionamiento.
6. Mostrar `SECOND_LIFE` o `REPAIR_REVIEW`.
7. Permitir cambiar respuestas.
8. Ofrecer siguientes pasos.
9. Si es visitante, la orientación termina sin guardar datos o puede crear cuenta.
10. Si tiene sesión, puede guardar como dispositivo.

### F-03 — Guardar una orientación

1. Terminar F-02 con sesión.
2. Pulsar Guardar en Mis dispositivos.
3. Completar descripción y opcionales.
4. Enviar.
5. Servidor crea dispositivo del usuario.
6. Abrir detalle.
7. Mostrar recomendación y decisión `undecided`.

### F-04 — Registrar un dispositivo sin orientación

1. Iniciar sesión.
2. Mis dispositivos → Añadir.
3. Completar categoría, descripción, estado funcional y condición.
4. Guardar.
5. Calcular recomendación con las mismas reglas de Orientación.
6. Abrir detalle.

### F-05 — Consultar y guardar un lugar

1. Abrir Puntos.
2. Buscar o filtrar.
3. Seleccionar ficha.
4. Ver fuente y fecha.
5. Confirmar condiciones directamente si lo desea mediante teléfono/sitio.
6. Solicitar ruta opcional.
7. Si tiene sesión, elegir Guardar como lugar de referencia.
8. Seleccionar su dispositivo.
9. Guardar referencia.
10. Ningún paso crea una reserva.

### F-06 — Preparar un dispositivo

1. Abrir dispositivo.
2. Elegir decisión.
3. Abrir Preparación.
4. Revisar cada ítem.
5. Marcar Hecho, Pendiente o No aplica.
6. Si existe condición de atención, mantener aviso preventivo.
7. Guardar.
8. Volver al detalle.

### F-07 — Cerrar el ciclo personal

1. Abrir dispositivo activo.
2. Pulsar Marcar como completado.
3. Elegir resultado.
4. Si fue llevado a un lugar, elegir ficha o “Otro lugar”.
5. Leer aviso de declaración personal.
6. Confirmar.
7. Registro pasa a Historial.
8. Puede corregirse o eliminarse posteriormente.

### F-08 — Recuperar contraseña

1. Login → Olvidé mi contraseña.
2. Introducir correo.
3. Recibir mensaje neutral.
4. Abrir correo.
5. Establecer nueva contraseña.
6. Continuar a Login/Panel.

### F-09 — Cambiar correo

1. Perfil → Cambiar correo.
2. Introducir nueva dirección.
3. Enviar.
4. Confirmar según flujo de Auth.
5. Noki solo presenta el nuevo correo como definitivo después de la confirmación.

### F-10 — Eliminar cuenta

1. Perfil → Eliminar cuenta.
2. Leer alcance.
3. Confirmar.
4. Reautenticar.
5. Servidor elimina datos privados y Auth.
6. App limpia almacenamiento local.
7. Vuelve a Inicio.

### F-11 — Pérdida de conexión

- **Contenido público ya almacenado:** puede seguir visible con aviso si procede.
- **Mapa/OSRM:** puede fallar; se conserva información textual.
- **Formulario privado:** puede conservar borrador temporal, pero no mostrar “Guardado”.
- **Escritura iniciada:** si no existe respuesta, la app consulta el estado real antes de asumir éxito.
- **Sesión caducada:** se vuelve a Login y no se muestran datos privados obsoletos.

---

## 11. Interfaz y accesibilidad

### 11.1 Inventario de pantallas

| ID | Pantalla | Objetivo | Acción principal |
| --- | --- | --- | --- |
| UI-01 | Inicio público | Explicar Noki y dirigir a funciones | Orientar mi dispositivo |
| UI-02 | Login | Acceder a una cuenta | Entrar |
| UI-03 | Crear cuenta | Registrar usuario real | Crear cuenta |
| UI-04 | Verificación | Completar activación | Reenviar / continuar |
| UI-05 | Recuperar contraseña | Solicitar enlace | Enviar instrucciones |
| UI-06 | Nueva contraseña | Restablecer acceso | Guardar contraseña |
| UI-07 | Panel personal | Resumir acciones y registros | Ver Mis dispositivos |
| UI-08 | Orientación — pregunta | Recoger una respuesta | Seleccionar opción |
| UI-09 | Orientación — resultado | Explicar recomendación | Siguiente acción |
| UI-10 | Aprende | Educar y mostrar fuentes | Elegir tema |
| UI-11 | Puntos de recolección | Consultar lugares | Seleccionar lugar |
| UI-12 | Detalle de lugar | Verificar datos/fuente | Ruta / Guardar referencia |
| UI-13 | Mis dispositivos | Consultar activos e historial | Añadir dispositivo |
| UI-14 | Crear/editar dispositivo | Registrar datos | Guardar |
| UI-15 | Detalle de dispositivo | Centralizar seguimiento | Elegir siguiente paso |
| UI-16 | Preparación | Completar checklist | Guardar preparación |
| UI-17 | Resultado final | Cerrar ciclo personal | Confirmar resultado |
| UI-18 | Mi perfil | Administrar cuenta | Editar/seguridad |
| UI-19 | Términos | Informar condiciones | Volver |
| UI-20 | Privacidad | Informar tratamiento de datos | Volver |
| UI-21 | Acerca de Noki | Mostrar propósito, versión y contacto | Consultar información o volver |
| UI-22 | Accesibilidad | Apariencia, tamaño, contraste y movimiento | Ajustar preferencia |
| UI-23 | Mapa ampliado | Interactuar con mapa sin mover la página | Elegir punto de partida |
| UI-24 | Dirección de partida | Buscar y confirmar desde dónde salir | Usar esta dirección |
| UI-25 | Recibir aparatos con Noki | Explicar requisitos y disponibilidad del alta | Consultar proceso; solicitar solo con alta activa |
| UI-26 | Solicitud de receptor | Autorizar y enviar datos por canal privado o pedir cita | Continuar solicitud; requiere RF-72 |
| UI-27 | Estado de revisión | Mostrar decisión y siguiente acción | Corregir, retirar o apelar; requiere RF-72 |
| UI-28 | Recepciones | Gestionar entregas asignadas | Abrir entrega; solo receptor aprobado |
| UI-29 | Entrega con recepción confirmada | Acordar y confirmar entre dos partes | Confirmar o cancelar; requiere RF-70 |
| UI-30 | Reportar un problema | Crear caso privado vinculado a una entrega | Enviar reporte; requiere RF-70 |

### 11.2 Navegación

**Visitante:**

Inicio → Orientación / Aprende / Puntos / Login / Crear cuenta.

**Usuario:**

Inicio → Mis dispositivos / Orientación / Puntos / Aprende / Perfil.

La navegación se limita a las pantallas UI-01 a UI-30 y a sus condiciones de activación. UI-26 a UI-30 no se habilitan hasta cumplir RF-72. Acerca de Noki es público; permite consultar versión, propósito, contacto, Términos y Privacidad. Volver restaura el contexto anterior sin crear copias de pantallas. UI-25 se abre desde Inicio y Perfil sin solicitar documentos. Un receptor aprobado alterna Mis aparatos y Recepciones dentro de la misma cuenta.

### 11.3 Jerarquía visual

- Una acción primaria por bloque.
- Títulos claros y descriptivos.
- Máximo recomendado de 65–75 caracteres por línea en textos largos de lectura.
- Botones con verbos: “Guardar”, “Continuar”, “Ver punto”, “Calcular ruta”.
- Evitar botones ambiguos como “Aceptar” sin contexto.
- Errores próximos al campo y resumen general cuando varios campos fallan.
- No utilizar microtexto gris de bajo contraste para información esencial.

### 11.4 Público 40–60

La interfaz debe priorizar:

- lectura cómoda;
- navegación predecible;
- instrucciones breves;
- lenguaje cotidiano;
- confirmaciones antes de borrar datos;
- salida visible en procesos largos;
- no depender de gestos ocultos;
- no depender de iconos sin etiqueta para acciones críticas.

### 11.5 Accesibilidad verificable

1. Navegación principal operable por teclado.
2. Foco visible.
3. Labels programáticas.
4. Mensajes de estado anunciables.
5. Contraste suficiente según WCAG 2.2 AA para texto y controles esenciales.
6. Tamaño de texto al 200 % sin pérdida de información esencial.
7. Alternativa textual al mapa.
8. No depender exclusivamente de color.
9. Respeto a movimiento reducido.
10. Indicaciones de error con texto.
11. Controles táctiles de tamaño suficiente.
12. Recursos e idioma español; los lectores de pantalla reciben textos y etiquetas en español.
13. Títulos de página distintos y comprensibles.

### 11.6 Identidad visual

| Elemento | Valor | Uso |
| --- | --- | --- |
| Verde | `#10661E` | Acción principal, selección, iconos y áreas de marca |
| Azul | `#0F2167` | Acciones secundarias y encabezados destacados |
| Fondo claro | `#F5F4F0` | Fondo principal |
| Gris | `#E5E5E5` | Separadores y superficies auxiliares |
| Verde claro | `#DCEADD` | Selección y avisos informativos, con texto oscuro |
| Lavanda | `#AAAED1` | Acento secundario, con texto azul oscuro |
| Blanco y negro | `#FFFFFF`, `#000000` | Contraste de texto e iconos según el fondo |

Los códigos hexadecimales identifican los colores. Se exige contraste mínimo de 4,5:1 para texto normal y 3:1 para texto grande y componentes esenciales. Los avisos de error llevan texto explicativo e icono, nunca solo color. La interfaz usa tema claro estable; el color dinámico del fondo de pantalla del teléfono no sustituye la identidad Noki.

**Tipografía:** Poppins incluida en el APK con su licencia. Títulos Medium 28–32 sp, subtítulos Medium 22 sp, cuerpo Regular 18 sp y texto auxiliar 16 sp. Poppins Light se reserva para textos decorativos grandes; no se utiliza para instrucciones, errores ni campos. Las fuentes escalan según la preferencia del sistema. No se fuerzan títulos de 42 sp en pantallas compactas.

**Logotipos:** `Flagicon.png` identifica el icono adaptativo del lanzador; se centra dentro del área segura y se verifica con máscaras circular y cuadrada. `Logo sobre fondos claros.png` se muestra en Inicio y Acerca de, manteniendo la proporción original. La variante «Logo sobre fondos ocuros2.png», de letras claras e isotipo verde, se utiliza sobre fondos oscuros. El logo no reemplaza títulos de pantalla ni etiquetas de botones. No se añade una mascota decorativa.

**Espaciado y controles:** márgenes de 24 dp en teléfonos habituales, reducibles a 16 dp en 320 dp; separación entre bloques 20–24 dp y entre controles relacionados 8–12 dp. Tarjetas con radio de 16–24 dp y borde visible. Una acción primaria por pantalla de formulario. Todos los botones críticos tienen texto además del icono. El área de lectura se limita a 640 dp en ventanas amplias.

### 11.7 Navegación y conservación del contexto

- La primera apertura presenta Inicio público. Mis aparatos dirige a acceso cuando no existe sesión. En la documentación funcional, “Mis dispositivos” identifica este mismo módulo.
- Una pregunta por pantalla de Orientación; se muestra el progreso, selección inequívoca y Continuar. Seleccionar una opción no avanza sin confirmación.
- Cambiar una respuesta elimina únicamente las respuestas posteriores incompatibles. La pantalla siguiente empieza en su encabezado, no conserva el desplazamiento del paso anterior.
- Atrás respeta la jerarquía. Desde un destino principal vuelve a Inicio; desde Inicio permite salir de Android. No se insertan pantallas duplicadas en la pila.
- Formularios modificados muestran “Descartar cambios” y “Seguir editando” al abandonarlos. Guardar solo cierra el formulario después de confirmación del servidor.
- Girar el teléfono conserva el paso, las respuestas y los campos no sensibles. Contraseñas no se serializan en estado guardado ni archivos.
- Una ruta de acceso iniciada para guardar una orientación vuelve a ese resultado tras autenticarse; no inicia un cuestionario nuevo ni crea un registro automáticamente.
- Los indicadores de carga tienen texto; los errores incluyen una acción de recuperación. Un estado vacío no se rellena con ejemplos ficticios. No se muestran ejemplos de pruebas como registros de una cuenta.

---

## 12. Estados y errores

### 12.1 Estados de interfaz obligatorios

Toda pantalla que dependa de datos debe contemplar:

- inicial;
- cargando;
- contenido;
- vacío;
- error recuperable;
- sin conexión;
- sesión caducada, cuando aplique;
- permiso denegado, cuando aplique.

### 12.2 Catálogo funcional de errores

| Código | Situación | Mensaje/recuperación |
| --- | --- | --- |
| AUTH_INVALID | Correo o contraseña incorrectos | “Correo o contraseña incorrectos.” |
| AUTH_UNVERIFIED | Correo sin confirmar | Explicar y ofrecer reenvío |
| AUTH_SESSION_EXPIRED | Sesión ya no válida | Volver a Login |
| AUTH_EMAIL_EXISTS | Correo ya registrado | Ofrecer Login/Recuperación |
| AUTH_RATE_LIMIT | Demasiados envíos | Pedir esperar sin repetir automáticamente |
| RESET_INVALID | Enlace de recuperación inválido/vencido | Solicitar otro enlace |
| VALIDATION_REQUIRED | Campo obligatorio vacío | Marcar campo |
| VALIDATION_FORMAT | Formato inválido | Explicar regla concreta |
| NETWORK_UNAVAILABLE | Sin conexión | Conservar borrador si aplica y Reintentar |
| SERVICE_UNAVAILABLE | Supabase no responde | Explicar que el servicio no respondió; Reintentar |
| DIRECTORY_EMPTY | No hay fichas publicadas | Estado vacío, no datos inventados |
| DIRECTORY_STALE | Ficha >90 días sin revisión | Mostrar advertencia |
| GEO_DENIED | Ubicación rechazada | Origen manual/enlace externo |
| GEO_UNAVAILABLE | Navegador no obtiene ubicación | Alternativa manual |
| ROUTE_UNAVAILABLE | OSRM falla | Abrir servicio externo |
| SAVE_FAILED | Escritura privada falló | Conservar formulario y Reintentar |
| DELETE_FAILED | Eliminación falló | Mantener registro y no mostrar éxito |
| FORBIDDEN | Acceso a recurso ajeno | Denegar sin revelar datos |
| ACCOUNT_DELETE_FAILED | Cuenta no se eliminó | Mantener sesión/estado real y ofrecer reintentar |
| UPDATE_AVAILABLE | Nueva versión Android compatible | Mostrar versión, descarga y Más tarde |

### 12.3 Mensajes prohibidos

No utilizar mensajes engañosos como:

- “Reciclaje completado” si solo el usuario marcó un resultado;
- “Entrega confirmada” si no existe confirmación externa;
- “Reserva realizada” al guardar un lugar;
- “Punto aliado” sin convenio;
- “Todo salió bien” cuando la respuesta del servidor es incierta.

---

## 13. Modelo de datos objetivo

Las tablas siguientes definen el contrato de datos de Noki. Los campos derivados no se envían como campos editables del cliente.

### 13.1 Supabase Auth

Fuente de verdad para:

- `id`;
- `email`;
- contraseña gestionada por Auth;
- estado de verificación;
- metadatos necesarios del proveedor.

Noki nunca guarda contraseñas en tablas propias.

### 13.2 `noki_profiles`

| Campo | Tipo | Regla |
| --- | --- | --- |
| `user_id` | UUID | PK y FK a `auth.users.id` |
| `display_name` | text | vacío hasta configurarlo; al editar, 2–80 caracteres sin espacios al principio o al final |
| `username` | text | obligatorio al completar registro, 3–24 letras ASCII, números o guion bajo; único sin distinguir mayúsculas |
| `username_changed_at` | timestamptz | fecha de servidor; siguiente cambio permitido tras 30 días; no editable directamente |
| `updated_at` | timestamptz | servidor |

RLS: lectura exclusiva del titular con correo confirmado y sesión vigente. Las escrituras se realizan mediante `noki_profile_action`, que deriva el titular de la sesión; no admite cambios directos desde el cliente. El nombre no concede roles. La foto se almacena en el bucket privado `noki-profile-photos`, con ruta única `<user_id>/avatar.jpg`, según RF-76.

### 13.3 `noki_collection_points`

| Campo | Tipo | Regla |
| --- | --- | --- |
| `id` | UUID | PK |
| `name` | text | obligatorio |
| `address` | text | obligatorio |
| `city` | text | `Medellín` en esta versión |
| `latitude` | numeric | obligatorio |
| `longitude` | numeric | obligatorio |
| `phone` | text nullable | solo si fuente lo publica |
| `website_url` | text nullable | URL válida |
| `source_name` | text | obligatorio |
| `source_url` | text | obligatorio |
| `accepted_categories` | text[] | categorías verificadas; puede estar vacío |
| `accepted_details` | text nullable | explicación fiel a la fuente |
| `hours_text` | text nullable | no inventar si no está publicado |
| `verified_at` | date | obligatorio |
| `active` | boolean | solo `true` se publica |
| `created_at` | timestamptz | servidor |
| `updated_at` | timestamptz | servidor |

Lectura pública: únicamente filas activas y campos públicos. Escritura: no disponible desde Noki App.

### 13.4 `noki_devices`

| Campo | Tipo | Regla |
| --- | --- | --- |
| `id` | UUID | PK |
| `user_id` | UUID | FK Auth, obligatorio |
| `category` | text | una de las seis categorías |
| `description` | text | 2–120 caracteres |
| `brand` | text nullable | 0–60 |
| `model` | text nullable | 0–60 |
| `function_state` | text | `works`, `faults`, `unknown` |
| `safety_state` | text | `normal`, `attention`, `unknown` |
| `recommendation_code` | text calculado | `safety_review`, `second_life`, `repair_review`; el servidor lo deriva de seguridad y funcionamiento |
| `decision` | text | `undecided`, `keep`, `repair`, `reuse`, `deliver` |
| `selected_point_id` | UUID nullable | FK a collection point |
| `lifecycle` | estado derivado | `archived` si archived_at existe; `completed` si hay resultado o entrega confirmada por el receptor; en otro caso `active` |
| `outcome` | text nullable | `kept`, `repaired`, `reused`, `delivered`, `other` |
| `outcome_point_id` | UUID nullable | referencia de resultado |
| `completed_at` | timestamptz nullable | requerido si completed, asignado por el servidor |
| `archived_at` | timestamptz nullable | solo servidor al retirar un pendiente; no equivale a entrega ni borrado |
| `version` | integer | 1 al crear; se incrementa en cada edición confirmada |
| `preparation` | jsonb | cinco claves y estados definidos en 13.5 |
| `created_at` | timestamptz | servidor |
| `updated_at` | timestamptz | servidor |

RLS: propietario único.

Integridad:

- `lifecycle='active'` → sin archivo ni resultado ni entrega confirmada por el receptor.
- `lifecycle='completed'` → resultado personal con su fecha o constancia de recepción con su fecha, aunque todavía no se haya anotado el resultado complementario.
- `lifecycle='archived'` → retirado sin resultado de entrega; permanece de solo lectura. El cliente no escribe `archived_at` directamente.
- `outcome_point_id` solo se usa cuando `outcome='delivered'`; si el resultado cambia a otro valor debe quedar en `null`.
- si `selected_point_id` apunta a un lugar que luego pasa a `active=false`, el vínculo puede conservarse para el usuario, pero la interfaz debe advertir que la ficha ya no está publicada como vigente.
- la recomendación debe ser recalculable a partir de `safety_state` y `function_state`.

### 13.5 Preparación de `noki_devices`

| Campo | Tipo | Regla |
| --- | --- | --- |
| `preparation` | jsonb | Objeto obligatorio dentro de `noki_devices`, con exactamente las cinco claves siguientes |
| `backup` | string | `pending`, `done`, `not_applicable` |
| `accounts` | string | igual |
| `media` | string | igual |
| `erasure` | string | igual |
| `destination` | string | igual |

No admite valores nulos, claves extra ni estados distintos. El propietario, la versión y la fecha de actualización son los del aparato. La preparación se guarda atómicamente con su registro y se elimina con él. RLS: acceso solo si el dispositivo pertenece al usuario.

### 13.6 `noki_private.account_consents`

| Campo | Tipo | Regla |
| --- | --- | --- |
| `user_id` | UUID | FK Auth, parte de la clave primaria |
| `version` | text | versión conjunta de términos y privacidad, parte de la clave primaria |
| `accepted_at` | timestamptz | servidor |
| `revoked_at` | timestamptz nullable | servidor; nulo mientras la aceptación está vigente |

Clave primaria: `(user_id, version)`. Las dos casillas se presentan separadas y desmarcadas; el servidor exige ambas afirmativas para registrar el consentimiento conjunto. El cliente no establece fechas ni puede leer o modificar filas directamente. La operación de aceptación comprueba versión vigente, correo confirmado y sesión activa. Repetir una aceptación vigente no cambia su fecha. Volver a aceptar después de retirarla registra la nueva fecha y elimina la marca de revocación de esa versión.

`noki_client_config` contiene una única fila con `id=true`, `email_otp_enabled` y `legal_version`. Lectura pública de configuración, sin datos personales; escritura reservada a la operación del servicio. La disponibilidad del correo se aplica al envío y a la verificación de códigos en la app, sin conceder acceso a registros privados.

### 13.7 Datos que no se almacenan

Las funciones de orientación, directorio y registro personal no guardan los siguientes datos. La solicitud voluntaria del receptor y las entregas solo incorporan los datos definidos en 20.7, en entidades separadas. Ningún flujo solicita documentos de identidad o biometría:

- ubicación GPS del usuario;
- documento de identidad;
- edad exacta;
- dirección de vivienda;
- fotografías del dispositivo;
- números de serie;
- conversaciones;
- información bancaria;
- peso del equipo;
- certificaciones ambientales;
- video de cámara;
- QR de entrega.


---

## 14. Integraciones, arquitectura y seguridad

### 14.1 Arquitectura

- Núcleo `shared` en Kotlin Multiplatform con objetivos JVM, iOS ARM64 e iOS Simulator ARM64. Contiene reglas y modelos independientes del sistema operativo.
- Cliente Android nativo en Kotlin y Jetpack Compose; comunicación HTTPS con servicios externos. Cliente iOS/iPadOS con SwiftUI; cliente Windows con Compose Desktop. Los adaptadores de plataforma no modifican las reglas compartidas.
- Supabase Auth para identidad.
- PostgreSQL/Supabase para datos privados y directorio.
- RLS para aislamiento.
- Operación de eliminación de cuenta mediante función segura de servidor/Edge Function.
- MapLibre Native con cartografía basada en OpenStreetMap y atribución visible.
- OSRM para rutas.
- Servicio externo de mapas como fallback.

**Estructura del cliente:** `ui` contiene pantallas Compose, navegación, tema y ViewModels; `domain` contiene estados, validaciones y reglas de orientación sin dependencia de Android; `data` contiene repositorios, autenticación, llamadas HTTPS y persistencia local. El estado fluye del ViewModel a la pantalla; las acciones del usuario vuelven al ViewModel. Las operaciones de red y disco se ejecutan fuera del hilo principal y son cancelables.

**Construcción:** Gradle Kotlin DSL, dependencias con versiones fijas, SDK de compilación y objetivo 36, API mínima 26. El paquete contiene recursos de marca y fuentes locales. El proceso de entrega ejecuta compilación, pruebas unitarias, análisis estático y pruebas de interfaz antes de firmar una versión de distribución. La clave de firma y las credenciales privilegiadas nunca se incluyen en el repositorio.

**Ubicación:** permiso de Android en primer plano, solicitado después de “Ruta desde mi ubicación”. No se requiere ubicación precisa si el sistema entrega una aproximada; la interfaz identifica esa condición. No se solicita permiso en segundo plano. Rechazar no bloquea el directorio. Un origen elegido manualmente requiere confirmación antes de enviarse al proveedor de rutas.

**Persistencia:** datos públicos y su fecha se conservan separados de los datos de cuenta. La sesión se renueva por el mecanismo de Auth, sin guardar contraseñas. Android Keystore protege la clave que cifra tokens y borradores privados. Cerrar sesión elimina esos datos y la pila de pantallas privadas, incluso cuando falla la petición remota. Reinstalar en otro teléfono exige autenticación; los registros confirmados se recuperan del servidor.

**Concurrencia:** cada creación utiliza un UUID estable como clave de idempotencia. Un reintento después de timeout consulta ese UUID antes de crear otro registro. Editar y eliminar utilizan el entero `version` leído, incrementado por el servidor en cada actualización. Si cambió, la operación no afecta filas y ofrece actualizar la lista, sin sobrescribir silenciosamente. Fecha de actualización y finalización proceden del servidor; la visualización usa la zona `America/Bogota`.

### 14.2 Contratos mínimos

| Operación | Entrada | Resultado |
| --- | --- | --- |
| `signUp` | nombre, email, password, aceptación | cuenta + verificación |
| `signIn` | email, password | sesión |
| `resendVerification` | email/contexto | envío controlado |
| `resetPassword` | email | correo de recuperación |
| `updatePassword` | sesión, nueva contraseña | cuenta actualizada |
| `updateEmail` | sesión, nuevo correo | proceso de confirmación |
| `getProfile` | sesión | perfil propio |
| `updateProfile` | sesión, nombre | perfil actualizado |
| `listDevices` | sesión, filtros | solo propios |
| `createDevice` | sesión, campos válidos | dispositivo propio |
| `updateDevice` | sesión, id propio, cambios | dispositivo actualizado |
| `deleteDevice` | sesión, id propio | eliminación |
| `savePreparation` | sesión, device propio | checklist |
| `listCollectionPoints` | público | fichas activas |
| `deleteAccount` | sesión + reautenticación | eliminación segura |

Los contratos se implementan en repositorios de datos; las pantallas observan estados de carga, datos y error mediante ViewModel.

### 14.3 Seguridad del cliente

- Solo clave publicable de Supabase en cliente Android.
- Nunca service role.
- Nunca contraseñas guardadas por Noki.
- Nunca confiar en `user_id` recibido del formulario para autorizar.
- Escapar/sanitizar contenido de usuario.
- Mostrar las entradas del usuario en componentes Text; no interpretar HTML, scripts ni enlaces no autorizados.
- Evitar exponer mensajes SQL internos.

### 14.4 RLS mínima

Debe probarse que:

- usuario A no puede leer dispositivos de B;
- A no puede modificar checklist de B;
- A no puede eliminar datos de B;
- visitantes no pueden leer perfiles ni dispositivos;
- visitantes sí pueden leer fichas públicas activas;
- ningún usuario normal puede editar `noki_collection_points`;
- aceptaciones legales solo son visibles por su propietario y el contexto administrativo autorizado.

### 14.5 Eliminación de cuenta

La función de eliminación:

1. valida JWT;
2. exige reautenticación o prueba reciente equivalente;
3. elimina/anonimiza datos privados;
4. elimina usuario Auth con credenciales de servidor;
5. devuelve éxito solo si el estado final es consistente.

No se ejecuta con service role expuesta en cliente Android.

### 14.6 Correo

Los enlaces de:

- verificación;
- cambio de correo;
- recuperación;

deben usar enlaces HTTPS controlados por Noki, asociados al paquete y a su certificado mediante Android App Links. La app valida host, ruta, estado y código de un solo uso antes de cambiar una sesión; no acepta tokens arbitrarios de otras aplicaciones. Si el correo se abre en otro equipo, debe ofrecer instrucciones para continuar en el teléfono o reenviar la solicitud. Un enlace de recuperación abre exclusivamente Nueva contraseña; no sustituye silenciosamente una sesión de otra cuenta.

### 14.7 Servicios de mapas

Antes de enviar coordenadas a OSRM, la interfaz informa que el cálculo usa un servicio externo. Noki no conserva las coordenadas.

---

## 15. Criterios de aceptación globales y trazabilidad

Los criterios específicos están dentro de cada HU. Esta sección evita duplicarlos y relaciona necesidades con funciones.

| Necesidad | Historias principales | RF principales | Pantallas |
| --- | --- | --- | --- |
| N1 | HU-EDU-01, HU-ORI-05 | RF-13, RF-14, RF-19 | UI-10, UI-09 |
| N2 | HU-ORI-01–07, HU-DEV-01/03/06 | RF-15–21, RF-30–35 | UI-08, UI-09, UI-14, UI-15 |
| N3 | HU-DIR-01–06 | RF-22–28 | UI-11, UI-12 |
| N4 | HU-DEV-02–09, HU-HIS-01–02 | RF-29–40 | UI-13–UI-17 |
| Acceso real | HU-AUTH-01–07 | RF-04–12 | UI-02–UI-06 |
| Control de cuenta | HU-PRO-01–05 | RF-41–45 | UI-18 |
| Privacidad y documentos legales | HU-AUTH-01, HU-PRO-05, HU-LEGAL-01–02 | RF-46–48 | UI-19, UI-20 |
| Inicio y panel | HU-HOME-01–02 | RF-01–03 | UI-01, UI-07 |
| Android y conectividad | HU-AND-01, HU-OFF-01, HU-UPD-01 | RF-49–52 | Todas las pantallas públicas y estructura Android |
| Prueba local y plataformas | T-25–28 | RF-53–54 | Separación de datos, núcleo y adaptación por plataforma |
| Orientación accionable | T-29 | RF-55 | Resultado con pasos, guardar y consultar lugares |
| Acceso con código y proveedores | T-34–35 | RF-56 | Correo, verificación, identidad y retorno al contexto |
| Bienvenida y acceso contextual | T-30 | RF-57 | Entrada, exploración pública y hoja de acceso |
| Accesibilidad y respuesta visual | T-31–32 | RF-58–59 | Preferencias, navegación, marca y pulsación |
| Navegación cartográfica | T-33 | RF-60 | Mapa ampliado, origen confirmado y retorno |
| Origen, apariencia, disposición y aceptación | T-36–40 | RF-61–64 | UI-11, UI-19–20, UI-22–24 |
| Destino comprensible | HU-DIR-07, T-41 | RF-65 | UI-11–12, UI-23–24 |
| Descubrir recepción | HU-REC-01, T-42 | RF-66 | UI-01, UI-18, UI-25 |
| Revisión y rol adicional | HU-REC-02–05, T-43–46 | RF-67–69 | UI-26–28, sujetos a activación |
| Entrega, seguridad y reportes | HU-ENT-01–03, HU-SEG-01, T-47–49 | RF-70–72 | UI-28–30, sujetos a activación |

### 15.1 Criterios globales de coherencia

Antes de considerar una versión apta para entrega:

1. No debe existir una pantalla o CTA que no tenga requisito e historia asociados.
2. No debe existir historia sin criterios de aceptación.
3. No debe existir función privada sin regla de autorización.
4. Ningún punto del directorio debe presentarse como aliado sin convenio.
5. Guardar un punto nunca debe generar reserva.
6. Un resultado `delivered` exige constancia de recepción, sin presentarse como certificación de reciclaje.
7. Toda ruta y acción visible debe tener un comportamiento definido y verificable.
8. El producto no debe depender de cuentas ficticias para funcionar.
9. Registro, verificación y recuperación deben funcionar con correos reales.
10. Toda afirmación de contenido debe poder rastrearse a una fuente o a una regla explícita del sistema.
11. Durante una transición, una acción de la pantalla que está saliendo no modifica el siguiente paso. Repetir Continuar o Ver orientación no omite preguntas, no supera el resultado ni deja una vista vacía. Atrás conserva las respuestas válidas.

---

## 16. Estrategia de pruebas

### 16.1 Unitarias

Probar al menos:

- reglas de recomendación;
- validación de formularios;
- normalización de campos;
- filtros del directorio;
- estados del checklist;
- transiciones active/completed;
- mensajes de error mapeados.

### 16.2 Integración con Supabase

Probar:

- registro real;
- verificación;
- login;
- recuperación;
- cambio de contraseña;
- cambio de correo;
- RLS negativa entre dos cuentas;
- CRUD de dispositivos;
- checklist;
- lectura pública de puntos;
- eliminación de cuenta.

### 16.3 End-to-end

Casos mínimos:

1. visitante → orientación → crear cuenta → verificar → guardar dispositivo;
2. usuario → crear dispositivo → decidir → seleccionar punto → preparar → completar;
3. usuario → historial → corregir resultado;
4. usuario → recuperar contraseña;
5. usuario → eliminar cuenta;
6. visitante → punto → ubicación denegada → ruta alternativa.

### 16.4 Accesibilidad

Probar:

- teclado;
- lector de pantalla en flujos principales;
- tamaño de texto al 200 %;
- contraste;
- foco;
- errores;
- móvil;
- movimiento reducido.

### 16.5 Público objetivo

Realizar prueba con mínimo 5 personas entre 40 y 60 años.

Tareas:

- explicar con sus palabras qué hace Noki;
- completar orientación;
- identificar que un punto no es necesariamente aliado;
- registrar un dispositivo;
- encontrar un punto y revisar su fuente;
- entender la diferencia entre decisión y resultado personal.

No ayudar durante la tarea salvo que se documente la intervención.

### 16.6 Versiones Android y dispositivos

Validar como mínimo:

- Android API 26, 30, 35 y 36;
- teléfono físico VIVO Y39 5G;
- teléfono de ancho compacto (320/360 dp);
- tableta de 600/840 dp;
- bloqueo vertical en teléfonos y adaptación sin recortes cuando el sistema imponga otra orientación;
- escala de texto al 100 %, 150 % y 200 %;
- TalkBack, teclado externo y navegación Atrás.

### 16.7 Red y proveedores

Simular:

- Supabase no disponible;
- OSRM no disponible;
- geolocalización denegada;
- modo sin conexión;
- respuesta lenta;
- sesión expirada;
- actualización del APK con la misma firma.

---

## 17. Métricas de éxito

No se inventan metas comerciales. Las métricas de esta versión son de funcionamiento y comprensión.

| ID | Métrica | Criterio de versión |
| --- | --- | --- |
| M-01 | Criterios críticos de aceptación | 100 % aprobados antes de publicar versión final |
| M-02 | Aislamiento entre cuentas | 0 lecturas/escrituras ajenas exitosas en pruebas negativas |
| M-03 | Registro/login/recuperación | Todos los casos válidos de prueba completan el flujo |
| M-04 | Fichas del directorio con fuente/fecha | 100 % de fichas publicadas |
| M-05 | Comprensión del propósito | Al menos 4 de 5 participantes 40–60 lo explican sin ayuda |
| M-06 | Completar orientación | Al menos 4 de 5 la completan sin asistencia |
| M-07 | Comprender ausencia de convenio | 5 de 5 identifican que una ficha no equivale a reserva o alianza |
| M-08 | Registrar un dispositivo | Al menos 4 de 5 completan la tarea sin ayuda |
| M-09 | Tamaño de texto al 200 % | 0 bloqueos de función crítica por superposición |
| M-10 | Navegación por teclado | 100 % de acciones esenciales alcanzables fuera del mapa gráfico |
| M-11 | Errores críticos | 0 errores que muestren éxito cuando el servidor falló |
| M-12 | Claridad del lenguaje | 0 referencias visibles al contexto interno de desarrollo |
| M-13 | Alta de receptor | 0 autoaprobaciones, filtraciones de documentos o aprobaciones basadas solo en el cliente |
| M-14 | Entregas con recepción confirmada | 0 cierres por escaneo solamente, receptor no asignado, duplicado o sin recepción explícita |

---

## 18. Riesgos y supuestos

| ID | Riesgo | Impacto | Mitigación |
| --- | --- | --- | --- |
| R-01 | Información de un lugar cambia | Alto | Fuente + fecha + revisión ≤90 días + aviso |
| R-02 | Usuario interpreta directorio como convenio | Alto | Copia explícita en lista y detalle |
| R-03 | Usuario interpreta orientación como diagnóstico | Alto | Aviso y lenguaje no determinista |
| R-04 | Correo de verificación no llega | Medio | Reenvío + revisión de proveedor/SMTP |
| R-05 | Proyecto Supabase Free vuelve a pausarse | Alto en exposición/uso | Monitorear actividad y evaluar plan adecuado antes de uso sostenido |
| R-06 | Proveedor de rutas falla | Medio | Dirección textual + enlace externo |
| R-07 | Usuario comparte dispositivo con otra persona | Alto | Cierre visible, limpieza local, no cachear vistas privadas |
| R-08 | Acceso cruzado por RLS incorrecta | Crítico | Pruebas negativas obligatorias |
| R-09 | Borrador local contiene datos después de logout | Alto | Limpieza obligatoria |
| R-10 | Contenido RAEE queda desactualizado | Medio | Fuentes y fecha de revisión |
| R-11 | Público objetivo no comprende la terminología | Alto | Prueba 40–60 y microcopy revisado |
| R-12 | Navegación muestra destinos fuera del alcance | Alto | Verificar inventario UI-01 a UI-30, permisos y condiciones de activación |
| R-13 | Usuario marca “entregado” como si fuera certificado | Alto | Etiqueta permanente de declaración personal |
| R-14 | Eliminación de cuenta queda parcial | Crítico | Operación de servidor transaccional/compensada y prueba E2E |

### 18.1 Supuestos aceptados

- El producto inicial se centra en Medellín.
- No existen convenios operativos con lugares del directorio.
- Los usuarios disponen de un correo real para crear cuenta.
- La orientación se basa en declaraciones del usuario, no inspección del equipo.
- Noki no procesa físicamente RAEE.
- El equipo puede mantener las fuentes del directorio y contenido educativo.

---

## 19. Definición de terminado

La versión de Noki App se considera terminada únicamente cuando se cumplan **todos** los puntos siguientes.

### 19.1 Correspondencia SPEC ↔ App

- [ ] Todas las funciones incluidas en el alcance existen.
- [ ] Ninguna función visible contradice el SPEC.
- [ ] No existen botones sin comportamiento real.
- [ ] No existen pantallas “de muestra”.
- [ ] Los textos visibles explican funciones, decisiones y estados del usuario con vocabulario consistente.
- [ ] El directorio de referencia no se confunde con receptores aprobados.
- [ ] La información para receptores no crea permisos ni solicita pruebas de identidad con el alta inactiva.
- [ ] No se habilita recepción con código ni QR operativo antes de aprobar todos los controles de la sección 20.
- [ ] Mis dispositivos permite administrar únicamente registros propios.

### 19.2 Cuentas reales

- [ ] Registro con correo real funciona.
- [ ] Verificación funciona.
- [ ] Reenvío funciona.
- [ ] Login funciona.
- [ ] Recuperación de contraseña funciona.
- [ ] Cambio de correo funciona.
- [ ] Cambio de contraseña funciona.
- [ ] Cierre de sesión limpia contexto local.
- [ ] Eliminación de cuenta funciona de extremo a extremo.

### 19.3 Orientación y contenido

- [ ] Orientación completa sus tres rutas.
- [ ] Cambiar respuestas recalcula correctamente.
- [ ] Guardar resultado crea dispositivo.
- [ ] Aprende contiene fuentes y fechas.
- [ ] No se presenta diagnóstico técnico.

### 19.4 Directorio

- [ ] Todas las fichas tienen fuente y fecha.
- [ ] Se muestra aviso de alcance.
- [ ] Buscar y filtrar funciona.
- [ ] Mapa y lista representan el mismo lugar seleccionado.
- [ ] Geolocalización se pide solo bajo acción.
- [ ] Ruta tiene fallback.
- [ ] Guardar lugar no se presenta como reserva.

### 19.5 Mis dispositivos

- [ ] Crear, editar y eliminar funcionan.
- [ ] Recomendación se recalcula.
- [ ] Decisión y resultado no se confunden.
- [ ] Checklist se sincroniza.
- [ ] Historial funciona.
- [ ] Resultado `delivered` exige confirmación del receptor tras validar el código y no certifica reciclaje.
- [ ] Una cuenta no puede acceder a datos de otra.

### 19.6 Calidad

- [ ] Pruebas críticas aprobadas.
- [ ] RLS negativa aprobada.
- [ ] Versiones Android y tamaños de pantalla objetivo aprobados.
- [ ] Tamaño de texto al 200 % aprobado.
- [ ] Teclado y foco aprobados.
- [ ] Prueba con público 40–60 realizada.
- [ ] No hay secretos publicados.
- [ ] No hay errores críticos conocidos sin documentar.
- [ ] La actualización firmada conserva los datos y respeta la confirmación del sistema.
- [ ] Supabase está operativo al momento de publicación final.

### 19.7 Documentación

- [ ] Términos y Privacidad corresponden al comportamiento real.
- [ ] Fuentes de contenido están revisadas.
- [ ] Fuentes del directorio están revisadas.
- [ ] Los requisitos de la versión entregada cuentan con evidencia de pruebas.


---

## 20. Lugares, recepción y entregas

### 20.1 RF-65 — Selección del lugar y ruta

El campo «Buscar lugar de entrega» filtra nombres y direcciones del catálogo; no cambia el punto de partida. Un botón abre la lista de destinos con sus direcciones completas. Seleccionar un destino abre su ficha directamente.

La ficha muestra primero el nombre, la dirección y la acción de ruta. Si falta la partida, ofrece ubicación opcional, dirección escrita o selección en mapa. Confirmar o cancelar la selección regresa a la misma ficha. Con partida elegida, «Calcular ruta en carro» solicita autorización para enviar las dos coordenadas a OSRM y abre el mapa ampliado. La vista muestra carga, la ruta, distancia y tiempo estimados, o un error recuperable. No se exige buscar la ruta desplazando otra pantalla. No hay seguimiento continuo, tráfico en tiempo real ni navegación giro a giro.

Horarios, condiciones, fuente y contacto permanecen debajo. El directorio de referencia y los receptores aprobados son conjuntos distintos. Una ficha de un establecimiento público no recibe permisos de Noki automáticamente.

### 20.2 RF-66 — Entrada al perfil receptor

Inicio y Perfil ofrecen «Quiero recibir aparatos». La explicación responde: quién puede solicitarlo, qué información debe preparar, cómo se revisa y cómo empezará a recibir. Se utiliza lenguaje orientado a la tarea y la prevención de fraudes; las decisiones de implementación no forman parte de los textos promocionales.

Al final aparecen dos casillas independientes, inicialmente desmarcadas:
- «He leído los requisitos y entiendo cómo recibir aparatos».
- «Acepto las condiciones del perfil de receptor y el uso de las fotos para revisar mi solicitud».

«Crear mi perfil de receptor» solo se habilita con ambas marcadas. Si falta sesión real, se pide acceso sin perder el recorrido. La práctica no envía solicitudes ni fotografías. Tener cuenta o aceptar condiciones no concede el rol.

### 20.3 RF-67 — Solicitud y revisión a distancia

La solicitud tiene tres pasos:
1. Responsable: correo verificado, nombre completo de 5–120 caracteres y declaración de mayoría de edad y autorización para operar el lugar.
2. Lugar: nombre de 3–100 caracteres, dirección completa y ciudad de 10–240, horarios de 5–200, al menos una categoría del catálogo y condiciones de recepción de 10–500.
3. Fotos y confirmación: resumen editable mediante Atrás; fotos de fachada/entrada, recepción y almacenamiento; autorización final desmarcada y botón «Enviar solicitud».

Las tres fotos deben mostrar el lugar actual, sin rostros, cédulas, placas u otros datos privados. No se recolectan documentos de identidad ni biometría. La revisión comprueba consistencia de información y condiciones del lugar, no identidad contra registros oficiales.

El selector de Android concede acceso únicamente a las imágenes elegidas. El cliente decodifica, corrige orientación, reduce tamaño y vuelve a codificar JPEG sin EXIF/GPS. Cada objeto admitido es JPEG de máximo 3 MiB; no se guarda el original en la nube. El servidor limita las rutas a tres posiciones de la solicitud propia, en un bucket privado. Antes de publicar el servicio se requiere validación adicional del contenido recibido en servidor, no solo MIME/tamaño.

El servidor registra solicitante, versión específica aceptada y fecha propia antes de autorizar carga. Si una carga falla, el borrador permanece; el usuario puede seleccionar de nuevo las fotos y reintentar. Solo tres objetos presentes permiten enviar a revisión.

La revisión inicial es remota. Se informa: «Noki podrá coordinar visitas ocasionales para comprobar que el lugar existe y funciona como se informó». No se exige una visita inicial ni se promete una frecuencia. Una visita debe tener finalidad definida, coordinación y personal autorizado; no se anuncia como realizada si no ocurrió.

### 20.4 RF-68 — Estados y aprobación

| Estado | Qué ve la persona | Acciones |
| --- | --- | --- |
| Borrador | Solicitud sin enviar o carga incompleta | Corregir y reintentar |
| En revisión | Datos enviados, aún sin permiso de recepción | Consultar estado |
| Requiere cambios | Motivo concreto y accionable | Corregir y reenviar |
| Aprobado | Marca «Perfil revisado» y lugar | Cambiar a receptor |
| Suspendido | Motivo y contacto | Consultar o reclamar; no recibir |
| Retirado | Solicitud retirada por trámite de soporte | Iniciar otra solicitud según resolución |

Solo el operador autorizado del servidor registra una decisión manual con nombre de revisor, motivo y fecha. El cliente no escribe estado ni se autoaprueba. Las decisiones dejan auditoría; aceptar términos, subir fotos o pagar no constituye aprobación.

Antes de atender solicitudes reales, los responsables deben designar quién revisa, cómo comprueba autorización del lugar y cómo gestiona correcciones, visitas, retiro y apelación. La web de administración es una entrega separada. Mientras se utiliza la consola administrativa, solo los operadores autorizados pueden registrar decisiones; la revisión humana no se sustituye por una aprobación automática.

### 20.5 RF-69 — Una cuenta, dos contextos

Un perfil aprobado conserva su función de usuario. «Cambiar a receptor» abre Recepciones; «Cambiar a mis aparatos» retorna al contexto personal sin cerrar sesión. Los permisos se consultan al servidor. Una suspensión debe impedir una recepción nueva aunque el teléfono muestre información anterior.

El perfil aprobado muestra una marca y el texto «Perfil revisado». Esto significa revisión manual de la información presentada; no certifica gestión ambiental ni garantiza ausencia de fraude. Su significado completo queda en los términos.

El catálogo de receptores muestra nombre del lugar, dirección, horario, categorías y condiciones. No muestra nombre civil, fotografías de revisión ni correo de la cuenta.

### 20.6 RF-70 — Recorrido de entrega y código

1. Decisión: seleccionar qué hacer; «Pasar a preparación» requiere una decisión distinta de «Aún no he decidido».
2. Preparación: revisar las cinco tareas. «Hecho» y «No aplica» cuentan como revisado. Para recolección, «Generar código y QR» requiere cinco revisadas y abre directamente la selección de receptor y aparatos. No se fuerza encender, cargar o desmontar un aparato.
3. Crear entrega: seleccionar un receptor aprobado y entre 1 y 20 aparatos propios preparados, aún sin entrega completada. Sus categorías deben estar admitidas por ese receptor. Mostrar dirección, horarios, condiciones y advertencias de seguridad antes de confirmar.
4. Se crea una única entrega activa por usuario, que puede agrupar varios aparatos para un mismo receptor. Una segunda petición concurrente se bloquea en el servidor mediante restricción única y bloqueo transaccional.
5. Código: «Generar código de entrega» muestra QR y alternativa escrita. Se explica que se muestra al receptor en el lugar, no se publica y no confirma por sí solo. Renovar cambia el código de la misma entrega, invalida el anterior y no crea otra entrega.
6. Receptor: escanear o escribir, comprobar código y revisar lista. Solo el receptor asignado y aprobado puede hacerlo. Debe pulsar «Confirmar recepción» y aceptar la confirmación después de tener los aparatos. Si la lista no coincide, no confirma.
7. La confirmación explícita del receptor cambia atómicamente la entrega a completada, registra fechas del servidor e invalida el código. No se pide una segunda confirmación a quien entrega. La mera lectura del código deja el estado pendiente.
8. Resultado: el cliente visible consulta el estado autenticado cada 5 segundos y al volver a primer plano. Al recibir completada, muestra Resultado y mueve los aparatos al historial derivado de esa constancia sin otro guardado. No inventa fechas ni certifica reciclaje posterior.

Si la decisión es conservar, reparar o reutilizar sin recepción en Noki, se permite un resultado personal sin inventar una entrega. La opción de recolección no está disponible como declaración libre.

Estados: pendiente de recepción, recepción confirmada, entrega confirmada, cancelada. Pendiente y recepción confirmada bloquean otra entrega activa del mismo usuario. Una entrega confirmada permite preparar otra con aparatos diferentes.

#### Código y control de intentos

- QR con identificador de entrega y secreto aleatorio de 256 bits; no incluye datos personales.
- Alternativa escrita: 10 caracteres hexadecimales aleatorios, 40 bits de entropía.
- Se almacenan hashes de secretos; los textos del código solo se devuelven al dueño al generarlo y permanecen en memoria del cliente.
- Vigencia de diez minutos; una renovación invalida QR y alternativa anterior.
- Cinco comprobaciones fallidas por receptor en quince minutos. Un error no revierte el contador. Se requiere protección adicional por IP antes de apertura masiva.
- Leer un código registra validación temporal para el receptor asignado; confirmar recepción exige esa validación vigente y consume el código.
- Las confirmaciones son idempotentes: repetir no duplica eventos ni aparatos.
- La práctica utiliza un prefijo diferente y un receptor ficticio, visible como práctica local; nunca se acepta como código real.

#### Cancelación y conservación

«Cancelar entrega» solicita motivo de 5–300 caracteres y confirmación. Solo el dueño puede cancelar antes de la confirmación de recepción. Invalida el código, libera la entrega activa y conserva la preparación. Puede crear una nueva con los aparatos elegidos correctamente.

No se borran confirmaciones para ocultar una recepción. Después de recibir, la discrepancia se tramita con Noki sin marcar falsamente la entrega como cancelada. Los aparatos vinculados a constancias no se borran ni reabren como registros libres; los derechos de rectificación/supresión se atienden mediante el procedimiento de soporte.

### 20.7 RF-71 — Datos y acceso

| Entidad | Datos principales | Acceso |
| --- | --- | --- |
| Solicitud de receptor | Responsable, lugar, condiciones, fotos, estado, consentimiento, decisión | Titular y operadores autorizados |
| Entrega | Emisor, receptor, lugar, estado y fechas | Solo ambas partes |
| Aparatos de entrega | Referencia y copia de descripción/categoría/seguridad | Solo ambas partes |
| Secretos de entrega | Hashes, vigencia, validación previa | Servicio interno |
| Intentos | Cuenta, ventana temporal y contador | Servicio interno |
| Auditoría de revisión | Revisor, persona, estado, motivo y fecha | Operadores autorizados |

Las tablas nativas de recepción están en un esquema privado sin acceso directo de clientes. El punto de entrada público usa contexto de usuario y funciones internas que comprueban cuenta confirmada, sesión vigente, propiedad y estado. RLS y revocación de privilegios restringen el acceso; no se confía en campos editables de perfil para conceder permisos.

El trigger de aparatos impide editar durante una entrega activa y guardar resultado de recolección sin confirmación del receptor tras validar el código. Los registros anteriores de la aplicación web permanecen separados.

Las fotos solo se leen por el titular o por un operador autorizado; el cliente solo puede sustituirlas mientras la solicitud sea borrador. La web futura deberá limitar cada revisión al personal asignado, exigir autenticación reforzada y auditar las consultas. No se distribuyen claves de servicio en la app.

Retención propuesta: fotos de solicitudes retiradas o abandonadas deben eliminarse dentro de treinta días; las aprobadas se revisan al menos anualmente y se conservan mientras sean necesarias para el perfil. El procedimiento de eliminación debe borrar el objeto mediante Storage, no solo su fila de metadatos. Antes de publicación se debe concretar responsable operativo, copias de respaldo, tratamiento de controversias y comprobación de cumplimiento de plazos.

### 20.8 RF-72 — Activación y fallos

El alta pública de nuevas cuentas por código depende del servicio de correo probado. Las cuentas existentes con correo confirmado pueden acceder; no se fingen códigos enviados.

La recepción real requiere solicitud aprobada, operación de revisión y soporte disponible. Si no hay receptores aprobados, Crear entrega lo explica y conserva la preparación; no atribuye recepción al directorio ni crea un receptor ficticio fuera de la práctica.

Una pérdida de conexión conserva la información confirmada. Tras una respuesta incierta se consulta el estado antes de repetir. No se muestra éxito al fallar la carga, revisión, lectura del código o confirmación.

No forman parte de la aprobación automática: autenticidad civil, licencia de gestor ambiental, visitas no realizadas o ausencia de fraude. La información operativa debe coincidir con las capacidades disponibles.

### 20.9 Historias y criterios

| Historia | Criterios mínimos |
| --- | --- |
| HU-REC-01 — Conocer requisitos | Inicio/Perfil llevan a la misma explicación; dos casillas controlan Continuar |
| HU-REC-02 — Presentar solicitud | Cuenta confirmada, tres pasos, validaciones, tres fotos privadas y consentimiento |
| HU-REC-03 — Comprobar lugar | Revisión remota, correcciones accionables y visitas ocasionales coordinadas |
| HU-REC-04 — Conocer decisión | Estado obtenido del servidor; sin autoaprobación ni permiso por casilla |
| HU-REC-05 — Cambiar contexto | Misma sesión, marca de revisión, Mis aparatos/Recepciones separados |
| HU-ENT-01 — Preparar entrega | Una activa por usuario, 1–20 aparatos preparados, receptor aprobado y categorías válidas |
| HU-ENT-02 — Confirmar entrega | Código asignado, temporal y de un uso; confirmación del receptor antes del resultado |
| HU-ENT-03 — Corregir selección | Cancelación previa a recepción libera la entrega e invalida el código, sin borrar aparatos |
| HU-SEG-01 — Proteger información | Sin lectura entre terceros, rol autoconcedido, secretos públicos o fotos ajenas |

### 20.10 RF-73 — Acceso contextual estable

El formulario «Entrar o crear cuenta» se presenta en un contenedor fijo con un único desplazamiento vertical, sin arrastre simultáneo de hoja inferior. Mantiene margen seguro y acceso al teclado. Llegar al final con deslizamientos rápidos no mueve repetidamente el contenedor ni genera un rebote continuo. «Volver sin perder mi progreso» conserva la orientación y no crea cuenta ni aparato.

### 20.11 RF-74 — Navegación por etapas

Cada etapa termina con un botón que nombra el siguiente paso. Las pestañas respetan los mismos requisitos que los botones; no permiten saltar validaciones. Al cambiar de etapa se vuelve al inicio del contenido correspondiente. «Resultado» permanece deshabilitado para recolección hasta que el receptor confirma la recepción. Las pulsaciones rápidas no duplican guardados ni saltan etapas.

## 21. Inicio, acceso y perfil

### 21.1 RF-75 — Propósito visible en Inicio

Bajo el título principal y antes de las acciones, Inicio incluye: «Noki te ayuda a decidir qué hacer con tus aparatos eléctricos y electrónicos: seguir usándolos, repararlos o llevarlos a recolección. Aquí puedes encontrar lugares en Medellín y preparar tu entrega paso a paso.» El texto permanece disponible después de la bienvenida inicial. No promete recogida a domicilio, convenios ni aceptación garantizada por un lugar. Se ajusta al ancho y al tamaño de letra, sin altura fija ni recorte.

### 21.2 RF-76 — Foto y nombre de perfil

1. Una cuenta verificada accede a «Perfil y ajustes». Se muestran su correo, foto o icono de persona y nombre o «Añade tu nombre».
2. «Editar mi nombre» abre un campo de 2–80 caracteres. Guardar requiere confirmación del servidor; cancelar conserva el valor previo. Cambiar el nombre no cambia la identidad de acceso ni la aprobación como receptor.
3. «Añadir foto» o «Cambiar foto» abre el selector de imágenes del sistema. No se solicita acceso general a la galería. Cancelar el selector no modifica el perfil.
4. La imagen elegida se orienta correctamente y abre un encuadre manual circular: arrastre, zoom de 1× a 4× y controles alternativos de posición horizontal/vertical y zoom. «Mostrar la foto completa» encaja todo el rectángulo original dentro del círculo, con fondo neutro. Vista previa y salida usan la misma transformación normalizada; solo se recorta lo que el usuario deja fuera al ampliar o mover. Se guarda JPEG de 512 × 512 píxeles, sin metadatos de ubicación y con máximo de 512 KiB. El archivo original no se modifica.
5. Una vista previa ofrece «Guardar foto» y «Cancelar». La selección no inicia la carga automáticamente. Durante el guardado se impiden operaciones duplicadas.
6. La foto es opcional y privada. Solo su titular puede obtenerla, reemplazarla o eliminarla con sesión vigente y consentimiento actual. No se publica una URL de libre acceso ni se utiliza para reconocimiento facial o aprobación de receptores.
7. «Quitar foto» requiere confirmación; elimina el objeto mediante Storage y muestra el icono de persona. No elimina la foto original de la galería ni el nombre.
8. «Actualizar perfil» recupera nombre y foto del servidor. Si falla la lectura de la foto, se muestra una explicación y la posibilidad de reintentar. La descarga se limita a 512 KiB y dimensiones máximas de 1024 × 1024 para no decodificar imágenes excesivas.
9. Cerrar sesión elimina foto, borrador y nombre de la memoria de la interfaz. Una respuesta tardía de otra sesión no se presenta como la foto de la cuenta actual. La práctica local no carga fotos en el servidor.

### 21.3 RF-77 — Disponibilidad del correo y aceptación

La app consulta `email_otp_enabled` al iniciar y al abrir el acceso. Ante un fallo, mantiene el envío deshabilitado y permite «Comprobar disponibilidad». El indicador habilita el recorrido por código sin reinstalar la aplicación. No sustituye la confirmación de correo ni los permisos del servidor.

El mismo recorrido sirve para crear cuenta o volver a entrar. La primera solicitud puede crear una identidad sin confirmar; solo el código validado permite iniciar sesión. Se mantienen casillas explícitas antes de solicitarlo y constancia centralizada al completar el acceso. Las plantillas de alta y acceso incluyen `{{ .Token }}`; no contienen contraseñas de SMTP ni códigos fijos.

La versión de términos y privacidad es `2026-10-09.1`. Una sesión sin aceptación local vigente no carga registros personales. Aceptar registra la versión en el servidor antes de usar perfil y fotos; rechazar conserva la consulta pública. Las operaciones de perfil y fotos comprueban también aceptación vigente en servidor. Se informa del nombre de usuario obligatorio, su intervalo de cambio y la diferencia entre retirar de pendientes y solicitar supresión de datos.

«Retirar permiso en este teléfono» detiene localmente las funciones personales y cierra la sesión incluso si no hay conexión. Con conexión se intenta registrar la retirada y revocar la sesión en servidor. Si no se confirma, se indica la diferencia y el canal de contacto. No se anuncia eliminación de registros ni se promete revocación remota sin respuesta confirmada.

### 21.4 Contrato con administración

La aprobación, rechazo y solicitud de correcciones de un lugar se realizan por personal autorizado desde el sitio administrativo, sobre el mismo backend. La app envía solicitudes y consulta su estado; no aprueba ni asigna permisos administrativos. La foto personal y los aparatos de usuarios no forman un directorio público.

La vinculación posterior debe notificar cambios de estado autorizados, volver a consultar al recuperar conexión y respetar los permisos por cuenta. Una notificación no sustituye la comprobación del estado en servidor. La app ofrece actualización explícita mientras no esté disponible la suscripción a cambios. No se replica información privada indiscriminadamente hacia el navegador administrativo.

### 21.5 Criterios de aceptación

| Historia | Criterios |
| --- | --- |
| HU-HOME-03 — Comprender Noki | Propósito visible sin registro; acciones principales accesibles; texto completo a 200 % |
| HU-PRO-06 — Gestionar mi foto | Selección, vista previa, cancelación, guardado, sustitución, nueva consulta y eliminación; archivo original intacto |
| HU-PRO-07 — Mantener privacidad del perfil | Acceso ajeno denegado, consentimiento requerido, sesión revocada rechazada y limpieza al salir |
| HU-AUTH-08 — Entrar con código | Correo recibido, código validado, sesión confirmada y aceptación centralizada; errores sin éxito ficticio |
| HU-ADM-01 — Consultar revisión | App y administración comparten estado autorizado; solo revisores pueden decidir; reconexión recupera estado |

### 21.6 RF-78 — Nombre de usuario y bienvenida

El registro se completa eligiendo un nombre de usuario después de verificar el correo y aceptar condiciones. Las cuentas previas sin nombre completan el mismo paso sin perder sus registros. El servidor valida formato y unicidad, serializa los cambios por titular e impide cambios antes de 30 días desde la elección o último cambio. Reenviar exactamente el mismo valor es idempotente y no reinicia el plazo; cambiar solo mayúsculas también cuenta como cambio. Perfil muestra la fecha disponible. El nombre personal opcional no sustituye este identificador ni modifica el saludo.

Inicio muestra «Bienvenido, [nombre de usuario]» junto al logo; con texto ampliado puede ubicarse debajo para mantenerlo completo. El nombre no concede roles ni se presenta como identidad civil verificada. Las solicitudes de rectificación de datos se atienden por el canal de privacidad, sin quedar anuladas por el intervalo del alias.

### 21.7 RF-79 — Acceso visible y confirmación

Sin sesión, la esquina superior derecha contiene «Registrarse o iniciar sesión» como botón con desplazamiento suave de texto durante dos recorridos. Con movimiento reducido permanece estático; con texto grande admite «Entrar o registrarse» en dos líneas. Con sesión y aceptación vigentes muestra la foto privada o icono alternativo. El lector de pantalla identifica «Mi cuenta» y el estado del acceso.

Tras entrar o completar el registro, una confirmación temporal precede a Inicio, nunca dirige automáticamente a Mis aparatos. El tiempo base es 2,4 segundos y respeta el tiempo recomendado por accesibilidad. No anuncia éxito ante un error. Si se inició desde la orientación, Inicio permite retomarla explícitamente.

### 21.8 RF-80 — Pendientes, código e historial

Guardar una orientación crea un pendiente, no una entrega. «Mis aparatos» distingue Pendientes e Historial; el texto explica el siguiente paso y permite continuar la única entrega activa. «Crear entrega y generar código» crea la entrega agrupada y solicita su código. Si falla el segundo paso, conserva la entrega y ofrece reintentar el código, sin duplicarla. «Resultado» permanece bloqueado hasta comprobar el código y registrar la confirmación del receptor. No se crea un resultado por guardar la preparación.

Historial incluye entregas confirmadas, resultados personales y aparatos retirados. Las entregas canceladas se muestran por separado y no cuentan como realizadas. Un pendiente retirado conserva `archived_at` de servidor, no admite más cambios y no puede incluirse en entregas. No se acepta que el cliente modifique esa fecha o borre directamente el registro; `noki_archive_device` comprueba titular, sesión, aceptación, versión y ausencia de entrega activa o constancia. La retirada no equivale a supresión de datos.

### 21.9 RF-81 — Confianza basada en evidencia

Los avisos distinguen lugares publicados por operadores, receptores cuya solicitud revisó Noki y ejemplos ficticios. Solo un receptor aprobado puede figurar como revisado en la selección de entrega. El directorio público no implica convenio, aprobación operativa ni compatibilidad con códigos. Se recuerda comprobar horarios y condiciones y mostrar el código al entregar. No se afirma que todos los lugares estén verificados ni que no exista riesgo.

### 21.10 Verificación específica

HU-PRO-08: formato, nombre ocupado sin distinguir mayúsculas, titular ajeno, cambio anticipado rechazado, cambio tras 30 días y persistencia de fecha. HU-AUTH-09: éxito temporal, Inicio, contexto recuperable y cierre de sesión sin foto anterior. HU-PRO-09: gesto y controles de encuadre, restablecimiento, original intacto y texto 200 %. HU-DEV-05: guardado pendiente, código automático, la confirmación del receptor, retiro archivado y cancelación separada. Las pruebas instrumentadas históricas usaron `co.noki.app.qa`. Desde la instrucción del titular, no se reinstala esa variante en su VIVO ni se ejecutan recorridos que modifiquen su cuenta automáticamente.

### 21.11 RF-82 — Continuidad de sesión y paquete de entrega

La actualización conserva `co.noki.app`, su firma de desarrollo en este equipo, almacenamiento cifrado y claves de Android Keystore. No desinstala la app real ni modifica las identidades o perfiles existentes para pruebas. Una credencial verificada se guarda antes de cargar perfil/aparatos; una caída de red posterior no debe borrarla ni cerrar la sesión. El acceso privado sigue requiriendo consentimiento y autorización de servidor. Un perfil no cargado ofrece Actualizar y no se interpreta como un usuario nuevo sin nombre.

Google queda pospuesto por decisión expresa del titular del 8 de octubre. El diseño acordado para después es Google directo, con código por correo como alternativa; no se añadirá un segundo código obligatorio tras Google. En esta entrega se conservan correo/OTP y contraseña existente, sin activar OAuth ni cambiar SMTP/plantillas.

El ZIP incluye código y recursos de Android, núcleo Kotlin Multiplatform, Gradle Wrapper, pruebas, migraciones y documentación actualizada. Excluye secretos, keystores, sesiones, cuentas exportadas, cachés, SDK, archivos locales del IDE y APK de QA. Debe verificarse compilando desde una extracción limpia. Los clientes iOS y Windows siguen pendientes; no se describe este proyecto como Flutter ni se afirma paridad de plataformas.

### 21.12 RF-83 — Preparación directa, contraseña y avisos (0.7.2)

- «Preparar entrega» aparece en Inicio y Mis aparatos; una entrega activa abre esa misma entrega. La orientación pública sigue disponible sin cuenta.
- Preparar preselecciona recolección como intención editable; guardar deja un pendiente. Decisión → Preparación → selección de receptor/aparatos → código/QR. Se evita una pantalla intermedia sin contenido nuevo.
- Sin receptores aprobados se conserva el pendiente y se ofrece directorio o pendientes. Una referencia pública no recibe códigos ni se aprueba automáticamente.
- Correo/contraseña es la entrada principal. Registro y recuperación verifican primero el correo mediante código y ofrecen guardar contraseña. Código sigue como alternativa sin cambio obligatorio.
- Perfil permite crear/cambiar contraseña: mínimo 12 caracteres y máximo 72 bytes UTF-8 sin truncar, repetición coincidente y petición autenticada a la misma cuenta. Se respeta la reautenticación del servidor. Nunca se guardan contraseñas/códigos en preferencias, estado restaurable, logs o ZIP.
- Auth interpreta primero el campo simbólico error_code aunque code sea numérico. Un error de credenciales no revela existencia del correo; un fallo posterior de perfil no borra la sesión válida.
- Éxito: verde/chulo. Error: rojo/X. Texto e icono complementan el color y se anuncian a accesibilidad. No se duplica el aviso del formulario.
- El sondeo solo corre con pantalla visible y consentimiento; se serializa con acciones y renovación de sesión. Fallar la red conserva el estado y se comunica, sin completar por suposición.
- received se mantiene como estado legado, sin reescritura masiva. complete sigue compatible; nuevas recepciones confirmadas pasan directamente a completed.
- Pruebas: credenciales correctas/incorrectas, actualización autenticada de contraseña, sesión conservada, Resultado automático, texto 200 %, avisos únicos, idempotencia, código no reutilizable.

### 21.13 RF-84 — Publicación automática de receptores (0.7.3)

- Aprobar una solicitud con coordenadas confirmadas crea o actualiza exactamente un punto público con el mismo identificador de solicitud. Incluye nombre del lugar, dirección, horario, categorías, condiciones, coordenadas y WhatsApp autorizado. Nunca publica nombre civil, correo de acceso, fotos privadas o notas internas.
- El formulario exige confirmar la entrada exacta en un mapa navegable o buscar la dirección y ajustar el marcador; no se adivinan coordenadas a partir del nombre. Editar la dirección del borrador exige reconfirmarlas. Una aprobación nueva requiere coordenadas. Un lugar aprobado antiguo sin coordenadas puede completarlas una vez; reubicar un lugar aprobado requiere revisión.
- Suspender o retirar el lugar lo oculta; eliminar la solicitud conserva un punto inactivo para no romper referencias históricas. No se reescriben entregas ni constancias.
- Puntos consulta al entrar, volver al primer plano y cada 10 segundos mientras está visible. La selección de receptores se actualiza cada 5 segundos, solo con sesión válida. No se ofrece sincronización instantánea ni sondeo en segundo plano. Sin conexión se identifica la copia guardada como posiblemente desactualizada.
- Refrescar marcadores no mueve la cámara del mapa. El propietario ve su punto en el mapa, pero no puede enviarse entregas a sí mismo. Una referencia pública externa no se presenta como receptor habilitado para QR.

### 21.14 RF-85 — Contacto y generación de códigos sin callejones sin salida (0.7.3)

- «Seguir usándolo», reparar por cuenta propia o darle otro uso sin entregarlo explican que no requieren QR/código. Donación, regalo y recolección con un receptor de Noki usan la intención «Entregar, donar o llevar a recolección», preparación, entrega y confirmación del receptor. Una entrega informal declarada históricamente no se convierte en una constancia del receptor.
- El estado físico no es una prohibición permanente. Un estado no normal/desconocido o una categoría no anunciada exige consultar al receptor y marcar, sin preselección: «Confirmé con el receptor que puede recibir todos estos aparatos en su estado actual».
- La misma pantalla muestra condiciones, categorías no anunciadas, WhatsApp y acción para abrirlo. No envía mensajes automáticamente. El receptor declara y autoriza un contacto público con código de país; no se copia el correo de Auth. Puede actualizarlo desde su perfil, sin modificar su aprobación. Se audita la autorización.
- Cambiar receptor, lista de aparatos o sus datos reinicia la casilla. Sin contacto público se explica que debe elegirse otro receptor o esperar su actualización; la preparación no se pierde. Un fallo al abrir WhatsApp ofrece el número para contacto manual.
- El servidor exige la declaración en esos casos y comprueba que corresponda al WhatsApp actual. Conserva fecha y número del acuerdo declarado en la entrega y lo comunica a ambas partes. Es una declaración del remitente, no prueba de conversación, aceptación del receptor, reserva, certificación de seguridad ni sustitución de sus condiciones.
- El receptor puede no aceptar aparatos fuera de lo acordado; no debe confirmar una recepción inexistente. Se conservan las advertencias de no encender/cargar/desmontar aparatos dañados, no transportar ante peligro inmediato y pedir asistencia.
- La casilla no elude: sesión verificada, pertenencia de aparatos, preparación completa, máximo 20, receptor aprobado, prohibición de autoentrega, una sola entrega activa, bloqueo de resultados y validación/confirmación final del receptor.
- Cada botón deshabilitado explica su requisito pendiente: receptor, selección, preparación, contacto, límite o actualización. Aparatos que ya no están disponibles pueden retirarse de la selección sin borrar registros.
- Pruebas: aislamiento entre cuentas, autorización de contacto, publicación/retiro, excepción de categoría, estado desconocido, cambio de selección, código manual, QR, no completar con solo escanear, confirmación del receptor, texto 200 %, regresión de sesión y acceso.

El complemento informativo de receptores del 14 de octubre de 2026 explica la visibilidad pública sin sesión, el contacto por WhatsApp y la declaración conservada en la entrega. La autorización específica aparece antes de publicar el contacto y en la confirmación de ubicación; se audita por titular y fecha. No se transforma la aceptación general previa en autorización automática de estos datos ni se cierra una sesión existente para actualizar el directorio. La versión general de cuenta permanece 2026-10-09.1.

Por instrucción del titular del 14 de octubre, esta revisión se cierra en el proyecto original y la app instalada, sin exportar ni entregar un ZIP. La integración del panel web, Google, distribución definitiva y clientes iOS/Windows mantienen su alcance pendiente; no se confunden con las correcciones Android verificadas aquí.

### 21.15 RF-86 — Continuidad entre preparación, mapa y código (0.7.4)

Recorrido principal: **Preparar entrega → Orientación → Continuar con mi entrega → Decisión → Preparación → Elegir receptor y confirmar aparatos → Generar QR/código → Consultar ruta, si se necesita → Mostrar QR/código → Confirmación del receptor → Resultado**.

- El receptor se elige antes de emitir el código porque este solo permite recibir al destinatario indicado. Se puede elegir desde la lista de receptores o el mapa, sin repetir la orientación ni registrar otra vez los aparatos. El texto antes de generar explica ese paso pendiente.
- «Elegir receptor en el mapa» abre un recorrido contextual, no cambia a la pestaña pública perdiendo la selección. Al elegir el lugar, su ficha se abre automáticamente con el siguiente paso y las alternativas de origen. Al regresar a confirmar se mantiene la lista de uno o varios aparatos, el identificador de creación y el receptor elegido. Por seguridad se vuelve a pedir la declaración de contacto si corresponde; no se infiere de consultar una ruta.
- La proyección autenticada `native_action('state').receivers` añade `request_id`. Se vincula exclusivamente con `CollectionPoint.receiver_request_id`; nunca se comparan nombre, dirección o teléfono para decidir quién recibe. El identificador del usuario receptor sigue siendo distinto del identificador público del punto. No se publican datos privados nuevos ni se modifican permisos o acceso.
- En la ficha, la página del directorio y el mapa ampliado se ofrece el mismo siguiente paso: «Continuar y generar QR» para una preparación completa sin entrega, o «Mostrar QR y código» para una entrega pendiente. Tras calcular la ruta queda visible esta acción dentro del mapa; no es necesario cerrar pantallas ni buscar la entrega en otra pestaña.
- La ruta es opcional: denegar ubicación, escribir una dirección, cancelar el cálculo o un fallo de conexión del servicio de rutas no impiden volver a la confirmación de aparatos. El cálculo de ruta no crea, completa ni cambia una entrega. Cambiar el marcador consultado elimina la ruta anterior para no confundir destinos.
- Desde el código se accede a «Ver lugar y calcular ruta», con el punto de su receptor preseleccionado y su ficha abierta. «Mostrar QR y código» vuelve a la misma entrega, sin crear otra ni sustituir el destinatario. Si el código venció se renueva el de esa entrega mediante el control existente.
- Consultar otro establecimiento cuando ya existe una entrega no cambia su receptor. Se indica explícitamente que sigue dirigida al receptor original. Para cambiarlo se conserva el flujo de cancelación con motivo y creación posterior, sin ocultar la constancia.
- Sustituido por RF-87: se elimina el filtro de receptores y la acción «Elegir receptor con código». Todos los puntos publicados siguen visibles, incluso para el propietario; la elegibilidad para una entrega se explica en la ficha sin filtrar el directorio.
- Si falta preparación, «Continuar preparación» guarda el lugar de referencia y abre Preparación; si todavía no hay decisión, abre Decisión. No se permite saltar las tareas pendientes ni desbloquear Resultado por elegir un punto.
- Una recepción completada mientras se consulta el mapa ofrece su estado/resultado en lugar de un nuevo QR pendiente. La comprobación final sigue siendo del servidor y del receptor; escanear sin confirmar no completa la entrega.
- Se mantienen correo/contraseña, código alternativo, sesiones, aceptación general, una entrega activa y app única `co.noki.app`. No se instala una variante de prueba en el VIVO ni se exporta ZIP.

Criterios de comprobación: continuidad de selección agrupada al salir/volver del mapa; receptor resuelto por ID aunque coincida el nombre de otro; referencia pública sin QR ficticio; disponibilidad/autoentrega; regreso a preparación incompleta; código real de la respuesta mostrado en pantalla; renovación en la misma entrega; acciones visibles a 200 %; regresión de acceso, contacto, caducidad y confirmación del receptor. La evidencia ejecutada y sus límites figuran en `docs/VERIFICACION.md`.

### 21.16 RF-87 — Destinos siempre visibles y QR desde el lugar elegido (0.7.5)

Corrige la regresión notificada por el titular en 0.7.4: el mapa contextual activaba un filtro basado en receptores disponibles para el usuario autenticado. Como la cuenta propietaria no puede enviarse una entrega y solo había un receptor aprobado, el filtro vaciaba el directorio aunque existían tres puntos publicados. Las pruebas anteriores de continuación no cubrían esa combinación; no se interpreta su aprobación como evidencia de este caso.

- **Visibilidad y autorización son independientes.** Mapa, selector de direcciones, lista y preparación muestran todos los puntos públicos activos, sin intersectarlos con los destinatarios autenticados. La búsqueda y el filtro explícito de categorías pueden reducir la lista; la categoría inicial de orientación no la reduce automáticamente. «Ver todos los lugares y actualizar» limpia esos filtros.
- Se eliminan «Receptores con código Noki» y «Elegir receptor con código». Sin destino, el texto indica tocar un lugar del mapa o de la lista. «Ver otros lugares» vuelve a la lista completa, sin activar un filtro oculto.
- **Recorrido:** aparatos preparados → elegir lugar → confirmar contacto solo cuando corresponda → «Generar QR y código» → pantalla con QR/código → «Ver lugar y calcular ruta», opcional. Elegir un marcador para consultar información no crea por sí solo una entrega. El botón explícito de generación evita crear compromisos accidentales.
- La ficha permite generar directamente, sin regresar a un selector. Muestra aparatos incluidos, condiciones y contacto. Se conservan todos los identificadores seleccionados al abrir el mapa; una selección vacía no se rellena automáticamente con el primer aparato. No se repite orientación ni preparación.
- Desde preparación se muestran también las referencias públicas y el lugar propio. Si temporalmente no cargó el directorio público, un receptor disponible en la consulta autenticada sigue accesible por nombre/dirección, sin inventar coordenadas.
- La cuenta propietaria ve su lugar con la explicación «Tu lugar de recepción». Otros usuarios pueden elegirlo; no se habilita autoentrega para superar una prueba. Una referencia externa puede consultarse y calcular su ruta, pero no obtiene una constancia QR que nadie pueda confirmar. Una suspensión o fallo de actualización no se presenta como desaparición de todos los lugares.
- La generación valida sesión y consentimiento existentes, preparación completa, 1–20 aparatos, una sola entrega activa, disponibilidad y declaración de contacto si procede. El servidor sigue siendo la autoridad. No se cambia Auth, RLS, aprobación, condiciones de Express.arte ni sus contactos.
- La respuesta de generación abre directamente la entrega pendiente y muestra el QR y código reales emitidos por el servicio. La nueva navegación sustituye el selector/mapa en lugar de apilarlos detrás del QR. Volver desde una ruta abre la misma entrega.
- Error de creación visible dentro de la propia ficha, con reintento; no solo detrás del panel. Si se creó la entrega pero falló la emisión del código, se abre esa misma entrega con acción para generarlo de nuevo, sin duplicar aparatos ni entregas.
- Consultar otra ubicación no cambia la entrega existente. Resultado continúa bloqueado hasta la confirmación del receptor. Se mantienen caducidad, renovación, cancelación auditada y bloqueo de reutilización.
- Verificación mínima de regresión: propietario con cero destinatarios y tres puntos, otra cuenta eligiendo receptor, referencias sin QR ficticio, cambio de disponibilidad sin ocultar puntos, grupo de aparatos generado desde la ficha, selección vacía, errores de creación/token, texto 200 %, y suites de acceso/sesión existentes. Los límites de las pruebas físicas se registran en VERIFICACION.
- Android `co.noki.app` 0.7.5/código 13; SPEC 3.13. Actualización sobre la misma instalación, sin otra app, sin borrar datos y sin exportar ZIP.



