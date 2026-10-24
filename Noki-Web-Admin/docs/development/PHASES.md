# Registro Centralizado de Fases de Desarrollo

Documento oficial de control y estado de fases conforme al **Artículo 8 de la Constitución del Proyecto**.

---

## Índice de Fases

| Fase | Nombre | Rama Git | Estado | Fecha de Cierre |
| :---: | :--- | :--- | :---: | :---: |
| **00** | Constitución y Arquitectura de Plataforma | `phase/00-constitucion-y-arquitectura` | `DESPLEGADA` | *En espera de aprobación para producción* |
| **01** | Autenticación, Seguridad Perimetral y Perfil | `phase/01-autenticacion-y-seguridad` | `IMPLEMENTADA` | 2026-10-16 |
| **02** | Dashboard de Resumen y Analítica | `phase/02-dashboard-resumen` | `IMPLEMENTADA` | 2026-10-16 |
| **03** | Directorio de Centros de Acopio | `phase/03-directorio-centros-acopio` | `IMPLEMENTADA` | 2026-10-16 |
| **04** | Receptores y Franjas Horarias | `phase/04-receptores-y-horarios` | `IMPLEMENTADA` | 2026-10-16 |
| **05** | Supervisión de Entregas y Trazabilidad | `phase/05-supervision-entregas` | `IMPLEMENTADA` | 2026-10-16 |
| **06** | Módulo Aprende (CMS Pedagógico) | `phase/06-modulo-aprende-cms` | `IMPLEMENTADA` | 2026-10-17 |
| **07** | Reportes, Balances y Auditoría | `phase/07-reportes-y-balances` | `IMPLEMENTADA` | 2026-10-17 |

---

## Fase 00: Constitución y Arquitectura de Plataforma

- **Objetivo**: Establecer la gobernanza constitucional (15 artículos obligatorios), la plataforma web base (React 18, Vite, TypeScript), el layout transversal de escritorio con menú lateral retráctil hacia los 7 módulos funcionales, el motor de accesibilidad universal WCAG 2.1 AA (contraste y escalado A+), la guardia perimetral contra roles móviles, el temporizador de inactividad de 30 minutos y la infraestructura de compilación para Firebase Hosting.
- **Artefactos Spec-Kit**:
  - Especificación: [specs/00-constitucion-y-arquitectura/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/00-constitucion-y-arquitectura/spec.md)
  - Plan de Implementación: [specs/00-constitucion-y-arquitectura/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/00-constitucion-y-arquitectura/plan.md)
  - Lista de Tareas: [specs/00-constitucion-y-arquitectura/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/00-constitucion-y-arquitectura/tasks.md)
  - Modelo de Datos: [specs/00-constitucion-y-arquitectura/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/00-constitucion-y-arquitectura/data-model.md)
  - Investigación Técnica: [specs/00-constitucion-y-arquitectura/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/00-constitucion-y-arquitectura/research.md)
  - Guía de Validación: [specs/00-constitucion-y-arquitectura/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/00-constitucion-y-arquitectura/quickstart.md)
- **Rama Git**: `phase/00-constitucion-y-arquitectura`
- **Resultados de Pruebas**: 
  - Pruebas unitarias y de integración (`tests/layout.test.tsx`, `tests/perimeter.test.tsx`, `tests/accessibility.test.ts`, `tests/a11y-keyboard.test.tsx`, `tests/avatar-validation.test.ts`, `tests/session.test.ts`).
- **Resultado de Compilación**:
  - Generación de bundle de producción en `dist/` verificado.
- **Firebase Hosting**:
  - Canal de vista previa: `phase-00-constitucion-y-arquitectura`
  - URL de Preview: [https://noki-89c7b--phase-00-constitucion-y-arquitectura-nwjbt8gw.web.app](https://noki-89c7b--phase-00-constitucion-y-arquitectura-nwjbt8gw.web.app)
  - Versión del despliegue: `ed7f79ca4266f22d`
  - Expiración: `2026-10-21 20:13:21`
  - Verificación funcional: URL verificada con respuesta HTTP 200 y título "Noki Administración Web — Ecosistema RAEE".
- **Estado Actual**: `DESPLEGADA`
- **Problemas Conocidos**: Ninguno. Todos los criterios de aceptación técnicos y constitucionales han sido implementados y probados.

---

## Fase 01: Autenticación, Seguridad Perimetral y Gestión de Perfil

- **Objetivo**: Implementar la arquitectura integral de autenticación para la consola de administración web de Noki. Provee una pantalla de acceso con aviso legal disuasorio permanente inalterable, validación perimetral con bloqueo específico al rol operativo de campo (`receptor`) con mensaje en rojo `#B42318`, detección contextual de cuenta que despliega el botón dinámico de recuperación de contraseña, conmutador accesible de visibilidad de contraseña, gestión de perfil de usuario con carga local de avatar ($\le 2$ MB, formatos PNG/JPEG/WebP) y restablecimiento al avatar por defecto oficial, temporizador de inactividad de 30 minutos (con aviso a los 28m) y trazabilidad inmutable de auditoría (`auditService`).
- **Artefactos Spec-Kit**:
  - Especificación: [specs/01-autenticacion-y-seguridad/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/01-autenticacion-y-seguridad/spec.md)
  - Plan de Implementación: [specs/01-autenticacion-y-seguridad/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/01-autenticacion-y-seguridad/plan.md)
  - Lista de Tareas: [specs/01-autenticacion-y-seguridad/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/01-autenticacion-y-seguridad/tasks.md)
  - Modelo de Datos: [specs/01-autenticacion-y-seguridad/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/01-autenticacion-y-seguridad/data-model.md)
  - Investigación Técnica: [specs/01-autenticacion-y-seguridad/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/01-autenticacion-y-seguridad/research.md)
  - Guía de Validación: [specs/01-autenticacion-y-seguridad/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/01-autenticacion-y-seguridad/quickstart.md)
  - Contratos de Interfaz: `specs/01-autenticacion-y-seguridad/contracts/` (`auth-service.json`, `perimeter-guard.json`, `profile-avatar.json`)
- **Rama Git**: `phase/01-autenticacion-y-seguridad`
- **Resultados de Pruebas**: 
  - 8 archivos de test, 26 pruebas unitarias e integración aprobadas al 100% (`tests/auth.test.tsx`, `tests/recovery.test.tsx`, `tests/perimeter.test.tsx`, `tests/avatar-validation.test.ts`, `tests/session.test.ts`, `tests/layout.test.tsx`, `tests/accessibility.test.ts`, `tests/a11y-keyboard.test.tsx`).
- **Resultado de Compilación**:
  - Compilación TypeScript estricta (`tsc --noEmit`) sin advertencias y bundle de producción en `dist/` generado exitosamente con Vite.
- **Estado Actual**: `IMPLEMENTADA`
- **Problemas Conocidos**: Ninguno. 100% de los requisitos funcionales (FR-001 a FR-011) y criterios de éxito (SC-001 a SC-006) implementados y verificados.

---

## Fase 02: Dashboard de Resumen y Analítica Estratégica

- **Objetivo**: Proveer una vista ejecutiva centralizada con cuatro (4) tarjetas de KPIs estratégicos (*Toneladas recicladas este mes*, *Total de usuarios registrados*, *9852 dispositivos históricos consolidados*, *Dispositivos reciclados hoy*), una barra superior de cinco (5) accesos directos operativos a los módulos de gestión (`Directorio`, `Receptores`, `Entregas`, `Aprende` y `Reportes`), y un panel de alertas de infraestructura crítica con badges tricolores semánticos (`Inoperativo` en `#B42318`, `Remodelación` en `#B54708` y estado saludable cuando la red está al 100%).
- **Artefactos Spec-Kit**:
  - Especificación: [specs/02-dashboard-resumen/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/02-dashboard-resumen/spec.md)
  - Plan de Implementación: [specs/02-dashboard-resumen/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/02-dashboard-resumen/plan.md)
  - Lista de Tareas: [specs/02-dashboard-resumen/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/02-dashboard-resumen/tasks.md)
  - Modelo de Datos: [specs/02-dashboard-resumen/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/02-dashboard-resumen/data-model.md)
  - Investigación Técnica: [specs/02-dashboard-resumen/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/02-dashboard-resumen/research.md)
  - Guía de Validación: [specs/02-dashboard-resumen/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/02-dashboard-resumen/quickstart.md)
  - Contratos de Interfaz: `specs/02-dashboard-resumen/contracts/` (`analytics-service.json`, `quick-access.json`)
- **Rama Git**: `phase/02-dashboard-resumen`
- **Resultados de Pruebas**: 
  - 9 archivos de test, 36 pruebas unitarias e integración aprobadas al 100% (`tests/dashboard.test.tsx`, `tests/auth.test.tsx`, `tests/recovery.test.tsx`, `tests/perimeter.test.tsx`, `tests/avatar-validation.test.ts`, `tests/session.test.ts`, `tests/layout.test.tsx`, `tests/accessibility.test.ts`, `tests/a11y-keyboard.test.tsx`).
- **Resultado de Compilación**:
  - Compilación TypeScript estricta (`tsc --noEmit`) sin advertencias y bundle de producción en `dist/` generado exitosamente con Vite en 866ms.
- **Estado Actual**: `IMPLEMENTADA`
- **Problemas Conocidos**: Ninguno. 100% de los requisitos funcionales (FR-001 a FR-007) y criterios de éxito (SC-001 a SC-005) implementados y verificados.

---

## Fase 03: Directorio de Centros de Acopio y Georreferenciación

- **Objetivo**: Administrar el catálogo maestro de infraestructura física de recolección de e-waste. Provee tabla estructurada con columnas obligatorias (*Lugar*, *Categoría / Ubicación*, *Estado*, *Acciones*), badges tricolores semánticos (`Funcional`, `Remodelación`, `Inoperativo`), búsqueda omnicanal en tiempo real con debounce de 250ms, filtros por píldora tricolor con conteo dinámico, formulario modal de 9 campos obligatorios con validación estricta de protocolo seguro HTTPS, visualización técnica con georreferenciación en Google Maps (`target="_blank"`, `rel="noopener noreferrer"`) y baja lógica con justificación auditable de al menos 10 caracteres (Art. VII).
- **Artefactos Spec-Kit**:
  - Especificación: [specs/03-directorio-centros-acopio/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/03-directorio-centros-acopio/spec.md)
  - Plan de Implementación: [specs/03-directorio-centros-acopio/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/03-directorio-centros-acopio/plan.md)
  - Lista de Tareas: [specs/03-directorio-centros-acopio/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/03-directorio-centros-acopio/tasks.md)
  - Modelo de Datos: [specs/03-directorio-centros-acopio/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/03-directorio-centros-acopio/data-model.md)
  - Investigación Técnica: [specs/03-directorio-centros-acopio/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/03-directorio-centros-acopio/research.md)
  - Guía de Validación: [specs/03-directorio-centros-acopio/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/03-directorio-centros-acopio/quickstart.md)
  - Contratos de Interfaz: `specs/03-directorio-centros-acopio/contracts/`
- **Rama Git**: `phase/03-directorio-centros-acopio`
- **Resultados de Pruebas**: 
  - 10 archivos de test, 53 pruebas unitarias e integración aprobadas al 100% (`tests/directory.test.tsx` con 17 pruebas).
- **Resultado de Compilación**:
  - Compilación TypeScript estricta (`tsc --noEmit`) sin advertencias y bundle de producción en `dist/` generado exitosamente con Vite.
- **Estado Actual**: `IMPLEMENTADA`
- **Problemas Conocidos**: Ninguno. 100% de los requisitos funcionales (FR-001 a FR-010) y criterios de éxito (SC-001 a SC-006) implementados y convergidos.

---

## Fase 04: Receptores y Franjas Horarias

- **Objetivo**: Administrar el personal de campo y las franjas horarias de recepción física de residuos electrónicos. Provee matriz operativa con columnas obligatorias (*Lugar*, *Encargados*, *Horario*, *Estado*, *Acciones*), chips interactivos de correo institucional de encargados, chips en estado pendiente `"Por definir"` para puntos sin personal, chips de rango horario 12h interactivos, modal de gestión y aprovisionamiento de cuentas móviles de receptores (`#modal-receptor`) con validación de correo RFC 5322 y asignación perimetral de rol exclusivo `receptor` (restringido para acceso web), modal de parametrización de franjas horarias (`#modal-schedule`) con validación estricta de orden temporal (`start_time < end_time`) y cuotas de entrega, modal de ficha técnica consolidada (`#modal-point-view`) y desvinculación segura sin borrado destructivo (Art. VII).
- **Artefactos Spec-Kit**:
  - Especificación: [specs/04-receptores-y-horarios/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/04-receptores-y-horarios/spec.md)
  - Plan de Implementación: [specs/04-receptores-y-horarios/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/04-receptores-y-horarios/plan.md)
  - Lista de Tareas: [specs/04-receptores-y-horarios/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/04-receptores-y-horarios/tasks.md)
  - Modelo de Datos: [specs/04-receptores-y-horarios/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/04-receptores-y-horarios/data-model.md)
  - Investigación Técnica: [specs/04-receptores-y-horarios/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/04-receptores-y-horarios/research.md)
  - Guía de Validación: [specs/04-receptores-y-horarios/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/04-receptores-y-horarios/quickstart.md)
  - Contratos de Interfaz: `specs/04-receptores-y-horarios/contracts/`
- **Rama Git**: `phase/04-receptores-y-horarios`
- **Resultados de Pruebas**: 
  - 11 archivos de test, 71 pruebas unitarias e integración aprobadas al 100% (`tests/receivers.test.tsx` con 18 pruebas nuevas).
- **Resultado de Compilación**:
  - Compilación TypeScript estricta (`tsc --noEmit`) sin errores y bundle de producción en `dist/` generado exitosamente con Vite en 1.21s (73 módulos transformados).
- **Estado Actual**: `IMPLEMENTADA`
- **Problemas Conocidos**: Ninguno. 100% de los requisitos funcionales (FR-001 a FR-010) y criterios de éxito (SC-001 a SC-006) implementados y validados.

---

## Fase 05: Supervisión de Entregas y Trazabilidad

- **Objetivo**: Fiscalizar y auditar el flujo de entregas ciudadanas de residuos electrónicos (e-waste) en la red de acopio. Provee matriz maestra de entregas con columnas obligatorias (*Código*, *Propietario*, *Punto*, *Estado*, *Acción*), códigos estructurados `#E...` con fecha/hora legible, badges semánticos normalizados (`Recibida` verde `#146C43` / `No recibida` ámbar `#B54708`), barra superior de búsqueda predictiva en tiempo real (`#search-entregas-input`), botones de estado con contadores en tiempo real (`Todos (7)`, `Recibida (4)`, `No recibida (3)`), selector desplegable de centros de acopio (`#filter-point-select`), modal interactivo de detalle técnico (`#modal-delivery-detail`) con receptor validante institucional (o `"Sin receptor asignado"`), tabla secundaria de **Líneas de dispositivos declarados (AF-13)** (*Categoría*, *Descripción*, *Cantidad*, *Condición*) con leyenda reglamentaria, y exportación a archivo CSV con codificación UTF-8 BOM (`Reporte_Entregas_NOKI_YYYY-MM-DD.csv`) mediante botón prominente `"Exportar reporte"`.
- **Artefactos Spec-Kit**:
  - Especificación: [specs/05-supervision-entregas/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/05-supervision-entregas/spec.md)
  - Plan de Implementación: [specs/05-supervision-entregas/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/05-supervision-entregas/plan.md)
  - Lista de Tareas: [specs/05-supervision-entregas/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/05-supervision-entregas/tasks.md)
  - Modelo de Datos: [specs/05-supervision-entregas/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/05-supervision-entregas/data-model.md)
  - Investigación Técnica: [specs/05-supervision-entregas/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/05-supervision-entregas/research.md)
  - Guía de Validación: [specs/05-supervision-entregas/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/05-supervision-entregas/quickstart.md)
  - Contratos de Interfaz: `specs/05-supervision-entregas/contracts/`
- **Rama Git**: `phase/05-supervision-entregas`
- **Resultados de Pruebas**: 
  - 12 archivos de test, 91 pruebas unitarias e integración aprobadas al 100% (`tests/deliveries.test.tsx` con 20 pruebas nuevas).
- **Resultado de Compilación**:
  - Compilación TypeScript estricta (`tsc --noEmit`) con 0 errores y bundle de producción en `dist/` generado exitosamente con Vite en 2.58s (79 módulos transformados).
- **Estado Actual**: `IMPLEMENTADA`
- **Problemas Conocidos**: Ninguno. 100% de los requisitos funcionales (FR-001 a FR-012) y criterios de éxito (SC-001 a SC-005) implementados y probados.

---

## Fase 06: Módulo Aprende — CMS Pedagógico

- **Objetivo**: Implementar el sistema de gestión de contenidos (CMS) pedagógico para el Módulo 06. Provee tabla editorial estructurada con columnas obligatorias (*Título*, *Fecha*, *Fuente*, *Acción*), badges de fuente redondeados (`.source-badge`) con contraste WCAG 2.1 AA (≥ 4.5:1), formulario modal polimórfico (`#modal-form`) para creación y edición de artículos con validación estricta de 5 campos (título, fecha, fuente, URL con protocolo `http://`/`https://`, cuerpo ≥ 10 chars), tarjeta de previsualización móvil en modal de inspección (`#modal-view`, `.aprende-card-preview`) con enlace externo `"Consultar fuente"` (`target="_blank"`, `rel="noopener noreferrer"`), y modal de confirmación disuasorio (`#modal-delete`) para baja lógica no destructiva (Art. VII de la Constitución) con registro en auditoría inmutable.
- **Artefactos Spec-Kit**:
  - Especificación: [specs/06-modulo-aprende-cms/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/06-modulo-aprende-cms/spec.md)
  - Plan de Implementación: [specs/06-modulo-aprende-cms/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/06-modulo-aprende-cms/plan.md)
  - Lista de Tareas: [specs/06-modulo-aprende-cms/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/06-modulo-aprende-cms/tasks.md)
  - Modelo de Datos: [specs/06-modulo-aprende-cms/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/06-modulo-aprende-cms/data-model.md)
  - Investigación Técnica: [specs/06-modulo-aprende-cms/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/06-modulo-aprende-cms/research.md)
  - Guía de Validación: [specs/06-modulo-aprende-cms/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/06-modulo-aprende-cms/quickstart.md)
  - Contratos de Interfaz: `specs/06-modulo-aprende-cms/contracts/` (`article-form.json`, `article-view.json`)
- **Rama Git**: `phase/06-modulo-aprende-cms`
- **Resultados de Pruebas**:
  - 13 archivos de test, 110 pruebas unitarias e integración aprobadas al 100% (`tests/education.test.tsx` con 19 pruebas nuevas).
  - Cobertura: catálogo editorial (US1), creación con validación estricta (US2), inspección visual + edición (US3), baja lógica con confirmación disuasoria (US4), cierre con tecla `Escape`, estado vacío.
- **Resultado de Compilación**:
  - Compilación TypeScript estricta (`tsc --noEmit`) con 0 errores y bundle de producción en `dist/` generado exitosamente con Vite en 1.01s (87 módulos transformados).
- **Estado Actual**: `IMPLEMENTADA`
- **Problemas Conocidos**: Ninguno. 100% de los requisitos funcionales (AF-61 a AF-68) y criterios de aceptación Gherkin implementados y verificados.

---

## Fase 07: Reportes, Balances y Auditoría

- **Objetivo**: Implementar el sistema integral de generación, consulta y análisis de balances y reportes operativos y estratégicos para el Módulo 07. Provee catálogo maestro de reportes periódicos (diario, semanal, mensual) con badges normalizados por nivel de granularidad temporal (`diario` en verde `#146C43`/`#DCFCE7`, `semanal` en azul `#0369A1`/`#E0F2FE`, `mensual` en púrpura `#6D28D9`/`#EDE9FE`), filtros por píldoras con conteo dinámico (`Todos (7)`, `Diario (3)`, `Semanal (2)`, `Mensual (2)`), buscador predictivo en tiempo real por código y período, selector de centros de acopio, modal accesible de ficha técnica consolidada (`#modal-report-detail`) con 3 tarjetas de KPIs ejecutivos (*Total entregas*, *Unidades e-waste*, *Puntos activos*) y tabla de desglose por categoría RAEE (*Categoría*, *Unidades*, *Peso estimado*, *Estado de recepción* con badges "Verificado"), y exportación auditable directa a archivo CSV con codificación UTF-8 BOM (`Reporte_{id}_NOKI.csv`) con cabecera estándar y compatibilidad nativa con hojas de cálculo.
- **Artefactos Spec-Kit**:
  - Especificación: [specs/07-reportes-y-balances/spec.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/07-reportes-y-balances/spec.md)
  - Plan de Implementación: [specs/07-reportes-y-balances/plan.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/07-reportes-y-balances/plan.md)
  - Lista de Tareas: [specs/07-reportes-y-balances/tasks.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/07-reportes-y-balances/tasks.md)
  - Modelo de Datos: [specs/07-reportes-y-balances/data-model.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/07-reportes-y-balances/data-model.md)
  - Investigación Técnica: [specs/07-reportes-y-balances/research.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/07-reportes-y-balances/research.md)
  - Guía de Validación: [specs/07-reportes-y-balances/quickstart.md](file:///c:/Users/Asus/Desktop/Noki%20Web%202,0/specs/07-reportes-y-balances/quickstart.md)
  - Contratos de Interfaz: `specs/07-reportes-y-balances/contracts/` (`reports-service.json`, `report-detail-modal.json`)
- **Rama Git**: `phase/07-reportes-y-balances`
- **Resultados de Pruebas**:
  - 14 archivos de test, 136 pruebas unitarias e integración aprobadas al 100% (`tests/reports.test.tsx` con 26 pruebas nuevas).
  - Cobertura: renderizado del catálogo y columnas de la tabla (US1), badges semánticos por periodicidad y contraste WCAG 2.1 AA (US1), botones de acción con aria-label (US1), filtros pill con contadores y restablecimiento (US1), búsqueda por código y mes (US1), apertura de modal de detalle con tarjetas KPI y desglose de 4 categorías RAEE (US2), cierre por Escape, botón cabecera y botón footer (US2), descarga y estructura del CSV con BOM UTF-8 (US3), estado vacío sin resultados.
- **Resultado de Compilación**:
  - Compilación TypeScript estricta (`tsc --noEmit`) con 0 errores y bundle de producción en `dist/` generado exitosamente con Vite en 2.19s (91 módulos transformados).
- **Estado Actual**: `IMPLEMENTADA`
- **Problemas Conocidos**: Ninguno. 100% de los requisitos funcionales (FR-001 a FR-012) y criterios de éxito (SC-001 a SC-006) implementados, probados y verificados.


