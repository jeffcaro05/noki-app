# Decisiones de arquitectura

Consolidación retrospectiva del 24/10/2026. Las decisiones se documentan ahora a partir del producto y de la conversación; no se atribuyen a archivos o PR históricos inexistentes.

## ADR01 Android nativo

Contexto: ya existe una app Kotlin y Jetpack Compose. Alternativa: migrar a Flutter para ampliar plataformas. Decisión del equipo: conservar la implementación y posponer la migración. Consecuencia: Android es el cliente móvil final; no hay versión iOS certificada.

## ADR02 Datos compartidos

Contexto: la app usa Supabase y la web inicial estaba asociada a Firebase. Alternativa: mantener dos bases y sincronizarlas. Decisión: ambos clientes usan la misma identidad y datos de Supabase, con permisos por función. Consecuencia: evita duplicación; exige mantener contratos, autorización y disponibilidad del servicio.

## ADR03 Alojamiento de la web

Contexto: ya hay un proyecto Firebase. Alternativa: desplegar y operar un servidor propio. Decisión: Firebase Hosting entrega la web estática; no se activa una segunda autenticación ni base para el panel. Consecuencia: actualizar estilos no obliga a volver a vincular los datos, pero no debe romper los contratos de acceso.

## ADR04 Código persistente de entrega

Contexto: renovar el código en pocos minutos dificulta el traslado. Alternativa: renovación periódica. Decisión: código estable durante la entrega pendiente, hasta recepción o cancelación; el servidor controla estado y receptor autorizado. Consecuencia: mostrarlo solo al entregar; escanear no equivale a confirmar recepción y un código cerrado no permite otra entrega.

## ADR05 Revisión humana de receptores

Contexto: no hay presupuesto para biometría/documentos con proveedor especializado. Alternativa: autenticidad y prueba de vida de pago. Decisión: solicitud con información y fotos del lugar, revisión administrativa, contacto y posibilidad de visitas. Consecuencia: no hay aprobación automática ni garantía absoluta contra fraude; los lugares de referencia no se anuncian como socios aprobados.
