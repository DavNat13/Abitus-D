# 10d - Fases de trabajo: 8, 9 y 10

Continuación de `10c-FASES-DE-TRABAJO-3.md`. La Fase 10 cierra la versión 1.0.

## Fase 8 - Cuerpo 3D interactivo

Objetivo: el mapa de calor sobre un modelo 3D.

Entregables:
- Modelo glTF con piezas por grupo muscular y licencia registrada en `distribution/THIRD_PARTY.md`.
- Visor Three.js en WebView con assets locales, rotación, zoom y selección de músculo.
- Integración con periodos y escala de color; respaldo automático al mapa 2D.
- Molestias por zona sobre el modelo (evolución de la lista de la Fase 4).

Criterios de aceptación:
- Funciona sin conexión en un dispositivo de gama media con fluidez aceptable.
- Tocar un músculo muestra series, ejercicios y comparación con el periodo anterior.
- Si el modelo falla al cargar, se usa el mapa 2D sin error visible.
- Si no hay modelo con licencia adecuada, la fase se cierra con el 2D y se registra la decisión.

## Fase 9 - Ajustes, cuenta y correos completos

Objetivo: configuración, privacidad, datos del usuario y comunicación.

Entregables:
- Ajustes de la app: idioma, tema y acento, unidades completas (kg o lb, km o millas, metros o yardas, cm o pies y pulgadas), timer, notificaciones, fórmula de 1RM, umbrales y contribución muscular.
- Ajustes de cuenta: perfil, contraseña, dispositivos activos y cierre remoto, exportación y eliminación de cuenta.
- Exportar a CSV, JSON y GPX; importar historial desde CSV; respaldo local cifrado.
- Papelera de 30 días para elementos borrados.
- Todos los correos del catálogo (`15-CORREOS.md`) con diseño final, en es y en, y pantalla "Acerca de" con créditos.
- Política y términos finales en la app y en `legal/` (revisión profesional si se comparte con terceros).

Criterios de aceptación:
- Un usuario exporta sus datos y elimina su cuenta desde la app; no queda nada suyo en la base.
- Importar un CSV de prueba reproduce el historial esperado sin duplicados.
- Los correos se ven bien en los clientes principales y respetan la cuota de Gmail con la cola de prioridad.
- Cambiar una unidad no altera los datos guardados.

## Fase 10 - Endurecimiento y distribución (versión 1.0)

Objetivo: calidad, rendimiento y paquete para compartir.

Entregables:
- Prueba de carga con k6 de 500 usuarios simultáneos; ajuste de límites y conexiones con el pooler de Neon.
- Medición de límites reales de Vercel (tiempo y cómputo de funciones) y Neon (horas de cómputo e inactividad).
- Revisión de seguridad (`13-SEGURIDAD-Y-LEGAL.md`), auditoría de dependencias y búsqueda de secretos.
- Accesibilidad, arranque y consumo de batería.
- `tools/export-db` y restauración probada; tarea programada de GitHub Actions configurada.
- APK de release firmado, suma de verificación, README de instalación, documento de seguridad, política, términos y créditos en `distribution/`.

Criterios de aceptación:
- Cumple los presupuestos de `17-CALIDAD-Y-PRUEBAS.md`.
- Un usuario externo instala el APK siguiendo solo el README.
- Los límites gratuitos medidos dejan margen para 500 usuarios o se documenta el tope real.
- La versión se etiqueta como 1.0.0.
