package co.noki.app.domain

enum class DeliveryStatus(val label:String) { PENDING("Pendiente de recepción"), RECEIVED("Recepción confirmada · falta tu confirmación"), COMPLETED("Entrega confirmada"), CANCELLED("Entrega cancelada") }
data class DeliveryItem(val deviceId:String,val description:String,val category:String,val safety:String)
data class Delivery(val id:String,val senderId:String,val receiverId:String,val receiverName:String,val address:String,
    val status:DeliveryStatus,val items:List<DeliveryItem>,val createdAt:String="",val completedAt:String?=null,val contactConfirmed:Boolean=false) {
    val active get()=status==DeliveryStatus.PENDING || status==DeliveryStatus.RECEIVED
}
data class ReceiverPlace(val id:String,val name:String,val address:String,val hours:String,val categories:List<String>,val conditions:String,val whatsapp:String?=null,val requestId:String?=null)

object ReceiverContact {
    fun valid(value:String)=Regex("^[1-9][0-9]{9,14}$").matches(value)
    fun whatsappUrl(value:String):String?=if(valid(value)) "https://wa.me/$value" else null
}

object DeliveryRules {
    fun ready(device:DeviceRecord)=!device.completed && device.archivedAt==null && device.decision==Decision.DELIVER && device.preparedCount==Preparation.entries.size
    fun locked(deviceId:String,deliveries:List<Delivery>)=deliveries.any {it.status!=DeliveryStatus.CANCELLED && it.items.any {i->i.deviceId==deviceId}}
    fun confirmed(deviceId:String,deliveries:List<Delivery>)=deliveries.any {it.status==DeliveryStatus.COMPLETED && it.items.any {i->i.deviceId==deviceId}}
    fun inHistory(device:DeviceRecord,deliveries:List<Delivery>)=device.archivedAt!=null || device.completed || confirmed(device.id,deliveries)
    fun pendingLabel(device:DeviceRecord,deliveries:List<Delivery>):String {
        if(device.archivedAt!=null) return "Retirado por ti · sin entrega"
        val delivery=deliveries.firstOrNull {it.status!=DeliveryStatus.CANCELLED && it.items.any {item->item.deviceId==device.id}}
        if(delivery!=null) return delivery.status.label
        if(device.completed) return device.outcome!!.label
        return if(device.decision==Decision.DELIVER) "Entrega pendiente · falta crear el código" else "Pendiente · ${device.decision.label}"
    }
    fun canRecord(device:DeviceRecord,deliveries:List<Delivery>)=device.archivedAt==null && (device.decision!=Decision.DELIVER || confirmed(device.id,deliveries))
    fun needsContact(devices:List<DeviceRecord>,receiver:ReceiverPlace)=devices.any {it.safety!=SafetyState.NORMAL || it.category.code !in receiver.categories}
    fun canCreate(devices:List<DeviceRecord>,deliveries:List<Delivery>,receiver:ReceiverPlace,contactConfirmed:Boolean=false):Boolean =
        devices.size in 1..20 && devices.map {it.id}.distinct().size==devices.size && deliveries.none {it.active} &&
        devices.all {ready(it) && !locked(it.id,deliveries)} &&
        (!needsContact(devices,receiver) || (contactConfirmed && receiver.whatsapp?.let(ReceiverContact::valid)==true))
}
