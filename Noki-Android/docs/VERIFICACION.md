# Verificación de entrega — SPEC 4.3 / Android 0.7.7

21 de octubre de 2026. Código de versión 15, paquete co.noki.app. Alcance: revisión documental, trazabilidad, compilación y empaquetado; sin modificaciones funcionales ni operaciones sobre usuarios.

## Revisión de especificación

- 40 apartados APP-01…APP-40 con implementación o procedimiento de verificación identificado.
- 48 archivos Kotlin de producción en el inventario inverso.
- 95 archivos de fuentes, recursos, pruebas, configuración de módulos y migraciones comparados antes/después: idénticos por SHA-256.
- Correspondencia comprobada entre fuentes originales y las extraídas del paquete de validación.
- SPEC sin nombres/seriales de teléfonos de ensayo ni eventos de instalación.
- Enlaces locales de los documentos vigentes nuevos comprobados; documentos históricos separados.
- Diferencias del SPEC anterior registradas en PENDIENTES; original conservado, no funciones inexistentes presentadas como disponibles.
- Scripts de exportación/validación revisados sintácticamente. Una errata de paréntesis detectada durante preparación fue corregida antes del paquete final.

## Compilación desde el contenido extraído

Se creó un paquete preparatorio, se extrajo en una carpeta nueva sin local.properties, .idea, .gradle ni compilados, y se ejecutó:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :shared:jvmTest :app:lintDebug --no-daemon
```

Resultado: **BUILD SUCCESSFUL**, 63 tareas ejecutadas, 10 min 1 s. No fue solo reutilización de informes anteriores. Se utilizó JDK 17 y SDK Android del equipo mediante variables de entorno; herramientas y caché de dependencias externas no se incorporan al ZIP.

Después de esa compilación, los ajustes restantes fueron documentación y herramientas de empaquetado. No cambiaron las fuentes funcionales compiladas. El ZIP definitivo conserva esas mismas fuentes y el APK producido por esa compilación; las huellas permiten verificarlo.

## Pruebas ejecutadas

| Suite | Informes | Pruebas | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|---:|
| Android / Compose / Robolectric | 12 | 88 | 0 | 0 | 0 |
| Núcleo JVM compartido | 4 | 27 | 0 | 0 | 0 |
| Total | 16 | 115 | 0 | 0 | 0 |

Cobertura parcial: correo/contraseña y OTP, preservación de sesión, perfil/foto, orientación, preparación/resultado, una entrega activa, contacto, directorio, selección/ruta, códigos estables y validación visible del formulario de receptor. Las pruebas de interfaz usan datos/respuestas controladas, no receptores ni entregas físicas reales.

Informes reproducibles tras compilar: app/build/reports/tests/testDebugUnitTest/, shared/build/reports/tests/jvmTest/ y app/build/reports/lint-results-debug.html.

## Análisis estático

**Cero errores y 30 advertencias**, no se ocultaron ni suprimieron para presentar un resultado limpio.

| Categoría | Cantidad |
|---|---:|
| AndroidGradlePluginVersion | 1 |
| DiscouragedApi | 2 |
| GradleDependency | 8 |
| LockedOrientationActivity | 2 |
| ModifierParameter | 2 |
| MonochromeLauncherIcon | 1 |
| NewerVersionAvailable | 4 |
| UseKtx | 10 |

Además, la construcción avisó sobre versiones de XML entre herramientas SDK, un icono Compose obsoleto y bibliotecas nativas sin quitar símbolos. No impidieron generar el APK. Estas advertencias y revisiones pendientes impiden presentar la entrega como garantía absoluta de ausencia de defectos.

## Contrato del servicio consultado

Solo consultas de lectura de metadatos/definiciones de Supabase, sin volcar usuarios, fotos ni secretos:

- Contratos de perfil, operación nativa y administración presentes.
- Operación de códigos conserva valid_until=delivery_closed.
- Confirmación del receptor completa la entrega en la función del servidor.
- Límite de cinco intentos/ventana de quince minutos y reinicio tras validación correcta presentes.

Esto comprueba correspondencia del contrato relevante, no es una auditoría integral de permisos ni una nueva prueba física de recepción. No se modificaron SMTP, Auth, RLS, migraciones desplegadas, cuentas, aprobaciones ni datos.

## APK entregable y seguridad del paquete

APK debug recompilado desde las fuentes extraídas, 70.732.339 bytes:
`245BE683645FFF618FB136A07AF6FE0B2573330BCFFA89CC2FFE359E9FE222E9` (SHA-256).

Verificación criptográfica del APK correcta. El certificado coincide con el de la compilación anterior; no se exporta la clave privada. Se conserva copia del compilado anterior en el área local de comprobaciones. No se instaló/actualizó ninguna app en teléfonos durante esta tarea.

Exportación incluye proyecto, SPEC/plan/tareas/matriz/pendientes/evidencia y APK. Excluye claves privadas, sesiones, contraseñas, configuración local del SDK, cachés y archivos de compilación salvo el APK explícito. Se escanean patrones de secretos; no es garantía matemática de ausencia de cualquier secreto desconocido.

Test-ProjectArchive comprueba rutas seguras, archivos necesarios, ausencia de entradas duplicadas, huellas de cada archivo y equivalencia de documentos. La matriz de la raíz solo ajusta rutas de enlaces al código.

## No realizado ni certificado

- No se enviaron correos, solicitudes, fotos ni entregas reales como prueba.
- No se ejecutaron pruebas instrumentadas sobre teléfonos en esta entrega.
- No se certificó accesibilidad integral/TalkBack, compatibilidad universal, iOS, Windows, firma de producción ni publicación en tienda.
- No se probó restauración completa del backend en otra cuenta.
- No se presenta como solucionado ningún pendiente legal, operativo, de seguridad o funcional descrito en PENDIENTES.

