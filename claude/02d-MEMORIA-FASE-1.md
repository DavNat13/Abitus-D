# 02d - Memoria: Fase 1 (fundaciones y acceso), 2026-11-22

Complemento de `02-MEMORIA.md`. Decisiones técnicas, versiones agregadas y hallazgos de la Fase 1.

## Decisiones técnicas

| Decisión | Motivo |
|---|---|
| Límites de tasa guardados en PostgreSQL (`rate_limit_events`); `@fastify/rate-limit` solo como tope grueso por instancia | Las funciones serverless no comparten memoria; los límites por correo deben ser globales |
| Respuestas de registro, reenvío y recuperación con duración mínima de 1,5 s | Evita que el tiempo de respuesta revele si el correo tiene cuenta |
| Cada correo se encola en `email_outbox` y se intenta enviar en la misma solicitud; lo que falla queda pendiente para `/internal/jobs/process-mail-outbox` | Sin procesos en segundo plano en el servidor (regla 25) |
| El código de un solo uso se guarda en la cola solo hasta enviarse; luego se borra, y la limpieza programada borra los que tengan más de 15 minutos | Reintentar el envío sin dejar códigos legibles en la base |
| Cada solicitud autenticada comprueba que el dispositivo siga activo | Cerrar sesión, cerrar todo o cambiar la contraseña corta el acceso al instante, sin esperar 15 minutos |
| Token de refresco de 60 días con rotación en cada uso; reutilizar uno rotado revoca la familia y avisa por correo; uno revocado solo se rechaza | Detectar robo sin enviar avisos por cierres normales |
| Iniciar sesión sin haber verificado el correo devuelve `AUTH_EMAIL_NOT_VERIFIED` solo si la contraseña es correcta | No revela si la cuenta existe a quien no conoce la contraseña |
| Una sola plantilla de correo (`standard-mail.tsx`) más textos por idioma, en vez de un archivo por plantilla | Las seis plantillas comparten la misma estructura; evita duplicar código |
| Tokens del dispositivo cifrados con AES-256-GCM y una clave del Android Keystore, guardados en DataStore | `androidx.security.crypto` está obsoleta; sin dependencias nuevas |
| `devices.installation_id` (UUID de la instalación) único por usuario | Un mismo teléfono puede tener varios usuarios |
| Consentimiento `age` registrado junto a términos y privacidad | Deja constancia de la confirmación de 18 años |
| Textos legales en `legal/*.md`, copiados como recursos raw al compilar (`buildSrc/CopyLegalResources`) | Una sola fuente para el repositorio y la app |
| Iconos convertidos con `tools/convert-material-symbol.mjs` | Repetible; mismo trazo y tamaño |
| Detekt: `MatchingDeclarationName` desactivada | Los archivos de Compose agrupan un enum o estado pequeño con su composable; ktlint ya controla los nombres de archivo |
| Selector de idioma dentro de la app (AppCompat) diferido a la Fase 9 (ajustes); en Android 13 o superior ya funciona el idioma por app del sistema (`locales_config`) | Fuera del alcance de la Fase 1 |
| Token oscuro `inputBorder` cambiado de #707384 a #777A8C | El valor anterior daba 2,88 a 1 sobre `surfaceSoft`; ahora 3,18 (AA para controles) |
| URL de producción de la app marcada PENDIENTE (`https://abitus-d.vercel.app/`) | El despliegue quedó para el final |

## Hallazgos y correcciones a la documentación

- Roboto se distribuye hoy con licencia SIL OFL 1.1 (no Apache 2.0). Se corrigió `05b` y se registró en `distribution/THIRD_PARTY.md`.
- Una app ya instalada sin el permiso `INTERNET` que luego lo recibe por actualización puede quedar sin red (`SocketException`) hasta desinstalarla. Solo afectó al APK de la Fase 0 en el emulador.
- Una clase declarada dentro de un `build.gradle.kts` queda como clase interna y Gradle no puede instanciarla; las tareas propias van en `android/buildSrc`.
- Los nombres de variables de entorno de `07` se alinearon con `.env.example` (`REFRESH_TOKEN_PEPPER`, `CODE_PEPPER`, `IP_HASH_PEPPER`, `SMTP_PASSWORD`, `MAIL_DAILY_CAP`).

## Versiones agregadas (verificadas el 2026-11-22 en npm, Maven Central y el Maven de Google)

| Capa | Versiones |
|---|---|
| Backend | zod 4.6.7, fastify-type-provider-zod 7.0.0, kysely 0.29.9, @fastify/helmet 13.2.0, @fastify/rate-limit 11.3.0, @node-rs/argon2 2.2.2, jose 6.2.18, nodemailer 10.6.1, @react-email/components 1.0.12, @react-email/render 2.3.1, react 19.3.1, uuid 14.0.3 |
| Android | Hilt 2.60.1, hilt-lifecycle-viewmodel-compose 1.4.0, lifecycle 2.11.1, DataStore 1.3.0, sqlite-bundled 2.7.1, Retrofit 3.0.0, OkHttp 5.5.1, kotlinx-serialization 1.13.0, coroutines-test 1.11.0, Turbine 1.2.1 |
| Recursos | Roboto variable (google/fonts, OFL 1.1); Material Symbols Outlined (google/material-design-icons, Apache 2.0) |

## Pendiente que no bloquea la Fase 2

- Medir con Gmail real el tiempo de envío y la llegada a spam (necesita la contraseña de aplicación).
- Prueba instrumentada del renovador de tokens con dos solicitudes simultáneas (hoy está cubierta por las pruebas del servidor y el recorrido manual).
