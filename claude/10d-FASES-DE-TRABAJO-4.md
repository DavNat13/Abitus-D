# 10d - Fases de trabajo: 7, 7b, 8, 9 y 10

Continuación de `10c-FASES-DE-TRABAJO-3.md`. La Fase 10 cierra la versión 1.0. Alineado con los bancos de ideas el 2026-11-24 (columna "Fase" de `11b` a `11e`).

## Fase 7 - Running y cardio

Entregables:
- Running con GPS (`09c`): métricas en vivo, parciales automáticos, pausa automática, resumen, ruta simplificada y trazado sin mapa base; modo ahorro de GPS.
- Avisos por voz de ritmo, récords (1, 5 y 10 km, media maratón), calzado con kilómetros.
- Intervalos, fartlek, plan de 5 o 10 km, pruebas de Cooper y de 5 km, predictor de tiempos, desnivel, consistencia de ritmo, podómetro y cadencia.
- Ciclismo, remo en ergómetro, brick, caminata y senderismo, actividad libre, partidos y competencias (resultado, minutos y sensación).
- Zonas de ritmo calculadas con una marca reciente.
- Exportar y compartir rutas en GPX.

Criterios de aceptación:
- Una carrera de 30 minutos con pantalla apagada se registra completa.
- La ruta guardada ocupa poco y se reconstruye bien.
- Ritmos y distancias respetan la unidad elegida (km o millas).

## Fase 7b - Natación y Health Connect

Entregables:
- Natación en piscina (largos, tiempos, estilos, ritmo por 100 m o 100 yardas, series guardadas, material) y aguas abiertas; swolf solo si viene de un reloj.
- Health Connect: sesiones, pasos, zonas de frecuencia cardíaca, sueño y frecuencia en reposo, con permisos y justificación; frecuencia máxima y en reposo medidas o estimadas con su margen en el perfil.
- Dashboards de cardio: ritmo, distancia y mejores marcas.

Criterios de aceptación:
- Sin Health Connect o con permisos rechazados, el resto de la app funciona sin errores.
- Metros o yardas se respetan en todas las vistas.

## Fase 8 - Cuerpo 3D interactivo

Entregables:
- Modelo glTF con piezas por grupo muscular y licencia registrada en `distribution/THIRD_PARTY.md`.
- Visor Three.js en WebView con assets locales, rotación, zoom y selección de músculo.
- Integración con periodos y escala de color; respaldo automático al mapa 2D de la Fase 6.
- Molestias por zona sobre el modelo (evolución de la lista de la Fase 4).

Criterios de aceptación:
- Funciona sin conexión en un dispositivo de gama media con fluidez aceptable.
- Tocar un músculo muestra series, ejercicios y comparación con el periodo anterior.
- Si el modelo falla al cargar, se usa el mapa 2D sin error visible.
- Si no hay modelo con licencia adecuada, la fase se cierra con el 2D y se registra la decisión.

## Fase 9 - Ajustes, cuenta y datos

Entregables:
- Ajustes: idioma, tema (incluido negro puro) y acento de color, unidades completas, timer, notificaciones, fórmula de 1RM, umbrales y contribución muscular; fuentes grandes y alto contraste; conversor de unidades rápido; ayudas contextuales la primera vez.
- Ajustes de entrenamiento: descanso por tipo de ejercicio, incremento por equipo, barra por defecto, redondeo, RPE o RIR, calentamientos, primer día de la semana, pantalla encendida; notificaciones por tipo con horario de silencio; privacidad y almacenamiento.
- Cuenta: perfil, contraseña, dispositivos activos y cierre remoto, exportación y eliminación de cuenta; bloqueo opcional con huella o PIN; recordar correo; medidor de fortaleza de contraseña; foto de perfil local.
- Datos: exportar a CSV, JSON y GPX; importar desde CSV y desde otras apps comunes; respaldo local cifrado y respaldo automático semanal; papelera de 30 días; historial de versiones de una rutina; detección de duplicados; compartir rutinas e importar las de amigos por archivo.
- Correos del catálogo (`15-CORREOS.md`) con diseño final en es y en; pantalla "Acerca de" con créditos.
- Política y términos finales en la app y en `legal/`.

Criterios de aceptación:
- Un usuario exporta sus datos y elimina su cuenta desde la app; no queda nada suyo en la base.
- Importar un CSV de prueba reproduce el historial esperado sin duplicados.
- Los correos se ven bien en los clientes principales y respetan la cuota de Gmail.
- Cambiar una unidad no altera los datos guardados.

## Fase 10 - Endurecimiento y distribución (versión 1.0)

Entregables:
- Prueba de carga con k6 de 500 usuarios simultáneos; límites y conexiones con el pooler de Neon.
- Despliegue en Vercel y Neon, keystore de release y prueba en un teléfono real (pendientes desde la Fase 0 y la 4).
- Medición de límites reales de Vercel y Neon.
- Revisión de seguridad (`13`), auditoría de dependencias y búsqueda de secretos; resolver el aviso `lintDebug` PropertyEscape.
- Accesibilidad, arranque y consumo de batería.
- `tools/export-db` y restauración probada; tarea programada de GitHub Actions.
- Llaves de acceso (passkeys) si son gratuitas y estables (VERIFICAR).
- Aviso de nueva versión del APK (consulta la última versión en GitHub) e informe de errores sin datos personales.
- APK firmado, suma de verificación, README de instalación, seguridad, política, términos y créditos en `distribution/`.
- Verificación de desarrolladores según `12`.

Criterios de aceptación:
- Cumple los presupuestos de `17-CALIDAD-Y-PRUEBAS.md`.
- Un usuario externo instala el APK siguiendo solo el README.
- Los límites gratuitos medidos dejan margen para 500 usuarios o se documenta el tope real.
- La versión se etiqueta como 1.0.0.
