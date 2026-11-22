// Reads seed files from database/seeds (JSON Lines: one record per line).
import { readdir, readFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

export const SEEDS_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', 'seeds');

export async function readJsonl(relativePath) {
  const text = await readFile(path.join(SEEDS_DIR, relativePath), 'utf8');
  return text
    .split('\n')
    .filter((line) => line.trim() !== '')
    .map((line, index) => {
      try {
        return JSON.parse(line);
      } catch {
        throw new Error(`${relativePath}:${String(index + 1)}: invalid JSON`);
      }
    });
}

/** Every exercise of every discipline file, in a stable order. */
export async function readAllExercises() {
  const files = (await readdir(path.join(SEEDS_DIR, 'exercises')))
    .filter((f) => f.endsWith('.jsonl'))
    .sort();
  const all = [];
  for (const file of files) all.push(...(await readJsonl(`exercises/${file}`)));
  return all;
}
