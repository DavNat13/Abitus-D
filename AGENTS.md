# Abitus-D - Instrucciones para agentes (opencode, Codex y otros)

Este archivo es para cualquier agente distinto de Claude Code. Las reglas del proyecto son las mismas: `CLAUDE.md` y la carpeta `claude/` mandan. No dupliques aquí lo que ya dicen; léelos.

## Antes de tocar código

1. Lee `CLAUDE.md` completo (reglas innegociables, idioma, estructura).
2. Lee `claude/02-MEMORIA.md` (estado actual y siguiente paso), `claude/02b-MEMORIA-DECISIONES-2.md`, `claude/03-REGLAS-DE-ORO.md` y `claude/12-DECISIONES-PENDIENTES.md`.
3. Lee la memoria de la fase en curso (`claude/02c` a `claude/02i`) y el documento del área (`claude/00-INDICE.md`).
4. Las fases están en `claude/10-FASES-DE-TRABAJO.md` (hoja de ruta y reglas comunes) y `10b` a `10f`. Las ideas de cada fase son las filas de `claude/11b` a `claude/11f` con su número en la columna "Fase"; al cerrar una fase se marcan "Hecha".

## Acuerdos con el autor (no están en otros archivos)

- Al cerrar una fase con todo en verde se puede pasar a la siguiente sin pedir aprobación, salvo que la memoria diga que el autor pidió esperar (revisa en claude/02-MEMORIA.md qué fase está aprobada).
- `.env`: se puede configurar libremente, excepto `SMTP_USER` y `SMTP_PASSWORD`, que pone el autor. Nunca imprimas el `.env` ni secretos en la salida; carga variables con `node --env-file`.
- Despliegue (Vercel, Neon) y keystore de firma quedan para el final del proyecto.
- Versionado: repositorio público https://github.com/DavNat13/Abitus-D. Un commit convencional (en inglés) por tarea verificada, push inmediato y etiqueta `fase-N` al cerrar cada fase. Revisa que no haya secretos antes de cada push.
- Diseño aprobado por el autor (2026-11-22): seguir el estilo de `claude/05d-UI-DIRECCION-VISUAL.md` en toda pantalla nueva. Tarjetas por sección con icono (`SectionCard`, `ListItemCard`, `IconBadge`), etiquetas `InfoPill`, desplegables (`AppDropdown`, `AppMultiDropdown`) en filtros y formularios en lugar de nubes de chips, hojas inferiores para elegir, menú de tres puntos (`OverflowMenu`) para acciones secundarias y `ConfirmDialog` antes de eliminar. Componentes en `android/app/src/main/java/com/abitusd/app/core/designsystem/`.
- Idioma de la app: español por defecto; inglés solo si el usuario lo elige en Perfil. Los nombres de ejercicios pueden mostrarse en inglés por disciplina (preferencia del autor para calistenia).
- Todo texto para el autor va en español y breve: avances, cierres y preguntas. Solo código, identificadores y commits en inglés.
- Nunca pidas al autor pegar secretos en el chat: indícale dónde configurarlos.
- La app es de uso personal: sin modo entrenador con alumnos. Descartadas por el autor: usar la app sin cuenta y la ficha de emergencia (ver `claude/02-MEMORIA.md`).
- Encabezado de cada pantalla: solo el nombre y el botón de información que abre "Cómo usar".
- Ahorro: salidas de comandos filtradas, sin releer archivos ni narrar de más.

## Comandos (Windows, Git Bash o PowerShell)

- Android: `cd android` y `JAVA_HOME="C:\Program Files\Java\jdk-22"`, luego `./gradlew ktlintFormat`, `./gradlew detekt ktlintCheck testDebugUnitTest assembleDebug`.
- Backend: `cd backend`, `npm test` (PostgreSQL en Docker, contenedor `abitus-d-postgres-1`), `npm run dev` en el puerto 3000.
- Scripts obligatorios antes de cada commit: `node tools/check-file-length.mjs` (150 líneas máximo), `node tools/check-no-emoji.mjs`, `node tools/check-contrast.mjs` si cambian colores.
- Iconos nuevos: SVG de Material Symbols Outlined convertidos con `node tools/convert-material-symbol.mjs <dir-svg> android/app/src/main/res/drawable` y expuestos en `AppIcons`.
- Emulador: AVD `abitusd_api29`; la app llega al backend en `http://10.0.2.2:3000/`.

## Al terminar cada tarea

Actualiza `claude/02-MEMORIA.md` (estado, decisiones, hechos verificados) y la memoria de la fase, como indica `CLAUDE.md`. Así el siguiente agente, sea Codex o Claude, retoma sin perder contexto.

## Límites de alcance (pedido del autor, 2026-11-25)

- Haz solo lo que pide la fase en curso según `claude/10c` a `10e` y sus filas de los bancos. Nada de funciones, pantallas, refactorizaciones ni limpiezas que no estén en la fase; si ves algo útil, anótalo en `claude/02-MEMORIA.md` como pendiente.
- No agregues dependencias nuevas sin verificar su versión y licencia en la fuente oficial y registrarlo en la memoria; no actualices las existentes salvo que la fase lo exija.
- No cambies la configuración de herramientas (Gradle, Detekt, ktlint, ESLint, tsconfig, Docker) ni los scripts de `tools/` salvo que la fase lo exija; nunca relajes una regla de lint o una prueba para que pase.
- No borres ni reescribas pruebas existentes para que pasen; corrige el código.
- No toques `AGENTS.md`, `CLAUDE.md`, `opencode.json`, `legal/` ni `claude/03-REGLAS-DE-ORO.md`. En `distribution/` solo puedes editar `THIRD_PARTY.md` (atribuciones y licencias de terceros).
- Git: solo `add`, `commit`, `push` normal y `tag` nuevo. Nunca reescribas historial, fuerces un push, borres ramas o etiquetas, ni toques el `.env`.
- Fuera de `C:\Abitus-D` solo usa el SDK de Android, el JDK y las cachés de Gradle y del emulador.

## Autonomía (pedido del autor, 2026-11-26)

- Trabaja de corrido: después de cada commit, sigue con la tarea siguiente sin escribirme. No me pidas confirmar lo que ya está decidido en los documentos o en mis mensajes.
- Si dos documentos se contradicen, aplica la decisión más reciente del autor (por fecha en `claude/02-MEMORIA.md` o en su último mensaje), corrige el documento viejo y sigue.
- Si una restricción de este archivo choca con una tarea que el autor pidió, anótalo en `claude/02-MEMORIA.md` como pendiente para el autor, avanza con todo lo demás y vuelve a eso al final.
- Solo detente por: una decisión de producto, costo, privacidad o licencia que no esté escrita; un permiso que opencode te pida; o un bloqueo que no puedas resolver. Si te detienes, una línea con lo que falta.
