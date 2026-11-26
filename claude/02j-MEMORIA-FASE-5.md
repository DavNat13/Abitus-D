# 02j - Memoria de las Fases 5 y 5b (Atleta y Reloj)

Cerradas el 2026-11-26 con las etiquetas `fase-5` y `fase-5b`. Plan en `10c`; ideas marcadas "Hecha" en `11d`, `11e` y `11f`.

## Qué se construyó

- Bienvenida en tres tarjetas y onboarding de cinco pasos omitibles: datos corporales, experiencia, objetivo y disponibilidad, salud previa y punto de partida con plantilla sugerida del catálogo. Hecho por opencode y corregido por Claude.
- Perfil del atleta editable en Perfil, sincronizado: tabla `athlete_profiles` (migración 015, Room 6).
- Favoritos de ejercicios y perfiles del reloj sincronizados: migración 016 y Room 7 (`exercise_favorites`, `timer_profiles`).
  - El id del favorito se deriva de usuario y ejercicio, así no se duplica entre dispositivos.
  - Quitar un favorito es un tombstone.
  - En la biblioteca, los favoritos van primero y hay un filtro "Favoritos".
- Glosario "Aprende" con 14 términos.
  - Se abre desde Perfil y desde el pie de cada hoja "Cómo usar" (`LocalOpenGlossary`).
- Estado de sincronización en Perfil: cambios pendientes, última sincronización (`last_sync_at` en `sync_state`) y botón "Sincronizar ahora".
- Reloj (`feature/clock`):
  - Motor por marcas de reloj de pared (`ClockEngine`, con pruebas en `ClockEngineTest`).
  - 14 modos y perfiles base.
  - Pausa, +15 s, saltar y vueltas con la mejor y la peor marcadas.
  - Anillo de progreso, colores con etiqueta, voz (fase, mitad, 10 s y 3-2-1), háptica por fase y tonos en el canal de notificación, que no cortan la música.
  - Modo silencioso y pantalla encendida opcional.
  - Acceso desde Inicio y desde la sesión activa (`LocalOpenClock`).
- Recordatorios del calendario y reloj recuperados tras reiniciar el teléfono o actualizar la app (`ClockBootReceiver` reenvía a `ClockReceiver`).

## Decisiones técnicas

- Por pedido del autor (2026-11-26), el reloj usa un servicio en primer plano (`ClockService`, tipo specialUse) mientras está activo, y además cada cambio de fase y el aviso de 10 s son alarmas exactas: si el sistema detiene el servicio, las alarmas siguen. El estado activo, el historial y los isométricos viven en Room (`clock_state`, Room 8) en tiempo de pared; los ajustes de pantalla, en SharedPreferences.
- `ClockBootReceiver` está exportado solo para BOOT_COMPLETED y MY_PACKAGE_REPLACED. Android no permite reenviar esas acciones protegidas, así que reenvía una acción privada (`clock.RECOVER`).
- Se corrigió un error previo: en Android 10 a 12 no existe `POST_NOTIFICATIONS`, y la comprobación del permiso siempre fallaba, por lo que el descanso, los recordatorios y el reloj no mostraban notificación. Ahora se usa `canNotify()` (`areNotificationsEnabled`). En Android 13+ el Reloj pide el permiso una vez.
- La base local de desarrollo no aplica migraciones sola: hay que correr `docker compose exec -T postgres psql -U abitusd -d abitusd < database/migrations/NNN.sql`. Las pruebas usan su propia base limpia.

## Criterios medidos (emulador API 29, 2026-11-26)

| Criterio | Resultado |
|---|---|
| Desviación menor a 1 s en 60 min con la pantalla apagada | Pomodoro de 60 min con la pantalla apagada y el servicio activo: cambios de fase a los 25, 30, 55 y 60 min con 712, 37, 305 y 4 ms; Tabata de 4 min: 16 cambios entre 6 y 36 ms. No se acumula porque el reloj se ancla a la hora del sistema |
| Widget y atajos en menos de 1 s | 152 ms (cronómetro) y 23 ms (reloj) con la app en memoria; 4 a 6 s en arranque en frío en el emulador sin GPU, a medir en el teléfono real |
| Perfil compartido se importa igual | El archivo de "Escalera compartida" se importó con el mismo spec JSON y se sincronizó (`dirty` 0) |
| El reloj sobrevive a cerrar la app y a reiniciar | Tras `adb reboot`, la alarma volvió a quedar a la misma hora exacta (12:23:06.096) y la notificación con sus 4 acciones; también tras `am force-stop` |
| Señal visible en cada fase | Etiqueta TRABAJO, DESCANSO o TERMINADO, color y anillo; nunca solo color |
| Perfil y favorito creados sin conexión se sincronizan | `dirty` 1 sin red, luego 0; las filas llegaron a PostgreSQL con el spec JSON exacto |
| Onboarding en menos de 2 minutos | Paso 1 lleno en 24 s y el resto omitido en unos 15 s; datos en PostgreSQL (1995, 1.78 m, 74 kg, onboarding_done) |

Pruebas: backend con 67 en verde (2 nuevas de reloj y favoritos); Android con Detekt, ktlint, pruebas unitarias y APK en verde.

## Fase 5b

- Relojes especiales como perfiles base: piscina con salida cada 1:30, vueltas de pista, respiración en caja y 4-7-8 (fases Inhala, Mantén y Exhala, también aceptadas por el servidor), frío, sauna y boxeo. Además, editor de secuencias personalizadas (agregar y quitar fases de trabajo, descanso y preparación) y contador de rondas en AMRAP.
- Hora grande (reloj de pared), cuenta regresiva a una fecha, metrónomo sin deriva (marcas monotónicas), historial local de vueltas con mejor, peor y promedio, y exportación a CSV.
- Accesos: atajos de Android (Reloj y Cronómetro), mosaico de ajustes rápidos, widget de dos botones y botones de volumen (subir pausa o sigue; bajar marca vuelta, ronda o salta).
- Compartir un perfil como JSON (`abitusd-timer-profile-v1`) por la hoja del sistema, importarlo desde un archivo, mostrarlo como QR y escanearlo con ZXing (`zxing-android-embedded` 4.3.0, Apache 2.0, verificado en Maven Central).
- Multitemporizador con alarmas propias, fases por repeticiones con botón "Listo", rutina convertida en secuencia, reloj abierto desde un ejercicio de la sesión, vueltas guardadas en las notas de la sesión, voz del siguiente ejercicio, cuenta regresiva de 10 s, isométricos y habilidades con récord, calentamiento general, vuelta a la calma, movilidad y contraste guiados, test de campo (course navette y Yo-Yo IR1 según las tablas publicadas, y sprint de 30 m), estilos del reloj (digital, minimalista, analógico), nombres de vuelta y widget con el entrenamiento de hoy.
- Control por voz con el reconocedor del teléfono (preferencia sin conexión; el botón solo aparece si hay reconocedor) y pausa por llamada con READ_PHONE_STATE pedido al activarla (decisiones del autor, 2026-11-26).
- Calculadora de discos gráfica: barra a escala con el código de colores IWF y el peso escrito en cada disco.
- Desviación de la última alarma guardada y mostrada como autotest de precisión.
- Se corrigió un aviso previo de lint en `BlockSheet` (locale observable).

## Pendiente

- Los términos del glosario se enlazan en todas las hojas "Cómo usar" (`GlossaryText`); abren el glosario completo, no el término exacto.
- El atajo abre el reloj en 23 ms con la app en memoria y 4 s en arranque en frío en este emulador sin GPU; medir en el teléfono real.
- Prueba en un teléfono real (Fase 10).
