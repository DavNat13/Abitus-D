# 19 - Prompt de rediseño visual (entre las Fases 3 y 4)

Se pega en Claude Code, en la raíz del proyecto. Es una fase intermedia: "Fase 3b - Rediseño visual". No cambia la lógica; solo la presentación.

## Prompt

```
Fase 3b de Abitus-D: rediseño visual completo antes de pasar a la Fase 4.

OBJETIVO
Rediseñar la app Android para que sea mucho más ordenada, moderna y minimalista, con el mejor diseño móvil posible: tarjetas modernas, buena jerarquía, uso correcto de la paleta de marca, iconografía coherente, modales y hojas inferiores interactivas, estados bien resueltos y movimiento sobrio. La app ya cubre las Fases 0 a 3; no cambies su comportamiento.

PASO 1 - LEE ANTES DE ACTUAR
CLAUDE.md, claude/02-MEMORIA.md, claude/02b-MEMORIA-DECISIONES-2.md, claude/03-REGLAS-DE-ORO.md, claude/05-UI-UX.md, claude/05b-UI-TOKENS.md, claude/05c-UI-COMPONENTES-Y-MOVIMIENTO.md, claude/09-FRONTEND-ANDROID.md y claude/17-CALIDAD-Y-PRUEBAS.md. La documentación del proyecto manda sobre cualquier skill o agente.

PASO 2 - SKILLS DEL PROYECTO (úsalas si están en .claude/skills; si falta alguna, dímelo y sigue con las demás)
- mobile-android-design: patrones de Material 3, Compose, navegación y layouts adaptables. Ignora su sugerencia de colores dinámicos: la paleta de marca es fija (05b).
- android-design-system: tokens de diseño y tema en Compose. Úsala para que no quede ningún color, tamaño ni forma suelta.
- compose: buenas prácticas de código Compose para las pantallas.
- frontend-design (si está disponible): solo como criterio estético, no como código web.

PASO 3 - AGENTES Y SKILLS LOCALES (carpetas de mi equipo; léelos antes de usarlos)
Agentes en C:\Users\David\.config\opencode\agents:
- ui-designer: dirección visual, sistema de componentes y revisión de consistencia.
- ux-architect: estructura de pantallas y jerarquía (adapta lo que sea de CSS a Compose).
- mobile-app-builder: implementación nativa Android.
- accessibility-auditor: auditoría de contraste, tamaños táctiles y lector de pantalla.
- whimsy-injector: solo micro-interacciones sobrias; PROHIBIDOS los emojis.
- performance-benchmarker: fluidez de scroll y animaciones.
- minimal-change-engineer, code-reviewer y reality-checker: control de cambios, revisión y verificación final.
Metodologías en C:\Users\David\.config\opencode\skills\metodologias: separation-of-concerns, library-first-approach, kaizen, plan-do-check-act y root-cause-tracing. En function-file-size-limits el límite de archivos de ESTE proyecto es 150 líneas, no 200.
Si un agente o skill contradice las reglas del proyecto, gana el proyecto. No copies archivos de esas carpetas al repositorio.

PASO 4 - REGLAS QUE NO SE ROMPEN
- Paleta, Roboto y tokens de 05b; ningún color hexadecimal literal ni tamaño suelto fuera de los tokens.
- Cero emojis en código, textos, recursos y documentación. Iconos solo vía AppIcons (Material Symbols).
- Máximo 150 líneas por archivo; divide componentes en archivos pequeños.
- Contraste AA en tema claro y oscuro, objetivos táctiles de al menos 48 dp, texto escalable al 200 %.
- Sin dependencias nuevas de pago ni que añadan seguimiento; costo cero. Cualquier librería nueva se justifica antes.
- No cambies modelos de datos, repositorios, sincronización ni navegación lógica; solo la capa de presentación.
- Cada texto sigue en strings.xml (es y en).

PASO 5 - ALCANCE DEL REDISEÑO
a) Sistema de diseño: revisa y mejora tokens, tipografía, formas, elevaciones y sombras tintadas, y crea o pule los componentes base: tarjetas (con jerarquía y estados), botones, campos, chips, barras de progreso, listas, encabezados, barra inferior y barra superior.
b) Superficies interactivas: hojas inferiores (bottom sheets), diálogos de confirmación, menús contextuales, selectores y snackbars, con gestos y animaciones suaves.
c) Estados: vacío, carga (esqueletos), error y sin conexión, con microcopy claro (05-UI-UX.md).
d) Pantallas existentes de las Fases 1 a 3: acceso (bienvenida, registro, verificación, login, recuperación), inicio, biblioteca y detalle de ejercicio, ejercicios propios, rutinas, programas y calendario.
e) Movimiento: transiciones entre pantallas, aparición de listas, toques con respuesta, respetando el ajuste de reducir animaciones del sistema.
f) Temas claro y oscuro coherentes; ningún texto ilegible ni elemento cortado en pantallas pequeñas.

PASO 6 - MÉTODO
1. Audita las pantallas actuales y lista los problemas de diseño en la memoria, con prioridad.
2. Escribe una propuesta breve de dirección visual (cómo usar cada color de la paleta, tipos de tarjeta, jerarquía) y guárdala en claude/05c o en un archivo nuevo de máximo 150 líneas.
3. Implementa primero el sistema de componentes y rediseña UNA pantalla piloto (biblioteca de ejercicios). Muéstrame el resultado con capturas antes y después y espera mi visto bueno.
4. Con mi aprobación, rediseña el resto por grupos de pantallas, un grupo por vez, ejecutando las pruebas en cada grupo.
5. Pasa accessibility-auditor, performance-benchmarker, code-reviewer y reality-checker al final y corrige lo que encuentren.

PASO 7 - CRITERIOS DE ACEPTACIÓN
- Todas las pantallas de las Fases 1 a 3 usan los componentes y tokens nuevos, sin valores sueltos.
- Las pruebas existentes siguen en verde y los linters y scripts del proyecto pasan.
- Contraste AA verificado en ambos temas y objetivos táctiles correctos.
- Fluidez aceptable al desplazarse en un dispositivo de gama media o emulador.
- Funciona sin conexión igual que antes; no hay regresiones de comportamiento.

PASO 8 - CIERRE
Actualiza la memoria (estado, decisiones y hechos verificados), etiqueta Git fase-3b-rediseno y entrégame un informe corto: qué cambió, capturas antes y después, qué decisiones tomaste, qué quedó pendiente y propuesta para iniciar la Fase 4. No empieces la Fase 4 sin mi aprobación.
```
