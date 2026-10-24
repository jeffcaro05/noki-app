# Configuración del acceso

Fecha: 10 de octubre de 2026.

## Estado

Auth requiere confirmación de correo. Google y Apple no están configurados. El titular guardó SMTP de Gmail y las plantillas «Magic link or OTP» y «Confirm sign up». El 8 de octubre a las 15:11 UTC, una solicitud autorizada a `/auth/v1/otp` respondió HTTP 200; el titular confirmó después la llegada a su Gmail. No se leyó ni almacenó el código o la contraseña SMTP en el proyecto.

La app consulta `public.noki_client_config.email_otp_enabled` al iniciar y entrar al formulario. Este indicador permite habilitar el recorrido después de la comprobación de envío sin generar otro APK; no sustituye verificación ni controles del servidor. Las pruebas de contrato cubren solicitud y verificación; la recepción de un correo por sí sola no prueba el inicio de sesión.

El indicador está activo. El titular confirmó después un acceso correcto con un nuevo código solicitado desde la app. La comprobación del servidor confirmó correo verificado, sesión activa, perfil creado y aceptación vigente `2026-10-08.1`. No se almacenó ni se pidió el código. Reingreso, otros dominios y errores de correo conservan pruebas pendientes.

## Correo con código

Remitente de prueba: Noki <nokiappcorporation@gmail.com>. Host `smtp.gmail.com`, puerto 465, usuario igual al remitente y contraseña de aplicación introducida por el titular exclusivamente en Supabase. La cuenta tiene verificación en dos pasos. No se contrató dominio ni servicio adicional. Gmail se utiliza para validar el recorrido inicial, sin prometer capacidad de envío masivo.

La versión 0.6.0 registra aceptación general en servidor además del recibo local cifrado. Sigue pendiente la revisión jurídica y operativa previa a apertura pública. Los receptores requieren revisión humana del lugar; no obtienen aprobación al registrarse o editar el perfil.

1. Para pruebas iniciales, usar el SMTP autorizado por el titular. Para distribución, resolver capacidad, reputación del remitente, límites, dominio y coste antes de contratar otro servicio.
2. Configurar SMTP en Auth; los secretos permanecen en Supabase, nunca en el APK ni en estos documentos.
3. Mantener confirmación de correo obligatoria. Configurar `{{ .Token }}` en ambas plantillas, Magic Link/OTP y Confirm sign up. Asunto: «Tu código para entrar a Noki». `docs/email-codigo.html` conserva el cuerpo de referencia, sin código fijo ni secretos.
4. Establecer caducidad, límites y protección contra abuso. La espera de 60 segundos del cliente complementa, no sustituye, la del servidor.
5. Mantener aceptación expresa de términos y privacidad y constancia versionada; completar revisión jurídica antes de apertura pública.
6. Probar recepción y verificación con direcciones controladas por el equipo en Gmail, iCloud y Outlook; verificar código vencido, incorrecto, reusado y cambio de correo.
7. Activar el indicador de servidor para el recorrido integrado tras comprobar envío y recepción. Completar la matriz antes de distribución pública. Un HTTP correcto al solicitar no demuestra entrega en la bandeja de entrada.

El servicio SMTP predeterminado de Supabase limita destinatarios y frecuencia; no sirve como habilitación general de registro. Los proyectos Free nuevos con ese SMTP tampoco permiten personalizar libremente plantillas. Fuentes: [SMTP](https://supabase.com/docs/guides/auth/auth-smtp), [códigos por correo](https://supabase.com/docs/guides/auth/auth-email-passwordless), [plantillas en Free](https://supabase.com/changelog/46599-changes-to-email-template-customisation-on-free-tier).

## Google y Apple

Actualización del 8 de octubre: el titular pidió posponer Google y conservar el login por correo. El diseño futuro aprobado es Google directo y código por correo como alternativa, sin OTP adicional obligatorio tras Google. No se ha habilitado OAuth ni cambiado SMTP, plantillas o cuentas con esta entrega. Consulta pública de Auth: correo habilitado, Google deshabilitado.

Las cuentas y clientes se configuran en las consolas oficiales por el titular del proyecto. Se requieren los identificadores, consentimientos, redirecciones y credenciales adecuados a cada plataforma. La validación se realiza mediante Supabase Auth; la app nunca recoge contraseñas de Google ni Apple. Se prueba cancelación, retorno a la acción pendiente, reautenticación y verificación de identidad antes de habilitar los botones.

Un correo que termine en `@icloud.com` puede usar el flujo por código sin equivaler a “Continuar con Apple”. Tener un iPhone no activa ese proveedor ni permite compilar iOS desde Windows.

## Perfil

El nombre se guarda en `noki_profiles.display_name` mediante una operación que deriva el titular de la sesión. La foto usa el bucket privado `noki-profile-photos` y una única ruta `<user_id>/avatar.jpg`. El consentimiento está en `noki_private.account_consents`. Cambiar nombre o foto no modifica roles; no se usa metadata editable para conceder permisos.

La app permite añadir, previsualizar, guardar, sustituir y quitar una foto personal. Las pruebas locales comprueban formato, recorte, tamaño y eliminación de ubicación; SQL comprueba propiedad, versión, consentimiento retirado y sesión inválida. La subida y eliminación reales desde el teléfono se verifican después del primer acceso.

La recuperación por código devuelve acceso a la misma cuenta y ofrece guardar contraseña nueva sin imponer el cambio. Perfil permite crear/cambiar contraseña mediante PUT autenticado a /auth/v1/user, repetición coincidente y mínimo 12 caracteres y límite del servicio de 72 bytes UTF-8 sin truncar. Correo/contraseña es la entrada principal; código sigue como alternativa. Se interpreta error_code antes de code numérico; no se guardan contraseñas. Cambio de correo y eliminación de cuenta siguen pendientes.
