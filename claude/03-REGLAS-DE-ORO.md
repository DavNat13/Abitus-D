# 03 - Reglas de oro

Estas reglas no se negocian ni se relajan "por ahora". Si una tarea parece exigir romperlas, detente, explica el conflicto y pide decisión.

## Proceso

1. Lee `02-MEMORIA.md`, esta página y `12-DECISIONES-PENDIENTES.md` antes de actuar. Al terminar una tarea relevante, actualiza la memoria.
2. Una fase a la vez. No empieces la siguiente sin cumplir los criterios de aceptación de la actual (`10-FASES-DE-TRABAJO.md`).
3. Cambios pequeños, verificables y dentro del alcance pedido. No refactorices ni "mejores" lo que no se pidió.
4. Si algo es ambiguo y costoso de rehacer, pregunta antes de construir. Si es barato de corregir, decide, documéntalo y sigue.
5. Verifica en la documentación oficial vigente las versiones de librerías y el comportamiento de APIs de Android, Health Connect, servicios en la nube y límites gratuitos. No los fijes de memoria. Registra lo verificado con fecha en la memoria.
6. Nunca declares algo terminado sin haberlo ejecutado y probado (compilación, pruebas, endpoint real, pantalla en emulador).

## Producto y diseño

7. Cero emojis en toda la interfaz, correos, notificaciones, strings, datos semilla y documentos del producto. Solo iconografía del sistema definido en `05-UI-UX.md`. Hay una verificación automática (`tools/check-no-emoji`).
8. Un solo sistema de iconos, un solo peso de trazo, un solo tamaño base. Las pantallas no referencian archivos de icono directamente: usan los nombres semánticos de `AppIcons`.
9. Todo color, tamaño de texto, espaciado y radio viene de tokens del sistema de diseño. Nada de valores sueltos en pantallas.
10. Ningún texto visible escrito en el código. Todo en recursos localizables. El español es el idioma por defecto; el inglés se incluye desde el inicio.
11. Rapidez en el gimnasio: registrar una serie debe exigir el mínimo de toques. La acción principal siempre al alcance del pulgar.

## Datos

12. Offline-first. La base local (Room) es la fuente de verdad en el cliente. Escribir primero local, sincronizar después. Ninguna acción de entrenamiento espera a la red.
13. Unidades internas del sistema internacional en base de datos y API: kilogramos, metros, segundos. La conversión a libras, millas, etc. ocurre solo al mostrar o capturar.
14. Identificadores generados en el cliente (UUID v7), de modo que escribir sin conexión y reintentar sea seguro (idempotencia).
15. Borrado lógico (tombstones) para todo lo que se sincroniza.
16. Los ejercicios y sus indicaciones técnicas deben ser reales y correctos. No inventes ejercicios, músculos ni consejos de seguridad. Si dudas, márcalo para revisión humana.
17. Los datos de salud son sensibles: guardar lo mínimo, pedir permiso explícito, nunca enviarlos a terceros.

## Seguridad

18. Consultas a base de datos siempre parametrizadas mediante el constructor de consultas. Prohibido concatenar entrada del usuario en SQL.
19. Validación estricta de toda entrada en el servidor (esquemas cerrados, longitudes y rangos máximos). La validación del cliente es solo comodidad.
20. Contraseñas con Argon2id. Tokens de refresco rotativos, guardados con hash. Nunca registrar contraseñas, códigos, tokens ni correos completos en logs.
21. Todo endpoint exige autenticación salvo los públicos listados en `07-BACKEND.md`. Toda consulta de datos de usuario filtra por el usuario autenticado; hay pruebas contra acceso a datos ajenos.
22. Los secretos viven en variables de entorno o en el gestor del proveedor. Nunca en el repositorio, nunca en el APK. Hay un `.env.example` sin valores reales. El keystore de firma no se sube.
23. Respuestas de registro y de recuperación de contraseña idénticas exista o no el correo (evitar enumeración de cuentas).

## Costos e infraestructura

24. Costo cero. No agregues servicios de pago ni dependencias que exijan tarjeta sin pedir confirmación explícita. El único gasto contemplado como posible es el dominio, y está en decisiones pendientes.
25. No asumas procesos en segundo plano en el servidor: el hosting escala a cero y el cómputo solo corre durante solicitudes. Las tareas periódicas se diseñan como endpoints invocados por un planificador externo. Ver `14-INFRAESTRUCTURA.md`.
26. Cuida los límites gratuitos: tamaño de base de datos, cuota diaria de correos, solicitudes. Diseña con presupuestos y colas.

## Código

27. Código, identificadores y commits en inglés; documentación y textos de producto en español. Commits convencionales (feat, fix, chore, docs, refactor, test).
28. Dependencias con propósito claro, documentadas en `06-LIBRERIAS.md`. Preferir pocas y mantenidas. Ejecutar auditoría de dependencias antes de cerrar una fase.
29. Cada módulo respeta la estructura de `16-ESTRUCTURA-CARPETAS.md`. La capa de base de datos, el backend y el frontend no se mezclan.
30. Toda decisión de arquitectura que se tome o cambie se registra en `02-MEMORIA.md` con fecha y motivo.

## Tamaño de archivos

31. Máximo 150 líneas por archivo. Aplica a todo archivo de texto escrito a mano: código (Kotlin, TypeScript, SQL), recursos, plantillas de correo, scripts y documentación. Si un archivo se acerca al límite, se divide antes de superarlo; nunca se entrega uno que lo exceda.
32. Cómo dividir, de forma optimizada y sin duplicar:
    - Una responsabilidad por archivo (una pantalla, un caso de uso, un repositorio, un endpoint, una tabla o grupo cohesivo de tablas).
    - Extraer componentes de interfaz, funciones auxiliares, constantes, tipos y esquemas de validación a archivos propios con nombre descriptivo.
    - Los archivos divididos viven en la misma carpeta o en una subcarpeta del módulo, con nombres que indiquen su rol (por ejemplo `WorkoutScreen.kt`, `WorkoutScreenSections.kt`, `WorkoutViewModel.kt`).
    - Sin código duplicado ni archivos "cajón de sastre" (`Utils`, `Helpers` genéricos): cada utilidad va a un archivo con nombre específico.
    - Importaciones explícitas y mínimas; no crear dependencias circulares al dividir.
    - Documentación dividida: un archivo principal con el resumen y enlaces a los complementarios (sufijos `b`, `c`).
    - La división no puede degradar el rendimiento ni la legibilidad: se divide por cohesión, no por cortar líneas al azar.
33. Excepciones acotadas: archivos generados (código generado, documento OpenAPI), archivos de bloqueo de dependencias y esquemas exportados de Room. Los datos semilla se dividen por disciplina; las migraciones SQL se dividen en varias migraciones. Cualquier otra excepción debe pedirse al usuario y registrarse en `02-MEMORIA.md`.
34. Verificación automática: `tools/check-file-length` falla si encuentra un archivo manual con más de 150 líneas (se ejecuta junto con `tools/check-no-emoji` antes de cerrar cualquier tarea).
