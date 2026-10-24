# Noki — Verificación

## Versión 0.7.7 — requisitos visibles al solicitar ser receptor

21 de octubre de 2026. Línea base vigente: SPEC 4.2, Android 0.7.7/código 15. Informe: [Validación de receptor](../verification/receptor-validacion-v077.md).

- Los tres pasos enumeran los requisitos pendientes. Continuar con información incompleta muestra el error junto al campo, casilla o foto y desplaza la vista al primero; no envía datos ni pierde las respuestas.
- Mínimos y contadores visibles; cambio de dirección exige confirmar de nuevo el mapa y explica por qué. El mapa y las aceptaciones previas también explican su bloqueo.
- Títulos externos a los campos para evitar el recorte de etiquetas flotantes con texto grande. Captura al 200 % revisada y prueba de que el título y el campo quedan dentro de la vista.
- 115 pruebas aprobadas: 88 Android/Robolectric y 27 del núcleo compartido, sin fallos, errores ni omitidas. Compilación final correcta; análisis estático sin errores, con 30 advertencias existentes.
- Actualización instalada únicamente en el VIVO sobre `co.noki.app`; archivo de datos protegidos idéntico antes/después. Arranque correcto, sin error AndroidRuntime observado. OPPO sin modificar, sin otra app de pruebas, sin borrar almacenamiento ni cerrar la cuenta.
- Sin cambios en Auth, contraseñas, SQL, aprobación, directorio ni QR. Los ensayos de solicitud usaron datos ficticios y no enviaron fotos ni solicitudes al servidor. La aprobación y recepción física reales no se certifican con estas pruebas.

## Versión 0.7.6 — QR y código estables

Línea base histórica de esta versión: SPEC 4.1, Android 0.7.6/código 14. Informe detallado: [QR estable](../verification/qr-estable-v076.md).

- El mismo par QR/código se recupera mientras la entrega esté pendiente, incluso tras reabrir o entrar con otra sesión del titular; no hay cuenta regresiva ni renovación por tiempo.
- Confirmación explícita del receptor o cancelación invalidan las credenciales. Escanear solo valida la lista; no completa por sí mismo.
- 100 pruebas aprobadas (82 Android y 18 compartidas); cero fallos, errores u omitidas. Compilación correcta; análisis estático con cero errores y 30 advertencias existentes.
- Pruebas SQL de credenciales, contacto y continuidad aprobadas con datos sintéticos revertidos. No se hicieron entregas físicas ni se aprobaron cuentas reales para estos ensayos.
- Actualización sobre la app existente sin desinstalar ni borrar datos; autenticación y perfil sin modificaciones.
- La entrega del ZIP se registra por separado en [Verificación del paquete](../verification/paquete-v076.md). Las secciones siguientes conservan resultados históricos, no describen todas el estado vigente.

## Versión 0.7.5 — corrección del directorio y generación desde el destino

Cierre del 15 de octubre de 2026, Android nativo `co.noki.app`, código 13. SPEC 3.13, RF-87. Sin cambios en Auth, SMTP, OAuth, esquema SQL, RLS, identidades o solicitudes de receptor.

- Causa confirmada: tres puntos públicos activos y un único receptor aprobado (Express.arte). El directorio contextual de 0.7.4 se intersectaba con los receptores permitidos para la cuenta; como el propietario no es su propio destinatario, quedaba vacío. No era una desaparición de datos en el servidor.
- Se elimina el filtro y su botón repetitivo. La lista pública, sus marcadores y sugerencias comparten `visibleDeliveryPlaces`, sin dependencia de la sesión o de la lista de receptores autenticada. Se comprueban lista pública completa y búsqueda/categoría explícitas.
- Preparación presenta los puntos publicados incluso con cero destinatarios. El lugar propio se identifica sin ocultarlo; las referencias externas no prometen confirmar un QR de Noki. Otra cuenta puede elegir el receptor disponible por su identificador de solicitud, sin comparar nombres.
- La ficha usa `DestinationDeliveryAction` para generar directamente, conservando el grupo de aparatos y pidiendo la declaración de contacto cuando corresponde. Abre la pantalla real de entrega con código y QR; la ruta permanece opcional. Una selección vacía no incluye aparatos silenciosamente.
- Once pruebas nuevas, más las 83 previas: propietario con tres destinos, otra cuenta desde lista publicada, cambio de disponibilidad, instrucción inicial sin botón en bucle, categoría no aplicada implícitamente, creación directa desde ficha con dos aparatos/texto 200 %, fallo de creación y reintento, selección vacía, fallo de token sin duplicar entrega. Las pruebas anteriores de contacto se conservan.
- Primera compilación completa aprobada: `verification/build-v075-verified.txt`, 4 min 5 s. 94 pruebas aprobadas: 76 Android/Robolectric y 18 compartidas; cero fallos, errores u omitidas. Análisis estático: cero errores, 30 advertencias conservadas. Incluye las suites de contraseña/OTP, perfil, sesión, preparación, caducidad y resultado.
- Se ajustó después una frase para indicar inequívocamente que la ruta puede consultarse después de generar el código, no después de entregar. Ejecución final aprobada: `verification/build-v075-final.txt`, `BUILD SUCCESSFUL`, 4 min 54 s. Repite las 94 pruebas con cero fallos/errores/omitidas; revisión estática final sin errores y 30 advertencias. El APK instalado y su huella corresponden a esta ejecución.
- Capturas generadas por los componentes reales con red controlada, 360 dp y fuente 200 %: `verification/destino-generar-200-v075.png` y `verification/continuidad-200-v075.png`. Se revisaron márgenes y legibilidad; botón completo en dos líneas y textos sin superposición. No son evidencia del motor nativo del mapa ni una entrega real.
- No se hicieron entregas con cuentas reales ni se aprobó otro receptor para producir un resultado de prueba. La recepción física entre dos personas y la navegación GPS del VIVO siguen requiriendo comprobación humana. Las suites SQL de 0.7.4 son evidencia previa de los contratos del servidor, que no cambian aquí; no se presentan como una ejecución SQL nueva.
- VIVO actualizado con `Success`: versión 0.7.5/código 13, mismo paquete `co.noki.app`; solo ese paquete figura para Noki en el usuario principal. No se desinstaló, borró almacenamiento ni abrió automáticamente la app para operar cuentas reales. No se generó ZIP ni se instaló una segunda app.
- Huella del archivo cifrado antes/después idéntica: `7056847ec6db60c2f2151042cd34f07e6d898201f30790043839a42c30ea298d`. No se leyó su contenido. Verifica conservación durante la actualización, no sustituye el acceso humano a la cuenta.
- Firma compatible validada: SHA-256 `ec9b356a1842c34e3c994a0549e79f7762a483419426db2874a02265060e0e13`. APK instalado: 71.271.971 bytes, SHA-256 `0DD694823C56FF4575C4F77FC5AA57D77AB2A61B2C0E3D08D21A3D64328DD8A1`.

## Versión 0.7.4 — continuidad de entrega y mapa

Revisión del 14 de octubre de 2026, Android nativo `co.noki.app`, código 12. No se cambió el inicio de sesión, la configuración SMTP/OAuth ni las credenciales. SPEC 3.12, RF-86.

- Elegir en el mapa devuelve el identificador del receptor a la misma preparación; se conservan los aparatos seleccionados. Una identidad no disponible no se sustituye silenciosamente por otra.
- La misma acción de continuación está en ficha, directorio y mapa ampliado: continuar a confirmación/generación o mostrar el QR de la entrega existente. Calcular ruta es opcional y no crea otra entrega.
- Compilación inicial de Kotlin y pruebas compartidas correctas: `verification/build-v074-compile.txt`, 3 min 16 s. Compilación, empaquetado, pruebas y análisis final completos: `verification/build-v074-verified.txt`, `BUILD SUCCESSFUL`, 6 min 27 s. Análisis estático: cero errores y 30 advertencias, sin ocultarlas.
- 83 pruebas automatizadas aprobadas: 65 Android/Robolectric y 18 compartidas, sin fallos, errores ni omitidas. Once nuevas comprueban selección exacta del receptor, QR visible tras crear, ida y vuelta al mapa conservando dos aparatos, renovación de la declaración de contacto, lugar retirado, referencia pública sin QR ficticio, preparación incompleta, recepción completada y texto 200 %. Incluyen las regresiones de contraseña/código y sesión.
- Captura del componente real de continuación al 200 % y 360 dp revisada: `verification/continuidad-200-v074.png`. Texto y botón completos, sin superposición. La captura aislada no representa una prueba del renderizador nativo del mapa.
- Cuatro suites SQL aprobadas con rollback; relación punto/receptor, privacidad, suspensión, contacto, QR, código manual y confirmación. Evidencia: `verification/backend-v074.md`. No se realizaron entregas en cuentas reales para probar.
- Comprobación de servicio de rutas desde el computador con origen de ejemplo, no ubicación del usuario: OSRM respondió `Ok`, una ruta y 153 coordenadas hasta el punto público de Express.arte. Es una comprobación puntual del servicio, no una prueba del GPS ni del recorrido físico.
- La suite de interfaz usa respuestas controladas y aparatos sintéticos. La comprobación física de recepción entre dos personas y el renderizado/GPS de la ruta en el VIVO no se sustituyen por estos ensayos.
- No se exporta ZIP ni se instala una segunda app.
- Actualización del VIVO confirmada con `Success`: versión 0.7.4, código 12, mismo paquete `co.noki.app`; solo ese paquete aparece para Noki en el usuario principal. No se desinstaló, borró almacenamiento ni abrió automáticamente la app para operar cuentas reales.
- Huella del archivo cifrado antes y después de actualizar idéntica: `ce90172ac9a13bfc4f449bb4af1477f71ce93dd5f2e36e376ddcd8bb1c1c8047`. No se leyó ni exportó su contenido. Esto verifica conservación del archivo, no sustituye la comprobación de acceso del titular.
- Firma compatible validada: SHA-256 `ec9b356a1842c34e3c994a0549e79f7762a483419426db2874a02265060e0e13`. APK instalado de 70.699.575 bytes, SHA-256 `3AA3E5EF8BFCDE5ED56E1B53362C488F1736AA7226D57229941F68E08AD89FAA`.

## Versión 0.7.3 — directorio automático y entrega con contacto

Revisión del 14 de octubre de 2026. Android nativo `co.noki.app`, código 11. No se cambió el inicio de sesión, SMTP, OAuth ni las credenciales de los usuarios.

- 72 pruebas automatizadas aprobadas: 54 Android/Robolectric y 18 del núcleo compartido; cero fallos, errores o pruebas omitidas en la ejecución final. Incluyen acceso por contraseña/código, persistencia de sesión, publicación del directorio, caché, categorías, contacto, QR y Resultado tras recepción.
- La prueba del botón recorre selección de receptor → botón deshabilitado con motivo → declaración de contacto → botón habilitado → creación y código recibido. Cambiar la selección exige confirmar otra vez. Sin contacto se explica la alternativa. Conservar un aparato explica que no genera QR.
- Texto 200 % sobre ancho de 360 dp: controles accesibles mediante desplazamiento, botón completo y textos sin superposición. Captura revisada: `verification/contacto-200-v073.png`.
- La primera ejecución de la prueba de creación agotó el tiempo de espera porque no avanzaba la cola principal de Robolectric al esperar el resultado de red simulado. La prueba final procesa esa cola y comprueba también errores del modelo; no se eliminaron comprobaciones ni se cambió la lógica productiva para hacerla pasar.
- Tres suites SQL aprobadas y revertidas: publicación/retiro, entrega agrupada/código manual y contacto/excepción de categoría/QR. Evidencia y avisos pendientes: `verification/backend-v073.md`. Cero usuarios sintéticos restantes.
- Firma compatible con la instalación existente: SHA-256 `ec9b356a1842c34e3c994a0549e79f7762a483419426db2874a02265060e0e13`.
- Compilación final completa y revisión estática terminadas correctamente: `verification/build-v073-verified.txt`, 11 min 37 s. El APK instalado corresponde a esta ejecución, SHA-256 `BA7DF07C8E7D7D4A8F6E650F97E08C9A98CE0C0E068CB50AE25590005814B584`.
- Revisión estática final: cero errores y 30 advertencias; se conservan en el informe de compilación para seguimiento. No se presenta el resultado como ausencia absoluta de defectos.
- Instalación por actualización confirmada en el VIVO: versión 0.7.3, código 11, mismo paquete `co.noki.app`. No se desinstaló ni se borraron datos. La huella del archivo cifrado fue idéntica antes y después: `4e7b87a5ec9843d1303c0db265f1aa3117f9af8cd361201aeeed98471c0f3a8f`. No se extrajo su contenido ni se leyeron credenciales.
- No se exporta un ZIP nuevo ni se instala una segunda app de pruebas, según instrucción del titular. Las evidencias previas de ZIP y QA que siguen abajo son históricas.

Estos ensayos no equivalen a una garantía de ausencia de defectos, prueba física de entrega de aparatos ni certificación de seguridad de un establecimiento. La web administrativa, OAuth Google, publicación en tiendas y clientes iOS/Windows mantienen sus verificaciones pendientes.

## Versión 0.7.2 — preparación directa, contraseña y recepción

Comprobada el 10 de octubre de 2026. Android nativo co.noki.app, código 10. Registro/correo SMTP y cuentas existentes conservados; Google sigue pospuesto.

- Primera suite completa: verification/build-v072-verified.txt, exitosa en 3 min 57 s. 57 pruebas aprobadas (43 Android/Robolectric + 14 compartidas), cero fallos/errores. Lint: cero errores y 29 advertencias; no se afirma producto sin defectos.
- Suite final desde el proyecto extraído, incluido el ajuste de contraseñas: verification/build-v072-final-package.txt, exitosa en 4 min 36 s. 58 pruebas aprobadas (44 Android/Robolectric + 14 compartidas), cero fallos, errores o pruebas omitidas. Análisis final: cero errores y 28 advertencias de compatibilidad, dependencias, orientación y convenciones. Las tres capturas finales coinciden por SHA-256 con las revisadas.
- Compilación limpia del contenido extraído: verification/build-v072-extracted.txt, exitosa en 4 min 16 s, 42 tareas ejecutadas, sin local.properties. Los 77 archivos de código, recursos y construcción coinciden por SHA-256 con el proyecto original.
- El ajuste final valida contraseñas nuevas con mínimo 12 caracteres y máximo 72 bytes UTF-8, sin truncarlas. No aplica ese mínimo al inicio de sesión con contraseñas existentes. Referencia del límite: https://github.com/supabase/auth/blob/master/internal/api/password.go.
- El primer intento tuvo un error de composición corregido; el segundo falló únicamente al capturar imágenes mediante PixelCopy en Robolectric. Se reemplazó por renderizado local de la vista y se repitió la suite completa; las imágenes actuales provienen del run aprobado.
- Cinco pruebas UI: acción directa en Inicio y Mis aparatos, paso automático a Resultado después de estado completed, aviso de error único con texto 200 %, icono de éxito. Tres imágenes revisadas: inicio-preparar-v072.png, resultado-automatico-v072.png y acceso-texto-grande-v072.png, en verification. Son fixtures, no entregas reales.
- Nueve pruebas de Auth: OTP vigente/vencido/restringido, correo confirmado, entrada con contraseña, error_code simbólico frente a code numérico, actualización autenticada de contraseña y aviso de reautenticación.
- Quince pruebas de modelo: preservación de sesión, renovación, perfil, consentimiento, recuperación con contraseña opcional, avisos diferenciados, consulta silenciosa, recuperación tras fallo de red y rechazo de contraseña Unicode que supera el límite, antes de contactar al servidor.
- Servidor: migración receiver_confirmation_completion aplicada; 26 aserciones transaccionales revertidas. Ver verification/backend-v072.md. Ninguna identidad/entrega de fixture persistió; cero receptores reales aprobados y una solicitud pendiente.
- Instalación final -r del APK compilado desde el contenido extraído: Success, versión 0.7.2/código 10, solo paquete co.noki.app en usuario principal del VIVO. SHA-256 del archivo cifrado antes y después idéntico: b98c5f805394fdb652cc4760a201bffc1998dd90d0e5dbd880245a8b002a4d43. No se leyó su contenido, ni se cerró sesión, borró almacenamiento o instaló QA. La instalación previa también conservó el archivo; esta huella corresponde a la actualización final.
- Firma de desarrollo compatible: SHA-256 ec9b356a1842c34e3c994a0549e79f7762a483419426db2874a02265060e0e13.
- APK final: 70.634.039 bytes, SHA-256 68218B3CED44BB22D094DC75D782FB262DBAA067CBCA21E09B0C901864573612. Es de desarrollo, no una publicación en tienda. La copia entregada coincide con la instalada y con la compilada desde el paquete extraído.
- ZIP: incluye fuentes, recursos, Wrapper, SPEC 3.10, pruebas, migraciones y evidencia seleccionada; no incluye claves de firma, sesiones, contraseña SMTP, local.properties, SDK ni cachés. CONTENIDO-SHA256.txt permite comprobar todos sus archivos. Las actualizaciones documentales y de empaquetado no modifican los 77 archivos de código/construcción comprobados y compilados.
- Pendiente: prueba humana con la contraseña propia y entrega física entre dos cuentas con receptor aprobado. No se considera superada por fixtures ni se aprueba un sitio para poder probar.


## Versión 0.7.1 — continuidad de cuenta y una sola app

Android nativo `co.noki.app`, código 9, instalado como actualización sobre 0.6.0 en el VIVO. Mantiene el acceso por código y contraseña existente; Google se pospone por indicación del titular. No se modificaron SMTP, plantillas, usuarios ni aprobación de lugares durante este ajuste.

- `verification/build-v071.txt`: primera compilación y análisis completos aprobados; 44 pruebas unitarias sin fallos ni errores, incluidas 10 nuevas de conservación de sesión y bloqueo de la simulación.
- `verification/build-v071-extracted.txt`: compilación limpia del código extraído del ZIP, utilizando su propio Wrapper y sin `local.properties`, aprobada en 6 min 48 s, 63 tareas ejecutadas. Repite las 44 pruebas con cero fallos/errores. Análisis final: cero errores y 28 advertencias; no se afirma ausencia absoluta de defectos.
- Las pruebas nuevas usan almacenamiento en memoria y respuestas HTTP controladas en el computador. Comprueban fallo de perfil, reintento, consentimiento pendiente, OTP válido seguido de fallo de red, renovación válida/fallida, sesión existente no sustituible, éxito de cuenta existente y rechazo de práctica.
- La app real conserva identificador, firma y alias de cifrado `noki.session.v1`. No se ejecuta `pm clear`, no se desinstala `co.noki.app` ni se elige un nombre/foto por el titular.
- Instalación `-r` confirmada con `Success`, versión 0.7.1/código 9; solo aparece `co.noki.app` al consultar los paquetes de Noki del usuario principal. La huella SHA-256 de `shared_prefs/noki_secure_v1.xml` fue idéntica antes y después de instalar, antes de volver a abrir la app. No se leyó el contenido ni se exportaron credenciales. Esto prueba conservación del archivo durante la actualización, no sustituye la comprobación de acceso del titular.
- `PRACTICE_ENABLED=false` en debug/release: no hay botón ni franja de prueba y el modelo bloquea la entrada a ese modo. Los fixtures anteriores no se mezclan ni se importan a una cuenta.
- Se desinstalaron únicamente los artefactos de pruebas creados para QA: `co.noki.app.qa`, `co.noki.app.qa.test` y el runner `co.noki.app.test`. Sus datos de pruebas no se conservan en el teléfono; la app original y sus registros no se eliminaron.
- Las 26 pruebas instrumentadas aprobadas corresponden a 0.7.0 y son evidencia de esa revisión anterior, no una nueva ejecución sobre 0.7.1. No se vuelve a instalar QA en el VIVO.

El ZIP incluye fuentes, recursos, Wrapper, pruebas, migraciones, SPEC 3.9 y evidencia seleccionada. No incluye credenciales, claves de firma, SDK, caches ni APK de QA. Las capturas y registros históricos no seleccionados permanecen en la carpeta de trabajo original; por eso algunas referencias históricas no acompañan al ZIP.

APK instalado y entregado: 70.601.267 bytes; SHA-256 `D91A53A035A3AE8B5581355A5249E88E77D9481740F8D7396C7CE9E461F5A1D2`. Firma de desarrollo válida verificada; huella pública del certificado SHA-256 `ec9b356a1842c34e3c994a0549e79f7762a483419426db2874a02265060e0e13`. No es firma de distribución ni publicación en tienda. El candidato extraído validó sus 104 archivos contra el manifiesto, sin diferencias. El empaquetado final añade el registro de esa compilación y esta actualización documental; se comprueba que los archivos de código y construcción coincidan con los compilados.

Pendientes: comprobación del titular de la nueva aceptación, elección de nombre, foto real y regreso a Inicio; entrega real con receptor aprobado; web administrativa, Google, iOS/Windows y demás tareas abiertas. No se presenta como app de producción certificada al 100 %.

## Versión 0.7.0 — 8 de octubre de 2026

### Paquete y alcance

Android nativo, identificador `co.noki.app`, código 8. APK de desarrollo de 70.697.462 bytes, SHA-256 `6E86D1776C60A85D4E9F3BD132AEDB8E1FC702C4A1BD7CD26C1DE68CC4D7FBFE`. No es una publicación en tienda ni una versión iOS/Windows.

- Compilación completa y análisis estático aprobados en `verification/build-v070-release-check.txt`. 34 pruebas unitarias sin errores ni fallos: las 29 previas, dos de pendiente/historial y tres de geometría de encuadre.
- Pruebas de interfaz: 26 aprobadas en 174,791 segundos, `verification/ui-v070-final.txt`. Se usó exclusivamente `co.noki.app.qa`, con datos separados; las pruebas rechazan ejecutarse sobre el paquete del propietario. Esa variante se desinstaló tras la petición posterior y no se vuelve a instalar en su VIVO.
- La primera ejecución de 26 recorridos encontró recorte del nombre de usuario máximo. Se adaptó la cabecera y se amplió la etiqueta de acceso para texto grande. El encuadre propaga la escala de letra al diálogo. Las capturas de componentes `saludo-100-v070.png`, `saludo-200-v070.png` y `encuadre-200-v070.png` se revisaron después de la corrección; el fondo vacío en las dos primeras pertenece a la prueba aislada del componente, no a Inicio.
- `historial-retirado-v070.png` muestra la separación del pendiente retirado sin resultado de entrega. El recorrido de práctica comprueba creación con código automático, validación de código y dos confirmaciones antes de Resultado.

### Backend y documentación

Migración de nombres de usuario e historial aplicada y comprobada antes y después con las tres suites SQL, en transacciones revertidas. Formato, unicidad, espera de 30 días, propiedad, versión, archivo idempotente e imposibilidad de editar o entregar archivados aprobados. Las identidades y receptores ficticios no permanecieron en el servidor. Detalle y avisos históricos de seguridad: `verification/backend-v070.md`.

SPEC 3.8, PLAN y TASKS describen RF-78–81 y el encuadre actualizado. Términos/privacidad `2026-10-08.2`: el usuario debe volver a decidir sobre las condiciones y elegir su alias; no se borran sus registros anteriores ni se aprueba su solicitud de receptor. El plazo de 30 días se impone en servidor, no solo en un botón deshabilitado.

### Comprobaciones reales que siguen pendientes

El titular ya confirmó recepción del correo y entrada en 0.6.0. En 0.7.0 falta que elija personalmente su nombre, pruebe selección/encuadre/subida de una foto real y confirme regreso a Inicio. No se elige un alias ni se cambia su foto desde las pruebas. Las entregas reales siguen requiriendo un receptor aprobado mediante el procedimiento humano y, posteriormente, la web administrativa solicitada. No se autoaprueba un lugar para evitar esta dependencia.

La batería automatizada no certifica seguridad absoluta ni sustituye pruebas con dos cuentas reales, TalkBack, dispositivos adicionales, revisión jurídica o gestión de incidentes. Los avisos de confianza distinguen referencias públicas, receptores revisados y ejemplos; no prometen que todos los lugares estén verificados o sean libres de riesgo.

## Versión 0.6.0 — 8 de octubre de 2026

### Paquete y comprobaciones

- Android nativo `co.noki.app`, versión 0.6.0, código 7, instalado mediante actualización en VIVO Y39 5G/V2436 con Android 16, sin desinstalar ni borrar registros del propietario.
- APK de depuración: 70.570.969 bytes. SHA-256: `53991933795292650EBC43B5355B32A9B58F3BAA98CA995F5AB10728BB320C48`.
- Compilación, pruebas unitarias y análisis estático aprobados: `verification/build-v060-final.txt`. Dos ejecuciones previas detectaron un tipo de retorno incorrecto en una prueba y la necesidad de comprobar la cabecera JPEG antes de decodificar; la ejecución final completa aprobó.
- 29 pruebas unitarias: OrientationTest (7), EmailAccessTest (5), ProfileAccessTest (7), DeliveryCodeTest (1), JourneyTest (3), DeliveryTest (6). Sin fallos.
- 23 pruebas instrumentadas aprobadas en VIVO, 118,744 segundos: `verification/ui-v060-final.txt`. Se comprobó previamente que no había una cuenta abierta. No se ejecutan estas pruebas sobre la sesión real del propietario.
- Análisis estático: 0 errores, 29 advertencias. No equivale a certificación de accesibilidad, seguridad o cumplimiento jurídico.
- Capturas revisadas: `verification/inicio-v060.png` y `texto-200-v060.png`. El resumen es legible, los márgenes se conservan y el contenido ampliado se desplaza sin superponerse a la navegación.

### Perfil y permisos

La migración `20261008060611_native_profile_access.sql` está aplicada. Introduce configuración pública sin escrituras del cliente, nombre por cuenta, aceptación centralizada versionada y bucket privado de foto. La operación pública es SECURITY INVOKER; sus funciones internas verifican correo, sesión y propiedad. No se modificaron los registros históricos de la web.

`verification/native-profile-tests.sql` aprobó con rollback: falta de aceptación, casillas incompletas, aceptación válida, nombre propio, imposibilidad de seleccionar otro titular, rutas de foto ajenas, escritura directa denegada, lectura entre cuentas, consentimiento retirado y sesión inexistente. Se repitió `native-handoffs-tests.sql` con la nueva versión legal; aislamiento, entrega agrupada, token y resultado protegido aprobaron.

Las siete pruebas de perfil comprueban disponibilidad remota, contrato de aceptación, edición ligada a sesión, descarga privada, límite de descarga, recorte a 512 px sin GPS y conservación del original, y rechazo de bytes sin cabecera JPEG. No equivalen todavía al recorrido real completo de subida, reemplazo y eliminación desde el teléfono.

Advisors mantiene información de [RLS sin políticas](https://supabase.com/docs/guides/database/database-linter?lint=0008_rls_enabled_no_policy) en siete tablas privadas sin acceso directo, incluida `account_consents`; el acceso se media mediante operaciones internas comprobadas. Mantiene advertencias de funciones históricas [anónimas](https://supabase.com/docs/guides/database/database-linter?lint=0028_anon_security_definer_function_executable) y [autenticadas](https://supabase.com/docs/guides/database/database-linter?lint=0029_authenticated_security_definer_function_executable), y [protección de contraseñas filtradas](https://supabase.com/docs/guides/auth/password-security#password-strength-and-leaked-password-protection). No se cambiaron esas funciones históricas ni se compró un plan. Las nuevas operaciones públicas de perfil/recepción no aparecen como SECURITY DEFINER expuestas.

### Correo y activación

El titular configuró Gmail SMTP con su contraseña de aplicación, fuera del chat y del código. Confirmó que guardó las dos plantillas. Una única solicitud autorizada a su Gmail respondió HTTP 200 a las 15:11:13 UTC; después confirmó recepción. Esta prueba demuestra el primer envío recibido, no la entrega a todos los proveedores ni el acceso verificado.

Tras esa confirmación se activó `noki_client_config.email_otp_enabled=true`. El titular pidió un código nuevo desde Noki y confirmó que pudo entrar. Una consulta posterior limitada a estados confirmó: correo verificado, sesión activa, perfil propio creado y consentimiento vigente `2026-10-08.1`. No se leyó el código ni se modificaron manualmente campos de confirmación de Auth. Google y Apple continúan sin configuración.

### Pendientes

Nombre/foto reales, reingreso, recuperación y matriz de errores de correo; otros dominios; límites y protección contra abuso para publicación. Cambio de correo/contraseña y eliminación de cuenta mantienen tareas propias. Continúan pendientes revisión jurídica, operación de receptores, escaneo físico, otras plataformas y dispositivos, validación adicional de archivos en servidor y administración web. No se afirma que todas las funciones del SPEC estén terminadas.

SPEC 3.7, PLAN y TASKS corresponden a esta versión; la web se mantiene fuera de esta implementación.

---

## Versión 0.5.0 — 8 de octubre de 2026

### Paquete y resultados

- Android nativo, `co.noki.app`, versión 0.5.0, código 6; instalado en VIVO Y39 5G/V2436 con Android 16 mediante actualización, sin desinstalar ni borrar los datos del propietario.
- APK de depuración: 73.048.539 bytes. SHA-256: `2756D8A9EA404DC6CE54CDF1E26026E786530234FFAA5C7A331B49501492810C`.
- Compilación de app y pruebas, núcleo compartido, pruebas unitarias y análisis estático: aprobados. Registro: `verification/build-v050-final.txt`.
- 22 pruebas unitarias aprobadas: OrientationTest (7), EmailAccessTest (5), JourneyTest (3), DeliveryTest (6) y DeliveryCodeTest (1).
- 23 pruebas de interfaz aprobadas en el VIVO, sin fallos. Ejecución final: `verification/ui-v050-final.txt`, 124,405 segundos. La ejecución inicial también aprobó 23; después se ajustó la disposición del QR y se repitió la suite completa.
- Análisis estático: 0 errores, 28 advertencias de herramientas/dependencias, API desaconsejada, orientación solicitada, parámetros de componentes, icono monocromático, condición redundante y sugerencias KTX. No equivale a certificación de seguridad o accesibilidad.

### Recorridos comprobados

1. Acceso contextual fijo: ocho deslizamientos rápidos al final del formulario, comprobación de posición estable y regreso sin perder la orientación. La captura incluye la ventana del formulario, no solo la actividad situada detrás.
2. Entrega de práctica: decisión de recolección, resultado inicialmente bloqueado, cinco tareas de preparación, botones para avanzar, elección del receptor ficticio y creación de entrega.
3. Generación de QR y código escrito, comprobación del código, confirmación del receptor, confirmación de quien entrega y habilitación del resultado. No permite reabrir la constancia como resultado personal libre.
4. Disposición del QR comprobada mediante límites de componentes y captura: no invade la explicación anterior. La codificación y decodificación del contenido del QR se probaron por software; no se confunde con escaneo físico mediante cámara.
5. Información de receptor: sin campos de identidad; dos aceptaciones independientes; una sola casilla no habilita la solicitud. Ambas permiten continuar al acceso; volver conserva el recorrido. No concede el rol.
6. Selector de lugar de entrega, filtro sin coincidencias, ficha, elección de partida y regreso. Cálculo real con origen público manual: abre automáticamente el mapa ampliado con línea, distancia y tiempo. Búsqueda de dirección obtiene coincidencias reales sin GPS.
7. Cuatro destinos principales, orientación y respuestas preservadas, toques repetidos, aceptación/rechazo de términos, preferencias y temas claros/oscuros, navegación vertical y etiquetas sin desbordamiento a 100/125/130/200 %.

Capturas revisadas: `verification/acceso-sin-rebote-v05.png`, `codigo-v05.png`, `resultado-v05.png`, `ruta-v05.png`, `receptor-v05.png` y `texto-200-v05.png`. La captura `entrega-confirmada-v05.png` coincide con el desvanecimiento del diálogo y no se utiliza como evidencia de disposición en reposo.

### Reglas de servidor

La migración aditiva de recepción nativa está aplicada. `verification/backend-v05.md` y `native-handoffs-tests.sql` documentan pruebas transaccionales revertidas: aislamiento, una sola entrega activa, grupo de dos aparatos, caducidad, límites de intentos, prevención de reutilización, confirmaciones idempotentes, cancelación y bloqueo del resultado prematuro. La llamada anónima devuelve 401. No se modificaron las tablas históricas de la web ni se aprobaron perfiles reales.

Las pruebas del teléfono solo generaron ejemplos locales identificados como «Teléfono de prueba automatizada». Se retiraron esos ejemplos y sus entregas locales al finalizar; pueden generarse nuevamente. No se eliminaron registros del propietario ni datos remotos. Las preferencias de accesibilidad anteriores se restauraron.

### Límites y trabajo pendiente

- Registro, verificación y recuperación por correo: servicio transaccional sin configurar, `EMAIL_OTP_READY=false`. Google/Apple siguen sin habilitarse. Crear el buzón de contacto no activa estos servicios.
- Solicitud real de receptor con tres fotos y entrega entre dos cuentas reales desde dispositivos: implementadas, pero no ejecutadas de extremo a extremo con identidades reales. Las pruebas locales y SQL no sustituyen ese recorrido.
- Escaneo físico del QR, permisos de cámara y GPS concedidos/denegados, pérdidas de red y escenarios concurrentes de dos conexiones independientes.
- Designar revisores; comprobar autorización del lugar; revisión jurídica; atención de reportes, apelación y retiro; validación de contenido de fotos en servidor; control y comprobación del borrado. No se contrataron servicios de identidad ni se recolectaron cédulas o biometría.
- Mejorar la explicación cuando una selección de aparatos no coincide con las categorías admitidas por un receptor y completar la matriz de errores de entrega.
- Perfil completo, recuperación y eliminación de cuenta, constancia centralizada del consentimiento general, auditoría histórica y endurecimiento previo a publicación.
- TalkBack, tabletas, versión mínima Android y pruebas con público objetivo. Clientes iOS/iPadOS y Windows, firma de distribución y actualización pública no completados.

SPEC 3.6, PLAN y TASKS conservan separados los requisitos y la evidencia. Esta compilación no representa la finalización de todos los requisitos del producto.

---

## Versión 0.3.0 — 4 de octubre de 2026

### Compilación y dispositivo

- APK nativo instalado en VIVO Y39 5G, modelo V2436, Android 16.
- Identificador `co.noki.app`; `versionName=0.3.0`; `versionCode=3`.
- Gradle 8.13, JDK 17, SDK/objetivo 36, mínimo 26. Android Studio instalado y proyecto configurado con JDK 17.
- APK de depuración: 69.897.214 bytes. SHA-256: `F72476D8E5030E24D67B7A98BF1F07547686A53A236447DC156181BCF66B7179`.
- No se desinstaló la app ni se borraron los datos existentes al actualizar. No es una firma de distribución.

### Resultados

| Comprobación | Resultado | Evidencia |
| --- | --- | --- |
| Compilación Android y núcleo compartido JVM | Aprobada | assembleDebug, assembleDebugAndroidTest, shared:jvmTest |
| Orientación y validaciones Android | 7 pruebas aprobadas | OrientationTest |
| Reglas del núcleo compartido | 3 pruebas aprobadas | JourneyTest |
| Contrato de acceso por código | 5 pruebas aprobadas con respuestas controladas, no correos reales | EmailAccessTest; Robolectric API 34/JDK 17 |
| Interfaz en VIVO | 12 pruebas aprobadas, sin fallos | verification/ui-v03-final.txt |
| Análisis estático | 0 errores; 22 advertencias | app/build/reports/lint-results-debug.html |
| Base de datos nueva | Reglas de aislamiento comprobadas; transacción revertida | verification/backend-v02.md y rls-tests.sql |

Las advertencias estáticas corresponden a versiones de dependencias/herramientas, parámetros de componentes, icono monocromático, un control de versión redundante y sugerencias KTX. No se equipara ausencia de errores estáticos con producto completo o certificación de accesibilidad.

### Recorridos comprobados

1. Inicio, cuatro destinos funcionales, Aprende y Acerca de sin cuenta.
2. Orientación de segunda vida, revisión y seguridad; vuelta atrás sin perder la respuesta; conservación al recrear la actividad.
3. Texto al 200 % y navegación en dos filas, sin palabras partidas internamente en las capturas revisadas.
4. Menú de accesibilidad: texto grande, contraste y reducción de movimiento; persistencia al recrear la actividad. Las preferencias iniciales se restauran al terminar la prueba.
5. Registro por código explícitamente no disponible mientras falta el servicio de correo; no se simulan envíos.
6. Guardar aparato en espacio local, elegir decisión, asociar punto real como referencia, marcar preparación, registrar resultado, reabrir y eliminar el registro creado por la prueba.
7. Directorio con dos fichas públicas, horario, fuente y mapa nativo que muestra cartografía y marcadores.
8. Elegir origen en mapa ampliado, confirmar, consultar una ficha, consentir envío al servicio de rutas y obtener una ruta real con distancia/tiempo y línea dibujada. Se usó un origen público manual; el recorrido no solicitó GPS.
9. Cifrado local con clave de prueba temporal y limpieza posterior.
10. Bienvenida inicial, entrada al acceso, regreso a Inicio, cancelación de la hoja de acceso y recuperación de la orientación antes de guardar. Al terminar las pruebas se dejó la bienvenida disponible para el propietario.

Capturas revisadas: `verification/inicio-v03.png`, `inicio-texto-200-v03.png`, `accesibilidad-v03.png`, `ruta-v03.png` y `bienvenida-v03.png`. La preparación cuenta además con `preparacion-v03.png` como evidencia de ejecución. El marco cartográfico se verificó después de cargar; una captura temprana de un mapa aún cargando no cuenta como una prueba de renderizado.

Se eliminaron únicamente los registros locales llamados “Teléfono de prueba automatizada” creados durante estas pruebas, incluidos dos de intentos interrumpidos. No se eliminaron aparatos de usuarios ni registros remotos. Esos ejemplos pueden crearse de nuevo; no se restauran automáticamente.

### Límites pendientes

- Correo transaccional, remitente, plantilla con código, aceptaciones legales y alta pública. Google/Apple desactivados según la configuración real de Auth. No se contrató un servicio ni se compró un dominio. El código del cliente permanece bloqueado mediante `EMAIL_OTP_READY=false`.
- Inicio de sesión, renovación, cambio de nombre y ciclo privado completo desde el VIVO con una cuenta del propietario. Las pruebas RLS y de contrato no sustituyen ese recorrido. No se solicitaron ni recuperaron sus contraseñas.
- Cambio de correo y contraseña, recuperación y eliminación de cuenta.
- Obtención de GPS real, flujo de permiso denegado, fallos de cartografía/ruta sin conexión y persistencia de una ruta después de rotación.
- TalkBack/VoiceOver, tableta, versiones mínimas Android y prueba con participantes de 40–60 años.
- Cliente iOS/iPadOS: no construido ni probado; falta Mac/Xcode. Windows: núcleo JVM disponible, cliente de escritorio no construido.
- Firma y distribución definitiva, actualizador y revisión de seguridad de las funciones históricas.

SPEC 3.3, PLAN y TASKS distinguen requisitos de producto de funcionalidades comprobadas. La aplicación web y sus cuentas no se sustituyen por esta actualización.

---

## Registro histórico — versión 0.1.0

Fecha: 3 de octubre de 2026. Versión del código: 0.1.0.

## Entorno comprobado

- Android Studio instalado mediante el paquete oficial distribuido por winget.
- Gradle 8.13 con comprobación SHA-256 de la distribución.
- JDK Microsoft OpenJDK 17.0.20.1.
- Android SDK 36; Build Tools 35.0.0; Platform Tools 37.0.1.
- VIVO Y39 5G, modelo V2436, Android 16: conexión ADB autorizada.
- Código y herramientas de compilación separados de la aplicación web existente.

## Resultados comprobados

| Comprobación | Resultado | Evidencia |
| --- | --- | --- |
| Compilación APK de depuración | Aprobada | `:app:assembleDebug` |
| Reglas de orientación y validación | Aprobadas | `:app:testDebugUnitTest`; siete pruebas JUnit |
| Análisis estático | Sin errores; 17 advertencias | `app/build/reports/lint-results-debug.html` |
| Instalación y pruebas de interfaz en VIVO | Aprobadas: seis pruebas, sin fallos | `verification/public-flow-test.txt` |
| Escala de texto de Compose al 200 % | Recorrido público aprobado | `largeTextKeepsOrientationUsable`; sin cambiar la configuración global del teléfono |
| Revisión visual en VIVO | Inicio y resultado legibles en las capturas revisadas | `verification/inicio.png`, `resultado.png`, `inicio-texto-200.png` |

Las advertencias corresponden a versiones de dependencias, una carpeta de recursos vacía con calificador innecesario, icono monocromático pendiente y sugerencias de extensiones Kotlin. No se equipara «sin errores de análisis» con accesibilidad o funcionamiento completo.

### Recorridos ejecutados en el VIVO

1. Inicio con acciones funcionales y sin accesos a módulos incompletos.
2. Aprende y Acerca de accesibles sin cuenta.
3. Conservación de la respuesta al recrear la actividad.
4. Orientación operable con texto ampliado al 200 %.
5. Rama preventiva que omite la pregunta de funcionamiento y permite revisar respuestas.
6. Rama de segunda vida y retorno a la respuesta seleccionada.

Las siete pruebas de dominio también recorren todas las categorías y combinaciones de estados que determinan las tres recomendaciones. La prueba física no sustituyó ni borró la aplicación web.

### Paquete instalado

- Archivo: `app/build/outputs/apk/debug/app-debug.apk`.
- Identificador: `co.noki.app`; versión 0.1.0; código 1.
- Tamaño: 19.872.395 bytes.
- SHA-256: `D87327096F0E64EE649160E5E5883220C96E721C22A42B12D7B658641C3DC0A4`.
- Firma: desarrollo; no es el paquete de distribución definitiva.

## Alcance que puede probarse

Inicio, Orientación y sus tres recomendaciones, retroceso de preguntas, Aprende y Acerca de Noki. La orientación y los textos educativos se ejecutan localmente. Las fuentes externas requieren una aplicación que abra enlaces y conexión para cargar su contenido.

## Funciones aún no habilitadas

Cuentas completas, registro/verificación/recuperación, perfil, borrado de cuenta, directorio verificado, mapa/rutas, Mis dispositivos, listas de preparación, resultados e historial, borradores cifrados y actualización de distribución. Los indicadores de compilación mantienen ocultos los accesos incompletos. Las tareas y los requisitos de estas funciones permanecen en TASKS y SPEC.

## Estado del servicio

Supabase se reactivó con autorización del usuario. La revisión encontró las tablas de la aplicación web existente; las tablas objetivo del nuevo SPEC aún no estaban creadas. No se modificaron tablas, usuarios, roles ni registros existentes durante esta preparación.

## Criterio de cierre

Esta compilación es una base de desarrollo verificable, no la versión completa del producto. Ninguna historia se considera terminada únicamente por la existencia de su pantalla. Faltan pruebas con participantes de 40–60 años, TalkBack, otras versiones Android y todos los flujos integrados del SPEC.
