package co.noki.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NokiApiException(message: String) : Exception(message)

data class NokiSession(val accessToken: String, val refreshToken: String, val userId: String, val email: String, val expiresAt: Long = 0)
data class CollectionPoint(
    val id: String, val name: String, val address: String, val source: String,
    val sourceUrl: String, val verifiedAt: String, val latitude: Double, val longitude: Double,
    val categories: List<String>, val hours: String?, val phone: String?, val details: String = "Confirma qué aparatos recibe.", val locationNote: String = "Marcador de referencia; confirma la entrada exacta.",
    val receiverRequestId:String?=null,val whatsapp:String?=null
)

class NokiApi(private val client: OkHttpClient = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build(),
              private val base: String = "https://cwsxjsmelrcfhbtbkssd.supabase.co") {
    // The publishable key identifies the project. It cannot replace server-side authorization.
    private val publishableKey = "sb_publishable_D6FWErgyxMpTN4cZfPLoSw_PkrH-cpa"

    private suspend fun request(path: String, method: String = "GET", body: JSONObject? = null, token: String? = null): String = withContext(Dispatchers.IO) {
        val builder = Request.Builder().url(base + path).header("apikey", publishableKey).header("Accept", "application/json")
        if(path.startsWith("/rest/v1/noki_collection_points")) builder.header("Cache-Control","no-cache")
        token?.let { builder.header("Authorization", "Bearer $it") }
        builder.header("Prefer", "return=representation")
        builder.method(method, body?.toString()?.toRequestBody("application/json; charset=utf-8".toMediaType()))
        try {
            client.newCall(builder.build()).execute().use { response ->
                val content = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val code = runCatching { authErrorCode(JSONObject(content)) }.getOrDefault("")
                    throw NokiApiException(when {
                        code == "email_not_confirmed" -> "Debes verificar tu correo antes de entrar."
                        code == "invalid_credentials" || code == "invalid_grant" -> "Correo o contraseña incorrectos. Revisa tus datos o pulsa «Olvidé mi contraseña»."
                        code == "weak_password" -> "Elige una contraseña de al menos 12 caracteres; combina letras, números y símbolos."
                        code == "same_password" -> "Esa ya es tu contraseña actual. Puedes seguir usándola para entrar."
                        code == "reauthentication_needed" || code == "reauthentication_not_valid" -> "Por seguridad, vuelve a entrar con un código reciente antes de cambiar tu contraseña."
                        code == "otp_expired" -> "El código no es válido o ya venció. Revisa el último mensaje o solicita otro código."
                        code == "email_address_not_authorized" -> "El envío de códigos a este correo todavía no está habilitado. Puedes seguir consultando Noki sin cuenta."
                        runCatching {JSONObject(content).optString("message").startsWith("NOKI_")}.getOrDefault(false) -> nativeError(JSONObject(content).getString("message"))
                        response.code == 429 -> "Hay demasiados intentos. Espera unos minutos y vuelve a intentar."
                        response.code == 409 -> "Este registro ya existe. Actualiza la lista antes de volver a intentarlo."
                        response.code == 401 || response.code == 403 -> "No se pudo validar el acceso. Revisa tus datos e inténtalo de nuevo."
                        response.code == 404 || code == "PGRST205" -> "Esta información aún no está disponible. Inténtalo de nuevo más tarde."
                        else -> "El servicio no pudo completar la solicitud. Inténtalo de nuevo."
                    })
                }
                content
            }
        } catch (error: IOException) {
            throw NokiApiException("No pudimos conectar con Noki. Revisa la conexión o inténtalo de nuevo en unos minutos.")
        }
    }

    suspend fun signIn(email: String, password: String): NokiSession {
        val response = JSONObject(request("/auth/v1/token?grant_type=password", "POST", JSONObject().put("email", email.trim().lowercase()).put("password", password)))
        val user = response.getJSONObject("user")
        if (user.isNull("email_confirmed_at") || user.optString("email_confirmed_at").isBlank()) throw NokiApiException("Debes verificar tu correo antes de entrar.")
        return parseSession(response)
    }

    private fun parseSession(response: JSONObject): NokiSession {
        val user = response.getJSONObject("user")
        if (user.isNull("email_confirmed_at") || user.optString("email_confirmed_at").isBlank()) throw NokiApiException("Debes verificar tu correo antes de entrar.")
        return NokiSession(response.getString("access_token"), response.getString("refresh_token"), user.getString("id"), user.getString("email"),
            response.optLong("expires_at", System.currentTimeMillis() / 1000 + response.optLong("expires_in", 3600)))
    }
    suspend fun refresh(session: NokiSession) = parseSession(JSONObject(request("/auth/v1/token?grant_type=refresh_token", "POST", JSONObject().put("refresh_token", session.refreshToken))))

    suspend fun sendEmailCode(email: String) {
        request("/auth/v1/otp", "POST", JSONObject().put("email",email.trim().lowercase()).put("create_user",true))
    }
    suspend fun updatePassword(session:NokiSession,password:String) {
        request("/auth/v1/user","PUT",JSONObject().put("password",password),session.accessToken)
    }
    suspend fun emailAccessEnabled():Boolean=JSONArray(request("/rest/v1/noki_client_config?select=email_otp_enabled&id=eq.true")).optJSONObject(0)?.optBoolean("email_otp_enabled")==true
    suspend fun profileAction(session:NokiSession,op:String,args:JSONObject=JSONObject()):JSONObject = JSONObject(request("/rest/v1/rpc/noki_profile_action","POST",JSONObject().put("op",op).put("args",args),session.accessToken))
    suspend fun acceptAccountTerms(session:NokiSession) {profileAction(session,"accept",JSONObject().put("version",LegalDocuments.VERSION).put("terms",true).put("privacy",true))}
    suspend fun verifyEmailCode(email: String, code: String): NokiSession = parseSession(JSONObject(request("/auth/v1/verify","POST",
        JSONObject().put("email",email.trim().lowercase()).put("token",code).put("type","email"))))
    suspend fun updateName(session: NokiSession,name:String) {
        profileAction(session,"name",JSONObject().put("name",name))
    }

    suspend fun profilePhoto(session:NokiSession):ByteArray?=withContext(Dispatchers.IO) {
        val req=Request.Builder().url(base+"/storage/v1/object/authenticated/noki-profile-photos/${session.userId}/avatar.jpg")
            .header("apikey",publishableKey).header("Authorization","Bearer ${session.accessToken}").header("Cache-Control","no-cache").build()
        try {client.newCall(req).execute().use {response->
            if(response.code==404) return@withContext null
            if(!response.isSuccessful) throw NokiApiException("No pudimos cargar tu foto. Puedes actualizar el perfil para reintentar.")
            response.body?.byteStream()?.use(::readBoundedProfilePhoto)
        }} catch(e:IOException) {throw NokiApiException("No pudimos cargar tu foto. Revisa tu conexión.")}
    }
    suspend fun uploadProfilePhoto(session:NokiSession,bytes:ByteArray)=withContext(Dispatchers.IO) {
        require(bytes.size<=524288)
        val req=Request.Builder().url(base+"/storage/v1/object/noki-profile-photos/${session.userId}/avatar.jpg")
            .header("apikey",publishableKey).header("Authorization","Bearer ${session.accessToken}").header("x-upsert","true")
            .header("Cache-Control","no-store").post(bytes.toRequestBody("image/jpeg".toMediaType())).build()
        try {client.newCall(req).execute().use {if(!it.isSuccessful) throw NokiApiException("No pudimos guardar tu foto. Comprueba la conexión y vuelve a intentarlo.")}}
        catch(e:IOException) {throw NokiApiException("Se interrumpió el envío. Actualiza tu perfil para comprobar si la foto se guardó.")}
        profileAction(session,"photo_changed")
    }
    suspend fun removeProfilePhoto(session:NokiSession) {
        request("/storage/v1/object/noki-profile-photos","DELETE",JSONObject().put("prefixes",JSONArray().put("${session.userId}/avatar.jpg")),session.accessToken)
        profileAction(session,"photo_changed")
    }

    suspend fun devices(session: NokiSession): List<co.noki.app.domain.DeviceRecord> {
        val rows = JSONArray(request("/rest/v1/noki_devices?select=*&order=updated_at.desc", token = session.accessToken))
        return (0 until rows.length()).map { deviceFromJson(rows.getJSONObject(it)) }
    }
    suspend fun saveDevice(session: NokiSession, device: co.noki.app.domain.DeviceRecord, create: Boolean): co.noki.app.domain.DeviceRecord {
        val body = device.toJson()
        val path: String
        if (create) {
            body.put("user_id", session.userId)
            // Recover an uncertain earlier response without creating another row.
            val existing = JSONArray(request("/rest/v1/noki_devices?id=eq.${device.id}&select=*", token = session.accessToken))
            if (existing.length() > 0) return deviceFromJson(existing.getJSONObject(0))
            path = "/rest/v1/noki_devices"
        } else {
            body.remove("id")
            path = "/rest/v1/noki_devices?id=eq.${device.id}&version=eq.${device.version}"
        }
        val rows = JSONArray(request(path, if (create) "POST" else "PATCH", body, session.accessToken))
        if (rows.length() != 1) throw NokiApiException("Este aparato cambió en otra sesión. Actualiza la lista antes de editarlo.")
        return deviceFromJson(rows.getJSONObject(0))
    }
    suspend fun archiveDevice(session:NokiSession,device:co.noki.app.domain.DeviceRecord)=deviceFromJson(JSONObject(request("/rest/v1/rpc/noki_archive_device","POST",JSONObject().put("device_id",device.id).put("expected_version",device.version),session.accessToken)))

    suspend fun signOut(session: NokiSession) { request("/auth/v1/logout?scope=local", "POST", JSONObject(), session.accessToken) }

    suspend fun nativeAction(session:NokiSession,op:String,args:JSONObject=JSONObject()):JSONObject {
        val result=JSONObject(request("/rest/v1/rpc/noki_native_action","POST",JSONObject().put("op",op).put("args",args),session.accessToken))
        if(result.has("error")) throw NokiApiException(nativeError(result.getString("error")))
        return result
    }
    suspend fun uploadPlacePhoto(session:NokiSession,path:String,bytes:ByteArray)=withContext(Dispatchers.IO) {
        require(bytes.size<=3145728)
        val request=Request.Builder().url(base+"/storage/v1/object/noki-receiver-places/"+path)
            .header("apikey",publishableKey).header("Authorization","Bearer ${session.accessToken}").header("x-upsert","true")
            .post(bytes.toRequestBody("image/jpeg".toMediaType())).build()
        client.newCall(request).execute().use { if(!it.isSuccessful) throw NokiApiException("No pudimos subir una foto. Tu borrador sigue guardado; revisa la conexión y vuelve a intentarlo.") }
    }

    suspend fun directory(): List<CollectionPoint> {
        val array = JSONArray(request("/rest/v1/noki_collection_points?select=*&active=eq.true&order=name.asc"))
        return (0 until array.length()).mapNotNull { index ->
            val item = array.getJSONObject(index)
            if (item.optString("source_url").isBlank() || item.optString("verified_at").isBlank()) null
            else CollectionPoint(item.getString("id"), item.getString("name"), item.getString("address"), item.getString("source_name"),
                item.getString("source_url"), item.getString("verified_at"), item.getDouble("latitude"), item.getDouble("longitude"),
                item.optJSONArray("accepted_categories")?.let { values -> (0 until values.length()).map { values.getString(it) } } ?: emptyList(),
                if (item.isNull("hours_text")) null else item.optString("hours_text"), if (item.isNull("phone")) null else item.optString("phone"), item.optString("accepted_details"), item.optString("location_note"),
                if(item.isNull("receiver_request_id")) null else item.optString("receiver_request_id").takeIf {it.isNotBlank()},
                if(item.isNull("whatsapp")) null else item.optString("whatsapp").takeIf {co.noki.app.domain.ReceiverContact.valid(it)})
        }
    }
}

// Auth may return a numeric HTTP "code" alongside its symbolic "error_code".
internal fun authErrorCode(body:JSONObject):String =
    body.optString("error_code").takeIf {it.isNotBlank()} ?: body.optString("code")

internal fun nativeError(code:String)=when(code) {
    "NOKI_USERNAME_FORMAT"->"Usa entre 3 y 24 letras sin tildes, números o guion bajo, sin espacios."
    "NOKI_USERNAME_TAKEN"->"Ese nombre de usuario ya está en uso. Elige otro."
    "NOKI_USERNAME_WAIT"->"Puedes cambiar el nombre de usuario una vez cada 30 días. Actualiza tu perfil para consultar la fecha."
    "NOKI_ARCHIVED"->"Este aparato ya está en el historial como retirado. No se puede modificar."
    "NOKI_VERSION"->"El aparato cambió en otra sesión. Actualiza la lista antes de continuar."
    "NOKI_SESSION"->"Tu sesión ya no está activa. Cierra sesión y vuelve a entrar."
    "NOKI_ACTIVE"->"Ya tienes una entrega pendiente. Termínala o cancélala antes de crear otra."
    "NOKI_ITEMS"->"Revisa los aparatos: deben estar preparados, sin otra entrega y aceptados por el receptor."
    "NOKI_RECEIVER"->"Este perfil no está habilitado para recibir. Actualiza la lista o elige otro receptor."
    "NOKI_WAIT"->"El resultado se habilita cuando el receptor valida el código y confirma que recibió los aparatos."
    "NOKI_LOCKED"->"Este aparato está en una entrega activa. Cancela esa entrega antes de editarlo."
    "NOKI_HISTORY"->"Este aparato forma parte de una constancia. No se puede borrar o reabrir desde aquí."
    "NOKI_RATE"->"Has hecho varios intentos. Espera 15 minutos antes de comprobar otro código."
    "NOKI_CODE"->"Este código no corresponde a una entrega pendiente para ti. Comprueba el código y el lugar elegido con quien entrega."
    "NOKI_CANNOT_CANCEL"->"La recepción ya fue confirmada. No se puede cancelar; contacta a Noki si hay un error."
    "NOKI_CONSENT"->"Lee y acepta los términos y la privacidad vigentes para continuar."
    "NOKI_REQUEST_LOCKED"->"Tu solicitud ya está en revisión. Actualiza para consultar su estado."
    "NOKI_PHOTOS"->"Falta subir alguna de las tres fotos del lugar. Vuelve a intentarlo."
    "NOKI_LOCATION"->"Confirma la ubicación del lugar en el mapa para poder publicarlo al aprobar la solicitud."
    "NOKI_CONTACT_DETAILS"->"El receptor debe indicar un WhatsApp público con código de país y autorizar su publicación."
    "NOKI_CONTACT"->"Contacta al receptor por WhatsApp y confirma que acepta los aparatos en su estado actual. Después marca la confirmación para generar el código."
    "NOKI_CONTACT_CHANGED"->"El contacto del receptor cambió. Actualiza los receptores, confirma con el nuevo número y vuelve a intentarlo."
    else->"La información cambió o no está completa. Actualiza y revisa los datos antes de continuar."
}
