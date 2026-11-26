-- Exercise favorites and saved timer profiles (phase 5). Durations in seconds.
-- Reversal: drop exercise_favorites and timer_profiles (including triggers and indexes).
create table exercise_favorites (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  exercise_id uuid not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);
create index exercise_favorites_user_seq_idx on exercise_favorites (user_id, server_seq);
create trigger exercise_favorites_bump_server_seq before update on exercise_favorites
  for each row execute function bump_server_seq();

create table timer_profiles (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  name text not null check (char_length(name) between 1 and 60),
  mode text not null check (mode in ('stopwatch', 'countdown', 'intervals', 'tabata', 'emom', 'amrap', 'for_time', 'custom')),
  spec text not null check (char_length(spec) <= 4000),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);
create index timer_profiles_user_seq_idx on timer_profiles (user_id, server_seq);
create trigger timer_profiles_bump_server_seq before update on timer_profiles
  for each row execute function bump_server_seq();
