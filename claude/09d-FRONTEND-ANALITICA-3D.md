# 09d - Frontend: analítica y cuerpo humano

Complemento de `09-FRONTEND-ANDROID.md`. Cálculo en el dispositivo con consultas sobre Room (consultas de referencia en `08-DATABASE.md`).

## Fórmulas y reglas

### 1RM estimado (e1RM)
- Epley (por defecto): `peso x (1 + repeticiones / 30)`.
- Brzycki (opcional): `peso x 36 / (37 - repeticiones)`.
- Solo se calcula con series de trabajo de 1 a 10 repeticiones. Series de más de 12 repeticiones se excluyen de la estimación. Calentamientos no cuentan.
- La fórmula la elige el usuario en Ajustes.

### Volumen
- Volumen de carga: suma de `carga x repeticiones` de series de trabajo.
- Series por músculo: cada serie de trabajo suma `contribution` del ejercicio por músculo (por defecto 1,0 al principal y 0,5 al secundario). Los valores y la contribución son configurables y se registran en `exercise_muscles`.
- Se guardan agregados diarios en `muscle_volume_daily` para consultar rápido por semana, mes y año.

### Veredicto de progreso por ejercicio
Objetivo: responder "estoy progresando o no".
1. Para cada sesión de las últimas 6 semanas tomar el mejor e1RM (o la mayor carga para ejercicios sin repeticiones útiles; mejor repeticiones para peso corporal; mejor ritmo en una distancia comparable para cardio).
2. Si hay menos de 3 puntos: "Sin datos suficientes".
3. Calcular la tendencia con una regresión robusta (por ejemplo Theil-Sen) y expresarla como variación porcentual por cuatro semanas.
4. Más de +1 por ciento: "Progresando". Entre -1 y +1: "Estable". Menos de -1: "Retroceso".
5. Los umbrales y la ventana son constantes configurables y se ajustarán con uso real.
6. Mostrar el dato que sustenta el veredicto (por ejemplo "e1RM de 92 a 97 kg en 6 semanas") para que sea verificable.

### Records personales
- Por ejercicio: mayor carga, mejor e1RM, más repeticiones, mejor ritmo por distancia, mayor duración.
- Se detectan al guardar una serie o cardio y se destacan en el resumen de la sesión.

### Entreno suficiente
- Series semanales por músculo frente a un rango de referencia configurable; mostrar "bajo", "en rango" o "alto" como orientación, sin prescribir.

## Dashboards (pantalla Progreso)

- Resumen: sesiones, volumen, duración y consistencia de la semana; comparación con la semana anterior.
- Evolución por ejercicio: línea de e1RM o carga con selector de rango (4 semanas, 3 meses, 1 año, todo).
- Volumen por músculo: barras semanales y mapa de calor.
- Cardio: distancia acumulada, ritmo medio, mejores marcas por distancia.
- Historial: calendario mensual; tocar un día abre el detalle de la sesión.
- Todas las vistas cuentan con estados cargando, vacío y sin datos suficientes.

## Cuerpo humano interactivo

### Interacción
- Selector de periodo: semana, mes, año. Selector de vista: frente, espalda.
- Cada grupo muscular se colorea con la escala de `05b-UI-TOKENS.md` según las series equivalentes promedio por semana en el periodo.
- Tocar un músculo muestra: nombre, series del periodo, ejercicios que lo trabajaron y comparación con el periodo anterior.
- Leyenda siempre visible; valores exactos al tocar (no depender solo del color).

### Implementación en dos etapas
1. Mapa 2D (frente y espalda) en SVG con una zona por grupo muscular (`svg_key`). Es la versión base y el respaldo para dispositivos modestos o si el 3D falla. Se entrega en la Fase 5.
2. Cuerpo 3D (Fase 8): modelo glTF con una pieza por grupo muscular (`mesh_key`), mostrado con Three.js dentro de un WebView que carga solo assets locales con `WebViewAssetLoader`, sin CDN ni red. Rotación con un dedo, zoom con dos, tocar para seleccionar.
- Comunicación con la interfaz Compose mediante una interfaz mínima (periodo y colores hacia el visor; músculo seleccionado hacia la app). Ningún dato personal entra al WebView.
- Rendimiento: modelo ligero (decenas de miles de triángulos como máximo, VERIFICAR en dispositivos de gama media), texturas pequeñas, carga diferida.

### Modelo 3D: requisitos y riesgo
- Piezas separadas por grupo muscular y nombres que coincidan con `mesh_key`.
- Licencia que permita uso y redistribución en una app gratuita; atribución si la exige. Registrar la fuente y la licencia en `distribution/THIRD_PARTY.md`.
- Si no hay modelo adecuado, el 2D se mantiene como producto final y el 3D queda pendiente (ver decisiones).
