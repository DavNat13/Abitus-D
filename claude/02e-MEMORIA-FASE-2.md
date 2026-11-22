# 02e - Memoria: Fase 2 (biblioteca de ejercicios), 2026-11-22

Complemento de `02-MEMORIA.md`.

## Decisiones
| Decisión | Motivo |
|---|---|
| Semillas en JSON Lines por disciplina (`database/seeds/exercises/*.jsonl`), un ejercicio por línea; son la única fuente de verdad | Cumplir 150 líneas sin duplicar contenido |
| Textos en es y en redactados para Abitus-D; de free-exercise-db (Unlicense, commit f00c92c) se toman nombre, músculos, equipo y nivel | La fuente trae consejos desactualizados (rodillas y puntas de pie) y ejercicios con nombre equivocado (regla 16) |
| 37 ejercicios con texto propio (`license: Propio`): running, natación, ciclismo, remo y estiramientos individuales | Las bases abiertas no los cubren o necesitaban compañero |
| IDs UUID v5 derivados del slug; `content_hash` por ejercicio; `catalog_version` sube solo si algo cambió | IDs estables entre entornos y descargas incrementales |
| `sync_seq` global + trigger `bump_server_seq` en cada update | Ningún cambio se pierde en el pull incremental |
| Catálogo con todos los idiomas en una descarga, ETag y 304 | Cambiar de idioma sin conexión |
| Ejercicios propios: primero Room (dirty), push y pull con WorkManager (trabajo único, red, backoff exponencial) | Offline-first (regla 12) |
| Detección de duplicados: nombre sin mayúsculas ni tildes y hasta 1 o 2 letras de diferencia | Entregable de la Fase 2 |
| Detekt: `TooManyFunctions` ignora `@Dao` | Un DAO tiene una función por consulta |

## Hallazgos
- Room 3 renombró `TypeConverter(s)` a `ColumnTypeConverter(s)`; transacciones con `withWriteTransaction`.
- `uiautomator dump` falla a veces en API 29 con Compose; para pruebas manuales usar capturas.
- Muestra para revisión humana y notas de datos dudosos: `database/seeds/REVIEW.md` (generado por `tools/import-exercises/build-review.mjs`).

## Verificado
Backend 52 pruebas; Android 24 pruebas unitarias, Detekt y ktlint; emulador API 29: migración Room 1 a 2 con sesión conservada, descarga de 236 ejercicios, búsqueda sin tildes, detalle, ejercicio creado en modo avión y sincronizado al volver la red, nombres en inglés al cambiar el idioma.
