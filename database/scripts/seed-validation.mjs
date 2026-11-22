// Validation of seed records before loading (08c-DATABASE-SEMILLAS.md). Fails loudly.
import { createHash } from 'node:crypto';

const METRICS = new Set([
  'weight_reps',
  'bodyweight_reps',
  'weighted_bodyweight_reps',
  'duration',
  'distance_duration',
  'swim_laps',
  'plyo',
]);
const PATTERNS = new Set([
  'push',
  'pull',
  'squat',
  'hinge',
  'lunge',
  'carry',
  'core',
  'jump',
  'locomotion',
  'swim',
  'mobility',
]);
const RELATIONS = new Set(['progression', 'regression', 'alternative']);
const SLUG = /^[a-z0-9_]{2,80}$/;

function checkTexts(slug, locale, t) {
  if (!t || typeof t.name !== 'string' || t.name.trim() === '')
    throw new Error(`${slug}: ${locale}.name missing`);
  for (const key of ['execution', 'cues', 'mistakes']) {
    if (!Array.isArray(t[key])) throw new Error(`${slug}: ${locale}.${key} must be a list`);
  }
}

/** Throws on the first invalid record. `refs` holds the known reference slugs. */
export function validateExercise(e, refs) {
  if (!SLUG.test(e.slug)) throw new Error(`bad slug: ${e.slug}`);
  if (!refs.disciplines.has(e.discipline))
    throw new Error(`${e.slug}: unknown discipline ${e.discipline}`);
  if (!METRICS.has(e.metric)) throw new Error(`${e.slug}: bad metric ${e.metric}`);
  if (!PATTERNS.has(e.pattern)) throw new Error(`${e.slug}: bad pattern ${e.pattern}`);
  if (!(e.difficulty >= 1 && e.difficulty <= 5)) throw new Error(`${e.slug}: bad difficulty`);
  if (!e.source || !e.license) throw new Error(`${e.slug}: source and license are required`);
  if (e.muscles.primary.length === 0) throw new Error(`${e.slug}: needs a primary muscle`);
  for (const m of [...e.muscles.primary, ...e.muscles.secondary]) {
    if (!refs.muscles.has(m)) throw new Error(`${e.slug}: unknown muscle ${m}`);
  }
  for (const q of e.equipment)
    if (!refs.equipment.has(q)) throw new Error(`${e.slug}: unknown equipment ${q}`);
  for (const r of e.relations) {
    if (!RELATIONS.has(r.type)) throw new Error(`${e.slug}: bad relation ${r.type}`);
  }
  checkTexts(e.slug, 'en', e.en);
  if (e.es !== null) checkTexts(e.slug, 'es', e.es);
}

/** Stable hash of everything that reaches the database (review notes excluded). */
export function contentHash(e) {
  const { review: _review, ...data } = e;
  return createHash('sha256').update(JSON.stringify(data)).digest('hex');
}

/** RFC 4122 UUID version 5 (SHA-1) of a name in a fixed namespace: same slug, same id. */
export function uuidV5(name) {
  const namespace = Buffer.from('6f2b8c1e4a7d4e0b9c3f5a1d2e7b8c90', 'hex');
  const hash = createHash('sha1')
    .update(Buffer.concat([namespace, Buffer.from(name)]))
    .digest();
  hash[6] = (hash[6] & 0x0f) | 0x50;
  hash[8] = (hash[8] & 0x3f) | 0x80;
  const h = hash.subarray(0, 16).toString('hex');
  return `${h.slice(0, 8)}-${h.slice(8, 12)}-${h.slice(12, 16)}-${h.slice(16, 20)}-${h.slice(20, 32)}`;
}
