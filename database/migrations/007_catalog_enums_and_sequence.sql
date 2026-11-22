-- 007 - Catalog enumerations (08-DATABASE.md) and the global sync sequence (07-BACKEND.md).
-- server_seq values come from one sequence so a single cursor orders every synced row.
-- Reversal: drop sequence sync_seq; drop the five types (only after dropping dependent tables).
create type metric_type as enum (
  'weight_reps', 'bodyweight_reps', 'weighted_bodyweight_reps', 'duration',
  'distance_duration', 'swim_laps', 'plyo'
);
create type movement_pattern as enum (
  'push', 'pull', 'squat', 'hinge', 'lunge', 'carry', 'core', 'jump', 'locomotion', 'swim', 'mobility'
);
create type muscle_role as enum ('primary', 'secondary');
create type relation_type as enum ('progression', 'regression', 'alternative');
create sequence sync_seq as bigint;
