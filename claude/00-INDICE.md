# 00 - Índice de la documentación

Carpeta de contexto para Claude Code. Cada archivo cubre un área; léelo antes de tocar esa área.

| Archivo | Contenido | Léelo cuando |
|---|---|---|
| 01-CONTEXTO.md | Qué es la app, para quién, alcance, glosario | Al empezar el proyecto y ante cualquier duda de propósito |
| 02-MEMORIA.md | Estado actual, decisiones, hechos verificados, registro de progreso | Siempre, al iniciar y al terminar cada sesión |
| 02b-MEMORIA-DECISIONES-2.md | Decisiones cerradas de la ronda 2 y hechos verificados | Siempre, junto con 02 |
| 02c-MEMORIA-VERIFICACIONES.md | Verificaciones externas de la Fase 0 y versiones elegidas | Al depender de límites externos o versiones |
| 03-REGLAS-DE-ORO.md | Reglas que nunca se rompen | Siempre |
| 04-STACK.md | Tecnologías elegidas, motivos y alternativas descartadas | Al configurar proyectos o cambiar tecnología |
| 05-UI-UX.md | Principios, navegación, flujos, microcopy y accesibilidad | Al construir cualquier pantalla |
| 05b-UI-TOKENS.md | Paleta de marca, temas claro y oscuro, tipografía, forma, iconos | Al usar colores, textos o iconos |
| 05c-UI-COMPONENTES-Y-MOVIMIENTO.md | Sombras, tarjetas, botones, campos, barras y animaciones | Al construir componentes o animaciones |
| 06-LIBRERIAS.md | Librerías por capa y su propósito | Al agregar o actualizar dependencias |
| 06b-LIBRERIAS-VERSIONES.md | Versiones verificadas y cambios (Room 3, Navigation 3, AGP 9) | Al crear proyectos o actualizar |
| 07-BACKEND.md | API, autenticación, sincronización, correos, seguridad del servidor | Al trabajar en `backend/` |
| 08-DATABASE.md | Convenciones, enumeraciones, índices, consultas, migraciones, respaldo | Al trabajar en `database/` o en consultas |
| 08b-DATABASE-TABLAS.md | Definición de tablas y atributos | Al crear o modificar tablas |
| 08c-DATABASE-SEMILLAS.md | Datos semilla del catálogo de ejercicios | Al cargar o ampliar la biblioteca |
| 08d-DATABASE-FUENTES-EJERCICIOS.md | Fuentes de ejercicios, licencias y atribución | Al importar ejercicios |
| 09-FRONTEND-ANDROID.md | Arquitectura de la app, paquetes, navegación, autenticación, idiomas | Al trabajar en `android/` |
| 09b-FRONTEND-OFFLINE-SYNC.md | Room, motor de sincronización, conflictos, recuperación de sesión | Al tocar datos locales o sincronización |
| 09c-FRONTEND-TIMERS-GPS-SALUD.md | Timers, GPS, natación, Health Connect, notificaciones, permisos | Al trabajar en timers, cardio o salud |
| 09d-FRONTEND-ANALITICA-3D.md | Fórmulas, veredicto de progreso, dashboards, cuerpo 2D y 3D | Al trabajar en analítica o mapa corporal |
| 10-FASES-DE-TRABAJO.md | Hoja de ruta, método de trabajo, Fases 0 y 1 | Al planificar o cerrar una fase |
| 10b-FASES-DE-TRABAJO-2.md | Fases 2 a 4 (biblioteca, planificación, registro) | Al planificar o cerrar una fase |
| 10c-FASES-DE-TRABAJO-3.md | Fases 5 a 7 (reloj, analítica, cardio y salud) | Al planificar o cerrar una fase |
| 10d-FASES-DE-TRABAJO-4.md | Fases 8 a 10 (3D, ajustes, distribución 1.0) | Al planificar o cerrar una fase |
| 10e-FASES-DE-TRABAJO-5.md | Fases 11 a 13, mejoras posteriores a la 1.0 | Al promover ideas a fases |
| 11-IDEAS-Y-FUTURO.md | Ideas, mejoras futuras y lo que está fuera de alcance | Al proponer funciones nuevas |
| 11b-IDEAS-REGISTRO-Y-PROGRAMACION.md | Ideas de registro, calentamiento, programación y hábitos | Al planificar funciones de entrenamiento |
| 11c-IDEAS-DEPORTES-Y-SALUD.md | Ideas de cardio, natación, calistenia, fuerza, recuperación y medidas | Al planificar deportes y salud |
| 11d-IDEAS-ANALISIS-Y-EXPERIENCIA.md | Ideas de análisis, datos, experiencia y accesibilidad | Al planificar analítica o mejoras de uso |
| 11e-RELOJ-Y-TIMERS.md | Apartado Reloj: cronómetro, intervalos, timers y reglas técnicas | Al trabajar en el reloj o timers |
| 12-DECISIONES-PENDIENTES.md | Decisiones abiertas que no se deben asumir | Antes de tocar algo que dependa de ellas |
| 13-SEGURIDAD-Y-LEGAL.md | Amenazas, lista de control, datos tratados, política de privacidad, marco legal | Al trabajar en seguridad o legal |
| 13b-DISTRIBUCION-APK.md | Paquete de distribución, README de instalación, firma y versiones | Al preparar el APK |
| 14-INFRAESTRUCTURA.md | Hosting, base de datos en la nube, despliegue, costos, respaldos | Al desplegar o configurar entornos |
| 15-CORREOS.md | Catálogo de correos, SMTP, cola, cuotas y antiabuso | Al trabajar en correos |
| 15b-CORREOS-PLANTILLAS.md | Diseño y contenido de cada plantilla | Al diseñar o implementar plantillas |
| 16-ESTRUCTURA-CARPETAS.md | Estructura exacta de carpetas y convenciones de nombres | Al crear archivos o módulos |
| 17-CALIDAD-Y-PRUEBAS.md | Definición de terminado, pruebas, presupuestos de rendimiento | Al cerrar cualquier tarea |
| 18-PROMPT-DE-INICIO.md | Prompts para arrancar Claude Code y para sesiones y fases nuevas | Al abrir una sesión nueva |

Regla de tamaño: ningún archivo de esta carpeta supera 150 líneas. Cuando un tema crece, se divide en un archivo principal y complementarios con sufijo `b`, `c` y se actualiza esta tabla.

## Convención de marcas dentro de los documentos

- VERIFICAR: dato que depende de versiones o políticas externas y debe comprobarse en la fuente oficial vigente antes de usarlo.
- PENDIENTE: decisión aún no tomada; está listada en `12-DECISIONES-PENDIENTES.md`.
- Las fechas de verificación están en `02-MEMORIA.md`.
