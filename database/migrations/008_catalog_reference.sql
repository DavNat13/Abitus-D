-- 008 - Catalog reference data: disciplines, muscle groups and equipment, with translations.
-- Read-only for clients. Small tables: always sent whole with the catalog.
-- Reversal: drop the six tables in reverse order.
create table disciplines (
  id smallint primary key,
  slug text not null unique check (slug ~ '^[a-z0-9_]{2,40}$'),
  sort_order smallint not null,
  icon_key text not null check (char_length(icon_key) <= 40)
);

create table discipline_translations (
  discipline_id smallint not null references disciplines (id) on delete cascade,
  locale text not null check (locale ~ '^[a-z]{2}$'),
  name text not null check (char_length(name) between 1 and 60),
  description text not null check (char_length(description) <= 300),
  primary key (discipline_id, locale)
);

create table muscle_groups (
  id smallint primary key,
  slug text not null unique check (slug ~ '^[a-z0-9_]{2,40}$'),
  body_region text not null check (body_region in ('upper', 'core', 'lower')),
  view text not null check (view in ('front', 'back')),
  mesh_key text not null check (char_length(mesh_key) <= 60),
  svg_key text not null check (char_length(svg_key) <= 60),
  sort_order smallint not null
);

create table muscle_group_translations (
  muscle_group_id smallint not null references muscle_groups (id) on delete cascade,
  locale text not null check (locale ~ '^[a-z]{2}$'),
  name text not null check (char_length(name) between 1 and 60),
  primary key (muscle_group_id, locale)
);

create table equipment (
  id smallint primary key,
  slug text not null unique check (slug ~ '^[a-z0-9_]{2,40}$')
);

create table equipment_translations (
  equipment_id smallint not null references equipment (id) on delete cascade,
  locale text not null check (locale ~ '^[a-z]{2}$'),
  name text not null check (char_length(name) between 1 and 60),
  primary key (equipment_id, locale)
);
