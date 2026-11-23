# 10b - Fases de trabajo: 2, 3 y 4

Continuación de `10-FASES-DE-TRABAJO.md`. Mismas reglas. Las ideas citadas están en `11b` a `11e`.

## Fase 2 - Biblioteca de ejercicios

Objetivo: catálogo real, buscable, traducido y con licencias claras.

Entregables:
- Migraciones del catálogo y cargador de semillas idempotente.
- `tools/import-exercises` y semillas v1 (es y en) según `08c` y `08d`; `distribution/THIRD_PARTY.md` con fuentes y atribución; archivo de revisión para dudas.
- Relaciones de alternativas, progresiones y regresiones entre ejercicios (base de la sustitución al vuelo).
- Endpoint de catálogo incremental con ETag.
- Android: descarga y almacenamiento en Room, biblioteca con búsqueda y filtros (disciplina, músculo, equipamiento, dificultad), detalle técnico en texto, ejercicios propios (crear, editar, borrar) y detección de duplicados.

Ideas incluidas: tutoriales de técnica en texto, búsqueda en el catálogo, detección de duplicados.

Criterios de aceptación:
- La biblioteca funciona sin conexión tras la primera descarga.
- Cambiar el idioma cambia nombres y detalles de los ejercicios disponibles.
- Un ejercicio propio creado sin conexión se sincroniza después.
- Muestra revisada por una persona por disciplina, sin errores técnicos graves.
- Cada ejercicio importado tiene `source` y `license`.

## Fase 3 - Planificación

Objetivo: rutinas, programas y calendario cómodos y flexibles.

Entregables:
- Migraciones y sincronización de rutinas, ejercicios planificados, programas, días y entrenamientos programados.
- Android: crear y editar rutinas (series, rango de repeticiones, carga, RPE o RIR, descanso, superseries y circuitos), duplicar, guardar una sesión como plantilla, carpetas y archivo, programas por semanas, calendario mensual y semanal.

Ideas incluidas:
- Plantillas de programas básicos (3 días, empuje-tracción-pierna).
- Perfiles de equipo (gimnasio, casa, parque) que filtran el catálogo.
- Rutina express de 20 minutos y rutina en casa sin equipo.
- Reprogramar sesión perdida, arrastrar para reorganizar la semana y exportar al calendario (ICS).
- Carga estimada del día, descanso activo y volumen planificado por músculo.

Criterios de aceptación:
- Se planifica una semana completa sin conexión y se ve en otro dispositivo tras sincronizar.
- Duplicar una rutina no duplica referencias ni rompe el orden.
- Reprogramar una sesión mantiene su historial.
- Estados vacíos y errores cubiertos.

## Fase 4 - Registro de entrenamiento

Ampliada y dividida en Fase 4 y Fase 4b el 2026-11-23: el detalle vigente está en `10f-FASE-4-Y-4B.md`, que manda sobre esta sección.

Objetivo: el corazón de la app, rápido y a prueba de fallos.

Entregables:
- Sesión activa según `05-UI-UX.md`: iniciar desde el plan o libre, series con carga, repeticiones, RPE o RIR, notas, "última vez", agregar y reordenar ejercicios, superseries.
- Motor de timers (servicio en primer plano, marcas de reloj monótono, recuperación tras cierre; reglas de `11e`) y descanso automático; la interfaz completa del reloj llega en la Fase 5.
- Resumen de sesión con volumen, duración y PRs.
- Recuperación de sesión tras cierre del proceso.
- Sincronización completa de sesiones y series (`09b`).

Ideas incluidas:
- Calculadora de discos y series de aproximación sugeridas.
- Deshacer última serie, copiar la sesión anterior y teclado numérico propio.
- Aviso de serie atípica, serie fallida y tipos de serie especiales (drop set, AMRAP, al fallo).
- Series por lado, registro de tempo y equipo y ajustes recordados.
- Notas con etiquetas, molestia por zona en lista, lista previa a la sesión.
- Sustitución de ejercicio al vuelo y modo manos mojadas.
- Pantalla de bloqueo con la serie actual y notificación de descanso con controles.

Criterios de aceptación:
- Confirmar una serie responde en menos de 100 ms y arranca el descanso automático.
- El descanso suena con pantalla apagada y la app en segundo plano.
- Una sesión registrada sin conexión llega completa al servidor sin duplicados.
- Matar la app a mitad de sesión no pierde series confirmadas.
- Deshacer y rehacer funcionan también sin conexión.
