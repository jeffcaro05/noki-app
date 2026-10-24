package co.noki.app.data

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.noki.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import co.noki.app.BuildConfig

class NokiModel @JvmOverloads constructor(app: Application, private val api:NokiApi=NokiApi(), private val store:KeyValueStore=SecureStore(app)) : AndroidViewModel(app) {
    private val consents = ConsentStore(store)
    var legalAccepted by mutableStateOf(false); private set
    private var epoch = 0
    private val operationMutex=Mutex()
    private val directoryMutex=Mutex()
    var receptionSyncError by mutableStateOf(false); private set
    var passwordSetupRequested by mutableStateOf(false); private set
    var session by mutableStateOf<NokiSession?>(null); private set
    var demo by mutableStateOf(false); private set
    var profileName by mutableStateOf(""); private set
    var username by mutableStateOf(""); private set
    var canChangeUsername by mutableStateOf(false); private set
    var usernameAvailableAt by mutableStateOf<String?>(null); private set
    var accessMessage by mutableStateOf<String?>(null); private set
    var profileLoaded by mutableStateOf(false); private set
    val needsUsername get()=session!=null && legalAccepted && profileLoaded && username.isBlank() && !busy && !passwordSetupRequested
    var emailAccessReady by mutableStateOf(false); private set
    var profilePhoto by mutableStateOf<android.graphics.Bitmap?>(null); private set
    var hasProfilePhoto by mutableStateOf(false); private set
    var profileNotice by mutableStateOf<String?>(null); private set
    var photoDraft by mutableStateOf<ByteArray?>(null); private set
    var devices by mutableStateOf<List<DeviceRecord>>(emptyList()); private set
    var points by mutableStateOf<List<CollectionPoint>>(bundledDirectory); private set
    var busy by mutableStateOf(true); private set
    private var noticeValue by mutableStateOf<String?>(null)
    var noticeSuccess by mutableStateOf(false); private set
    var notice:String?
        get()=noticeValue
        set(value) {noticeValue=value;noticeSuccess=false}
    fun success(message:String) {notice=message;noticeSuccess=true}
    var directoryNotice by mutableStateOf<String?>(null); private set
    var deliveries by mutableStateOf<List<Delivery>>(emptyList()); private set
    var receivers by mutableStateOf<List<ReceiverPlace>>(emptyList()); private set
    var receiverApplication by mutableStateOf<JSONObject?>(null); private set
    var receptionReady by mutableStateOf(false); private set
    var deliveryCode by mutableStateOf<DeliveryCode?>(null); private set
    var previewDeliveryId by mutableStateOf<String?>(null); private set
    val receiverApproved get() = receiverApplication?.optString("status")=="approved"
    val ownId get()=if(demo) "practice-sender" else session?.userId.orEmpty()
    val outgoing get()=deliveries.filter {it.senderId==ownId}
    val activeDelivery get()=outgoing.firstOrNull {it.active}
    val canSave get() = (demo || (session != null && username.isNotBlank())) && legalAccepted
    init {
        viewModelScope.launch {
            try {
                session = withContext(Dispatchers.IO) { store.get("session") }?.let { raw -> JSONObject(raw).let {
                    NokiSession(it.getString("access"), it.getString("refresh"), it.getString("user"), it.getString("email"), it.getLong("expires"))
                } }
                if (session != null) {
                    legalAccepted=withContext(Dispatchers.IO) {consents.accepted(session!!.userId)}
                    if(legalAccepted) loadDevices()
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) {
                // A network/profile error is NOT a sign-out. Keep the encrypted session intact.
                notice = if(session!=null) "Tu sesión se conserva. No pudimos cargar tus datos; revisa la conexión y pulsa Actualizar en Mi cuenta."
                    else "No pudimos leer tu sesión guardada. Puedes volver a entrar; tus registros en la cuenta no se han borrado."
            }
            finally { busy = false }
        }
        loadPoints()
        refreshEmailCapability()
    }
    private suspend fun persistSession(value: NokiSession?) = withContext(Dispatchers.IO) {
        store.put("session", value?.let { JSONObject().put("access", it.accessToken).put("refresh", it.refreshToken)
            .put("user", it.userId).put("email", it.email).put("expires", it.expiresAt).toString() })
    }
    private suspend fun validSession(): NokiSession {
        requireConsent()
        val current = session ?: throw NokiApiException("Entra a tu cuenta para guardar.")
        if (current.expiresAt > System.currentTimeMillis() / 1000 + 90) return current
        val renewed = api.refresh(current)
        persistSession(renewed); session = renewed
        return renewed
    }
    fun signIn(email: String, password: String, consent: Boolean = false, done: () -> Unit) = action {
        if(!consent) throw NokiApiException("Lee y acepta los términos y la política de privacidad para entrar.")
        if(session!=null) throw NokiApiException("Ya tienes una sesión abierta. Puedes actualizarla desde Mi cuenta.")
        val value = api.signIn(email, password)
        completeSignIn(value,done)
    }
    private suspend fun completeSignIn(value:NokiSession,done:()->Unit,setupPassword:Boolean=false) {
        // Save a successful authentication even if a later profile/consent request loses connection.
        persistSession(value); session=value; demo=false; legalAccepted=false; profileLoaded=false
        devices=emptyList();username="";profileName="";profilePhoto=null;hasProfilePhoto=false;epoch++;clearReception()
        api.acceptAccountTerms(value)
        withContext(Dispatchers.IO) {consents.accept(value.userId)};legalAccepted=true
        loadDevices();done();passwordSetupRequested=setupPassword
        if(username.isNotBlank() && !setupPassword) accessMessage="Sesión iniciada correctamente"
    }
    fun signOut(done: () -> Unit) = action {
        val old = session
        persistSession(null); session = null; demo = false; legalAccepted=false; devices = emptyList(); profileName=""; profilePhoto=null;photoDraft=null;hasProfilePhoto=false;profileNotice=null;epoch++; clearReception()
        username="";usernameAvailableAt=null;canChangeUsername=false;accessMessage=null;profileLoaded=false;passwordSetupRequested=false
        done()
        if (old != null) {
            try { api.signOut(old) } catch (_: Exception) { notice = "La sesión se quitó de este teléfono. Sin conexión no pudimos confirmar su revocación en el servidor." }
        }
    }
    fun enterDemo(consent:Boolean = false, done: () -> Unit) = action {
        if(!BuildConfig.PRACTICE_ENABLED) throw NokiApiException("Para guardar aparatos y preparar una entrega, entra a tu cuenta.")
        if(!consent) throw NokiApiException("Lee y acepta los términos antes de guardar ejemplos en este teléfono.")
        if (session != null) throw NokiApiException("Cierra tu sesión antes de entrar al espacio de prueba.")
        withContext(Dispatchers.IO) {consents.accept("practice")};legalAccepted=true
        val raw = withContext(Dispatchers.IO) { store.get("practice") } ?: "[]"
        val array = JSONArray(raw)
        devices = (0 until array.length()).map { deviceFromJson(array.getJSONObject(it)) }
        demo = true; epoch++; clearReception()
        val deliveryRows=JSONArray(withContext(Dispatchers.IO) {store.get("practice_deliveries")} ?: "[]")
        deliveries=(0 until deliveryRows.length()).map {deliveryFromJson(deliveryRows.getJSONObject(it))}
        receivers=listOf(practiceReceiver); receptionReady=true; done()
    }
    fun reload() = action { loadDevices() }
    fun sendEmailCode(email:String, consent:Boolean=false, sent:()->Unit) = action {
        if(!consent) throw NokiApiException("Se necesita tu aceptación antes de enviar el código.")
        if(!emailAccessReady) throw NokiApiException("El registro por correo todavía no está habilitado. Puedes explorar Noki sin cuenta o entrar con una cuenta existente.")
        api.sendEmailCode(email); sent()
    }
    fun verifyEmailCode(email:String,code:String,consent:Boolean=false,setupPassword:Boolean=false,done:()->Unit) = action {
        if(!consent) throw NokiApiException("Se necesita tu aceptación para verificar la cuenta.")
        if(session!=null) throw NokiApiException("Ya tienes una sesión abierta. Puedes actualizarla desde Mi cuenta.")
        if(!emailAccessReady) throw NokiApiException("La verificación por correo todavía no está habilitada.")
        val value=api.verifyEmailCode(email,code)
        completeSignIn(value,done,setupPassword)
    }
    fun finishPasswordSetup() {
        passwordSetupRequested=false
        if(username.isNotBlank()) accessMessage="Sesión iniciada correctamente"
    }
    fun updatePassword(password:String,confirmation:String,done:()->Unit)=action {
        if(password.length<12 || password.toByteArray(Charsets.UTF_8).size>72) throw NokiApiException("Usa al menos 12 caracteres. Si la contraseña es muy larga, acórtala; algunos símbolos ocupan más espacio.")
        if(password!=confirmation) throw NokiApiException("Las contraseñas no coinciden.")
        api.updatePassword(validSession(),password)
        success("Contraseña guardada. Ya puedes entrar con tu correo y esta contraseña.")
        done()
    }
    fun updateName(name:String,done:()->Unit) = action {
        val trimmed=name.trim()
        if(trimmed.length !in 2..80) throw NokiApiException("Escribe un nombre de entre 2 y 80 caracteres.")
        api.updateName(validSession(),trimmed); profileName=trimmed; done()
    }
    fun refreshEmailCapability()=viewModelScope.launch {emailAccessReady=runCatching {api.emailAccessEnabled()}.getOrDefault(false)}
    fun dismissAccessMessage() {accessMessage=null}
    fun updateUsername(value:String,done:()->Unit={})=action {
        if(!Regex("^[A-Za-z0-9_]{3,24}$").matches(value.trim())) throw NokiApiException(nativeError("NOKI_USERNAME_FORMAT"))
        val first=username.isBlank()
        api.profileAction(validSession(),"username",JSONObject().put("username",value.trim()))
        loadProfile()
        if(first) accessMessage="Registro completado correctamente" else success("Nombre de usuario actualizado.")
        done()
    }
    private suspend fun loadProfile() {
        val generation=epoch
        val auth=validSession()
        val p=api.profileAction(auth,"state")
        if(generation!=epoch) return
        profileName=p.optString("display_name");hasProfilePhoto=p.optBoolean("has_photo");profileNotice=null
        username=if(p.isNull("username")) "" else p.getString("username")
        canChangeUsername=p.optBoolean("can_change_username")
        usernameAvailableAt=if(p.isNull("username_change_available_at")) null else p.getString("username_change_available_at")
        profileLoaded=true
        if(!hasProfilePhoto) {profilePhoto=null;return}
        try {
            val bytes=api.profilePhoto(auth)
            val bitmap=withContext(Dispatchers.IO) {bytes?.let {decodeProfilePhoto(it)}}
            if(generation==epoch) profilePhoto=bitmap
        } catch(e:Exception) {if(generation==epoch) profileNotice="No pudimos cargar tu foto. Pulsa «Actualizar perfil» para reintentar."}
    }
    fun reloadProfile()=action {loadProfile()}
    fun chooseProfilePhoto(uri:android.net.Uri)=action {photoDraft=preparePlacePhoto(getApplication(),uri)}
    fun discardProfilePhoto() {if(!busy) photoDraft=null}
    fun saveProfilePhoto(frame:PhotoFrame)=action {
        val bytes=photoDraft ?: return@action
        api.uploadProfilePhoto(validSession(),frameProfilePhoto(bytes,frame));photoDraft=null;loadProfile();success("Foto de perfil guardada.")
    }
    fun removeProfilePhoto()=action {api.removeProfilePhoto(validSession());profilePhoto=null;hasProfilePhoto=false;success("Foto de perfil eliminada.")}
    private suspend fun loadDevices() {
        if (demo) return
        loadProfile()
        val generation = epoch
        val result = api.devices(validSession())
        if (generation == epoch) {devices = result;loadReception()}
    }
    fun save(record: DeviceRecord, create: Boolean, done: (DeviceRecord) -> Unit) = action {
        requireConsent()
        if(!canSave) throw NokiApiException("Completa tu nombre de usuario en Mi cuenta.")
        if(record.archivedAt!=null) throw NokiApiException(nativeError("NOKI_ARCHIVED"))
        val previous=devices.firstOrNull {it.id==record.id}
        if(deliveries.any {it.active && it.items.any {i->i.deviceId==record.id}}) throw NokiApiException(nativeError("NOKI_LOCKED"))
        if(record.outcome!=null && (record.decision==Decision.DELIVER || record.outcome==Outcome.DELIVERED) && !DeliveryRules.confirmed(record.id,deliveries)) throw NokiApiException(nativeError("NOKI_WAIT"))
        if(record.decision==Decision.DELIVER && record.outcome!=null && record.outcome!=Outcome.DELIVERED) throw NokiApiException(nativeError("NOKI_INPUT"))
        if(previous!=null && DeliveryRules.confirmed(record.id,deliveries) && (record.outcome==null || record.copy(outcome=previous.outcome,completedAt=previous.completedAt)!=previous)) throw NokiApiException(nativeError("NOKI_HISTORY"))
        val saved = if (demo) record.copy(version = record.version + if (create) 0 else 1, updatedAt = Instant.now().toString(),
            completedAt = if (record.outcome == null) null else record.completedAt ?: Instant.now().toString()) else api.saveDevice(validSession(), record, create)
        val next = listOf(saved) + devices.filterNot { it.id == saved.id }
        if (demo) savePractice(next)
        devices = next; done(saved)
    }
    fun delete(record: DeviceRecord, done: () -> Unit) = action {
        requireConsent()
        if(record.completed || DeliveryRules.locked(record.id,deliveries)) throw NokiApiException(nativeError("NOKI_LOCKED"))
        val archived=if(demo)record.copy(archivedAt=Instant.now().toString(),version=record.version+1)else api.archiveDevice(validSession(),record)
        val next = devices.map {if(it.id==record.id)archived else it}
        if (demo) savePractice(next)
        devices = next; done()
    }
    private fun requireConsent() {if(!legalAccepted) throw NokiApiException("Para usar tus registros, revisa los términos y la privacidad en Mi cuenta.")}
    fun acceptCurrentConsent(done:()->Unit) = action {
        val subject=session?.userId ?: if(demo) "practice" else throw NokiApiException("Entra a tu cuenta para continuar.")
        withContext(Dispatchers.IO) {consents.accept(subject)};legalAccepted=true
        if(session!=null) {
            try {api.acceptAccountTerms(validSession())} catch(e:Exception){legalAccepted=false;withContext(Dispatchers.IO){consents.revoke(subject)};throw e}
            loadDevices()
        }
        if(session!=null) {if(username.isNotBlank()) accessMessage="Sesión lista. Gracias por revisar las condiciones."} else done()
    }
    fun revokeConsent() = action {
        val old=session
        val subject=old?.userId ?: if(demo) "practice" else null
        if(subject!=null) withContext(Dispatchers.IO) {consents.revoke(subject)}
        persistSession(null);session=null;demo=false;legalAccepted=false;devices=emptyList();profileName="";profilePhoto=null;photoDraft=null;hasProfilePhoto=false;profileNotice=null;epoch++;clearReception()
        username="";usernameAvailableAt=null;canChangeUsername=false;accessMessage=null;profileLoaded=false;passwordSetupRequested=false
        if(old!=null) {
            try {
                val auth=if(old.expiresAt > System.currentTimeMillis()/1000+90) old else api.refresh(old)
                api.profileAction(auth,"revoke")
                api.signOut(auth)
            } catch(e:CancellationException) {throw e}
            catch(_:Exception) {notice="Se retiró el permiso y se cerró la sesión en este teléfono. No pudimos confirmar todos los cambios en el servidor; puedes solicitarlos por el correo de contacto."}
        }
    }
    private suspend fun savePractice(records: List<DeviceRecord>) = withContext(Dispatchers.IO) {
            store.put("practice", JSONArray().apply { records.forEach { put(it.toJson().put("version", it.version).put("completed_at", it.completedAt ?: JSONObject.NULL).put("archived_at",it.archivedAt ?: JSONObject.NULL).put("updated_at", it.updatedAt)) } }.toString())
    }
    private fun clearReception() {deliveries=emptyList();receivers=emptyList();receiverApplication=null;receptionReady=false;deliveryCode=null;previewDeliveryId=null}
    private suspend fun loadReception() {
        if(demo) return
        val generation=epoch
        val state=api.nativeAction(validSession(),"state")
        if(generation!=epoch) return
        receiverApplication=state.optJSONObject("application")
        val places=state.getJSONArray("receivers")
        receivers=(0 until places.length()).map {val p=places.getJSONObject(it);val c=p.getJSONArray("categories");ReceiverPlace(p.getString("id"),p.getString("name"),p.getString("address"),p.getString("hours"),(0 until c.length()).map {c.getString(it)},p.getString("conditions"),p.optString("whatsapp").takeIf {it.isNotBlank() && it!="null"},p.optString("request_id").takeIf {it.isNotBlank() && it!="null"})}
        val rows=state.getJSONArray("deliveries");deliveries=(0 until rows.length()).map {deliveryFromJson(rows.getJSONObject(it))}
        receptionReady=true;receptionSyncError=false
        if(deliveries.none {it.id==deliveryCode?.id && it.status==DeliveryStatus.PENDING}) deliveryCode=null
    }
    // Reads only, while visible. Serialized with user actions and token rotation.
    suspend fun refreshReceptionQuietly() {
        if(demo || !canSave || busy || !operationMutex.tryLock()) return
        try {loadReception()}
        catch(e:CancellationException) {throw e}
        catch(_:Exception) {receptionSyncError=true}
        finally {operationMutex.unlock()}
    }
    fun reloadReception()=action {requireConsent();loadReception()}
    fun completeReceiverLocation(latitude:Double,longitude:Double,done:()->Unit)=action {
        val request=receiverApplication ?: throw NokiApiException("Consulta primero el estado de tu solicitud.")
        api.nativeAction(validSession(),"request_location",JSONObject().put("id",request.getString("id"))
            .put("latitude",latitude).put("longitude",longitude).put("confirmed",true))
        loadReception();refreshDirectoryQuietly();success("Ubicación guardada. Tu lugar ya aparece en Puntos.");done()
    }
    fun submitReceiver(body:JSONObject,photos:List<android.net.Uri>,done:()->Unit)=action {
        if(demo) throw NokiApiException("Las solicitudes de receptor requieren una cuenta verificada; el espacio de prueba no envía fotos.")
        if(photos.size!=3) throw NokiApiException("Selecciona las tres fotos indicadas.")
        val auth=validSession()
        val draft=api.nativeAction(auth,"request_draft",body)
        try {
            api.nativeAction(auth,"request_contact",JSONObject().put("id",draft.getString("id"))
                .put("whatsapp",body.getString("public_whatsapp")).put("publish_contact",true))
            api.nativeAction(auth,"request_location",JSONObject().put("id",draft.getString("id"))
                .put("latitude",body.getDouble("latitude")).put("longitude",body.getDouble("longitude")).put("confirmed",true))
            photos.forEachIndexed {index,uri ->
                val bytes=preparePlacePhoto(getApplication(),uri)
                api.uploadPlacePhoto(auth,"${auth.userId}/${draft.getString("id")}/${index+1}.jpg",bytes)
            }
            api.nativeAction(auth,"request_submit")
            loadReception();done()
        } catch(e:Exception) {runCatching {loadReception()};throw e}
    }
    private suspend fun persistDeliveries(next:List<Delivery>) {
        withContext(Dispatchers.IO) {store.put("practice_deliveries",JSONArray().apply {next.forEach {put(it.toJson())}}.toString())};deliveries=next
    }
    fun updateReceiverContact(whatsapp:String,done:()->Unit)=action {
        val request=receiverApplication ?: throw NokiApiException("Consulta primero tu solicitud.")
        api.nativeAction(validSession(),"request_contact",JSONObject().put("id",request.getString("id"))
            .put("whatsapp",whatsapp).put("publish_contact",true))
        loadReception();refreshDirectoryQuietly();success("WhatsApp de contacto actualizado.");done()
    }
    fun createDelivery(ids:List<String>,receiverId:String,id:String,contactConfirmed:Boolean=false,done:(String)->Unit)=action {
        requireConsent()
        val selected=devices.filter {it.id in ids};val receiver=receivers.firstOrNull {it.id==receiverId} ?: throw NokiApiException(nativeError("NOKI_RECEIVER"))
        if(!DeliveryRules.canCreate(selected,outgoing,receiver,contactConfirmed) || selected.size!=ids.size) throw NokiApiException(if(activeDelivery!=null) nativeError("NOKI_ACTIVE") else if(DeliveryRules.needsContact(selected,receiver)) nativeError("NOKI_CONTACT") else nativeError("NOKI_ITEMS"))
        if(demo) {
            val item=Delivery(id,ownId,receiver.id,receiver.name,receiver.address,DeliveryStatus.PENDING,selected.map {DeliveryItem(it.id,it.description,it.category.code,it.safety.code)},Instant.now().toString())
            persistDeliveries(listOf(item)+deliveries)
            deliveryCode=practiceCode(id)
        } else {
            try {api.nativeAction(validSession(),"create",JSONObject().put("id",id).put("receiver_id",receiverId).put("device_ids",JSONArray(ids)).put("contact_confirmed",contactConfirmed).put("contact_whatsapp",receiver.whatsapp ?: JSONObject.NULL))}
            catch(e:Exception) {runCatching {loadReception()};throw e}
            loadReception()
            try {
                val code=api.nativeAction(validSession(),"token",JSONObject().put("id",id))
                deliveryCode=deliveryCodeFromJson(id,code)
            } catch(e:CancellationException) {throw e}
            catch(_:Exception) {notice="La entrega quedó pendiente. No pudimos mostrar el código; abre esta entrega y pulsa «Mostrar QR y código». No necesitas crear otra."}
        }
        done(id)
    }
    fun deliveryAction(op:String,id:String="",code:String="",reason:String="",done:()->Unit={})=action {
        requireConsent()
        if(demo) {
            val item=deliveries.firstOrNull {it.id==id || (op=="preview" && it.id==deliveryCode?.id)} ?: throw NokiApiException(nativeError("NOKI_STATE"))
            when(op) {
                "token" -> {
                    if(item.status!=DeliveryStatus.PENDING) throw NokiApiException(nativeError("NOKI_STATE"))
                    deliveryCode=deliveryCode?.takeIf {it.id==id} ?: practiceCode(id)
                }
                "preview" -> {
                    val current=deliveryCode ?: throw NokiApiException(nativeError("NOKI_CODE"))
                    if(item.status!=DeliveryStatus.PENDING || !current.isDisplayable() || (code!=current.qr && code.uppercase()!=current.code)) throw NokiApiException(nativeError("NOKI_CODE"))
                    previewDeliveryId=item.id
                }
                "receive" -> {
                    if(item.status!=DeliveryStatus.PENDING || previewDeliveryId!=id || deliveryCode?.isDisplayable()!=true) throw NokiApiException(nativeError("NOKI_CODE"))
                    persistDeliveries(deliveries.map {if(it.id==id)it.copy(status=DeliveryStatus.COMPLETED,completedAt=Instant.now().toString())else it});deliveryCode=null;previewDeliveryId=null
                }
                "complete" -> {
                    if(item.status!=DeliveryStatus.RECEIVED) throw NokiApiException(nativeError("NOKI_WAIT"))
                    persistDeliveries(deliveries.map {if(it.id==id)it.copy(status=DeliveryStatus.COMPLETED,completedAt=Instant.now().toString())else it})
                }
                "cancel" -> {
                    if(item.status!=DeliveryStatus.PENDING) throw NokiApiException(nativeError("NOKI_CANNOT_CANCEL"))
                    persistDeliveries(deliveries.map {if(it.id==id)it.copy(status=DeliveryStatus.CANCELLED)else it});deliveryCode=null;previewDeliveryId=null
                }
            }
        } else {
            try {
                val result=api.nativeAction(validSession(),op,JSONObject().put("id",id).put("code",code).put("reason",reason))
                if(op=="token") deliveryCode=deliveryCodeFromJson(id,result)
                if(op=="preview") previewDeliveryId=result.getString("id")
                loadReception()
            } catch(e:Exception) {runCatching {loadReception()};throw e}
        }
        done()
    }
    private fun practiceCode(id:String):DeliveryCode {
        val random=java.security.SecureRandom();fun randomHex(size:Int)=ByteArray(size).also {random.nextBytes(it)}.joinToString("") {"%02x".format(it)}
        return DeliveryCode(id,"NOKI-PRACTICE:$id:${randomHex(32)}",randomHex(5).uppercase(),"",validWhilePending=true)
    }
    private fun action(block: suspend () -> Unit) {
        if (busy) return
        busy = true; notice = null
        viewModelScope.launch {
            try { operationMutex.withLock {block()} }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { notice = if (e is NokiApiException) e.message else "No pudimos guardar el cambio. Tus datos anteriores siguen disponibles. Inténtalo de nuevo." }
            finally { busy = false }
        }
    }
    fun loadPoints() = viewModelScope.launch {refreshDirectoryQuietly()}
    suspend fun refreshDirectoryQuietly() {
        if(!directoryMutex.tryLock()) return
        try {
            points = api.directory()
            withContext(Dispatchers.IO) { store.put("points", JSONArray().apply { points.forEach { p -> put(JSONObject().put("id",p.id).put("name",p.name).put("address",p.address).put("source",p.source).put("url",p.sourceUrl).put("date",p.verifiedAt).put("lat",p.latitude).put("lng",p.longitude).put("categories",JSONArray(p.categories)).put("hours",p.hours).put("phone",p.phone).put("details",p.details).put("location",p.locationNote).put("receiver",p.receiverRequestId ?: JSONObject.NULL).put("whatsapp",p.whatsapp ?: JSONObject.NULL)) } }.toString()) }
            directoryNotice = null
        } catch (e: CancellationException) { throw e }
        catch (_: Exception) {
            runCatching {
                val raw = withContext(Dispatchers.IO) { store.get("points") } ?: return@runCatching
                val items = JSONArray(raw)
                val cached = (0 until items.length()).map { i -> val p=items.getJSONObject(i); val cats=p.getJSONArray("categories")
                    CollectionPoint(p.getString("id"),p.getString("name"),p.getString("address"),p.getString("source"),p.getString("url"),p.getString("date"),p.getDouble("lat"),p.getDouble("lng"),(0 until cats.length()).map { cats.getString(it) },if(p.isNull("hours")) null else p.optString("hours"),if(p.isNull("phone")) null else p.optString("phone"),p.getString("details"),p.getString("location"),if(p.isNull("receiver")) null else p.optString("receiver").takeIf {it.isNotBlank()},if(p.isNull("whatsapp"))null else p.optString("whatsapp").takeIf(ReceiverContact::valid)) }
                points = cached
            }
            directoryNotice = if (points.isEmpty()) "No pudimos cargar los lugares. Revisa tu conexión y vuelve a intentar." else "Mostrando la última copia guardada. Los horarios pueden cambiar; confirma antes de ir."
        } finally {directoryMutex.unlock()}
    }
}
