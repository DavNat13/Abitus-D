# 10f - Fase 4 y Fase 4b (acordado con el autor el 2026-11-23)

Detalla y amplía la Fase 4 de `10b-FASES-DE-TRABAJO-2.md`, que queda dividida en dos. Diseño: estilo aprobado de `05d-UI-DIRECCION-VISUAL.md`. Durante la sesión, botones de 56 dp y animaciones mínimas.

## Fase 4 - Registro de entrenamiento

Objetivo: registrar sesiones de forma rápida, ordenada y a prueba de fallos, repartida en varias pantallas.

### Pantallas (ninguna hace todo)
1. Entrenar (inicio de sección): tarjeta "Sesión de hoy" con "Empezar", vista semanal compacta y acceso a Rutinas, Programas, Calendario e Historial.
2. Previa de la sesión: qué toca, ejercicios, tiempo estimado, chequeo breve (sueño, energía, molestias) y "Empezar". Permite cambiar ejercicios antes de arrancar.
3. Sesión activa, un ejercicio por pantalla: objetivo, "última vez", filas de series con un toque para confirmar, deslizar para cambiar de ejercicio.
4. Mapa de la sesión (hoja inferior): todos los ejercicios con su progreso; saltar, reordenar, agregar, superseries.
5. Descanso: barra persistente con tiempo, +15 s y saltar; al tocarla, temporizador grande. Descanso automático al confirmar.
6. Herramientas en hojas: calculadora de discos, alternativas del ejercicio, notas y molestia por zona.
7. Resumen final: duración, volumen, series, PRs, comparación con la anterior, RPE de la sesión, notas, guardar como plantilla.
8. Historial: sesiones por fecha y detalle editable.

### Entregables técnicos
- Tablas `workout_sessions`, `session_exercises` y `sets` (`08b`) en servidor y Room, con sincronización genérica por tabla (como la Fase 3).
- Estado de la sesión activa persistido en Room (`09b`): recuperar tras cierre del proceso; al abrir con una sesión sin cerrar, continuar o finalizar.
- Motor de descanso con servicio en primer plano, reloj monótono y notificación con controles (`09c`, `11e`). La pantalla de reloj completa llega en la Fase 5.
- Tipos de serie: normal, calentamiento, drop set, AMRAP, al fallo, rest-pause; series por lado, tempo y holds isométricos con cronómetro.
- Deshacer y rehacer la última acción, copiar la sesión anterior, series de aproximación sugeridas.
- Guardar sesión como plantilla (pendiente de la Fase 3).
- Tipo de día en programas y calendario: entrenamiento, descanso o descanso activo (pedido del autor); Inicio y Calendario lo muestran.
- Sesión perdida: ofrecer moverla, saltarla o empujar el plan un día.

### Criterios de aceptación
- Confirmar una serie responde en menos de 100 ms y arranca el descanso.
- El descanso suena con la pantalla apagada y la app en segundo plano.
- Una sesión registrada sin conexión llega completa al servidor sin duplicados.
- Matar la app a mitad de sesión no pierde series confirmadas.
- Deshacer y rehacer funcionan sin conexión.

## Fase 4b - Planificación avanzada

Objetivo: organizar el entrenamiento en ciclos y adaptar el calendario a la vida real.

- Periodización: macrociclo, mesociclos o bloques (3 a 8 semanas, con objetivo) y microciclos (semana o rotación).
- Fases por bloque: acumulación, intensificación, descarga y prueba de marcas.
- Ejercicios por bloque, con rotación de variantes entre bloques sin rehacer el programa.
- Progresión automática: doble progresión, porcentaje o RPE objetivo por semana y progresiones de calistenia usando las relaciones del catálogo.
- Descarga programada cada N semanas o sugerida si el rendimiento baja o el RPE sube varias sesiones seguidas.
- Semana de prueba (1RM, máximo de repeticiones o tiempo) que recalcula las cargas del bloque siguiente.
- Días fijos o rotación (A, B, C, descanso), disponibilidad semanal que reacomoda las sesiones y doble sesión en un día.
- Hora planificada y recordatorio configurable.
- Vista semanal compacta y línea de tiempo del bloque ("Semana 3 de 5, acumulación").
- Objetivos de series semanales por músculo con aviso de déficit o exceso.
- Modo viaje o lugar: sustituir ejercicios por equivalentes con el equipo del perfil elegido.
- Plantillas de semana reutilizables.
- Pantalla de editor de programa por bloques: bloques en tarjetas; dentro, semanas, días, ejercicios y reglas de progresión.

Criterios de aceptación de la 4b: crear un mesociclo de 4 semanas con descarga, ver la progresión sugerida semana a semana, mover una semana por falta de disponibilidad sin perder el orden y sincronizar todo sin conexión.
