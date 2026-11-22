// Fails if a hand-written text file exceeds 150 lines (golden rules 31 to 34).
import path from 'node:path';
import { listTextFiles, readText } from './lib/list-text-files.mjs';

const MAX_LINES = 150;

// Generated and lock files are exempt (rule 33).
const EXEMPT_NAMES = new Set([
  'package-lock.json', 'pnpm-lock.yaml', 'yarn.lock', 'gradlew', 'gradlew.bat',
  'gradle-wrapper.properties', 'openapi.json',
]);
const EXEMPT_DIRS = ['android/app/schemas/'];

function isExempt(file) {
  if (EXEMPT_NAMES.has(path.posix.basename(file))) return true;
  return EXEMPT_DIRS.some((dir) => file.startsWith(dir));
}

const offenders = [];
for (const file of listTextFiles()) {
  if (isExempt(file)) continue;
  const text = readText(file);
  if (text === null) continue;
  const lines = text.endsWith('\n') ? text.split('\n').length - 1 : text.split('\n').length;
  if (lines > MAX_LINES) offenders.push(`${file}: ${lines} lines`);
}

if (offenders.length > 0) {
  console.error(`check-file-length: ${offenders.length} file(s) over ${MAX_LINES} lines:`);
  for (const offender of offenders) console.error(`  ${offender}`);
  process.exit(1);
}
console.log('check-file-length: OK');
