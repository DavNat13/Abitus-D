# 09b - Frontend: offline y sincronización

Complemento de `09-FRONTEND-ANDROID.md`. El protocolo del servidor está en `07-BACKEND.md`.

## Principio

Room es la fuente de verdad en el dispositivo. Toda escritura del usuario va primero a Room y queda marcada como pendiente; la sincronización la envía cuando hay red. Nada del entrenamiento espera a la red.

## Room

- Entidades equivalentes a las tablas de `08b-DATABASE-TABLAS.md` que el usuario escribe o lee: ejercicios propios, rutinas, programas, calendario, sesiones, series, cardio, mediciones, actividad diaria, preferencias.
- Catálogo (disciplinas, músculos, equipamiento, ejercicios base y traducciones) como tablas de solo lectura, actualizadas por descarga incremental.
- Campos de sincronización en cada entidad escribible: `updatedAt`, `deletedAt`, `dirty` (pendiente de envío) y `serverSeq` (último valor recibido).
- Tabla `sync_state` con el cursor de pull y la versión del catálogo.
- Esquemas exportados y pruebas de migración para cada cambio de versión de Room.
- Identificadores UUID v7 generados en el cliente.

## Motor de sincronización

1. Disparadores: después de cada escritura local (con una pequeña espera para agrupar), al abrir la app, al recuperar red y de forma periódica con WorkManager.
2. Push: lee filas `dirty`, las envía en lotes de hasta 200, y marca como sincronizadas las aceptadas. Las rechazadas se registran con el motivo y no se reintentan sin cambio.
3. Pull: pide cambios desde el cursor guardado, los aplica en una transacción de Room y avanza el cursor. Repite mientras `hasMore`.
4. Orden: primero push, luego pull, para minimizar conflictos.
5. Reintentos con espera exponencial y restricción de red conectada. Sin trabajos duplicados (trabajo único con nombre).
6. Si el servidor indica que el cursor es demasiado antiguo, se hace una resincronización completa conservando lo pendiente de envío.

## Conflictos

- La última modificación por fila gana (`updatedAt`); el cliente respeta la respuesta del servidor.
- Los registros de sesiones y series son de solo adición en la práctica; no se esperan conflictos.
- Un borrado local pendiente no se pisa con una versión remota más antigua.
- Los cambios hechos en otro dispositivo aparecen tras el pull sin interrumpir lo que el usuario está haciendo; en la sesión activa no se reemplazan filas que se están editando.

## Estado visible

- `SyncStatusChip`: sincronizado, sincronizando, pendiente (con número de cambios), sin conexión.
- Mensajes calmados: "Tus datos están guardados en este dispositivo".
- En Ajustes de cuenta: hora de la última sincronización y botón para sincronizar ahora.

## Sesión activa y recuperación

- Cada serie confirmada se escribe en Room de inmediato; no hay datos solo en memoria.
- El estado de la sesión activa (ejercicio actual, timer en curso, hora de inicio) se persiste para recuperarse tras la muerte del proceso o un reinicio.
- Al abrir la app con una sesión sin cerrar, se ofrece continuarla o finalizarla.

## Catálogo

- Descarga inicial al primer inicio de sesión con indicador de progreso; luego incremental por versión y ETag.
- El idioma del catálogo sigue al idioma de la app; al cambiar de idioma se descarga o se reutiliza la traducción disponible.
- Los ejercicios propios del usuario se mezclan con los del catálogo en la biblioteca, con una marca visual sutil.

## Exportación y respaldo

- Exportación completa desde Ajustes de cuenta: pide los datos al servidor (si hay red) o genera el archivo desde Room (JSON y CSV), y lo comparte con el selector de Android.
- Importación de un archivo exportado propio: fase posterior (`11-IDEAS-Y-FUTURO.md`).

## Pruebas obligatorias

- Escribir sin conexión, reiniciar la app, recuperar red y verificar que todo llega al servidor sin duplicados.
- Dos dispositivos editando la misma fila: gana la modificación más reciente.
- Borrado en un dispositivo se refleja en el otro.
- Fallo a mitad de un lote: no se pierden ni duplican filas.
- Cursor expirado: resincronización completa sin perder cambios pendientes.
