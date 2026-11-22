# 14 - Infraestructura y despliegue

Principio: costo cero ABSOLUTO y portabilidad. Regla del usuario: no se registra tarjeta de crédito ni débito en ningún servicio y no se compra dominio. Todo servicio debe permitir el alta sin tarjeta. Los límites gratuitos cambian: VERIFICAR antes de depender de ellos (valores de 2026-11-22 en `02-MEMORIA.md`).

## Arquitectura de despliegue

```
Dispositivos Android --HTTPS--> Vercel Hobby (Fastify, Node) --SQL (pooler)--> Neon (São Paulo)
                                       |
                                       +--SMTP--> Gmail (app.abitus.d@gmail.com)
Planificador gratuito --HTTPS--> /internal/jobs/*
```

La URL base es la que asigna Vercel (`*.vercel.app`); no hay dominio propio. La app la recibe por configuración de build.

## Backend en Vercel Hobby

- Plan Hobby sin tarjeta. Condición de uso: solo no comercial. El proyecto es personal y gratuito; si algún día se monetiza, se debe migrar (ver plan de salida).
- Fastify se despliega como función de Node (Fluid compute); sin procesos persistentes ni tareas en segundo plano dentro del proceso.
- Región de la función: la más cercana a Neon São Paulo (VERIFICAR en el panel; por defecto suele ser Washington D.C. y es configurable en el proyecto).
- Hash de contraseñas con binario precompilado (`@node-rs/argon2`) para evitar compilar en el despliegue. Parámetros Argon2id en `07-BACKEND.md`; medir el tiempo de CPU en la Fase 10.
- Límites del plan: tiempo máximo por invocación, memoria y número de invocaciones mensuales (VERIFICAR). Si el cómputo mensual se acerca al tope, se reduce la frecuencia de sincronización.
- Arranque en frío esperado de pocos cientos de milisegundos a pocos segundos; la app es offline-first y solo lo nota en registro y login.
- Salud: `/health` y `/ready`.
- El código no depende de Vercel: servidor Fastify estándar (`backend/src/server.ts`) que Vercel detecta sin configuración, sin adaptador propio (decisión 2026-11-22 en `02`). Un `Dockerfile` se mantiene para migrar.

## Base de datos en Neon

- PostgreSQL estándar, región São Paulo (aws-sa-east-1), la más cercana a Chile entre las gratuitas sin tarjeta.
- Conexión por pooler (pgbouncer) y SSL obligatorio; sin pool grande en serverless.
- Riesgos del plan gratuito (VERIFICAR): tope mensual de horas de cómputo, apagado por inactividad con latencia en la primera consulta y eliminación de proyectos tras largo tiempo sin actividad.
- Mitigaciones: sincronización por lotes (no por evento), catálogo de ejercicios en caché en el dispositivo, sin consultas periódicas innecesarias, GitHub Actions o planificador que mantenga actividad mínima solo si el límite lo permite.
- Alternativa si Neon falla: Supabase gratuito (PostgreSQL estándar, sin tarjeta). Migración por volcado y restauración.
- Alerta al 70 por ciento del almacenamiento (presupuesto en `08-DATABASE.md`).
- Migraciones aplicadas desde `database/`, nunca por la aplicación en producción sin control.

## Correo

- SMTP configurable por variables; proveedor actual Gmail con contraseña de aplicación (`15-CORREOS.md`). Sin dominio, sin SPF/DKIM propios.

## Secretos y configuración

- Variables de entorno del proyecto en Vercel. Nunca en el repositorio ni en el chat.
- `.env.example` documenta las variables sin valores.
- Rotación de secretos sin cambiar código; códigos y tokens con "peppers" independientes.

## Entornos

| Entorno | Backend | Base de datos | Correo |
|---|---|---|---|
| Local | Node en la máquina o Docker | PostgreSQL en Docker Compose | Servidor de pruebas local (Mailpit) |
| Producción | Vercel Hobby | Neon São Paulo | Gmail SMTP |

No hay entorno de preparación en la nube mientras no exista una opción gratuita que no complique los límites.

## Tareas programadas (decisión cerrada)

Cloud Scheduler queda descartado: depende de Google Cloud, que pide tarjeta. Vercel Hobby solo permite un cron diario.

- Tareas: reintento de correos, limpieza de códigos y tokens vencidos, purga de tombstones, resumen semanal.
- Se implementan como `/internal/jobs/{name}` protegidos con un secreto de trabajo.
- Disparo principal: flujo programado de GitHub Actions (gratuito en repositorio público; puede retrasarse minutos) que llama al endpoint. Respaldo: cron diario de Vercel y limpieza oportunista al atender solicitudes.
- Un servicio de cron externo gratuito sin tarjeta es opcional, solo si hace falta más frecuencia.
- Ninguna tarea es crítica para la seguridad: un retraso nunca debe dejar válido un código o token vencido (la validación comprueba la fecha siempre).

## Respaldos (decisión cerrada)

- Exportación manual semanal: un script `tools/export-db` genera un volcado cifrado que el usuario guarda fuera del proveedor.
- Respaldo propio de la app: el usuario puede exportar sus datos desde la aplicación (ver `13-SEGURIDAD-Y-LEGAL.md`).
- Neon gratuito ofrece una ventana corta de restauración (VERIFICAR); no se confía solo en ella.
- Probar la restauración antes de considerar válido un respaldo.

## Observabilidad con costo cero

- Registros de Vercel con redacción de datos sensibles (nunca contraseñas, códigos, tokens ni correos completos).
- Métricas básicas: errores, latencia, tamaño de la base, correos enviados por día.
- Un monitor externo gratuito sobre `/health` es opcional; no se usa para mantener nada despierto.

## Plan de salida (portabilidad)

1. El mismo código corre en cualquier host Node o en Docker.
2. La base se migra con volcado y restauración de PostgreSQL.
3. El SMTP se cambia por variables de entorno.
4. La app solo necesita la nueva URL base (configurable por build).
