package co.noki.app.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.noki.app.data.LegalDocuments
import co.noki.app.domain.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable private fun ReceiverFieldError(issue: ReceiverIssue?) {
    if (issue != null) Row(Modifier.fillMaxWidth().semantics { error(issue.message) }, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Outlined.Close, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
        Text(issue.message, Modifier.weight(1f), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable private fun ReceiverInput(
    field: ReceiverField, label: String, value: String, minimum: Int, maximum: Int,
    issue: ReceiverIssue?, modifier: Modifier, busy: Boolean, hint: String = "",
    keyboard: KeyboardType = KeyboardType.Text, changed: (String) -> Unit
) {
    // External labels wrap without the floating-label cutout clipping large text.
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = if (issue != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clearAndSetSemantics { testTag = "receiver-label-${field.name}" })
        OutlinedTextField(
            value, changed, enabled = !busy,
            modifier = Modifier.fillMaxWidth().testTag("receiver-${field.name}").semantics { contentDescription = label },
            isError = issue != null, keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            supportingText = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (issue != null) Text(issue.message)
                    else if (hint.isNotEmpty()) Text(hint)
                    Text("${value.trim().length}/$maximum caracteres · mínimo $minimum")
                }
            }
        )
    }
}

@Composable private fun ReceiverPending(issues: List<ReceiverIssue>, attempted: Boolean, busy: Boolean, review: (ReceiverField) -> Unit) {
    if (issues.isEmpty()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
            Text("Este paso está completo. Puedes continuar.", Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
        }
    } else {
        Surface(color = if (attempted) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(if (issues.size == 1) "Falta completar 1 requisito" else "Faltan ${issues.size} requisitos",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.semantics { if (attempted) liveRegion = LiveRegionMode.Polite })
                Text(issues.joinToString(" · ") { it.field.label }, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { review(issues.first().field) }, enabled = !busy) {
                    Text("Revisar: ${issues.first().field.label}")
                }
            }
        }
    }
}

/** UI state is kept locally; validation never submits or approves a request. */
@OptIn(ExperimentalFoundationApi::class)
@Composable internal fun ReceiverApplicationForm(
    email: String, draft: JSONObject?, busy: Boolean, submit: (JSONObject, List<Uri>) -> Unit
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var attempted by rememberSaveable(step) { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf(draft?.optString("full_name").orEmpty()) }
    var place by rememberSaveable { mutableStateOf(draft?.optString("place_name").orEmpty()) }
    var address by rememberSaveable { mutableStateOf(draft?.optString("address").orEmpty()) }
    var latitude by rememberSaveable { mutableStateOf(draft?.takeUnless { it.isNull("latitude") }?.optDouble("latitude")) }
    var longitude by rememberSaveable { mutableStateOf(draft?.takeUnless { it.isNull("longitude") }?.optDouble("longitude")) }
    var addressChanged by rememberSaveable { mutableStateOf(false) }
    var chooseLocation by remember { mutableStateOf(false) }
    var hours by rememberSaveable { mutableStateOf(draft?.optString("hours").orEmpty()) }
    var conditions by rememberSaveable { mutableStateOf(draft?.optString("conditions").orEmpty()) }
    var whatsapp by rememberSaveable { mutableStateOf(draft?.optString("public_whatsapp")?.takeUnless { it == "null" }.orEmpty()) }
    var publishContact by rememberSaveable { mutableStateOf(false) }
    var categories by rememberSaveable { mutableStateOf(draft?.optJSONArray("categories")?.let { list -> (0 until list.length()).map { list.getString(it) } } ?: emptyList<String>()) }
    var adult by rememberSaveable { mutableStateOf(false) }
    var consent by rememberSaveable { mutableStateOf(false) }
    var photos by rememberSaveable { mutableStateOf(listOf("", "", "")) }
    var selectedPhoto by rememberSaveable { mutableIntStateOf(0) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) photos = photos.toMutableList().also { it[selectedPhoto] = uri.toString() }
    }
    val input = ReceiverApplicationInput(name, adult, place, address, latitude, longitude, hours, whatsapp, publishContact, categories, conditions, photos, consent)
    val issues = ReceiverApplicationRules.issues(input, step)
    fun issue(field: ReceiverField) = if (attempted) issues.firstOrNull { it.field == field } else null
    val requesters = remember { ReceiverField.entries.associateWith { BringIntoViewRequester() } }
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    fun fieldModifier(field: ReceiverField) = Modifier.bringIntoViewRequester(requesters.getValue(field))
    fun review(field: ReceiverField) {
        attempted = true
        keyboard?.hide()
        scope.launch {
            // Let inline errors recompose and measure before choosing the scroll target.
            withFrameNanos { }; withFrameNanos { }
            requesters.getValue(field).bringIntoView()
        }
    }
    fun next() {
        if (issues.isNotEmpty()) review(issues.first().field)
        else { keyboard?.hide(); step++ }
    }

    Page(resetKey = step) {
        Text("Tu lugar de recepción", style = MaterialTheme.typography.headlineMedium)
        Text("Paso ${step + 1} de 3", style = MaterialTheme.typography.labelLarge)
        if (step > 0) TextButton(onClick = { step-- }, enabled = !busy) { Text("Volver al paso anterior") }
        when (step) {
            0 -> {
                Text("Quién estará a cargo", style = MaterialTheme.typography.titleLarge)
                Text("Correo verificado: $email")
                ReceiverInput(ReceiverField.NAME, "Nombre completo del responsable", name, 5, 120, issue(ReceiverField.NAME), fieldModifier(ReceiverField.NAME), busy) { name = it.take(120) }
                Column(fieldModifier(ReceiverField.ADULT)) {
                    AcceptanceCheck("Soy mayor de edad y tengo autorización para operar este lugar", adult) { if (!busy) adult = it }
                    ReceiverFieldError(issue(ReceiverField.ADULT))
                }
                ReceiverPending(issues, attempted, busy, ::review)
                PrimaryButton("Pasar a datos del lugar", !busy, ::next)
            }
            1 -> {
                Text("Datos del lugar", style = MaterialTheme.typography.titleLarge)
                Text("Completa cada campo y confirma el punto exacto en el mapa. Los requisitos pendientes aparecen al final.")
                ReceiverInput(ReceiverField.PLACE, "Nombre del lugar", place, 3, 100, issue(ReceiverField.PLACE), fieldModifier(ReceiverField.PLACE), busy) { place = it.take(100) }
                ReceiverInput(ReceiverField.ADDRESS, "Dirección completa y ciudad", address, 10, 240, issue(ReceiverField.ADDRESS), fieldModifier(ReceiverField.ADDRESS), busy) {
                    val nextAddress = it.take(240)
                    if (address != nextAddress) {
                        if (latitude != null || longitude != null) addressChanged = true
                        address = nextAddress; latitude = null; longitude = null
                    }
                }
                Column(fieldModifier(ReceiverField.LOCATION), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = {
                        if (address.trim().length < 10) review(ReceiverField.ADDRESS) else chooseLocation = true
                    }, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
                        Text(if (latitude == null || longitude == null) "Ubicar mi lugar en el mapa" else "Ubicación confirmada · ajustar")
                    }
                    Text(when {
                        addressChanged -> "Cambiaste la dirección. Confirma de nuevo la ubicación en el mapa para continuar."
                        latitude == null || longitude == null -> "Pendiente: confirma la entrada del lugar en el mapa. Escribir la dirección no confirma el marcador."
                        else -> "Ubicación confirmada. Este marcador se publicará cuando se apruebe tu solicitud."
                    }, style = MaterialTheme.typography.bodyMedium)
                    ReceiverFieldError(issue(ReceiverField.LOCATION))
                }
                ReceiverInput(ReceiverField.HOURS, "Días y horarios de recepción", hours, 5, 200, issue(ReceiverField.HOURS), fieldModifier(ReceiverField.HOURS), busy,
                    hint = "Ejemplo: lunes a viernes, de 9 a. m. a 5 p. m.") { hours = it.take(200) }
                ReceiverInput(ReceiverField.WHATSAPP, "WhatsApp público de contacto", whatsapp, 10, 15, issue(ReceiverField.WHATSAPP), fieldModifier(ReceiverField.WHATSAPP), busy,
                    hint = "Incluye el código de país: en Colombia, 57 seguido de los 10 dígitos. No empieces por 0.", keyboard = KeyboardType.Phone) { whatsapp = it.filter(Char::isDigit).take(15) }
                Text("Al aprobarse, el nombre, dirección, marcador, horario, categorías, condiciones y WhatsApp del lugar estarán visibles también sin iniciar sesión. El correo de acceso y las fotos de revisión seguirán privados.", style = MaterialTheme.typography.bodyMedium)
                Column(fieldModifier(ReceiverField.PUBLISH)) {
                    AcceptanceCheck("Autorizo publicar los datos del lugar y este WhatsApp para consultas sobre entregas", publishContact) { if (!busy) publishContact = it }
                    ReceiverFieldError(issue(ReceiverField.PUBLISH))
                }
                Column(fieldModifier(ReceiverField.CATEGORIES)) {
                    Text("Qué aparatos recibes", style = MaterialTheme.typography.titleMedium)
                    Text("Selecciona al menos una opción.", style = MaterialTheme.typography.bodyMedium)
                    ReceiverFieldError(issue(ReceiverField.CATEGORIES))
                }
                DeviceCategory.entries.forEach { category ->
                    AcceptanceCheck(category.label, category.code in categories) { checked ->
                        if (!busy) categories = if (checked) (categories + category.code).distinct() else categories - category.code
                    }
                }
                ReceiverInput(ReceiverField.CONDITIONS, "Condiciones para recibirlos", conditions, 10, 500, issue(ReceiverField.CONDITIONS), fieldModifier(ReceiverField.CONDITIONS), busy,
                    hint = "Indica qué estado, tamaños y accesorios aceptas. Si no hay restricciones, escríbelo claramente.") { conditions = it.take(500) }
                ReceiverPending(issues, attempted, busy, ::review)
                PrimaryButton("Pasar a fotos y revisión", !busy, ::next)
            }
            2 -> {
                Text(place, style = MaterialTheme.typography.titleLarge)
                Text("$address\n$hours")
                Text(conditions)
                Text("Añade tres fotos actuales", style = MaterialTheme.typography.titleLarge)
                Text("Sin personas, cédulas ni datos privados. Las fotos se enviarán únicamente al pulsar «Enviar solicitud».")
                Text("Al aprobarse el perfil, otros usuarios podrán consultar el nombre, dirección, horarios, WhatsApp y condiciones del lugar. Las fotos de revisión y tu nombre personal no se publican.", style = MaterialTheme.typography.bodyMedium)
                val photoFields = listOf(ReceiverField.FRONT_PHOTO, ReceiverField.RECEPTION_PHOTO, ReceiverField.STORAGE_PHOTO)
                listOf("Fachada y entrada", "Área de recepción", "Espacio de almacenamiento").forEachIndexed { index, label ->
                    Column(fieldModifier(photoFields[index])) {
                        OutlinedButton(onClick = { selectedPhoto = index; picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, enabled = !busy,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                            Text("${index + 1}. $label · ${if (photos[index].isEmpty()) "Elegir foto" else "Foto seleccionada · cambiar"}")
                        }
                        ReceiverFieldError(issue(photoFields[index]))
                    }
                }
                Note("Revisaremos tu solicitud a distancia. Noki podrá coordinar visitas ocasionales para comprobar el lugar y sus condiciones de recepción.")
                Column(fieldModifier(ReceiverField.CONSENT)) {
                    AcceptanceCheck("Confirmo que la información es correcta y autorizo usar estas fotos para revisar mi solicitud, según los términos y la privacidad", consent) { if (!busy) consent = it }
                    ReceiverFieldError(issue(ReceiverField.CONSENT))
                }
                ReceiverPending(issues, attempted, busy, ::review)
                PrimaryButton(if (busy) "Enviando solicitud…" else "Enviar solicitud", !busy) {
                    val pending = ReceiverApplicationRules.all(input)
                    if (pending.isNotEmpty()) {
                        val first = pending.first().field
                        if (first.step != step) step = first.step else review(first)
                    } else {
                        keyboard?.hide()
                        submit(JSONObject().put("full_name", name.trim()).put("place_name", place.trim()).put("address", address.trim())
                            .put("latitude", latitude).put("longitude", longitude).put("hours", hours.trim()).put("conditions", conditions.trim())
                            .put("public_whatsapp", whatsapp).put("categories", JSONArray(categories)).put("accepted", true)
                            .put("adult_authorized", adult).put("legal_version", LegalDocuments.VERSION), photos.map(Uri::parse))
                    }
                }
                Text("La solicitud no activa el rol. Podrás recibir aparatos cuando veas la aprobación en tu perfil.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    if (chooseLocation) ReceiverLocationDialog(address, latitude?.let { lat -> longitude?.let { org.maplibre.android.geometry.LatLng(lat, it) } },
        dismiss = { chooseLocation = false }, selected = { latitude = it.latitude; longitude = it.longitude; addressChanged = false; chooseLocation = false })
}
