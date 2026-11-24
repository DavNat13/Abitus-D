-- 014 - Advanced planning (10f Fase 4b): program blocks (mesocycles with phase and
-- progression), exercise substitutions per block, weekly set targets per muscle and the
-- planned time of a scheduled workout. Synced like the other planning tables.
-- Reversal: drop the three tables and their triggers; drop scheduled_workouts.planned_time.
create table program_blocks (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  program_id uuid not null,
  position smallint not null check (position between 0 and 20),
  name text not null default '' check (char_length(name) <= 60),
  goal text not null default 'general'
    check (goal in ('general', 'strength', 'hypertrophy', 'endurance', 'skill')),
  phase text not null default 'accumulation'
    check (phase in ('accumulation', 'intensification', 'deload', 'test')),
  weeks smallint not null check (weeks between 1 and 8),
  progression text not null default 'none' check (progression in ('none', 'double', 'percent', 'rpe')),
  step numeric(5, 2) check (step between 0 and 50),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create table block_substitutions (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  block_id uuid not null,
  from_exercise_id uuid not null,
  to_exercise_id uuid not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);

create table muscle_targets (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  muscle_group_id smallint not null,
  min_sets smallint not null check (min_sets between 0 and 60),
  max_sets smallint not null check (max_sets between 0 and 60),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq'),
  check (min_sets <= max_sets)
);

alter table scheduled_workouts
  add column planned_time text check (planned_time ~ '^([01][0-9]|2[0-3]):[0-5][0-9]$');

create index program_blocks_user_seq_idx on program_blocks (user_id, server_seq);
create index block_substitutions_user_seq_idx on block_substitutions (user_id, server_seq);
create index muscle_targets_user_seq_idx on muscle_targets (user_id, server_seq);

create trigger program_blocks_bump_server_seq before update on program_blocks
  for each row execute function bump_server_seq();
create trigger block_substitutions_bump_server_seq before update on block_substitutions
  for each row execute function bump_server_seq();
create trigger muscle_targets_bump_server_seq before update on muscle_targets
  for each row execute function bump_server_seq();
