# 13 - Seguridad y legal

Seguridad del servidor en `07-BACKEND.md`; seguridad del cliente en `09-FRONTEND-ANDROID.md`. Distribución del APK y documentos de usuario en `13b-DISTRIBUCION-APK.md`. Este archivo es la lista de control transversal.

## Modelo de amenazas (resumen)

| Amenaza | Defensa principal |
|---|---|
| Robo o adivinación de contraseñas | Argon2id, política de longitud, limitación de intentos, avisos por correo |
| Robo de sesión | Tokens de acceso cortos, refresco rotativo con detección de reutilización, sesiones por dispositivo revocables |
| Inyección SQL | Consultas parametrizadas, sin SQL con entrada de usuario |
| Acceso a datos ajenos (IDOR) | Filtro por usuario en toda consulta y pruebas dedicadas |
| Abuso del registro o del correo | Verificación por código, límites por IP y correo, topes diarios, respuestas idénticas |
| Fuga de secretos | Variables de entorno, sin secretos en repositorio ni APK, búsqueda automática de secretos |
| Fuga de datos en logs | Registro sin PII ni tokens, enmascarado de identificadores |
| Intercepción de tráfico | HTTPS obligatorio, sin tráfico en texto plano en la app |
| Dependencias vulnerables | Auditoría periódica y actualización prioritaria de seguridad |
| Pérdida de datos | Respaldos, exportación para el usuario, sincronización |

## Lista de control antes de cerrar una fase

- [ ] Ningún endpoint nuevo sin autenticación, salvo los listados como públicos.
- [ ] Toda entrada validada con esquema cerrado y límites.
- [ ] Todas las consultas parametrizadas; revisión de cualquier SQL crudo.
- [ ] Pruebas de acceso a recursos de otro usuario (deben fallar).
- [ ] Limitación de tasa en rutas sensibles.
- [ ] Sin secretos ni datos personales en logs, repositorio ni APK.
- [ ] Auditoría de dependencias sin vulnerabilidades altas sin resolver.
- [ ] Verificación de cero emojis y de máximo 150 líneas.
- [ ] Permisos de Android mínimos y justificados.
- [ ] Documentos legales actualizados si cambió el tratamiento de datos.

## Datos que se tratan

| Dato | Finalidad | Sensibilidad |
|---|---|---|
| Correo y contraseña (hash) | Cuenta y acceso | Alta |
| Nombre, idioma, unidades, altura, año de nacimiento, objetivo | Personalizar | Media |
| Entrenamientos, series, cargas, rutinas | Funcionamiento y analítica | Media |
| Rutas GPS simplificadas | Cardio | Alta (revelan ubicación y hábitos) |
| Frecuencia cardíaca y pasos (resúmenes) | Cardio y actividad | Alta (datos de salud) |
| Mediciones corporales | Seguimiento | Alta |
| Dirección IP (solo con hash para antiabuso) y dispositivos | Seguridad | Media |

Principios: recoger lo mínimo, pedir consentimiento explícito para salud y ubicación, no compartir con terceros, no usar para publicidad ni perfilado, permitir exportar y eliminar.

## Proveedores que intervienen

Alojamiento del servidor (Vercel), base de datos en la nube (Neon) y correo (Gmail). Cada uno se nombra en la política de privacidad con su función y ubicación aproximada de datos (VERIFICAR regiones al elegirlas). No hay SDK de analítica ni de publicidad.

## Contenido mínimo de la política de privacidad

1. Quién es el responsable y cómo contactarlo.
2. Qué datos se recogen y para qué (tabla anterior en lenguaje claro).
3. Base y consentimiento: aceptación al registrarse; consentimiento separado y revocable para ubicación y salud.
4. Con quién se comparten (los proveedores técnicos y nadie más).
5. Dónde se almacenan y por cuánto tiempo.
6. Derechos del usuario: acceso, rectificación, eliminación, portabilidad (exportar), retirar consentimiento; cómo ejercerlos desde la app.
7. Seguridad: medidas aplicadas, sin garantías absolutas.
8. Edad mínima: 18 años; el registro exige confirmarla.
9. Cambios de la política y cómo se notifican; versión y fecha.
10. Contacto para consultas y reclamos: app.abitus.d@gmail.com.

## Marco legal

- La ley chilena de protección de datos personales (Ley 21.719) tiene calendario de entrada en vigor escalonado; VERIFICAR vigencia y alcance con una fuente oficial y, si la app sale del círculo de amigos, con un profesional.
- La app no sustituye el consejo médico ni de un profesional del deporte; incluirlo en términos y en el primer uso.
- Los términos incluyen: uso personal y no comercial, sin garantías, limitación de responsabilidad, uso aceptable, suspensión de cuentas, y la responsabilidad del usuario sobre su salud y su técnica.
- Versionar los documentos legales; registrar la aceptación (`consents`) con versión y fecha; pedir nueva aceptación si cambian materialmente.

## Eliminación de cuenta y datos

- Desde Ajustes de cuenta, con contraseña y confirmación.
- Borra todos los datos del usuario del servidor y revoca tokens; el cliente limpia lo local.
- Correo de confirmación sin datos del entrenamiento.
- Respaldos: los datos eliminados desaparecen de los respaldos al rotar según su retención; indicarlo en la política.
