-- 009 - Exercises of the catalog and exercises owned by users (08b-DATABASE-TABLAS.md).
-- Catalog rows: owner_user_id null, slug unique, source and license required.
-- User rows: owner_user_id set, synced with server_seq and soft deletes.
-- Reversal: drop the five tables in reverse order.
create table exercises (
  id uuid primary key,
  slug text check (slug ~ '^[a-z0-9_]{2,80}$'),
  owner_user_id uuid references users (id) on delete cascade,
  discipline_id smallint not null references disciplines (id),
  metric_type metric_type not null,
  movement_pattern movement_pattern not null,
  difficulty smallint not null check (difficulty between 1 and 5),
  is_unilateral boolean not null default false,
  uses_bodyweight boolean not null default false,
  default_rest_seconds integer not null default 90 check (default_rest_seconds between 0 and 1800),
  source text check (char_length(source) <= 200),
  license text check (char_length(license) <= 60),
  catalog_version integer not null default 0,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq'),
  check (
    (owner_user_id is null and slug is not null and source is not null and license is not null)
    or owner_user_id is not null
  )
);

create unique index exercises_catalog_slug_idx on exercises (slug) where owner_user_id is null;
create index exercises_discipline_idx on exercises (discipline_id);
create index exercises_catalog_version_idx on exercises (catalog_version) where owner_user_id is null;
create index exercises_owner_seq_idx on exercises (owner_user_id, server_seq) where owner_user_id is not null;

create table exercise_translations (
  exercise_id uuid not null references exercises (id) on delete cascade,
  locale text not null check (locale ~ '^[a-z]{2}$'),
  name text not null check (char_length(name) between 1 and 120),
  summary text not null default '' check (char_length(summary) <= 500),
  setup text not null default '' check (char_length(setup) <= 1000),
  execution text[] not null default '{}',
  cues text[] not null default '{}',
  common_mistakes text[] not null default '{}',
  safety_notes text not null default '' check (char_length(safety_notes) <= 1000),
  primary key (exercise_id, locale)
);

create table exercise_muscles (
  exercise_id uuid not null references exercises (id) on delete cascade,
  muscle_group_id smallint not null references muscle_groups (id),
  role muscle_role not null,
  contribution numeric(3, 2) not null check (contribution > 0 and contribution <= 1),
  primary key (exercise_id, muscle_group_id)
);

create index exercise_muscles_group_idx on exercise_muscles (muscle_group_id);

create table exercise_equipment (
  exercise_id uuid not null references exercises (id) on delete cascade,
  equipment_id smallint not null references equipment (id),
  primary key (exercise_id, equipment_id)
);

create table exercise_relations (
  exercise_id uuid not null references exercises (id) on delete cascade,
  related_exercise_id uuid not null references exercises (id) on delete cascade,
  relation_type relation_type not null,
  primary key (exercise_id, related_exercise_id, relation_type),
  check (exercise_id <> related_exercise_id)
);

-- Current catalog version; the seed loader raises it on every change.
create table catalog_state (
  id smallint primary key default 1 check (id = 1),
  version integer not null default 0,
  updated_at timestamptz not null default now()
);
insert into catalog_state (id, version) values (1, 0);
