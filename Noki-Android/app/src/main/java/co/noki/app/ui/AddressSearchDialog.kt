package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.noki.app.data.*
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSearchDialog(dismiss:()->Unit,selected:(AddressMatch)->Unit,initialAddress:String="",forReceiver:Boolean=false) {
    val context=LocalContext.current
    val keyboard=LocalSoftwareKeyboardController.current
    val scope=rememberCoroutineScope()
    var address by rememberSaveable {mutableStateOf(initialAddress.take(180))}
    var busy by remember {mutableStateOf(false)}
    var error by remember {mutableStateOf<String?>(null)}
    var results by remember {mutableStateOf<List<AddressMatch>>(emptyList())}
    var searched by remember {mutableStateOf(false)}
    var chosen by remember {mutableStateOf<AddressMatch?>(null)}
    var job by remember {mutableStateOf<Job?>(null)}
    DisposableEffect(Unit) {onDispose {job?.cancel()}}
    Dialog(onDismissRequest=dismiss,properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) {
        DialogSystemBars()
        Scaffold(Modifier.fillMaxSize().safeDrawingPadding(),topBar={TopAppBar(expandedHeight=with(androidx.compose.ui.platform.LocalDensity.current){MaterialTheme.typography.titleMedium.lineHeight.toDp()*2+16.dp},title={Text(if(forReceiver) "Ubicar lugar de recepción" else "Escribir punto de partida",style=MaterialTheme.typography.titleMedium)},navigationIcon={IconButton(onClick=dismiss){Icon(Icons.AutoMirrored.Outlined.ArrowBack,"Volver al mapa")}})}) { padding ->
            Box(Modifier.padding(padding)) { Page {
                Text(if(forReceiver) "¿Dónde recibirás los aparatos?" else "¿Desde dónde vas a salir?",style=MaterialTheme.typography.headlineMedium)
                Text("Incluye la calle o carrera, el número y la ciudad. No necesitas activar la ubicación.")
                OutlinedTextField(address,{address=it.take(180);results=emptyList();chosen=null;searched=false},label={Text("Dirección y ciudad")},placeholder={Text("Carrera 48 # 10-45, Medellín")},modifier=Modifier.fillMaxWidth(),enabled=!busy,maxLines=3)
                Text(if(forReceiver) "Al buscar, esta dirección se enviará al servicio de direcciones de Android. Comprueba el marcador: la ubicación que confirmes se publicará cuando tu lugar esté aprobado." else "Al buscar, esta dirección se enviará al servicio de direcciones de Android. Noki no la guarda en tu cuenta ni conserva un historial de búsqueda.",style=MaterialTheme.typography.bodyMedium)
                PrimaryButton(if(busy) "Buscando dirección…" else "Buscar dirección",!busy && address.trim().length>=6) {
                    keyboard?.hide();busy=true;error=null;chosen=null;results=emptyList();searched=false
                    job=scope.launch {
                        try { results=MapServices.findAddress(context,address);searched=true }
                        catch(e:CancellationException) {throw e}
                        catch(e:Exception) {error=if(e is NokiApiException) e.message else "No pudimos buscar la dirección. Intenta de nuevo o elige el punto en el mapa."}
                        finally {busy=false}
                    }
                }
                if(busy) TextButton(onClick={job?.cancel();busy=false}) {Text("Cancelar búsqueda")}
                error?.let {Note(it,true)}
                if(searched && results.isEmpty()) Note("No encontramos esa dirección. Añade la ciudad, revisa el número o vuelve al mapa para elegir el punto.")
                if(results.isNotEmpty()) Text("Comprueba la coincidencia",style=MaterialTheme.typography.titleLarge)
                results.forEach { match -> Choice(match.label,chosen==match) {chosen=match} }
                if(chosen!=null) {
                    Note("La ubicación encontrada puede ser aproximada. Comprueba el marcador en el mapa antes de calcular la ruta.")
                    PrimaryButton("Usar esta dirección") {chosen?.let(selected)}
                }
                OutlinedButton(onClick=dismiss,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text("Volver sin cambiar el punto")}
            } }
        }
    }
}
