// Generates database/seeds/REVIEW.md: review notes per exercise, the exercises with own text
// and a sample per discipline for the human review required by Phase 2 (10b).
// Usage: node tools/import-exercises/build-review.mjs
import { readdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const SEEDS = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', '..', 'database', 'seeds');
const SAMPLE_PER_DISCIPLINE = 3;

const files = (await readdir(path.join(SEEDS, 'exercises'))).filter((f) => f.endsWith('.jsonl')).sort();
const all = [];
for (const file of files) {
  const text = await readFile(path.join(SEEDS, 'exercises', file), 'utf8');
  all.push(...text.split('\n').filter(Boolean).map((l) => JSON.parse(l)));
}

const out = [
  '# Revisión del catálogo de ejercicios',
  '',
  'Archivo generado por `tools/import-exercises/build-review.mjs`. No editar a mano.',
  '',
  '## Muestra para revisión humana (criterio de aceptación de la Fase 2)',
  '',
  'Revisar en la app o en los archivos de `exercises/` que nombre, músculos, equipo e',
  'indicaciones (es y en) sean correctos. Anotar errores en `REVIEW-NOTES.md` o en el chat.',
  '',
];
const byDiscipline = new Map();
for (const e of all) byDiscipline.set(e.discipline, [...(byDiscipline.get(e.discipline) ?? []), e]);
for (const [discipline, rows] of byDiscipline) {
  const step = Math.max(1, Math.floor(rows.length / SAMPLE_PER_DISCIPLINE));
  const sample = rows.filter((_, i) => i % step === 0).slice(0, SAMPLE_PER_DISCIPLINE);
  out.push(`- ${discipline}: ${sample.map((e) => `\`${e.slug}\` (${e.es.name})`).join(', ')}`);
}
out.push('', '## Decisiones tomadas sobre datos dudosos de la fuente', '');
for (const e of all.filter((x) => x.review.length > 0)) {
  out.push(`- \`${e.slug}\`: ${e.review.join(' | ')}`);
}
out.push('', '## Ejercicios con texto propio (sin fuente externa)', '');
const own = all.filter((e) => e.license === 'Propio').map((e) => `\`${e.slug}\``);
for (let i = 0; i < own.length; i += 8) out.push(`- ${own.slice(i, i + 8).join(', ')}`);
out.push('');
await writeFile(path.join(SEEDS, 'REVIEW.md'), out.join('\n'));
console.log(`REVIEW.md: ${String(out.length)} lines`);
