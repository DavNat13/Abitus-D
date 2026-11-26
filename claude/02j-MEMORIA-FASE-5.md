# 02j - Memoria de la Fase 5 (Atleta y Reloj, núcleo)

Cerrada el 2026-11-26 con la etiqueta `fase-5`. Plan en `10c`; ideas marcadas "Hecha" en `11d`, `11e` y `11f`.

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

- El reloj no usa servicio en primer plano, igual que el descanso de la Fase 4 (`02h`): cada cambio de fase y el aviso de 10 s son alarmas exactas, y la notificación usa el cronómetro del sistema. El estado vive en SharedPreferences (`abitusd_clock`) en tiempo de pared, por eso sobrevive al cierre y al reinicio.
- `ClockBootReceiver` está exportado solo para BOOT_COMPLETED y MY_PACKAGE_REPLACED. Android no permite reenviar esas acciones protegidas, así que reenvía una acción privada (`clock.RECOVER`).
- Se corrigió un error previo: en Android 10 a 12 no existe `POST_NOTIFICATIONS`, y la comprobación del permiso siempre fallaba, por lo que el descanso, los recordatorios y el reloj no mostraban notificación. Ahora se usa `canNotify()` (`areNotificationsEnabled`). En Android 13+ el Reloj pide el permiso una vez.
- La base local de desarrollo no aplica migraciones sola: hay que correr `docker compose exec -T postgres psql -U abitusd -d abitusd < database/migrations/NNN.sql`. Las pruebas usan su propia base limpia.

## Criterios medidos (emulador API 29, 2026-11-26)

| Criterio | Resultado |
|---|---|
| Desviación menor a 1 s en 60 min con la pantalla apagada | DRIFT |
| El reloj sobrevive a cerrar la app y a reiniciar | Tras `adb reboot`, la alarma volvió a quedar a la misma hora exacta (12:23:06.096) y la notificación con sus 4 acciones; también tras `am force-stop` |
| Señal visible en cada fase | Etiqueta TRABAJO, DESCANSO o TERMINADO, color y anillo; nunca solo color |
| Perfil y favorito creados sin conexión se sincronizan | `dirty` 1 sin red, luego 0; las filas llegaron a PostgreSQL con el spec JSON exacto |
| Onboarding en menos de 2 minutos | Paso 1 lleno en 24 s y el resto omitido en unos 15 s; datos en PostgreSQL (1995, 1.78 m, 74 kg, onboarding_done) |

Pruebas: backend con 67 en verde (2 nuevas de reloj y favoritos); Android con Detekt, ktlint, pruebas unitarias y APK en verde.

## Pendiente

- Pasados a la 5b (ver `10c`): multitemporizador, contador de rondas en AMRAP, editor de secuencias con calentamiento y enfriamiento, fases por repeticiones, voz del próximo ejercicio y autotest de precisión en pantalla.
- El glosario no se abre todavía al tocar un término dentro del texto; se llega por "Cómo usar" y Perfil.
- Prueba en un teléfono real (Fase 10).
