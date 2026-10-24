# Noki — plan técnico de mantenimiento

SPEC 4.3 · Android 0.7.7 · 21 de octubre de 2026.

## Línea base y arquitectura

1. Android nativo: MainActivity, Compose/Material, navegación y accesibilidad.
2. Reglas compartidas: orientación determinista, estados de aparato/entrega, validación de solicitud y encuadre.
3. NokiModel: sesión, consentimiento, perfil, operaciones serializadas, caché pública y estado de recepción.
4. NokiApi: Auth, REST/RPC y Storage de Supabase. Permisos/transiciones del servidor no se delegan al cliente.
5. MapLibre/OSM para mapa; Geocoder para dirección y OSRM para ruta consentida.
6. Web administrativa existente: cliente independiente sobre el mismo backend; Firebase Hosting solo aloja su publicación.

## Invariantes que deben conservarse

- No sustituir identidades ni cerrar sesión por cualquier error de perfil/red.
- Login por contraseña existente y OTP siguen operativos; no habilitar OAuth sin configuración.
- Aparato pendiente no es entrega confirmada.
- Un solo envío activo del emisor; 1–20 aparatos y receptor distinto de sí mismo.
- Directorio público no se filtra por lista privada de receptores elegibles.
- Código/QR estable mientras pendiente; confirmación o cancelación lo invalida.
- Escaneo da vista previa; solo confirmación explícita del receptor completa nueva entrega.
- Receptor necesita revisión administrativa, no autoaprobación.
- Foto/consentimiento y permisos separados; ningún secreto administrativo en cliente.
- La variante de prácticas no se presenta como experiencia real.

## Secuencia para futuras modificaciones

1. Definir cambio y APP afectado; comprobar permisos y esquema si toca servicio.
2. Implementar mínimo cambio; preservar datos y compatibilidad.
3. Añadir regresiones a reglas/cliente/interfaz según riesgo.
4. Ejecutar compilación, suites locales y análisis; revisar presentación/accesibilidad cuando cambie UI.
5. Validar en entorno autorizado lo que requiera dispositivos o backend. No crear entregas reales ficticias como prueba.
6. Actualizar SPEC, matriz, pendientes y evidencia distinguiendo realizado/no realizado.
7. Incrementar versión cuando cambie aplicación; conservar firma en actualizaciones.
8. Exportar paquete con integridad, extraer en carpeta nueva y compilar.

## Esta entrega

Solo revisión documental, trazabilidad y empaquetado: no cambia fuentes funcionales, dependencias, contratos SQL, SMTP, cuentas ni firmas. Se verifica el contenido extraído, no solo la carpeta de trabajo.

## Fuera de esta entrega

[PENDIENTES](PENDIENTES.md) recoge recuperación diferenciada, gestión de cuenta, acceso social, seguridad/operación, distribución y otras plataformas. Son decisiones y trabajos posteriores, no tareas completadas por producir el ZIP.
