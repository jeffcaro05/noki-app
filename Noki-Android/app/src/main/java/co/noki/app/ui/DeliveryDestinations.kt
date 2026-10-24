package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import co.noki.app.data.*
import co.noki.app.domain.*
import java.util.UUID

/** Public visibility never depends on the authenticated handoff-recipient list. */
internal fun visibleDeliveryPlaces(points:List<CollectionPoint>,search:String="",category:String="",onlyPublished:Boolean=false):List<CollectionPoint> =
    points.filter {p -> (p.name.contains(search.trim(),true) || p.address.contains(search.trim(),true)) &&
        (!onlyPublished || category.isBlank() || category in p.categories)}

internal fun destinationDescription(point:CollectionPoint,receivers:List<ReceiverPlace>,ownRequestId:String?):String = when {
    point.receiverRequestId==null -> "Referencia pública · consulta la dirección y las condiciones. No confirma entregas en Noki."
    point.receiverRequestId==ownRequestId -> "Tu lugar de recepción · visible para los demás usuarios. No puedes enviarte una entrega a ti mismo."
    receivers.any {it.requestId==point.receiverRequestId} -> "Recibe entregas en Noki · elige este destino para obtener tu QR y código."
    else -> "Lugar publicado · consulta su disponibilidad para tu entrega."
}

@Composable internal fun PublishedDeliveryPlaces(points:List<CollectionPoint>,receivers:List<ReceiverPlace>,ownRequestId:String?,selected:(CollectionPoint)->Unit) {
    points.forEach {point ->
        ActionCard(point.name,"${point.address}\n${destinationDescription(point,receivers,ownRequestId)}",Icons.Outlined.Place) {selected(point)}
    }
}

/** The same creation action works in a place sheet without navigating back to a selector. */
@Composable internal fun DestinationDeliveryAction(model:NokiModel,ids:List<String>,receiver:ReceiverPlace,open:(String)->Unit) {
    val selected=model.devices.filter {it.id in ids}
    var contactConfirmed by remember(receiver,selected) {mutableStateOf(false)}
    val requestId=rememberSaveable {UUID.randomUUID().toString()}
    var attempted by remember {mutableStateOf(false)}
    Text("Aparatos de esta entrega",style=MaterialTheme.typography.titleMedium)
    selected.forEach {Text("• ${it.description}")}
    Text("${receiver.hours}\n${receiver.conditions}")
    if(selected.isEmpty()) Note("Primero selecciona los aparatos que vas a entregar.")
    if(selected.size!=ids.size || selected.any {!DeliveryRules.ready(it) || DeliveryRules.locked(it.id,model.outgoing)})
        Note("Revisa la preparación de los aparatos antes de continuar. Tu información está guardada.",true)
    if(selected.size>20) Note("Puedes incluir hasta 20 aparatos en una entrega.",true)
    DeliveryContactPanel(receiver,selected,contactConfirmed,{contactConfirmed=it},model.demo)
    if(!model.receptionReady || model.receptionSyncError) {
        Note("No pudimos confirmar la disponibilidad. Actualiza para volver a intentarlo.",true)
        OutlinedButton(onClick=model::reloadReception,enabled=!model.busy) {Text("Actualizar disponibilidad")}
    }
    if(attempted && !model.noticeSuccess) model.notice?.let {StatusNotice(it)}
    Text("Verás tu QR y código en la siguiente pantalla y podrás calcular la ruta. Muéstralos al receptor cuando entregues los aparatos.")
    PrimaryButton(if(model.busy && attempted) "Generando…" else "Generar QR y código",
        !model.busy && model.receptionReady && !model.receptionSyncError && selected.size==ids.size && DeliveryRules.canCreate(selected,model.outgoing,receiver,contactConfirmed)) {
        attempted=true
        model.createDelivery(ids,receiver.id,requestId,contactConfirmed,open)
    }
}
