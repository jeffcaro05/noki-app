# Trazabilidad académica

Fecha: 24/10/2026. Los criterios CA01–CA15 se detallan en el plan de proyecto. La matriz funcional completa de Android está en [MATRIZ-SPEC-CODIGO.md](../../Noki-Android/docs/MATRIZ-SPEC-CODIGO.md); la web se especifica en [SPEC-WEB.md](../../Noki-Web-Admin/SPEC-WEB.md). Los issues se crean en este cierre, no retrospectivamente como supuesta evidencia de sprints anteriores.

| Historia y criterio | Issue | Implementación principal | Verificación |
|---|---|---|---|
| HU-01 / CA01–02 | [#1](https://github.com/jeffcaro05/noki-app/issues/1) | `Orientation.kt`, `Journey.kt`, pantallas de orientación y decisión | `OrientationTest.kt`, `JourneyTest.kt` |
| HU-02 / CA03–04 | [#2](https://github.com/jeffcaro05/noki-app/issues/2) | `AccountScreen.kt`, `NokiApi.kt`, `SecureStore.kt` | `EmailAccessTest.kt`, `SessionPreservationTest.kt` |
| HU-03 / CA05–08 | [#3](https://github.com/jeffcaro05/noki-app/issues/3) | `Delivery.kt`, `DeliveryScreens.kt`, migraciones de entrega | `DeliveryTest.kt`, `StableDeliveryCodeTest.kt`, `DeliveryExperienceTest.kt` |
| HU-04 / CA09–11 | [#4](https://github.com/jeffcaro05/noki-app/issues/4) | `ReceiverApplicationForm.kt`, componentes administrativos y API de revisión | `ReceiverApplicationExperienceTest.kt`, pruebas web; SQL de seguridad conservado |
| HU-05 / CA12 | [#5](https://github.com/jeffcaro05/noki-app/issues/5) | `NativeMap.kt`, `DeliveryMapContinuation.kt` | `DeliveryMapContinuationTest.kt`, `PublishedDestinationsTest.kt` |
| HU-06 / CA13 | [#6](https://github.com/jeffcaro05/noki-app/issues/6) | `ProfileCropDialog.kt`, `AccountScreen.kt` | `PhotoFrameTest.kt`, `ProfileAccessTest.kt` |
| HU-07 / CA14 | [#7](https://github.com/jeffcaro05/noki-app/issues/7) | `AccessibilityScreen.kt`, `NokiApp.kt`, estilos y preferencias web | Matriz de app y pruebas de navegador; evaluación humana integral no certificada |
| HU-08 / CA15 | [#8](https://github.com/jeffcaro05/noki-app/issues/8) | `Noki-Web-Admin/src/admin`, avisos y consulta del servicio | 28 pruebas unitarias y 10 de navegador; informe de servidor previo |

## Inventario del proceso

- Necesidad y público: plan de proyecto e INVESTIGACION-INICIAL.md.
- Requisitos y alcance: SPEC app 4.3, SPEC web 6.1, BACKLOG.md y ocho issues.
- Planificación: plan académico; `Noki-Android/docs/PLAN.md`, `TASKS.md`; `Noki-Web-Admin/docs/PLAN-INTEGRACION.md`, `TASKS-INTEGRACION.md`.
- Decisiones: DECISIONES-ARQUITECTURA.md, cinco ADR de cierre.
- Código: las dos carpetas de producto; migraciones históricas no equivalen a respaldo o instalador de base de datos.
- Verificación: VERIFICACION-CIERRE.md y reportes originales; 153 pruebas aprobadas en las ejecuciones finales (115 + 28 + 10), con primer timeout de navegador registrado.
- Apoyo de IA: [bitacora_IA.md](../../bitacora_IA.md).
- Pendientes humanos y de formalización: [#9](https://github.com/jeffcaro05/noki-app/issues/9).

Este inventario hace trazable la consolidación disponible. No atribuye adopción histórica de un método, plugin o artefacto de Spec Kit que no conste, ni certifica que toda la evolución del prototipo esté cubierta por la matriz del producto actual.
