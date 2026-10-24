package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import co.noki.app.data.NokiModel
import co.noki.app.domain.Validation
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.noki.app.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(model: NokiModel, done: () -> Unit, about: () -> Unit = {}, signedOut: () -> Unit = {}, accessibility: (() -> Unit)? = null, receiver:(()->Unit)?=null,inlineNotice:Boolean=false) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var confirmPractice by remember { mutableStateOf(false) }
    var registration by rememberSaveable { mutableStateOf(false) }
    var setupPassword by rememberSaveable {mutableStateOf(true)}
    var editPassword by remember {mutableStateOf(false)}
    var codeSent by rememberSaveable { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }
    var resendAfter by rememberSaveable { mutableLongStateOf(0L) }
    var cooldown by remember { mutableIntStateOf(0) }
    var editName by remember { mutableStateOf(false) }
    var editUsername by remember {mutableStateOf(false)}
    var displayName by remember(model.profileName) { mutableStateOf(model.profileName) }
    var localAccessibility by remember { mutableStateOf(false) }
    var legalReading by remember {mutableStateOf(false)}
    var pendingConsent by remember {mutableStateOf<(() -> Unit)?>(null)}
    var emailConsent by remember {mutableStateOf(false)}
    var rejected by remember {mutableStateOf(false)}
    LaunchedEffect(Unit) {model.refreshEmailCapability()}
    LaunchedEffect(resendAfter) { while(android.os.SystemClock.elapsedRealtime() < resendAfter) {
        cooldown=((resendAfter-android.os.SystemClock.elapsedRealtime()+999)/1000).toInt(); kotlinx.coroutines.delay(1000)
    }; cooldown=0 }
    Page(resetKey=registration to codeSent) {
        Text(if (model.canSave) "Perfil y ajustes" else if(registration) "Entrar con tu correo" else "Entra para guardar tu progreso", style = MaterialTheme.typography.headlineMedium)
        if(inlineNotice) model.notice?.let {StatusNotice(it,model.noticeSuccess)}
        if(rejected) Note("No aceptaste. Puedes seguir consultando orientación, lugares y Aprende; no se guardarán datos personales.")
        if(model.session!=null && !model.legalAccepted) {
            Text("Antes de volver a consultar tus registros, revisa los términos y la privacidad. No hemos borrado tus aparatos.")
            PrimaryButton("Revisar y decidir",!model.busy) {pendingConsent={model.acceptCurrentConsent(done)}}
            OutlinedButton(onClick={model.signOut(signedOut)},modifier=Modifier.fillMaxWidth()) {Text("Cerrar sesión sin aceptar")}
        } else if(model.session!=null && !model.profileLoaded) {
            Text("Tu cuenta sigue aquí",style=MaterialTheme.typography.titleLarge)
            Text("Estamos recuperando tu perfil. Si no termina de cargar, comprueba la conexión y vuelve a intentarlo. No necesitas crear otra cuenta.")
            PrimaryButton("Actualizar",!model.busy) {model.reload()}
            TextButton(onClick={model.signOut(signedOut)},enabled=!model.busy) {Text("Cerrar sesión")}
        } else if(model.session!=null && model.legalAccepted && model.username.isBlank()) {
            Text("Completa tu registro",style=MaterialTheme.typography.titleLarge)
            Text("Elige cómo quieres que te demos la bienvenida. Tu nombre de usuario es obligatorio y podrás cambiarlo una vez cada 30 días.")
            UsernameForm(model,true)
            TextButton(onClick={model.signOut(signedOut)},enabled=!model.busy) {Text("Cerrar sesión y continuar después")}
        } else if (!model.canSave) {
            Text("Guarda tus aparatos, prepara una entrega y vuelve a consultar lo que hiciste. Tu orientación se conserva mientras entras.")
            if (registration) {
                Text("Puedes usar Gmail, iCloud, Outlook u otro correo al que tengas acceso.",style=MaterialTheme.typography.bodyMedium)
                Text(if(setupPassword) "Primero verificaremos tu correo con un código. Después podrás guardar una contraseña para entrar las próximas veces." else "Recibirás un código de un solo uso para entrar a tu misma cuenta.",style=MaterialTheme.typography.bodyMedium)
                if(!model.emailAccessReady) {Note("El envío de códigos aún no está habilitado. No se creará ninguna cuenta ni se enviará un mensaje desde esta pantalla.",true);TextButton(onClick=model::refreshEmailCapability){Text("Comprobar disponibilidad")}}
                OutlinedTextField(email,{email=it;codeSent=false;code="";emailConsent=false},label={Text("Correo electrónico")},modifier=Modifier.fillMaxWidth(),singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),enabled=!model.busy && !codeSent)
                if(codeSent) {
                    Note("Busca el último código enviado a $email. Revisa también la carpeta de correo no deseado. No compartas ese código.")
                    OutlinedTextField(code,{code=it.filter(Char::isDigit).take(10)},label={Text("Código de verificación")},modifier=Modifier.fillMaxWidth(),singleLine=true,enabled=!model.busy,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.NumberPassword))
                    PrimaryButton("Verificar y continuar",!model.busy && code.length in 6..10) { if(emailConsent) model.verifyEmailCode(email,code,true,setupPassword=setupPassword) {code=""} else pendingConsent={emailConsent=true;model.verifyEmailCode(email,code,true,setupPassword=setupPassword){code=""}} }
                    TextButton(onClick={codeSent=false;code=""},enabled=!model.busy) {Text("Cambiar correo")}
                }
                PrimaryButton(if(cooldown>0) "Puedes reenviar en ${cooldown}s" else if(codeSent) "Reenviar código" else "Enviar código",model.emailAccessReady && !model.busy && cooldown==0) {
                    if(!Validation.email(email)) model.notice="Escribe un correo válido."
                    else if(emailConsent) model.sendEmailCode(email,true) {codeSent=true;resendAfter=android.os.SystemClock.elapsedRealtime()+60000}
                    else pendingConsent={emailConsent=true;model.sendEmailCode(email,true) {codeSent=true;resendAfter=android.os.SystemClock.elapsedRealtime()+60000}}
                }
                TextButton(onClick={registration=false;code=""}) {Text("Entrar con contraseña")}
            } else {
            OutlinedTextField(email, { email = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), enabled = !model.busy)
            OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !model.busy,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) "Ocultar" else "Ver") } })
            PrimaryButton("Iniciar sesión", !model.busy) {
                if (!Validation.email(email) || password.isEmpty()) model.notice = "Escribe un correo válido y tu contraseña."
                else pendingConsent={model.signIn(email, password,true) { password = "" }}
            }
            OutlinedButton(onClick={registration=true;setupPassword=true;codeSent=false;code=""},modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("Crear cuenta con correo")}
            TextButton(onClick={registration=true;setupPassword=true;codeSent=false;code=""},modifier=Modifier.fillMaxWidth()) {Text("Olvidé mi contraseña")}
            TextButton(onClick={registration=true;setupPassword=false;codeSent=false;code=""},modifier=Modifier.fillMaxWidth()) {Text("Entrar con un código")}
            }
            Text("Tu contraseña es la de Noki, no la de tu correo electrónico. Si aún no definiste una, puedes hacerlo entrando con un código.",style=MaterialTheme.typography.bodyMedium)
            if(BuildConfig.PRACTICE_ENABLED) OutlinedButton(onClick = { confirmPractice = true }, enabled = !model.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text("Probar sin cuenta") }
        } else {
            Text(if (model.demo) "Espacio de prueba" else model.session?.email.orEmpty())
            if(!model.demo) {
                ProfilePhotoEditor(model)
                OutlinedButton(onClick={editPassword=true},enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Crear o cambiar contraseña")}
                Text("@${model.username}",style=MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick={editUsername=true},enabled=!model.busy && model.canChangeUsername,modifier=Modifier.fillMaxWidth()) {Text("Cambiar nombre de usuario")}
                if(!model.canChangeUsername) Text("Podrás cambiarlo a partir del ${usernameDate(model.usernameAvailableAt)}.",style=MaterialTheme.typography.bodyMedium)
                Text(model.profileName.ifBlank {"Añade tu nombre"},style=MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick={editName=true},enabled=!model.busy,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("Editar mi nombre")}
                OutlinedButton(onClick=model::reloadProfile,enabled=!model.busy,modifier=Modifier.fillMaxWidth()) {Text("Actualizar perfil")}
            }
            Note(if (model.demo) "Los aparatos de prueba se guardan cifrados solo en este teléfono. No se envían a tu cuenta, no crean entregas ni afectan registros de otras personas." else "Tus aparatos se guardan en tu cuenta. Noki no guarda tu contraseña en el teléfono.")
            PrimaryButton("Ver mis aparatos", onClick = done)
            OutlinedButton(onClick = { model.signOut { password = ""; signedOut() } }, enabled = !model.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(if (model.demo) "Salir del espacio de prueba" else "Cerrar sesión") }
        }
        if(model.receiverApproved) Text("✓ Perfil de receptor revisado",color=MaterialTheme.colorScheme.primary)
        receiver?.let {open -> OutlinedButton(onClick=open,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text(if(model.receiverApproved) "Ver mi perfil de receptor" else "Quiero recibir aparatos")}}
        OutlinedButton(onClick={if(accessibility != null) accessibility() else localAccessibility=true},modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("Ajustes de accesibilidad")}
        OutlinedButton(onClick={legalReading=true},modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("Términos y privacidad")}
        Text("Versión ${co.noki.app.BuildConfig.VERSION_NAME}",style=MaterialTheme.typography.bodyMedium)
        TextButton(onClick = about) { Text("Acerca de Noki") }
    }
    pendingConsent?.let { action -> ConsentDialog(accept={pendingConsent=null;rejected=false;action()},decline={pendingConsent=null;password="";code="";emailConsent=false;rejected=true}) }
    if(legalReading) Dialog(onDismissRequest={legalReading=false},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        DialogSystemBars()
        Surface(Modifier.fillMaxSize().safeDrawingPadding()) {Column {TextButton(onClick={legalReading=false}){Text("Volver a mi cuenta")};Box(Modifier.weight(1f)){LegalScreen(model)}}}
    }
    if(localAccessibility) Dialog(onDismissRequest={localAccessibility=false},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        DialogSystemBars()
        Surface(Modifier.fillMaxSize().safeDrawingPadding()) {Column {
            TextButton(onClick={localAccessibility=false}) {Text("Volver al acceso")}
            Box(Modifier.weight(1f)) {AccessibilityScreen()}
        } }
    }
    if(editPassword) PasswordSetupDialog(model,{editPassword=false},{editPassword=false})
    if(editName) AlertDialog(onDismissRequest={if(!model.busy) editName=false},title={Text("¿Cómo te llamas?")},text={
        OutlinedTextField(displayName,{displayName=it.take(80)},label={Text("Nombre")},supportingText={Text("Entre 2 y 80 caracteres.")},enabled=!model.busy)
    },confirmButton={TextButton(onClick={model.updateName(displayName){editName=false}},enabled=!model.busy && displayName.trim().length in 2..80){Text("Guardar nombre")}},dismissButton={TextButton(onClick={editName=false},enabled=!model.busy){Text("Cancelar")}})
    if(editUsername) Dialog(onDismissRequest={if(!model.busy)editUsername=false},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        Surface(Modifier.fillMaxSize().safeDrawingPadding()) {Page {
            Text("Cambiar nombre de usuario",style=MaterialTheme.typography.headlineMedium)
            Text("Después de guardarlo tendrás que esperar 30 días para cambiarlo nuevamente.")
            UsernameForm(model,false){editUsername=false}
            TextButton(onClick={editUsername=false},enabled=!model.busy){Text("Cancelar")}
        }}
    }
    if (BuildConfig.PRACTICE_ENABLED && confirmPractice) AlertDialog(onDismissRequest = { confirmPractice = false }, title = { Text("Un espacio separado para probar") },
        text = { Text("Aquí puedes guardar aparatos de ejemplo, preparar una entrega y registrar un resultado sin afectar tu cuenta. No se simula la recepción por parte de un establecimiento. La información se pierde si desinstalas Noki.") },
        confirmButton = { TextButton(onClick = { confirmPractice = false; pendingConsent={model.enterDemo(true,done)} }) { Text("Entrar a la prueba") } },
        dismissButton = { TextButton(onClick = { confirmPractice = false }) { Text("Cancelar") } })
}

@Composable private fun UsernameForm(model:NokiModel,initial:Boolean,done:()->Unit={}) {
    var value by rememberSaveable {mutableStateOf(model.username)}
    if(!initial) model.notice?.let {StatusNotice(it,model.noticeSuccess)}
    OutlinedTextField(value,{value=it.take(24)},label={Text("Nombre de usuario")},supportingText={Text("De 3 a 24 caracteres: letras sin tildes, números o guion bajo. Sin espacios.")},singleLine=true,enabled=!model.busy,modifier=Modifier.fillMaxWidth(),keyboardOptions=KeyboardOptions(autoCorrectEnabled=false))
    PrimaryButton(if(initial) "Completar registro" else "Guardar nombre de usuario",!model.busy && Regex("^[A-Za-z0-9_]{3,24}$").matches(value) && (initial || value!=model.username)) {model.updateUsername(value,done)}
}
private fun usernameDate(value:String?):String=runCatching {java.time.OffsetDateTime.parse(value).atZoneSameInstant(java.time.ZoneId.of("America/Bogota")).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))}.getOrDefault("momento indicado al actualizar tu perfil")
