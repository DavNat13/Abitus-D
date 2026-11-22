# 08c - Base de datos: datos semilla del catálogo

Complemento de `08-DATABASE.md`. Cómo se cargan y qué cubren las semillas.

## Datos semilla (catálogo)

### Formato
- Archivos versionados en `database/seeds/` (por disciplina), por ejemplo JSON, con `slug` estable, traducciones es y en, músculos con rol, equipamiento y relaciones.
- Un cargador idempotente (upsert por `slug`) que incrementa `catalog_version`. Nunca borra ejercicios existentes: se marcan `deleted_at` si se retiran.
- Cada ejercicio incluye: nombre, resumen, preparación, ejecución, indicaciones, errores comunes, notas de seguridad, tipo de métrica, patrón de movimiento, dificultad, músculos principales y secundarios, equipamiento, progresiones y regresiones.

### Reglas de calidad
- Ejercicios reales y bien descritos. Si no hay certeza de un dato técnico, no se incluye y se marca en un archivo de revisión (`database/seeds/REVIEW.md`).
- Nombres comunes en español y en inglés; sin emojis.
- Evitar duplicados: un movimiento, un registro; variantes como ejercicios relacionados.

### Cobertura inicial por disciplina (meta de la Fase 2)
Calistenia (aproximadamente 50), powerlifting (aproximadamente 25), hipertrofia (aproximadamente 80), halterofilia (aproximadamente 20), pliometría (aproximadamente 30), running (aproximadamente 15 entre tipos de entrenamiento y ejercicios de técnica y fuerza para corredores), natación (aproximadamente 25 entre estilos, series y ejercicios técnicos), ciclismo (aproximadamente 10), remo (aproximadamente 10), core (aproximadamente 30), movilidad y estiramientos (aproximadamente 30), funcional (aproximadamente 20). Las cantidades son orientativas; manda la calidad.

### Grupos musculares base
Frente: pecho, deltoides anterior, deltoides lateral, bíceps, antebrazos (flexores), abdominales (recto abdominal), oblicuos, cuádriceps, aductores, tibial anterior. Espalda: trapecio superior, trapecio medio e inferior, deltoides posterior, dorsales, romboides, lumbares (erectores), tríceps, antebrazos (extensores), glúteos, isquiotibiales, gemelos y sóleo. Cada uno con `mesh_key` y `svg_key`.

### Disciplinas base
Las del archivo `01-CONTEXTO.md`.

Fuentes, licencias y atribución de ejercicios importados: `08d-DATABASE-FUENTES-EJERCICIOS.md`.
