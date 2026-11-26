# Licencias y atribuciones de terceros

Abitus-D incluye o usa los siguientes componentes. Lista vigente a la Fase 1 (2026-11-22).

## Recursos incluidos en la app

| Componente | Uso | Licencia | Fuente |
|---|---|---|---|
| Roboto (fuente variable) | Tipografía de la interfaz | SIL Open Font License 1.1 | github.com/google/fonts, carpeta ofl/roboto |
| Material Symbols Outlined | Iconos de la interfaz (convertidos a vectores) | Apache License 2.0 | github.com/google/material-design-icons |

## Mapa corporal: Body Muscles

Fuente: https://github.com/vulovix/body-muscles, revisión `15c8085ee97cb94c51f92c75224416b15f955b5c`.
Datos: `src/data/muscles.front.ts` y `src/data/muscles.back.ts`.
Licencia: Apache License, Version 2.0 (https://www.apache.org/licenses/LICENSE-2.0).
La copia íntegra de LICENSE se incluye en la app como `body_muscles_license.txt`, accesible desde Créditos.
Modificaciones de Abitus-D: conversión de trazos a XML y Path de Compose, coordenadas de presentación, agrupación para el catálogo y colores interactivos. No se incorpora la librería React.

NOTICE original:

Body Muscles
Copyright 2024 Ivan Vulović

This product includes software developed by Ivan Vulović.
https://github.com/vulovix/body-muscles

## Datos del catálogo de ejercicios (fuentes)

| Fuente | Uso | Licencia | Enlace |
|---|---|---|---|
| free-exercise-db (yuhonas), commit f00c92c | Origen de 199 ejercicios: nombre en inglés, músculos, equipo y nivel. Las indicaciones en español e inglés son texto propio, revisado para evitar consejos desactualizados | Unlicense (dominio público) | github.com/yuhonas/free-exercise-db |
| Abitus-D | 37 ejercicios escritos para el proyecto (running, natación, ciclismo, remo y estiramientos individuales) | Propio | Este repositorio |

Cada ejercicio guarda su `source` y su `license` en la base de datos. No se incluyen imágenes de la fuente.

## Librerías de la app Android

Jetpack Compose, Material 3, Activity, Lifecycle, Navigation 3, Room 3, SQLite, DataStore y Hilt (Apache License 2.0, Google y AndroidX); Retrofit y OkHttp (Apache License 2.0, Square); kotlinx.serialization y kotlinx.coroutines (Apache License 2.0, JetBrains).

## Librerías del servidor

Fastify y sus plugins, fastify-type-provider-zod, Zod, Kysely, node-postgres, jose, Nodemailer (MIT No Attribution), @node-rs/argon2, uuid, React y React Email. Todas con licencias MIT o compatibles con distribución gratuita.

## Textos legales

La política de privacidad y los términos de uso son borradores propios (carpeta `legal/`); deben revisarse con un profesional si la app se comparte fuera del círculo de amigos del autor.
