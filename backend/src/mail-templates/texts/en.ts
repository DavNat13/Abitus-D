import type { MailTexts } from './mail-texts.js';

export const en: MailTexts = {
  appName: 'Abitus-D',
  footerSupport: 'Support: app.abitus.d@gmail.com',
  footerPrivacy: 'The privacy policy is available in the app.',
  templates: {
    verification_code: {
      subject: 'Your verification code',
      preheader: 'Use this code to confirm your email in Abitus-D.',
      title: 'Confirm your email',
      paragraphs: [
        'Enter this code in the app to activate your account.',
        'It expires in 10 minutes.',
      ],
      note: 'If you did not create an account, ignore this email. Do not share the code with anyone.',
    },
    reset_code: {
      subject: 'Your password reset code',
      preheader: 'Use this code to choose a new password.',
      title: 'Reset your password',
      paragraphs: [
        'Enter this code in the app to choose a new password.',
        'It expires in 10 minutes.',
      ],
      note: 'If you did not request it, ignore this email: your password has not changed.',
    },
    welcome: {
      subject: 'Welcome to Abitus-D',
      preheader: 'Your account is active. Here are the first steps.',
      title: 'Your account is active',
      paragraphs: [
        'You can start logging your training. The app works offline and syncs when the signal is back.',
      ],
      steps: [
        'Choose the disciplines you practice.',
        'Create your first routine.',
        'Log your first session.',
      ],
    },
    new_device: {
      subject: 'New sign-in to your account',
      preheader: 'Your account was signed in from a new device.',
      title: 'New sign-in',
      paragraphs: [
        'Your account was signed in from {deviceName} on {when} (UTC).',
        'You can review and end sessions in Profile, Account settings, Devices.',
      ],
      note: 'If this was not you, change your password right away.',
    },
    password_changed: {
      subject: 'Your password was changed',
      preheader: 'We confirm that your password was changed.',
      title: 'Password changed',
      paragraphs: [
        'The password of your account was changed. Sessions on your other devices were ended.',
      ],
      note: 'If this was not you, recover your account right away and contact support.',
    },
    sessions_revoked: {
      subject: 'We signed you out on all devices',
      preheader: 'All sessions of your account were ended.',
      title: 'Sessions ended',
      paragraphs: [
        'We ended the sessions of your account on all devices. You will need to sign in again.',
      ],
      note: 'If you did not request this, we recommend changing your password.',
    },
  },
};
