package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.maplibre.android.geometry.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun ReceiverLocationDialog(address:String,initial:LatLng?,dismiss:()->Unit,selected:(LatLng)->Unit) {
    var anchor by remember {mutableStateOf(initial)}
    var candidate by remember {mutableStateOf(initial)}
    var search by remember {mutableStateOf(false)}
    var confirmed by remember {mutableStateOf(false)}
    Dialog(onDismissRequest=dismiss,properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) {
        DialogSystemBars()
        Scaffold(Modifier.fillMaxSize().safeDrawingPadding(),topBar={TopAppBar(title={Text("Ubicación del lugar",style=MaterialTheme.typography.titleMedium)},navigationIcon={IconButton(onClick=dismiss){Icon(Icons.AutoMirrored.Outlined.ArrowBack,"Volver")}})},
            bottomBar={Surface {Column(Modifier.fillMaxWidth().heightIn(max=340.dp).verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Mueve el mapa hasta dejar la marca en la entrada del lugar. Al aprobarse, esta ubicación será pública.")
                OutlinedButton(onClick={search=true},modifier=Modifier.fillMaxWidth()) {Text("Buscar dirección del lugar")}
                AcceptanceCheck("El marcador coincide con la entrada de mi lugar",confirmed){confirmed=it}
                if(candidate==null) Text("Falta elegir la entrada del lugar: busca la dirección o mueve el mapa.",style=MaterialTheme.typography.bodyMedium)
                else if(!confirmed) Text("Falta marcar la casilla «El marcador coincide con la entrada de mi lugar» para confirmar.",style=MaterialTheme.typography.bodyMedium)
                PrimaryButton("Confirmar ubicación",candidate!=null && confirmed) {candidate?.let(selected)}
                Text("© OpenStreetMap contributors",style=MaterialTheme.typography.labelSmall)
            }}}) {padding ->
            key(anchor) {NativeMap(emptyList(),null,null,null,selected={},manualOrigin={anchor=it;confirmed=false},
                choosingOrigin=true,centerChanged={if(candidate!=it) confirmed=false;candidate=it},initialCenter=anchor,
                modifier=Modifier.fillMaxSize().padding(padding))}
        }
    }
    if(search) AddressSearchDialog(dismiss={search=false},selected={anchor=it.coordinate;candidate=it.coordinate;confirmed=false;search=false},initialAddress=address,forReceiver=true)
}
