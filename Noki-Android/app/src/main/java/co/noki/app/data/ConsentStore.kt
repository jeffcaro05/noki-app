package co.noki.app.data

import org.json.JSONObject
import java.time.Instant
import java.security.MessageDigest

/** Local UX receipt, not a server authorization rule or a digital signature. */
class ConsentStore(private val secure:KeyValueStore) {
    private fun key(subject:String)="consent."+MessageDigest.getInstance("SHA-256").digest(subject.toByteArray()).joinToString("") {"%02x".format(it)}
    fun accepted(subject:String):Boolean = secure.get(key(subject))?.let {runCatching {val r=JSONObject(it);r.optString("version")==LegalDocuments.VERSION && r.optBoolean("terms") && r.optBoolean("privacy")}.getOrDefault(false)} ?: false
    fun accept(subject:String) {secure.put(key(subject),JSONObject().put("subject",subject).put("version",LegalDocuments.VERSION).put("terms",true).put("privacy",true).put("accepted_at_device",Instant.now().toString()).toString())}
    fun revoke(subject:String) {secure.put(key(subject),null)}
}
