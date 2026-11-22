# Abitus-D - Instrucciones para Claude Code

Aplicación Android nativa para registrar, planificar y analizar entrenamientos (fuerza, calistenia, pliometría, running, natación y más). Uso personal y de un grupo de amigos, distribuida como APK. Backend monolítico y base de datos PostgreSQL.

Este archivo es el punto de entrada. Toda la documentación de contexto está en la carpeta `claude/`. No trabajes de memoria: lee primero, actúa después.

## Orden de lectura al iniciar cada sesión

1. `claude/02-MEMORIA.md` y `claude/02b-MEMORIA-DECISIONES-2.md` - estado actual, decisiones tomadas y qué sigue.
2. `claude/03-REGLAS-DE-ORO.md` - reglas que nunca se rompen.
3. `claude/12-DECISIONES-PENDIENTES.md` - lo que aún no está decidido (no lo asumas).
4. El documento del área en la que vas a trabajar (ver `claude/00-INDICE.md`).

## Resumen de reglas innegociables

- Cero emojis en toda la interfaz, correos, notificaciones, textos y datos semilla. Solo iconografía del sistema de iconos definido.
- Offline-first: la base local del dispositivo es la fuente de verdad en la app; la red nunca bloquea el registro de un entrenamiento.
- Costo cero: no agregues servicios de pago ni dependencias que exijan tarjeta sin pedir confirmación.
- Seguridad: consultas siempre parametrizadas, validación estricta de entrada, contraseñas con Argon2id, ningún secreto en el repositorio.
- Ningún texto visible al usuario escrito directamente en el código: todo va en recursos localizables (es por defecto, en como segundo idioma).
- Valores en unidades del sistema internacional en la base de datos (kg, m, s); la conversión ocurre solo en la interfaz.
- Ejercicios reales y correctos: no inventes ejercicios ni indicaciones técnicas.
- Verifica versiones de librerías y APIs en la documentación oficial al instalarlas; no las fijes de memoria.
- Máximo 150 líneas por archivo (código, recursos, plantillas, scripts y documentación). Si se pasa, divide por responsabilidad, sin duplicar y sin perder rendimiento. Excepciones solo para archivos generados o de bloqueo. Detalle en la regla 31 en adelante de `claude/03-REGLAS-DE-ORO.md`.
- Al terminar cualquier tarea relevante, actualiza `claude/02-MEMORIA.md`.

## Idioma

- Documentación y comunicación con el usuario: español.
- Código, identificadores, nombres de ramas y mensajes de commit: inglés (commits convencionales).
- Interfaz: español por defecto, inglés incluido desde el inicio, más idiomas agregables sin tocar código.

## Entorno

- Máquina de desarrollo del usuario: Windows. Ruta del proyecto: `C:\Abitus-D`.
- Usa comandos compatibles con PowerShell cuando des instrucciones al usuario.
- Archivos de texto con finales de línea LF (ver `.gitattributes`).

## Estructura

```
C:\Abitus-D
  CLAUDE.md
  claude/          documentación de contexto (esta carpeta manda)
  android/         app Android (Kotlin, Jetpack Compose)
  backend/         API (TypeScript, Fastify)
  database/        migraciones, semillas, consultas
  legal/           política de privacidad y términos
  distribution/    README de instalación, seguridad, changelog
  tools/           scripts de verificación y pruebas de carga
```

Detalle completo en `claude/16-ESTRUCTURA-CARPETAS.md`.

## Cómo trabajar

- Una fase a la vez, según `claude/10-FASES-DE-TRABAJO.md`. No avances a la siguiente fase sin cumplir los criterios de aceptación de la actual.
- Cambios pequeños y verificables. Si algo es ambiguo y costoso de rehacer, pregunta antes de construir.
- Si una instrucción de estos documentos entra en conflicto con la realidad (una librería cambió, un límite gratuito cambió), no improvises en silencio: documenta el hallazgo en `claude/02-MEMORIA.md` y propón el ajuste.
