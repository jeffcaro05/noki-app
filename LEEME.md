# Noki App — PWA offline 0.3

Esta entrega implementa orientación, comparación de alternativas, directorio offline, mapa local y mascota. Incluye manifiesto y caché para instalarse como PWA en Android.

## Abrir

Abre index.html con Chrome o Edge para revisarla sin internet. Para editar, abre esta carpeta con Archivo → Abrir carpeta en Visual Studio Code.

## Instalar en celular

Para instalarla como PWA, debe abrirse desde HTTPS o localhost; Chrome no activa service workers desde un archivo `file://`. Sirve esta carpeta con un servidor local para probarla o publícala en un hosting HTTPS. En Chrome elige «Instalar aplicación» o «Añadir a pantalla de inicio». Después de la primera carga, el contenido guardado puede abrirse sin conexión.

## Archivos

- index.html: estructura y recursos locales.
- styles.css: presentación.
- logic.js: preguntas, reglas y resultados independientes de la interfaz.
- app.js: navegación.
- tests/logic.test.cjs: comprobaciones de las ocho rutas y corrección de respuestas.

El progreso solo se conserva mientras la página permanece abierta. Recargar o cerrar inicia una nueva sesión. No se transmiten respuestas.

Para ejecutar las pruebas de lógica si tienes Node.js: node tests/logic.test.cjs

La validación visual en navegador y con usuarios sigue pendiente. Este módulo no solicita cámara, ubicación, datos personales ni conexión con GitHub.
