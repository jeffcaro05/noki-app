# Revisión documental del SPEC 4.0

Fecha: 15 de octubre de 2026.

Este registro explica la reorganización documental. No es parte de los requisitos normativos ni certifica la implementación de la app.

## Documento vigente y conservación del original

- Línea base vigente: `Noki-App-SPEC.md`, versión 4.0.
- Copia íntegra previa: `archive/Noki-App-SPEC-3.13-original.md`.
- SHA-256 del original archivado: `D2C091D016F6EE49CDC3318C7C609F24949C7CEF771F22C73672811408A1D3E2`.

La copia original conserva las notas operativas, resultados históricos, criterios anteriores y contexto de evolución. No debe usarse como especificación vigente cuando contradiga la versión 4.0.

## Qué cambió en la documentación

1. Se sustituyó la acumulación de anexos cronológicos por 22 capítulos funcionales, más una sección de autoridad del documento.
2. Se excluyeron de la línea base instalaciones en equipos particulares, nombres de teléfonos de prueba, compilaciones, seriales, rutas de trabajo circunstanciales, paquetes de entrega, ejemplos con cuentas reales y resultados históricos de pruebas.
3. Se conservaron requisitos legítimos de plataforma, compatibilidad, orientación, tamaños de pantalla, permisos y accesibilidad. Especificar compatibilidad Android no equivale a registrar dónde se instaló una versión.
4. Se consolidaron reglas antiguas y posteriores bajo una única definición por requisito. Los requisitos no se dan por implementados por estar escritos.
5. Se preservaron 87 RF, 26 RNF, 24 RN, las 62 HU identificadas en el original y 30 vistas lógicas UI. Las narraciones y criterios CA anteriores se sintetizaron en criterios observables por HU; los identificadores CA antiguos se conservan en el archivo original, no se reutilizan para criterios diferentes.
6. Se añadieron tablas coherentes de estados, permisos, contratos, errores, condiciones operativas y aceptación. No son resultados de pruebas ejecutadas.
7. Se distinguieron alcance Android, ampliaciones previstas y panel administrativo separado. Se mantuvo la arquitectura Kotlin/Compose, sin afirmar Flutter ni disponibilidad iOS.

## Contradicciones resueltas

| Tema | Regla consolidada |
| --- | --- |
| Contraseña y acceso | Contraseña principal para cuentas que la tengan; código para alta, alternativa y recuperación. Creación/cambio con 12 caracteres mínimos y 72 bytes máximos; no invalidar localmente contraseñas existentes por mínimos nuevos. |
| Datos del registro | Alias obligatorio; nombre personal y foto opcionales; no nombre civil obligatorio para la cuenta común. |
| Recepción | Confirmación del receptor completa la entrega; escaneo solo abre vista previa; no segunda confirmación del emisor en entregas nuevas. |
| Conservación y reutilización | Sin transferencia no se genera QR; donación/regalo con transferencia sigue entrega. |
| Pendientes e historial | Guardar no significa entregar; retirar archiva como sin entrega; supresión es un procedimiento distinto. |
| Lugares y códigos | Directorio público completo, sin filtro oculto de receptores; generación contextual desde el destino; ruta opcional posterior. |
| Atención o categoría distinta | Contacto público y declaración explícita permiten continuar; no garantía de aceptación ni eliminación de precauciones. |
| Receptor | Revisión remota del lugar y fotos privadas, posibles visitas coordinadas; no cédula ni biometría ni aprobación automática. |
| Diseño | Temas claro/oscuro/sistema, transición de vistas 260–380 ms y adaptación vertical con ventanas redimensionables; se retiraron reglas antiguas incompatibles. |
| Ubicación | No persistir ubicación personal; sí persistir coordenadas públicas autorizadas de lugares. |
| Pruebas | No presentar simulación como cuenta o entrega real; evidencia fuera del SPEC. |

## Condiciones pendientes que no deben confundirse con funciones certificadas

Esta revisión fue documental. Las observaciones siguientes provienen de inspección limitada de código y documentos existentes; no sustituyen una auditoría funcional completa ni una prueba de servicios desplegados.

- **Cambio del correo de cuenta:** en `AccountScreen.kt`, «Cambiar correo» corresponde al formulario de código. No se identificó ahí un recorrido de cambio del correo de una cuenta ya creada. RF-43 se conserva como requisito; no se afirma que esté cumplido.
- **Eliminación de cuenta:** no se identificó un flujo automático de extremo a extremo en la interfaz y modelo revisados. RF-45 exige un procedimiento real y diferencia solicitud de eliminación ejecutada. No se anuncia automatización inexistente.
- **Marca/modelo opcionales:** estaban en el SPEC anterior; el `DeviceRecord` de `shared/.../Journey.kt` revisado no expone esos campos. Se conserva la obligación documental y se registra la diferencia, en lugar de eliminarla silenciosamente.
- **Actualizaciones:** no se identificó «Buscar actualización» en la interfaz de cuenta revisada. RF-52 solo permite ofrecerlo con un canal operativo; una APK instalada no constituye actualización automática.
- **Fotos del lugar:** `REVISION-LEGAL.md` registra como pendiente la validación y saneamiento de contenido en servidor. El requisito se mantiene como obligación, no como control ya desplegado.
- **Retención:** el original calificaba como propuesta eliminar fotografías retiradas/abandonadas en treinta días y revisar anualmente las aprobadas. La propuesta se conserva en el archivo y en este registro. Deben aprobarse y comunicar los plazos concretos, además de operar el borrado real; la línea base exige esa política y no convierte la propuesta en una obligación jurídica ya validada.
- **Operación y apertura pública:** designación de revisores, atención de derechos, control de abuso y revisión jurídica siguen requiriendo evidencia operativa. La versión del SPEC no acredita por sí sola esas condiciones.
- **Google, iOS/escritorio y panel web:** se identifican como ampliaciones o producto separado, no como clientes o funciones ya disponibles.

## Comprobación documental realizada

- 87 RF definidos una vez; sin faltantes RF-01 a RF-87.
- 26 RNF y 24 RN definidos una vez; sin faltantes.
- 62 HU del original presentes en criterios de aceptación; sin identificadores duplicados.
- 30 UI conservadas y actualizadas como vistas lógicas, no pantallas adicionales obligatorias.
- Tablas Markdown con número de columnas consistente.
- Original archivado con huella idéntica a la previa.
- Sin referencias al VIVO, cuentas de prueba, punto de prueba, proyecto remoto específico ni versiones de instalación en el SPEC vigente.

No se cambió código de la app, esquema de base de datos, configuración de autenticación ni cuentas. No se ejecutaron compilaciones, instalaciones o pruebas funcionales por este cambio documental. Los resultados de pruebas de versiones anteriores permanecen en sus documentos de seguimiento y no se certifican nuevamente aquí.
