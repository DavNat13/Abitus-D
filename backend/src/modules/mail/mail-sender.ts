// SMTP transport (Nodemailer). The provider changes only through environment variables.
import nodemailer from 'nodemailer';
import type { AppConfig } from '../../app/config.js';

export interface OutgoingMail {
  readonly to: string;
  readonly subject: string;
  readonly html: string;
  readonly text: string;
}

export interface MailSender {
  send(mail: OutgoingMail): Promise<void>;
}

const SEND_TIMEOUT_MS = 8000;
const SMTPS_PORT = 465;

export function createSmtpSender(smtp: AppConfig['smtp']): MailSender {
  const transport = nodemailer.createTransport({
    host: smtp.host,
    port: smtp.port,
    secure: smtp.port === SMTPS_PORT,
    requireTLS: smtp.port !== SMTPS_PORT && smtp.user !== '',
    ...(smtp.user === '' ? {} : { auth: { user: smtp.user, pass: smtp.password } }),
    connectionTimeout: SEND_TIMEOUT_MS,
    greetingTimeout: SEND_TIMEOUT_MS,
    socketTimeout: SEND_TIMEOUT_MS,
  });
  return {
    async send(mail) {
      await transport.sendMail({ from: smtp.from, ...mail });
    },
  };
}
