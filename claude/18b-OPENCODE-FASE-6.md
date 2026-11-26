# 18b - Traspaso a opencode: Fases 6 y 6b

Documento para seguir las Fases 6 y 6b con opencode y modelos de OpenAI. Plan aprobado: `02k-MEMORIA-FASE-6.md` (Fase 6) y `02l-MEMORIA-FASE-6B.md` (Fase 6b). Reglas: `AGENTS.md`, `CLAUDE.md` y `03-REGLAS-DE-ORO.md`.

## Estado al 2026-11-26 (Claude)

Hecho, con commit y push (Detekt, ktlint, lint, pruebas y compilación en verde):
- **Dominio puro con pruebas** en `feature/progress/domain`:
  - `OneRepMax` (Epley y Brzycki, solo de 1 a 10 repeticiones);
  - `VerdictEngine` (Theil-Sen, % cada 4 semanas, mínimo 3 sesiones);
  - `ExerciseProgress` (puntos por sesión, récords, top 10, semanas sin récord; peso corporal más lastre en dominadas y fondos);
  - `BodyTrends` (media móvil, ritmo semanal robusto, semanas al objetivo, IMC, cintura-altura, fuerza relativa).
  - Pruebas: `ProgressDomainTest` y `BodyTrendsTest`.
- **Datos:**
  - `ProgressDao` (series de trabajo, ejercicios entrenados, totales por rango);
  - `AnalysisSettingsStore` (fórmula, ventana, umbral y rango de series; las vistas se recalculan solas);
  - `ProgressRepository` (usa el último peso medido o el del perfil);
  - `MeasurementRepository`.
- **Medidas sincronizadas:**
  - migración `017_body_measurements.sql`, `backend/src/modules/sync/progress-tables.ts` con su prueba (69 pruebas en verde);
  - Room 9 (`BodyMeasurementEntity`, `MeasurementDao`) y `ProgressSyncAdapters.kt`.
- **Pantalla Progreso** (`feature/progress/ui`), con cuatro pestañas:
  - Resumen: semana contra la anterior, ejercicios que progresan y estancados;
  - Ejercicios: gráfico `LineChart`, veredicto con su dato, récords y top 10;
  - Medidas: registro, tendencia, objetivo, índices con sus límites, fuerza relativa e historial;
  - Ajustes del análisis.
- **Gráfico propio:** `core/designsystem/chart/LineChart.kt` (sin librería, con valor al tocar).

## Rediseño obligatorio de Progreso (rechazado por el autor el 2026-11-26)

El autor calificó el diseño actual de Progreso como poco profesional y fuera del estilo del resto de la app. La lógica y los datos sirven; la interfaz se rehace antes de seguir:
- **Navegación:** las 5 pestañas no caben en el selector de píldora (los textos se cortan).
  - Dejar 3 o 4 secciones legibles; los ajustes del análisis van a un icono de engranaje en el encabezado que abre una hoja inferior.
  - Tomar como referencia las pestañas de Entrenar y su calendario.
- **Resumen:** reemplazar las líneas de texto por tarjetas de cifra grande (sesiones, series, volumen, minutos) con la variación contra la semana anterior y una flecha más palabra.
  - Los ejercicios que progresan y los estancados van en `ListItemCard` con `IconBadge` e `InfoPill` para el veredicto.
- **Series por músculo:** reemplazar el texto corrido por `VolumeBars`, que ya existe, con la franja del rango objetivo (min y max de los ajustes). Cada fila lleva el nombre, la barra, la cifra y el estado en una píldora.
- **Mapa corporal:** el dibujo actual parece "slenderman" (cuerpo de unas 10 cabezas, hombros y brazos finos).
  - Rehacerlo con proporciones atléticas de 7,5 a 8 cabezas: hombros anchos, forma en V, brazos y piernas con volumen, y músculos con forma anatómica reconocible (pectoral en abanico, deltoides redondeados, recto abdominal en bloques, cuádriceps con vasto lateral y medial).
  - Mostrar un solo cuerpo grande a todo el ancho con un selector Frente o Espalda, no dos cuerpos pequeños lado a lado.
  - Contorno suave, relleno por intensidad, leyenda con muestras de color.
  - Decisión del autor (2026-11-26): usar una ilustración anatómica de licencia libre en vez del dibujo propio. Candidatas más detalladas (2026-11-26; el autor descartó `react-body-highlighter` por básico): `VVGowda/muscle-svg` (MIT, 157 regiones en 41 grupos), `vulovix/body-muscles` (Apache-2.0, más de 70 músculos), `PerspicaciousGuy/Open-Muscle-Map` (MIT, 31 músculos, hombre y mujer) y `lilbuda/muscle-graph` (MIT, adaptado de `react-muscle-highlighter`). Mostrar capturas al autor para que elija.com/HichamELBSI/react-native-body-highlighter). VERIFICAR la licencia de ambos repositorios (archivo LICENSE) antes de copiar los datos. Pasar los polígonos a `BodyShapes` con un `svgKey` por cada uno de los 21 grupos del catálogo, y registrar la fuente, la licencia y la atribución en `distribution/THIRD_PARTY.md` y en Acerca de o Créditos. Si ninguna licencia resulta compatible o el dibujo no tiene calidad profesional, buscar otra ilustración MIT o CC0 y consultar al autor.
- **Verificación visual obligatoria:** capturas del emulador en claro y oscuro, y al 200 % de texto, comparadas con Inicio, Entrenar y Reloj antes de dar algo por terminado. Guía visual: `05d-UI-DIRECCION-VISUAL.md`.

## Pendiente de la Fase 6 (en este orden)

1. **Mapa corporal 2D** (`BodyMap`), un pedido del autor que debe quedar impecable:
   - frente y espalda dibujados con `Path` en Compose, dibujo propio y sin licencias de terceros;
   - una región por `svgKey` de `MuscleGroupEntity` (consultar los `svgKey` reales del catálogo en Room);
   - proporciones anatómicas, contorno y músculos con forma propia, modo claro y oscuro;
   - color por series semanales promedio (`VolumeCalculator.byMuscle`) con una leyenda que no dependa solo del color;
   - selector de semana, mes o año;
   - al tocar un músculo se abre una hoja con sus series, los ejercicios y la comparación con el periodo anterior.
   - Va como una pestaña "Cuerpo" en Progreso.
2. **Volumen semanal por músculo:** "bajo", "en rango" o "alto" con el rango de `AnalysisSettings`, en Resumen o Cuerpo.
3. **Historial con calendario dentro de Progreso:** reutilizar `feature/workout/ui/history/HistoryScreen.kt`.
4. **Perfil:** récords destacados, y el nivel y la edad de entrenamiento con su dato (`TrainingAge` en el dominio, con prueba).
5. **Paleta de datos accesible para daltonismo:** tokens en el tema, verificados con `node tools/check-contrast.mjs`. Aplicarla al gráfico, al mapa y a la escala de RPE.
6. **Celebración sobria de récord** en `SummaryScreen`: animación breve, sin emojis, sin animación si "reducir movimiento" está activo.
7. **Récords por variante de calistenia:** máximo de repeticiones o tiempo por ejercicio de peso corporal (ya se calculan por ejercicio; mostrarlos agrupados por disciplina).
8. **Hojas "Cómo usar"** de Progreso actualizadas con las pestañas nuevas.
9. **Medir los criterios en el emulador:**
   - crear `tools/seed-progress-history.mjs` que genere 2 años de sesiones (unas 300) sincronizables;
   - abrir Progreso y medir en logcat la etiqueta `AbitusProgress` (`analyze ... ms=`);
   - comprobar cifras exactas con un historial conocido;
   - verificar que cambiar la fórmula o el umbral recalcula las vistas;
   - comprobar que una medida creada sin conexión pasa `dirty` de 1 a 0 y llega a PostgreSQL.
   - Antes, aplicar la migración 017 a la base de desarrollo: `docker compose exec -T postgres psql -U abitusd -d abitusd < database/migrations/017_body_measurements.sql`.
10. **Cierre de la Fase 6:**
    - marcar "Hecha" en las filas de los bancos `11b` a `11f` con "6";
    - completar `02k` con las cifras medidas;
    - etiqueta `fase-6`.

## Fase 6b

Toda la fase 6b está por hacer, según `02l-MEMORIA-FASE-6B.md`. Al cerrar: etiqueta `fase-6b` y "Siguiente: Fase 7" en `02-MEMORIA.md`.

## Prompt para opencode

```text
Eres el ingeniero principal de Abitus-D. Continúa las Fases 6 y 6b hasta cerrarlas, sin detenerte entre tareas.

1. Lee AGENTS.md, CLAUDE.md, claude/02-MEMORIA.md, claude/03-REGLAS-DE-ORO.md, claude/18b-OPENCODE-FASE-6.md (estado y pendientes), claude/02k-MEMORIA-FASE-6.md y claude/02l-MEMORIA-FASE-6B.md. Revisa el código que ya existe en android/app/src/main/java/com/abitusd/app/feature/progress y core/designsystem/chart antes de crear nada.
2. Empieza por el "Rediseño obligatorio de Progreso" de 18b (el autor rechazó el diseño actual) y después sigue la lista "Pendiente de la Fase 6" en orden y toda la 6b. El mapa corporal 2D debe ser un dibujo propio, anatómico y de calidad alta: revísalo con capturas del emulador en claro y oscuro antes de darlo por terminado.
3. Por cada tarea: impleméntala con el estilo aprobado (tarjetas con icono, desplegables, hojas inferiores), textos en strings es y en, máximo 150 líneas por archivo, cero emojis, unidades SI, y sin librerías nuevas de gráficos. Prueba con npm test (backend) y ./gradlew detekt ktlintCheck testDebugUnitTest lintDebug assembleDebug (JAVA_HOME="C:\Program Files\Java\jdk-22"), más node tools/check-file-length.mjs y node tools/check-no-emoji.mjs. Haz un commit convencional y un push por cada tarea verificada.
4. Las pruebas en el emulador las haces tú: SDK en C:\Users\David\AppData\Local\Android\Sdk, AVD abitusd_api29, backend con npm run dev en el puerto 3000 (la app usa http://10.0.2.2:3000). Mide cada criterio de 02k y 02l y anota las cifras reales.
5. Al cerrar cada subfase: filas "Hecha" en los bancos, memoria completada (02k o 02l), etiqueta fase-6 o fase-6b subida, y 02-MEMORIA.md al día.
6. Mensajes para mí: breves y en español. Pregunta solo si una decisión de producto, costo, privacidad o alcance no está en los documentos. No termines tu turno hasta cerrar la fase 6b o quedar bloqueado.
```
