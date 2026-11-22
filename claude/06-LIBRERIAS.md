# 06 - Librerías

Regla: antes de instalar cada librería, VERIFICAR en su documentación oficial la versión estable vigente y su compatibilidad (con la versión de Kotlin, Compose, Node o Fastify en uso). Registrar versiones elegidas y fecha en `02-MEMORIA.md`. Esta lista fija el propósito, no el número de versión (referencia en `06b-LIBRERIAS-VERSIONES.md`).

Para cada dependencia nueva fuera de esta lista: justificar propósito, mantenimiento activo y licencia compatible con distribución gratuita.

## Android

| Librería | Propósito | Notas |
|---|---|---|
| Kotlin y Coroutines/Flow | Lenguaje y concurrencia | |
| Jetpack Compose (BOM) y Material 3 | Interfaz | Personalizado con los tokens de `05-UI-UX.md` |
| Navigation 3 | Navegación | Rutas tipadas; reemplaza a Navigation Compose |
| Hilt | Inyección de dependencias | |
| Room 3 | Base de datos local | Esquemas exportados y migraciones probadas |
| WorkManager | Sincronización en segundo plano | Restricción de red, reintento exponencial |
| DataStore | Preferencias | Sin SharedPreferences nuevas |
| Retrofit y OkHttp | Cliente HTTP | Interceptor de autenticación con renovación de token |
| kotlinx.serialization | JSON | |
| Gráficos para Compose (por ejemplo Vico) | Gráficos de evolución y volumen | VERIFICAR mantenimiento; alternativa: dibujar con Canvas |
| Play Services Location | GPS (FusedLocationProvider) | Plan B sin servicios de Google para dispositivos que no los tengan (LocationManager) |
| Health Connect client (androidx.health.connect) | Lectura de pasos, frecuencia cardíaca, sesiones | Versión estable vigente; la 1.2 estaba en alfa en 2026-11 |
| AndroidX WebKit (WebViewAssetLoader) | Cargar el visor 3D desde assets locales | Sin red, sin CDN |
| Three.js (empaquetado en assets) | Cuerpo 3D | Archivos locales; no cargar desde internet |
| AndroidX Core SplashScreen | Pantalla de arranque | |
| AndroidX AppCompat (solo localización) | Idioma por app (setApplicationLocales) | |
| Tink o alternativa vigente | Cifrado de tokens con clave de Android Keystore | VERIFICAR el estado de androidx.security.crypto antes de usarlo |
| Timber (o registro propio) | Logging sin datos sensibles | Desactivado en release salvo errores sin PII |
| JUnit, MockK, Turbine | Pruebas unitarias y de Flow | |
| Compose UI Test | Pruebas de interfaz | |
| Detekt y ktlint | Estilo y calidad | |
| Convertidor de codificación de polilíneas (propio) | Rutas GPS compactas | Implementación pequeña propia; sin librería de mapas |

Imágenes y animación: evitar librerías de carga de imágenes salvo que haya fotos reales; los ejercicios usan iconografía e ilustraciones locales (PENDIENTE: fuente de ilustraciones, ver decisiones).

## Backend (TypeScript)

| Librería | Propósito | Notas |
|---|---|---|
| Fastify | Servidor HTTP | |
| @fastify/helmet | Cabeceras de seguridad | |
| @fastify/cors | CORS | Cerrado por completo; la app nativa no lo usa |
| @fastify/rate-limit | Limitación de tasa | Reglas por IP, usuario y correo |
| Zod y proveedor de tipos Zod para Fastify | Validación de entrada y salida | Esquemas estrictos y cerrados |
| @fastify/swagger | Documento OpenAPI | Contrato compartido |
| Kysely y pg | Consultas tipadas y parametrizadas | Prohibido `sql.raw` con entrada de usuario |
| Script propio `database/scripts/migrate.mjs` sobre `pg` | Aplicar migraciones de `database/` | Elegido en la Fase 0 (ver `02`) |
| @node-rs/argon2 | Hash de contraseñas (Argon2id) | Binario precompilado; parámetros en `06b` |
| jose | JWT de acceso | Algoritmo asimétrico o secreto fuerte; rotación de claves |
| Nodemailer | Envío SMTP | Transporte configurable por entorno |
| React Email (componentes y render) | Plantillas HTML de correo | Respaldo: MJML |
| uuidv7 (o equivalente) | UUID v7 | Para identificadores del servidor |
| Pino | Logging estructurado | Redacción de campos sensibles |
| Vitest | Pruebas unitarias e integración | Con `fastify.inject` |
| Docker Compose (desarrollo) | PostgreSQL local para desarrollo y pruebas | |
| TypeScript, ESLint, Prettier | Calidad | Modo estricto |

## Herramientas transversales

| Herramienta | Propósito |
|---|---|
| k6 | Pruebas de carga (objetivo 500 usuarios virtuales simultáneos) |
| gitleaks o equivalente | Detección de secretos en el repositorio |
| npm audit (o equivalente) y revisión de dependencias de Gradle | Auditoría de vulnerabilidades |
| Script propio `tools/check-no-emoji` | Cumplir regla de cero emojis |
| Scripts `tools/check-contrast` y `tools/convert-material-symbol` | Contraste AA de los tokens; iconos Material Symbols a vectores |
| GitHub Actions (opcional) | Integración continua gratuita; VERIFICAR límites del plan |

## Política de dependencias

1. Preferir la librería estándar o de Jetpack frente a una de terceros.
2. Una dependencia nueva se documenta aquí con su propósito.
3. Sin SDK de analítica, publicidad ni seguimiento.
4. Revisar licencias; todo debe permitir uso y distribución gratuita.
5. Congelar versiones con archivo de bloqueo (`package-lock` o equivalente, catálogo de versiones de Gradle).
6. Actualizar dependencias de seguridad con prioridad; el resto, al cerrar cada fase.
