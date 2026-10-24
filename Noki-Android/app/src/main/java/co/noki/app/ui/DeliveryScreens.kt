package co.noki.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.noki.app.data.*
import co.noki.app.domain.*
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.*
import java.util.UUID

@Composable internal fun CreateDeliveryScreen(model:NokiModel,initialDevice:String,open:(String)->Unit,points:(String,List<String>)->Unit={_,_->},pending:()->Unit={},requestedReceiver:String="",receiverConsumed:()->Unit={}) {
    DeliveryAutoRefresh(model,!model.demo && model.canSave)
    DirectoryAutoRefresh(model)
    var ids by rememberSaveable {mutableStateOf(listOf(initialDevice).filter {it.isNotBlank()})}
    var receiverId by rememberSaveable {mutableStateOf("")}
    var chooseReceiver by rememberSaveable {mutableStateOf(true)}
    var inspectedPointId by rememberSaveable {mutableStateOf("")}
    LaunchedEffect(requestedReceiver,model.receivers) {
        if(requestedReceiver.isNotBlank()) {
            val requested=model.receivers.firstOrNull {it.requestId==requestedReceiver}
            receiverId=requested?.id.orEmpty();chooseReceiver=requested==null
            if(requested!=null) receiverConsumed()
        }
    }
    val receiver=model.receivers.firstOrNull {it.id==receiverId}
    val selected=model.devices.filter {it.id in ids}
    var contactConfirmed by remember(receiver,selected) {mutableStateOf(false)}
    val requestId=rememberSaveable {UUID.randomUUID().toString()}
    Page(resetKey=receiverId) {
        Text("Prepara una entrega",style=MaterialTheme.typography.headlineMedium)
        Text("Puedes incluir varios aparatos para un mismo receptor. Solo tendrás una entrega pendiente a la vez.")
        Text("Elige el lugar de entrega, confirma los aparatos y genera tu QR. Después puedes calcular la ruta.",style=MaterialTheme.typography.bodyMedium)
        if(model.activeDelivery!=null) {
            Note("Ya tienes una entrega pendiente. No necesitas crear otra.")
            PrimaryButton("Ver entrega pendiente") {open(model.activeDelivery!!.id)}
        } else if(!model.canSave) {Note("Entra a tu cuenta para crear una entrega.")}
        else {
            if(!model.demo) OutlinedButton(onClick={model.reloadReception();model.loadPoints()},enabled=!model.busy) {Text("Actualizar lugares")}
            if(model.demo) Note("Práctica local: este receptor es ficticio y no recibe aparatos reales.")
            Text("1. Elige dónde entregar",style=MaterialTheme.typography.titleLarge)
            if(model.receivers.isEmpty()) {
                Note(if(model.receiverApproved) "Tu preparación está guardada. Tu propio lugar aparece en Puntos, pero no puedes enviarte una entrega a ti mismo. Necesitas otro receptor; para probar tu lugar, prepara la entrega desde otra cuenta." else "Tu preparación está guardada. Aún no hay receptores disponibles para tu cuenta. La lista se actualizará automáticamente cuando se habilite uno.")
            }
            if(chooseReceiver || receiver==null) {
                PublishedDeliveryPlaces(visibleDeliveryPlaces(model.points),model.receivers,model.receiverApplication?.optString("id")) {point ->
                    inspectedPointId=point.id
                    val target=model.receivers.firstOrNull {it.requestId==point.receiverRequestId}
                    receiverId=target?.id.orEmpty();chooseReceiver=target==null;receiverConsumed()
                }
                // A delayed public-directory request must not hide an available authenticated recipient.
                model.receivers.filter {r->model.points.none {it.receiverRequestId==r.requestId}}.forEach {place ->
                    Choice("${place.name}\n${place.address}",place.id==receiverId) {receiverId=place.id;chooseReceiver=false;inspectedPointId="";receiverConsumed()}
                }
                model.points.firstOrNull {it.id==inspectedPointId}?.takeIf {receiver==null}?.let {point ->
                    Note(destinationDescription(point,model.receivers,model.receiverApplication?.optString("id")))
                    OutlinedButton(onClick={points(point.id,ids)}) {Text("Ver ${point.name} y su ruta")}
                }
            }
            if(receiver!=null) {
                Text("${receiver.name}\n${receiver.address}",style=MaterialTheme.typography.titleMedium)
                Text("${receiver.hours}\n${receiver.conditions}");DeliveryTrustNote(reviewed=true,practice=model.demo)
                TextButton(onClick={chooseReceiver=!chooseReceiver}) {Text(if(chooseReceiver) "Cerrar lista de receptores" else "Cambiar receptor")}
            }
            if(!model.demo) OutlinedButton(onClick={points(receiver?.requestId.orEmpty(),ids)},modifier=Modifier.fillMaxWidth()) {Text("Elegir lugar en el mapa")}
            if(requestedReceiver.isNotBlank() && model.receptionReady && model.receivers.none {it.requestId==requestedReceiver}) Note("Ese lugar ya no está disponible para tu cuenta. Elige otro receptor de la lista; tus aparatos siguen seleccionados.",true)
            Text("2. Confirma los aparatos",style=MaterialTheme.typography.titleLarge)
            val ready=model.devices.filter {DeliveryRules.ready(it) && !DeliveryRules.locked(it.id,model.outgoing)}
            ready.forEach {device -> AcceptanceCheck(device.description,device.id in ids) {check->ids=if(check)(ids+device.id).distinct() else ids-device.id}}
            if(ready.isEmpty()) {Note("Completa la preparación de un aparato y elige «Entregar, donar o llevar a recolección» para incluirlo.");OutlinedButton(onClick=pending){Text("Volver a mis pendientes")}}
            if(ids.any {id->ready.none {it.id==id}}) {
                Note("Uno de los aparatos seleccionados ya no está disponible o no terminó su preparación. Revisa tus pendientes o retíralo de esta lista.",true)
                OutlinedButton(onClick={ids=ids.filter {id->ready.any {it.id==id}}}){Text("Quitar aparatos no disponibles")}
            }
            if(receiver==null) Note("Elige arriba el lugar que recibirá los aparatos.")
            if(selected.isEmpty()) Note("Marca al menos un aparato para incluirlo en la entrega.")
            if(selected.size>20) Note("Puedes incluir hasta 20 aparatos en una entrega. Quita los que sobran para continuar.",true)
            if(receiver!=null) DeliveryContactPanel(receiver,selected,contactConfirmed,{contactConfirmed=it},model.demo)
            if(!model.receptionReady || model.receptionSyncError) Note("Revisa tu conexión y pulsa «Actualizar lugares» para confirmar la disponibilidad.",true)
            Text("El código identifica esta lista; solo podrá usarlo el receptor elegido. Generarlo no confirma una recepción ni garantiza que el lugar acepte los aparatos.")
            PrimaryButton("Generar QR y código",!model.busy && model.receptionReady && !model.receptionSyncError && receiver!=null && DeliveryRules.canCreate(selected,model.outgoing,receiver,contactConfirmed) && selected.size==ids.size) {model.createDelivery(ids,receiverId,requestId,contactConfirmed,open)}
        }
    }
}

@Composable internal fun DeliveryScreen(model:NokiModel,id:String,result:(String)->Unit,receiverMode:Boolean=false,points:()->Unit={}) {
    val delivery=model.deliveries.firstOrNull {it.id==id}
    var cancel by remember {mutableStateOf(false)}
    var confirmReceive by remember {mutableStateOf(false)}
    var confirmComplete by remember {mutableStateOf(false)}
    var practiceReception by remember {mutableStateOf(false)}
    var reason by rememberSaveable {mutableStateOf("")}
    val receiving=receiverMode || practiceReception
    DeliveryAutoRefresh(model,delivery?.active!=false)
    if(delivery==null) {Page {Text("No encontramos esta entrega.");PrimaryButton("Actualizar",!model.busy,onClick=model::reloadReception)};return}
    Page(resetKey=delivery.status) {
        Text(if(delivery.status==DeliveryStatus.COMPLETED) "Resultado de la entrega" else if(receiving) "Revisar recepción" else "Tu entrega",style=MaterialTheme.typography.headlineMedium)
        Text(delivery.receiverName,style=MaterialTheme.typography.titleLarge);Text(delivery.address)
        if(delivery.status==DeliveryStatus.COMPLETED) StatusNotice("Entrega completada",success=true) else Note(delivery.status.label)
        if(model.receptionSyncError) StatusNotice("No pudimos actualizar la entrega. Se reintentará al recuperar la conexión; todavía no confirmamos cambios.")
        Text("Aparatos incluidos",style=MaterialTheme.typography.titleLarge)
        delivery.items.forEach {Text("• ${it.description}");if(it.safety!="normal") Note("Este aparato requiere atención. Confirma sus condiciones antes de recibirlo.",true)}
        if(delivery.contactConfirmed) Note(if(receiving) "Quien entrega declaró haber acordado contigo la recepción de esta lista y su estado. Compruébalo antes de confirmar. Puedes no aceptar los aparatos si no cumplen lo acordado." else "Declaraste haber confirmado con el receptor la recepción de estos aparatos en su estado actual. Mantén lo acordado; el código no sustituye su revisión en el lugar.")
        if(!model.demo) OutlinedButton(onClick=model::reloadReception,enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Actualizar estado de la entrega")}
        when(delivery.status) {
            DeliveryStatus.PENDING -> {
                if(receiving) {
                    Text("Comprueba que los aparatos coincidan con la lista. No confirmes si todavía no los tienes.")
                    if(model.previewDeliveryId!=id) CodeEntry(model) { }
                    PrimaryButton("Confirmar recepción",!model.busy && model.previewDeliveryId==id) {confirmReceive=true}
                } else {
                    Text("Muestra este código al receptor cuando estés en el lugar y vayas a entregar los aparatos. Puede escanearlo o escribirlo. No lo publiques ni lo envíes a otras personas.")
                    val code=model.deliveryCode?.takeIf {it.id==id}
                    if(code!=null && code.isDisplayable()) {
                        val bitmap=remember(code.qr) {runCatching {BarcodeEncoder().encodeBitmap(code.qr,BarcodeFormat.QR_CODE,640,640).asImageBitmap()}.getOrNull()}
                        bitmap?.let {qr->Box(Modifier.fillMaxWidth(),contentAlignment=Alignment.Center) {Image(qr,"Código QR de esta entrega",Modifier.size(280.dp))}}
                        Text(code.code,modifier=Modifier.fillMaxWidth(),style=MaterialTheme.typography.headlineMedium,textAlign=TextAlign.Center)
                        Text(if(code.validWhilePending) "Este QR y código son los mismos mientras tu entrega esté pendiente. Dejan de funcionar cuando el receptor confirma la recepción o cancelas la entrega." else "Este código corresponde a una versión anterior. Consulta el código vigente de esta entrega antes de salir.")
                    } else Text("Tu entrega está guardada. Abre su QR y código para mostrárselos al receptor.")
                    if(code?.validWhilePending!=true) PrimaryButton("Mostrar QR y código",!model.busy) {model.deliveryAction("token",id)}
                    if(!model.demo) OutlinedButton(onClick=points,modifier=Modifier.fillMaxWidth()) {Text("Ver lugar y calcular ruta")}
                    Text("Cuando el receptor confirme que recibió los aparatos, verás aquí el resultado automáticamente. No necesitas confirmar otra vez.")
                    if(model.demo) OutlinedButton(onClick={practiceReception=true},enabled=model.deliveryCode!=null,modifier=Modifier.fillMaxWidth()) {Text("Probar recepción de este ejemplo")}
                    OutlinedButton(onClick={cancel=true},enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Cancelar entrega")}
                }
            }
            DeliveryStatus.RECEIVED -> {
                Text("El receptor confirmó que recibió los aparatos. Falta la confirmación de quien entrega.")
                if(!receiving || model.demo) PrimaryButton("Confirmar que entregué los aparatos",!model.busy) {confirmComplete=true}
                Text("Si hay un error, no confirmes: contacta a ${LegalDocuments.CONTACT}. Una recepción confirmada no puede borrarse para ocultar lo ocurrido.",style=MaterialTheme.typography.bodyMedium)
            }
            DeliveryStatus.COMPLETED -> {
                Text("El receptor confirmó que recibió los aparatos. Ya quedaron en Mis aparatos e Historial. Esta constancia no certifica el reciclaje o tratamiento posterior.")
                Text("Referencia: ${delivery.id}",style=MaterialTheme.typography.bodyMedium)
                if(!receiving || model.demo) delivery.items.forEach {item->PrimaryButton("Ver aparato: ${item.description}") {result(item.deviceId)}}
            }
            DeliveryStatus.CANCELLED -> Text("El código dejó de ser válido. Tus aparatos y su preparación se conservan; puedes crear otra entrega.")
        }
    }
    if(cancel) AlertDialog(onDismissRequest={cancel=false},title={Text("¿Cancelar esta entrega?")},text={Column(verticalArrangement=Arrangement.spacedBy(12.dp)){Text("Se invalidará el código y podrás crear otra entrega. No se borrarán tus aparatos. Se conservará la constancia de cancelación.");OutlinedTextField(reason,{reason=it.take(300)},label={Text("Motivo de cancelación")})}},confirmButton={TextButton(onClick={cancel=false;model.deliveryAction("cancel",id,reason=reason)},enabled=!model.busy && reason.trim().length>=5){Text("Sí, cancelar entrega")}},dismissButton={TextButton(onClick={cancel=false}){Text("Conservar entrega")}})
    if(confirmReceive) AlertDialog(onDismissRequest={confirmReceive=false},title={Text("¿Recibiste todos estos aparatos?")},text={Text("Confirma solo después de comprobar la lista y tener los aparatos. La entrega quedará completada y se mostrará el resultado a quien entrega.")},confirmButton={TextButton(onClick={confirmReceive=false;model.deliveryAction("receive",id)},enabled=!model.busy){Text("Sí, los recibí")}},dismissButton={TextButton(onClick={confirmReceive=false}){Text("Todavía no")}})
    if(confirmComplete) AlertDialog(onDismissRequest={confirmComplete=false},title={Text("¿Terminaste la entrega?")},text={Text("El receptor ya confirmó. Al aceptar cerrarás la entrega y podrás registrar el resultado.")},confirmButton={TextButton(onClick={confirmComplete=false;model.deliveryAction("complete",id)},enabled=!model.busy){Text("Sí, terminé la entrega")}},dismissButton={TextButton(onClick={confirmComplete=false}){Text("Volver")}})
}

@Composable internal fun CodeEntry(model:NokiModel,found:(String)->Unit) {
    var code by remember {mutableStateOf("")}
    val scanner=rememberLauncherForActivityResult(ScanContract()) {result->result.contents?.let {value->model.deliveryAction("preview",code=value) {model.previewDeliveryId?.let(found)}}}
    Text("Escanea el QR o escribe el código que muestra quien entrega.")
    OutlinedTextField(code,{code=it.trim().take(160)},label={Text("Código de entrega")},modifier=Modifier.fillMaxWidth(),singleLine=true,enabled=!model.busy)
    PrimaryButton("Comprobar código",!model.busy && code.isNotBlank()) {model.deliveryAction("preview",code=code) {code="";model.previewDeliveryId?.let(found)}}
    OutlinedButton(onClick={scanner.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE).setPrompt("Enfoca el código QR de Noki").setBeepEnabled(false).setBarcodeImageEnabled(false).setOrientationLocked(true))},enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Escanear QR")}
}

@Composable internal fun ReceptionScreen(model:NokiModel,open:(String)->Unit,user:()->Unit) {
    Page {
        Text("Recepciones",style=MaterialTheme.typography.headlineMedium)
        PrimaryButton("Cambiar a mis aparatos",onClick=user)
        if(!model.receiverApproved) {Note("Tu perfil debe estar aprobado y activo para recibir aparatos.");return@Page}
        Text(model.receiverApplication?.optString("place_name").orEmpty(),style=MaterialTheme.typography.titleLarge)
        Text("Perfil revisado",color=MaterialTheme.colorScheme.primary)
        CodeEntry(model,open)
        OutlinedButton(onClick=model::reloadReception,enabled=!model.busy) {Text("Actualizar recepciones")}
        val incoming=model.deliveries.filter {it.receiverId==model.ownId}.sortedByDescending {it.createdAt}
        if(incoming.isEmpty()) Note("Todavía no tienes recepciones. Usa el código de quien entrega para comprobar la lista cuando llegue.")
        incoming.forEach {delivery->OutlinedCard(onClick={open(delivery.id)},modifier=Modifier.fillMaxWidth()) {Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text("${delivery.items.size} aparato(s)",style=MaterialTheme.typography.titleMedium);Text(delivery.status.label);Text(delivery.items.joinToString {it.description})}}}
    }
}
