# 10 - Fases de trabajo: hoja de ruta, Fase 0 y Fase 1

Una fase a la vez. No se avanza sin cumplir los criterios de aceptación y sin actualizar `02-MEMORIA.md`. Detalle de las demás fases en `10b` (2 a 4), `10c` (5 a 7), `10d` (8 a 10) y `10e` (mejoras posteriores). Calidad en `17-CALIDAD-Y-PRUEBAS.md`. Las ideas citadas están en `11b` a `11e`.

## Hoja de ruta

| Fase | Nombre | Qué puede hacer el usuario al terminar |
|---|---|---|
| 0 | Preparación | Nada visible; el terreno está listo |
| 1 | Fundaciones y acceso | Crear cuenta, verificar, entrar y mantener la sesión |
| 2 | Biblioteca de ejercicios | Buscar ejercicios reales y crear propios |
| 3 | Planificación | Armar rutinas, programas y calendario |
| 4 | Registro de entrenamiento | Entrenar y registrar series con descanso automático |
| 5 | Reloj y timers | Usar cronómetro, intervalos y perfiles para todo |
| 6 | Analítica | Saber si progresa, con mapa corporal 2D |
| 7 | Running, natación y salud | Registrar cardio con GPS y datos de salud |
| 8 | Cuerpo 3D | Ver el mapa de calor sobre un modelo 3D |
| 9 | Ajustes, cuenta y correos | Configurar todo, exportar e importar datos |
| 10 | Endurecimiento y distribución | Instalar la versión 1.0 desde el APK |
| 11 a 13 | Mejoras posteriores | Entrenamiento inteligente, salud y experiencia |

La versión 1.0 es el final de la Fase 10. Cada fase termina con una etiqueta Git `fase-N`.

## Cómo se ejecuta cada fase (para Claude Code)

1. Leer `02`, `02b`, `03`, `12` y los documentos del área.
2. Escribir en la memoria el plan de la fase como tareas pequeñas, cada una terminable en una sesión.
3. Construir de abajo hacia arriba: base de datos, backend, Android, pruebas.
4. Cada tarea cumple la definición de terminado de `17`.
5. Cerrar: comprobar los criterios, actualizar la memoria y mostrar el resultado al usuario antes de seguir.
6. Todo lo que no figure en la fase va a las ideas; no se mete de contrabando.

## Fase 0 - Preparación

Objetivo: terreno listo y requisitos externos confirmados.

Acciones del usuario (sin tarjeta, sin dominio):
- Cuenta de GitHub, de Vercel (plan Hobby) y de Neon (región São Paulo).
- Cuenta app.abitus.d@gmail.com con verificación en dos pasos y contraseña de aplicación; guardada solo en variables de entorno.
- Keystore de firma de Android generado y respaldado fuera del repositorio.

Entregables:
- Estructura de carpetas (`16-ESTRUCTURA-CARPETAS.md`), Git con `.gitignore`, `.gitattributes` (LF) y `.env.example`.
- Verificación de: requisitos de Android para APK fuera de Play en Chile, límites vigentes de Vercel, Neon y Gmail, y versiones de `06b`. Resultados en la memoria.
- Proyectos vacíos que compilan: `android/` (AGP 9, Room 3, Navigation 3, applicationId `com.abitusd.app`), `backend/` y `database/`, con Detekt, ktlint, ESLint y Prettier.
- Scripts `tools/check-no-emoji` y `tools/check-file-length`.
- Gestor de paquetes y versiones registrados.

Criterios de aceptación:
- La app vacía compila e instala en un emulador con Android 10.
- El backend arranca en local con PostgreSQL en Docker y responde `/health`.
- Un despliegue vacío en Vercel responde `/health` desde internet.
- Los scripts de verificación pasan.

## Fase 1 - Fundaciones y acceso

Objetivo: cuenta, sesión segura, estructura de la app y sistema de diseño.

Entregables:
- Base de datos: identidad y seguridad (usuarios, dispositivos, tokens de refresco, códigos, consentimientos, cola de correo, auditoría).
- Backend: registro con confirmación de 18 años, verificación por código, login, refresco rotativo, logout, recuperación y cambio de contraseña, dispositivos, límites de tasa y SMTP con Gmail (códigos, bienvenida y aviso de dispositivo nuevo).
- Android: tema (tokens, Roboto, `AppIcons`), bienvenida, registro, verificación, login, recuperación, navegación base (Navigation 3) con barra inferior, idiomas es y en, unidades base (conversión en `core/i18n`), Room base y política visible.
- Pruebas de seguridad del módulo de autenticación.

Criterios de aceptación:
- Un usuario nuevo se registra, recibe el código, verifica, entra y mantiene la sesión tras reiniciar la app.
- Reutilizar un token de refresco ya rotado revoca la familia.
- Un login desde otro dispositivo envía el aviso por correo.
- Sin enumeración de cuentas (respuestas idénticas).
- Pruebas automáticas en verde; contraste AA verificado en ambos temas.
