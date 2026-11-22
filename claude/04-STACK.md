# 04 - Stack tecnológico

Las versiones exactas se verifican al crear cada proyecto (regla 5 de `03-REGLAS-DE-ORO.md`). Aquí se fija la tecnología; versiones de referencia en `06b-LIBRERIAS-VERSIONES.md`. Cambios clave: Navigation 3, Room 3 y AGP 9.

## Resumen

| Capa | Tecnología | Motivo |
|---|---|---|
| App Android | Kotlin, Jetpack Compose, Material 3 personalizado | Nativo; necesario para timers en segundo plano, GPS y Health Connect |
| Base de datos local | Room 3 (SQLite) | Offline-first, consultas analíticas locales rápidas |
| Sincronización | WorkManager + protocolo propio de push y pull | Reintentos, restricciones de red, ejecución en segundo plano |
| Inyección de dependencias | Hilt | Estándar en Android |
| Red | Retrofit + OkHttp + kotlinx.serialization | Maduro y tipado |
| Backend | TypeScript sobre Node LTS con Fastify | Rápido, ligero, buena validación y plantillas de correo |
| Validación | Zod | Esquemas cerrados compartidos con la documentación OpenAPI |
| Acceso a datos | Kysely sobre el controlador `pg` | Consultas parametrizadas con tipos |
| Base de datos remota | PostgreSQL 16 o superior | Relacional, índices, agregaciones |
| Migraciones | Archivos SQL versionados en `database/migrations` | Capa de base de datos separada y revisable |
| Autenticación | Argon2id (`@node-rs/argon2`), JWT de acceso corto (jose), refresco opaco rotativo | Seguridad y sesiones por dispositivo |
| Correo | Nodemailer sobre SMTP configurable, plantillas con React Email | Independencia del proveedor y diseño cuidado |
| Contenedor | Docker (desarrollo y plan de salida) | Portabilidad entre proveedores |
| Hosting del backend | Vercel Hobby (sin tarjeta, uso no comercial) | Gratuito, Node con Fastify, arranque en frío corto |
| Base de datos en la nube | Neon (PostgreSQL), región São Paulo | Gratuito sin tarjeta, estándar; Supabase como alternativa |
| Correo saliente | Gmail SMTP con contraseña de aplicación (app.abitus.d@gmail.com) | Sin dominio ni tarjeta; 500 destinatarios por día |
| Cuerpo humano | Mapa 2D en SVG y luego 3D con Three.js dentro de WebView | Interacción rica con iteración rápida |
| Mapas y GPS | FusedLocationProvider; sin mapa de terceros de pago | Costo cero |
| Salud | Health Connect (lectura) | Pasos, frecuencia cardíaca y sesiones de ejercicio |
| Pruebas de carga | k6 | Validar 500 usuarios simultáneos |

## Principios de elección

- Una sola base de código por capa, sin frameworks que obliguen a pagar.
- Cada pieza reemplazable: SMTP configurable, PostgreSQL estándar, Docker.
- Seguridad por defecto: consultas parametrizadas, esquemas cerrados.
- Si hay duda entre una librería nueva y la estándar, gana la estándar.

## Arquitectura general

```
App Android  <--HTTPS/JSON-->  Backend (Vercel)  <--SQL-->  PostgreSQL (Neon)
 Room local                      Fastify + Zod                    migraciones en /database
 WorkManager sync                Auth + Sync + Catalog            semillas versionadas
                                 Correo SMTP -----> Gmail
```

- Monolito modular: un único servicio de backend con módulos internos (auth, cuenta, catálogo, sincronización, correo).
- Contrato único de API documentado en OpenAPI; lo consume la app Android.
- La app funciona completa sin conexión. El servidor es el punto de encuentro entre dispositivos y el respaldo.

## Plataformas y requisitos de desarrollo

- Android: minSdk 29 (Android 10), targetSdk y compileSdk de la versión estable vigente (VERIFICAR).
- Java Development Kit y Android Studio de versiones compatibles con el plugin de Android Gradle vigente (VERIFICAR).
- Node LTS vigente (VERIFICAR) y gestor de paquetes elegido en la Fase 0 (registrar en memoria).
- Docker para base de datos local y pruebas del backend.
- Entorno de desarrollo del autor: Windows.

## Alternativas descartadas

Ver la tabla "Decisiones descartadas" de `02-MEMORIA.md`.

## Qué no se usa y por qué

- Firebase y servicios que exijan reescribir el backend a su modelo: bloqueo de proveedor.
- Redis o colas externas: se evita el costo; las colas de correo viven en PostgreSQL.
- Analítica o publicidad de terceros: privacidad; no hay SDK de seguimiento.
- Servicios de mapas de pago: costo cero.
