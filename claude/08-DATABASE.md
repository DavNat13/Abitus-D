# 08 - Base de datos

PostgreSQL. Carpeta: `database/`. Migraciones SQL versionadas e inmutables una vez aplicadas; datos semilla en archivos propios; consultas críticas documentadas.

Los detalles están repartidos: tablas en `08b-DATABASE-TABLAS.md` y datos semilla en `08c-DATABASE-SEMILLAS.md`.

## Convenciones

- Nombres en inglés, `snake_case`, tablas en plural.
- Claves primarias UUID (v7 generado por el cliente para datos del usuario; el servidor genera los del catálogo).
- Marcas de tiempo con zona horaria (`timestamptz`), en UTC.
- Unidades internas: kilogramos (`numeric(7,2)`), metros (`numeric(10,2)`), segundos (`integer`). Por defecto se muestran kg y km; el usuario elige otras unidades en ajustes (`09-FRONTEND-ANDROID.md`).
- Correo con tipo `citext` (sin distinción de mayúsculas) y único.
- Tablas sincronizables: `user_id`, `created_at`, `updated_at`, `deleted_at` (borrado lógico) y `server_seq` (de una secuencia global).
- Restricciones `CHECK` para rangos (por ejemplo RPE entre 1 y 10, repeticiones no negativas) y claves foráneas con `ON DELETE` explícito.
- Datos del catálogo: solo lectura para clientes.

## Enumeraciones

- `metric_type`: `weight_reps`, `bodyweight_reps`, `weighted_bodyweight_reps`, `duration`, `distance_duration`, `swim_laps`, `plyo`.
- `movement_pattern`: `push`, `pull`, `squat`, `hinge`, `lunge`, `carry`, `core`, `jump`, `locomotion`, `swim`, `mobility`.
- `set_type`: `warmup`, `normal`, `drop`, `failure`, `amrap`.
- `muscle_role`: `primary`, `secondary`.
- `relation_type`: `progression`, `regression`, `alternative`.
- `track_source`: `gps`, `health_connect`, `manual`.
- `code_purpose`: `verify_email`, `reset_password`.
- `email_status`: `pending`, `sent`, `failed`.

## Índices principales

- `workout_sessions (user_id, started_at desc)`.
- `session_exercises (session_id, position)` y `(user_id, exercise_id)`.
- `sets (session_exercise_id, set_index)` y `(user_id, exercise_id, performed_at desc)`.
- `scheduled_workouts (user_id, date)`.
- `routine_exercises (routine_id, position)`.
- `exercises (discipline_id)`, `exercise_muscles (muscle_group_id)`.
- `muscle_volume_daily (user_id, date)`.
- Por cada tabla sincronizable: `(user_id, server_seq)` para el pull incremental.
- Parciales sobre `deleted_at is null` donde convenga.

## Consultas críticas (optimizar y probar con EXPLAIN)

1. Última vez de un ejercicio: último conjunto de series de `sets` por (`user_id`, `exercise_id`) ordenado por `performed_at`.
2. Evolución de un ejercicio: mejor e1RM o mayor carga por sesión en un rango de fechas.
3. Volumen por músculo en semana, mes y año: suma de `muscle_volume_daily` por rango.
4. PRs por ejercicio: máximos por tipo de registro.
5. Calendario del mes: sesiones por día y entrenamientos programados.
6. Pull de sincronización: filas del usuario con `server_seq` mayor al cursor, ordenadas, con límite.
7. Búsqueda en biblioteca: por nombre traducido, disciplina, músculo y equipamiento.
8. Ritmo y distancia de running y natación por periodo.

En la app estas consultas se ejecutan sobre Room con los mismos criterios; el servidor las usa para exportación y para pruebas de coherencia.

## Migraciones

- Una migración por cambio, numerada y fechada; nunca editar una ya aplicada.
- Cada migración debe ser reversible o documentar por qué no.
- Probar migraciones en una base limpia y sobre una base con datos de prueba.
- Las semillas se aplican después de las migraciones, por separado.

## Respaldo y retención

- Respaldo de Neon según el plan gratuito (VERIFICAR ventana de restauración) más una exportación manual semanal propia (ver `14-INFRAESTRUCTURA.md`).
- Tombstones: 90 días como mínimo.
- Eliminación de cuenta: borra en cascada todos los datos del usuario.

## Presupuesto de tamaño (plan gratuito de 1 GB)

- Las rutas GPS se guardan simplificadas y codificadas; los puntos crudos se quedan solo en el dispositivo.
- No almacenar series de frecuencia cardíaca crudas; solo promedios y máximos.
- Medir tamaño por usuario en las pruebas de carga y proyectar el crecimiento. Alertar si se supera el 70 por ciento del límite.
