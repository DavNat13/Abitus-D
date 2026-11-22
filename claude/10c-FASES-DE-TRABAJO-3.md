# 10c - Fases de trabajo: 5, 6 y 7

Continuación de `10b-FASES-DE-TRABAJO-2.md`. Las ideas citadas están en `11b` a `11e`.

## Fase 5 - Reloj y timers

Objetivo: un apartado Reloj que sirva para cualquier actividad (diseño completo en `11e-RELOJ-Y-TIMERS.md`).

Entregables:
- Pantalla Reloj accesible desde Inicio, desde la barra de entrenamiento y desde un acceso directo de Android.
- Modos: cronómetro con vueltas, temporizador, multitemporizador, intervalos, Tabata, EMOM, AMRAP, for time, pirámides y secuencias personalizadas.
- Extras: reloj de piscina, modo reloj de pared, respiración guiada, metrónomo y cadencia, contador de vueltas de pista.
- Perfiles guardados y biblioteca de perfiles base (Tabata, EMOM, boxeo, movilidad, piscina, pomodoro, cocina); compartir un perfil por archivo.
- Notificación viva con controles (pausa, vuelta, sumar 15 s, saltar), háptica por fase, sonidos que no cortan la música y voz que anuncia la fase.
- Ligar un timer a un ejercicio o rutina y guardar las vueltas como parciales de la sesión.
- Pantalla completa de lectura lejana, colores y formas por fase, ajuste de tiempo en marcha.

Criterios de aceptación:
- Desviación menor a 1 segundo en 60 minutos con la pantalla apagada.
- El reloj sobrevive a cerrar la app y a reiniciar el teléfono, con el estado recuperado.
- Cada fase tiene señal visible además de sonido y vibración.
- Un perfil creado sin conexión se sincroniza y funciona en otro dispositivo.

## Fase 6 - Analítica

Objetivo: responder "estoy progresando o no", "entreno suficiente" y "qué descuido".

Entregables:
- Consultas en Room (`08-DATABASE.md`) y reglas de `09d`: e1RM, volumen con 1,0 y 0,5, veredicto con umbrales ajustables, PRs.
- Agregados diarios de volumen por músculo.
- Dashboard de Progreso, evolución por ejercicio, historial con calendario y mapa corporal 2D (semana, mes, año).
- Medidas corporales: peso con gráfico de tendencia y perímetros.

Ideas incluidas:
- Comparar semanas y bloques de entrenamiento.
- Rachas de consistencia, racha tolerante y objetivos semanales.
- Gráfico de peso corporal y objetivo de peso.
- Mejores series por ejercicio, ejercicios estancados y explicación del veredicto.
- Frecuencia por músculo, equilibrio empuje y tracción, volumen por patrón de movimiento y tonelaje semanal.
- Resumen semanal y mensual, y calendario anual de actividad.

Criterios de aceptación:
- Con datos de prueba conocidos, los valores coinciden con los esperados (cifras exactas).
- Fluidez con 2 años de datos de prueba.
- Todo veredicto muestra el dato que lo sustenta; "Sin datos suficientes" cuando corresponde.
- Cambiar los umbrales o la fórmula de 1RM recalcula las vistas.

## Fase 7 - Running, natación y salud

Objetivo: cardio completo y datos de Health Connect.

Entregables:
- Running con GPS: métricas en vivo, pausa, resumen y ruta simplificada (`09c`).
- Natación en piscina (largos, tiempos, estilos) y en aguas abiertas.
- Health Connect (pasos, frecuencia cardíaca, sesiones) con permisos y justificación.
- Dashboards de cardio: ritmo, distancia y mejores marcas.
- Exportar rutas a GPX.

Ideas incluidas:
- Intervalos para correr, zonas de frecuencia cardíaca, parciales automáticos y pausa automática.
- Avisos por voz de ritmo, modo ahorro de GPS y récords de running.
- Calzado con kilómetros acumulados.
- Natación: ritmo por 100 m o 100 yardas, series guardadas y material usado.
- Actividad libre (fútbol, tenis, escalada, artes marciales) con duración y esfuerzo.
- Ciclismo y remo básicos, brick.

Criterios de aceptación:
- Una carrera de 30 minutos con pantalla apagada se registra completa.
- La ruta guardada ocupa poco y se reconstruye bien.
- Sin Health Connect o con permisos rechazados, el resto de la app funciona sin errores.
- Los ritmos y distancias respetan la unidad elegida (km o millas, m o yardas).
