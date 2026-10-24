package co.noki.app.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import co.noki.app.data.NokiModel
import co.noki.app.domain.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DevicesScreen(model: NokiModel, add: () -> Unit, open: (String) -> Unit, account: () -> Unit,openDelivery:(String)->Unit={}) {
    DeliveryAutoRefresh(model,model.activeDelivery!=null)
    var history by rememberSaveable { mutableStateOf(false) }
    var search by rememberSaveable { mutableStateOf("") }
    Page {
        Text("Tus pendientes y tu historial", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
        PrimaryButton(if(model.activeDelivery==null) "Preparar entrega" else "Continuar mi entrega", !model.busy, add)
        if (!model.canSave) {
            Text("Guarda tu orientación, prepara tu aparato y lleva un registro de lo que decidiste hacer.")
            PrimaryButton("Entrar o crear cuenta", onClick = account)
            Note("Consultar puntos y orientarte no requiere una cuenta.")
        } else {
            model.activeDelivery?.let {active->
                Note("Tienes una entrega pendiente con ${active.items.size} aparato(s). Continúa aquí; no necesitas empezar de nuevo.")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(selected = !history, onClick = { history = false }, label = { Text("Pendientes") })
                FilterChip(selected = history, onClick = { history = true }, label = { Text("Historial") })
            }
            OutlinedTextField(search, { search = it }, label = { Text("Buscar por nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            val list = model.devices.filter { DeliveryRules.inHistory(it,model.outgoing) == history && it.description.contains(search.trim(), true) }
            if (list.isEmpty()) {
                Icon(if (history) Icons.Outlined.TaskAlt else Icons.Outlined.Devices, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                Text(if (search.isNotBlank()) "No encontramos ese aparato." else if (history) "Tus decisiones se verán aquí." else "Tu próximo paso empieza aquí.", style = MaterialTheme.typography.titleLarge)
                Text(if (history) "Aquí verás entregas confirmadas, resultados personales y aparatos retirados. Las entregas canceladas se muestran por separado." else "Lo que guardes aquí seguirá pendiente. No contará como entregado hasta que el receptor confirme que lo recibió.")
            }
            list.forEach { device ->
                OutlinedCard(onClick = { open(device.id) }, shape = RoundedCornerShape(22.dp), colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(device.category.label, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                        Text(device.description, style = MaterialTheme.typography.titleLarge)
                        Text(DeliveryRules.pendingLabel(device,model.outgoing))
                        if (!DeliveryRules.inHistory(device,model.outgoing)) {
                            LinearProgressIndicator(progress = { device.preparedCount / 5f }, modifier = Modifier.fillMaxWidth())
                            Text("Preparación: ${device.preparedCount} de 5 revisados", style = MaterialTheme.typography.bodyMedium)
                        } else Text("Registrado: ${displayDate(device.archivedAt ?: device.completedAt ?: model.outgoing.firstOrNull {d->d.status==DeliveryStatus.COMPLETED && d.items.any {it.deviceId==device.id}}?.completedAt)}", style = MaterialTheme.typography.bodyMedium)
                        Text(if(history) "Ver historial →" else "Continuar preparación →", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            if(history) {
                val cancelled=model.outgoing.filter {it.status==DeliveryStatus.CANCELLED && it.items.any {item->item.description.contains(search.trim(),true)}}
                if(cancelled.isNotEmpty()) Text("Entregas canceladas",style=MaterialTheme.typography.titleLarge)
                cancelled.forEach {delivery->OutlinedCard(onClick={openDelivery(delivery.id)},modifier=Modifier.fillMaxWidth()) {Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text(delivery.receiverName,style=MaterialTheme.typography.titleMedium)
                    Text(delivery.items.joinToString {it.description});Text("Cancelada · no cuenta como entrega realizada")
                }}}
            }
            if (!model.demo) OutlinedButton(onClick = model::reload, enabled = !model.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Actualizar mis aparatos") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveDeviceScreen(model: NokiModel, id: String, orientation: Orientation, onSaved: (String) -> Unit, about: () -> Unit,prepareDelivery:Boolean=false) {
    var description by rememberSaveable { mutableStateOf("") }
    var invalid by rememberSaveable { mutableStateOf(false) }
    var showAccess by rememberSaveable { mutableStateOf(true) }
    if (!model.canSave) {
        Page {
            Text("Guarda tu orientación",style=MaterialTheme.typography.headlineMedium)
            Note("${orientation.category!!.label}\n${orientation.recommendation()!!.title}")
            Text("Para guardar este aparato y preparar una entrega, entra a tu cuenta. No tendrás que responder de nuevo.")
            PrimaryButton("Entrar o crear cuenta") {showAccess=true}
        }
        if(showAccess) AccessDialog(dismiss={showAccess=false}) {
            AccountScreen(model, {}, about,inlineNotice=true)
        }
        return
    }
    Page {
        Text("Ponle un nombre para encontrarlo", style = MaterialTheme.typography.headlineMedium)
        Text("Tu orientación ya está lista. Guarda un pendiente para continuar con la preparación; esto no registra una entrega.")
        OutlinedTextField(description, { description = it.take(120); invalid = false }, label = { Text("Nombre del aparato") }, placeholder = { Text("Por ejemplo: teléfono anterior") },
            supportingText = { Text(if (invalid) "Escribe entre 2 y 120 caracteres." else "${description.length}/120 · No incluyas contraseñas ni datos privados.") },
            isError = invalid, modifier = Modifier.fillMaxWidth(), enabled = !model.busy)
        Note("${orientation.category!!.label}\n${orientation.recommendation()!!.title}")
        PrimaryButton("Pasar a decisión", !model.busy) {
            invalid = !Validation.description(description)
            if (!invalid) model.save(DeviceRecord(id, description.trim(), orientation.category!!, orientation.safety!!, orientation.function ?: FunctionState.UNKNOWN,decision=if(prepareDelivery)Decision.DELIVER else Decision.UNDECIDED), true) { onSaved(it.id) }
        }
        Text(if (model.demo) "Se guardará únicamente en tu espacio de prueba local." else "Se guardará en tu cuenta. Esta acción no crea una entrega ni una reserva.", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DeviceDetailScreen(model: NokiModel, id: String, points: (DeviceRecord) -> Unit, deleted: () -> Unit,createDelivery:(String)->Unit,openDelivery:(String)->Unit,resumePreparation:Boolean=false,preparationResumed:()->Unit={}) {
    val device = model.devices.firstOrNull { it.id == id }
    var section by rememberSaveable(id) { mutableIntStateOf(if(DeliveryRules.confirmed(id,model.deliveries))3 else 0) }
    LaunchedEffect(resumePreparation) {if(resumePreparation && device!=null) {section=if(device.decision==Decision.UNDECIDED)0 else 1;preparationResumed()}}
    var edit by remember { mutableStateOf(false) }
    var deleteConfirm by remember { mutableStateOf(false) }
    var selectedOutcome by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmResult by remember { mutableStateOf(false) }
    var confirmReopen by remember { mutableStateOf(false) }
    if (device == null) {
        Page { Text("Este aparato no está disponible."); PrimaryButton("Volver a mis aparatos", onClick = deleted) }
        return
    }
    if(device.archivedAt!=null) {
        Page {
            Text(device.description,style=MaterialTheme.typography.headlineMedium)
            Note("Retirado por ti · sin entrega")
            Text("Lo retiraste de tus pendientes el ${displayDate(device.archivedAt)}. No cuenta como entrega ni reciclaje.")
            Text("Conservamos esta referencia en el historial. Para solicitar la eliminación de tus datos, contacta a ${co.noki.app.data.LegalDocuments.CONTACT}.")
            PrimaryButton("Volver a mis aparatos",onClick=deleted)
        };return
    }
    val point = model.points.firstOrNull { it.id == device.pointId }
    val handoff=model.outgoing.firstOrNull {it.status!=DeliveryStatus.CANCELLED && it.items.any {item->item.deviceId==id}}
    LaunchedEffect(handoff?.status) {if(handoff?.status==DeliveryStatus.COMPLETED) section=3}
    DeliveryAutoRefresh(model,handoff?.active==true)
    val locked=handoff!=null
    val canResult=device.completed || (device.decision!=Decision.UNDECIDED && DeliveryRules.canRecord(device,model.deliveries))
    fun save(value: DeviceRecord) { model.save(value, false) {} }
    Page(resetKey=section) {
        Text(device.category.label, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
        Text(device.description, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
        Note(DeliveryRules.pendingLabel(device,model.outgoing))
        TextButton(onClick = { edit = true }, enabled = !model.busy && !locked) { Text("Editar nombre y estado") }
        Note(device.recommendation.title, device.safety != SafetyState.NORMAL)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (listOf(0 to "Decisión", 1 to "Preparación")+ (if(device.decision==Decision.DELIVER)listOf(2 to "Entrega")else emptyList()) +listOf(3 to "Resultado")).forEach { (index,title) ->
                FilterChip(selected = section == index, onClick = { section = index },enabled=when(index){1->device.decision!=Decision.UNDECIDED;2->DeliveryRules.ready(device)||handoff!=null;3->canResult;else->true}, label = { Text(title) })
            }
        }
        if(device.decision==Decision.DELIVER && !canResult) Text("Resultado se habilitará después de confirmar la entrega con el receptor.",style=MaterialTheme.typography.bodyMedium)
        if(device.decision in listOf(Decision.KEEP,Decision.REPAIR,Decision.REUSE)) Note("Con este plan no se genera código ni QR: conservarás el aparato o gestionarás su reparación por tu cuenta. Si vas a donarlo, regalarlo o llevarlo a un receptor de Noki, elige «Entregar, donar o llevar a recolección» en Decisión.")
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            when (section) {
                0 -> {
                    Text("¿Qué te gustaría hacer?", style = MaterialTheme.typography.titleLarge)
                    Text("Es una intención. Puedes cambiarla antes de registrar lo que finalmente hiciste.")
                    Decision.entries.forEach { decision ->
                        Choice(decision.label, device.decision == decision) { if (!model.busy && !device.completed && !locked) save(device.copy(decision = decision)) }
                    }
                    if (device.completed) Note("Este aparato ya tiene un resultado. Reábrelo si quieres cambiar tu plan.")
                    Text("Lugar de referencia", style = MaterialTheme.typography.titleLarge)
                    Text(point?.name ?: if (device.pointId == null) "Todavía no has elegido un lugar." else "El lugar guardado no está en la lista actual. Consulta el directorio antes de ir.")
                    OutlinedButton(onClick = { points(device) }, enabled = !model.busy && !device.completed && !locked, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(if (device.pointId == null) "Elegir un punto" else "Cambiar punto") }
                    if (device.pointId != null && !device.completed && !locked) TextButton(onClick = { save(device.copy(pointId = null)) }, enabled = !model.busy) { Text("Quitar referencia") }
                    Note("Elegir un punto no reserva un horario ni garantiza la recepción. Confirma las condiciones directamente con el lugar.")
                    PrimaryButton("Pasar a preparación",device.decision!=Decision.UNDECIDED && !model.busy) { section = 1 }
                }
                1 -> {
                    Text("Prepara tu siguiente paso", style = MaterialTheme.typography.titleLarge)
                    if(device.decision==Decision.DELIVER) DeliveryTrustNote()
                    if (device.safety != SafetyState.NORMAL) Note("No enciendas, cargues ni desmontes el aparato para completar esta lista. Consulta con el receptor cómo proceder. Marca «No aplica» en tareas que no puedas realizar de forma segura; no las marques como hechas.", true)
                    Text("${device.preparedCount} de 5 revisados. “No aplica” cuenta como revisado, no como una tarea realizada.")
                    LinearProgressIndicator(progress = { device.preparedCount / 5f }, modifier = Modifier.fillMaxWidth())
                    Preparation.entries.forEach { item ->
                        OutlinedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.testTag("prep-${item.name}")) {
                            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(item.label, style = MaterialTheme.typography.titleMedium)
                                Text(item.help, style = MaterialTheme.typography.bodyMedium)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    CheckState.entries.forEach { state -> FilterChip(selected = device.preparation[item] == state,
                                        onClick = { save(device.copy(preparation = device.preparation + (item to state))) }, enabled = !model.busy && !device.completed && !locked, label = { Text(state.label) }) }
                                }
                            }
                        }
                    }
                    if(device.decision==Decision.DELIVER) {
                        if(device.preparedCount<5) Note("Revisa las cinco tareas. Marca «No aplica» cuando una tarea no corresponda; no hagas nada que ponga en riesgo tu seguridad.")
                        Text("A continuación elegirás el receptor y confirmarás los aparatos para generar el QR. No necesitas calcular una ruta para continuar.",style=MaterialTheme.typography.bodyMedium)
                        PrimaryButton(if(handoff!=null) "Ver código y entrega" else "Generar código y QR",!model.busy && (device.preparedCount==5 || handoff!=null)) {
                            (handoff ?: model.activeDelivery)?.let {openDelivery(it.id)} ?: createDelivery(id)
                        }
                    } else PrimaryButton("Pasar a resultado",!model.busy && canResult) {section=3}
                }
                2 -> {
                    Text("Tu código de entrega",style=MaterialTheme.typography.titleLarge)
                    Text("Agrupa uno o varios aparatos y elige un receptor aprobado. El código le permitirá revisar y confirmar lo que recibe. Al confirmar que recibió los aparatos, la entrega se completará y verás el resultado automáticamente.")
                    if(handoff!=null) {Note(handoff.status.label);PrimaryButton("Ver mi entrega") {openDelivery(handoff.id)}}
                    else PrimaryButton(if(model.activeDelivery!=null) "Ver entrega pendiente" else "Pasar a generar entrega",!model.busy && DeliveryRules.ready(device)) {
                        model.activeDelivery?.let {openDelivery(it.id)} ?: createDelivery(id)
                    }
                    PrimaryButton("Pasar a resultado",canResult) {section=3}
                }
                else -> {
                    Text(if (device.completed) "Tu resultado" else "¿Qué hiciste con el aparato?", style = MaterialTheme.typography.titleLarge)
                    Note(if(DeliveryRules.confirmed(id,model.deliveries)) {
                        if(model.demo) "Completaste una recepción de prueba. No se realizó una entrega real."
                        else "El receptor confirmó que recibió los aparatos. El resultado ya está en tu historial; no certifica el reciclaje o tratamiento posterior."
                    } else "Este registro es tu declaración personal. No es un certificado de reciclaje ni un comprobante de recepción de un establecimiento.")
                    if(handoff?.status==DeliveryStatus.COMPLETED) {
                        StatusNotice("Entrega realizada",success=true)
                        Text("Recibido en ${handoff.receiverName}")
                        Text("Registrado: ${displayDate(handoff.completedAt)}")
                        PrimaryButton("Volver a mis aparatos",onClick=deleted)
                    } else if (device.completed) {
                        Text(device.outcome!!.label, style = MaterialTheme.typography.titleLarge)
                        Text("Registrado: ${displayDate(device.completedAt)}")
                        if(!locked) PrimaryButton("Reabrir para corregir", !model.busy) { confirmReopen = true }
                        else Text("La entrega confirmada conserva su constancia. Para reportar un error escribe a ${co.noki.app.data.LegalDocuments.CONTACT}.")
                    } else {
                        Text("Registra solo algo que ya haya ocurrido. Si aún no lo has hecho, puedes volver después.")
                        (if(device.decision==Decision.DELIVER) listOf(Outcome.DELIVERED) else Outcome.entries.filter {it!=Outcome.DELIVERED}).forEach { outcome -> Choice(outcome.label, selectedOutcome == outcome.name) { selectedOutcome = outcome.name } }
                        PrimaryButton("Guardar resultado", !model.busy && canResult && selectedOutcome != null) { confirmResult = true }
                    }
                }
            }
        }
        HorizontalDivider()
        if(!device.completed && !locked) TextButton(onClick = { deleteConfirm = true }, enabled = !model.busy) { Text("Retirar de pendientes", color = MaterialTheme.colorScheme.error) }
    }
    if (edit) EditDeviceDialog(device, model.busy, { edit = false }) { updated -> model.save(updated, false) { edit = false } }
    if (deleteConfirm) AlertDialog(onDismissRequest = { deleteConfirm = false }, title = { Text("¿Retirar este aparato de pendientes?") },
        text = { Text("“${device.description}” pasará al historial como «Retirado por ti», sin contar como entregado. Su preparación se conservará como referencia.") },
        confirmButton = { TextButton(onClick = { deleteConfirm = false; model.delete(device, deleted) }, enabled = !model.busy) { Text("Retirar y guardar en historial") } },
        dismissButton = { TextButton(onClick = { deleteConfirm = false }) { Text("Conservar") } })
    if (confirmResult) AlertDialog(onDismissRequest = { confirmResult = false }, title = { Text("Confirmar resultado") },
        text = { Text("${Outcome.valueOf(selectedOutcome!!).label}. Se registrará en el historial con la fecha actual. Las tareas pendientes no se marcarán como realizadas.") },
        confirmButton = { TextButton(onClick = { confirmResult = false; save(device.copy(outcome = Outcome.valueOf(selectedOutcome!!))) }, enabled = !model.busy) { Text("Sí, registrar") } },
        dismissButton = { TextButton(onClick = { confirmResult = false }) { Text("Todavía no") } })
    if (confirmReopen) AlertDialog(onDismissRequest = { confirmReopen = false }, title = { Text("¿Reabrir este aparato?") },
        text = { Text("Volverá a Pendientes. Se quitarán el resultado y su fecha; tu preparación se conservará.") },
        confirmButton = { TextButton(onClick = { confirmReopen = false; save(device.copy(outcome = null, completedAt = null)); section = 0 }) { Text("Reabrir") } },
        dismissButton = { TextButton(onClick = { confirmReopen = false }) { Text("Cancelar") } })
}

@Composable
private fun EditDeviceDialog(device: DeviceRecord, busy: Boolean, dismiss: () -> Unit, save: (DeviceRecord) -> Unit) {
    var name by rememberSaveable(device.id) { mutableStateOf(device.description) }
    var safety by rememberSaveable { mutableStateOf(device.safety.name) }
    var function by rememberSaveable { mutableStateOf(device.function.name) }
    AlertDialog(onDismissRequest = { if (!busy) dismiss() }, title = { Text("Editar aparato") }, text = {
        Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(name, { name = it.take(120) }, label = { Text("Nombre") }, enabled = !busy)
            EnumMenu("Seguridad", SafetyState.valueOf(safety).label, SafetyState.entries.map { it.name to it.label }, !busy) { safety = it }
            if (safety == SafetyState.NORMAL.name) EnumMenu("Funcionamiento", FunctionState.valueOf(function).label, FunctionState.entries.map { it.name to it.label }, !busy) { function = it }
            Text("Cambiar el estado recalcula la orientación. No compruebes el equipo encendiéndolo o desmontándolo.")
        }
    }, confirmButton = { TextButton(onClick = { save(device.copy(description = name.trim(), safety = SafetyState.valueOf(safety), function = if (safety == SafetyState.NORMAL.name) FunctionState.valueOf(function) else FunctionState.UNKNOWN)) }, enabled = !busy && Validation.description(name)) { Text("Guardar cambios") } },
        dismissButton = { TextButton(onClick = dismiss, enabled = !busy) { Text("Cancelar") } })
}

@Composable
fun EnumMenu(label: String, value: String, values: List<Pair<String,String>>, enabled: Boolean = true, selected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        OutlinedButton(onClick = { expanded = true }, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text(value, Modifier.weight(1f)); Icon(Icons.Outlined.ExpandMore,null) }
        DropdownMenu(expanded, { expanded = false }) { values.forEach { (id, text) -> DropdownMenuItem(text = { Text(text) }, onClick = { selected(id); expanded = false }) } }
    }
}
private fun displayDate(value: String?): String = runCatching { OffsetDateTime.parse(value).atZoneSameInstant(java.time.ZoneId.of("America/Bogota")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy · h:mm a")) }.getOrDefault("Fecha no disponible")
