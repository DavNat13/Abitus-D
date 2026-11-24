# 02i - Memoria: Fase 4b (planificación avanzada), 2026-11-24

Complemento de `02-MEMORIA.md`. Plan acordado en `10f-FASE-4-Y-4B.md`.

## Hecho y verificado
- Servidor: migración 014 (`program_blocks`, `block_substitutions`, `muscle_targets`, `scheduled_workouts.planned_time`) con checks (semanas 1 a 8, min <= max, hora HH:mm); sincronización genérica; 61 pruebas en verde.
- Room v5 con migración automática 4 a 5 (hubo que declararla: sin ella la app se cerraba al abrir), DAO y adaptadores de sincronización; limpieza al cerrar sesión.
- Dominio con pruebas (`PeriodizationTest`): BlockPlan (qué bloque y semana toca), ProgressionRule (descarga series x 0,6 y carga x 0,85; porcentaje por semana; RPE +0,5 por semana hasta 10; doble progresión; carga tras bloque de prueba por Epley), WeekFit, DeloadAdvisor (RPE medio >= 9 en las 3 últimas sesiones), ExerciseSwap.
- Editor de programa por bloques: tarjetas por bloque, línea de tiempo por semana con color de fase, atajo "mesociclo de 4 semanas" (3 de acumulación con doble progresión de 2,5 kg y 1 de descarga), hoja de bloque (fase, objetivo, duración, progresión, incremento) y cambios de ejercicio por bloque.
- Entrenar: tarjeta "Semana N de M, fase" con la semana actual marcada y aviso de descarga sugerida.
- Previa: "Plan de hoy" con la semana del bloque; las series y cargas al empezar ya incluyen la progresión; modo viaje o lugar con el perfil de equipo de la biblioteca y aviso de los ejercicios sin alternativa.
- Calendario: hora planificada con recordatorio (alarma inexacta, sin permiso extra), disponibilidad semanal, guardar la semana como plantilla (programa de 1 semana), objetivos de series por músculo con franja en las barras.

## Criterios de aceptación medidos (emulador API 29)
- Mesociclo de 4 semanas con descarga: creado con el atajo; la línea muestra 3 semanas de acumulación y 1 de descarga.
- Progresión semana a semana: Entrenar y la Previa muestran "Semana 1 de 4, acumulación"; las cargas por semana están cubiertas por pruebas (100, 100, 105 y descarga 85 kg).
- Mover una semana por disponibilidad: entrenamientos del 24, 26 y 29 con días libres miércoles, viernes y sábado pasaron al 25, 27 y 28 en el mismo orden, con la fecha original guardada.
- Sin conexión: con wifi y datos apagados, un bloque nuevo quedó pendiente (dirty 1); al volver la red se sincronizó (dirty 0, serverSeq asignado).

## Decisiones
- Plantillas de semana como programas de 1 semana: aplicar una plantilla es activar ese programa; no hay tabla nueva.
- Modo viaje busca un nivel de relaciones (alternativas y regresiones). El catálogo no tiene opciones de parque para los básicos con barra; se avisa en vez de inventar cambios.
- Disponibilidad con menos días que entrenamientos: los sobrantes se juntan en el último día disponible (doble sesión).
- Recordatorio con `setAndAllowWhileIdle` (inexacto) para no pedir otro permiso.

## Pendiente
- Recordatorios: no se vuelven a programar tras reiniciar el teléfono (falta un receptor de BOOT_COMPLETED).
- Prueba en teléfono real junto con el despliegue (Vercel, Neon, keystore).
