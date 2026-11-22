# 02b - Memoria: decisiones cerradas el 2026-11-22 (ronda 2)

Complemento de `02-MEMORIA.md`. Estas decisiones salieron de `12-DECISIONES-PENDIENTES.md`.

| Tema | Decisión | Motivo |
|---|---|---|
| Nombre | Abitus-D (la D es de deporte); carpeta `C:\Abitus-D` | Decisión del autor |
| applicationId | `com.abitusd.app` | No cambia después de distribuir |
| Tarjetas | Prohibidas en cualquier servicio | Evitar cargos inesperados |
| Dominio | No se compra; solo servicios gratuitos | Costo cero |
| Hosting | Vercel Hobby (uso no comercial) + Neon São Paulo; alternativa Supabase | Sin tarjeta; Cloud Run descartado |
| Región | La más cercana o la que mejor funcione: São Paulo | Menor latencia disponible sin tarjeta |
| Correo | Gmail SMTP con app.abitus.d@gmail.com y contraseña de aplicación | Sin dominio |
| Plataforma | Solo Android | Foco |
| Edad mínima | 18 años | Decisión del autor |
| Contribución muscular | 1,0 principal y 0,5 secundario, configurable | Decisión del autor |
| Umbrales de veredicto | +/-1 por ciento en ventana de 6 semanas, ajustables | Decisión del autor |
| Unidades | Por defecto kg y km; opciones lb, millas, metros, yardas, pies y pulgadas | Decisión del autor |
| Marco legal | Buenas prácticas (Chile y datos de salud); revisión profesional si se comparte con terceros | Costo cero ahora, prudencia después |
| Respaldos | Exportación manual semanal con `tools/export-db` | Sin automatización de pago |
| Programador de tareas | GitHub Actions como principal; cron diario de Vercel y limpieza oportunista como respaldo. Cloud Scheduler descartado | Sin tarjeta |
| Reloj | Apartado propio de reloj y timers; banco de ideas ampliado en `11b` a `11e`; nada de pagos ni publicidad | Decisión del autor |
| Fases | 0 a 10 hasta la versión 1.0 (Reloj es la Fase 5); mejoras 11 a 13 después | Ideas asignadas por fase en `10b` a `10e` |
| Ejercicios | free-exercise-db como base más textos propios; wger solo con atribución (`08d`) | Licencias |

## Descartado en esta ronda

| Opción | Motivo |
|---|---|
| Google Cloud Run y Cloud Scheduler | Exigen tarjeta |
| Cloudflare Workers | Límite de CPU incompatible con Argon2id |
| Render | Apagado tras 15 minutos y reactivación lenta |
| Brevo | Exige dominio autenticado |
| PWA e iPhone | Fuera de alcance |

## Hechos verificados (2026-11-22)

- Vercel Hobby: sin tarjeta, solo uso no comercial; las tareas cron del plan Hobby corren como máximo una vez al día.
- Neon gratuito en São Paulo sin tarjeta; riesgo de tope de horas de cómputo y de borrado tras mucha inactividad.
- Gmail SMTP: requiere verificación en dos pasos y contraseña de aplicación; remitente debe ser la cuenta.
- Verificación de desarrolladores de Android: obligatoria desde septiembre de 2026 en Brasil, Indonesia, Singapur y Tailandia; global desde 2027 en adelante; hay cuentas gratuitas de distribución limitada y un flujo avanzado para instalar sin verificar. Chile no figura en la lista inicial; la suposición del autor no está confirmada. Reverificar en Fase 0.
- Versiones de librerías: ver `06b-LIBRERIAS-VERSIONES.md`.
