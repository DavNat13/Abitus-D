// Rebuilds the relations of every catalog exercise from a relations list.
// Line format: <slug-a> <progression|alternative> <slug-b>
//   progression: b is harder than a (a gets "progression" to b, b gets "regression" to a)
//   alternative: both get "alternative" to each other
// Usage: node tools/import-exercises/apply-relations.mjs <relations.txt>
import { readdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', '..', 'database', 'seeds', 'exercises');
const [listPath] = process.argv.slice(2);
if (!listPath) {
  console.error('usage: apply-relations.mjs <relations.txt>');
  process.exit(1);
}

const files = (await readdir(DIR)).filter((f) => f.endsWith('.jsonl'));
const byFile = new Map();
const bySlug = new Map();
for (const file of files) {
  const rows = (await readFile(path.join(DIR, file), 'utf8')).split('\n').filter(Boolean).map((l) => JSON.parse(l));
  byFile.set(file, rows);
  for (const row of rows) {
    row.relations = [];
    bySlug.set(row.slug, row);
  }
}

function add(from, type, to) {
  const row = bySlug.get(from);
  if (!row) throw new Error(`unknown slug: ${from}`);
  if (!bySlug.has(to)) throw new Error(`unknown slug: ${to}`);
  if (!row.relations.some((r) => r.type === type && r.slug === to)) row.relations.push({ type, slug: to });
}

const lines = (await readFile(listPath, 'utf8')).split('\n').map((l) => l.trim()).filter((l) => l && !l.startsWith('#'));
for (const line of lines) {
  const [a, kind, b] = line.split(/\s+/);
  if (kind === 'progression') {
    add(a, 'progression', b);
    add(b, 'regression', a);
  } else if (kind === 'alternative') {
    add(a, 'alternative', b);
    add(b, 'alternative', a);
  } else {
    throw new Error(`bad relation line: ${line}`);
  }
}

for (const [file, rows] of byFile) {
  await writeFile(path.join(DIR, file), `${rows.map((r) => JSON.stringify(r)).join('\n')}\n`);
}
const total = [...bySlug.values()].reduce((sum, r) => sum + r.relations.length, 0);
console.log(`relations: ${String(lines.length)} lines, ${String(total)} relation entries`);
