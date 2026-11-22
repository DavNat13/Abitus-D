// Inline styles for mail clients. Colors come from the design tokens (05b-UI-TOKENS.md).
import type { CSSProperties } from 'react';

const INK = '#101632';
const PRIMARY = '#0135A8';
const TEXT_SECONDARY = '#5C6174';
const SURFACE_SOFT = '#F7F4FF';
const OUTLINE = '#DEDEE2';
const WHITE = '#FFFFFF';
const FONT = 'Roboto, -apple-system, "Segoe UI", Helvetica, Arial, sans-serif';

export const styles: Record<string, CSSProperties> = {
  body: { backgroundColor: SURFACE_SOFT, margin: 0, padding: '24px 0', fontFamily: FONT },
  container: {
    maxWidth: '600px',
    backgroundColor: WHITE,
    border: `1px solid ${OUTLINE}`,
    borderRadius: '16px',
    padding: '32px 28px',
  },
  brand: { color: PRIMARY, fontSize: '16px', fontWeight: 700, margin: '0 0 24px' },
  title: { color: INK, fontSize: '24px', lineHeight: '30px', fontWeight: 700, margin: '0 0 16px' },
  paragraph: { color: INK, fontSize: '16px', lineHeight: '24px', margin: '0 0 12px' },
  step: { color: INK, fontSize: '16px', lineHeight: '24px', margin: '0 0 6px' },
  codeBox: {
    backgroundColor: SURFACE_SOFT,
    border: `1px solid ${OUTLINE}`,
    borderRadius: '12px',
    padding: '16px',
    margin: '20px 0',
    textAlign: 'center',
  },
  code: {
    color: INK,
    fontSize: '32px',
    fontWeight: 700,
    letterSpacing: '8px',
    fontVariantNumeric: 'tabular-nums',
    margin: 0,
  },
  note: { color: TEXT_SECONDARY, fontSize: '14px', lineHeight: '20px', margin: '16px 0 0' },
  divider: { borderColor: OUTLINE, margin: '28px 0 16px' },
  footer: { color: TEXT_SECONDARY, fontSize: '12px', lineHeight: '16px', margin: '0 0 4px' },
};
