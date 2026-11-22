// Merges hand-written texts into database/seeds/exercises/<discipline>.jsonl.
// Input lines: {"slug", "en": {...}, "es": {...}} with name, summary, setup, execution,
// cues, mistakes and safety. Texts are written for Abitus-D (golden rule 16): source
// instructions are not copied when they contain outdated or doubtful advice.
// Usage: node tools/import-exercises/merge-content.mjs <discipline> <content.jsonl>
import { readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', '..');
const KEYS = ['name', 'summary', 'setup', 'execution', 'cues', 'mistakes', 'safety'];

const [discipline, contentPath] = process.argv.slice(2);
if (!discipline || !contentPath) {
  console.error('usage: merge-content.mjs <discipline> <content.jsonl>');
  process.exit(1);
}
const target = path.join(ROOT, 'database', 'seeds', 'exercises', `${discipline}.jsonl`);
const records = (await readFile(target, 'utf8')).split('\n').filter(Boolean).map((l) => JSON.parse(l));
const bySlug = new Map(records.map((r) => [r.slug, r]));

function texts(input, fallbackName, where) {
  const out = {};
  for (const key of KEYS) {
    const value = input[key] ?? (key === 'name' ? fallbackName : undefined);
    if (value === undefined) throw new Error(`${where}: missing ${key}`);
    out[key] = value;
  }
  return out;
}

const content = (await readFile(contentPath, 'utf8')).split('\n').filter((l) => l.trim() !== '');
for (const line of content) {
  const c = JSON.parse(line);
  const record = bySlug.get(c.slug);
  if (!record) throw new Error(`unknown slug in ${discipline}: ${c.slug}`);
  record.en = texts(c.en, record.en.name, `${c.slug}.en`);
  record.es = texts(c.es, undefined, `${c.slug}.es`);
  if (c.review) record.review = [...record.review, ...c.review];
}
await writeFile(target, `${records.map((r) => JSON.stringify(r)).join('\n')}\n`);
const done = records.filter((r) => r.es !== null).length;
console.log(`${discipline}: ${String(done)}/${String(records.length)} with Spanish`);
