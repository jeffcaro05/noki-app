# Plan de integración administrativa

Fecha: 20 de octubre de 2026. Fuente: archivo «Noki Web 2,0.rar» de Luis Miguel.

1. Preservar el prototipo y sustituir sus datos simulados en la aplicación activa.
2. Autorizar únicamente las dos cuentas propietarias mediante código enviado por Supabase Auth. Separar la sesión administrativa de la sesión móvil.
3. Conectar usuarios, solicitudes de receptor, puntos y entregas con las tablas que realmente usa Android. No utilizar las tablas antiguas del prototipo como fuente de verdad.
4. Publicar las aprobaciones con el mecanismo existente de la app, registrar auditoría y aplicar control de concurrencia.
5. Adaptar identidad visual, navegación, modo oscuro y accesibilidad. Mostrar estados vacíos, errores y reconexión sin inventar datos.
6. Comprobar permisos, regresiones, compilación, navegación y acceso real por correo. Documentar por separado lo implementado, probado y pendiente de publicación.

## Límites

No cambiar contraseñas, borrado de cuentas, identidad de firma Android ni códigos de entrega. No aprobar solicitudes reales automáticamente. No almacenar documentos de identidad ni introducir biometría. No inventar métricas de peso, reciclaje o emisiones. No publicar encima de un sitio existente sin confirmar el destino.

La administración de entregas es de consulta: solo el receptor asignado confirma la recepción desde la app. Las decisiones de revisión requieren una nota; quien solicita un perfil de receptor no puede aprobarse a sí mismo.

## Revisión visual — 21 de octubre de 2026

1. Conservar logos y tipografía local; definir azul profundo, verde bosque y superficies claras cálidas/oscuro suave.
2. Separar la presentación en `identity.css` y la ilustración decorativa en `BrandCircuit.tsx`, sin tocar las operaciones de datos.
3. Mantener el formulario utilizable durante la entrada; limitar la animación a una reproducción de cuatro segundos y respetar movimiento reducido.
4. Comprobar componentes, navegación completa, correo/código, teclado, contraste, móvil y texto ampliado con tráfico de prueba interceptado.
5. Comparar los archivos de API, formularios operativos, tipos, configuración de Firebase y migraciones con sus huellas anteriores.
6. Compilar y publicar únicamente Hosting en la misma dirección, después de la autorización personal de Firebase. Comprobar la versión publicada sin enviar correos ni alterar registros.
