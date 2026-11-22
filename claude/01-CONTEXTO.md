# 01 - Contexto del proyecto

## Qué es

Aplicación Android nativa para registrar, planificar y analizar entrenamientos. Nombre: Abitus-D (la D es de deporte). Contacto: app.abitus.d@gmail.com. Edad mínima de uso: 18 años.

El valor central: registrar una serie en el gimnasio o una salida de running debe ser rápido, funcionar sin conexión y ayudar a responder una pregunta concreta: "estoy progresando o no".

## Para quién

- Uso personal del autor y de un grupo de amigos (corredores, nadadores, practicantes de fuerza y calistenia).
- Capacidad objetivo: 500 usuarios simultáneos.
- Idioma principal: español. Debe poder cambiarse de idioma, tanto la app como los ejercicios.
- Solo Android. iPhone y PWA quedan fuera del alcance actual (ver `11-IDEAS-Y-FUTURO.md`).
- Costo cero absoluto: sin tarjeta en ningún servicio y sin dominio comprado.

## Distribución

- Instalación personal: APK firmado subido a Google Drive, con README de instalación, documento de seguridad, política de privacidad y términos.
- No se publica en Google Play (por ahora).
- Versión mínima de Android: 10 (API 29).

## Restricciones que moldean todo

1. Costo cero. Solo servicios con plan gratuito permanente. Ver `14-INFRAESTRUCTURA.md`.
2. Uso en el gimnasio: señal mala o nula. Offline-first obligatorio.
3. Varios dispositivos por usuario. Sincronización con resolución de conflictos.
4. Datos de salud y rendimiento: sensibles. Minimizar, proteger y permitir exportar y borrar.
5. Interfaz profesional y minimalista, sin emojis, con un sistema de iconos coherente.

## Alcance

Dentro del alcance:

- Acceso: bienvenida, registro con verificación por correo, login, recuperación de contraseña, sesiones por dispositivo, política de privacidad.
- Biblioteca de ejercicios reales por disciplina con detalles técnicos, más ejercicios propios del usuario.
- Planificación: programas, rutinas, calendario.
- Registro activo: series, repeticiones, carga, RPE, RIR, tiempos, distancias, notas, con timers (descanso, intervalos, cronómetro).
- Running y natación con GPS (running y aguas abiertas) y lectura de Health Connect (pasos, frecuencia cardíaca, sesiones de ejercicio).
- Analítica: dashboards, historial, evolución, PRs, 1RM estimado, volumen por músculo, veredicto de progreso, cuerpo humano 3D interactivo con mapa de calor por series.
- Apartado Reloj: cronómetro, temporizadores, intervalos y perfiles guardados (`11e-RELOJ-Y-TIMERS.md`).
- Ajustes de la app y de la cuenta; respaldo y exportación; eliminación de cuenta.
- Correos automáticos con buen diseño.

Fuera del alcance (por ahora): nutrición, red social o feed, planes generados por IA, pagos, publicación en tiendas, versión iOS nativa.

## Disciplinas iniciales de la biblioteca

Calistenia, powerlifting, hipertrofia (musculación), halterofilia, pliometría, running, natación, ciclismo, remo, core, movilidad y estiramientos, funcional. Se pueden agregar más sin cambiar el código.

## Principios de producto

- Rapidez en el gimnasio: pocos toques por serie, números grandes, una mano.
- Cada métrica responde una pregunta concreta; si no responde ninguna, no se muestra.
- Honestidad con los datos: sin cifras inventadas; si no hay datos suficientes, se dice.
- El usuario es dueño de sus datos: puede exportarlos y borrarlos desde la app.

## Glosario

- RPE: esfuerzo percibido, escala 1 a 10 (admite medios puntos).
- RIR: repeticiones en reserva (cuántas repeticiones quedaban antes del fallo).
- 1RM / e1RM: una repetición máxima; e1RM es la estimada a partir de una serie.
- PR: récord personal.
- Volumen: suma de series (o de carga por repeticiones) en un periodo.
- Serie efectiva: serie de trabajo, no calentamiento.
- EMOM: cada minuto en el minuto. Tabata: bloques de trabajo y descanso cortos repetidos.
- Superserie: dos o más ejercicios encadenados sin descanso completo entre ellos.
- Offline-first: la app funciona sin red y sincroniza después.
- Tombstone: marca de borrado lógico que se sincroniza.
- Catálogo: datos de solo lectura para el cliente (disciplinas, músculos, equipamiento, ejercicios base).
