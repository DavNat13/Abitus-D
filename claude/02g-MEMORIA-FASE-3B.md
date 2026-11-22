# 02g - Memoria: Fase 3b (rediseño visual), 2026-11-22

Complemento de `02-MEMORIA.md`. Dirección visual en `05d-UI-DIRECCION-VISUAL.md`.

## Herramientas
- Agentes y metodologías locales del autor leídos y aplicados como criterio (no copiados al repositorio).
- Las skills mobile-android-design, android-design-system, compose y frontend-design no existen en `.claude/skills` ni en las carpetas del autor; se trabajó sin ellas.

## Auditoría de las pantallas actuales (prioridad 1 = más grave)

| P | Pantalla | Problema |
|---|---|---|
| 1 | Todas | Sin componente de tarjeta propio: cada pantalla arma su `Card` con estilos repetidos; sin sombra tintada ni estado presionado (05c) |
| 1 | Todas | Sin movimiento: ni presión, ni entrada de listas, ni transición de pantallas; no se respeta "reducir animaciones" |
| 1 | Biblioteca | Tarjetas altas sin icono ni indicador de toque (caben 5 por pantalla); dos filas de filtros con estilos distintos; el chip seleccionado usa el lila por defecto de Material y no `primaryContainer`; el botón flotante tapa la última tarjeta |
| 1 | Programa | 28 a 200 botones de texto sueltos para los días; sin agrupación por semana; selector en diálogo plano |
| 1 | Calendario | "Mes anterior" y "Mes siguiente" como texto largo; fecha en formato ISO; acciones del día como botones de texto que se parten en dos filas; sin tarjeta para cada entrenamiento |
| 2 | Detalle de ejercicio | Muro de texto: viñetas con carácter, sin chips para músculos ni equipo, sin cabecera con icono de disciplina ni metadatos agrupados |
| 2 | Editor de rutina | Tarjetas de ejercicio sin jerarquía (nombre, objetivos y acciones al mismo nivel); acciones como texto |
| 2 | Entrenar | Sin título de pantalla; pestañas sin indicador de marca coherente |
| 2 | Estados | Vacíos con una línea de texto gris; sin esqueletos de carga; error y sin conexión sin patrón común |
| 2 | Diálogos | Plantillas, días y rutinas en `AlertDialog`; 05c reserva los diálogos para confirmaciones; eliminar no pide confirmación |
| 3 | Barra inferior | Sin `shadowNav`; Inicio y Progreso son marcadores de posición |
| 3 | Tema oscuro | No revisado pantalla por pantalla |

## Decisiones
- Sin librerías nuevas: Material 3 de Compose ya trae hojas inferiores, snackbars, menús y animaciones.
