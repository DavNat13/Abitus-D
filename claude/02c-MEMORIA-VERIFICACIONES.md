# 02c - Memoria: verificaciones externas de la Fase 0 (2026-11-22)

Complemento de `02-MEMORIA.md`. Datos leídos en fuentes oficiales el 2026-11-22. Cambian: VERIFICAR de nuevo antes de depender de ellos. Prevalecen sobre cifras anteriores de `02`, `02b` y `06b`.

## Verificación de desarrolladores de Android (APK fuera de Play)

Fuentes: developer.android.com/developer-verification/guides, /guides/limited-distribution, /guides/faq y el blog "Expanding Android developer verification in 2027" (2026-11-19).

- En vigor desde el 2026-09-30 en Brasil, Indonesia, Singapur y Tailandia, para dispositivos certificados con Android 8 o superior.
- Expansión global desde enero de 2027, gradual "a todos los países" durante varios meses, sin fecha por país. Chile no figura con fecha: aplicará en algún momento de 2027.
- Cuenta de distribución limitada: gratuita, sin documento de identidad; exige cuenta de Google con verificación en dos pasos, perfil de pagos de Google (nombre y dirección, sin cobro) y correo de contacto. Hasta 50 dispositivos autorizados (antes 20), cada uno acepta con QR o enlace. Uso personal sin fines comerciales. Se puede pasar luego a cuenta completa (25 USD), no al revés.
- Sin registro, el usuario aún puede instalar por ADB o con el "flujo avanzado": modo desarrollador, comprobación antiestafa, reinicio, espera única de un día, confirmación biométrica; permiso por 7 días o indefinido. Las actualizaciones fallan si el flujo está desactivado.
- Conclusión: hoy no afecta a Chile; antes de la Fase 10 hay que registrar `com.abitusd.app` en una cuenta de distribución limitada (ver `12`).

## Vercel Hobby

Fuentes: vercel.com/docs/plans/hobby, /docs/limits, /docs/functions/limitations, /docs/cron-jobs/usage-and-pricing, /docs/functions/configuring-functions/region, /docs/frameworks/backend/fastify, /docs/functions/runtimes/node-js.

- Funciones: duración máxima 300 s; 2 GB y 1 vCPU; 1.000.000 invocaciones al mes; 4 horas de CPU activa y 360 GB-h de memoria aprovisionada al mes.
- Transferencia: 100 GB Fast Data Transfer, 10 GB Fast Origin Transfer, 1.000.000 solicitudes de CDN al mes.
- Cron: hasta 100 por proyecto, como máximo una vez al día, precisión de una hora.
- Región: una sola región en Hobby (por defecto iad1), configurable con `regions` en `vercel.json`. Se configuró `gru1` (São Paulo); confirmar en el primer despliegue.
- Al superar límites: no se cobra; la función queda bloqueada hasta cumplir 30 días.
- Fastify sin configuración: detecta `src/server.ts` y ejecuta `listen`. Node 22.x, 24.x y 26.x disponibles (26 por defecto); `engines.node` fija la versión.
- No se encontró en estas páginas la condición de uso no comercial ni si se pide tarjeta; se mantiene lo de `02b` y se confirma al crear la cuenta.

## Neon (plan gratuito)

Fuentes: neon.com/faqs/free-plan-limits-and-quotas y neon.com/pricing.

- Sin tarjeta; plan permanente. 100 proyectos, 10 ramas por proyecto.
- 100 CU-hora por proyecto al mes; 1 GB por proyecto (20 GB en total); 5 GB de transferencia pública al mes.
- Escala a cero tras 5 minutos sin actividad (no desactivable). Restauración instantánea de 6 horas, 1 instantánea manual.
- No pausa ni borra proyectos por inactividad (corrige el riesgo de borrado anotado en `02b`). Si se agota el cómputo, se suspende hasta el mes siguiente sin perder datos.
- Región aws-sa-east-1 en el plan gratuito: no confirmada en estas páginas; se confirma al crear el proyecto.

## Gmail SMTP (cuenta personal)

Fuentes: support.google.com/mail/answer/22839 y support.google.com/accounts/answer/185833.

- Límite: más de 500 destinatarios en un correo o más de 500 correos al día provoca bloqueo de 1 a 24 horas. Google puede cambiarlo sin aviso.
- Contraseña de aplicación: exige verificación en dos pasos; no disponible si la verificación usa solo llaves de seguridad, en cuentas de organización o con Protección Avanzada. Se revoca al cambiar la contraseña de la cuenta.

## Versiones elegidas (fuentes oficiales)

| Pieza | Versión | Fuente |
|---|---|---|
| Android Gradle Plugin | 9.5.1 (exige Gradle 9.6.0+, JDK 17, build-tools 36.0.0, API máxima 37) | developer.android.com/build/releases |
| Gradle | 9.9.2 | gradle.org/releases |
| Kotlin (Compose compiler) | 2.4.21 | kotlinlang.org/docs/releases.html |
| KSP | 2.3.14 | github.com/google/ksp/releases |
| Compose BOM | 2026.11.01 (Compose 1.13.0, Material 3 1.5.0) | developer.android.com/develop/ui/compose/bom/bom-mapping |
| activity-compose | 1.13.0 | developer.android.com/jetpack/androidx/releases/activity |
| Navigation 3 | 1.2.2 | developer.android.com/jetpack/androidx/releases/navigation3 |
| Room 3 | 3.0.3 (`androidx.room3`) | developer.android.com/jetpack/androidx/releases/room3 |
| compileSdk / targetSdk | 37.1 / 37 (Android 17) | developer.android.com/about/versions |
| Detekt | 2.0.0-alpha.6 (la 1.23.8 estable no soporta Kotlin 2.4) | github.com/detekt/detekt/releases |
| ktlint-gradle / ktlint | 14.2.0 / 1.8.0 | GitHub de cada proyecto |
| Android command-line tools | 15859902 (SHA-256 comprobado) | developer.android.com/studio |
| Node | 22.x local y en Vercel (LTS vigentes: 22, 24 y 26) | nodejs.org/en/about/previous-releases |
| Fastify / pg | 5.12.8 / 8.23.1 | registry.npmjs.org |
| TypeScript | 6.0.3 (la 7.0.2 no es compatible con typescript-eslint 8.73) | registry.npmjs.org |
| ESLint / typescript-eslint / Prettier / Vitest | 10.15.0 / 8.73.0 / 3.10.1 / 5.1.1 | registry.npmjs.org |
| PostgreSQL local | 17 (imagen `postgres:17-alpine`) | Igualar con la versión de Neon al crear el proyecto |
