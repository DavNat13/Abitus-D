# 05b - Sistema de diseño: colores, tipografía, forma e iconos

Complemento de `05-UI-UX.md`. Sombras, tarjetas, botones, campos y animaciones están en `05c-UI-COMPONENTES-Y-MOVIMIENTO.md`. Fuente única de valores: ninguna pantalla usa colores, tamaños ni radios sueltos.

## Paleta de marca (definida por el autor)

| Nombre | Valor | Rol |
|---|---|---|
| ink | #101632 | Texto principal, fondos oscuros, sombras tintadas |
| navy | #001667 | Marca profunda: estados presionados, degradados, énfasis |
| primary | #0135A8 | Acento principal: botones, enlaces, selección, gráficos |
| green | #547F43 | Estados positivos: completado, PR, progreso, éxito |
| lavender | #F7F4FF | Superficie suave: campos, chips, secciones alternas |
| white | #FFFFFF | Fondo de pantalla y tarjetas (tema claro) |

Reglas de uso: el azul `primary` es el único acento de acción; el verde se reserva para lo positivo y nunca para botones de navegación; `navy` e `ink` no se usan como texto pequeño sobre `primary`.

## Contrastes verificados por cálculo (WCAG, 2026-11-22)

| Combinación | Ratio | Uso permitido |
|---|---|---|
| ink sobre blanco | 17,8 a 1 | Texto de cualquier tamaño |
| navy sobre blanco | 16,0 a 1 | Texto de cualquier tamaño |
| primary sobre blanco | 10,1 a 1 | Texto y botones |
| blanco sobre primary | 10,1 a 1 | Texto de botón |
| green sobre blanco | 4,7 a 1 | Texto normal sí (AA), con poco margen |
| green sobre lavender | 4,3 a 1 | NO para texto normal; usar `greenText` |
| blanco sobre green | 4,7 a 1 | Texto de botón verde (AA) |

Antes de cerrar la Fase 1, ejecutar un script de contraste sobre todos los tokens y registrar el resultado en la memoria.

## Tema claro (por defecto)

| Token | Valor | Uso |
|---|---|---|
| background | #FFFFFF | Fondo de pantalla |
| surface | #FFFFFF | Tarjetas, barras, hojas |
| surfaceSoft | #F7F4FF | Campos, chips, secciones alternas |
| outline | #DEDEE2 | Bordes decorativos de tarjetas y separadores |
| outlineStrong | #BCBEC6 | Bordes de énfasis |
| inputBorder | #888B99 | Borde de campos (contraste 3 a 1 o más) |
| textPrimary | #101632 | Texto principal |
| textSecondary | #5C6174 | Texto secundario (6,2 a 1) |
| textDisabled | #9FA2AD | Deshabilitado |
| primary | #0135A8 | Acción principal |
| primaryPressed | #001667 | Estado presionado |
| onPrimary | #FFFFFF | Texto sobre primary |
| primaryContainer | #E6EBF6 | Fondos de énfasis suave, indicador de navegación |
| onPrimaryContainer | #001667 | Texto sobre primaryContainer |
| success | #547F43 | Relleno de estados positivos |
| greenText | #456B35 | Texto verde (6,2 a 1 sobre blanco, 5,3 sobre tinte) |
| successContainer | #EAF0E8 | Fondo suave de éxito |
| error | #B3261E | Errores y acciones destructivas |
| errorContainer | #FBEAE9 | Fondo suave de error |
| warning | #A15C00 | Advertencias (5,2 a 1) |
| warningContainer | #FBF1E1 | Fondo suave de advertencia |

`error` y `warning` no estaban en la paleta del autor; se añaden solo por necesidad semántica y de accesibilidad. Se pueden ajustar con su aprobación.

## Tema oscuro (opcional)

Derivado de `ink`. El tema claro es el predeterminado; el usuario puede elegir claro, oscuro o seguir el sistema.

| Token | Valor |
|---|---|
| background | #101632 |
| surface | #1E243E |
| surfaceRaised | #282D46 |
| outline | #363B53 |
| inputBorder | #707384 |
| textPrimary | #F7F4FF |
| textSecondary | #B7B9C2 |
| textDisabled | #707384 |
| primary | #7FA1F2 (7,0 a 1 sobre ink) |
| onPrimary | #101632 |
| primaryContainer | #1B2B63 |
| success | #8DB879 |
| error | #F2837B |
| warning | #F0B429 |

## Degradado de marca

`brandGradient`: de `navy` (#001667) a `primary` (#0135A8), a 135 grados. Uso moderado: tarjeta principal de Inicio, encabezado de sesión activa, pantalla de bienvenida. El texto sobre el degradado es blanco. Nunca en superficies pequeñas ni en botones de acción comunes.

## Escala del mapa de calor muscular

Escala secuencial de un solo tono (azul de marca). El número exacto aparece siempre al tocar y en la leyenda.

| Nivel | Series por semana (promedio) | Claro | Oscuro |
|---|---|---|---|
| 0 | 0 | #EEEFF1 | #282D46 |
| 1 | 1 a 4 | #CCD7EE | #283661 |
| 2 | 5 a 9 | #8DA4D8 | #3F5590 |
| 3 | 10 a 14 | #4068BE | #5B7BC8 |
| 4 | 15 o más | #0135A8 | #7FA1F2 |

Músculo seleccionado: contorno de 2 dp en `navy` (claro) o `lavender` (oscuro). Los umbrales son constantes configurables.

## Tipografía

Fuente: Roboto (licencia Apache 2.0). Es la tipografía nativa de Android, sobria y muy legible en pantallas pequeñas y a distancia, y encaja con el estilo limpio de la marca.

- Se incluye en la app (pesos 400, 500 y 700) para que la interfaz se vea igual en dispositivos cuyo fabricante cambia la fuente del sistema. Roboto no trae un peso semibold: se usa Medium (500) y Bold (700).
- Cifras: comprobar visualmente que las columnas de números (series, cargas, tiempos) se alinean; si no, activar numerales tabulares (`tnum`). VERIFICAR con la versión de la fuente incluida.
- Para el timer en pantalla completa se puede usar Roboto Mono solo si hace falta estabilidad de ancho; por defecto, Roboto Bold.

| Estilo | Tamaño / interlineado | Peso | Uso |
|---|---|---|---|
| displayNumeric | 48 / 56 | Bold | Timer y cifras principales |
| headline | 28 / 34 | Bold | Títulos de bienvenida y resúmenes |
| titleLarge | 22 / 28 | Medium | Título de pantalla |
| titleMedium | 18 / 24 | Medium | Título de sección y tarjeta |
| body | 16 / 24 | Regular | Texto general |
| label | 14 / 20 | Medium | Etiquetas, botones, chips |
| caption | 12 / 16 | Regular | Notas y metadatos |

Registrar la fuente y su licencia en `distribution/THIRD_PARTY.md`.

## Espaciado y forma

- Base de 4 dp. Escala: 4, 8, 12, 16, 20, 24, 32, 48. Margen de pantalla 20 dp; separación entre tarjetas 12 a 16 dp.
- Radios: 12 (campos y botones), 16 (tarjetas), 24 (tarjeta principal y hojas inferiores), completo (chips y píldoras).
- Bordes: 1 dp `outline` en tarjetas; 1,5 dp en botones secundarios; 2 dp `primary` en foco.
- Objetivos táctiles de 48 dp como mínimo; botones principales durante el entrenamiento, 56 dp o más.

## Iconografía

- Un único sistema: Material Symbols, estilo Outlined, peso 400, tamaño base 24 dp, convertidos a vectores de la app. Se exponen con nombres semánticos en `AppIcons` (por ejemplo `AppIcons.Timer`, `AppIcons.Discipline.Running`); las pantallas nunca usan archivos de icono directamente.
- Iconos de disciplinas con el mismo trazo y tamaño; hasta tener un set propio, usar equivalentes de Material Symbols y marcarlos para reemplazo.
- Prohibido mezclar familias de iconos o usar emojis o caracteres Unicode decorativos como icono.
- Color de icono solo con tokens: textPrimary, textSecondary, primary, success, error, onPrimary.
- Icono dentro de un contenedor circular suave (`primaryContainer` o `successContainer`) para destacar en tarjetas y estados vacíos.
