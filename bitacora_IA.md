# Bitácora de uso de IA de Noki

## Alcance y responsabilidad

Consolidada el **24/10/2026** para la entrega académica de Noki. Responsables humanos: Jeffry Giovanny Caro Arango y Luis Miguel Castañeda. Jeffry declaró uso de ChatGPT para programación, integración, pruebas y documentación, con COP 200.000 de gasto total. Luis declaró uso de planes gratuitos de Antigravity. No se dispone de sus conversaciones para atribuirle prompts o resultados concretos.

Esta bitácora reconstruye decisiones a partir de la conversación de desarrollo, los SPEC y los informes conservados. Los prompts se **resumen**, salvo las frases entre comillas; no es una exportación íntegra ni un registro cronológico exhaustivo. Las fechas exactas se indican únicamente cuando existe un corte documental comprobable. No se fabrican sesiones, tiempos, modelos usados, aprobaciones ni pruebas de personas.

El asistente propuso y produjo código, pruebas y textos; Jeffry revisó recorridos, señaló defectos y decidió qué conservar. Los resultados aceptados siguen siendo responsabilidad del equipo. Las pruebas automáticas complementan, pero no reemplazan, la revisión humana.

## Registro de decisiones

### IA01 Propósito y navegación de la app

- **Solicitud resumida:** explicar para qué sirve Noki, mejorar la apariencia móvil y evitar bucles, textos amontonados y botones sin respuesta.
- **Apoyo de IA:** componentes de interfaz, estados, instrucciones de continuación y pruebas de recorridos.
- **Decisión humana:** conservar orientación breve y comprensible, con márgenes, estados visibles y acceso a las funciones principales.
- **Resultado localizable:** `Noki-Android/app/src/main/java/co/noki/app/ui/NokiApp.kt`, `NokiRoot.kt`, `BottomDestinations.kt`; SPEC app y matriz.
- **Control:** revisiones de Jeffry y pruebas de experiencia. No se considera una certificación de accesibilidad universal.

### IA02 Plataforma de entrega

- **Solicitud resumida:** valorar conservar el trabajo y usar Flutter para varias plataformas.
- **Apoyo de IA:** explicación del cambio de tecnología y sus consecuencias.
- **Decisión humana:** «dejémoslo así entonces tal vez más adelante se cambie»; mantener Android nativo.
- **Resultado:** Kotlin y Jetpack Compose, Android 0.7.7; no se presenta como Flutter ni como app iOS terminada.
- **Control:** `Noki-Android/app/build.gradle.kts`, `MainActivity.kt`; ADR01.

### IA03 Acceso por correo sin perder las cuentas existentes

- **Solicitud resumida:** registro real, correo de verificación, acceso por contraseña y recuperación, sin dañar usuarios ya registrados.
- **Apoyo de IA:** configuración guiada del envío, plantillas de códigos, lógica de acceso y pruebas de preservación de sesión.
- **Intervención humana:** el propietario configuró la contraseña de aplicación directamente en el proveedor; confirmó que llegó el correo y que pudo entrar. Después pidió no exigir código en cada acceso con contraseña válida.
- **Resultado:** contraseña para cuentas confirmadas; código alternativo y recuperación. Google/Apple quedan desactivados por decisión de alcance, no simulados como funcionales.
- **Control:** `EmailAccessTest.kt`, `SessionPreservationTest.kt`, `docs/CONFIGURACION-ACCESO.md`.
- **Privacidad:** esta bitácora no contiene contraseñas, OTP ni códigos OAuth.

### IA04 Perfil y accesibilidad

- **Solicitud resumida:** nombre obligatorio, bienvenida, foto encuadrable, cambio de nombre cada 30 días, tema claro/oscuro y transiciones suaves.
- **Apoyo de IA:** componentes de perfil, encuadre, preferencias y estados de interfaz.
- **Decisión humana:** respetar legibilidad, márgenes y movimiento reducido; mantener una sola app instalada.
- **Resultado y comprobación:** `AccountHeader.kt`, `ProfileCropDialog.kt`, `AccessibilityScreen.kt`, `PhotoFrameTest.kt`, `ProfileAccessTest.kt`. Reglas detalladas en SPEC 4.3.

### IA05 Recepción sin proveedor biométrico de pago

- **Solicitud inicial resumida:** verificar documentos y personas para evitar estafas.
- **Alternativa examinada:** proveedor especializado de autenticidad documental y prueba de vida, con costo.
- **Decisión humana:** quitar la opción pagada. Revisar información y fotos del lugar; avisar sobre posibles visitas, sin exigir que el equipo visite cada solicitud antes de aprobar.
- **Resultado:** solicitud en la app y decisión administrativa en la web. No se promete reconocimiento facial, autenticidad documental garantizada ni ausencia de fraude.
- **Control:** `ReceiverApplicationForm.kt`, `ReceiverApplicationScreen.kt`, pruebas de formulario y funciones administrativas; ADR05.

### IA06 Entrega y Resultado

- **Solicitud resumida:** preparar entrega desde Inicio y Mis aparatos; guiar decisión y preparación; mostrar código y QR antes de Resultado.
- **Decisión humana explícita:** completar con la confirmación del receptor, sin exigir una segunda confirmación del remitente.
- **Resultado:** una entrega activa de uno o varios aparatos; escanear o ingresar el código abre revisión, pero no completa por sí solo. La confirmación del receptor cierra la entrega; cancelar permite preparar otra.
- **Control:** `Delivery.kt`, `DeliveryScreens.kt`, `DeliveryTest.kt`, `DeliveryExperienceTest.kt` y migración de confirmación del receptor.

### IA07 Decisiones que no implican entrega y advertencias

- **Solicitud resumida:** si la persona conserva el aparato, explicar que no se genera código; si necesita contactar al receptor, indicar cómo y permitir continuar después de confirmar el contacto requerido.
- **Apoyo de IA:** mensajes y validación de condiciones para evitar pantallas sin salida.
- **Corrección adoptada:** no tratar una decisión personal como recepción; no usar un aviso genérico como sustituto de una acción siguiente.
- **Control:** `Journey.kt`, `ReceiverContact.kt`, `ContactDeliveryExperienceTest.kt`; mantener las advertencias de seguridad descritas en SPEC.

### IA08 Directorio y continuidad del mapa

- **Solicitud resumida:** publicar automáticamente nuevos receptores aprobados, conservar lugares de referencia y evitar perder el QR al elegir destino o calcular ruta.
- **Decisión humana:** simplificar etiquetas y recorrido; distinguir referencia de receptor habilitado y no inventar convenios.
- **Resultado:** selección de destino, continuidad de entrega, punto de partida por ubicación con permiso o dirección; acceso al código conservado.
- **Control:** `DeliveryPlacePicker.kt`, `DeliveryMapContinuation.kt`, `DirectoryAutoRefresh.kt`, `PublishedDestinationsTest.kt`, `DirectorySyncTest.kt`.

### IA09 Código persistente

- **Solicitud:** el cambio del QR aproximadamente cada nueve minutos no correspondía al tiempo de traslado.
- **Decisión humana:** conservar el mismo código hasta la recepción; mantener también la cancelación coherente con una entrega anulada.
- **Resultado documentado al 21/10/2026:** código estable durante la entrega pendiente, invalidado al cerrarla. No se convierte en un permiso permanente para entregas distintas.
- **Control:** `StableDeliveryCodeTest.kt`, `DeliveryCodeTest.kt`, `20261015063551_stable_handoff_codes.sql`; ADR04.

### IA10 Web administrativa real

- **Solicitud resumida:** convertir la web inicial de Luis en un panel funcional que use los datos de la app, revise receptores y limite acceso a administradores autorizados.
- **Apoyo de IA:** integración, permisos, componentes, consulta de actividad y pruebas con servicios interceptados.
- **Decisión humana:** reutilizar el alojamiento Firebase y los datos Supabase existentes, sin una segunda base de datos ni dependencia de USB.
- **Resultado:** panel React/TypeScript, Supabase compartido y Firebase Hosting para archivos. Jeffry confirmó el acceso a datos reales en el panel publicado.
- **Control:** `Noki-Web-Admin/SPEC-WEB.md`, `supabase/tests/admin_security.sql`, `admin_publication.sql`; ADR02 y ADR03.

### IA11 Requisitos visibles y diseño web

- **Solicitud resumida:** explicar qué impide avanzar o aprobar y mejorar estéticamente la web sin romper la vinculación.
- **Apoyo de IA:** validaciones visibles, estilos, animación de acceso y pruebas de tamaños, contraste, teclado y movimiento reducido.
- **Decisión humana:** preservar los contratos de acceso y datos; no desactivar comprobaciones para habilitar botones artificialmente.
- **Control:** `ReceiverApplicationExperienceTest.kt`, pruebas unitarias y de navegador de la web. Un análisis automático de accesibilidad no sustituye una evaluación con personas.

### IA12 Publicación y secretos

- **Solicitud resumida:** publicar la web en la dirección existente y explicar la seguridad de la clave pública de cliente.
- **Apoyo de IA:** guías de conexión y publicación y explicación de la separación entre configuración pública y credenciales privadas.
- **Incidencia documentada:** el informe web registra que una consulta de Firebase CLI expuso credenciales en una salida local de herramienta; se informó al propietario y se cerró/revocó la conexión según las comprobaciones documentadas. No se copia esa salida ni se afirma que el procedimiento original no tuvo errores.
- **Control adoptado:** excluir sesiones y registros de autenticación; no solicitar códigos en el chat. Los permisos del servidor siguen siendo necesarios aunque la clave del cliente sea publicable.

### IA13 SPEC y paquetes de código

- **Solicitud resumida:** que el SPEC corresponda con lo implementado y el profesor pueda revisar el proyecto completo sin APK ni datos de teléfonos de ensayo en la especificación.
- **Apoyo de IA:** inventario inverso, separación de antecedentes, reglas de exportación, guías de apertura y revisión de coherencia.
- **Resultado:** SPEC app 4.3 con 40 apartados y matriz de 48 archivos Kotlin de producción; SPEC web 6.1. SDK, dependencias y compilación se explican como requisitos externos, sin prometer que un proyecto Android compile sin ellos.
- **Control:** `MATRIZ-SPEC-CODIGO.md`, `VERIFICACION.md`, herramientas de exportación; revisión de huellas.

### IA14 Plan académico e investigación inicial

- **Solicitud resumida:** completar la plantilla del curso con el producto final, integrantes, responsabilidades, dedicación, costos y preguntas de la presentación.
- **Datos aportados por Jeffry:** profesor Diego Arcila Herrera; integrantes y usuarios GitHub; cuatro meses; dedicaciones estimadas; COP 200.000 total en ChatGPT; congelar alcance actual.
- **Apoyo de IA:** redacción, cálculo académico explícito, diagramas y revisión de formato del Word.
- **Corrección metodológica:** las respuestas de las páginas 3–4 son antecedentes cualitativos, no prueba de uso de la app ni una muestra de tamaño conocido. No convertir las campañas presupuestadas en gastos reales.
- **Confirmación posterior del 24/10:** Jeffry recordó aproximadamente siete personas de 40–60 años que probaron Noki un mes antes: cinco la consideraron fácil e intuitiva y dos tuvieron dificultades. No precisó versión, tareas ni tiempos. Se incorpora como reporte retrospectivo, no como éxito medido de tareas de la versión final. Confirmó que el video aún falta.
- **Control:** `docs/academico/INVESTIGACION-INICIAL.md`, plan final y protocolo de validación. No se inventan participantes, edades, tiempos, consentimientos, video ni entrega en D2L.

### IA15 Consolidación en GitHub

- **Solicitud del 24/10/2026:** actualizar GitHub, completar esta bitácora y entregar el documento final.
- **Apoyo de IA:** copiar fuentes revisadas, excluir secretos y compilados, crear ocho issues de trazabilidad y un PR de consolidación, repetir pruebas aisladas y actualizar el plan.
- **Decisión de integridad:** preservar la raíz e historial del prototipo. El registro del cierre tiene fecha real; no se retrofechan commits ni se atribuyen aportes Git a Luis que no están registrados.
- **Control:** `docs/academico/TRAZABILIDAD.md`, `VERIFICACION-CIERRE.md`, issues #1–#9 y PR de consolidación. No se modifican usuarios, solicitudes ni entregas de producción.

## Criterio para aceptar una propuesta de IA

1. Compararla con el flujo y alcance decididos por el equipo.
2. Revisar código y contratos afectados; conservar cuentas y datos existentes.
3. Ejecutar pruebas pertinentes y distinguir simulaciones de evidencia real.
4. Corregir resultados engañosos, etiquetas ambiguas y promesas no implementadas.
5. Registrar la decisión y sus límites. No usar «sin errores» o «100 % seguro» como conclusión de pruebas parciales.

## Límites que siguen abiertos

La bitácora cubre las decisiones identificables y no recupera conversaciones de Antigravity ni prompts ausentes. La prueba con aproximadamente siete personas consta como declaración retrospectiva; faltan sus fichas y versión evaluada. El video, revisión independiente y entrega institucional requieren evidencia humana adicional. El cierre técnico no los sustituye. Ver [issue #9](https://github.com/jeffcaro05/noki-app/issues/9).
