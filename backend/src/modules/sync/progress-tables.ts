import { z } from 'zod';

const meters = (min: number, max: number) => z.number().min(min).max(max).nullable();

export const progressSchemas = {
  body_measurements: z.strictObject({
    id: z.uuid(),
    measuredOn: z.iso.date(),
    weightKg: z.number().min(20).max(500).nullable(),
    waistM: meters(0.3, 2.5),
    chestM: meters(0.3, 2.5),
    hipM: meters(0.3, 2.5),
    armM: meters(0.1, 1),
    thighM: meters(0.2, 1.5),
    note: z.string().max(200),
    updatedAt: z.iso.datetime({ offset: true }),
  }),
} as const;

export const PROGRESS_NUMERIC_COLUMNS = [
  'weight_kg',
  'waist_m',
  'chest_m',
  'hip_m',
  'arm_m',
  'thigh_m',
];
