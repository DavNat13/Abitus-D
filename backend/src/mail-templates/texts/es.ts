import type { MailTexts } from './mail-texts.js';

export const es: MailTexts = {
  appName: 'Abitus-D',
  footerSupport: 'Soporte: app.abitus.d@gmail.com',
  footerPrivacy: 'La política de privacidad está disponible dentro de la app.',
  templates: {
    verification_code: {
      subject: 'Tu código de verificación',
      preheader: 'Usa este código para confirmar tu correo en Abitus-D.',
      title: 'Confirma tu correo',
      paragraphs: ['Escribe este código en la app para activar tu cuenta.', 'Vence en 10 minutos.'],
      note: 'Si no creaste una cuenta, ignora este correo. No compartas el código con nadie.',
    },
    reset_code: {
      subject: 'Código para restablecer tu contraseña',
      preheader: 'Usa este código para elegir una contraseña nueva.',
      title: 'Restablece tu contraseña',
      paragraphs: [
        'Escribe este código en la app para elegir una contraseña nueva.',
        'Vence en 10 minutos.',
      ],
      note: 'Si no lo solicitaste, ignora este correo: tu contraseña no cambió.',
    },
    welcome: {
      subject: 'Bienvenido a Abitus-D',
      preheader: 'Tu cuenta está activa. Estos son los primeros pasos.',
      title: 'Tu cuenta está activa',
      paragraphs: [
        'Ya puedes registrar tus entrenamientos. La app funciona sin conexión y sincroniza cuando vuelve la señal.',
      ],
      steps: [
        'Elige las disciplinas que practicas.',
        'Crea tu primera rutina.',
        'Registra tu primera sesión.',
      ],
    },
    new_device: {
      subject: 'Nuevo inicio de sesión en tu cuenta',
      preheader: 'Se inició sesión desde un dispositivo nuevo.',
      title: 'Nuevo inicio de sesión',
      paragraphs: [
        'Se inició sesión en tu cuenta desde {deviceName} el {when} (UTC).',
        'Puedes revisar y cerrar sesiones en Perfil, Ajustes de la cuenta, Dispositivos.',
      ],
      note: 'Si no fuiste tú, cambia tu contraseña de inmediato.',
    },
    password_changed: {
      subject: 'Tu contraseña fue cambiada',
      preheader: 'Confirmamos el cambio de tu contraseña.',
      title: 'Contraseña cambiada',
      paragraphs: [
        'La contraseña de tu cuenta se cambió. Cerramos las sesiones de los demás dispositivos.',
      ],
      note: 'Si no fuiste tú, recupera tu cuenta de inmediato y escríbenos a soporte.',
    },
    sessions_revoked: {
      subject: 'Cerramos la sesión en todos tus dispositivos',
      preheader: 'Todas las sesiones de tu cuenta se cerraron.',
      title: 'Sesiones cerradas',
      paragraphs: [
        'Cerramos la sesión de tu cuenta en todos los dispositivos. Tendrás que iniciar sesión otra vez.',
      ],
      note: 'Si no lo pediste tú, te recomendamos cambiar tu contraseña.',
    },
  },
};
