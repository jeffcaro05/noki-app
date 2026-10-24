package co.noki.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.noki.app.data.*

@Composable
fun LegalScreen(model:NokiModel?=null) {
    val context=LocalContext.current
    var privacy by rememberSaveable {mutableStateOf(false)}
    var error by remember {mutableStateOf(false)}
    var revoke by remember {mutableStateOf(false)}
    Page {
        Text(if(privacy) "Política de privacidad" else "Términos y condiciones",style=MaterialTheme.typography.headlineMedium)
        Text("Versión ${LegalDocuments.VERSION}",style=MaterialTheme.typography.bodyMedium)
        Text("Incluye complemento de receptores del ${LegalDocuments.RECEIVER_NOTICE_DATE}.",style=MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick={privacy=!privacy},modifier=Modifier.fillMaxWidth()) {Text(if(privacy) "Leer términos y condiciones" else "Leer política de privacidad")}
        (if(privacy) LegalDocuments.privacy else LegalDocuments.terms).forEach { (title,body) ->
            Text(title,style=MaterialTheme.typography.titleLarge)
            Text(body,style=MaterialTheme.typography.bodyMedium)
        }
        Text("Directorio y contacto público",style=MaterialTheme.typography.titleLarge)
        Text(LegalDocuments.receiverNotice,style=MaterialTheme.typography.bodyMedium)
        if(privacy) listOf("Privacidad de Supabase" to "https://supabase.com/privacy","Privacidad de OpenStreetMap" to "https://osmfoundation.org/wiki/Privacy_Policy","Servicio de rutas OSRM" to "https://project-osrm.org/","Información de privacidad de Android" to "https://www.android.com/safety/privacy/").forEach { (label,url) ->
            TextButton(onClick={try {context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))} catch(_:Exception){error=true}}) {Text(label)}
        }
        Text(LegalDocuments.CONTACT)
        OutlinedButton(onClick={try {context.startActivity(Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:${LegalDocuments.CONTACT}")))} catch(_:Exception){error=true}},modifier=Modifier.fillMaxWidth()) {Text("Escribir a Noki")}
        if(error) Note("No encontramos una aplicación para abrir esta opción. Puedes escribirnos al correo indicado.")
        if(model?.canSave == true) OutlinedButton(onClick={revoke=true},enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Retirar permiso en este teléfono")}
    }
    if(revoke && model!=null) AlertDialog(onDismissRequest={revoke=false},title={Text("Volver al acceso público")},text={Text("Cerraremos tu sesión y detendremos las funciones personales en este teléfono. Tus registros no se borrarán. Para solicitar la supresión de tu cuenta o revocar el tratamiento en el servidor, escribe a ${LegalDocuments.CONTACT}.")},confirmButton={TextButton(onClick={revoke=false;model.revokeConsent()},enabled=!model.busy){Text("Retirar y cerrar sesión")}},dismissButton={TextButton(onClick={revoke=false}){Text("Cancelar")}})
}

@Composable
fun ConsentDialog(accept:()->Unit,decline:()->Unit) {
    var terms by rememberSaveable {mutableStateOf(false)}
    var privacy by rememberSaveable {mutableStateOf(false)}
    var reading by rememberSaveable {mutableStateOf(false)}
    Dialog(onDismissRequest=decline,properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) {
        DialogSystemBars()
        Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column {
                TextButton(onClick={if(reading) reading=false else decline()}) {Text(if(reading) "Volver a la aceptación" else "Volver sin aceptar")}
                Box(Modifier.weight(1f)) {
                    if(reading) LegalScreen() else Page {
                        Text("Antes de guardar tu progreso",style=MaterialTheme.typography.headlineMedium)
                        Text("Los responsables de Noki son ${LegalDocuments.RESPONSIBLE}. Contacto: ${LegalDocuments.CONTACT}.")
                        Text("Necesitamos tu autorización para gestionar la cuenta y guardar tus aparatos, decisiones, preparación y resultados. No incluye publicidad ni activa la ubicación.")
                        OutlinedButton(onClick={reading=true},modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("Leer términos y privacidad")}
                        ConsentCheck("Acepto los términos y condiciones",terms) {terms=it}
                        ConsentCheck("Autorizo el tratamiento de mis datos según la política de privacidad",privacy) {privacy=it}
                        Text("Versión ${LegalDocuments.VERSION}. Guardaremos una constancia cifrada en este dispositivo.",style=MaterialTheme.typography.bodyMedium)
                        PrimaryButton("Aceptar y continuar",terms && privacy,accept)
                        OutlinedButton(onClick=decline,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("No aceptar")}
                        Text("Si no aceptas, puedes seguir consultando orientación, lugares y Aprende, sin guardar datos personales. Puedes decidir más adelante.",style=MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
@Composable private fun ConsentCheck(label:String,checked:Boolean,change:(Boolean)->Unit) {
    Row(Modifier.fillMaxWidth().toggleable(checked,role=Role.Checkbox,onValueChange=change).padding(vertical=8.dp),verticalAlignment=Alignment.CenterVertically) {
        Checkbox(checked,onCheckedChange=null)
        Spacer(Modifier.width(12.dp))
        Text(label,Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium)
    }
}
