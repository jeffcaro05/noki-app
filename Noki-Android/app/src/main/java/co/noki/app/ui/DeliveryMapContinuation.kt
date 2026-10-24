package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.noki.app.data.CollectionPoint
import co.noki.app.domain.*

/** Same next action in the place sheet, directory and expanded route map. */
@Composable internal fun DeliveryMapContinuation(
    point:CollectionPoint?, active:Delivery?, device:DeviceRecord?, choosingReceiver:Boolean,
    receivers:List<ReceiverPlace>, canSave:Boolean,
    openDelivery:(String)->Unit, continueDelivery:(CollectionPoint?)->Unit,
    chooseAnother:()->Unit, backToPreparation:()->Unit,
    ownRequestId:String?=null, generateHere:(@Composable (ReceiverPlace)->Unit)?=null
) {
    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(6.dp)) {
        if(active!=null) {
            Text("Tu entrega: ${active.receiverName}",style=MaterialTheme.typography.titleSmall)
            if(point!=null && receivers.none {it.id==active.receiverId && it.requestId==point.receiverRequestId})
                Text("Consultar otro lugar no cambia el receptor de tu entrega.",style=MaterialTheme.typography.bodyMedium)
            PrimaryButton(if(active.status==DeliveryStatus.PENDING) "Mostrar QR y código" else "Ver estado de la entrega") {openDelivery(active.id)}
        } else if(choosingReceiver || device?.decision==Decision.DELIVER) {
            when {
                point==null -> {
                    Text("Toca un lugar en el mapa o en la lista para elegir el destino de tu entrega.")
                }
                point.receiverRequestId==null -> {
                    Text("Este lugar es una referencia pública y todavía no confirma entregas en Noki. Puedes consultar su ruta. Para registrar una entrega con QR, elige un lugar que reciba entregas en la app.")
                    PrimaryButton("Ver otros lugares",onClick=chooseAnother)
                }
                !canSave -> PrimaryButton("Entrar para continuar mi entrega") {continueDelivery(point)}
                point.receiverRequestId==ownRequestId -> {
                    Text("Este es tu lugar de recepción. Los demás usuarios pueden elegirlo para entregarte aparatos; no puedes enviarte una entrega a ti mismo.")
                    PrimaryButton("Ver otros lugares",onClick=chooseAnother)
                }
                receivers.none {it.requestId==point.receiverRequestId} -> {
                    Text("No pudimos confirmar la disponibilidad de este lugar para tu entrega. Actualiza el directorio o elige otro lugar; tu preparación se conserva.")
                    PrimaryButton("Ver otros lugares",onClick=chooseAnother)
                }
                device!=null && !DeliveryRules.ready(device) -> {
                    Text("Completa la preparación del aparato antes de generar el código.")
                    PrimaryButton("Continuar preparación",onClick=backToPreparation)
                }
                else -> {
                    val receiver=receivers.first {it.requestId==point.receiverRequestId}
                    if(generateHere!=null) generateHere(receiver)
                    else PrimaryButton("Elegir este destino") {continueDelivery(point)}
                }
            }
        } else if(device!=null && !device.completed) {
            PrimaryButton("Guardar lugar y continuar",onClick=backToPreparation)
        } else if(point!=null) {
            Text("Para obtener un código, primero prepara los aparatos que vas a entregar.",style=MaterialTheme.typography.bodyMedium)
            PrimaryButton("Preparar una entrega") {continueDelivery(point)}
        }
    }
}
