# 11e - Reloj, cronómetro y timers (apartado nuevo)

Se propone una sección "Reloj" accesible desde Inicio, desde la barra persistente de entrenamiento y desde un acceso directo de Android. Debe servir para cualquier cosa: pesas, cardio, natación, movilidad, juegos, cocina o estudio. Implementación técnica en `09c-FRONTEND-TIMERS-GPS-SALUD.md`. Banco de ideas, no compromiso. E esfuerzo y V valor: B, M, A.

## Modos del reloj

| Modo | Qué hace | E | V |
|---|---|---|---|
| Cronómetro | Tiempo total con vueltas, mejor y peor vuelta, diferencias | M | A |
| Temporizador | Cuenta regresiva con presets y nombre | B | A |
| Multitemporizador | Varios temporizadores a la vez, cada uno con su nombre | M | A |
| Intervalos | Trabajo y descanso, rondas, calentamiento y enfriamiento | M | A |
| Tabata | Preconfigurado: 20 s trabajo, 10 s descanso, 8 rondas | B | A |
| EMOM y E2MOM | Cada minuto (o cada N) con tarea por minuto | M | A |
| AMRAP | Máximas rondas en un tiempo, con contador de rondas | B | A |
| For time | Cronómetro hasta terminar un trabajo con límite de tiempo | B | M |
| Pirámides y escaleras | Tiempos que suben y bajan automáticamente | M | M |
| Secuencias personalizadas | Cadena libre de bloques con nombre, tiempo y sonido propio | A | A |
| Reloj de piscina | Aguja de segundos grande, salida cada X segundos | M | A |
| Cuenta regresiva a una fecha | Competencia, prueba de máximos o un evento | B | M |
| Modo reloj de pared | Hora grande para el gimnasio, con la hora de la sesión | B | M |
| Respiración y calma | Patrones guiados con animación y temporizador | B | M |
| Metrónomo y cadencia | Pulso para correr o levantar con tempo | M | A |
| Contador de vueltas de pista | Vueltas con toque o botón físico | B | M |
| Reloj de combate | Rounds y descansos para boxeo y artes marciales | B | M |
| Temporizador de frío o sauna | Aviso de tiempo máximo recomendado | B | M |

## Cómo funciona de forma moderna

| Idea | Detalle | E | V |
|---|---|---|---|
| Pantalla completa de lectura lejana | Cifras enormes, contraste alto, legible a 3 metros | B | A |
| Colores por fase | Trabajo, descanso y calentamiento con colores y formas distintas | B | A |
| Anillo de progreso animado | Con movimiento suave y opción de reducirlo | M | A |
| Notificación viva | Cronómetro visible en pantalla de bloqueo y barra de estado | M | A |
| Controles desde la notificación | Pausar, vuelta, sumar tiempo, saltar | M | A |
| Widget de reloj | Iniciar el último temporizador con un toque | M | A |
| Accesos directos | Mantener pulsado el icono: cronómetro, Tabata, descanso | B | A |
| Háptica distintiva | Patrones de vibración distintos por fase y cuenta final | B | A |
| Sonidos y voz | Pitidos, cuenta de 3 segundos, voz que dice la fase y el ejercicio | M | A |
| Se mezcla con la música | Sonidos cortos sin cortar la reproducción | M | A |
| Modo silencioso | Solo vibración o solo visual | B | A |
| Control por voz | "Iniciar cronómetro", "pausa", "vuelta" | A | M |
| Control con botones de volumen | Vuelta o pausa sin mirar la pantalla | M | M |
| Pantalla encendida | Opcional, con aviso de consumo | B | A |
| Unir al entrenamiento | Cada timer puede ligarse a un ejercicio o a una rutina | M | A |
| Guardar vueltas en la sesión | Las vueltas se guardan como parciales y se pueden exportar | M | A |
| Perfiles guardados | Ya aprobada; nombre, bloques, sonidos y color | M | A |
| Biblioteca de perfiles | Tabata, EMOM, boxeo, movilidad, piscina, pomodoro, cocina | B | A |
| Exportar o compartir un perfil | Archivo o código QR | M | M |
| Historial de vueltas | Con mejor, peor, promedio y gráfico | B | M |
| Texto a voz del próximo ejercicio | "Siguiente: sentadilla, 8 repeticiones" | M | M |
| Ajuste de tiempo en marcha | Sumar o restar 15 segundos sin detenerlo | B | A |
| Pausa por llamada | Se detiene al contestar y reanuda al terminar | M | M |
| Temas del reloj | Digital, analógico, minimalista, de alto contraste | M | M |
| Modo horizontal y soporte | Pensado para dejarlo apoyado frente a ti | M | M |

## Reglas técnicas del reloj

- El tiempo se calcula con marcas de reloj monótono (instante de inicio y de fin), nunca con contadores que se detengan con la app: así sobrevive a que Android suspenda la aplicación.
- El timer activo vive en un servicio en primer plano con notificación; las alertas exactas dependen de permisos que se VERIFICAN según la versión de Android y la forma de distribución del APK.
- Tras reiniciar el teléfono o cerrar la app, el reloj recupera el estado por marcas guardadas en Room.
- Ningún sonido ni vibración pueden ser la única señal: siempre hay un cambio visible y un texto (accesibilidad, regla de `05-UI-UX.md`).
- Toda cifra se muestra con tipografía de números tabulares para que no salten (Roboto).
- Los perfiles de timer son datos del usuario y se sincronizan como el resto.
