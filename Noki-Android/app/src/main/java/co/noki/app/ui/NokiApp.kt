package co.noki.app.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import co.noki.app.data.NokiModel
import java.util.UUID
import co.noki.app.R
import co.noki.app.BuildConfig
import co.noki.app.domain.*

private data class Tab(val route: String, val label: String, val icon: ImageVector)
private val tabs = listOf(Tab("home", "Inicio", Icons.Outlined.Home), Tab("devices", "Mis aparatos", Icons.Outlined.Devices),
    Tab("points", "Puntos", Icons.Outlined.Place), Tab("learn", "Aprende", Icons.AutoMirrored.Outlined.MenuBook))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NokiApp(startWithAccount: Boolean = false) {
    val model: NokiModel = viewModel()
    val nav = rememberNavController()
    val reduceMotion = LocalAccessibility.current.reduceMotion
    val accessibilityManager=androidx.compose.ui.platform.LocalAccessibilityManager.current
    var menuOpen by remember { mutableStateOf(false) }
    var entryAccessOpened by rememberSaveable { mutableStateOf(false) }
    var pendingOrientation by rememberSaveable {mutableStateOf<String?>(null)}
    LaunchedEffect(Unit) { if (startWithAccount && !entryAccessOpened && model.session == null) { entryAccessOpened=true;nav.navigate("account") { launchSingleTop=true } } }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val current by nav.currentBackStackEntryAsState()
    val route = current?.destination?.route?.substringBefore('?')?.substringBefore('/') ?: "home"
    val contextualPoints = route == "points" && listOf("category","device","delivery","choose").any {!current?.arguments?.getString(it).isNullOrBlank()}
    val isRoot = tabs.any { it.route == route } && !contextualPoints
    fun goTab(target: String) { nav.navigate(target) { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } }
    fun clearPrivateNavigation() { nav.navigate("home") { popUpTo(nav.graph.id) { inclusive = true }; launchSingleTop = true } }
    LaunchedEffect(model.needsUsername,route) {
        if(model.needsUsername && route!="account" && route!="save") nav.navigate("account") {launchSingleTop=true}
    }
    LaunchedEffect(model.accessMessage) {
        if(model.accessMessage!=null) {
            if(route=="save") current?.arguments?.let {args->pendingOrientation="save/${args.getString("category")}/${args.getString("safety")}/${args.getString("function")}/${args.getString("id")}?delivery=${args.getString("delivery") ?: "0"}"}
            kotlinx.coroutines.delay(accessibilityManager?.calculateRecommendedTimeoutMillis(2400,containsText=true) ?: 2400)
            model.dismissAccessMessage();clearPrivateNavigation()
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
    val wide = maxWidth >= 700.dp
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                expandedHeight = with(LocalDensity.current) { MaterialTheme.typography.titleMedium.lineHeight.toDp() * (if(route=="home" && model.username.isNotBlank() && (fontScale>=1.3f || model.username.length>12)) 3 else 2) + 16.dp },
                title = {
                    if (route == "home") HomeBrand(if(model.session!=null && model.legalAccepted)model.username else "")
                    else Text(when (route) {
                        "orientation" -> "Orientación"
                        "prepare" -> "Preparar entrega"
                        "devices" -> "Mis aparatos"
                        "device" -> "Mi aparato"
                        "save" -> "Guardar orientación"
                        "account" -> "Mi cuenta"
                        "points" -> "Puntos de recolección"
                        "learn" -> "Aprende"
                        "about" -> "Acerca de Noki"
                        "accessibility" -> "Accesibilidad"
                        "legal" -> "Términos y privacidad"
                        "receiver" -> "Recibir aparatos"
                        "receiver-application" -> "Solicitud de receptor"
                        "receptions", "reception" -> "Recepciones"
                        "create-delivery", "delivery" -> "Mi entrega"
                        else -> "Noki"
                    }, style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = { if (!isRoot) IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver")
                } },
                actions = {
                    if (route == "home") AccountHeader(model) {nav.navigate("account") {launchSingleTop=true}}
                    Box {
                        IconButton(onClick = { menuOpen=true }) { Icon(Icons.Outlined.MoreVert,"Abrir menú") }
                        DropdownMenu(menuOpen,{menuOpen=false}) {
                            DropdownMenuItem(text={Text("Accesibilidad")},leadingIcon={Icon(Icons.Outlined.AccessibilityNew,null)},onClick={menuOpen=false;nav.navigate("accessibility") { launchSingleTop=true }})
                            DropdownMenuItem(text={Text("Perfil y ajustes")},leadingIcon={Icon(Icons.Outlined.AccountCircle,null)},onClick={menuOpen=false;nav.navigate("account") { launchSingleTop=true }})
                            DropdownMenuItem(text={Text("Términos y privacidad")},onClick={menuOpen=false;nav.navigate("legal") { launchSingleTop=true }})
                            DropdownMenuItem(text={Text("Acerca de Noki")},onClick={menuOpen=false;nav.navigate("about") { launchSingleTop=true }})
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (isRoot && !wide && (LocalDensity.current.fontScale >= 1.3f || maxWidth < 360.dp)) Surface(color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    tabs.chunked(2).forEach { pair -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { tab -> FilledTonalButton(onClick = { goTab(tab.route) }, modifier = Modifier.weight(1f).heightIn(min = 60.dp).semantics {selected=route==tab.route;role=Role.Tab},
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = if(route == tab.route) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)) {
                            Text(tab.label, modifier=Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, minLines=2, maxLines=2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        } }
                    } }
                }
            }
            else if (isRoot && !wide) BottomDestinations(tabs.map {Triple(it.route,it.label,it.icon)},route,::goTab)
        }
    ) { padding ->
        Row(Modifier.padding(padding)) {
        if (wide) NavigationRail(containerColor = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxHeight()) {
            tabs.forEach { tab -> NavigationRailItem(selected = route == tab.route, onClick = { goTab(tab.route) }, icon = { Icon(tab.icon,null) }, label = { Text(tab.label) }) }
        }
        Column(Modifier.weight(1f)) {
        if (BuildConfig.PRACTICE_ENABLED && model.demo) Surface(color = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer) { Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Espacio de prueba · solo en este teléfono", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
            TextButton(onClick = { model.signOut { clearPrivateNavigation() } }, enabled = !model.busy) { Text("Salir") }
        } }
        if (model.busy) LinearProgressIndicator(Modifier.fillMaxWidth().testTag("OperationInProgress"))
        model.notice?.let {message -> StatusNotice(message,model.noticeSuccess) {model.notice=null}}
        NavHost(nav, startDestination = "home", modifier = Modifier.weight(1f),
            enterTransition = { if(reduceMotion) EnterTransition.None else fadeIn(tween(380)) + slideInHorizontally(tween(380)) { it / 16 } }, exitTransition = { if(reduceMotion) ExitTransition.None else fadeOut(tween(260)) },
            popEnterTransition = { if(reduceMotion) EnterTransition.None else fadeIn(tween(340)) }, popExitTransition = { if(reduceMotion) ExitTransition.None else fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 16 } }
        ) {
            composable("home") { HomeScreen(model, { nav.navigate("orientation") { launchSingleTop = true } }, { goTab("points") }, { goTab("learn") }, { goTab("devices") },{nav.navigate("receiver") {launchSingleTop=true}},pendingOrientation?.let {saved->{nav.navigate(saved){launchSingleTop=true};pendingOrientation=null}},prepare={model.activeDelivery?.let {nav.navigate("delivery/${it.id}")} ?: nav.navigate("prepare")}) }
            composable("prepare") {OrientationScreen(
                points={category->nav.navigate("points?category=${category.code}")},
                save={orientation,id->nav.navigate("save/${orientation.category!!.name}/${orientation.safety!!.name}/${(orientation.function ?: FunctionState.UNKNOWN).name}/$id?delivery=1")},
                delivery=true
            )}
            composable("orientation") { OrientationScreen(
                points = { category -> nav.navigate("points?category=${category.code}") },
                save = { orientation, id -> nav.navigate("save/${orientation.category!!.name}/${orientation.safety!!.name}/${(orientation.function ?: FunctionState.UNKNOWN).name}/$id") }
            ) }
            composable("learn") { LearnScreen { nav.navigate("orientation") { launchSingleTop = true } } }
            composable("points?category={category}&device={device}&delivery={delivery}&point={point}&choose={choose}&items={items}", arguments = listOf("category","device","delivery","point","choose","items").map {name->navArgument(name){defaultValue=""}}) { entry ->
                val args=entry.arguments!!
                val deviceId=args.getString("device").orEmpty()
                DirectoryScreen(model,args.getString("category").orEmpty(),deviceId,onSelected={
                    if(nav.previousBackStackEntry?.destination?.route=="device/{id}") nav.previousBackStackEntry?.savedStateHandle?.set("resume_preparation",true)
                    nav.popBackStack()
                },
                    initialPointId=args.getString("point").orEmpty(),deliveryId=args.getString("delivery").orEmpty(),choosingReceiver=args.getString("choose")=="1",
                    continueDelivery={point->
                        if(!model.canSave) nav.navigate("account") {launchSingleTop=true}
                        else if(args.getString("choose")=="1" && nav.previousBackStackEntry?.destination?.route?.startsWith("create-delivery/")==true) {
                            nav.previousBackStackEntry?.savedStateHandle?.set("map_receiver",point?.receiverRequestId.orEmpty());nav.popBackStack()
                        } else if(deviceId.isNotBlank()) nav.navigate("create-delivery/$deviceId?receiver=${point?.receiverRequestId.orEmpty()}") {launchSingleTop=true}
                        else nav.navigate("prepare") {launchSingleTop=true}
                    },openDelivery={id->
                        if(nav.previousBackStackEntry?.destination?.route=="delivery/{id}" && nav.previousBackStackEntry?.arguments?.getString("id")==id) nav.popBackStack()
                        else nav.navigate("delivery/$id") {
                            if(nav.previousBackStackEntry?.destination?.route?.startsWith("create-delivery/")==true)
                                popUpTo("create-delivery/{id}?receiver={receiver}") {inclusive=true}
                            else popUpTo(entry.destination.id) {inclusive=true}
                            launchSingleTop=true
                        }
                    },deliveryDeviceIds=if(args.getString("choose")=="1") args.getString("items").orEmpty().split(',').filter {it.isNotBlank()}.distinct() else null)
            }
            composable("devices") { DevicesScreen(model, {model.activeDelivery?.let {nav.navigate("delivery/${it.id}")} ?: nav.navigate("prepare")}, { nav.navigate("device/$it") }, { nav.navigate("account") },{nav.navigate("delivery/$it"){launchSingleTop=true}}) }
            composable("account") { AccountScreen(model, { goTab("devices") }, { nav.navigate("about") }, { clearPrivateNavigation() }, { nav.navigate("accessibility") },{nav.navigate("receiver") {launchSingleTop=true}}) }
            composable("save/{category}/{safety}/{function}/{id}?delivery={delivery}",arguments=listOf(navArgument("delivery"){defaultValue="0"})) { entry ->
                val args = entry.arguments!!
                SaveDeviceScreen(model, args.getString("id")!!, Orientation(DeviceCategory.valueOf(args.getString("category")!!), SafetyState.valueOf(args.getString("safety")!!), FunctionState.valueOf(args.getString("function")!!)),
                    onSaved = { id -> nav.navigate("device/$id") { popUpTo(if(args.getString("delivery")=="1")"prepare" else "orientation") { inclusive = true } } }, about = { nav.navigate("about") },prepareDelivery=args.getString("delivery")=="1")
            }
            composable("device/{id}") { entry ->
                val resumePreparation by entry.savedStateHandle.getStateFlow("resume_preparation",false).collectAsState()
                DeviceDetailScreen(model, entry.arguments!!.getString("id")!!,
                    points = { device -> nav.navigate("points?category=${device.category.code}&device=${device.id}&point=${device.pointId.orEmpty()}") }, deleted = { goTab("devices") },createDelivery={nav.navigate("create-delivery/$it") {launchSingleTop=true}},openDelivery={nav.navigate("delivery/$it") {launchSingleTop=true}},
                    resumePreparation=resumePreparation,preparationResumed={entry.savedStateHandle["resume_preparation"]=false})
            }
            composable("create-delivery/{id}?receiver={receiver}",arguments=listOf(navArgument("receiver"){defaultValue=""})) {entry->
                val deviceId=entry.arguments!!.getString("id")!!
                val savedPoint=model.devices.firstOrNull {it.id==deviceId}?.pointId
                val initialReceiver=entry.arguments!!.getString("receiver").orEmpty().ifBlank {model.points.firstOrNull {it.id==savedPoint}?.receiverRequestId.orEmpty()}
                val mapReceiver by entry.savedStateHandle.getStateFlow("map_receiver",initialReceiver).collectAsState()
                CreateDeliveryScreen(model,deviceId,open={nav.navigate("delivery/$it") {popUpTo("create-delivery/{id}?receiver={receiver}"){inclusive=true};launchSingleTop=true}},
                    points={point,ids->nav.navigate("points?device=$deviceId&choose=1&point=$point&items=${android.net.Uri.encode(ids.joinToString(","))}")},pending={goTab("devices")},
                    requestedReceiver=mapReceiver,receiverConsumed={entry.savedStateHandle["map_receiver"]=""})
            }
            composable("delivery/{id}") {entry->
                val id=entry.arguments!!.getString("id")!!
                DeliveryScreen(model,id,{nav.navigate("device/$it") {launchSingleTop=true}},points={
                    val receiverId=model.outgoing.firstOrNull {it.id==id}?.receiverId
                    val point=model.receivers.firstOrNull {it.id==receiverId}?.requestId.orEmpty()
                    nav.navigate("points?delivery=$id&point=$point")
                })
            }
            composable("receptions") {ReceptionScreen(model,{nav.navigate("reception/$it") {launchSingleTop=true}},{goTab("devices")})}
            composable("reception/{id}") {entry->DeliveryScreen(model,entry.arguments!!.getString("id")!!,{},receiverMode=true)}
            composable("about") { AboutScreen() }
            composable("accessibility") { AccessibilityScreen() }
            composable("legal") { LegalScreen(model) }
            composable("receiver") { ReceiverScreen(model,{nav.navigate("legal") {launchSingleTop=true}},{nav.navigate("receiver-application") {launchSingleTop=true}},{nav.navigate("receptions") {launchSingleTop=true}}) }
            composable("receiver-application") {ReceiverApplicationScreen(model) {nav.popBackStack()}}
        }
        }
        }
    }
    }
    if(model.passwordSetupRequested) PasswordSetupDialog(model,model::finishPasswordSetup,model::finishPasswordSetup)
    if(model.accessMessage!=null) androidx.compose.ui.window.Dialog(onDismissRequest={},properties=androidx.compose.ui.window.DialogProperties(dismissOnBackPress=false,dismissOnClickOutside=false)) {
        Surface(shape=RoundedCornerShape(24.dp),color=MaterialTheme.colorScheme.primaryContainer) {Column(Modifier.padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(14.dp)) {
            Icon(Icons.Outlined.CheckCircle,null,Modifier.size(48.dp),tint=MaterialTheme.colorScheme.primary)
            Text(model.accessMessage.orEmpty(),style=MaterialTheme.typography.titleLarge,modifier=Modifier.semantics {liveRegion=LiveRegionMode.Polite})
            Text("Te llevamos al inicio.",style=MaterialTheme.typography.bodyMedium)
        }}
    }
}

@Composable
fun Page(resetKey:Any?=Unit,content: @Composable ColumnScope.() -> Unit) {
    val scroll=remember(resetKey) {androidx.compose.foundation.ScrollState(0)}
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 640.dp).fillMaxWidth().imePadding()
            .verticalScroll(scroll, overscrollEffect = null).testTag("PageScroll")
            .padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}

@Composable
fun PrimaryButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    val interactions=remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val reduce=LocalAccessibility.current.reduceMotion
    val scale by animateFloatAsState(if(pressed && !reduce) .985f else 1f,tween(if(reduce) 0 else 100),label="buttonPress")
    Button(onClick = onClick, enabled = enabled, interactionSource=interactions, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).scale(scale), shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)) { Text(label,textAlign=androidx.compose.ui.text.style.TextAlign.Center) }
}

@Composable
fun Note(text: String, warning: Boolean = false) {
    Surface(color = if (warning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp)) {
        Text(text, Modifier.fillMaxWidth().padding(18.dp), style = MaterialTheme.typography.bodyMedium,
            color = if (warning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
internal fun HomeScreen(model: NokiModel, orient: () -> Unit, points: () -> Unit, learn: () -> Unit, devices: () -> Unit, receiver:()->Unit,resume:(()->Unit)?=null,prepare:()->Unit=orient) {
    Page {
        Text("TODAVÍA SIRVE, RECICLA", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text("¿Qué puedes hacer con ese aparato?", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
        Text("Noki te ayuda a decidir qué hacer con tus aparatos eléctricos y electrónicos: seguir usándolos, repararlos o llevarlos a recolección. Aquí puedes encontrar lugares en Medellín y preparar tu entrega paso a paso.", style = MaterialTheme.typography.bodyMedium)
        PrimaryButton(if(model.activeDelivery==null) "Preparar entrega" else "Continuar mi entrega",!model.busy,prepare)
        resume?.let {ActionCard("Continuar con mi aparato","Tus respuestas siguen guardadas. Retoma la preparación cuando quieras.",Icons.Outlined.PlayArrow,it)}
        Surface(color = NokiGreen, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Devices, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    Text("Empieza por su estado", style = MaterialTheme.typography.titleLarge, color = Color.White)
                }
                Text("Responde hasta 3 preguntas y recibe pasos concretos. No necesitas abrir ni encender el aparato.", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Button(onClick = orient, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = NokiGreen),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(14.dp)) { Text("Orientar mi dispositivo") }
            }
        }
        if (model.devices.isNotEmpty()) ActionCard("Continúa con tus aparatos", "${model.devices.count { !DeliveryRules.inHistory(it,model.outgoing) }} pendientes · ${model.devices.count { DeliveryRules.inHistory(it,model.outgoing) }} en el historial", Icons.Outlined.Checklist, devices)
        Text("A tu alcance", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        if (BuildConfig.DIRECTORY_READY) ActionCard("Encontrar un punto", "Consulta la dirección, la fuente y las condiciones antes de ir.", Icons.Outlined.Place, points)
        ActionCard("Entender tus opciones", "Seguir usando, reparar, reutilizar o llevar a recolección.", Icons.AutoMirrored.Outlined.MenuBook, learn)
        ReceiverInvitation(receiver)
        Text("Tu decisión, a tu ritmo. Noki ofrece orientación; no realiza un diagnóstico técnico.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ActionCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, shape = RoundedCornerShape(20.dp), colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun Choice(text: String, selected: Boolean, action: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth().selectable(selected = selected, role = Role.RadioButton, onClick = action)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp).heightIn(min = 40.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = null)
            Spacer(Modifier.width(12.dp))
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun OrientationScreen(points: (DeviceCategory) -> Unit, save: (Orientation, String) -> Unit,delivery:Boolean=false) {
    val reduceMotion=LocalAccessibility.current.reduceMotion
    var step by rememberSaveable { mutableIntStateOf(0) }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var safety by rememberSaveable { mutableStateOf<String?>(null) }
    var function by rememberSaveable { mutableStateOf<String?>(null) }
    var draftId by rememberSaveable { mutableStateOf(UUID.randomUUID().toString()) }
    val orientation = Orientation(DeviceCategory.entries.find { it.name == category }, SafetyState.entries.find { it.name == safety }, FunctionState.entries.find { it.name == function })
    val result = orientation.recommendation()
    BackHandler(enabled = step > 0) {
        step = if (step == 3) { if (safety == SafetyState.NORMAL.name) 2 else 1 } else step - 1
    }
    AnimatedContent(targetState = step, transitionSpec = { if(reduceMotion) EnterTransition.None.togetherWith(ExitTransition.None) else (fadeIn(tween(340)) + slideInHorizontally(tween(380)) { it / 12 }).togetherWith(fadeOut(tween(100))) }, label = "orientationStep") { visibleStep ->
    key(visibleStep) { Page {
        if (visibleStep < 3) {
            Text("PASO ${visibleStep + 1} DE HASTA 3", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
            LinearProgressIndicator(progress = { (visibleStep + 1) / 3f }, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primaryContainer)
            Text(when (visibleStep) { 0 -> "¿Qué aparato tienes?"; 1 -> "¿Has notado alguna señal de atención?"; else -> "¿Sabes si funciona?" },
                style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
            Text(when (visibleStep) {
                0 -> "Elige la opción que mejor lo describa."
                1 -> "Por ejemplo: hinchazón, fuga, calentamiento inusual o daño importante. No lo enciendas ni lo manipules para comprobarlo."
                else -> "Responde solo con lo que ya sabes. No necesitas probarlo."
            })
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (visibleStep) {
                    0 -> DeviceCategory.entries.forEach { item -> Choice(item.label, category == item.name) { if(step==visibleStep) category = item.name } }
                    1 -> SafetyState.entries.forEach { item -> Choice(item.label, safety == item.name) { if(step==visibleStep) {safety = item.name; function = null} } }
                    else -> FunctionState.entries.forEach { item -> Choice(item.label, function == item.name) { if(step==visibleStep) function = item.name } }
                }
            }
            PrimaryButton(if (visibleStep == 2 || visibleStep == 1 && safety != null && safety != SafetyState.NORMAL.name) "Ver orientación" else "Continuar",
                enabled = step==visibleStep && when (visibleStep) { 0 -> category != null; 1 -> safety != null; else -> function != null }) {
                if(step==visibleStep) step = if (visibleStep == 1 && safety != SafetyState.NORMAL.name) 3 else (visibleStep + 1).coerceAtMost(3)
            }
            if (visibleStep > 0) OutlinedButton(onClick = { if(step==visibleStep) step=visibleStep-1 }, enabled=step==visibleStep, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Pregunta anterior") }
        } else if (result != null) {
            Icon(if (result == Recommendation.SAFETY_REVIEW) Icons.Outlined.HealthAndSafety else Icons.Outlined.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(42.dp))
            Text(result.title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading(); liveRegion = LiveRegionMode.Polite })
            Text(result.description)
            Note(result.next, warning = result == Recommendation.SAFETY_REVIEW)
            Text("Tus siguientes pasos", style = MaterialTheme.typography.titleLarge)
            recommendedSteps(orientation.category!!, result).forEachIndexed { index, text ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp)) { Text("${index + 1}", Modifier.padding(10.dp), color = MaterialTheme.colorScheme.primary) }
                    Text(text, Modifier.weight(1f))
                }
            }
            Text("Esta orientación se basa en tus respuestas. No es un diagnóstico técnico.", style = MaterialTheme.typography.bodySmall)
            PrimaryButton(if(delivery) "Continuar con mi entrega" else "Guardar y preparar mi aparato") { save(orientation, draftId) }
            OutlinedButton(onClick = { points(orientation.category!!) }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(if (result == Recommendation.SAFETY_REVIEW) "Consultar puntos antes de trasladarlo" else "Ver puntos de recolección") }
            OutlinedButton(onClick = { step = 0 }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Cambiar respuestas") }
            TextButton(onClick = { category = null; safety = null; function = null; draftId = UUID.randomUUID().toString(); step = 0 }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Orientar otro aparato") }
        }
    } } }
}

data class LearnTopic(val title: String, val text: String, val source: String, val url: String)
private val topics = listOf(
    LearnTopic("¿Qué son los RAEE?", "Son los residuos de aparatos eléctricos y electrónicos. Un aparato que ya no usas no siempre es un residuo: puede seguir siendo útil para ti o para otra persona.", "Ministerio de Ambiente de Colombia", "https://www.minambiente.gov.co/asuntos-ambientales-sectorial-y-urbana/residuos-de-aparato-electricos-y-electronicos-raee/"),
    LearnTopic("Antes de descartarlo", "Seguir utilizando un equipo, revisar si puede repararse y darle otro uso son decisiones diferentes. La orientación te ayuda a considerar opciones; la revisión técnica corresponde a una persona capacitada.", "Ministerio de Ambiente de Colombia", "https://www.minambiente.gov.co/asuntos-ambientales-sectorial-y-urbana/residuos-de-aparato-electricos-y-electronicos-raee/"),
    LearnTopic("Cuida tu información", "Antes de entregar un teléfono o computador, revisa si puedes respaldar tus archivos, cerrar tus cuentas y borrar tus datos de forma segura. Sigue las instrucciones del fabricante. No fuerces el encendido de un aparato dañado.", "Ayuda de Android", "https://support.google.com/android/answer/6088915?hl=es"),
    LearnTopic("Si notas daño o calentamiento", "No cargues, abras ni enciendas un aparato con hinchazón, fuga, calentamiento inusual o daño importante para completar una guía. Consulta con una persona capacitada o con el lugar de recepción antes de manipularlo o trasladarlo.", "Agencia de Protección Ambiental de Estados Unidos", "https://www.epa.gov/recycle/used-lithium-ion-batteries"),
    LearnTopic("Antes de ir a un punto", "Comprueba qué aparatos recibe el lugar, sus horarios y las condiciones. Una ficha en Noki es información de consulta: no representa una reserva, un convenio ni una aceptación garantizada.", "Red Verde", "https://www.redverde.co/")
)

@Composable
private fun LearnScreen(orient: () -> Unit) {
    val context = LocalContext.current
    var open by rememberSaveable { mutableIntStateOf(-1) }
    var linkError by remember { mutableStateOf(false) }
    Page {
        Text("Entiende tus opciones", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
        Text("Información breve para decidir con más claridad.")
        topics.forEachIndexed { index, topic ->
            OutlinedCard(onClick = { open = if (open == index) -1 else index }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(topic.title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        Icon(if (open == index) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, if (open == index) "Contraer" else "Leer")
                    }
                    if (open == index) {
                        Text(topic.text)
                        Text("Fuente: ${topic.source}", style = MaterialTheme.typography.bodySmall)
                        Text("Revisión: 3 de octubre de 2026", style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = {
                            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(topic.url))) }
                            catch (_: ActivityNotFoundException) { linkError = true }
                        }) { Text("Consultar fuente") }
                    }
                }
            }
        }
        if (linkError) {
            Note("No encontramos una aplicación para abrir el enlace.")
        }
        PrimaryButton("Orientar mi dispositivo", onClick = orient)
    }
}

@Composable
private fun AboutScreen() {
    Page {
        Image(painterResource(if(LocalDarkAppearance.current) R.drawable.noki_wordmark_dark else R.drawable.noki_wordmark), "Noki", Modifier.width(180.dp).height(72.dp))
        Text("Todavía sirve, recicla", style = MaterialTheme.typography.headlineMedium)
        Text("Noki te acompaña a comprender tus opciones y tomar decisiones sobre los aparatos que ya no utilizas.")
        Text("Versión ${co.noki.app.BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
        Note("La orientación no es un diagnóstico. Los lugares del directorio son referencias de consulta; confirma sus condiciones antes de ir.")
        Text("Contacto", style = MaterialTheme.typography.titleMedium)
        Text("nokiappcorporation@gmail.com")
    }
}

