# 15b - Correos: diseño y plantillas

Complemento de `15-CORREOS.md`. El diseño usa la misma paleta y tipografía que la app (`05b-UI-TOKENS.md`), adaptadas al correo.

## Estructura común

1. Preheader (texto breve de vista previa, oculto en el cuerpo).
2. Cabecera con el nombre de la app en texto (sin logo obligatorio).
3. Título claro (una línea).
4. Texto breve (2 a 4 frases).
5. Elemento principal: código en caja grande, o botón de acción cuando corresponda.
6. Nota de seguridad corta cuando aplica.
7. Pie con soporte, enlace a la política de privacidad y, si es opcional, enlace de baja.

## Estilo

- Ancho máximo 600 px, fondo claro neutro, tarjeta blanca, texto oscuro; botón y acento con el color primario.
- Fuente del sistema (sans-serif) con respaldo; la fuente de la app no se incrusta.
- Códigos con cifras tabulares y espaciado amplio.
- Jerarquía: título, cuerpo, nota. Un solo botón por correo.
- Sin emojis ni caracteres decorativos; iconos solo como texto o no usar iconos.
- Contraste AA en texto y botones, incluso con imágenes bloqueadas.

## Plantillas

Cada plantilla: asunto (es y en), preheader, título, cuerpo, elemento principal y nota.

### Código de verificación
- Asunto: "Tu código de verificación" / "Your verification code".
- Cuerpo: indicar para qué es, mostrar el código de 6 dígitos, vigencia de 10 minutos.
- Nota: "Si no creaste una cuenta, ignora este correo". No compartir el código con nadie.

### Bienvenida
- Asunto: "Bienvenido" / "Welcome".
- Cuerpo: confirmación de la cuenta, tres primeros pasos (elegir disciplinas, crear una rutina, registrar la primera sesión) y que funciona sin conexión.
- Elemento: botón para abrir la app (enlace profundo si está disponible; si no, texto).
- Pie: enlace a ayuda y a la política de privacidad.

### Aviso de dispositivo nuevo
- Asunto: "Nuevo inicio de sesión en tu cuenta" / "New sign-in to your account".
- Cuerpo: modelo o nombre del dispositivo, fecha y hora, ubicación aproximada solo si se calcula sin guardar datos personales (si no, omitir).
- Elemento: instrucciones para cerrar sesiones desde Ajustes de cuenta.
- Nota: si no fue el usuario, cambiar la contraseña de inmediato.

### Código de recuperación
- Asunto: "Código para restablecer tu contraseña" / "Your password reset code".
- Cuerpo: mismo formato del código de verificación con vigencia de 10 minutos.
- Nota: si no lo solicitaste, ignóralo; tu contraseña no cambió.

### Contraseña cambiada
- Asunto: "Tu contraseña fue cambiada" / "Your password was changed".
- Cuerpo: confirmar el cambio, indicar que se cerraron otras sesiones.
- Nota: si no fuiste tú, recuperar la cuenta de inmediato y contactar soporte.

### Cuenta eliminada
- Asunto: "Tu cuenta fue eliminada" / "Your account was deleted".
- Cuerpo: confirmar la eliminación y qué datos se borraron; mencionar que los respaldos se depuran según la política.

### Resumen semanal (opcional)
- Asunto: "Tu resumen semanal" / "Your weekly summary".
- Cuerpo: sesiones, volumen, mejor marca, estado de progreso (con el dato que lo sustenta).
- Pie: enlace para desactivar los resúmenes.

## Implementación

- Plantillas con React Email, cada una en su archivo; componentes compartidos (cabecera, pie, caja de código, botón) en archivos separados.
- Textos en archivos de idioma, no escritos dentro de los componentes.
- Variables seguras escapadas; nunca se inyecta HTML del usuario.
- Pruebas: generar HTML y texto para cada plantilla y cada idioma; verificar que contiene los elementos clave y ningún emoji.
- Cada archivo respeta el máximo de 150 líneas.
