package co.noki.app.domain

enum class DeviceCategory(val code: String, val label: String) {
    PHONE("phone", "Teléfono o tableta"),
    COMPUTER("computer", "Computador o periférico"),
    AUDIO_VIDEO("audio_video", "Audio o video"),
    ACCESSORIES("accessories", "Accesorios y cables"),
    APPLIANCE("appliance", "Pequeño electrodoméstico"),
    OTHER("other", "Otro aparato electrónico")
}
enum class SafetyState(val code: String, val label: String) {
    ATTENTION("attention", "Sí, he notado alguna de esas señales"),
    NORMAL("normal", "No he notado algo así"),
    UNKNOWN("unknown", "No estoy seguro")
}
enum class FunctionState(val code: String, val label: String) {
    WORKS("works", "Sí, funciona"), FAULTS("faults", "Funciona con fallas"), UNKNOWN("unknown", "No lo sé")
}
enum class Recommendation(val title: String, val description: String, val next: String) {
    SAFETY_REVIEW(
        "Primero, una revisión segura",
        "Como notaste una señal de atención o tienes dudas, conviene consultar a una persona capacitada antes de manipular o trasladar el aparato.",
        "No lo enciendas, cargues ni desmontes para responder esta guía. Consulta las condiciones de manejo antes de llevarlo a un lugar."
    ),
    SECOND_LIFE(
        "Todavía puede tener una segunda vida",
        "Según lo que nos cuentas, el aparato funciona y no has notado señales de atención. Puedes considerar seguir usándolo o entregarlo a alguien que lo necesite.",
        "Si lo entregas a otra persona, revisa primero cómo proteger tu información. No es necesario descartarlo solo porque ya no lo uses."
    ),
    REPAIR_REVIEW(
        "Una revisión puede ayudarte a decidir",
        "El aparato presenta fallas o no conoces su funcionamiento. Una revisión puede ayudarte a valorar si conviene repararlo antes de descartarlo.",
        "Consulta las opciones de revisión. Si decides llevarlo a recolección, confirma qué equipos acepta el lugar antes de desplazarte."
    )
}
data class Orientation(
    val category: DeviceCategory? = null,
    val safety: SafetyState? = null,
    val function: FunctionState? = null
) {
    fun withSafety(value: SafetyState): Orientation = copy(safety = value, function = if (value == SafetyState.NORMAL) function else null)
    fun recommendation(): Recommendation? = when {
        category == null || safety == null -> null
        safety != SafetyState.NORMAL -> Recommendation.SAFETY_REVIEW
        function == null -> null
        function == FunctionState.WORKS -> Recommendation.SECOND_LIFE
        else -> Recommendation.REPAIR_REVIEW
    }
}
object Validation {
    fun name(value: String) = value.trim().length in 2..80
    fun description(value: String) = value.trim().length in 2..120
    fun optional(value: String) = value.trim().length <= 60
    fun email(value: String) = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(value.trim())
    fun password(value: String) = value.length in 8..64
}
