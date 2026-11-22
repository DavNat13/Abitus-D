# 17 - Calidad y pruebas

## Definición de terminado (aplica a toda tarea)

Una tarea está terminada solo si cumple todo lo siguiente:

1. Compila y se ejecuta (app en emulador o dispositivo; backend contra PostgreSQL local).
2. Pruebas automáticas relevantes escritas y en verde.
3. Se probó el flujo real, incluido el caso sin conexión cuando aplica.
4. `tools/check-no-emoji` y `tools/check-file-length` pasan.
5. Linters (Detekt, ktlint, ESLint, Prettier) sin errores.
6. Sin secretos, datos personales ni registros sensibles añadidos.
7. Textos en recursos localizables (es y en); sin valores de diseño sueltos.
8. Documentación afectada actualizada y `02-MEMORIA.md` al día.
9. Los criterios de aceptación de la fase se cumplen.

## Pirámide de pruebas

| Nivel | Android | Backend |
|---|---|---|
| Unitarias | Dominio, fórmulas (e1RM, volumen, veredicto), conversión de unidades, timers | Servicios, validación, reglas de conflicto |
| Integración | Room con migraciones, repositorios, motor de sincronización con servidor simulado | Rutas con `fastify.inject` contra PostgreSQL real, migraciones sobre base limpia |
| Interfaz y extremo a extremo | Flujos clave con Compose UI Test; recorrido registro a sesión | Escenarios con dos dispositivos sincronizando |

## Pruebas imprescindibles

- Autenticación: refresco rotativo, reutilización de token, límites de intentos, respuestas idénticas, códigos vencidos y agotados.
- Autorización: acceso a recursos de otro usuario siempre falla.
- Sincronización: sin conexión, doble dispositivo, borrados, lotes parciales, cursor expirado (`09b`).
- Fórmulas de analítica con cifras exactas conocidas (`09d`).
- Timers: pantalla apagada, segundo plano, ahorro de batería, reinicio del proceso.
- Migraciones de Room y de PostgreSQL con datos existentes.
- Idiomas: cambio de idioma de app y de catálogo; cadena de respaldo de traducciones.
- Correos: HTML y texto de cada plantilla en es y en, sin emojis.

## Presupuestos de rendimiento

| Métrica | Meta |
|---|---|
| API, lecturas con instancia activa (p95) | Menor a 300 ms |
| API, escrituras de sincronización (p95) | Menor a 500 ms por lote de 200 filas |
| Arranque en frío de la app (gama media) | Menor a 2 s |
| Confirmar una serie | Respuesta visible en menos de 100 ms |
| Mapa de calor y dashboards con 2 años de datos | Menor a 500 ms de consulta local |
| Tamaño del APK | Objetivo inicial menor a 40 MB (VERIFICAR con el modelo 3D) |
| Consumo de batería en 1 hora de cardio con GPS | Medir y registrar; no hay meta fija inicial |

## Prueba de carga (Fase 10)

- Herramienta: k6, script en `tools/`.
- Escenario: 500 usuarios virtuales simultáneos con mezcla realista: sincronización incremental (la mayoría), push de sesiones, descargas de catálogo con ETag, y una minoría de login y registro.
- Medir: latencia p50, p95 y p99, tasa de errores, conexiones a la base, tamaño de base por usuario, correos enviados.
- Aceptación: tasa de errores menor a 1 por ciento y latencias dentro de los presupuestos, descontando el arranque en frío inicial.
- Ejecutar contra un entorno que no afecte datos reales ni agote cuotas gratuitas (por ejemplo, con envío de correo simulado).
- Registrar resultados y ajustes en `02-MEMORIA.md`.

## Calidad de contenido

- Revisión humana de una muestra de ejercicios del catálogo por disciplina antes de cerrar la Fase 2.
- Revisión de textos en español e inglés (tono, ortografía, coherencia).
- Contraste AA verificado en ambos temas y accesibilidad básica (lector de pantalla, texto escalado).

## Automatización

- Ejecutar linters y pruebas localmente antes de cada commit; opcionalmente con un flujo de integración continua gratuito (VERIFICAR límites).
- Búsqueda de secretos y auditoría de dependencias en cada cierre de fase.
- Scripts de `tools/` documentados y ejecutables en PowerShell.
