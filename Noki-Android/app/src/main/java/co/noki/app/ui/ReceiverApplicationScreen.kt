package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import co.noki.app.data.NokiModel

@Composable internal fun AcceptanceCheck(label: String, value: Boolean, changed: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).toggleable(value = value, role = Role.Checkbox, onValueChange = changed).padding(vertical = 8.dp)) {
        Checkbox(value, null)
        Spacer(Modifier.width(8.dp))
        Text(label, Modifier.weight(1f))
    }
}

@Composable internal fun ReceiverApplicationScreen(model: NokiModel, done: () -> Unit) {
    if (!model.canSave || model.demo) {
        Page {
            Text("Entra para crear tu perfil de receptor", style = MaterialTheme.typography.headlineMedium)
            Text("La solicitud se vincula a tu cuenta y a un correo verificado. El espacio de prueba no envía solicitudes ni fotografías.")
            if (model.demo) PrimaryButton("Salir de la prueba para entrar", !model.busy) { model.signOut {} }
        }
        if (!model.demo) AccessDialog(dismiss = done) { AccountScreen(model, {}, {}, inlineNotice = true) }
        return
    }
    ReceiverApplicationForm(model.session?.email.orEmpty(), model.receiverApplication, model.busy) { body, photos ->
        model.submitReceiver(body, photos, done)
    }
}
