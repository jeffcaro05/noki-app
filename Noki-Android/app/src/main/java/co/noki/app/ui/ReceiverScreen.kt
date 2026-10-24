package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import co.noki.app.data.NokiModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable internal fun ReceiverScreen(model:NokiModel,legal:()->Unit,apply:()->Unit,inbox:()->Unit) {
    DeliveryAutoRefresh(model,!model.demo && model.canSave)
    var locate by remember {mutableStateOf(false)}
    var contact by remember {mutableStateOf(false)}
    var understood by rememberSaveable {mutableStateOf(false)}
    var agreed by rememberSaveable {mutableStateOf(false)}
    Page {
        Text("Recibir aparatos con Noki",style=MaterialTheme.typography.headlineMedium,modifier=Modifier.semantics {heading()})
        Text("Ayuda a otras personas a entregar sus aparatos en un lugar con condiciones claras. Podrás seguir usando tu misma cuenta para gestionar tus propios aparatos.")
        val request=model.receiverApplication
        when(request?.optString("status")) {
            "approved" -> {Icon(Icons.Outlined.Verified,"Perfil revisado",tint=MaterialTheme.colorScheme.primary);Text("Perfil revisado",style=MaterialTheme.typography.titleLarge);Text(request.optString("place_name"));PrimaryButton("Cambiar a receptor",onClick=inbox)
                if(request.isNull("latitude")) {Note("Tu perfil está habilitado. Falta confirmar su ubicación para que aparezca en el mapa.");OutlinedButton(onClick={locate=true},enabled=!model.busy,modifier=Modifier.fillMaxWidth()){Text("Ubicar mi lugar en el mapa")}}
            }
            "pending" -> Note("Tu solicitud está en revisión. Consulta aquí la respuesta antes de recibir aparatos.")
            "needs_changes" -> Note("Necesitamos que revises tu solicitud: ${request.optString("review_note")}",true)
            "suspended" -> Note("La recepción está suspendida. ${request.optString("review_note")}",true)
        }
        if(model.canSave && !model.demo) OutlinedButton(onClick=model::reloadReception,enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Consultar estado de mi solicitud")}
        if(request!=null && request.optString("status")!="withdrawn") OutlinedButton(onClick={contact=true},enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Actualizar WhatsApp del lugar")}
        Text("Cómo empezar",style=MaterialTheme.typography.titleLarge,modifier=Modifier.semantics {heading()})
        ReceiverStep("1. Confirma tu cuenta","Entra con tu correo verificado. Debes ser mayor de edad y estar autorizado para recibir aparatos en el lugar que registres.")
        ReceiverStep("2. Presenta tu lugar","Indica la dirección, los horarios y qué aparatos recibes. Añade fotos de la fachada, del área de recepción y del espacio donde los guardarás.")
        ReceiverStep("3. Espera la revisión","Revisaremos los datos para reducir el riesgo de fraudes, estafas y robos. Si falta información, podrás corregirla desde tu solicitud. Te mostraremos aquí la respuesta.")
        ReceiverStep("4. Empieza a recibir","Cuando se apruebe tu perfil verás la marca «Perfil revisado» y podrás cambiar a receptor. Escanea o escribe el código, revisa los aparatos y confirma únicamente lo que recibiste.")
        Note("Noki podrá coordinar visitas ocasionales para comprobar que el lugar existe y funciona como se informó. No necesitas una visita inicial para enviar la solicitud.")
        Text("Una cuenta, dos formas de usarla",style=MaterialTheme.typography.titleLarge,modifier=Modifier.semantics {heading()})
        Text("Puedes cambiar entre «Mis aparatos» y «Recepciones» sin cerrar sesión. La entrega termina cuando validas el código y confirmas que recibiste los aparatos. Leer un código permite revisar la entrega; no la finaliza por sí solo.")
        Text("Protección de tus datos",style=MaterialTheme.typography.titleLarge,modifier=Modifier.semantics {heading()})
        Text("Las fotos del lugar se envían de forma privada para revisar tu solicitud. Evita incluir rostros, documentos, placas o datos personales. No envíes fotos de tu cédula por correo o chat.")
        Text("Sin cobros en esta etapa. Cualquier comisión futura deberá informarse y aceptarse antes de aplicarse.",style=MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick=legal,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("Consultar términos y privacidad")}
        if(request?.optString("status") !in listOf("approved","pending","suspended")) {
            AcceptanceCheck("He leído los requisitos y entiendo cómo recibir aparatos",understood) {understood=it}
            AcceptanceCheck("Acepto las condiciones del perfil de receptor y el uso de las fotos para revisar mi solicitud",agreed) {agreed=it}
            if(!understood || !agreed) Text("Para continuar falta: " + listOfNotNull(
                if(!understood) "confirmar que leíste los requisitos" else null,
                if(!agreed) "aceptar las condiciones y el uso de las fotos" else null
            ).joinToString(" y ") + ".",style=MaterialTheme.typography.bodyMedium)
            PrimaryButton("Crear mi perfil de receptor",understood && agreed && !model.busy,onClick=apply)
            if(!model.canSave || model.demo) Text("Necesitarás entrar con una cuenta verificada para enviar la solicitud.",style=MaterialTheme.typography.bodyMedium)
        }
    }
    if(locate) ReceiverLocationDialog(model.receiverApplication?.optString("address").orEmpty(),null,dismiss={locate=false},selected={model.completeReceiverLocation(it.latitude,it.longitude){locate=false}})
    if(contact) ReceiverContactDialog(model){contact=false}
}

@Composable private fun ReceiverStep(title:String,description:String) {
    Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {Text(title,style=MaterialTheme.typography.titleMedium);Text(description,style=MaterialTheme.typography.bodyMedium)}
}

/** Manual pager: no timer, tracking, autoplay or changing content while it is read. */
@Composable internal fun ReceiverInvitation(open:()->Unit) {
    val pager=rememberPagerState(pageCount={2})
    val scope=rememberCoroutineScope()
    val reduce=LocalAccessibility.current.reduceMotion
    Surface(shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.secondaryContainer) {
        Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            HorizontalPager(state=pager) { page ->
                Column(Modifier.fillMaxWidth().padding(end=4.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text(if(page==0) "Aprende a recibir aparatos" else "Revisa y confirma la recepción",style=MaterialTheme.typography.titleLarge)
                    Text(if(page==0) "Conoce los requisitos y presenta tu lugar de recepción." else "Comprueba el código y la lista. Confirma solo cuando hayas recibido los aparatos.",style=MaterialTheme.typography.bodyMedium)
                }
            }
            Text("Información ${pager.currentPage+1} de 2",style=MaterialTheme.typography.labelSmall)
            OutlinedButton(onClick=open,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {Text("Quiero recibir aparatos")}
            TextButton(onClick={scope.launch {val next=1-pager.currentPage;if(reduce) pager.scrollToPage(next) else pager.animateScrollToPage(next)}}) {Text(if(pager.currentPage==0) "Ver información de seguridad" else "Ver requisitos de recepción")}
        }
    }
}
