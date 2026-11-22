# 05d - Dirección visual (Fase 3b)

Complemento de `05b` (tokens) y `05c` (componentes y movimiento); no los contradice, los concreta. Estilo: ordenado, sobrio y moderno, con mucho aire, una sola acción fuerte por pantalla y profundidad sutil.

## Uso de cada color de la paleta

| Color | Dónde sí | Dónde no |
|---|---|---|
| primary | Botón principal, enlaces, selección (chips, pestaña, día del calendario), iconos de acción, foco | Fondos grandes, texto largo |
| navy | Presionado, degradado de marca, texto sobre `primaryContainer` | Texto pequeño sobre `primary` |
| ink | Texto principal, velo de hojas (40 %), sombras tintadas | Bordes |
| lavender (`surfaceSoft`) | Campos, chips en reposo, fondo de bloques secundarios dentro de una tarjeta | Fondo de pantalla |
| green | Solo positivo: hecho, PR, progreso, éxito | Botones de navegación o acciones neutras |
| white | Fondo y tarjetas en tema claro | - |

`primaryContainer` es el tinte de énfasis: contenedor del icono de la tarjeta, chip seleccionado, píldora de la barra inferior y día elegido.

## Jerarquía de pantalla

1. Encabezado (`ScreenHeader`): título `titleLarge`, subtítulo `caption` opcional y hasta una acción de icono a la derecha (48 dp).
2. Herramientas: búsqueda y una sola fila de chips desplazable.
3. Contenido: tarjetas separadas 12 dp; secciones con `SectionHeader` (`label`, `textSecondary`).
4. Acción principal: botón primario abajo o botón flotante; nunca dos primarios.

Margen lateral 20 dp; contenido inferior con hueco para el botón flotante y la barra inferior.

## Tipos de tarjeta

| Componente | Forma | Uso |
|---|---|---|
| `AppCard` | `surface`, borde `outline`, radio 16, `shadowSm`; interactiva escala 0,98 y sube a `shadowMd` | Base de todas |
| `ListItemCard` | `AppCard` de 72 dp mín.: icono en círculo `primaryContainer` (40 dp), título `titleMedium`, metadatos `caption`, indicador `chevron` | Ejercicios, rutinas, programas |
| `GroupCard` | Una `AppCard` con filas separadas por filete `outline` | Semanas del programa, secciones del detalle, ajustes |
| `HeroCard` | Degradado de marca, radio 24, texto blanco | Inicio (entrenamiento de hoy) |
| `StatusBanner` | Contenedor suave semántico, sin sombra | Éxito, error, advertencia, sin conexión |
| `EmptyState` | Icono en círculo, una línea, una acción | Listas vacías |
| `SkeletonCard` | Bloques `surfaceSoft` con brillo de 1.200 ms | Cargas |

Nunca tarjeta con sombra dentro de otra tarjeta.

## Superficies interactivas

- Hojas inferiores (`AppBottomSheet`): radio superior 24, asa, velo `ink` 40 %. Para filtros, elegir rutina o día, plantillas y acciones de un elemento.
- Diálogo (`ConfirmDialog`): solo para confirmar acciones destructivas (eliminar rutina, programa o entrenamiento).
- Menú contextual (`DropdownMenu` con iconos) para acciones secundarias de una tarjeta.
- Snackbar para confirmar acciones reversibles (archivado, plantilla creada).

## Chips

`AppChip` único: reposo `surfaceSoft` y borde `outline`; seleccionado `primaryContainer`, borde `primary`, texto `onPrimaryContainer` e icono de verificación. Altura visual 36 dp con área táctil de 48 dp.

## Movimiento

Tokens en `AppMotion` (duraciones y curvas de 05c). Todo pasa por `rememberReduceMotion()`, que lee la escala de animaciones del sistema; con 0 no hay animación.
- Presión: escala con resorte corto (tarjetas 0,98, botones 0,97).
- Listas: aparición con desplazamiento de 8 dp, 40 ms entre elementos, solo los 8 primeros y una vez.
- Pantallas: fundido con ligero desplazamiento horizontal de eje compartido, 300 ms.
- Pestañas inferiores: fundido.

## Iconos

Disciplinas con el icono que declara la semilla (`icon`), vía `AppIcons.discipline(iconKey)`. Acciones con iconos de Material Symbols Outlined añadidos por `tools/convert-material-symbol.mjs`.

## Accesibilidad

Áreas táctiles de 48 dp aunque el elemento se vea menor; iconos con descripción cuando no hay texto; nada depende solo del color; textos sin altura fija para escalar al 200 %.
