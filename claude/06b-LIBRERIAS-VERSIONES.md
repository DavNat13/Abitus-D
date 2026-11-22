# 06b - Librerías: versiones verificadas y cambios modernos

Complemento de `06-LIBRERIAS.md`. Datos verificados el 2026-11-22; son una foto, no una promesa. Regla 5: VERIFICAR otra vez al crear el proyecto y registrar las versiones finales en `02-MEMORIA.md`.

## Android

| Pieza | Estado verificado (2026-11) | Decisión |
|---|---|---|
| Compose BOM | 2026.11.01 estable (Compose 1.13). Exige Kotlin 2.2 o superior y compileSdk 37.1 | Usar el BOM vigente; compileSdk 37 o el que pida el BOM |
| Material 3 | Línea 1.4.0 estable; 1.5.0 en candidata al 2026-11-04 | Usar la versión que fije el BOM; Material 3 Expressive solo si aporta y respeta `05b` |
| Android Gradle Plugin | Serie 9.x (9.2.x en 2026-04). Kotlin integrado (sin plugin `kotlin-android`); pide Gradle 9.1 o superior y JDK 17 | Proyecto nuevo directamente en AGP 9 con DSL nuevo |
| Navigation | Navigation 3 estable (1.2.0, 2026-09); 1.3.0 en alfa | Usar Navigation 3 con rutas tipadas; no usar la alfa |
| Room | Room 3.0 (`androidx.room3`), estable 3.0.x desde 2026-07: solo KSP, genera solo Kotlin, exige un `SQLiteDriver`, API de corrutinas; Room 2.8 sigue existiendo | Proyecto nuevo con Room 3 y driver incluido de AndroidX; sin KAPT |
| Gráficos | Vico 3.x (3.1.0 trae gráfico circular experimental); un solo módulo `compose` | Vico para líneas y barras; Canvas para el mapa de calor y el cuerpo |
| Health Connect | 1.1.0 estable; 1.2 en alfa | Usar 1.1.0; no depender de la alfa |
| Inyección | Hilt sigue siendo el estándar de Android | Se mantiene Hilt con KSP |

Notas de migración útiles para Claude Code:
- Room 3: `androidx.room:room-runtime` pasa a `androidx.room3:room3-runtime`; las transacciones usan `withWriteTransaction`; el plugin de Gradle es `androidx.room3` con `schemaDirectory` obligatorio.
- Room 3 incluye conversor integrado para `kotlin.uuid.Uuid`, útil para los UUIDv7 de `09b`.
- Con AGP 9 las versiones de Kotlin y KSP deben coincidir; si hay conflicto, fijarlas de forma estricta en el catálogo.

## Backend

| Pieza | Estado verificado | Decisión |
|---|---|---|
| Hash Argon2id | `@node-rs/argon2` 2.0.2 (binding Rust, binarios precompilados, sin node-gyp, MIT). El paquete `argon2` (C++) falla a menudo al compilar en despliegues | Usar `@node-rs/argon2` |
| Parámetros Argon2id | Mínimo OWASP: m=19456 KiB, t=2, p=1; opción más fuerte m=65536, t=3, p=4 | Empezar con m=19456, t=2, p=1 por el límite de CPU serverless; medir en Fase 10 y subir si cabe |
| Zod | Serie 4 vigente | VERIFICAR compatibilidad con el proveedor de tipos de Fastify |
| Kysely, pg, Fastify, Nodemailer | Versiones estables vigentes | VERIFICAR al instalar |
| Hosting | Vercel Hobby (sin tarjeta, no comercial) | Ver `14-INFRAESTRUCTURA.md` |

Cambio respecto de `06-LIBRERIAS.md`: la fila `argon2` se sustituye por `@node-rs/argon2`, y CORS se cierra por completo (la app nativa no usa CORS; ya no hay PWA).

## Qué se revisa antes de cada fase

1. Notas de versión de Compose, Room y Navigation (los cambios mayores ocurren en agosto, noviembre y abril).
2. Avisos de seguridad de dependencias (`npm audit`, Gradle).
3. Cambios en límites gratuitos de Vercel, Neon y Gmail.
