package co.noki.app.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import co.noki.app.data.*
import co.noki.app.domain.DeviceCategory
import co.noki.app.domain.SafetyState
import kotlinx.coroutines.*
import org.maplibre.android.geometry.LatLng
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(model: NokiModel, initialCategory: String = "", deviceId: String = "", onSelected: () -> Unit = {},
    initialPointId:String="", deliveryId:String="", choosingReceiver:Boolean=false,
    continueDelivery:(CollectionPoint?)->Unit={}, openDelivery:(String)->Unit={}, deliveryDeviceIds:List<String>?=null) {
    DirectoryAutoRefresh(model)
    DeliveryAutoRefresh(model,!model.demo && model.canSave)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var search by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable(initialCategory) { mutableStateOf(initialCategory) }
    var onlyPublished by rememberSaveable { mutableStateOf(false) }
    var showMap by rememberSaveable { mutableStateOf(true) }
    var selectedId by rememberSaveable { mutableStateOf(initialPointId.takeIf {it.isNotBlank()}) }
    var sheet by rememberSaveable { mutableStateOf(initialPointId.isNotBlank()) }
    var origin by remember { mutableStateOf<LatLng?>(null) }
    var originType by rememberSaveable { mutableStateOf("") }
    var manual by rememberSaveable { mutableStateOf(false) }
    var expandedMap by rememberSaveable { mutableStateOf(false) }
    var mapCenter by remember { mutableStateOf<LatLng?>(null) }
    var path by remember { mutableStateOf<RoutePath?>(null) }
    var routeTarget by remember { mutableStateOf<CollectionPoint?>(null) }
    var confirmRoute by remember { mutableStateOf<CollectionPoint?>(null) }
    var locating by remember { mutableStateOf(false) }
    var locationNotice by remember { mutableStateOf(false) }
    var addressSearch by rememberSaveable { mutableStateOf(false) }
    var returnToPoint by rememberSaveable { mutableStateOf(false) }
    var routing by remember { mutableStateOf(false) }
    var routeJob by remember { mutableStateOf<Job?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val device = model.devices.firstOrNull { it.id == (deliveryDeviceIds?.firstOrNull() ?: deviceId) }
    var trackedDeliveryId by rememberSaveable {mutableStateOf(deliveryId.ifBlank {model.activeDelivery?.id.orEmpty()})}
    LaunchedEffect(model.activeDelivery?.id) {model.activeDelivery?.let {trackedDeliveryId=it.id}}
    val activeDelivery=model.outgoing.firstOrNull {it.id==trackedDeliveryId} ?: model.activeDelivery
    fun selectPoint(id:String) {
        routeJob?.cancel();routing=false;path=null;routeTarget=null;error=null
        selectedId=id;sheet=true
    }
    fun setOrigin(value: LatLng, type: String) {
        routeJob?.cancel(); routing = false; origin = value; originType = type; path = null; routeTarget = null; manual = false; mapCenter=value;error=null
        if(returnToPoint) { sheet=true; expandedMap=false; returnToPoint=false }
    }
    fun locate() {
        if (locating) return
        locating = true; error = null
        scope.launch {
            try { setOrigin(MapServices.currentLocation(context), "Ubicación del teléfono · puede ser aproximada") }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { error = e.message ?: "No pudimos obtener tu ubicación.";if(returnToPoint){sheet=true;returnToPoint=false} }
            finally { locating = false }
        }
    }
    val permissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) locate() else {error = "No autorizaste la ubicación. El mapa y la lista siguen funcionando; puedes elegir el punto en el mapa o escribir una dirección.";if(returnToPoint){sheet=true;returnToPoint=false}}
    }
    fun open(url: String, dial: Boolean = false) {
        try { context.startActivity(Intent(if(dial) Intent.ACTION_DIAL else Intent.ACTION_VIEW, Uri.parse(url))) }
        catch (_: Exception) { error = "No encontramos una aplicación para abrir esta opción. Puedes usar la dirección o el teléfono de la ficha." }
    }
    val filtered = visibleDeliveryPlaces(model.points,search,category,onlyPublished)
    val selectedPoint = model.points.firstOrNull { it.id == selectedId }
    val ownRequestId=model.receiverApplication?.optString("id")
    val preparedIds=deliveryDeviceIds ?: listOfNotNull(device?.id)
    fun showDelivery(id:String) {routeJob?.cancel();sheet=false;expandedMap=false;openDelivery(id)}
    val nextStep:@Composable ()->Unit = {
        DeliveryMapContinuation(selectedPoint,activeDelivery,device,choosingReceiver,model.receivers,model.canSave,
            openDelivery=::showDelivery,
            continueDelivery={routeJob?.cancel();sheet=false;expandedMap=false;continueDelivery(it)},
            chooseAnother={routeJob?.cancel();routing=false;path=null;routeTarget=null;selectedId=null;sheet=false;expandedMap=false;search="";category="";onlyPublished=false;showMap=false;model.loadPoints();if(model.canSave)model.reloadReception()},
            backToPreparation={
                if(device!=null && selectedPoint!=null && !model.busy) model.save(device.copy(pointId=selectedPoint.id),false) {sheet=false;expandedMap=false;onSelected()}
                else if(device!=null && !model.busy) onSelected()
            },ownRequestId=ownRequestId,generateHere=if(deliveryDeviceIds==null && preparedIds.isEmpty()) null else {receiver ->
                DestinationDeliveryAction(model,preparedIds,receiver,::showDelivery)
            })
    }
    Page {
        Text("Encuentra dónde llevarlo", style = MaterialTheme.typography.headlineMedium)
        if(!sheet && !expandedMap) nextStep()
        Note("Aquí encontrarás receptores habilitados en Noki y lugares de referencia pública. Solo los receptores habilitados pueden confirmar tus entregas con código. La lista se actualiza automáticamente mientras la consultas.")
        if (device != null) Note("Eligiendo un lugar para: ${device.description}")
        if (device?.safety != null && device.safety != SafetyState.NORMAL) Note("Consulta por teléfono antes de trasladar este aparato. No todos los lugares reciben equipos o baterías dañadas.", true)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = showMap, onClick = { showMap = true }, label = { Text("Mapa") }, leadingIcon = { Icon(Icons.Outlined.Map,null) })
            FilterChip(selected = !showMap, onClick = { showMap = false }, label = { Text("Lista") }, leadingIcon = { Icon(Icons.Outlined.List,null) })
        }
        if (showMap) {
            if(!expandedMap) NativeMap(filtered, selectedId, origin, path, selected = ::selectPoint, manualOrigin = {}, centerChanged={ mapCenter=it },interactive=false)
            else Spacer(Modifier.height(330.dp))
            PrimaryButton("Ampliar y mover el mapa") { expandedMap=true }
            Text("Mueve y acerca el mapa en la vista ampliada. Aquí puedes deslizar la página sin cambiarlo.",style=MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { open("https://www.openstreetmap.org/copyright") }) { Text("© OpenStreetMap contributors") }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { locationNotice=true }, enabled = !locating) { Text(if (locating) "Ubicándote…" else "Partir desde mi ubicación") }
                OutlinedButton(onClick = { manual=true; expandedMap=true }) { Text("Elegir punto de partida") }
                OutlinedButton(onClick = { addressSearch=true }) { Text("Escribir dirección") }
            }
            origin?.let { Text(originType, style = MaterialTheme.typography.bodyMedium) }
            if (origin != null) TextButton(onClick = { routeJob?.cancel(); routing=false; origin=null; path=null; routeTarget=null }) { Text("Quitar punto de partida y ruta") }
            if (routing) { LinearProgressIndicator(Modifier.fillMaxWidth()); TextButton(onClick = { routeJob?.cancel(); routing=false }) { Text("Cancelar cálculo") } }
            path?.let { r -> Note("Ruta en carro a ${routeTarget?.name}\n${String.format(Locale.forLanguageTag("es-CO"), "%.1f", r.meters / 1000)} km · aprox. ${kotlin.math.ceil(r.seconds / 60).toInt()} min, sin tráfico en tiempo real.\nPunto de partida: $originType. Es una orientación, no navegación paso a paso.") }
        }
        error?.let { Note(it, true) }
        model.directoryNotice?.let { Note(it,true) }
        DeliveryPlacePicker(search,{search=it},filtered) { selectPoint(it.id) }
        EnumMenu("Aparato que quieres llevar", DeviceCategory.entries.firstOrNull { it.code == category }?.label ?: "Todos los aparatos",
            listOf("" to "Todos los aparatos") + DeviceCategory.entries.map { it.code to it.label }) { category = it; onlyPublished = false }
        if (category.isNotBlank()) FilterChip(selected = onlyPublished, onClick = { onlyPublished = !onlyPublished }, label = { Text("Solo categorías publicadas") })
        if (filtered.isEmpty()) {
            Text(if(model.points.isEmpty() && model.directoryNotice == null) "No hay lugares publicados por ahora. Puedes actualizar para volver a consultar." else "No encontramos lugares con esos filtros.")
            TextButton(onClick = { search=""; category=""; onlyPublished=false; model.loadPoints();if(model.canSave)model.reloadReception() }) { Text("Ver todos los lugares y actualizar") }
            if(device!=null || choosingReceiver) OutlinedButton(onClick=onSelected) {Text("Volver a mi preparación")}
        }
        Text("${filtered.size} lugares para consultar", style = MaterialTheme.typography.titleLarge)
        PublishedDeliveryPlaces(filtered,model.receivers,ownRequestId) {selectPoint(it.id)}
        TextButton(onClick = { model.loadPoints();if(model.canSave)model.reloadReception() }) { Text("Actualizar directorio") }
        Text("Los marcadores ubican el establecimiento, no el contenedor exacto. No hay reservas ni recepción garantizada.", style = MaterialTheme.typography.bodyMedium)
    }
    if(expandedMap) Dialog(onDismissRequest={expandedMap=false;manual=false;if(returnToPoint){sheet=true;returnToPoint=false}}, properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) {
        DialogSystemBars()
        Scaffold(modifier=Modifier.fillMaxSize().safeDrawingPadding(),topBar={TopAppBar(expandedHeight=with(androidx.compose.ui.platform.LocalDensity.current){MaterialTheme.typography.titleMedium.lineHeight.toDp()*2+16.dp},title={Text(if(manual) "Punto de partida" else "Mapa de puntos",style=MaterialTheme.typography.titleMedium)},navigationIcon={IconButton(onClick={expandedMap=false;manual=false;if(returnToPoint){sheet=true;returnToPoint=false}}){Icon(Icons.AutoMirrored.Outlined.ArrowBack,"Volver a puntos")}})},
            bottomBar={Surface { Column(Modifier.fillMaxWidth().heightIn(max=420.dp).verticalScroll(rememberScrollState()).padding(horizontal=16.dp,vertical=8.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                if(manual) {
                    Text("Mueve el mapa hasta dejar la marca donde quieres salir.",style=MaterialTheme.typography.bodyMedium)
                    PrimaryButton("Confirmar punto de partida",mapCenter!=null) { mapCenter?.let {setOrigin(it,"Punto de partida elegido en el mapa");expandedMap=false} }
                    TextButton(onClick={manual=false}) {Text("Cancelar selección")}
                } else {
                    nextStep()
                    if(routing) { LinearProgressIndicator(Modifier.fillMaxWidth()); Text("Calculando tu ruta…") }
                    path?.let { r -> Text("Ruta en carro a ${routeTarget?.name}",style=MaterialTheme.typography.titleSmall); Text("${String.format(Locale.forLanguageTag("es-CO"),"%.1f",r.meters/1000)} km · aprox. ${kotlin.math.ceil(r.seconds/60).toInt()} min · sin tráfico en tiempo real",style=MaterialTheme.typography.bodyMedium) }
                    OutlinedButton(onClick={manual=true},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {Text("Elegir punto de partida",textAlign=androidx.compose.ui.text.style.TextAlign.Center)}
                    OutlinedButton(onClick={locationNotice=true},enabled=!locating,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {Text(if(locating) "Ubicándote…" else "Partir desde mi ubicación",textAlign=androidx.compose.ui.text.style.TextAlign.Center)}
                    OutlinedButton(onClick={addressSearch=true},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {Text("Escribir dirección")}
                }
                origin?.let {Text(originType,style=MaterialTheme.typography.labelSmall,maxLines=2)}
                error?.let { Text(it,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.error) }
                Text("© OpenStreetMap contributors",style=MaterialTheme.typography.labelSmall)
            } }}) { padding ->
            NativeMap(filtered,selectedId,origin,path,selected={selectPoint(it);expandedMap=false;manual=false},manualOrigin={if(manual){setOrigin(it,"Punto de partida elegido en el mapa");expandedMap=false}},choosingOrigin=manual,centerChanged={mapCenter=it},modifier=Modifier.fillMaxSize().padding(padding),initialCenter=mapCenter)
        }
    }
    if (sheet && selectedPoint != null) ModalBottomSheet(onDismissRequest = { sheet = false }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(selectedPoint.name, style = MaterialTheme.typography.headlineMedium)
            DeliveryTrustNote(reviewed=selectedPoint.receiverRequestId!=null,referenceOnly=selectedPoint.receiverRequestId==null)
            error?.let {Note(it,true)}
            Text(selectedPoint.address)
            nextStep()
            if (origin != null) PrimaryButton("Calcular ruta en carro", !routing) { sheet=false; confirmRoute=selectedPoint }
            else {
                Text("Elige desde dónde sales para calcular la ruta a este lugar.")
                OutlinedButton(onClick={sheet=false;returnToPoint=true;locationNotice=true},modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) {Text("Partir desde mi ubicación")}
                OutlinedButton(onClick={sheet=false;returnToPoint=true;addressSearch=true},modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) {Text("Escribir dirección de partida")}
                TextButton(onClick={sheet=false;returnToPoint=true;manual=true;expandedMap=true}) {Text("Elegir punto de partida en mapa")}
            }
            Text(selectedPoint.locationNote)
            Text("Qué recibe", style = MaterialTheme.typography.titleMedium)
            Text(selectedPoint.details)
            Text("Horario publicado", style = MaterialTheme.typography.titleMedium)
            Text(selectedPoint.hours ?: "La fuente no publica un horario. Confírmalo antes de ir.")
            Text("Fuente: ${selectedPoint.source}\nRevisado: ${selectedPoint.verifiedAt}", style = MaterialTheme.typography.bodyMedium)
            val age = runCatching { ChronoUnit.DAYS.between(LocalDate.parse(selectedPoint.verifiedAt), LocalDate.now()) }.getOrDefault(999)
            if (age > 90) Note("Han pasado más de 90 días desde esta revisión. Confirma si el servicio sigue disponible.", true)
            selectedPoint.phone?.let { phone -> OutlinedButton(onClick = { open("tel:$phone", true) }, modifier = Modifier.fillMaxWidth().heightIn(min=52.dp)) { Text("Llamar para confirmar") } }
            selectedPoint.whatsapp?.let {WhatsappContact(it)}
            if(selectedPoint.receiverRequestId==null) TextButton(onClick = { open(selectedPoint.sourceUrl) }) { Text("Consultar fuente original") }
            OutlinedButton(onClick = { open("https://www.google.com/maps/dir/?api=1&destination=${selectedPoint.latitude},${selectedPoint.longitude}") }, modifier = Modifier.fillMaxWidth().heightIn(min=52.dp)) { Text("Abrir en otra app de mapas") }
            TextButton(onClick = { sheet=false }) { Text("Cerrar ficha") }
            Spacer(Modifier.height(16.dp))
        }
    }
    if(addressSearch) AddressSearchDialog(dismiss={addressSearch=false;if(returnToPoint){sheet=true;returnToPoint=false}},selected={match -> val expand = !returnToPoint;setOrigin(match.coordinate,"Dirección: ${match.label}");addressSearch=false;expandedMap=expand})
    if(locationNotice) AlertDialog(onDismissRequest={locationNotice=false;if(returnToPoint){sheet=true;returnToPoint=false}},title={Text("Usar la ubicación del teléfono")},
        text={Text("Noki consultará tu ubicación solo para elegir el punto de partida. Puede ser aproximada y no se seguirá en segundo plano. Puedes negarte y escribir una dirección o elegir el punto en el mapa.")},
        confirmButton={TextButton(onClick={locationNotice=false;permissions.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION))}){Text("Continuar con ubicación")}},
        dismissButton={TextButton(onClick={locationNotice=false;if(returnToPoint){sheet=true;returnToPoint=false}}){Text("Ahora no")}})
    confirmRoute?.let { destination -> AlertDialog(onDismissRequest = { confirmRoute=null }, title = { Text("Calcular una ruta") },
        text = { Text("Se enviarán el origen y el destino al servicio público OSRM. Noki no guarda tu ubicación en tu cuenta. El tiempo estimado es para carro y no incluye tráfico en tiempo real.") },
        confirmButton = { TextButton(onClick = {
            confirmRoute=null
            val start=origin ?: return@TextButton
            routeJob?.cancel(); routing=true; error=null; path=null; routeTarget=null; expandedMap=true; manual=false
            routeJob=scope.launch {
                try { val result=MapServices.drivingRoute(start,LatLng(destination.latitude,destination.longitude)); path=result; routeTarget=destination; showMap=true }
                catch(e: CancellationException) { throw e }
                catch(e: Exception) { error=if(e is NokiApiException) e.message else "No pudimos calcular la ruta. Revisa la conexión o abre la dirección en otra app de mapas." }
                finally { routing=false }
            }
        }) { Text("Calcular") } }, dismissButton = { TextButton(onClick = { confirmRoute=null }) { Text("Cancelar") } }) }
}
