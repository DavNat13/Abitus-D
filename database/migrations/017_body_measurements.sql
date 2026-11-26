-- Body measurements of the athlete (phase 6). SI units: kilograms and meters.
-- Reversal: drop body_measurements (including its trigger and index).
create table body_measurements (
  id uuid primary key,
  user_id uuid not null references users (id) on delete cascade,
  measured_on date not null,
  weight_kg numeric(5, 2) check (weight_kg between 20 and 500),
  waist_m numeric(4, 3) check (waist_m between 0.3 and 2.5),
  chest_m numeric(4, 3) check (chest_m between 0.3 and 2.5),
  hip_m numeric(4, 3) check (hip_m between 0.3 and 2.5),
  arm_m numeric(4, 3) check (arm_m between 0.1 and 1.0),
  thigh_m numeric(4, 3) check (thigh_m between 0.2 and 1.5),
  note text not null default '' check (char_length(note) <= 200),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null,
  deleted_at timestamptz,
  server_seq bigint not null default nextval('sync_seq')
);
create index body_measurements_user_seq_idx on body_measurements (user_id, server_seq);
create trigger body_measurements_bump_server_seq before update on body_measurements
  for each row execute function bump_server_seq();
