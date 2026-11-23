-- 013 - Workout logging (08b, 10f Fase 4): sessions, their exercises and sets, all synced
-- like the planning tables (client UUIDs, user_id, soft deletes, server_seq, no foreign keys
-- between synced rows). Also adds active rest days to program_days.
-- Cardio, swimming and plyometric columns of 08b arrive with Phase 7.
-- Reversal: drop the three tables and their triggers; drop program_days.active_rest.
create table workout_sessions (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  routine_id uuid,
  scheduled_workout_id uuid,
  title text not null check (char_length(title) between 1 and 80),
  started_at timestamptz not null,
  ended_at timestamptz,
  notes text not null default '' check (char_length(notes) <= 2000),
  session_rpe numeric(3, 1) check (session_rpe between 1 and 10),
  sleep_quality smallint check (sleep_quality between 1 and 5),
  energy smallint check (energy between 1 and 5),
  bodyweight_kg numeric(5, 2) check (bodyweight_kg between 20 and 400),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq'),
  check (ended_at is null or ended_at >= started_at)
);

create table session_exercises (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  session_id uuid not null,
  exercise_id uuid not null,
  position smallint not null check (position between 0 and 200),
  group_id text not null default '' check (char_length(group_id) <= 40),
  notes text not null default '' check (char_length(notes) <= 500),
  discomfort text not null default '' check (char_length(discomfort) <= 40),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create table sets (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  session_exercise_id uuid not null,
  exercise_id uuid not null,
  performed_at timestamptz not null,
  set_index smallint not null check (set_index between 0 and 100),
  set_type text not null default 'normal'
    check (set_type in ('normal', 'warmup', 'drop', 'amrap', 'failure', 'rest_pause')),
  side text not null default 'both' check (side in ('both', 'left', 'right')),
  reps smallint check (reps between 0 and 1000),
  load_kg numeric(7, 2) check (load_kg between 0 and 2000),
  added_load_kg numeric(6, 2) check (added_load_kg between -500 and 500),
  duration_s integer check (duration_s between 0 and 86400),
  distance_m numeric(10, 2) check (distance_m between 0 and 1000000),
  rpe numeric(3, 1) check (rpe between 1 and 10),
  rir smallint check (rir between 0 and 10),
  rest_s integer check (rest_s between 0 and 3600),
  tempo text not null default '' check (char_length(tempo) <= 12),
  completed_at timestamptz,
  notes text not null default '' check (char_length(notes) <= 500),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

alter table program_days add column active_rest boolean not null default false;

create index workout_sessions_user_seq_idx on workout_sessions (user_id, server_seq);
create index workout_sessions_user_started_idx on workout_sessions (user_id, started_at desc);
create index session_exercises_user_seq_idx on session_exercises (user_id, server_seq);
create index session_exercises_session_idx on session_exercises (session_id, position);
create index sets_user_seq_idx on sets (user_id, server_seq);
create index sets_user_exercise_idx on sets (user_id, exercise_id, performed_at desc);

create trigger workout_sessions_bump_server_seq before update on workout_sessions
  for each row execute function bump_server_seq();
create trigger session_exercises_bump_server_seq before update on session_exercises
  for each row execute function bump_server_seq();
create trigger sets_bump_server_seq before update on sets
  for each row execute function bump_server_seq();
