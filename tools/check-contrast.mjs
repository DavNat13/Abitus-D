// WCAG 2.x contrast of the text and control pairs used by the design tokens (05b-UI-TOKENS.md).
// Reads AppColors.kt so the check always uses the real values. Fails below the required ratio.
// Usage: node tools/check-contrast.mjs
import { readFileSync } from 'node:fs';

const SOURCE = 'android/app/src/main/java/com/abitusd/app/core/designsystem/theme/AppColors.kt';
const TEXT_AA = 4.5;
const UI_AA = 3;

const source = readFileSync(new URL(`../${SOURCE}`, import.meta.url), 'utf8');
const named = Object.fromEntries(
  [...source.matchAll(/private val (\w+) = Color\(0xFF([0-9A-F]{6})\)/g)].map((m) => [m[1], m[2]]),
);

function theme(name) {
  const block = source.split(`val ${name} =`)[1]?.split('\n    )')[0] ?? '';
  const tokens = {};
  for (const m of block.matchAll(/(\w+) = (?:Color\(0xFF([0-9A-F]{6})\)|(\w+)),/g)) {
    const hex = m[2] ?? named[m[3]];
    if (hex) tokens[m[1]] = hex;
  }
  return tokens;
}

function luminance(hex) {
  const [r, g, b] = [0, 2, 4].map((i) => {
    const c = parseInt(hex.slice(i, i + 2), 16) / 255;
    return c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4;
  });
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

function ratio(a, b) {
  const [hi, lo] = [luminance(a), luminance(b)].sort((x, y) => y - x);
  return (hi + 0.05) / (lo + 0.05);
}

const PAIRS = [
  ['textPrimary', 'background', TEXT_AA],
  ['textSecondary', 'background', TEXT_AA],
  ['textPrimary', 'surfaceSoft', TEXT_AA],
  ['textSecondary', 'surfaceSoft', TEXT_AA],
  ['primary', 'background', TEXT_AA],
  ['onPrimary', 'primary', TEXT_AA],
  ['onPrimaryContainer', 'primaryContainer', TEXT_AA],
  ['primary', 'primaryContainer', UI_AA],
  ['successText', 'background', TEXT_AA],
  ['successText', 'successContainer', TEXT_AA],
  ['error', 'background', TEXT_AA],
  ['error', 'surfaceSoft', TEXT_AA],
  ['textPrimary', 'errorContainer', TEXT_AA],
  ['warning', 'background', TEXT_AA],
  ['textPrimary', 'warningContainer', TEXT_AA],
  ['inputBorder', 'surfaceSoft', UI_AA],
];

let failed = 0;
for (const name of ['LightAppColors', 'DarkAppColors']) {
  const t = theme(name);
  for (const [fg, bg, min] of PAIRS) {
    if (!t[fg] || !t[bg]) throw new Error(`${name}: missing ${fg} or ${bg}`);
    const value = ratio(t[fg], t[bg]);
    const ok = value >= min;
    if (!ok) failed += 1;
    console.log(`${ok ? 'OK  ' : 'FAIL'} ${name} ${fg} on ${bg}: ${value.toFixed(2)} (min ${min})`);
  }
}
if (failed > 0) {
  console.error(`check-contrast: ${failed} pair(s) below AA`);
  process.exit(1);
}
console.log('check-contrast: OK');
