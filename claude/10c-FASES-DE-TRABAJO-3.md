# 10c - Fases de trabajo: 5, 5b, 6 y 6b

Continuación de `10b-FASES-DE-TRABAJO-2.md`. Alineado con los bancos de ideas el 2026-11-24: cada idea de `11b` a `11f` tiene su columna "Fase"; una fase incluye todas las filas con su número. Reglas comunes de toda fase en `10-FASES-DE-TRABAJO.md`.

## Fase 5 - Atleta y Reloj (núcleo)

Objetivo: conocer al atleta desde el primer uso y un apartado Reloj que sirva para cualquier actividad (diseño en `11e` y `11f`, técnica en `09c`).

Entregables (primero el atleta):
- Bienvenida en tres tarjetas y onboarding de unos 5 pasos omitibles (`11f`): datos corporales, experiencia y disciplinas, objetivo y disponibilidad, salud previa con cuestionario de aptitud, programa sugerido por reglas y test iniciales opcionales.
- Ficha del atleta editable en Perfil; datos sincronizados (columnas o tabla nueva en servidor y Room).
- Regla común desde esta fase: toda pantalla vacía explica el siguiente paso.
- Glosario "Aprende" enlazado desde cada término y desde "Cómo usar"; estado de sincronización visible con reintento.

Entregables del reloj:
- Motor único por marcas de reloj (nunca contando ticks), servicio en primer plano mientras haya un timer y alarmas exactas en cada cambio de fase (tipo de servicio y permisos a VERIFICAR).
- Estado guardado en Room: el timer se recupera al cerrar la app y al reiniciar el teléfono. El mismo receptor de reinicio vuelve a programar los recordatorios del calendario (pendiente de la 4b).
- Modos: cronómetro con vueltas, temporizador, multitemporizador, intervalos, Tabata, EMOM y E2MOM, AMRAP, for time, pirámides y secuencias personalizadas; fases por tiempo o por repeticiones.
- Perfiles guardados y sincronizados (tabla nueva en servidor y Room) y biblioteca base: Tabata, EMOM, boxeo, movilidad, piscina, pomodoro, cocina.
- Notificación viva con pausa, vuelta, sumar 15 s y saltar; aviso 10 s antes del fin del descanso.
- Señales: color y forma por fase, háptica distinta, sonidos que no cortan la música, voz con la fase y el próximo ejercicio, modo silencioso; anuncio de mitad y últimos segundos.
- Pantalla completa legible a 3 metros, anillo de progreso que respeta "reducir movimiento", ajuste de tiempo en marcha, pantalla encendida opcional.
- Pantalla Reloj accesible desde Inicio y desde la sesión activa.
- Autotest de precisión del reloj.
- Tarea pequeña pedida por el autor: ejercicios favoritos (estrella en el detalle, filtro "Favoritos" en la biblioteca y primeros al elegir un ejercicio), sincronizados.

Criterios de aceptación:
- Desviación menor a 1 segundo en 60 minutos con la pantalla apagada.
- El reloj sobrevive a cerrar la app y a reiniciar el teléfono, con el estado recuperado.
- Cada fase tiene señal visible además de sonido y vibración.
- Un perfil creado sin conexión se sincroniza y funciona en otro dispositivo.
- Un favorito marcado sin conexión aparece en otro dispositivo.
- El onboarding se completa u omite en menos de 2 minutos; sus datos se editan en Perfil y se sincronizan.

## Fase 5b - Reloj (extras)

Objetivo: el resto de `11e` y las ideas de otros bancos que usan el mismo motor.

Entregables:
- Relojes: piscina (salida cada X segundos), pared, respiración guiada, metrónomo y cadencia, vueltas de pista, combate, frío y sauna, cuenta regresiva a una fecha.
- Accesos: widget (último timer y entrenamiento de hoy), accesos directos de Android, mosaico de ajustes rápidos, modo horizontal y temas del reloj.
- Control sin mirar: botones de volumen y control por voz (reconocimiento sin conexión a VERIFICAR; si no es gratuito y local, se descarta y se registra); pausa por llamada.
- Unir un timer a un ejercicio o rutina, guardar vueltas como parciales, etiquetas de vuelta, historial con mejor, peor y promedio, exportar vueltas a CSV.
- Compartir perfiles por archivo y por código QR (librería a VERIFICAR: licencia libre y sin servicios).
- Calculadora de discos gráfica: barra dibujada con los discos por lado, colores y tamaños según el peso.
- Del motor: cuenta regresiva antes de una serie cronometrada, registro de isométricos con récord, práctica de habilidad, calentamiento y movilidad guiados, rutina convertida en secuencia de timer; test de campo (course navette, sprint de 30 m y Yo-Yo) con pitidos generados por la app.

Criterios de aceptación:
- Cada extra funciona sin conexión y con la pantalla apagada cuando aplica.
- El widget y los accesos directos abren el timer correcto en menos de 1 segundo.
- Un perfil compartido por archivo o QR se importa igual en otro teléfono.

## Fase 6 - Analítica (núcleo)

Objetivo: responder "estoy progresando o no", "entreno suficiente" y "qué descuido".

Entregables:
- Consultas en Room y reglas de `09d`: e1RM (también con peso corporal y lastre), volumen con 1,0 y 0,5, PRs, récords por variante de calistenia.
- Veredicto con umbrales ajustables y explicación del dato que lo sustenta.
- Mejores series por ejercicio y ejercicios estancados; récords destacados y nivel y edad de entrenamiento en el perfil.
- Panel de Progreso, evolución por ejercicio, historial con calendario y mapa corporal 2D (semana, mes, año).
- Medidas: peso con tendencia y objetivo, perímetros, índices opcionales con aviso de sus límites, fuerza relativa y fuerza contra peso.
- Paleta de datos accesible para daltonismo (gráficos, zonas y RPE) y celebración sobria de récords.

Criterios de aceptación:
- Con datos de prueba conocidos, los valores coinciden con los esperados (cifras exactas).
- Fluidez con 2 años de datos de prueba.
- Todo veredicto muestra el dato que lo sustenta; "Sin datos suficientes" cuando corresponde.
- Cambiar los umbrales o la fórmula de 1RM recalcula las vistas.

## Fase 6b - Analítica (comparar y hábitos)

Entregables:
- Comparar semanas, bloques y dos sesiones; resumen semanal y mensual; calendario anual de actividad.
- Rachas (incluida la tolerante), objetivos semanales y frecuencia por músculo; "qué entrenar hoy" según lo descuidado.
- Equilibrio empuje-tracción y superior-inferior con alertas; volumen por patrón, tonelaje, distribución de intensidad, curva fuerza-repeticiones, eficiencia, densidad, mapa por hora y mejor día y hora.
- Búsqueda en el historial; etiquetas y notas rápidas filtrables.
- Carga interna de la sesión (sRPE por minutos) con carga semanal, monotonía y tensión; evaluaciones periódicas cada 4 a 8 semanas comparadas con la anterior.
- Cumplimiento del plan: sesiones y series planificadas contra hechas, por semana y por bloque.

Criterios de aceptación:
- Cada comparación y alerta muestra las cifras de ambos lados.
- La búsqueda responde en menos de 300 ms con 2 años de datos.
