# 08b - Base de datos: tablas

Complemento de `08-DATABASE.md`. Definición de tablas y atributos. Convenciones y enumeraciones en el archivo principal.

## Tablas: identidad y seguridad

### users
`id`, `email` (citext único), `email_verified_at`, `password_hash`, `display_name`, `locale`, `primary_goal`, `height_cm`, `birth_year`, `created_at`, `updated_at`, `deleted_at`.

### user_preferences
`user_id` (PK y FK), `weight_unit`, `distance_unit`, `theme`, `locale`, `week_start_day`, `default_rest_seconds`, `timer_sound`, `timer_vibration`, `notify_rest_end`, `notify_reminders`, `e1rm_formula`, `weight_step_kg`, `updated_at`. Sincronizable.

### devices
`id`, `user_id`, `installation_id` (UUID generado por la app al instalarse; único por usuario), `name`, `platform`, `app_version`, `first_seen_at`, `last_seen_at`, `revoked_at`.

### refresh_tokens
`id`, `user_id`, `device_id`, `family_id`, `token_hash` (único), `expires_at`, `rotated_at`, `revoked_at`, `created_at`. Índices por `user_id` y `family_id`.

### verification_codes
`id`, `email`, `purpose` (`code_purpose`), `code_hash`, `attempts`, `expires_at`, `consumed_at`, `created_at`, `ip_hash`. Índice por (`email`, `purpose`, `created_at`).

### consents
`id`, `user_id`, `document` (privacy, terms, age: confirmación de 18 años o más), `version`, `accepted_at`.

### email_outbox
`id`, `user_id` (nulo si aún no existe), `to_email`, `template`, `locale`, `payload` (jsonb sin datos sensibles innecesarios), `priority`, `status`, `attempts`, `last_error`, `created_at`, `sent_at`. Índice por (`status`, `priority`, `created_at`).

### audit_log
`id`, `user_id`, `event` (login, new_device, password_change, account_delete, etc.), `device_id`, `ip_hash`, `created_at`. Sin contenido sensible.

### rate_limit_events
`id`, `action` (login, register, resend_code, forgot_password, etc.), `key_hash` (HMAC de IP o correo), `created_at`. Estado compartido de los límites de tasa, porque las instancias serverless no comparten memoria. Se purga por tarea programada.

## Tablas: catálogo (solo lectura para clientes)

### disciplines
`id` (smallint), `slug` (único), `sort_order`, `icon_key`.
### discipline_translations
`discipline_id`, `locale`, `name`, `description`. PK (`discipline_id`, `locale`).

### muscle_groups
`id`, `slug` (único), `body_region`, `view` (front, back), `mesh_key` (nombre de la pieza en el modelo 3D), `svg_key` (zona en el mapa 2D), `sort_order`.
### muscle_group_translations
`muscle_group_id`, `locale`, `name`.

### equipment
`id`, `slug` (único).
### equipment_translations
`equipment_id`, `locale`, `name`.

### exercises
`id`, `slug` (único entre los de catálogo), `owner_user_id` (nulo para catálogo; con valor para ejercicios propios), `discipline_id`, `metric_type`, `movement_pattern`, `difficulty` (1 a 5), `is_unilateral`, `uses_bodyweight`, `default_rest_seconds`, `catalog_version`, `created_at`, `updated_at`, `deleted_at`, `server_seq`.
Los ejercicios propios del usuario se sincronizan; los del catálogo no.

### exercise_translations
`exercise_id`, `locale`, `name`, `summary`, `setup`, `execution`, `cues` (arreglo de texto), `common_mistakes` (arreglo), `safety_notes`. PK (`exercise_id`, `locale`). Índice de búsqueda por nombre (por ejemplo trigramas, VERIFICAR extensión disponible en Neon).

### exercise_muscles
`exercise_id`, `muscle_group_id`, `role`, `contribution` (numeric; por defecto 1,0 para principal y 0,5 para secundario). Cómo contar series por músculo.

### exercise_equipment
`exercise_id`, `equipment_id`.

### exercise_relations
`exercise_id`, `related_exercise_id`, `relation_type`.

## Tablas: planificación del usuario (sincronizables)

### routines
`id`, `user_id`, `name`, `notes`, `color_key`, y campos de sincronización.

### routine_exercises
`id`, `routine_id`, `user_id`, `exercise_id`, `position`, `group_id` (superserie o circuito), `target_sets`, `target_reps_min`, `target_reps_max`, `target_load_kg`, `target_rpe`, `target_rir`, `target_duration_s`, `target_distance_m`, `rest_seconds`, `notes`.

### programs
`id`, `user_id`, `name`, `start_date`, `weeks_count`, `is_active`, `notes`.

### program_days
`id`, `program_id`, `user_id`, `week_index`, `day_index`, `routine_id` (nulo para descanso), `is_rest`.

### scheduled_workouts
`id`, `user_id`, `date`, `routine_id`, `program_day_id`, `status` (planned, done, skipped), `session_id` (nulo hasta completarse).

## Tablas: registro (sincronizables)

### workout_sessions
`id`, `user_id`, `routine_id`, `scheduled_workout_id`, `title`, `started_at`, `ended_at`, `notes`, `session_rpe`, `bodyweight_kg`, `avg_hr`, `max_hr`, `device_id`, `source`.

### session_exercises
`id`, `session_id`, `user_id`, `exercise_id`, `position`, `group_id`, `notes`.

### sets
`id`, `session_exercise_id`, `user_id`, `exercise_id` (desnormalizado), `performed_at` (desnormalizado), `set_index`, `set_type`, `reps`, `load_kg`, `added_load_kg`, `duration_s`, `distance_m`, `rpe`, `rir`, `rest_s`, `height_cm` (pliometría), `pool_length_m`, `laps`, `stroke`, `completed_at`, `notes`.
La desnormalización de `user_id`, `exercise_id` y `performed_at` evita uniones en las consultas de progreso.

### cardio_tracks
`id`, `session_exercise_id`, `user_id`, `distance_m`, `duration_s`, `moving_duration_s`, `avg_pace_s_per_km`, `elevation_gain_m`, `avg_hr`, `max_hr`, `polyline` (texto codificado y simplificado), `points_count`, `source`.

### body_measurements
`id`, `user_id`, `measured_at`, `weight_kg`, `body_fat_pct`, `notes`.

### daily_activity
`id`, `user_id`, `date`, `steps`, `active_minutes`, `source`. Un registro por usuario y día (único).

## Tablas derivadas

### personal_records
`id`, `user_id`, `exercise_id`, `record_type` (max_weight, e1rm, max_reps, best_pace_at_distance, longest_duration), `value`, `context` (por ejemplo la distancia), `achieved_at`, `set_id`. Se recalcula al sincronizar sets. Puede vivir solo en el cliente; en el servidor es opcional y reconstruible.

### muscle_volume_daily
`user_id`, `date`, `muscle_group_id`, `sets_equivalent` (numeric), `updated_at`. Clave (`user_id`, `date`, `muscle_group_id`). Mantiene el mapa de calor rápido: semana, mes y año se obtienen sumando por rango. Reconstruible desde `sets`, `exercise_muscles` y `contribution`.
