package co.noki.app.domain

enum class Decision(val label: String) {
    UNDECIDED("Aún no he decidido"), KEEP("Seguir usándolo"), REPAIR("Revisar o reparar"),
    REUSE("Darle otro uso sin entregarlo"), DELIVER("Entregar, donar o llevar a recolección")
}
enum class Preparation(val label: String, val help: String) {
    BACKUP("Guardar una copia de tus archivos", "Comprueba que puedas abrir la copia antes de borrar el equipo."),
    ACCOUNTS("Cerrar y desvincular tus cuentas", "Sigue la guía del fabricante. No compartas tus contraseñas con quien recibe el equipo."),
    MEDIA("Retirar SIM y memorias extraíbles", "Solo si puedes hacerlo sin abrir ni desmontar el aparato."),
    ERASURE("Borrar tus datos", "Hazlo únicamente después de comprobar tu copia. Noki no borra los datos del aparato."),
    DESTINATION("Confirmar las condiciones del destino", "Pregunta por tu aparato, su estado, el horario y el lugar exacto de recepción.")
}
enum class CheckState(val label: String) { PENDING("Pendiente"), DONE("Hecho"), NOT_APPLICABLE("No aplica") }
enum class Outcome(val label: String) {
    KEPT("Lo sigo usando"), REPAIRED("Lo reparé"), REUSED("Le di otro uso o lo entregué a alguien"),
    DELIVERED("Lo llevé a recolección"), OTHER("Tomé otra decisión")
}

data class DeviceRecord(
    val id: String, val description: String, val category: DeviceCategory,
    val safety: SafetyState, val function: FunctionState,
    val decision: Decision = Decision.UNDECIDED,
    val pointId: String? = null,
    val preparation: Map<Preparation, CheckState> = Preparation.entries.associateWith { CheckState.PENDING },
    val outcome: Outcome? = null, val completedAt: String? = null,
    val version: Int = 1, val updatedAt: String = "", val archivedAt:String?=null
) {
    val recommendation: Recommendation get() = Orientation(category, safety, function).recommendation()!!
    val completed: Boolean get() = outcome != null
    val preparedCount: Int get() = preparation.values.count { it != CheckState.PENDING }
}

fun recommendedSteps(category: DeviceCategory, result: Recommendation): List<String> = when (result) {
    Recommendation.SAFETY_REVIEW -> listOf(
        "Deja de usar o cargar el aparato. No lo abras ni intentes retirar una batería interna.",
        "Llama al lugar antes de trasladarlo y describe la señal que observaste. No asumas que un contenedor recibe baterías dañadas.",
        "Si hay humo, fuego o peligro inmediato, aléjate y llama al 123. No sigas esta guía para manipularlo."
    )
    Recommendation.SECOND_LIFE -> listOf(
        "Piensa si todavía te sirve para una tarea concreta. Si lo conservas, registra esa decisión.",
        if (category == DeviceCategory.PHONE || category == DeviceCategory.COMPUTER) "Antes de entregarlo a alguien, respalda tus archivos, desvincula tus cuentas y sigue la guía de borrado del fabricante." else "Si alguien más lo va a usar, informa su estado y entrega sus accesorios útiles. No lo desmontes.",
        "Si no encuentras un nuevo uso y decides descartarlo, consulta un punto y confirma la recepción."
    )
    Recommendation.REPAIR_REVIEW -> listOf(
        "Describe la falla que ya conoces. No necesitas encender el aparato para completar la orientación.",
        "Solicita un diagnóstico y presupuesto a un servicio técnico. Pregunta por repuestos, garantía y protección de tus datos antes de autorizar una reparación.",
        "Si decides no repararlo, consulta lugares de recolección. Noki no determina si un aparato tiene arreglo."
    )
}
