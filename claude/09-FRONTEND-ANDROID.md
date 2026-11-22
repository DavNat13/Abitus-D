# 09 - Frontend Android: arquitectura

Carpeta: `android/`. Kotlin, Jetpack Compose, Material 3 personalizado con los tokens de `05b-UI-TOKENS.md`. Complementos: `09b` (offline y sincronización), `09c` (timers, GPS, salud), `09d` (analítica y cuerpo 3D).

## Configuración base

- minSdk 29 (Android 10). targetSdk y compileSdk: la versión estable vigente (VERIFICAR).
- Un solo módulo Gradle (`app`) organizado por paquetes de funcionalidad. Se puede dividir en módulos más adelante si el tamaño lo exige.
- applicationId: `com.abitusd.app` (decidido; cambiarlo después rompe las actualizaciones, así que no se modifica). Namespace de código igual.
- Build de release con minificación (R8) y firma con keystore fuera del repositorio.
- Catálogo de versiones de Gradle (`libs.versions.toml`) como única fuente de versiones.

## Arquitectura

- Presentación (Compose + ViewModel), dominio (casos de uso y modelos), datos (repositorios, Room, red, sincronización).
- Flujo de datos unidireccional: el ViewModel expone un `StateFlow<UiState>`; la interfaz envía eventos; los efectos de una sola vez (navegación, mensajes) van por un canal de eventos.
- La interfaz solo lee de Room mediante repositorios. La red no se consulta desde pantallas: la sincronización actualiza Room y la interfaz reacciona.
- Inyección con Hilt. Corrutinas con dispatchers inyectados para poder probar.
- Cada pantalla: archivo de pantalla, archivo de secciones o componentes, ViewModel, estado. Respetar el máximo de 150 líneas por archivo.

## Estructura de paquetes

```
app/src/main/java/<applicationId>/
  core/
    designsystem/   tema, tokens, tipografía, AppIcons, componentes base
    i18n/           idioma por app, unidades, formateo
    network/        Retrofit, interceptores, errores
    database/       Room: base, DAO, entidades, migraciones
    sync/           motor de sincronización y WorkManager
    security/       almacenamiento cifrado de tokens
    di/             módulos de Hilt
    util/           utilidades con nombre específico
  feature/
    auth/           bienvenida, registro, verificación, login, recuperación
    onboarding/     unidades, disciplinas, objetivo
    home/           inicio
    train/          programas, rutinas, calendario
    workout/        sesión activa, series, timers, resumen
    cardio/         running y natación (GPS, largos)
    library/        biblioteca de ejercicios y ejercicios propios
    progress/       dashboards, historial, mapa corporal
    health/         Health Connect
    profile/        perfil, ajustes de app y de cuenta, privacidad
```

Dentro de cada funcionalidad: `ui/`, `domain/`, `data/`.

## Navegación

- Navigation Compose con rutas tipadas. Barra inferior de 4 secciones (ver `05-UI-UX.md`).
- Estado de la barra y de la sesión activa persistente entre secciones.
- Flujo de acceso separado del flujo principal; el token válido decide el destino inicial.

## Autenticación en el cliente

- Tokens guardados cifrados con una clave del Android Keystore (VERIFICAR la alternativa vigente a `androidx.security.crypto`, que puede estar obsoleta).
- Interceptor de OkHttp: añade el token; ante 401 intenta renovar una sola vez, con exclusión mutua para evitar renovaciones simultáneas; si falla, cierra la sesión local.
- Cerrar sesión: avisa si hay cambios sin sincronizar; al confirmar, limpia datos locales del usuario y tokens.
- Identificador de dispositivo generado en la instalación; nombre legible para mostrar en Ajustes.

## Idiomas y unidades

- Cadenas en recursos: `values` (español por defecto) y `values-en`. Más idiomas se agregan solo con archivos nuevos.
- Idioma por app con la API de localización de AppCompat (VERIFICAR comportamiento vigente), sincronizado con la preferencia del usuario.
- Textos de ejercicios: vienen del catálogo traducido, guardado en Room por idioma. Cadena de respaldo: idioma elegido, inglés, español, `slug`.
- Conversión de unidades solo en la capa de presentación; internamente kilogramos, metros y segundos.
- Unidades por defecto: kilogramos y kilómetros. Opciones: peso en kg o lb; distancia en km o millas; distancias cortas en metros o yardas; altura en cm o pies y pulgadas; ritmo por km, por milla o por 100 m / 100 yd en natación; temperatura no aplica.
- Cada ajuste de unidad es independiente (`user_settings`); cambiarla no altera los datos guardados.

## Seguridad en el cliente

- Sin tráfico en texto plano; sin copias de seguridad automáticas de Android para datos sensibles (`allowBackup` desactivado o reglas de exclusión).
- Ningún secreto en el APK. Sin registro de tokens, contraseñas ni correos completos.
- Permisos solicitados solo en el momento de uso, con explicación previa: ubicación, notificaciones, actividad física y Health Connect.
- WebView del visor 3D: solo contenido local de assets, sin red, sin JavaScript expuesto más allá de una interfaz mínima.

## Estados de pantalla

Cada pantalla define cuatro estados explícitos: cargando, contenido, vacío y error. El estado sin conexión no es un error: se muestra una indicación discreta y se sigue operando con datos locales.

## Rendimiento

- Arranque en frío menor a 2 segundos en un dispositivo de gama media.
- Confirmar una serie da respuesta visible en menos de 100 ms.
- Listas con claves estables y carga paginada desde Room.
- Gráficos con datos precalculados por consulta; sin cálculos pesados en el hilo principal.
