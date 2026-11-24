# 02h - Memoria: Fase 4 (registro de entrenamiento), 2026-11-23

Complemento de `02-MEMORIA.md`. Plan acordado en `10f-FASE-4-Y-4B.md`.

## Hecho y verificado
- Servidor: migración 013 (`workout_sessions`, `session_exercises`, `sets`, `program_days.active_rest`), sincronización genérica; 59 pruebas en verde.
- Room v4 con migración automática, entidades de sesión, estado de sesión activa local (`active_session`) y adaptadores de sincronización.
- Dominio con pruebas: SessionFactory, SessionStats (volumen sin calentamientos, récords por 1RM de Epley hasta 12 repeticiones), PlateCalculator, UndoStack.
- Pantallas: tarjeta "Sesión de hoy" en Entrenar, Entrenamiento libre, Previa (ejercicios, tiempo estimado, sueño y energía), Sesión activa (un ejercicio por página, franjas por serie, hoja de edición con pasos de 2,5 kg y 1 repetición, RPE y tipo de serie, deshacer y rehacer, mapa de la sesión, calculadora de discos), barra de descanso, Resumen (duración, volumen, series, récords, RPE, notas, guardar, guardar como plantilla, descartar) e Historial.
- En emulador: iniciar la sesión de hoy, editar carga, confirmar serie (verde), descanso con notificación y recuperación de la sesión tras cerrar el proceso.

## Criterios medidos (2026-11-23, emulador API 29, versión de depuración)
- Confirmar serie: 3 a 15 ms de escritura en Room (registro AbitusPerf), por debajo de 100 ms.
- Sesión registrada sin conexión: al volver la red llegó completa al servidor (4 ejercicios, 3 series confirmadas), sin duplicados (claves UUID del cliente y upsert).
- Cierre forzado a mitad de sesión: la sesión y sus series confirmadas se recuperan al abrir la app.
- Deshacer y rehacer: locales, funcionan sin conexión.
- Descanso con pantalla apagada: alarma exacta RTC_WAKEUP programada; con la pantalla apagada (mWakefulness=Asleep) la notificación "Descanso terminado" se publicó a los 140 s en el canal de prioridad alta con sonido y vibración (2026-11-24). En un teléfono real se comprobará cuando haya servidor desplegado (la versión de depuración apunta al emulador).
- Hallazgo: la base de desarrollo necesitaba `npm run migrate` en database/ tras agregar la 013; sin eso el servidor respondía 500 al sincronizar.

## Decisiones
- Descanso sin servicio en primer plano: alarma exacta (`AlarmManagerCompat.setExactAndAllowWhileIdle`) más notificación con cuenta regresiva del sistema y acciones +15 s y Saltar. Permisos: `USE_EXACT_ALARM` (concedido a apps de temporizador) y `POST_NOTIFICATIONS` pedido en la Previa. Motivo, según developer.android.com (2026-11-23): `health` exige permisos de sensores, `shortService` se corta a los 3 minutos y `specialUse` pasa por revisión de Play.
- El fin del descanso se guarda como hora de reloj en `active_session` para sobrevivir al cierre del proceso.
- Encabezados: solo el nombre de la pantalla y un botón de información (círculo con signo de exclamación, Material Symbols) que abre una hoja explicando cada elemento (InfoButton, strings_info.xml). Pedido del autor el 2026-11-23.
- Hecho además: descanso activo en programas, calendario e Inicio; sesión perdida (hacerla hoy, mover el plan o saltarla); repetir una sesión del historial.
- Diseño de series rehecho a pedido del autor: nada de campos pequeños en la lista; franjas grandes y edición en hoja inferior con botones de 56 dp.

## Cerrado el 2026-11-24 (etiqueta fase-4)
- Sesión sin terminar: antes de empezar otra se ofrece continuarla, guardarla o descartarla (PreviousSession); repetir una sesión guarda la anterior.
- Botón de información en todas las pantallas principales y editores (WorkoutInfo, EditorInfo).
- Tarjeta "Sesión en curso" con el nombre de la sesión; Inicio marca como hecha la sesión de hoy.
- Volumen planificado por músculo en barras (VolumeBars) en el editor de rutina y el calendario.
- Herramientas por ejercicio: series de calentamiento al 50 y 70 por ciento (WarmupPlan, con prueba), cambiar por un ejercicio relacionado del catálogo y molestia por zona.
- Verificado en emulador: aviso de sesión sin terminar, cambio de ejercicio persistido y fin del descanso con pantalla apagada.

## Para más adelante
- Probar en un teléfono real (después del despliegue).
- lintDebug sigue marcando local.properties (PropertyEscape) aunque está escapado; revisar en la Fase 10.
- Volumen en kg de series con peso corporal (hoy cuenta 0 kg); decidir en la Fase 6 (analítica).
