package co.noki.app.data

import co.noki.app.domain.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

data class DeliveryCode(val id:String,val qr:String,val code:String,val expiresAt:String,val validWhilePending:Boolean=false) {
    fun isDisplayable(now:Instant=Instant.now()):Boolean = validWhilePending ||
        runCatching {Instant.parse(expiresAt)>now}.getOrDefault(false)
}
fun deliveryCodeFromJson(id:String,j:JSONObject)=DeliveryCode(id,j.getString("qr"),j.getString("code"),
    j.optString("expires_at"),j.optString("valid_until")=="delivery_closed")
fun deliveryFromJson(j:JSONObject)=Delivery(j.getString("id"),j.getString("sender_id"),j.getString("receiver_id"),j.getString("receiver_name"),j.getString("address"),DeliveryStatus.valueOf(j.getString("status").uppercase()),j.getJSONArray("items").let {a->(0 until a.length()).map {val i=a.getJSONObject(it);DeliveryItem(i.getString("device_id"),i.getString("description"),i.getString("category"),i.getString("safety_state"))}},j.optString("created_at"),if(j.isNull("completed_at"))null else j.getString("completed_at"),j.optBoolean("contact_confirmed") || !j.isNull("contact_confirmed_at"))
fun Delivery.toJson()=JSONObject().put("id",id).put("sender_id",senderId).put("receiver_id",receiverId).put("receiver_name",receiverName).put("address",address).put("status",status.name.lowercase()).put("created_at",createdAt).put("completed_at",completedAt?:JSONObject.NULL).put("contact_confirmed",contactConfirmed).put("items",JSONArray().apply {items.forEach {put(JSONObject().put("device_id",it.deviceId).put("description",it.description).put("category",it.category).put("safety_state",it.safety))}})
val practiceReceiver=ReceiverPlace("practice-receiver","Receptor de ejemplo","Lugar ficticio · no te desplaces", "Solo práctica local",DeviceCategory.entries.map {it.code},"No recibe aparatos reales.")
