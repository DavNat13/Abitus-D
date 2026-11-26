# 02l - Memoria de la Fase 6b (Analítica: comparar y hábitos)

Estado: plan aprobado por el autor el 2026-11-26; sin implementar. Contexto, decisiones, archivos y verificación en `02k-MEMORIA-FASE-6.md`.

### Datos
- **Migración 018 y Room 10:**
  - `tags` en sesiones (texto corto, como notas rápidas filtrables);
  - tabla `assessments` para las evaluaciones periódicas, con fecha y resultados de los test en JSON;
  - marca de "pausa" en el calendario para la racha tolerante (estado `paused` en los días programados, si encaja; si no, una tabla de pausas).
- **Reutilizados:** los resultados del test de campo de la 5b (`FieldTestCard`) y los isométricos (`ClockRecords`) alimentan las evaluaciones.

### Funciones
- **Comparar:**
  - semanas y bloques lado a lado (volumen, intensidad y récords);
  - dos sesiones del mismo entrenamiento;
  - resumen semanal y mensual con los músculos descuidados.
- **Hábitos:**
  - calendario anual tipo mapa de calor (`HeatGrid`);
  - rachas, incluida la tolerante;
  - objetivos semanales;
  - frecuencia por músculo;
  - "Qué entrenar hoy" según lo descuidado, con su dato.
- **Equilibrio:**
  - empuje contra tracción y tren superior contra inferior;
  - alertas de desbalance (pecho contra espalda, cuádriceps contra isquiotibiales), siempre con las cifras de ambos lados.
- **Análisis:**
  - volumen por patrón de movimiento y tonelaje semanal;
  - distribución de intensidad (series cerca del fallo según RPE y RIR);
  - curva fuerza-repeticiones;
  - eficiencia (progreso por hora) y densidad (volumen por minuto);
  - mapa por hora y mejor día y hora.
- **Búsqueda en el historial** por ejercicio, nota, fecha, molestia o etiqueta, con índices en Room y en menos de 300 ms.
- **Carga interna:**
  - sRPE de cada sesión (RPE por minutos);
  - carga semanal, monotonía (media dividida por la desviación estándar diaria) y tensión (carga por monotonía).
- **Evaluaciones periódicas:**
  - batería cada 4 a 8 semanas (test de campo, isométricos, 1RM estimado de básicos, peso y perímetros);
  - comparación con la evaluación anterior y recordatorio de la siguiente.
- **Cumplimiento del plan:** sesiones y series planificadas contra hechas, por semana y por bloque, con `BlockRepository` y `CalendarPlanning`.

### Criterios de la Fase 6b
- Cada comparación y cada alerta muestra las cifras de ambos lados.
- La búsqueda responde en menos de 300 ms con 2 años de datos, medido en logcat.
- **Cierre:** las filas de los bancos marcadas como "Hecha", la memoria `02k`, el estado "Siguiente: Fase 7" y la etiqueta `fase-6b`.

