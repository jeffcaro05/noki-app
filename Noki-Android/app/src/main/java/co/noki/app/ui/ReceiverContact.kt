package co.noki.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import co.noki.app.domain.*

@Composable internal fun WhatsappContact(number:String,enabled:Boolean=true) {
    val context=LocalContext.current
    var unavailable by remember(number) {mutableStateOf(false)}
    Text("Solo WhatsApp: +$number")
    OutlinedButton(onClick={
        val url=ReceiverContact.whatsappUrl(number)
        unavailable=url==null || runCatching {context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))}.isFailure
    },enabled=enabled && ReceiverContact.valid(number),modifier=Modifier.fillMaxWidth()) {Text("Contactar por WhatsApp")}
    if(unavailable) Note("No pudimos abrir WhatsApp. Guarda el número +$number y escribe desde tu aplicación de WhatsApp.",true)
}

@Composable internal fun DeliveryContactPanel(receiver:ReceiverPlace,devices:List<DeviceRecord>,confirmed:Boolean,changed:(Boolean)->Unit,practice:Boolean=false) {
    val special=DeliveryRules.needsContact(devices,receiver)
    val other=devices.filter {it.category.code !in receiver.categories}
    if(other.isNotEmpty()) Note("Este lugar no anuncia la recepción de: ${other.map {it.category.label}.distinct().joinToString()}. Pregunta si puede recibir estos aparatos antes de continuar.")
    if(devices.any {it.safety!=SafetyState.NORMAL}) Note("Describe al receptor las señales o daños del aparato y pregunta cómo proceder antes de trasladarlo. No lo enciendas, cargues ni desmontes. Si hay humo, fuego o peligro inmediato, aléjate y llama al 123; no lo transportes.",true)
    if(!practice) receiver.whatsapp?.takeIf(ReceiverContact::valid)?.let {WhatsappContact(it)}
    if(special) {
        Text("Antes de generar el código",style=MaterialTheme.typography.titleMedium)
        if(receiver.whatsapp?.let(ReceiverContact::valid)!=true) Note("El receptor todavía no publicó un WhatsApp de contacto. Elige otro receptor o vuelve cuando actualice su contacto. Tu preparación queda guardada.",true)
        else {
            Text("Escribe al receptor, describe cada aparato y su estado, y espera su respuesta. Si confirma que puede recibirlos, marca la casilla.")
            AcceptanceCheck("Confirmé con el receptor que puede recibir todos estos aparatos en su estado actual",confirmed,changed)
            Text("Esta es tu declaración. Sin un acuerdo real podrías desplazarte y que el lugar rechace la entrega. La confirmación no certifica que sea seguro transportar un aparato dañado.",style=MaterialTheme.typography.bodyMedium)
            if(!confirmed) Note("Falta marcar la confirmación después de hablar con el receptor para habilitar el código y el QR.")
        }
    } else if(devices.isNotEmpty()) Text("Confirma horario y condiciones antes de salir. Si el lugar no acepta tu aparato o cambia lo acordado, elige otro receptor.")
}

@Composable internal fun ReceiverContactDialog(model:co.noki.app.data.NokiModel,dismiss:()->Unit) {
    var number by remember {mutableStateOf(model.receiverApplication?.optString("public_whatsapp")?.takeUnless {it=="null"}.orEmpty())}
    var publish by remember {mutableStateOf(false)}
    AlertDialog(onDismissRequest={if(!model.busy)dismiss()},title={Text("WhatsApp del lugar")},text={Column(Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(number,{number=it.filter(Char::isDigit).take(15)},label={Text("Número con código de país")},supportingText={Text("Colombia: 57 seguido de los 10 dígitos. Ejemplo: 573001234567.")},enabled=!model.busy)
        Text("El número aparecerá en la ficha pública del lugar, incluso sin iniciar sesión.")
        AcceptanceCheck("Autorizo mostrar este WhatsApp públicamente para consultas sobre entregas",publish){publish=it}
    }},confirmButton={TextButton(onClick={model.updateReceiverContact(number,dismiss)},enabled=!model.busy && publish && ReceiverContact.valid(number)){Text("Guardar contacto")}},dismissButton={TextButton(onClick=dismiss,enabled=!model.busy){Text("Cancelar")}})
}
