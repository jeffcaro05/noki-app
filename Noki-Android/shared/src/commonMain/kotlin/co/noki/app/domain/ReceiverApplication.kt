package co.noki.app.domain

enum class ReceiverField(val label: String, val step: Int) {
    NAME("Nombre del responsable", 0), ADULT("Mayoría de edad y autorización", 0),
    PLACE("Nombre del lugar", 1), ADDRESS("Dirección y ciudad", 1),
    LOCATION("Ubicación en el mapa", 1), HOURS("Días y horarios", 1),
    WHATSAPP("WhatsApp de contacto", 1), PUBLISH("Autorización de publicación", 1),
    CATEGORIES("Aparatos que recibes", 1), CONDITIONS("Condiciones de recepción", 1),
    FRONT_PHOTO("Foto de fachada y entrada", 2), RECEPTION_PHOTO("Foto del área de recepción", 2),
    STORAGE_PHOTO("Foto del almacenamiento", 2), CONSENT("Confirmación de información y fotos", 2)
}

data class ReceiverIssue(val field: ReceiverField, val message: String)

data class ReceiverApplicationInput(
    val name: String = "", val adult: Boolean = false,
    val place: String = "", val address: String = "",
    val latitude: Double? = null, val longitude: Double? = null,
    val hours: String = "", val whatsapp: String = "", val publishContact: Boolean = false,
    val categories: List<String> = emptyList(), val conditions: String = "",
    val photos: List<String> = listOf("", "", ""), val consent: Boolean = false
)

/** One source for the pending-field list, inline errors, and navigation guard. */
object ReceiverApplicationRules {
    fun issues(input: ReceiverApplicationInput, step: Int): List<ReceiverIssue> = all(input).filter { it.field.step == step }

    fun all(input: ReceiverApplicationInput): List<ReceiverIssue> = buildList {
        fun requireField(valid: Boolean, field: ReceiverField, message: String) {
            if (!valid) add(ReceiverIssue(field, message))
        }
        requireField(input.name.trim().length in 5..120, ReceiverField.NAME, "Escribe el nombre completo del responsable (mínimo 5 caracteres).")
        requireField(input.adult, ReceiverField.ADULT, "Marca la casilla de mayoría de edad y autorización para operar el lugar.")
        requireField(input.place.trim().length in 3..100, ReceiverField.PLACE, "Escribe el nombre del lugar (mínimo 3 caracteres).")
        requireField(input.address.trim().length in 10..240, ReceiverField.ADDRESS, "Completa la dirección y la ciudad (mínimo 10 caracteres).")
        requireField(input.latitude != null && input.latitude.isFinite() && input.latitude in -90.0..90.0 &&
            input.longitude != null && input.longitude.isFinite() && input.longitude in -180.0..180.0,
            ReceiverField.LOCATION, "Abre «Ubicar mi lugar en el mapa», comprueba el marcador y pulsa «Confirmar ubicación».")
        requireField(input.hours.trim().length in 5..200, ReceiverField.HOURS, "Indica los días y horarios de recepción (mínimo 5 caracteres).")
        requireField(ReceiverContact.valid(input.whatsapp), ReceiverField.WHATSAPP, "Completa el WhatsApp con 10 a 15 dígitos, sin comenzar por 0. En Colombia: 57 seguido del número de celular.")
        requireField(input.publishContact, ReceiverField.PUBLISH, "Marca la autorización para publicar los datos del lugar y su WhatsApp.")
        requireField(input.categories.isNotEmpty(), ReceiverField.CATEGORIES, "Selecciona al menos un tipo de aparato que recibes.")
        requireField(input.conditions.trim().length in 10..500, ReceiverField.CONDITIONS, "Describe las condiciones de recepción (mínimo 10 caracteres).")
        listOf(ReceiverField.FRONT_PHOTO, ReceiverField.RECEPTION_PHOTO, ReceiverField.STORAGE_PHOTO).forEachIndexed { index, field ->
            requireField(!input.photos.getOrNull(index).isNullOrBlank(), field, "Selecciona la ${field.label.replaceFirstChar { it.lowercase() }}.")
        }
        requireField(input.consent, ReceiverField.CONSENT, "Marca la confirmación de información correcta y la autorización para revisar las fotos.")
    }
}
