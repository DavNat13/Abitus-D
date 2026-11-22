# 15 - Correos automáticos

Plantillas y diseño en `15b-CORREOS-PLANTILLAS.md`. Este archivo cubre catálogo, transporte, cuotas y antiabuso. Los datos de proveedores se VERIFICAN antes de usarlos.

## Transporte

- Nodemailer sobre SMTP configurable por variables de entorno (`SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASS`, `MAIL_FROM`). Cambiar de proveedor no toca el código.
- Proveedor: Gmail por SMTP con la cuenta app.abitus.d@gmail.com. Requiere verificación en dos pasos y una contraseña de aplicación (guardada solo como secreto de entorno). El remitente debe ser esa misma dirección. Límite aproximado de 500 destinatarios por día (VERIFICAR). Host y puerto según la documentación vigente de Google.
- Brevo u otro proveedor transaccional queda diferido: exige autenticar un dominio, que no se compra.
- Desarrollo: servidor de pruebas local (Mailpit) para no gastar la cuota de Gmail.
- Riesgo: Google puede bloquear la cuenta por envío masivo; por eso la cola prioriza y limita (ver presupuesto).
- Servidor SMTP propio: descartado (puerto 25 bloqueado en la nube, IPs sin reputación). Ver `02-MEMORIA.md`.
- Sin dominio propio no hay SPF/DKIM propios; al enviar desde Gmail se usa la autenticación de Google. Se indica a los amigos revisar spam la primera vez.

## Catálogo de correos

| Correo | Disparador | Prioridad | Contenido clave |
|---|---|---|---|
| Código de verificación | Registro y reenvío | Crítica | Código de 6 dígitos, vigencia de 10 minutos, aviso de no compartirlo |
| Bienvenida | Al verificar la cuenta | Baja | Qué puede hacer, primeros pasos, enlace a ayuda |
| Aviso de dispositivo nuevo | Login desde un dispositivo no visto | Alta | Dispositivo, fecha y hora aproximada, cómo cerrar sesiones |
| Código de recuperación | Olvidé mi contraseña | Crítica | Código, vigencia, aviso si no lo pidió |
| Contraseña cambiada | Cambio o restablecimiento | Alta | Confirmación y cómo actuar si no fue el usuario |
| Sesión cerrada en todos los dispositivos | Logout global o reutilización de token | Alta | Motivo y recomendación de cambiar la contraseña |
| Exportación lista | Solicitud de exportación | Media | Cómo obtener el archivo; sin datos de entrenamiento en el cuerpo |
| Cuenta eliminada | Eliminación completada | Alta | Confirmación y qué se borró |
| Resumen semanal (opcional) | Tarea programada, con consentimiento | Baja | Sesiones, volumen, mejoras; con enlace para desactivar |
| Cambios en términos o privacidad | Nueva versión de documentos | Media | Qué cambia y cómo revisarlo |

Regla: ningún correo contiene contraseñas, tokens de sesión ni datos de entrenamiento detallados.

## Envío y cola

- Códigos de verificación y recuperación: se envían dentro de la solicitud, con tiempo de espera acotado (por ejemplo 8 segundos). Si falla, se guardan en `email_outbox` para reintento.
- Resto de correos: se encolan en `email_outbox` con prioridad y se envían al atender solicitudes o por una tarea programada.
- Orden de prioridad: crítica, alta, media, baja. Si se acerca la cuota diaria, se posterga lo de baja prioridad.
- Reintentos con espera creciente y máximo de intentos; los fallos quedan registrados sin datos sensibles.

## Presupuesto diario

Con unos 500 al día en Gmail (usar un tope operativo de 300): reservar al menos el 70 por ciento para correos críticos y de alta prioridad. Un registro consume 2 correos (código y bienvenida). Alertar al usar el 80 por ciento de la cuota diaria y registrar el consumo por día.

## Antiabuso

- Límites por correo e IP en registro, reenvío de código y recuperación.
- Espera de 60 segundos entre reenvíos y tope diario por correo.
- Respuestas idénticas exista o no la cuenta; el aviso real llega solo al titular.
- Códigos con HMAC y pepper, vigencia de 10 minutos, 5 intentos.
- No enviar correos a direcciones que rebotaron repetidamente.
- El enlace o código de un correo nunca autentica por sí solo una acción destructiva.

## Localización

- Cada plantilla existe en español e inglés; se elige por el idioma del usuario, o por el idioma de la app en el registro.
- Formato de fecha y hora según idioma; zona horaria del usuario si se conoce, o UTC indicada.
- Agregar un idioma nuevo implica agregar un archivo de textos, no cambiar la lógica.

## Calidad y entrega

- HTML con tablas y estilos en línea, ancho de 600 px, compatible con clientes principales (VERIFICAR con pruebas reales).
- Versión de texto plano siempre incluida.
- Texto de prevista (preheader) en cada correo.
- Modo oscuro razonable (colores que no se rompan al invertirse).
- Enlace de baja y preferencias en los correos no críticos; los de seguridad no se pueden desactivar.
- Sin emojis (regla de oro) y sin imágenes críticas: el correo se entiende con imágenes bloqueadas.
- Pruebas: renderizado de cada plantilla en es y en, verificación de que no hay emojis ni enlaces rotos.
