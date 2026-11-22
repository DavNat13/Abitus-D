# 05 - UI y UX

Diseño profesional, minimalista y consistente. Los valores del sistema de diseño están en `05b-UI-TOKENS.md` (colores, tipografía, forma, iconos) y `05c-UI-COMPONENTES-Y-MOVIMIENTO.md` (sombras, tarjetas, botones, campos, animaciones). Ninguna pantalla usa valores sueltos.

## Principios

1. Rapidez en el gimnasio: pocos toques, números grandes, una mano.
2. Jerarquía visual clara: fondo blanco, azul de marca como único acento de acción, verde solo para estados positivos, resto neutro.
3. Estilo moderno: tarjetas con borde fino y sombra tintada, animaciones breves y con propósito, mucho aire y texto corto.
4. Cero emojis en cualquier parte de la interfaz.
5. Funcional sin conexión: siempre se ve el estado de sincronización, sin alarmar.
6. Accesible: contraste AA, objetivos táctiles amplios, texto escalable.

## Navegación

Barra inferior con 4 secciones, con el icono y la etiqueta:

| Sección | Contenido |
|---|---|
| Inicio | Entrenamiento de hoy o próximo, resumen semanal, último PR, estado de progreso |
| Entrenar | Programas, rutinas, calendario, iniciar sesión (acción principal destacada) |
| Progreso | Dashboards, evolución, historial, cuerpo 2D y 3D |
| Biblioteca | Ejercicios por disciplina, búsqueda, filtros, ejercicios propios |

Acceso al Perfil desde el icono superior derecho de Inicio. Perfil contiene Ajustes de la app y Ajustes de la cuenta. Durante un entrenamiento activo, una barra persistente permite volver a la sesión desde cualquier sección.

## Flujos principales

### Acceso
1. Bienvenida: dos acciones, "Crear cuenta" e "Iniciar sesión". Sin tutorial largo.
2. Registro: correo, contraseña, aceptación de términos y política de privacidad (enlaces visibles). Indicador de fortaleza de contraseña.
3. Verificación: código de 6 dígitos recibido por correo, con reenvío con espera visible.
4. Onboarding corto y omitible: unidades, disciplinas que practica, objetivo, datos corporales opcionales.
5. Inicio.
- Login: correo y contraseña. Si es un dispositivo nuevo, el usuario recibe un correo de aviso.
- Recuperar contraseña: correo, código, nueva contraseña.

### Entrenamiento activo (la pantalla más importante)
- Encabezado: tiempo transcurrido y "Finalizar".
- Tarjeta del ejercicio actual: nombre, objetivo planificado y "Última vez" (por ejemplo carga, repeticiones y RPE de la sesión anterior).
- Filas de series: número, carga, repeticiones, RPE o RIR (conmutable), confirmar. Un toque confirma la serie planificada; solo se edita lo que cambió. Pasos rápidos de carga configurables (por ejemplo 1,25 y 2,5 kg).
- Al confirmar: arranca el descanso automático del ejercicio. Barra de descanso persistente con ajuste de 15 s y omitir.
- Agregar nota, agregar ejercicio, reordenar, agrupar en superserie.
- La pantalla permanece encendida durante la sesión.
- Al finalizar: resumen con duración, volumen, series, posibles PRs y notas.

### Cardio (running y natación)
- Running: iniciar, métricas en vivo (distancia, tiempo, ritmo), pausa, finalizar, resumen con ruta.
- Natación en piscina: largo de piscina, contador de largos con un toque, tiempo por largo, estilo.
- Natación en aguas abiertas: GPS como running.

### Progreso
- Pregunta guía en cada vista: "estoy progresando", "entreno suficiente", "qué músculos descuido".
- Veredicto por ejercicio: Progresando, Estable, Retroceso, Sin datos suficientes (reglas en `09-FRONTEND-ANDROID.md`).
- Cuerpo humano interactivo: selector de periodo (semana, mes, año), colores por series, tocar un músculo para ver detalle.

## Microcopy

- Español neutro, frases cortas y directas, sin signos de exclamación en exceso, sin emojis.
- Mensajes de error dicen qué pasó y qué hacer. Ejemplo: "No pudimos sincronizar. Tus datos están guardados en este dispositivo."
- Aviso sin conexión discreto: "Sin conexión. Tus datos se guardan en este dispositivo."
- Todo texto vive en `strings.xml` (es y en).

## Accesibilidad

- Contraste AA como mínimo en ambos temas.
- Texto escalable hasta 200 por ciento sin romper el diseño.
- Descripciones de contenido en iconos que no tengan texto.
- Navegación posible con lector de pantalla en las pantallas principales.
- No depender solo del color (mapa de calor con cifras, estados con texto o icono).
- Háptica discreta al confirmar serie y al terminar el descanso (configurable).

## Verificación automática

- `tools/check-no-emoji`: falla si encuentra emojis en recursos de texto, plantillas de correo, semillas y cadenas del código.
- Revisión de tokens: ninguna pantalla debe contener colores hexadecimales literales ni tamaños de texto sueltos.
