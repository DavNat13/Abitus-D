-- Athlete onboarding and editable profile. SI units; one deterministic row per account.
-- Reversal: drop athlete_profiles (including its trigger and indexes).
create table athlete_profiles (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  birth_year smallint check (birth_year between 1900 and 2100),
  sex text check (sex in ('female', 'male', 'undisclosed')),
  height_m numeric(4, 2) check (height_m between 0.5 and 2.8),
  weight_kg numeric(5, 2) check (weight_kg between 20 and 500),
  training_years smallint check (training_years between 0 and 100),
  level text check (level in ('beginner', 'intermediate', 'advanced')),
  disciplines text not null default '' check (char_length(disciplines) <= 200),
  goal text check (goal in ('general', 'strength', 'hypertrophy', 'endurance', 'skill')),
  secondary_goal text check (secondary_goal in ('general', 'strength', 'hypertrophy', 'endurance', 'skill')),
  days_per_week smallint check (days_per_week between 1 and 7),
  session_duration_s integer check (session_duration_s between 300 and 14400),
  place text check (place in ('gym', 'home', 'park', 'other')),
  equipment text not null default '' check (char_length(equipment) <= 500),
  health_consent boolean not null default false,
  health_limited boolean,
  injuries text not null default '' check (char_length(injuries) <= 1000),
  push_ups smallint check (push_ups between 0 and 1000),
  pull_ups smallint check (pull_ups between 0 and 1000),
  plank_duration_s integer check (plank_duration_s between 0 and 86400),
  onboarding_done boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq'),
  check (id = user_id),
  check (health_consent or (health_limited is null and injuries = ''))
);
create index athlete_profiles_user_seq_idx on athlete_profiles (user_id, server_seq);
create trigger athlete_profiles_bump_server_seq before update on athlete_profiles
  for each row execute function bump_server_seq();
