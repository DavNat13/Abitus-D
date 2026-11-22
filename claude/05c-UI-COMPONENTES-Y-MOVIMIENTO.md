# 05c - Componentes, sombras y movimiento

Complemento de `05b-UI-TOKENS.md` (colores, tipografía, forma, iconos). Estilo: moderno, limpio, con profundidad sutil. Fondo blanco, tarjetas con borde fino y sombra tintada, animaciones breves y con propósito.

## Elevación y sombras

Las sombras se tiñen con `ink` (#101632) en lugar de negro, para que se vean limpias sobre blanco. En Compose usar `Modifier.shadow(elevation, shape, ambientColor, spotColor)` con `ink` a baja opacidad (disponible desde API 28; minSdk es 29). Si se necesita un sombreado en capas más fino, dibujarlo con un modificador propio, en su propio archivo.

| Token | Elevación | Opacidad de sombra (ink) | Uso |
|---|---|---|---|
| shadowNone | 0 dp | 0 | Elementos planos, campos |
| shadowSm | 2 dp | 8 a 10 por ciento | Tarjetas en reposo |
| shadowMd | 6 dp | 12 por ciento | Tarjeta presionada o destacada, chips seleccionados |
| shadowLg | 14 dp | 16 por ciento | Hojas inferiores, menús, diálogos |
| shadowNav | 8 dp hacia arriba | 10 por ciento | Barra de navegación inferior |

En tema oscuro las sombras se reducen y se prioriza el borde y el tono de superficie.

## Tarjetas

- Estándar: fondo `surface`, borde 1 dp `outline`, radio 16, `shadowSm`, relleno interno 16.
- Interactiva: al presionar escala a 0,98 y sube a `shadowMd` (150 ms).
- Tarjeta principal (entrenamiento de hoy): degradado de marca, radio 24, texto blanco, botón blanco con texto `primary`, `shadowMd`.
- Tarjeta de estado: contenedor suave (`successContainer`, `errorContainer`, `warningContainer`) con icono y texto, sin sombra.
- Métrica (`MetricTile`): cifra grande, etiqueta y variación (verde si mejora, `textSecondary` si es neutra, `error` si empeora).
- No anidar tarjetas con sombra dentro de tarjetas; separar con espacio o filete.

## Botones

Altura 52 dp (56 dp durante el entrenamiento), radio 12, texto `label` en mayúsculas de frase.

| Tipo | Reposo | Presionado | Deshabilitado |
|---|---|---|---|
| Primario | Relleno `primary`, texto blanco, `shadowSm` | `primaryPressed`, escala 0,97 | Relleno `outline`, texto `textDisabled` |
| Secundario | Fondo blanco, borde 1,5 dp `primary`, texto `primary` | Fondo `primaryContainer` | Borde `outline`, texto `textDisabled` |
| Tonal | Fondo `surfaceSoft`, texto `navy` | Fondo `primaryContainer` | Texto `textDisabled` |
| Texto | Sin fondo, texto `primary` | Fondo `primaryContainer` | Texto `textDisabled` |
| Destructivo | Fondo blanco, borde y texto `error` | Fondo `errorContainer` | Como secundario |
| Éxito | Relleno `success`, texto blanco | Verde más oscuro (`greenText`) | Como primario |

- Confirmar serie: botón circular o píldora. En reposo, contorno `outlineStrong`; completada, relleno `success` con marca de verificación.
- Con carga en curso, botón con indicador circular y texto atenuado; no permitir doble toque.
- Un solo botón primario por pantalla o por hoja.

## Campos de entrada

- Fondo `surfaceSoft`, borde 1 dp `inputBorder`, radio 12, altura 56 dp, etiqueta flotante.
- Foco: borde 2 dp `primary`. Error: borde `error` y mensaje con icono debajo.
- Campos numéricos del entrenamiento (carga, repeticiones): cifra `displayNumeric` reducida a 28 a 32, botones de más y menos de 48 dp.
- Verificación de código: seis casillas individuales con foco automático.

## Chips, barras y hojas

- Chip: fondo `surfaceSoft`, borde `outline`, radio completo. Seleccionado: fondo `primaryContainer`, borde `primary`, texto `onPrimaryContainer`.
- Barra superior: blanca, sin sombra; al desplazar el contenido aparece un filete `outline` o `shadowSm`.
- Barra inferior: blanca, `shadowNav`; seleccionado con icono `primary` y píldora `primaryContainer` detrás; no seleccionado `textSecondary`.
- Hoja inferior: radio superior 24, `shadowLg`, asa de arrastre, fondo con velo de `ink` al 40 por ciento.
- Diálogos solo para confirmaciones destructivas o críticas.
- Barra de descanso persistente: tarjeta compacta con anillo de progreso, tiempo y botones de ajuste.

## Estados de pantalla

Cargando con esqueleto (brillo suave sobre `surfaceSoft`), vacío (icono en contenedor circular, una línea, una acción), error con reintento, sin conexión con aviso discreto en `warningContainer`.

## Movimiento

Principio: la animación explica un cambio, nunca decora. Rápida y sin bloquear la interacción.

| Tipo | Duración | Curva |
|---|---|---|
| Micro (presión, conmutadores) | 100 a 150 ms | Estándar |
| Elementos (aparecer, expandir) | 200 a 250 ms | Desaceleración enfatizada |
| Transición de pantalla | 300 ms | Eje compartido o fundido |
| Hojas inferiores y diálogos | 250 ms | Desaceleración enfatizada |
| Gráficos al entrar | 600 ms, una sola vez | Desaceleración |

Curvas sugeridas: estándar (0,2; 0; 0; 1), desaceleración enfatizada (0,05; 0,7; 0,1; 1), aceleración (0,3; 0; 1; 1). Valores verificables al implementar.

Animaciones concretas:
- Presión de tarjetas y botones: escala 0,97 a 0,98 con resorte corto.
- Serie confirmada: marca de verificación dibujada en 200 ms, destello del fondo en `successContainer` durante 300 ms y háptica discreta.
- Anillo del timer: progreso continuo; en los últimos 3 segundos late suavemente.
- Listas: entrada escalonada (aparición con ligero desplazamiento hacia arriba, 40 ms entre elementos, hasta 8 elementos).
- Cifras de `MetricTile`: conteo animado de 400 ms al cambiar.
- Gráficos: trazo o barras crecen al entrar una vez; sin repetir al volver a la pantalla.
- Mapa corporal: transición de color de 300 ms al cambiar periodo; el músculo seleccionado pulsa una vez.
- Esqueleto de carga: brillo de 1.200 ms en bucle.
- Cambio de pestaña inferior: fundido, sin deslizamiento lateral.

Reglas:
- Respetar la configuración del sistema de animaciones (si está desactivada, sin animaciones) y ofrecer "Reducir animaciones" en Ajustes.
- Durante una sesión activa, animaciones mínimas: la rapidez manda.
- Nada supera los 400 ms salvo los gráficos de entrada; objetivo de 60 fotogramas por segundo.
- No animar propiedades de diseño en listas largas; preferir transformaciones y opacidad.
- Probar en un dispositivo de gama media.
