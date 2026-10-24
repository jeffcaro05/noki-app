package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable internal fun PasswordSetupDialog(model:co.noki.app.data.NokiModel,done:()->Unit,dismiss:()->Unit) {
    // Passwords are ephemeral: never rememberSaveable, persisted or logged.
    var password by remember {mutableStateOf("")}
    var confirmation by remember {mutableStateOf("")}
    var visible by remember {mutableStateOf(false)}
    Dialog(onDismissRequest={if(!model.busy)dismiss()},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        DialogSystemBars()
        Surface(Modifier.fillMaxSize().safeDrawingPadding()) {Page {
            Text("Tu contraseña de Noki",style=MaterialTheme.typography.headlineMedium)
            Text("Guárdala para entrar con tu correo sin pedir un código cada vez. No uses la contraseña de tu correo.")
            model.notice?.let {StatusNotice(it,model.noticeSuccess)}
            OutlinedTextField(password,{password=it},label={Text("Nueva contraseña")},supportingText={Text(if(password.toByteArray(Charsets.UTF_8).size>72) "La contraseña es demasiado larga. Acórtala para continuar." else "Al menos 12 caracteres. Combina letras, números y símbolos.")},isError=password.toByteArray(Charsets.UTF_8).size>72,
                singleLine=true,enabled=!model.busy,modifier=Modifier.fillMaxWidth(),
                keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),
                visualTransformation=if(visible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon={TextButton(onClick={visible=!visible}) {Text(if(visible) "Ocultar" else "Ver")}})
            OutlinedTextField(confirmation,{confirmation=it},label={Text("Repite la contraseña")},
                singleLine=true,enabled=!model.busy,modifier=Modifier.fillMaxWidth(),
                isError=confirmation.isNotEmpty() && confirmation!=password,
                supportingText={if(confirmation.isNotEmpty() && confirmation!=password)Text("Las contraseñas no coinciden.")},
                keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),
                visualTransformation=if(visible) VisualTransformation.None else PasswordVisualTransformation())
            PrimaryButton("Guardar contraseña",!model.busy && password.length>=12 && password.toByteArray(Charsets.UTF_8).size<=72 && password==confirmation) {
                model.updatePassword(password,confirmation) {password="";confirmation="";done()}
            }
            TextButton(onClick={password="";confirmation="";dismiss()},enabled=!model.busy) {Text("Ahora no")}
        }}
    }
}
