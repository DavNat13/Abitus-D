# 11e - Reloj, cronómetro y timers (apartado nuevo)

Se propone una sección "Reloj" accesible desde Inicio, desde la barra persistente de entrenamiento y desde un acceso directo de Android. Debe servir para cualquier cosa: pesas, cardio, natación, movilidad, juegos, cocina o estudio. Implementación técnica en `09c-FRONTEND-TIMERS-GPS-SALUD.md`. Banco de ideas, no compromiso. E esfuerzo, V valor: B, M, A; Fase: fase asignada o "Hecha".

## Modos del reloj

| Modo | Qué hace | E | V | Fase |
|---|---|---|---|---|
| Cronómetro | Tiempo total con vueltas, mejor y peor vuelta, diferencias | M | A | Hecha |
| Temporizador | Cuenta regresiva con presets y nombre | B | A | Hecha |
| Multitemporizador | Varios temporizadores a la vez, cada uno con su nombre | M | A | 5b |
| Intervalos | Trabajo y descanso, rondas, calentamiento y enfriamiento | M | A | Hecha |
| Tabata | Preconfigurado: 20 s trabajo, 10 s descanso, 8 rondas | B | A | Hecha |
| EMOM y E2MOM | Cada minuto (o cada N) con tarea por minuto | M | A | Hecha |
| AMRAP | Máximas rondas en un tiempo, con contador de rondas | B | A | 5b |
| For time | Cronómetro hasta terminar un trabajo con límite de tiempo | B | M | Hecha |
| Pirámides y escaleras | Tiempos que suben y bajan automáticamente | M | M | Hecha |
| Secuencias personalizadas | Cadena libre de bloques con nombre, tiempo y sonido propio | A | A | 5b |
| Reloj de piscina | Aguja de segundos grande, salida cada X segundos | M | A | 5b |
| Cuenta regresiva a una fecha | Competencia, prueba de máximos o un evento | B | M | 5b |
| Modo reloj de pared | Hora grande para el gimnasio, con la hora de la sesión | B | M | 5b |
| Respiración y calma | Patrones guiados con animación y temporizador | B | M | 5b |
| Metrónomo y cadencia | Pulso para correr o levantar con tempo | M | A | 5b |
| Contador de vueltas de pista | Vueltas con toque o botón físico | B | M | 5b |
| Reloj de combate | Rounds y descansos para boxeo y artes marciales | B | M | 5b |
| Temporizador de frío o sauna | Aviso de tiempo máximo recomendado | B | M | 5b |

## Cómo funciona de forma moderna

| Idea | Detalle | E | V | Fase |
|---|---|---|---|---|
| Pantalla completa de lectura lejana | Cifras enormes, contraste alto, legible a 3 metros | B | A | Hecha |
| Colores por fase | Trabajo, descanso y calentamiento con colores y formas distintas | B | A | Hecha |
| Anillo de progreso animado | Con movimiento suave y opción de reducirlo | M | A | Hecha |
| Notificación viva | Cronómetro visible en pantalla de bloqueo y barra de estado | M | A | Hecha |
| Controles desde la notificación | Pausar, vuelta, sumar tiempo, saltar | M | A | Hecha |
| Widget de reloj | Iniciar el último temporizador con un toque | M | A | 5b |
| Accesos directos | Mantener pulsado el icono: cronómetro, Tabata, descanso | B | A | 5b |
| Háptica distintiva | Patrones de vibración distintos por fase y cuenta final | B | A | Hecha |
| Sonidos y voz | Pitidos, cuenta de 3 segundos, voz que dice la fase y el ejercicio | M | A | Hecha |
| Se mezcla con la música | Sonidos cortos sin cortar la reproducción | M | A | Hecha |
| Modo silencioso | Solo vibración o solo visual | B | A | Hecha |
| Control por voz | "Iniciar cronómetro", "pausa", "vuelta" | A | M | 5b |
| Control con botones de volumen | Vuelta o pausa sin mirar la pantalla | M | M | 5b |
| Pantalla encendida | Opcional, con aviso de consumo | B | A | Hecha |
| Unir al entrenamiento | Cada timer puede ligarse a un ejercicio o a una rutina | M | A | 5b |
| Guardar vueltas en la sesión | Las vueltas se guardan como parciales y se pueden exportar | M | A | 5b |
| Perfiles guardados | Ya aprobada; nombre, bloques, sonidos y color | M | A | Hecha |
| Biblioteca de perfiles | Tabata, EMOM, boxeo, movilidad, piscina, pomodoro, cocina | B | A | Hecha |
| Exportar o compartir un perfil | Archivo o código QR | M | M | 5b |
| Historial de vueltas | Con mejor, peor, promedio y gráfico | B | M | 5b |
| Texto a voz del próximo ejercicio | "Siguiente: sentadilla, 8 repeticiones" | M | M | 5b |
| Ajuste de tiempo en marcha | Sumar o restar 15 segundos sin detenerlo | B | A | Hecha |
| Pausa por llamada | Se detiene al contestar y reanuda al terminar | M | M | 5b |
| Temas del reloj | Digital, analógico, minimalista, de alto contraste | M | M | 5b |
| Modo horizontal y soporte | Pensado para dejarlo apoyado frente a ti | M | M | 5b |

## Reglas técnicas del reloj

- El tiempo se calcula con marcas de reloj monótono (instante de inicio y de fin), nunca con contadores que se detengan con la app: así sobrevive a que Android suspenda la aplicación.
- El timer activo vive en un servicio en primer plano con notificación; las alertas exactas dependen de permisos que se VERIFICAN según la versión de Android y la forma de distribución del APK.
- Tras reiniciar el teléfono o cerrar la app, el reloj recupera el estado por marcas guardadas en Room.
- Ningún sonido ni vibración pueden ser la única señal: siempre hay un cambio visible y un texto (accesibilidad, regla de `05-UI-UX.md`).
- Toda cifra se muestra con tipografía de números tabulares para que no salten (Roboto).
- Los perfiles de timer son datos del usuario y se sincronizan como el resto.

## Propuestas del 2026-11-24

| Idea | Detalle | E | V | Fase |
|---|---|---|---|---|
| Aviso antes del fin del descanso | Vibración 10 s antes para preparar la siguiente serie | B | A | Hecha |
| Fases por tiempo o por repeticiones | Una fase avanza al tocar "listo" cuando terminas las repeticiones | M | A | 5b |
| Anuncio de mitad y últimos segundos | "Mitad", "quedan 10 segundos", configurable | B | M | Hecha |
| Autotest de precisión | Mide la desviación real del reloj en el teléfono | B | M | 5b |
| Rutina como secuencia de timer | Convierte un circuito de la rutina en una secuencia con un toque | M | A | 5b |
| Práctica de habilidad | Isométricos (pino, plancha frontal) con descanso automático y mejor tiempo | M | A | 5b |
| Etiquetas de vuelta | Nombrar una vuelta (subida, bajada) con un toque | B | M | 5b |
| Exportar vueltas a CSV | Tiempos y parciales a un archivo | B | M | 5b |
| Mosaico de ajustes rápidos | Iniciar el cronómetro desde el panel de notificaciones | B | M | 5b |
| Descanso adaptativo | Propone más descanso si la serie anterior fue muy exigente | M | A | 11 |
