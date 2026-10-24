# Verificación del cierre académico

Fecha: **24/10/2026**. No se cambiaron funciones de la app o la web, usuarios, solicitudes ni entregas de producción. Esta revisión consolida las fuentes finales en GitHub y el documento académico.

## Ejecuciones nuevas

| Comprobación | Resultado | Alcance |
|---|---|---|
| Android `:app:testDebugUnitTest :shared:jvmTest --no-daemon --rerun-tasks` | BUILD SUCCESSFUL; 36 tareas ejecutadas; 5 min 21 s | 88 pruebas de app y 27 de reglas compartidas; 115 en total, cero fallos/errores. |
| Web `vitest run` | 28 pruebas en 3 archivos aprobadas | Componentes y lógica con respuestas controladas. |
| Web TypeScript `tsc --noEmit` | Correcto | Comprobación de tipos. |
| Web `vite build` | Correcto | Compilación en carpeta de comprobación separada; no despliegue. |
| Web `playwright test` primer intento | 9 aprobadas, 1 timeout al cargar la página inicial | Coincidió con las otras tareas de construcción; no se atribuye una causa definitiva sin medición. |
| Web `playwright test` repetición completa | **10 aprobadas**, 37,2 s | Misma configuración y código, sin aumentar timeout ni omitir el caso fallido. |

La repetición no elimina el primer timeout del registro. En Android se observaron advertencias de versión de XML del SDK y de un icono obsoleto; no impidieron las pruebas. El informe de lint anterior (21/10) conserva 0 errores y 30 advertencias: **no se volvió a ejecutar lint en este cierre**.

Las pruebas de navegador interceptan Supabase y bloquean conexiones externas del servicio. No enviaron códigos de correo ni aprobaron receptores reales. Las pruebas de servidor descritas en el informe web del 20/10 son evidencia previa; no se ejecutaron nuevamente en producción.

## Integridad de publicación

Se conserva el historial y los archivos del prototipo en la raíz. Se añaden las carpetas Android, panel y documentación. La app corresponde al paquete verificado de SPEC 4.3 y la web al código actual con SPEC de entrega 6.1. Los archivos de implementación se comparan por huella antes de publicar.

Se excluyen APK, firmas privadas, configuración local del SDK, dependencias instaladas, cachés, compilados, sesiones, fotografías de usuarios y registros de autenticación. La revisión por patrones no encontró claves privadas, secretos Supabase, tokens de acceso personales ni sesiones JWT en los archivos seleccionados. Un escaneo de patrones no es una auditoría exhaustiva de seguridad.

## Evidencia humana

Jeffry reportó aproximadamente siete participantes de 40–60 años, alrededor de septiembre de 2026: cinco opiniones favorables sobre facilidad/intuitividad y dos dificultades. No se dispone de versión, tareas o fichas individuales; no se transforma ese recuerdo en un porcentaje medido de éxito de la versión final. Ver INVESTIGACION-INICIAL.md.

El video está pendiente, confirmado por Jeffry; su guion está preparado. Entrega D2L y revisión independiente no confirmadas. Los hitos y tablero documentales no equivalen a Milestones/Projects nativos ni a registros históricos de sprints. Seguimiento: issue #9.
