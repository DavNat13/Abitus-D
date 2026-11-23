# Abitus-D - Instrucciones para agentes (Codex y otros)

Este archivo es para cualquier agente distinto de Claude Code. Las reglas del proyecto son las mismas: `CLAUDE.md` y la carpeta `claude/` mandan. No dupliques aquí lo que ya dicen; léelos.

## Antes de tocar código

1. Lee `CLAUDE.md` completo (reglas innegociables, idioma, estructura).
2. Lee `claude/02-MEMORIA.md` (estado actual y siguiente paso), `claude/02b-MEMORIA-DECISIONES-2.md`, `claude/03-REGLAS-DE-ORO.md` y `claude/12-DECISIONES-PENDIENTES.md`.
3. Lee la memoria de la fase en curso (`claude/02c` a `claude/02g`) y el documento del área (`claude/00-INDICE.md`).
4. Las fases están en `claude/10-FASES-DE-TRABAJO.md` y `claude/10b-FASES-DE-TRABAJO-2.md`.

## Acuerdos con el autor (no están en otros archivos)

- Al cerrar una fase con todo en verde se puede pasar a la siguiente sin pedir aprobación, salvo que la memoria diga que el autor pidió esperar (revisa en claude/02-MEMORIA.md qué fase está aprobada).
- `.env`: se puede configurar libremente, excepto `SMTP_USER` y `SMTP_PASSWORD`, que pone el autor. Nunca imprimas el `.env` ni secretos en la salida; carga variables con `node --env-file`.
- Despliegue (Vercel, Neon) y keystore de firma quedan para el final del proyecto.
- Versionado: repositorio público https://github.com/DavNat13/Abitus-D. Un commit convencional (en inglés) por tarea verificada, push inmediato y etiqueta `fase-N` al cerrar cada fase. Revisa que no haya secretos antes de cada push.
- Diseño aprobado por el autor (2026-11-22): seguir el estilo de `claude/05d-UI-DIRECCION-VISUAL.md` en toda pantalla nueva. Tarjetas por sección con icono (`SectionCard`, `ListItemCard`, `IconBadge`), etiquetas `InfoPill`, desplegables (`AppDropdown`, `AppMultiDropdown`) en filtros y formularios en lugar de nubes de chips, hojas inferiores para elegir, menú de tres puntos (`OverflowMenu`) para acciones secundarias y `ConfirmDialog` antes de eliminar. Componentes en `android/app/src/main/java/com/abitusd/app/core/designsystem/`.
- Idioma de la app: español por defecto; inglés solo si el usuario lo elige en Perfil. Los nombres de ejercicios pueden mostrarse en inglés por disciplina (preferencia del autor para calistenia).
- Mensajes al autor breves y en español.

## Comandos (Windows, Git Bash o PowerShell)

- Android: `cd android` y `JAVA_HOME="C:\Program Files\Java\jdk-22"`, luego `./gradlew ktlintFormat`, `./gradlew detekt ktlintCheck testDebugUnitTest assembleDebug`.
- Backend: `cd backend`, `npm test` (PostgreSQL en Docker, contenedor `abitus-d-postgres-1`), `npm run dev` en el puerto 3000.
- Scripts obligatorios antes de cada commit: `node tools/check-file-length.mjs` (150 líneas máximo), `node tools/check-no-emoji.mjs`, `node tools/check-contrast.mjs` si cambian colores.
- Iconos nuevos: SVG de Material Symbols Outlined convertidos con `node tools/convert-material-symbol.mjs <dir-svg> android/app/src/main/res/drawable` y expuestos en `AppIcons`.
- Emulador: AVD `abitusd_api29`; la app llega al backend en `http://10.0.2.2:3000/`.

## Al terminar cada tarea

Actualiza `claude/02-MEMORIA.md` (estado, decisiones, hechos verificados) y la memoria de la fase, como indica `CLAUDE.md`. Así el siguiente agente, sea Codex o Claude, retoma sin perder contexto.
