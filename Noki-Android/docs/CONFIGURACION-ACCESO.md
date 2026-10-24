# Acceso del cliente Android

Estado de la implementación 0.7.7 · SPEC 4.3, APP-08…APP-13.

El proyecto usa Supabase Auth existente. No reconfigurar cuentas ni SMTP para compilar.

- Entrada principal: correo y contraseña.
- Alternativa/registro: OTP enviado por correo, validado en el servicio.
- El mismo envío OTP puede crear identidad desconocida (create_user=true), también desde recuperar acceso. Esta limitación está documentada.
- Registro/recuperación ofrece crear contraseña, pero no la exige para usar código.
- Mi cuenta permite establecer/cambiar contraseña; no cambiar correo ni borrar cuenta automáticamente.
- Alias obligatorio, único y cambio controlado por servidor cada 30 días.
- Google y Apple no están habilitados en el cliente. No confundir contraseña SMTP con OAuth.

La URL y clave publicable en NokiApi identifican el servicio, no son credenciales administrativas. Tokens de sesión se cifran localmente. Contraseñas se procesan sin guardarlas. SMTP, plantillas, credenciales privadas y reglas remotas no están en el ZIP.

Los textos legales incorporados usan versión general 2026-10-09.1 y complemento de contacto de receptores. Las versiones históricas en archive no se aplican como configuración actual.

Una actualización del proyecto no debe recrear usuarios, modificar su correo, deshabilitar confirmación de correo ni sustituir la sesión porque falló cargar perfil.
