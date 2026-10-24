# Tareas — Administración web 2.0

## Implementado

- [x] Extraer el RAR conservando el original.
- [x] Identificar los servicios simulados y las tablas reales usadas por Android.
- [x] Crear membresías para las dos cuentas confirmadas existentes, sin resetear credenciales.
- [x] Añadir sesión administrativa OTP, caducidad e inactividad verificadas en servidor.
- [x] Añadir API de consulta, revisión de receptores, mantenimiento de puntos y auditoría.
- [x] Publicar aprobaciones mediante el mismo mecanismo de la app, sin duplicar receptores.
- [x] Impedir autorrevisión y sobrescritura concurrente.
- [x] Habilitar consulta privada de fotos para administradores autorizados.
- [x] Añadir notificaciones Realtime sin datos personales y actualización de respaldo.
- [x] Construir Resumen, Usuarios, Receptores, Lugares, Entregas, Informes y Actividad.
- [x] Conservar y diferenciar Express.arte y los lugares de referencia reales.
- [x] Incorporar roles visibles y acceso directo a solicitudes pendientes.
- [x] Adaptar marca, texto grande, modo oscuro, contraste y movimiento reducido.
- [x] Generar informes agregados sin cifras ambientales inventadas.
- [x] Verificar contratos SQL en transacciones revertidas.
- [x] Ejecutar pruebas de componentes y navegador aisladas.
- [x] Revisar avisos de seguridad y dependencias de producción.

## Cierre y publicación

- [x] Ejecutar nuevamente la batería completa tras los últimos ajustes de presentación: 28 pruebas de componentes, 3 de navegador y compilación correctas.
- [x] Confirmar entrada real de un propietario en la nueva web sin compartir su OTP. Jeffry confirmó acceso y datos reales.
- [x] Autorizar Firebase con la cuenta que tiene acceso a `noki-89c7b` (paso personal del propietario).
- [x] Publicar exclusivamente Hosting, verificar cabeceras y abrir el sitio publicado: https://noki-89c7b.web.app.
- [ ] Repaso del propietario: nueva solicitud desde otra cuenta, fotos, revisión y reflejo en la app.

Los pasos de comprobación humana no se marcan como realizados por una prueba con datos ficticios. No se aprueba ninguna solicitud real como sustituto de una prueba.

## Revisión exclusivamente visual — 21 de octubre de 2026

- [x] Paleta Noki y jerarquía visual de acceso, navegación, tarjetas, tablas y diálogos.
- [x] Ilustración SVG/CSS local y animación de entrada finita, no bloqueante.
- [x] Indicador de correo/código sin cambios en el contrato de autenticación.
- [x] Modos claro/oscuro, teclado, contraste y movimiento reducido del sistema y del panel.
- [x] Comprobaciones a 320/390/820/1366/1440 píxeles y texto ampliado hasta 200 % en los escenarios documentados.
- [x] 28 pruebas de componentes y 10 recorridos de navegador aislados.
- [x] API, revisión de receptores, formulario de lugares, tipos, migraciones y configuración Hosting sin modificaciones, comprobado por SHA-256.
- [x] Publicar esta revisión visual en el Hosting existente tras renovar la autorización de Firebase y comprobar el sitio real: despliegue y comprobación de producción correctos el 21 de octubre de 2026.
