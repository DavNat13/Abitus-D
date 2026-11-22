-- 003 - Users and their preferences (08b-DATABASE-TABLAS.md). Units: SI (kg, m, s).
-- Reversal: drop table user_preferences; drop table users;
create table users (
  id uuid primary key,
  email citext not null unique check (char_length(email) <= 254),
  email_verified_at timestamptz,
  password_hash text not null,
  display_name text check (char_length(display_name) between 1 and 60),
  locale text not null default 'es' check (locale ~ '^[a-z]{2}(-[A-Z]{2})?$'),
  primary_goal text check (char_length(primary_goal) <= 40),
  height_cm numeric(5, 1) check (height_cm between 50 and 272),
  birth_year smallint check (birth_year between 1900 and 2100),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  deleted_at timestamptz
);

create table user_preferences (
  user_id uuid primary key references users (id) on delete cascade,
  weight_unit text not null default 'kg' check (weight_unit in ('kg', 'lb')),
  distance_unit text not null default 'km' check (distance_unit in ('km', 'mi')),
  theme text not null default 'light' check (theme in ('light', 'dark', 'system')),
  locale text not null default 'es' check (locale ~ '^[a-z]{2}(-[A-Z]{2})?$'),
  week_start_day smallint not null default 1 check (week_start_day between 1 and 7),
  default_rest_seconds integer not null default 90 check (default_rest_seconds between 0 and 3600),
  timer_sound boolean not null default true,
  timer_vibration boolean not null default true,
  notify_rest_end boolean not null default true,
  notify_reminders boolean not null default false,
  e1rm_formula text not null default 'epley' check (e1rm_formula in ('epley', 'brzycki')),
  weight_step_kg numeric(5, 2) not null default 2.5 check (weight_step_kg > 0 and weight_step_kg <= 50),
  updated_at timestamptz not null default now()
);
