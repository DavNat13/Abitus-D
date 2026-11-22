// Every template renders HTML and plain text in es and en, with its key elements, no emojis
// and with user values escaped.
import { describe, expect, it } from 'vitest';
import { renderMail } from '../src/mail-templates/render-mail.js';
import type { MailLocale, MailPayloads, MailTemplate } from '../src/modules/mail/mail-catalog.js';

const PAYLOADS: { [T in MailTemplate]: MailPayloads[T] } = {
  verification_code: { code: '482913' },
  reset_code: { code: '105277' },
  welcome: {},
  new_device: { deviceName: 'Pixel <b>8</b>', when: '2026-11-22 18:04' },
  password_changed: {},
  sessions_revoked: { reason: 'token_reuse' },
};

const PICTOGRAPHIC = /\p{Extended_Pictographic}/u;
const LOCALES: MailLocale[] = ['es', 'en'];

describe('mail templates', () => {
  for (const template of Object.keys(PAYLOADS) as MailTemplate[]) {
    for (const locale of LOCALES) {
      it(`${template} renders in ${locale}`, async () => {
        const mail = await renderMail(template, locale, PAYLOADS[template] as never);
        expect(mail.subject.length).toBeGreaterThan(5);
        expect(mail.html).toContain(`lang="${locale}"`);
        expect(mail.html).toContain('max-width:600px');
        expect(mail.text.length).toBeGreaterThan(40);
        expect(PICTOGRAPHIC.test(mail.html + mail.text + mail.subject)).toBe(false);
        const code = (PAYLOADS[template] as { code?: string }).code;
        if (code !== undefined) {
          expect(mail.html).toContain(code);
          expect(mail.text).toContain(code);
        }
      });
    }
  }

  it('escapes values provided by users', async () => {
    const mail = await renderMail('new_device', 'en', PAYLOADS.new_device);
    expect(mail.html).not.toContain('<b>8</b>');
    expect(mail.html).toContain('Pixel &lt;b&gt;8&lt;/b&gt;');
  });
});
