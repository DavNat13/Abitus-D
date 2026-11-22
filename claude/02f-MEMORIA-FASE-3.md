# 02f - Memoria: Fase 3 (planificación), 2026-11-22

Complemento de `02-MEMORIA.md`.

## Decisiones
| Decisión | Motivo |
|---|---|
| Sincronización genérica por tabla (push y pull) con gana la última escritura por `updatedAt` y tombstones | Una sola ruta para rutinas, programas, días y calendario |
| Sin claves foráneas entre filas sincronizadas en el servidor | Las filas llegan en cualquier orden desde varios dispositivos |
| Fechas del calendario como texto ISO (`DATE` de pg devuelto como texto) | Evita corrimientos por zona horaria |
| Reordenar ejercicios con botones subir y bajar en lugar de arrastrar | Accesible, simple y sin dependencias; arrastrar puede añadirse después |
| Reprogramar conserva la fila y guarda `rescheduledFrom` | Historial de cambios del plan |
| Plantillas por slug del catálogo; si el catálogo no está descargado se avisa | Ejercicios reales del catálogo, sin inventar |
| Perfiles de equipo (Todo, Gimnasio, Casa, Parque) guardados en el dispositivo | Preferencia local; Gimnasio lista lo que falta, el resto lista lo que hay |
| Exportación ICS con eventos de día completo compartida por FileProvider | Funciona con cualquier app de calendario, sin permisos |
| Volumen planificado: serie completa al músculo principal y media al secundario | Regla estándar usada en `PlannedVolume` |

## Aplazado
- "Guardar sesión como plantilla": a la Fase 4, porque las sesiones aún no existen.
- Arrastrar para reordenar: sustituido por subir y bajar (ver arriba).
