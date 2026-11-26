# 02k - Memoria de las Fases 6 y 6b (Analítica)

Estado: plan aprobado por el autor el 2026-11-26; sin implementar. Al cerrar cada subfase se agregan aquí los criterios medidos.

## Contexto
La pestaña Progreso existe, pero está vacía. Ya hay datos suficientes para analizar:
- sesiones, series (carga, repeticiones, lastre, duración, RPE de sesión) y ejercicios con su contribución por músculo;
- bloques, plan semanal y perfil del atleta.

Las Fases 6 y 6b responden tres preguntas, siempre mostrando el dato que respalda cada respuesta:
- "¿estoy progresando?";
- "¿entreno suficiente?";
- "¿qué descuido?".

Alcance: todas las filas con "6" o "6b" en los bancos `11b` a `11f`, más los entregables de `10c`. Fuente de las fórmulas: `09d`.

Decisiones del autor (2026-11-26):
- **Gráficos:** propios con Compose (sin librería), igual que el `VolumeBars` que ya existe.
- **Mapa corporal 2D:** ilustración `VVGowda/muscle-svg` (MIT, con atribución), elegida por el autor el 2026-11-26 tras rechazar el dibujo propio; frente y espalda, agrupada en los 21 grupos del catálogo.
- **Ajustes del análisis:** fórmula de 1RM, umbrales y ventana del veredicto, rango de series por músculo. Van en una tarjeta dentro de Progreso y pasan a Ajustes en la Fase 9.
- **Orden:** 6 y 6b en un mismo plan, con las etiquetas `fase-6` y `fase-6b`.

## Fase 6 - Núcleo

### 1. Datos
- **Backend y migración 017:** tabla `body_measurements`, sincronizada con el mismo patrón genérico que `athlete-tables.ts` y `clock-tables.ts`.
  - Campos: fecha, peso (kg), cintura, pecho, brazo, muslo y cadera (m), y nota.
- **Room 9:** entidad y DAO con su `AutoMigration(8→9)` y su adaptador de sincronización.
  - Se copian `core/database/clock/*` y `core/sync/ClockSyncAdapters.kt`, que siguen el patrón de `PlanningSyncAdapter`.
- **Sin tablas de agregados:** los PRs y el volumen se calculan con consultas en Room. Si la prueba de fluidez falla, se agrega una caché local (`muscle_volume_daily`).

### 2. Dominio puro y probado (`feature/progress/domain`)
- **`OneRepMax`:** Epley y Brzycki, solo series de 1 a 10 repeticiones, sin calentamientos.
  - Con peso corporal y lastre: (peso del atleta + lastre) × factor. El peso sale de la medida más reciente o del perfil.
- **`Verdict`:**
  - mejor e1RM por sesión en las últimas 6 semanas, con mínimo 3 puntos;
  - pendiente Theil-Sen expresada como % cada 4 semanas;
  - umbrales de +1 % y -1 %, configurables;
  - texto con el dato que lo sustenta, por ejemplo "e1RM de 92 a 97 kg en 6 semanas";
  - para ejercicios con peso corporal o por tiempo usa las mejores repeticiones o el mejor tiempo.
- **`PersonalRecords`:** por ejercicio, la mayor carga, el mejor e1RM, el máximo de repeticiones y la mayor duración.
  - Incluye el récord por variante de calistenia y el top 10 de series.
- **`Stagnation`:** ejercicios estancados o en retroceso, con las semanas sin récord.
- **`TrainingAge`:** nivel y edad de entrenamiento según la primera sesión, la frecuencia y la fuerza relativa, mostrando el dato que los respalda.
- **`BodyTrends`:**
  - media móvil del peso;
  - ritmo semanal hacia el objetivo, con aviso si supera un ritmo razonable;
  - IMC y cintura-altura, con un aviso de sus límites;
  - fuerza relativa (e1RM dividido por el peso corporal) a lo largo del tiempo.
- **Volumen y suficiencia:** se reutiliza `feature/train/data/VolumeCalculator.kt` (`byMuscle`) y `MuscleVolume`. El estado "bajo", "en rango" o "alto" se calcula con el rango configurable.
- **Pruebas unitarias** con datos conocidos y cifras exactas: ese es el criterio de aceptación.

### 3. Pantalla Progreso (`feature/progress/ui`)
- **Estilo aprobado:** tarjetas con icono, desplegables y hojas inferiores, más un selector segmentado para las subsecciones Resumen, Ejercicios, Cuerpo y Medidas.
- **Resumen:**
  - sesiones, volumen, duración y constancia de la semana, comparadas con la anterior;
  - récords destacados;
  - ejercicios estancados;
  - "Sin datos suficientes" cuando corresponda.
- **Ejercicios:**
  - un desplegable para elegir el ejercicio;
  - gráfico de línea del e1RM o de la carga, con rangos de 4 semanas, 3 meses, 1 año o todo;
  - el veredicto con su explicación;
  - el top 10 de series y los récords.
- **Cuerpo, con el mapa 2D:**
  - componente `BodyMap`: frente y espalda dibujados con `Path` en Compose, una región por cada `svgKey` del catálogo (`MuscleGroupEntity`);
  - escala de color por series semanales promedio, con una leyenda que no depende solo del color;
  - selector de periodo: semana, mes o año;
  - al tocar un músculo se abre una hoja con sus series, los ejercicios que lo trabajaron y la comparación con el periodo anterior;
  - calidad visual: proporciones anatómicas, contorno con sombreado suave, músculos con forma propia, modo claro y oscuro, y legible a 200 %.
- **Historial con calendario:** se integra el calendario mensual que ya existe en `feature/workout/ui/history/HistoryScreen.kt`.
- **Medidas:**
  - registrar el peso y los perímetros;
  - gráficos de tendencia con el objetivo;
  - índices opcionales con su aviso;
  - gráfico de fuerza contra peso.
- **Perfil:** suma los récords destacados, y el nivel y la edad de entrenamiento con su dato.
- **Componentes de gráficos propios** en `core/designsystem/chart/` (`LineChart`, `BarChart`, `HeatGrid`):
  - accesibles: descripción del contenido y valores al tocar;
  - respetan "reducir movimiento".
- **Paleta de datos accesible para daltonismo:** tokens nuevos en el tema, verificados con `tools/check-contrast.mjs` y `05b`.
- **Celebración sobria de récord:** animación breve en el resumen de la sesión, sin emojis y sin animación si "reducir movimiento" está activo.
- **Tarjeta "Ajustes del análisis":** fórmula de 1RM, umbrales, ventana y rango de series. Al cambiarla, las vistas se recalculan solas.
- **Textos:** todo en recursos `es` y `en`; hojas "Cómo usar" en cada subsección y estados vacíos con el siguiente paso.

### Criterios de la Fase 6 (medidos en el emulador)
- **Valores exactos** con datos de prueba conocidos: pruebas unitarias y un historial cargado en el emulador.
- **Fluidez con 2 años de datos:** un script de semillas en `tools/` genera alrededor de 300 sesiones. Abrir Progreso y cambiar de rango sin tirones; se mide el tiempo de las consultas en logcat.
- **Veredicto:** siempre con su dato, o "Sin datos suficientes".
- **Ajustes:** cambiar la fórmula o los umbrales recalcula las vistas.
- **Cierre:** memoria `02k` y etiqueta `fase-6`.

## Fase 6b

Plan en `02l-MEMORIA-FASE-6B.md`.

## Archivos principales
- **Nuevos:**
  - `database/migrations/017_body_measurements.sql` y `018_tags_assessments.sql`;
  - `backend/src/modules/sync/progress-tables.ts` con su prueba;
  - en Android, `core/database/progress/*`, `core/sync/ProgressSyncAdapters.kt`, `feature/progress/{domain,data,ui}/*` y `core/designsystem/chart/*`.
- **Modificados:**
  - `AbitusDatabase.kt` (versiones 9 y 10);
  - `MainShell.kt` (la pestaña Progreso deja de estar vacía);
  - `ProfileScreen.kt` (récords y nivel);
  - `SummaryScreen.kt` (celebración de récord);
  - `HistoryScreen.kt` (búsqueda y etiquetas);
  - `WorkoutInfo` y `EditorInfo` (hojas de ayuda);
  - documentos `claude/` (02, 02k, 00, 10c y bancos).

## Verificación
- **En cada tarea:**
  - backend: `npm test`;
  - Android: `./gradlew detekt ktlintCheck testDebugUnitTest lintDebug assembleDebug`;
  - `check-file-length`, `check-no-emoji` y `check-contrast` si cambian los colores.
- **En el emulador:**
  - cargar el historial sintético de 2 años;
  - revisar cada subsección con capturas: el mapa 2D en claro y en oscuro, y el texto al 200 %;
  - medir los tiempos de consulta;
  - comprobar que la sincronización sin conexión de las medidas pasa `dirty` de 1 a 0.
- **Git:** un commit por tarea con push, y las etiquetas `fase-6` y `fase-6b` al cerrar.
