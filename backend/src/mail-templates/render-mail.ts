// Renders a template in a language to subject, HTML and plain text.
import { render } from '@react-email/render';
import { createElement } from 'react';
import type { MailLocale, MailPayloads, MailTemplate } from '../modules/mail/mail-catalog.js';
import { StandardMail } from './standard-mail.js';
import { en } from './texts/en.js';
import { es } from './texts/es.js';
import { fillPlaceholders, type MailTexts } from './texts/mail-texts.js';

const TEXTS: Record<MailLocale, MailTexts> = { es, en };

export interface RenderedMail {
  readonly subject: string;
  readonly html: string;
  readonly text: string;
}

export async function renderMail<T extends MailTemplate>(
  template: T,
  locale: MailLocale,
  payload: MailPayloads[T],
): Promise<RenderedMail> {
  const texts = TEXTS[locale];
  const t = texts.templates[template];
  const values: Record<string, string> = { ...payload };
  const element = createElement(StandardMail, {
    lang: locale,
    appName: texts.appName,
    preheader: t.preheader,
    title: t.title,
    paragraphs: t.paragraphs.map((p) => fillPlaceholders(p, values)),
    code: values.code,
    steps: t.steps,
    note: t.note,
    footerLines: [texts.footerSupport, texts.footerPrivacy],
  });
  const [html, text] = await Promise.all([render(element), render(element, { plainText: true })]);
  return { subject: t.subject, html, text };
}
