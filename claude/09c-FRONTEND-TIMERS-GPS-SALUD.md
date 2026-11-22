# 09c - Frontend: timers, GPS y salud

Complemento de `09-FRONTEND-ANDROID.md`. Todo comportamiento de Android que dependa de la versión del sistema (servicios en primer plano, alarmas exactas, permisos) se VERIFICA en la documentación oficial vigente antes de implementarlo.

## Timers

Tipos: descanso (cuenta regresiva), intervalos (EMOM, Tabata, trabajo y descanso personalizados con rondas), cronómetro libre con vueltas.

- Un `WorkoutTimerManager` único es la fuente de verdad. Calcula con `SystemClock.elapsedRealtime`, no contando ticks; así sobrevive a pausas del proceso.
- Se ejecuta dentro de un servicio en primer plano mientras hay sesión o timer activo, con notificación persistente y acciones: sumar 15 s, restar 15 s, omitir, pausar.
- Tipo de servicio en primer plano apropiado por caso (salud, ubicación u otro): VERIFICAR requisitos y permisos vigentes.
- Al terminar el descanso: sonido y vibración según preferencias del usuario, notificación y retorno a la sesión.
- Respaldo con alarma programada si el servicio es detenido por el sistema; las alarmas exactas requieren permiso en versiones recientes (VERIFICAR) y deben pedirse solo si hace falta.
- Descanso automático al confirmar una serie, con la duración del ejercicio (o la planificada).
- La pantalla permanece encendida durante la sesión activa.
- Pruebas: pantalla apagada, app en segundo plano, ahorro de batería, rotación y reinicio del proceso.

## Running y natación en aguas abiertas (GPS)

- `FusedLocationProviderClient` con intervalo de 1 a 2 segundos; descartar puntos con precisión pobre (por ejemplo, peor de 25 a 30 m) y saltos imposibles.
- Servicio en primer plano de tipo ubicación con notificación visible. Permiso de ubicación solicitado con explicación; solicitar ubicación en segundo plano solo si es realmente necesario (VERIFICAR).
- Métricas: distancia (`Location.distanceBetween`), tiempo total, tiempo en movimiento, ritmo instantáneo suavizado con media móvil, ritmo medio, desnivel (barómetro si existe).
- Pausa manual y pausa automática opcional.
- Puntos crudos en Room mientras dura la sesión. Al finalizar: simplificar la ruta (algoritmo de Douglas-Peucker), codificarla como polilínea, guardarla en `cardio_tracks` y borrar los puntos crudos tras sincronizar.
- Dispositivos sin servicios de Google: usar `LocationManager` como respaldo.
- Sin mapa de fondo de pago: la ruta se dibuja como trazo sobre fondo neutro con los tokens del tema.

## Natación

- Piscina: el GPS no funciona. El usuario fija el largo de piscina (25 m, 50 m o personalizado) y cuenta largos con un toque (botón grande o tecla de volumen opcional). Se registra tiempo por largo, estilo y distancia total (largos por largo de piscina).
- Series de natación: distancia y tiempo objetivo por serie, descanso entre series.
- Aguas abiertas: GPS como running.
- SWOLF y brazadas: solo si se obtienen de un reloj mediante Health Connect; no se estiman.

## Health Connect

- Estado de disponibilidad con la API del cliente: en Android 14 o superior está integrado; en Android 10 a 13 requiere la aplicación Health Connect instalada. Si no está, la función se explica y se ofrece instalarla sin bloquear el resto de la app.
- Permisos solicitados con el contrato oficial de permisos y una pantalla de justificación de uso (requerida por la plataforma).
- Lectura inicial: pasos, frecuencia cardíaca, sesiones de ejercicio y distancia. Escritura de sesiones completadas: opcional y posterior.
- Importación: tras un entrenamiento, ofrecer asociar frecuencia cardíaca promedio y máxima de la ventana de la sesión; importar sesiones externas (por ejemplo, de un reloj) como cardio con fuente `health_connect`.
- Privacidad: solo se guardan resúmenes (promedio y máximo, pasos diarios). No se suben series crudas de frecuencia cardíaca. Todo con consentimiento explícito y desactivable en Ajustes.
- Versión de la librería: la estable vigente (la 1.1.0 era la estable en 2026-11; VERIFICAR).

## Notificaciones

- Canales separados: timer de descanso, sesión activa, recordatorios, cuenta y seguridad.
- Permiso de notificaciones (Android 13 o superior) solicitado con contexto.
- Recordatorios de entrenamiento configurables y desactivables; sin textos con emojis.

## Permisos: orden y explicación

1. Notificaciones: al iniciar el primer timer.
2. Ubicación: al iniciar la primera actividad con GPS.
3. Health Connect: al activarlo en Ajustes o en el primer cardio.
4. Cada permiso con pantalla explicativa previa y alternativa si se rechaza.
