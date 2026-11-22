// Shape of the localized mail texts. Placeholders use {name} and are filled as plain text.
import type { MailTemplate } from '../../modules/mail/mail-catalog.js';

export interface TemplateTexts {
  readonly subject: string;
  readonly preheader: string;
  readonly title: string;
  readonly paragraphs: readonly string[];
  readonly steps?: readonly string[];
  readonly note?: string;
}

export interface MailTexts {
  readonly appName: string;
  readonly footerSupport: string;
  readonly footerPrivacy: string;
  readonly templates: Record<MailTemplate, TemplateTexts>;
}

export function fillPlaceholders(text: string, values: Record<string, string>): string {
  return text.replace(/\{(\w+)\}/g, (match, name: string) => values[name] ?? match);
}
