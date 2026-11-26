import { z } from 'zod';

const stamp = z.iso.datetime({ offset: true });
const phase = z.strictObject({
  kind: z.enum(['work', 'rest', 'prepare', 'inhale', 'hold', 'exhale']),
  durationS: z.number().int().min(1).max(86400),
  reps: z.number().int().min(1).max(1000).nullable().optional(),
  label: z.string().trim().min(1).max(60).nullable().optional(),
});
export const timerSpec = z.strictObject({
  phases: z.array(phase).max(50),
  rounds: z.number().int().min(1).max(100),
});

export const clockSchemas = {
  exercise_favorites: z.strictObject({ id: z.uuid(), exerciseId: z.uuid(), updatedAt: stamp }),
  timer_profiles: z.strictObject({
    id: z.uuid(),
    name: z.string().trim().min(1).max(60),
    mode: z.enum([
      'stopwatch',
      'countdown',
      'intervals',
      'tabata',
      'emom',
      'amrap',
      'for_time',
      'custom',
    ]),
    spec: z
      .string()
      .max(4000)
      .refine((s) => {
        try {
          return timerSpec.safeParse(JSON.parse(s)).success;
        } catch {
          return false;
        }
      }),
    updatedAt: stamp,
  }),
} as const;
