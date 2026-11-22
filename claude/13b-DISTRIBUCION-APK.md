# 13b - Distribución del APK

Complemento de `13-SEGURIDAD-Y-LEGAL.md`. El APK se distribuye por Google Drive a amigos, sin Google Play. Todo lo que dependa de políticas de Android o de Google se VERIFICA al llegar a la Fase 9 (y la verificación de desarrolladores, en la Fase 0).

## Paquete de distribución (`distribution/`)

| Archivo | Contenido |
|---|---|
| `app-release-<versión>.apk` | APK firmado (no se guarda en el repositorio; se publica en Drive) |
| `README-INSTALACION.md` | Instalación y actualización paso a paso |
| `SECURITY.md` | Cómo se protegen los datos, cómo reportar vulnerabilidades |
| `CHECKSUMS.txt` | Suma SHA-256 del APK y huella del certificado de firma |
| `CHANGELOG.md` | Cambios por versión |
| `THIRD_PARTY.md` | Licencias y atribuciones (fuentes, modelo 3D, librerías) |
| Política de privacidad y términos | Copia de `legal/` en el idioma de cada usuario |

## README de instalación: contenido

1. Requisitos: Android 10 o superior, espacio, conexión para el registro (la app funciona luego sin conexión).
2. Descargar el APK desde el enlace de Drive y comprobar la suma SHA-256 (cómo hacerlo en el teléfono).
3. Permitir la instalación de aplicaciones desde la fuente usada (por ejemplo, el navegador o el gestor de archivos) y volver a desactivarlo después.
4. Si Google Play Protect advierte por tratarse de una app no distribuida por Play, explicar por qué ocurre y cómo continuar de forma consciente; recomendar solo instalar si el APK vino del autor.
5. Primer inicio: crear cuenta, verificar correo, permisos solicitados y para qué sirven.
6. Cómo actualizar: instalar la nueva versión sobre la anterior (misma firma); no desinstalar para no perder datos locales sin sincronizar.
7. Cómo exportar datos y eliminar la cuenta.
8. Solución de problemas: correo de verificación en spam, sin conexión, permisos, ahorro de batería que mata el timer.
9. Contacto de soporte.

## Firma y versiones

- Un único keystore de release, guardado fuera del repositorio y con copia de seguridad cifrada. Perderlo impide actualizar las instalaciones existentes.
- `versionCode` incremental; `versionName` semántico.
- Cada versión publicada tiene su suma SHA-256 y su entrada en `CHANGELOG.md`.
- La app puede avisar de una versión nueva consultando un archivo de versión alojado junto al APK (fase posterior; evita auto actualizadores no verificados).

## Seguridad de la distribución

- El enlace de Drive se comparte solo con las personas invitadas; no se publica en lugares abiertos.
- No se incluyen en el APK secretos, claves ni URLs internas de administración.
- Verificar la firma del APK antes de subirlo.
- Aclarar en el README que no hay soporte oficial de Google Play ni actualizaciones automáticas.

## Verificaciones a hacer en la Fase 0

- Requisitos vigentes de Android para instalar apps fuera de Google Play y de verificación de desarrolladores, y si afectan a usuarios en Chile.
- Cambios de políticas de Health Connect y de permisos sensibles para apps no publicadas en Play.
- Resultado registrado en `02-MEMORIA.md` con fecha y fuente.
