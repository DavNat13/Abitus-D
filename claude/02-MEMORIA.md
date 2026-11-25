# 02 - Memoria del proyecto

Archivo vivo. Claude Code lo lee al iniciar cada sesión y lo actualiza al terminar cualquier tarea relevante. Mantén las entradas cortas, fechadas y con el motivo.

## Estado actual

- Fecha de creación de la documentación: 2026-11-22.
- Fase actual: Fase 4b cerrada (fase-4b, 2026-11-24; detalle en `02i-MEMORIA-FASE-4B.md`). Fase 4 cerrada (fase-4, `02h`). Siguiente: Fase 5 según `10c`.
- Repositorio remoto: https://github.com/DavNat13/Abitus-D (público). Versionado: un commit convencional por tarea verificada, push tras cada tarea y etiqueta `fase-N` al cerrar cada fase. Al ser público, revisar secretos antes de cada push.
- Código: autenticación completa (backend, base de datos y app Android); detalle y decisiones de la Fase 1 en `02d-MEMORIA-FASE-1.md`. Verificaciones externas en `02c-MEMORIA-VERIFICACIONES.md`.
- Siguiente paso: Fase 5 según `10c` (reloj núcleo y ejercicios favoritos). Fases 5 a 13 alineadas con los bancos de ideas el 2026-11-24: cada fila de `11b` a `11e` tiene su columna "Fase".
- Pedido del autor para la Fase 4 (2026-11-23): en programas y calendario, cada día se elige como entrenamiento, descanso o descanso activo (hoy solo existe descanso con un aviso). Requiere un tipo de día en program_days (migración en servidor y Room, sincronización), el selector en el editor de programas y que Inicio y Calendario muestren el descanso activo con su sugerencia. Pendiente al final: Vercel, Neon, keystore.

Fase 0: tareas 1 a 7 hechas y verificadas (estructura, verificaciones, scripts, backend, database, Docker, Android en emulador API 29); tarea 8 (despliegue en Vercel) diferida al final. Detalle en el historial de Git.

Fase 1: tareas 1 a 9 hechas y verificadas (etiqueta `fase-1`); detalle en `02d-MEMORIA-FASE-1.md`.

Fase 2: tareas 1 a 9 hechas y verificadas (etiqueta `fase-2`); detalle en `02e-MEMORIA-FASE-2.md`.

## Plan de la Fase 3 (2026-11-22)

| N | Tarea | Estado |
|---|---|---|
| 1 | Migración 012 de planificación y sincronización genérica por tabla en el backend | Hecha |
| 2 | Room v3 con las tablas de planificación y motor de sincronización genérico | Hecha |
| 3 | Repositorios de rutinas, programas y calendario; plantillas; volumen planificado | Hecha |
| 4 | Pestaña Entrenar: rutinas (editor, carpetas, duplicar, archivar), programas, calendario, ICS | Hecha |
| 5 | Perfiles de equipo que filtran la biblioteca | Hecha |
| 6 | Verificación sin conexión en emulador, memoria y etiqueta `fase-3` | Hecha |

Detalle y aplazamientos en `02f-MEMORIA-FASE-3.md`.

Nota: la ronda 2 de decisiones (nombre Abitus-D, hosting sin tarjeta, correo, edad, unidades) está en `02b-MEMORIA-DECISIONES-2.md` y prevalece sobre lo que aquí contradiga.

## Decisiones tomadas

| Fecha | Decisión | Motivo |
|---|---|---|
| 2026-11-24 | Uso personal: sin modo entrenador con alumnos; onboarding y perfil del atleta al inicio de la Fase 5 | Pedido del autor; las ideas de preparador se aplican al propio atleta (banco 11f) |
| 2026-11-22 | Android nativo (Kotlin, Jetpack Compose) como producto principal | Timers en segundo plano, GPS, Health Connect y calidad de interfaz |
| 2026-11-22 | Versión mínima de Android 10 (API 29) | Decisión del autor |
| 2026-11-22 | Offline-first con Room y sincronización incremental | Uso en gimnasio con mala señal; varios dispositivos |
| 2026-11-22 | Backend monolítico modular en TypeScript con Fastify | Simplicidad, buen ecosistema de validación y plantillas de correo |
| 2026-11-22 | PostgreSQL como base de datos | Relacional, índices y consultas analíticas |
| 2026-11-22 | Hosting: Vercel Hobby con base de datos en Neon (São Paulo). Reemplaza a Cloud Run | Sin tarjeta; ver `14-INFRAESTRUCTURA.md` |
| 2026-11-22 | Correos por SMTP configurable (Nodemailer) con Gmail y app.abitus.d@gmail.com | Sin dominio ni tarjeta; Brevo diferido |
| 2026-11-22 | Backend portable: contenedor Docker y PostgreSQL estándar | Poder migrar de proveedor sin reescribir |
| 2026-11-22 | Solo Android. Sin iPhone, sin PWA | Decisión del autor; foco en calidad nativa |
| 2026-11-22 | Cuerpo 3D: primero mapa corporal 2D como base y respaldo; luego 3D con Three.js en WebView | Riesgo de conseguir un modelo con músculos separados y licencia adecuada |
| 2026-11-22 | Estimación de 1RM por defecto: Epley, configurable (Brzycki) | Fórmulas estándar |
| 2026-11-22 | Paleta de marca del autor: #101632, #001667, #0135A8, #547F43, #F7F4FF. Fondo blanco, tema claro por defecto, tema oscuro opcional derivado | Decisión del autor; contrastes calculados (green sobre lavender no pasa AA para texto: se usa greenText #456B35). Error y warning se añaden por necesidad semántica |
| 2026-11-22 | Tipografía: Roboto (400, 500, 700), incluida en la app | Nativa de Android, legible y sobria; elegida por el autor |
| 2026-11-22 | Estilo visual: tarjetas modernas con borde fino y sombras tintadas con ink, botones con estados, animaciones breves que respetan la configuración de accesibilidad | Decisión del autor; detalle en `05c` |
| 2026-11-22 | Máximo 150 líneas por archivo (código y documentación); dividir por responsabilidad, sin duplicar | Decisión del autor; facilita que Claude Code no se pierda y mantiene el código legible |
| 2026-11-22 | Analítica calculada en el dispositivo con consultas sobre Room | Funciona sin conexión y es rápida; el servidor replica las consultas para la futura PWA |
| 2026-11-22 | Gestor de paquetes: npm (incluido con Node), un proyecto por capa | Sin herramientas extra; Vercel lo detecta sin configuración |
| 2026-11-22 | Node 22.x en local y Vercel; subir a 24 LTS antes de la Fase 10 | Es el instalado; 22 termina soporte en abril de 2027 |
| 2026-11-22 | Vercel con Fastify sin configuración (`src/server.ts` con `listen`); sin adaptador en `src/adapters/vercel` | Lo indica la guía oficial; el mismo archivo sirve en local y en Docker. Ajusta `14` |
| 2026-11-22 | Migraciones con script propio `database/scripts/migrate.mjs` (orden, transacción, checksum, candado) | Requisitos mínimos; evita una dependencia más |
| 2026-11-22 | TypeScript 6.0 y no 7.0 | typescript-eslint 8.73 no soporta 7.0 |
| 2026-11-22 | Detekt 2.0.0-alpha.6 (herramienta, no va en el APK) | La estable 1.23.8 no soporta Kotlin 2.4 ni AGP 9; cambiar a 2.0 estable al salir |
| 2026-11-22 | PostgreSQL 17 en local | Igualar con Neon al crear el proyecto |
| 2026-11-22 | Autorización del autor: al cerrar una fase con todo en verde se pasa a la siguiente sin pedir aprobación. Claude configura el `.env` local; `SMTP_USER` y `SMTP_PASSWORD` (contraseña de aplicación de Gmail) los completa el autor a mano | Avance continuo; las credenciales de Gmail no pasan por el chat |
| 2026-11-22 | `.env` local apunta a Gmail SMTP (smtp.gmail.com, puerto 465, SSL); Mailpit queda comentado como alternativa | Probar el envío real cuando el autor ponga la contraseña |
| 2026-11-22 | Copia de seguridad del sistema desactivada (`allowBackup=false`, reglas de extracción excluyen todo) | Tokens y datos de salud no salen del dispositivo; el respaldo es la sincronización y la exportación |

## Decisiones descartadas y por qué

| Opción | Motivo del descarte |
|---|---|
| Usar la app sin cuenta y crearla después | No le gusta al autor (2026-11-24); la cuenta sigue siendo obligatoria desde el registro |
| Ficha de emergencia en el cardio | Riesgo de bromas o mal uso que perjudique al proyecto (decisión del autor, 2026-11-24) |
| Render (plan gratuito) | Se apaga tras 15 minutos sin tráfico y tarda hasta un minuto en reactivarse |
| Oracle Cloud Always Free | El autor lo rechazó; además redujo sus límites sin aviso en 2026 |
| Supabase (plan gratuito) | Se pausa tras una semana de inactividad; 500 MB; sin respaldos automáticos |
| Fly.io | Ya no tiene plan gratuito para usuarios nuevos |
| Resend como proveedor inicial | Tope de 100 correos por día en plan gratuito |
| Servidor SMTP propio | Puerto 25 bloqueado en la nube, IPs sin reputación y mala entrega; ver `15-CORREOS.md` |
| App única multiplataforma (Flutter, Capacitor, React Native) | Se prioriza calidad nativa en Android |

## Hechos verificados (2026-11-22)

Estos datos cambian. Antes de depender de ellos, VERIFICAR otra vez en la fuente.

- Oracle redujo los límites de las instancias Ampere A1 en cuentas gratuitas a 2 OCPU y 12 GB (InfoQ, julio 2026).
- Render gratuito: se apaga a los 15 minutos sin tráfico; reactivación de hasta un minuto; sistema de archivos efímero (docs de Render).
- Supabase gratuito: 500 MB por proyecto, proyectos se pausan tras una semana de inactividad, 50.000 usuarios activos mensuales (supabase.com/pricing).
- Neon gratuito: 100 proyectos, 1 GB de almacenamiento y 100 horas de cómputo por proyecto al mes, el cómputo se apaga a los 5 minutos de inactividad (neon.com/faqs/free-plan-limits-and-quotas).
- Cloud Run: descartado por exigir cuenta de facturación con tarjeta.
- Resend gratuito: 100 correos por día y 3.000 al mes, un dominio (resend.com/pricing).
- Brevo gratuito: 300 correos por día, sin tarjeta, pero requiere autenticar un dominio; diferido.
- Health Connect Jetpack: versión estable 1.1.0; hay 1.2.0 en alfa (developer.android.com/jetpack/androidx/releases/health-connect).

Verificaciones de la Fase 0 (Android fuera de Play, Vercel, Neon, Gmail y versiones): ver `02c-MEMORIA-VERIFICACIONES.md`; prevalecen sobre esta lista.

## Riesgos abiertos

1. Cambios en los límites gratuitos de Vercel o Neon. Mitigación: código portable y PostgreSQL estándar.
2. Correos de Gmail en spam o cuenta bloqueada por envío masivo. Mitigación: cola con topes y aviso a los amigos.
3. Modelo 3D con músculos separados y licencia adecuada. Mitigación: mapa 2D primero.
4. Tope de 1 GB en Neon gratuito. Mitigación: rutas GPS simplificadas y codificadas; derivados en caché mínimos.
5. Verificación de desarrolladores de Android: llega a todos los países durante 2027. Mitigación: cuenta gratuita de distribución limitada (50 dispositivos) antes de la Fase 10.
6. Detekt en alfa. Mitigación: solo afecta al análisis; pasar a 2.0 estable cuando salga.
7. Correos dentro de la solicitud alargan registro y login con dispositivo nuevo si el SMTP es lento. Mitigación: tiempo máximo de 8 s y cola; medir con Gmail real.
8. El relleno de 1,5 s contra enumeración no cubre un SMTP más lento que eso. Mitigación: medir con Gmail y ajustar en la Fase 10.

## Registro de progreso

Formato de cada entrada: `AAAA-MM-DD | área | qué se hizo | resultado de la verificación | siguiente paso`.

- 2026-11-22 | documentación | Creada la carpeta `claude/` y el archivo `CLAUDE.md` | Sin código aún | Cerrar decisiones pendientes y arrancar Fase 0
- 2026-11-22 | entorno | SDK de Android por línea de comandos (licencias aceptadas con autorización del autor), AVD `abitusd_api29` | Paquetes instalados | -
- 2026-11-22 | tools | check-no-emoji y check-file-length | Probados con casos positivos y negativos; pasan | -
- 2026-11-22 | backend y database | Fastify `/health` y `/ready`, migrador y 001 | tsc, ESLint, Prettier, 4 pruebas en verde; `/health` y `/ready` 200 contra PostgreSQL en Docker; imagen Docker responde `/health` | Desplegar en Vercel
- 2026-11-22 | android | Proyecto vacío AGP 9.5.1, Compose, Room 3, Navigation 3 | assembleDebug, prueba unitaria, Detekt y ktlint en verde; instalada y abierta en emulador Android 10 (API 29) | Fase 1 tras aprobación
- 2026-11-22 | database | Migraciones 002 a 006 de identidad y seguridad | Aplicadas sobre base limpia en cada corrida de pruebas | -
- 2026-11-22 | backend | Auth, sesiones, cuenta, correo, límites, trabajos programados | 41 pruebas en verde contra PostgreSQL; ESLint, Prettier, tsc; npm audit sin vulnerabilidades; correo real a Mailpit | -
- 2026-11-22 | android | Sistema de diseño, sesión cifrada, pantallas de acceso, barra inferior, política | 13 pruebas unitarias, Detekt y ktlint en verde; recorrido real en emulador API 29 (registro, código, verificación, reinicio con sesión, cierre, login, aviso de dispositivo nuevo) | Fase 2 tras aprobación
- 2026-11-22 | planificación | Fase 3 completa (backend, Room v3, pestaña Entrenar, perfiles de equipo) | 56 pruebas de backend y pruebas unitarias Android en verde; Detekt, ktlint, longitudes y emojis OK; en emulador sin red: plantilla, programa activado, reprogramación; al volver la red el servidor tiene 3 rutinas, 12 ejercicios, 1 programa, 12 días y 12 programados | Fase 4
- 2026-11-22 | diseño | tools/check-contrast sobre los tokens | 32 pares AA en ambos temas tras ajustar inputBorder oscuro a #777A8C | -
- 2026-11-23 | acceso | Bienvenida, registro, verificación, login y recuperación en tarjetas con icono (iniciado con Codex, cerrado por Claude) | Detekt, ktlint, pruebas unitarias y APK en verde; scripts OK | Inicio y Perfil
- 2026-11-23 | diseño | Inicio, Perfil, selector de tema y auditoría final de la Fase 3b | Detekt, ktlint, pruebas, APK, contraste, longitudes y emojis OK; texto al 200 % y tema oscuro revisados en emulador | Fase 4 con aprobación
- 2026-11-24 | registro | Fase 4 completa: sesiones con sincronización, sesión activa, descanso por alarma exacta, resumen, historial, descanso activo, sesión perdida, herramientas | 59 pruebas de servidor y pruebas Android en verde; confirmar serie 3 a 15 ms; sesión sin conexión llega sin duplicados; descanso suena con pantalla apagada | Fase 4b
- 2026-11-24 | planificación | Fase 4b completa: bloques y fases, progresión automática, descarga sugerida, semana de prueba, modo viaje, sustituciones por bloque, disponibilidad semanal, hora con recordatorio, plantillas de semana y objetivos por músculo | 61 pruebas de servidor y pruebas Android en verde; criterios medidos en emulador | Fase 5
- 2026-11-24 | documentación | Fases 5 a 13 alineadas con los bancos de ideas: columna Fase en 11b a 11e, subfases 5b, 6b, 7b y 11b, reglas comunes de diseño en 10, ideas nuevas y ejercicios favoritos (pedido del autor) en la Fase 5 | check-file-length y check-no-emoji en verde | Fase 5
- 2026-11-24 | documentación | Ronda 2 de ideas en 11f: glosario, estado de sincronización y calculadora de discos gráfica (5 y 5b), cumplimiento del plan (6b), condiciones de sesión (7), búsqueda global (9), pausas activas y cafeína (12); ficha de emergencia descartada | check-file-length y check-no-emoji en verde | Fase 5
- 2026-11-24 | documentación | Banco 11f (onboarding, perfil, acceso, ajustes, diseño y criterio deportivo) repartido en las fases 5 a 12 | check-file-length y check-no-emoji en verde | Fase 5

## Cómo actualizar este archivo

1. Al iniciar: leer "Estado actual" y "Registro de progreso".
2. Al terminar una tarea: agregar una línea al registro; si cambió el estado, actualizar "Estado actual".
3. Al decidir algo de arquitectura, producto o infraestructura: agregarlo a "Decisiones tomadas" con fecha y motivo.
4. Al descartar algo: moverlo a "Decisiones descartadas" con el motivo.
5. Al verificar un dato externo: agregarlo a "Hechos verificados" con fecha y fuente.
