# 16 - Estructura de carpetas

Raíz: `C:\Abitus-D`. Cada capa (base de datos, backend, frontend) es independiente y se comunica por contratos explícitos: SQL y migraciones para la base, OpenAPI para la API.

```
C:\Abitus-D
  CLAUDE.md                      punto de entrada para Claude Code
  claude/                        documentación de contexto (este directorio)
  .gitignore  .gitattributes  .env.example  docker-compose.yml  README.md

  database/
    migrations/                  SQL numerado e inmutable (001_..., 002_...)
    seeds/                       catálogo por disciplina (es y en) y REVIEW.md
    queries/                     consultas críticas documentadas
    scripts/                     aplicar migraciones, cargar semillas, respaldo

  backend/
    src/
      app/                       arranque, plugins, configuración
      modules/
        auth/  account/  catalog/  sync/  mail/  health/
          (cada uno: routes, schemas, service, repository)
      shared/                    errores, validación, registro, tiempo
      mail-templates/            plantillas React Email y textos por idioma
    test/                        pruebas unitarias e integración
    Dockerfile  package.json  tsconfig.json

  android/
    app/src/main/java/<applicationId>/   core/ y feature/ (ver 09)
    app/src/main/res/            values (es), values-en, drawable (iconos), font
    app/src/main/assets/         visor 3D y modelo (Fase 8)
    app/src/test/  app/src/androidTest/
    gradle/libs.versions.toml

  legal/                         PRIVACY_POLICY (es, en), TERMS (es, en)
  distribution/                  README-INSTALACION, SECURITY, CHECKSUMS, CHANGELOG, THIRD_PARTY
  tools/                         check-no-emoji, check-file-length, carga (k6)
```

## Convenciones de nombres

- Android: paquetes en minúsculas; clases en PascalCase; funciones y variables en camelCase; recursos en snake_case con prefijo (`ic_`, `string` por pantalla, por ejemplo `workout_`).
- Backend: archivos en kebab-case (por ejemplo `auth.routes.ts`, `auth.schemas.ts`); tipos en PascalCase.
- Base de datos: tablas y columnas en snake_case; migraciones `NNN_descripcion.sql`.
- Documentación: archivos numerados en `claude/`, complementarios con sufijo `b`, `c`.

## Reglas de organización

1. Máximo 150 líneas por archivo (regla 31). Dividir por responsabilidad con nombres descriptivos.
2. Una pantalla de Android: `XScreen.kt` (estructura), `XSections.kt` (componentes), `XViewModel.kt`, `XState.kt`.
3. Un endpoint del backend: ruta delgada, esquema, servicio, repositorio; sin SQL en rutas ni lógica en repositorios.
4. Nada de carpetas "utils" genéricas; cada utilidad vive en un archivo con nombre específico y dentro del módulo que la usa.
5. Las capas no se importan entre sí: la app no conoce el backend más allá del contrato; el backend no conoce Android.
6. Lo que sea compartido entre capas es documentación y contratos, no código.

## Lo que no va al repositorio

Keystores, archivos `.env` con valores reales, APK, volcados de base de datos, datos personales, claves, tokens, artefactos de compilación.

## Archivos de raíz

- `.gitignore` cubre Android, Node, entornos y archivos de sistema.
- `.gitattributes` fuerza finales de línea LF en texto.
- `.env.example` lista las variables sin valores.
- `docker-compose.yml` levanta PostgreSQL local y, opcionalmente, un servidor de pruebas de correo.
- `README.md` resume cómo levantar cada capa en local y enlaza a `claude/`.
