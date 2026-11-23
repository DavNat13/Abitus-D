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
- Idioma: la app arranca en español siempre (AppLanguage envuelve el contexto de Activity y Application); el inglés se elige en Perfil. Antes seguía el idioma del sistema.
- Nombres de ejercicios en inglés por disciplina (Perfil, EnglishNamesStore), pedido del autor para calistenia; las explicaciones siguen en el idioma de la app.
- Filtros de la biblioteca y formulario de ejercicio propio con desplegables (AppDropdown y AppMultiDropdown) en lugar de nubes de chips, a pedido del autor.
- Detalle de ejercicio, formulario propio, rutinas, editor, programas y calendario en tarjetas por sección con icono (SectionCard), acciones secundarias en menú contextual y confirmación antes de eliminar.
- Las rutinas creadas desde plantillas guardan el nombre en el idioma activo al crearlas; no se traducen después.
- Acceso rediseñado (2026-11-23): Codex lo empezó en sandbox sin poder compilar; Claude corrigió detalles y verificó pruebas y APK. El sandbox de Codex en Windows no ve el SDK de AppData y deja un daemon de Gradle que falla también fuera; se resuelve con ./gradlew --stop.
- Inicio: tarjeta principal con degradado (HeroCard) para el entrenamiento de hoy, siguiente entrenamiento y resumen de la semana, leyendo solo Room. Perfil en tarjetas: Preferencias (idioma, tema, nombres en inglés), Información legal y Cuenta (cerrar sesión con confirmación).
- Tema: selector Claro, Oscuro o Sistema en Perfil (ThemePreference, claro por defecto según 05b); la barra de estado se adapta al tema.

## Auditoría final (2026-11-23)
- Contraste: tools/check-contrast OK en ambos temas. Corregido el botón tonal, que usaba navy como texto y en tema oscuro no se leía; ahora usa onPrimaryContainer.
- Texto al 200 %: los botones tenían alto fijo y cortaban el texto (ahora alto mínimo de 52 dp y el texto puede ocupar dos líneas); las etiquetas de la barra inferior y del selector segmentado se partían (ahora una línea con puntos suspensivos).
- Áreas táctiles: botones, chips, filas y botones de icono de 48 dp como mínimo. Excepción documentada: las celdas del calendario miden el ancho de pantalla dividido en 7 (unos 42 dp en pantallas de 360 dp), como el selector de fechas de Material.
- Reducir animaciones: con la escala de animación del sistema en 0 la app funciona sin animaciones (rememberReduceMotion).
- Lector de pantalla: revisado en código (iconos decorativos sin descripción, botones de icono con descripción, encabezados marcados). No se probó TalkBack de punta a punta: pendiente en un dispositivo real.
- Fluidez: el emulador usa renderizado por software (SwiftShader) y la versión de depuración, así que la medición no es válida. Pendiente medir con la versión de lanzamiento en un teléfono de gama media.
