// Kysely types of the catalog tables (database/migrations 007 to 011). Keep in sync by hand.
import type { ColumnType, Generated } from 'kysely';

type Timestamp = ColumnType<Date, Date | string | undefined, Date | string>;
type NullableTimestamp = ColumnType<
  Date | null,
  Date | string | null | undefined,
  Date | string | null
>;

export type MetricType =
  | 'weight_reps'
  | 'bodyweight_reps'
  | 'weighted_bodyweight_reps'
  | 'duration'
  | 'distance_duration'
  | 'swim_laps'
  | 'plyo';

export type MovementPattern =
  | 'push'
  | 'pull'
  | 'squat'
  | 'hinge'
  | 'lunge'
  | 'carry'
  | 'core'
  | 'jump'
  | 'locomotion'
  | 'swim'
  | 'mobility';

export interface DisciplinesTable {
  id: number;
  slug: string;
  sort_order: number;
  icon_key: string;
}

export interface DisciplineTranslationsTable {
  discipline_id: number;
  locale: string;
  name: string;
  description: string;
}

export interface MuscleGroupsTable {
  id: number;
  slug: string;
  body_region: string;
  view: string;
  mesh_key: string;
  svg_key: string;
  sort_order: number;
}

export interface NameTranslationTable {
  locale: string;
  name: string;
}

export interface ExercisesTable {
  id: string;
  slug: string | null;
  owner_user_id: string | null;
  discipline_id: number;
  metric_type: MetricType;
  movement_pattern: MovementPattern;
  difficulty: number;
  is_unilateral: ColumnType<boolean, boolean | undefined, boolean>;
  uses_bodyweight: ColumnType<boolean, boolean | undefined, boolean>;
  default_rest_seconds: ColumnType<number, number | undefined, number>;
  source: string | null;
  license: string | null;
  catalog_version: ColumnType<number, number | undefined, number>;
  content_hash: string | null;
  created_at: Timestamp;
  updated_at: Timestamp;
  deleted_at: NullableTimestamp;
  server_seq: Generated<string>;
}

export interface ExerciseTranslationsTable {
  exercise_id: string;
  locale: string;
  name: string;
  summary: ColumnType<string, string | undefined, string>;
  setup: ColumnType<string, string | undefined, string>;
  execution: ColumnType<string[], string[] | undefined, string[]>;
  cues: ColumnType<string[], string[] | undefined, string[]>;
  common_mistakes: ColumnType<string[], string[] | undefined, string[]>;
  safety_notes: ColumnType<string, string | undefined, string>;
}

export interface ExerciseMusclesTable {
  exercise_id: string;
  muscle_group_id: number;
  role: 'primary' | 'secondary';
  contribution: ColumnType<string, number | string, number | string>;
}

export interface CatalogDatabase {
  disciplines: DisciplinesTable;
  discipline_translations: DisciplineTranslationsTable;
  muscle_groups: MuscleGroupsTable;
  muscle_group_translations: NameTranslationTable & { muscle_group_id: number };
  equipment: { id: number; slug: string };
  equipment_translations: NameTranslationTable & { equipment_id: number };
  exercises: ExercisesTable;
  exercise_translations: ExerciseTranslationsTable;
  exercise_muscles: ExerciseMusclesTable;
  exercise_equipment: { exercise_id: string; equipment_id: number };
  exercise_relations: {
    exercise_id: string;
    related_exercise_id: string;
    relation_type: 'progression' | 'regression' | 'alternative';
  };
  catalog_state: { id: number; version: number; updated_at: Timestamp };
}
