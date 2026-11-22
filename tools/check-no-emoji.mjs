// Fails if any repository text file contains an emoji (golden rule 7).
import { listTextFiles, readText } from './lib/list-text-files.mjs';

const PICTOGRAPHIC = /\p{Extended_Pictographic}/u;
// Copyright, registered and trademark signs are typographic and allowed.
const ALLOWED = new Set([0xa9, 0xae, 0x2122]);
const EMOJI_SELECTOR = 0xfe0f;
const KEYCAP = 0x20e3;
const FLAG_FIRST = 0x1f1e6;
const FLAG_LAST = 0x1f1ff;

function isEmoji(char) {
  const code = char.codePointAt(0);
  if (ALLOWED.has(code)) return false;
  if (code === EMOJI_SELECTOR || code === KEYCAP) return true;
  if (code >= FLAG_FIRST && code <= FLAG_LAST) return true;
  return PICTOGRAPHIC.test(char);
}

function lineHasEmoji(line) {
  for (const char of line) {
    if (char.codePointAt(0) > 0x7f && isEmoji(char)) return true;
  }
  return false;
}

const findings = [];
for (const file of listTextFiles()) {
  const text = readText(file);
  if (text === null) continue;
  text.split('\n').forEach((line, index) => {
    if (lineHasEmoji(line)) findings.push(`${file}:${index + 1}`);
  });
}

if (findings.length > 0) {
  console.error(`check-no-emoji: ${findings.length} line(s) with emoji found:`);
  for (const finding of findings) console.error(`  ${finding}`);
  process.exit(1);
}
console.log('check-no-emoji: OK');
