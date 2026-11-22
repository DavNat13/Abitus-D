# Abitus-D

App Android nativa para registrar, planificar y analizar entrenamientos, con backend propio. Uso personal y de un grupo de amigos. Documentación de contexto en [`claude/`](claude/00-INDICE.md); punto de entrada en [`CLAUDE.md`](CLAUDE.md).

## Requisitos de desarrollo (Windows)

- Node 22 LTS y npm (gestor elegido para todo el repositorio).
- Docker Desktop.
- JDK 17 o superior (probado con JDK 22) y SDK de Android en `%LOCALAPPDATA%\Android\Sdk` (platform 37.1, build-tools 36.0.0, emulador, imagen `android-29;google_apis;x86_64`).

## Levantar en local (PowerShell, desde `C:\Abitus-D`)

1. Copia `.env.example` a `.env` y completa los valores locales (nunca se sube).
2. Base de datos:

```powershell
docker compose up -d postgres
```

```powershell
cd database; npm install; npm run migrate; cd ..
```

3. Backend (`http://localhost:3000/health` y `/ready`):

```powershell
cd backend; npm install; npm run dev
```

4. App Android (con `JAVA_HOME` apuntando a un JDK 17 o superior):

```powershell
cd android; .\gradlew.bat assembleDebug testDebugUnitTest detekt ktlintCheck
```

## Verificaciones antes de cerrar cualquier tarea

```powershell
.\tools\check-no-emoji.ps1
```

```powershell
.\tools\check-file-length.ps1
```

```powershell
cd backend; npm run typecheck; npm run lint; npm run format:check; npm test
```
