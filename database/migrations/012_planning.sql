-- 012 - Planning tables (08b): routines, planned exercises, programs, days and scheduled
-- workouts. All are synced: client UUIDs, user_id, soft deletes and server_seq.
-- References between synced rows are not foreign keys: rows arrive in any order in a batch.
-- Reversal: drop the five tables and their triggers.
create table routines (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  name text not null check (char_length(name) between 1 and 80),
  notes text not null default '' check (char_length(notes) <= 1000),
  folder text not null default '' check (char_length(folder) <= 60),
  color_key text not null default '' check (char_length(color_key) <= 20),
  archived boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create table routine_exercises (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  routine_id uuid not null,
  exercise_id uuid not null,
  position smallint not null check (position between 0 and 200),
  group_id text not null default '' check (char_length(group_id) <= 40),
  target_sets smallint check (target_sets between 1 and 50),
  target_reps_min smallint check (target_reps_min between 0 and 1000),
  target_reps_max smallint check (target_reps_max between 0 and 1000),
  target_load_kg numeric(7, 2) check (target_load_kg between 0 and 2000),
  target_rpe numeric(3, 1) check (target_rpe between 1 and 10),
  target_rir smallint check (target_rir between 0 and 10),
  target_duration_s integer check (target_duration_s between 0 and 86400),
  target_distance_m numeric(10, 2) check (target_distance_m between 0 and 1000000),
  rest_seconds integer check (rest_seconds between 0 and 3600),
  notes text not null default '' check (char_length(notes) <= 500),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create table programs (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  name text not null check (char_length(name) between 1 and 80),
  start_date date,
  weeks_count smallint not null check (weeks_count between 1 and 52),
  is_active boolean not null default false,
  notes text not null default '' check (char_length(notes) <= 1000),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create table program_days (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  program_id uuid not null,
  week_index smallint not null check (week_index between 0 and 51),
  day_index smallint not null check (day_index between 0 and 6),
  routine_id uuid,
  is_rest boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create table scheduled_workouts (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  date date not null,
  routine_id uuid,
  program_day_id uuid,
  status text not null default 'planned' check (status in ('planned', 'done', 'skipped')),
  session_id uuid,
  rescheduled_from date,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create index routines_user_seq_idx on routines (user_id, server_seq);
create index routine_exercises_user_seq_idx on routine_exercises (user_id, server_seq);
create index routine_exercises_routine_idx on routine_exercises (routine_id, position);
create index programs_user_seq_idx on programs (user_id, server_seq);
create index program_days_user_seq_idx on program_days (user_id, server_seq);
create index scheduled_workouts_user_seq_idx on scheduled_workouts (user_id, server_seq);
create index scheduled_workouts_user_date_idx on scheduled_workouts (user_id, date);

create trigger routines_bump_server_seq before update on routines
  for each row execute function bump_server_seq();
create trigger routine_exercises_bump_server_seq before update on routine_exercises
  for each row execute function bump_server_seq();
create trigger programs_bump_server_seq before update on programs
  for each row execute function bump_server_seq();
create trigger program_days_bump_server_seq before update on program_days
  for each row execute function bump_server_seq();
create trigger scheduled_workouts_bump_server_seq before update on scheduled_workouts
  for each row execute function bump_server_seq();
