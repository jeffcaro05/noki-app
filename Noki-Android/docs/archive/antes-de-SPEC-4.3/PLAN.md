# Noki App — PLAN

Actualizado: 21 de octubre de 2026. Línea base: SPEC 4.2; Android 0.7.7.

## Estado y siguiente etapa

La implementación Android incluye acceso por correo/contraseña y código, orientación, perfil, directorio, preparación, entrega y recepción. La corrección 0.7.7 explica qué falta en cada paso de solicitud de receptor y lleva al primer requisito pendiente. Conserva el acceso y el mismo QR/código mientras la entrega siga pendiente. Los resultados se registran en VERIFICACION; no acreditan todos los requisitos del SPEC: las brechas y validaciones humanas pendientes se conservan en TASKS.

La administración web ya está publicada en https://noki-89c7b.web.app y usa el servicio común. En 0.7.7 el alcance es corregir la validación visible de la solicitud de receptor, probar sus tres pasos y conservar la instalación existente. No se modifican permisos del servidor, acceso por correo, QR ni proveedores sociales.

### Corrección 0.7.7

1. Centralizar los requisitos pendientes por campo y paso, conservando las restricciones.
2. Mostrar mínimos y contadores, guía junto a casillas/fotos, resumen dinámico y desplazamiento al primer requisito pendiente al intentar continuar.
3. Explicar la reconfirmación del mapa tras cambiar la dirección y los requisitos de las aceptaciones previas.
4. Probar límites, casillas, coordenadas, fotos, retorno, envío único y texto al 200 % en el computador, sin solicitudes reales de prueba.
5. Compilar, ejecutar regresiones y análisis estático, y actualizar solo el dispositivo autorizado sin desinstalar ni borrar datos. Registrar la instalación fuera del SPEC.

## Tecnología

| Componente | Elección | Función |
| --- | --- | --- |
| Entorno | Android Studio | Edición, compilación, instalación y depuración |
| Lenguaje | Kotlin | Lógica del cliente Android |
| Núcleo multiplataforma | Kotlin Multiplatform | Modelos y reglas compartidos, pruebas comunes, destinos JVM/iOS |
| Interfaz | Jetpack Compose y Material 3 | Pantallas y controles nativos |
| Navegación | Navigation Compose | Destinos, Atrás y estado de navegación |
| Estado | ViewModel, StateFlow y corrutinas | Separar pantalla, operaciones y datos |
| Servicios | Supabase Auth y PostgreSQL | Cuentas, registros propios y catálogo público |
| Protección local | Android Keystore | Cifrado de sesiones y borradores |
| Mapa | MapLibre Native | Mapa nativo con atribución cartográfica |
| Rutas | Servicio OSRM y salida externa | Ruta estimada, sin promesa de tráfico en vivo |
| Pruebas | JUnit, Robolectric y pruebas instrumentadas | Reglas, interfaz y ejecución en dispositivos |
| Entrega | APK firmado; AAB para tienda | Instalación y actualización nativa |

No se utiliza una página web dentro de WebView como interfaz de Noki. La aplicación web existente se mantiene separada y no se sobrescribe al desarrollar Android.

## Secuencia de construcción

### 1. Entorno y estructura

Instalar Android Studio, SDK y JDK compatible. Mantener el proyecto independiente, fijar versiones y comprobar una compilación desde una extracción limpia. Configurar las rutas del SDK y las cachés en cada equipo, sin distribuir rutas personales. Utilizar pruebas automatizadas y dispositivos de ensayo sin afectar cuentas reales.

Salida: proyecto que abre en Android Studio, compilación y pruebas ejecutables, APK instalable.

### 2. Identidad, navegación y orientación

Incorporar los logos originales sin deformación y Poppins local. Implementar componentes comunes, Inicio, navegación, tres ramas de Orientación y Aprende. Probar Atrás, rotación, escala de texto, ausencia de conexión y controles accesibles.

Salida: recorrido público nativo funcional, sin botones decorativos ni datos ficticios.

Componentes compartidos de interfaz: Page, PrimaryButton, ActionCard, Choice, Note y selectores. La bienvenida vive en NokiRoot; las preferencias y la vista de accesibilidad están separadas de los flujos de aparatos. La animación de entrada no añade esperas de red. Movimiento reducido se transmite a navegación, orientación, botones y mapa.

### 3. Cuentas y protección de datos

Inspeccionar el esquema remoto; crear las tablas necesarias mediante cambios aditivos que no alteren la aplicación existente. Configurar correo transaccional y App Links. Implementar registro, verificación, recuperación, sesión cifrada, perfil, cambios y eliminación segura de cuenta. Revisar aislamiento entre dos identidades antes de habilitar datos privados.

Salida: ciclo de cuenta comprobado con correos reales; pruebas negativas de acceso y eliminación.

Acceso por correo: configurar un remitente y servicio SMTP, incorporar `{{ .Token }}` en las plantillas de alta y acceso y comprobar entrega real antes de activar `noki_client_config.email_otp_enabled` para pruebas integradas. Completar alta, código erróneo/vencido/usado, reenvío y varios dominios antes de distribución pública. Las pruebas con respuestas controladas no sustituyen el correo recibido. Google y Apple requieren clientes de proveedor, autorización y redirecciones seguras; no se publican botones activos sin esa configuración. El formulario se presenta como hoja contextual fija cuando una acción privada parte de la orientación.

### 4. Directorio, mapa y ruta

Publicar únicamente fichas cuya dirección, fuente, fecha y coordenadas hayan sido comprobadas. Implementar búsqueda, categorías, detalle, mapa nativo, selección común entre lista y mapa y advertencia de antigüedad. Pedir ubicación solo al solicitar una ruta. Incorporar alternativa textual y externa, junto con copia pública local.

Salida: recorrido lugar → fuente → ruta, también con ubicación denegada y proveedor no disponible.

La vista previa no captura arrastres. La navegación cartográfica se realiza en un diálogo ampliado, separado del desplazamiento de la página, con retorno visible y confirmación manual del origen.

### 5. Mis dispositivos

Implementar alta manual y desde Orientación, edición, eliminación, decisión, referencia de lugar, preparación y resultado personal. Utilizar idempotencia y protección frente a ediciones simultáneas. Añadir historial con búsqueda y filtros. La intención no se presenta como una acción realizada.

Salida: preparar entrega → decidir → preparar → elegir receptor/aparatos → generar código → receptor confirma recepción → resultado e historial automáticos. Conservar/reparar/reutilizar mantiene resultado personal sin inventar una entrega. Una constancia de recepción confirmada no se reabre como registro libre.

### 6. Distribución y validación

Configurar firma de distribución fuera del repositorio, versión, manifiesto de actualización y conservación de datos. Ejecutar la matriz del SPEC: errores de red, proveedor pausado, accesibilidad, permisos, teléfonos/tabletas y cinco participantes del público objetivo. Resolver defectos antes de liberar.

Salida: APK/AAB firmado, pruebas y requisitos trazables a la versión entregada.

## Reglas de ejecución

- Cada tarea enlaza HU y criterios de aceptación; una pantalla visible no basta para cerrar una historia.
- Las tareas se marcan completadas solo con prueba registrada.
- El SPEC contiene el comportamiento contractual; este plan y TASKS contienen la organización del trabajo. No se incorporan relatos de correcciones al SPEC.
- Un cambio funcional exige ajustar requisitos y pruebas en el mismo conjunto de cambios del código.
- No se asignan números de issues inexistentes ni se publica en GitHub sin resolver el destino del proyecto Android.
- No se borran tablas ni usuarios de la aplicación existente.
- La capacidad histórica es 120 horas brutas entre dos integrantes durante cuatro semanas. No se multiplica por sprint ni se presupone una reducción del 20 %. La duración de sprints debe acordarse antes de calendarizar.

## Condiciones de publicación

### Plataformas

1. Android: completar y verificar el flujo público y privado en teléfonos; validar también tableta y Android mínimo antes de distribuir.
2. iOS/iPadOS: disponer de Mac y Xcode; integrar NokiCore en una interfaz SwiftUI, almacenamiento Keychain, mapas y enlaces del sistema. Compilar, firmar y probar en iPhone e iPad. Conectar un iPhone a Windows no permite ejecutar esta fase.
3. Windows: construir una interfaz Compose Desktop sobre el núcleo JVM; adaptar teclado, ventana, mapa y almacenamiento protegido. Generar instalador y ejecutar la misma matriz funcional. El sitio web administrativo sigue siendo un producto distinto.

No se declara paridad entre plataformas hasta aprobar las pruebas de cada una. No se contratan servicios de compilación ni cuentas de distribución sin aprobación del equipo.

### Pruebas de recorrido

Desde 0.7.1 la práctica no se ofrece en la app entregada. Las pruebas de sesión se ejecutan con datos sintéticos; no se instala otra app en el teléfono de uso personal ni se usan recorridos destructivos sobre cuentas reales. Las pruebas de RLS se ejecutan dentro de una transacción que termina en rollback. La revisión visual requiere autorización; no se elimina el bloqueo ni se desactivan protecciones del sistema.

Correo de contacto: nokiappcorporation@gmail.com. El proveedor de envío debe admitir verificación y recuperación para los usuarios previstos; el servicio de correo predeterminado no se da por suficiente sin probarlo. La firma de distribución se custodia por el equipo. La publicación en tienda y cualquier coste o contratación requieren una decisión explícita.

### Apariencia, cartografía y consentimiento

Separar preferencia de apariencia, tema animado, documentos y almacenamiento de aceptación en componentes propios. Mantener solicitud vertical en teléfonos y diseño adaptable cuando el sistema no respete la restricción. Usar geocodificación nativa mediante petición explícita, con confirmación del resultado y alternativa manual. No añadir claves de geocodificación al cliente.

Verificar los tres puntos de entrada del mapa, rechazo de ubicación, búsqueda vacía/fallida, persistencia de apariencia, tamaños 100/125/130/200 %, textos centrados y contraste en ambos temas. Las pruebas usan direcciones públicas, nunca la ubicación privada del propietario. Revisar aceptación/rechazo, control de carga de sesión previa y retirada del permiso local. Completar constancia remota de aceptación y revisión jurídica antes de registro público.
## Lugares y receptores

1. RF-65: separar destino y partida; integrar selector desplegable, ficha directa y retorno a la ficha después de elegir salida. Mantener lista y direcciones legibles con texto 200 %. Probar sin GPS y sin coincidencias.
2. RF-66: explicación desde Inicio y Perfil con dos casillas, entrada contextual a cuenta y solicitud guiada.
3. Registro público: resolver SMTP/remitente, código real y constancia general de aceptación. Las cuentas existentes requieren correo confirmado; no se inventan verificaciones.
4. RF-67–68: revisión remota de datos y tres fotografías del lugar, sin cédulas ni biometría. Carga JPEG privada, eliminación de metadatos, estados, corrección y decisión manual auditada. Coordinar visitas ocasionales cuando sean necesarias; no exigir visita inicial. El servidor concede el rol, nunca una casilla local.
5. RF-69: verificar lugar, representación, categorías y condiciones. Separar el catálogo de referencia de los receptores aprobados; no convertir fichas públicas existentes en cuentas operativas.
6. RF-70: una entrega activa por usuario, hasta veinte aparatos, receptor aprobado y QR/código escrito estables mientras esté pendiente. Escanear o introducir el código permite revisar; la confirmación explícita del receptor completa y abre Resultado. Cancelar antes de recibir invalida el código y conserva preparación. Probar recuperación del mismo código tras reabrir o reingresar, concurrencia, reutilización después de cierre, cuenta ajena y respuesta perdida. Los códigos de acceso por correo mantienen su caducidad propia.
7. RF-71–72: permisos mínimos, evidencias privadas, registro de accesos, bloqueo por suspensión y borrado real. Probar escalamiento de privilegios, concurrencia, falsificación de decisiones, caducidad, denegación, cancelación y accesibilidad.
8. Preparar contratos de administración sin construir todavía la web. No habilitar el flujo real hasta disponer de revisores y atención de incidentes.

9. RF-73–74: contenedor de acceso fijo sin arrastre de hoja, desplazamiento estable y botones «Pasar a…». Las pestañas no saltan validaciones; la ficha del destino prioriza ruta y el cálculo lleva al mapa ampliado.

La compilación 0.5.0 incorpora la solicitud de receptor y el recorrido de entrega. La aprobación exige una decisión humana; el sistema no certifica identidad civil. Antes de uso público quedan revisión jurídica, operación de revisión/soporte, validación de imágenes en servidor, borrado operativo, protección adicional contra abuso y recorridos con cuentas reales. La evidencia de cada versión se mantiene en VERIFICACION.md.

## Inicio y cuenta — RF-75–77

1. Mantener un resumen breve del propósito de Noki en Inicio, antes de las acciones; comprobar lectura completa con texto ampliado.
2. Guardar consentimiento vigente y nombre en servidor, con propiedad y sesión comprobadas. Probar rechazo, revocación y acceso desde otra identidad en transacciones revertidas.
3. Separar el componente de foto de perfil: selector del sistema, recorte/preparación, vista previa, subida explícita, actualización y borrado. Probar límites de tamaño, privacidad, cancelación y conservación del original.
4. Habilitar acceso por código de forma centralizada después de comprobar el envío. El usuario introduce el código exclusivamente en la app; no se copia a registros, documentación ni chat.
5. Ejecutar registro real, edición del nombre, foto y reingreso con consentimiento persistente. Registrar por separado la evidencia de cada paso, los errores y las pruebas pendientes.
6. Las decisiones de revisión de lugares pertenecen a la administración web. Mantener el contrato y la base de datos común; implementar la web y sus notificaciones de cambios después de recibir su proyecto, sin conceder permisos administrativos al cliente móvil.

## Cuenta y pendientes — RF-78–81

1. Nombre de usuario obligatorio, único y con intervalo de 30 días comprobado en servidor. Conservar las cuentas existentes y solicitar completar nombre y nueva aceptación, no generar identidades nuevas.
2. Cabecera adaptable con acceso desplazable, foto privada y saludo; éxito temporal seguido de Inicio con orientación recuperable.
3. Encuadre manual y controles accesibles con la misma geometría en vista previa y JPEG. Probar texto 200 %, cancelación y archivo original intacto.
4. Pendientes distintos de constancias, código al crear entrega y archivo de pendientes retirados; SQL con transacciones revertidas para restricciones e idempotencia. No aprobar lugares reales para pruebas.
5. Avisos de confianza específicos del estado real del lugar. Mantener la web administrativa fuera de esta fase.
6. La variante técnica `co.noki.app.qa` se conserva únicamente para entornos de desarrollo separados: no se instala en el teléfono de uso personal del titular. Actualizar únicamente `co.noki.app` sin borrar datos, después de pruebas locales y análisis; documentar por separado la revisión visual y el recorrido real pendiente del titular y receptor.

## Continuidad de cuenta y entrega del proyecto — RF-82

1. Mantener SMTP, plantillas OTP, cuentas, rutas de API y claves de cifrado existentes. Un fallo al cargar perfil o aparatos no debe cerrar la sesión, borrar credenciales ni forzar un registro nuevo.
2. Distinguir perfil todavía no cargado de perfil cargado sin nombre de usuario. Ofrecer Actualizar antes de pedir otro nombre; conservar sesión si falla la red tras verificar el código.
3. Google se pospone por indicación del titular. La decisión futura es Google directo con el código por correo como alternativa, no un código adicional obligatorio tras Google. No activar proveedor ni sustituir el login actual en esta entrega.
4. Entregar ZIP para Android Studio con Gradle Wrapper, código Android/Kotlin Multiplatform, recursos, pruebas, migraciones y documentación. Excluir secretos, claves de firma, rutas locales, cachés y APK de QA. Registrar la firma compatible y la limitación de reconstruir en otro equipo sin compartir claves privadas.

## Criterios de la entrega 0.7.6

1. SPEC, PLAN y TASKS independientes en la raíz del ZIP e idénticos a los del proyecto.
2. Carpeta `Noki-Android` completa para Android Studio; sin SDK, JDK, cachés ni configuración local del IDE. Las herramientas y dependencias se instalan o descargan en el equipo de destino.
3. APK ya compilado de `co.noki.app` separado de las fuentes; sin APK de QA ni claves privadas de firma.
4. Comprobar integridad de los archivos mediante SHA-256 y compilar/probar las fuentes extraídas, sin instalar otra app ni operar cuentas reales.
5. Conservar migraciones como historial técnico. Abrir y ejecutar contra el proyecto existente no requiere volver a aplicarlas ni copiar datos del servidor.


