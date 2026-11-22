# 08d - Fuentes de ejercicios y licencias

Complemento de `08c-DATABASE-SEMILLAS.md`. Verificado el 2026-11-22. Las licencias se VERIFICAN de nuevo antes de importar y el resultado se anota en `distribution/THIRD_PARTY.md`.

## Candidatas

| Fuente | Contenido | Licencia | Idiomas | Uso propuesto |
|---|---|---|---|---|
| free-exercise-db (yuhonas) | Más de 800 ejercicios en JSON, con músculos, equipo, nivel e instrucciones; trae imágenes | Unlicense (dominio público) para los datos; dudas sobre el origen de las imágenes | Inglés | Base principal de ejercicios de fuerza. Imágenes: no incluir hasta aclarar su licencia |
| wger | Base colaborativa de ejercicios con traducciones, incluido español | Datos CC-BY-SA; el código es AGPL | Varios | Referencia y traducciones al español con atribución, tras revisar el efecto de "compartir igual" |
| exercemus/exercises | Ejercicios en JSON | Licencia distinta por ejercicio | Inglés | Solo si la licencia de cada registro lo permite |
| RepDB (nivel gratuito) | API de ejercicios | Atribución obligatoria; no permite redistribuir el conjunto | Inglés | No usar para la semilla; solo consulta puntual si hiciera falta |

Otras bases con medios de licencia restrictiva se descartan para el empaquetado.

## Estrategia

1. Importar los registros de free-exercise-db por un script `tools/import-exercises` hacia el formato de `08c` (slug estable, `source`, `license`).
2. Las traducciones al español se redactan o revisan a mano; se pueden contrastar con wger, citando la atribución si se copia texto.
3. Cada ejercicio guarda `source` y `license`. Los registros de fuentes con "compartir igual" quedan marcados para poder separarlos o regenerarlos.
4. Los datos propios (disciplinas de natación, running, ciclismo, remo, movilidad) se escriben a mano, porque las bases abiertas cubren sobre todo fuerza.
5. Ilustraciones: por defecto, iconos y texto (ver `12-DECISIONES-PENDIENTES.md`). Si se agregan imágenes, solo con licencia clara.

## Atribución

- Archivo `distribution/THIRD_PARTY.md` con fuente, licencia y enlace.
- Pantalla "Acerca de" con los créditos de las fuentes que lo exijan.
- Como el APK se comparte solo con amigos, aun así se cumplen las condiciones como si fuera público.
