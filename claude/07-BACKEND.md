# 07 - Backend

Monolito modular en TypeScript con Fastify, en contenedor Docker. Carpeta: `backend/`. Base de datos y migraciones viven en `database/` (capa separada).

## Módulos internos

| Módulo | Responsabilidad |
|---|---|
| auth | Registro, verificación, login, refresco, logout, recuperación de contraseña |
| account | Perfil, preferencias, dispositivos, consentimientos, exportación, eliminación |
| catalog | Disciplinas, músculos, equipamiento y ejercicios base con traducciones |
| sync | Intercambio incremental de datos del usuario (push y pull) |
| mail | Plantillas, cola de correo y transporte SMTP |
| health | Verificaciones de salud y disponibilidad del servicio |
| shared | Configuración, errores, validación, registro, utilidades |

Cada módulo: rutas, esquemas Zod, servicio (lógica), repositorio (consultas). Las rutas no contienen lógica de negocio ni SQL.

## Convenciones de API

- Prefijo `/v1`. JSON UTF-8.
- Errores en formato problem+json con un `code` estable (por ejemplo `AUTH_INVALID_CREDENTIALS`). Los mensajes para el usuario los pone el cliente, localizados por `code`.
- Paginación por cursor. Marcas de tiempo en ISO 8601 UTC.
- Esquemas de entrada cerrados (se rechazan campos desconocidos), con longitudes y rangos máximos.
- Límite de tamaño de cuerpo (por ejemplo 1 MB; sincronización por lotes acotados).
- Idempotencia: identificadores generados en el cliente; repetir una escritura no duplica.
- Documento OpenAPI generado desde los esquemas; es el contrato para la app.

## Endpoints v1

Públicos (sin token): registro, verificación, reenvío de código, login, refresco, recuperación de contraseña, salud del servicio. Todo lo demás exige token de acceso.

### Autenticación
| Método y ruta | Descripción |
|---|---|
| POST /v1/auth/register | Crea cuenta pendiente de verificación y envía código. Respuesta idéntica exista o no el correo |
| POST /v1/auth/verify-email | Verifica el código; activa la cuenta; devuelve tokens |
| POST /v1/auth/resend-code | Reenvía código respetando espera y tope diario |
| POST /v1/auth/login | Correo, contraseña y datos del dispositivo; devuelve tokens; si el dispositivo es nuevo, envía aviso por correo |
| POST /v1/auth/refresh | Rota el token de refresco |
| POST /v1/auth/logout | Revoca el dispositivo actual |
| POST /v1/auth/logout-all | Revoca todos los dispositivos |
| POST /v1/auth/password/forgot | Envía código de recuperación; respuesta idéntica exista o no el correo |
| POST /v1/auth/password/reset | Correo, código y nueva contraseña; revoca sesiones |
| POST /v1/auth/password/change | Con sesión activa: contraseña actual y nueva; revoca otras sesiones |

### Cuenta
| Método y ruta | Descripción |
|---|---|
| GET, PATCH /v1/me | Perfil |
| GET, PATCH /v1/me/preferences | Unidades, idioma, tema, timer, notificaciones |
| GET /v1/me/devices | Dispositivos con sesión |
| DELETE /v1/me/devices/{id} | Cerrar sesión remota |
| POST /v1/me/consents | Registrar aceptación de política y términos con versión |
| POST /v1/me/export | Exportar todos los datos del usuario (JSON; CSV opcional) |
| DELETE /v1/me | Eliminar cuenta y todos sus datos; exige contraseña y confirmación; envía correo de confirmación |

### Catálogo
| Método y ruta | Descripción |
|---|---|
| GET /v1/catalog?since={version}&locale={es} | Descarga incremental del catálogo con traducciones; soporta ETag |

### Sincronización
| Método y ruta | Descripción |
|---|---|
| POST /v1/sync/push | Lote de cambios del dispositivo |
| GET /v1/sync/pull?cursor={n}&limit={n} | Cambios del usuario posteriores al cursor |

Las rutinas, sesiones, series y demás datos del usuario viajan por sincronización; no hay un CRUD REST separado para ellos mientras el cliente sea la app. Las estadísticas se calculan en el dispositivo; si algún día se necesitan en el servidor, se agregan endpoints `/v1/stats/*` con las mismas consultas descritas en `08-DATABASE.md`.

### Operación
| Método y ruta | Descripción |
|---|---|
| GET /health | Vivo |
| GET /ready | Listo (verifica base de datos) |
| POST /internal/jobs/{name} | Tareas programadas; protegidas por secreto o identidad del planificador; no públicas |

## Sincronización

### Modelo
- Cada tabla sincronizable tiene `id` (UUID v7 del cliente), `user_id`, `updated_at`, `deleted_at` y `server_seq` (secuencia asignada por el servidor en cada escritura).
- Push: el cliente envía un lote (máximo 200 filas) con operaciones `upsert` o `delete` por tabla. El servidor valida cada fila, comprueba que pertenece al usuario y aplica reglas de conflicto.
- Pull: el cliente envía su último `cursor` y recibe cambios con `server_seq` mayor, en orden y en páginas (por ejemplo 500). La respuesta incluye `hasMore`, `nextCursor` y `minValidCursor`.
- Si el cursor del cliente es menor que `minValidCursor` (se purgaron tombstones antiguos), el cliente debe hacer resincronización completa.
- Tombstones: conservar al menos 90 días.

### Conflictos
- Las sesiones de entrenamiento y sus series son principalmente de solo adición; los conflictos son raros.
- Regla general: gana la modificación con `updated_at` más reciente (última escritura gana por fila). Si hay empate, gana la del servidor.
- Protección contra relojes desfasados: si `updated_at` del cliente supera la hora del servidor más 5 minutos, se recorta a la hora del servidor.
- Un `delete` posterior a un `upsert` se respeta; un `upsert` posterior a un `delete` revive la fila.
- El servidor nunca confía en `user_id` enviado por el cliente: lo toma del token.

### Cuotas por usuario (anti abuso y control de tamaño)
- Límite de filas por tabla y por usuario, de bytes por lote y de lotes por minuto. Los valores iniciales se definen en configuración y se ajustan con pruebas de carga.

## Seguridad del servidor

- Contraseñas: Argon2id con `@node-rs/argon2` y parámetros de `06b-LIBRERIAS-VERSIONES.md`; longitud mínima 10; rechazo de contraseñas comunes mediante lista local.
- Token de acceso: JWT de vida corta (por ejemplo 15 minutos) con `sub`, `did` (id de dispositivo), `jti` y `exp`.
- Token de refresco: aleatorio de 256 bits, opaco, guardado solo como hash; rotación en cada uso con detección de reutilización (si se reusa un token ya rotado, se revoca toda la familia y se avisa por correo).
- Códigos de verificación y recuperación: 6 dígitos, almacenados con HMAC y pepper del servidor, vigencia de 10 minutos, máximo 5 intentos, espera de 60 segundos entre reenvíos, tope diario por correo e IP.
- Limitación de tasa: login por IP y por correo con retardo progresivo; registro y recuperación por IP y por correo; sincronización por usuario.
- Sin enumeración de cuentas: respuestas y tiempos similares exista o no el correo.
- Autorización: toda consulta incluye `user_id` del token. Pruebas automáticas contra acceso a recursos ajenos (IDOR).
- Entrada: esquemas Zod cerrados; longitudes máximas; normalización de correo (minúsculas, recorte).
- SQL: solo consultas parametrizadas mediante Kysely; revisión de cualquier uso de SQL crudo.
- Cabeceras de seguridad (helmet), HTTPS solamente (HSTS), CORS cerrado.
- Registro (logs): sin contraseñas, códigos, tokens ni correos completos; enmascarar identificadores. Incluir id de solicitud.
- Secretos por variables de entorno (ver `14-INFRAESTRUCTURA.md`); rotación posible sin reescribir código.
- Eliminación de cuenta: borrado de todos los datos del usuario y de los tokens; conservar solo lo mínimo legalmente necesario (por ejemplo registro de eliminación sin datos personales).

## Correo (resumen)

Detalle en `15-CORREOS.md`. Puntos clave:
- El envío de códigos de verificación y recuperación se hace dentro de la solicitud, con tiempo de espera acotado, porque el cómputo solo corre durante solicitudes. Si falla, se registra en `email_outbox` y se reintenta por la tarea programada o en la siguiente solicitud.
- La cola prioriza seguridad por encima de bienvenida y resúmenes, respetando la cuota diaria del proveedor.

## Rendimiento

- Objetivo: percentil 95 menor a 300 ms en lecturas con instancia activa, sin contar el arranque en frío de la función serverless.
- Pool de conexiones pequeño por función (por ejemplo 3) usando el pooler de Neon; tiempo máximo de consulta (statement timeout).
- Compresión de respuestas; ETag para el catálogo.
- Evitar N+1: consultas por lote en sincronización.
- Concurrencia de funciones acorde con el máximo de conexiones del pooler de Neon.
- Medir con k6: 500 usuarios virtuales simultáneos con mezcla realista (ver `17-CALIDAD-Y-PRUEBAS.md`).

## Configuración por entorno

Variables (sin valores) documentadas en `.env.example`: `NODE_ENV`, `PORT`, `LOG_LEVEL`, `DATABASE_URL`, `JWT_ACCESS_SECRET`, `REFRESH_TOKEN_PEPPER`, `CODE_PEPPER`, `IP_HASH_PEPPER`, `JOBS_SECRET`, `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `MAIL_FROM`, `MAIL_DAILY_CAP`. El servidor falla al arrancar si falta una obligatoria o si un secreto tiene menos de 32 caracteres.
