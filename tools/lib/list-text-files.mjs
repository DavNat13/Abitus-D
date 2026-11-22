// Lists repository text files (tracked and untracked, honoring .gitignore).
import { execFileSync } from 'node:child_process';
import { readFileSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const BINARY_EXTENSIONS = new Set([
  '.png', '.jpg', '.jpeg', '.webp', '.gif', '.ico', '.ttf', '.otf', '.woff', '.woff2',
  '.jar', '.keystore', '.jks', '.glb', '.gltf', '.bin', '.zip', '.apk', '.aab', '.pdf',
]);

export const repoRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', '..');

export function listTextFiles() {
  const output = execFileSync(
    'git',
    ['ls-files', '--cached', '--others', '--exclude-standard', '-z'],
    { cwd: repoRoot, encoding: 'utf8', maxBuffer: 64 * 1024 * 1024 },
  );
  return output
    .split('\0')
    .filter(Boolean)
    .filter((file) => !BINARY_EXTENSIONS.has(path.extname(file).toLowerCase()));
}

export function readText(file) {
  try {
    return readFileSync(path.join(repoRoot, file), 'utf8');
  } catch {
    return null;
  }
}
