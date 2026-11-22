// Appends catalog skeletons from free-exercise-db to database/seeds/exercises/<discipline>.jsonl.
// Existing slugs are never touched: the seed files are the source of truth and hold the
// Spanish texts written by hand. English texts come from the source; review notes are kept.
//
// Usage:
//   node tools/import-exercises/import-exercises.mjs <exercises.json> <discipline> <selection.txt>
// selection.txt lines: SourceId slug pattern [metric] [rest-seconds]
// Source: github.com/yuhonas/free-exercise-db (Unlicense), file dist/exercises.json.
import { appendFile, readFile } from 'node:fs/promises';
import { existsSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import {
  defaultMetric,
  mapDifficulty,
  mapEquipment,
  mapMuscles,
  SOURCE_COMMIT,
  SOURCE_LICENSE,
} from './source-mapping.mjs';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', '..');
const PATTERNS = new Set([
  'push', 'pull', 'squat', 'hinge', 'lunge', 'carry', 'core', 'jump', 'locomotion', 'swim', 'mobility',
]);

const [sourcePath, discipline, selectionPath] = process.argv.slice(2);
if (!sourcePath || !discipline || !selectionPath) {
  console.error('usage: import-exercises.mjs <exercises.json> <discipline> <selection.txt>');
  process.exit(1);
}

const source = new Map(JSON.parse(await readFile(sourcePath, 'utf8')).map((e) => [e.id, e]));
const target = path.join(ROOT, 'database', 'seeds', 'exercises', `${discipline}.jsonl`);
const existing = existsSync(target)
  ? new Set((await readFile(target, 'utf8')).split('\n').filter(Boolean).map((l) => JSON.parse(l).slug))
  : new Set();

const lines = (await readFile(selectionPath, 'utf8')).split('\n').map((l) => l.trim()).filter(Boolean);
let added = 0;
for (const line of lines) {
  const [id, slug, pattern, metric, rest] = line.split(/\s+/);
  const item = source.get(id);
  if (!item) throw new Error(`source id not found: ${id}`);
  if (!PATTERNS.has(pattern)) throw new Error(`bad pattern for ${id}: ${pattern}`);
  if (existing.has(slug)) continue;
  const primary = mapMuscles(item.primaryMuscles, item.name);
  const secondary = mapMuscles(item.secondaryMuscles, item.name);
  const usedMetric = metric && metric !== '-' ? metric : defaultMetric(item);
  const record = {
    slug,
    discipline,
    metric: usedMetric,
    pattern,
    difficulty: mapDifficulty(item.level),
    unilateral: /one-arm|one arm|single|one-leg|one leg|alternat/i.test(item.name),
    bodyweight: usedMetric === 'bodyweight_reps' || usedMetric === 'weighted_bodyweight_reps',
    rest: Number(rest ?? (usedMetric === 'weight_reps' ? 120 : 60)),
    source: `free-exercise-db ${id} (commit ${SOURCE_COMMIT.slice(0, 7)})`,
    license: SOURCE_LICENSE,
    muscles: { primary: primary.slugs, secondary: secondary.slugs.filter((s) => !primary.slugs.includes(s)) },
    equipment: mapEquipment(item.equipment),
    relations: [],
    en: { name: item.name, summary: '', setup: '', execution: item.instructions, cues: [], mistakes: [], safety: '' },
    es: null,
    review: [...primary.notes, ...secondary.notes],
  };
  await appendFile(target, `${JSON.stringify(record)}\n`);
  existing.add(slug);
  added += 1;
}
console.log(`${discipline}: ${String(added)} added, ${String(existing.size)} total`);
