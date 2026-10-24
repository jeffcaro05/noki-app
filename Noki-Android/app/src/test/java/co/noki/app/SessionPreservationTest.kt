package co.noki.app

import android.os.Looper
import androidx.lifecycle.ViewModelStore
import co.noki.app.data.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/** In-memory fixtures only: never reads the VIVO, encrypted owner data or the real backend. */
@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk=[34])
class SessionPreservationTest {
    private class MemoryStore : KeyValueStore {
        val values=ConcurrentHashMap<String,String>()
        override fun get(name:String)=values[name]
        override fun put(name:String,value:String?) {if(value==null) values.remove(name) else values[name]=value}
    }
    private val store=MemoryStore()
    private val lifecycle=ViewModelStore()
    private val requests=CopyOnWriteArrayList<String>()
    @Volatile private var failProfile=false
    @Volatile private var failTerms=false
    @Volatile private var failRefresh=false
    @Volatile private var deliveryStatus="pending"
    @Volatile private var failReception=false
    private val user="synthetic-existing-user"
    private fun saved(expires:Long=System.currentTimeMillis()/1000+3600)=JSONObject()
        .put("access","synthetic-access").put("refresh","synthetic-refresh").put("user",user)
        .put("email","account@example.invalid").put("expires",expires).toString()
    private fun seed(consent:Boolean=true,expires:Long=System.currentTimeMillis()/1000+3600) {
        store.put("session",saved(expires));if(consent) ConsentStore(store).accept(user)
    }
    private fun responseSession()="""{"access_token":"synthetic-renewed","refresh_token":"synthetic-refresh-renewed","expires_in":3600,"user":{"id":"$user","email":"account@example.invalid","email_confirmed_at":"2026-10-08T00:00:00Z"}}"""
    private fun model():NokiModel {
        val client=OkHttpClient.Builder().addInterceptor {chain ->
            val request=chain.request();val path=request.url.encodedPath;requests.add(path)
            val json=request.body?.let {body->val buffer=okio.Buffer();body.writeTo(buffer);JSONObject(buffer.readUtf8())}
            var status=200
            val body=when(path) {
                "/rest/v1/noki_client_config" -> "[{\"email_otp_enabled\":true}]"
                "/rest/v1/noki_collection_points", "/rest/v1/noki_devices" -> "[]"
                "/rest/v1/rpc/noki_native_action" -> {
                    if(failReception) throw IOException("synthetic offline reception")
                    """{"receivers":[],"deliveries":[{"id":"fixture-delivery","sender_id":"$user","receiver_id":"fixture-receiver","receiver_name":"Fixture","address":"Fixture address","status":"$deliveryStatus","completed_at":${if(deliveryStatus=="completed") "\"2026-10-09T00:00:00Z\"" else "null"},"items":[{"device_id":"fixture-device","description":"Fixture phone","category":"phone","safety_state":"normal"}]}]}"""
                }
                "/rest/v1/rpc/noki_profile_action" -> {
                    when(json?.optString("op")) {
                        "state" -> {
                            if(failProfile) throw IOException("synthetic offline profile")
                            "{\"username\":\"Usuario_existente\",\"has_photo\":false,\"display_name\":\"Persona\",\"can_change_username\":false,\"username_change_available_at\":\"2026-11-07T12:00:00Z\"}"
                        }
                        "accept" -> {if(failTerms) throw IOException("synthetic offline consent");"{}"}
                        else -> "{}"
                    }
                }
                "/auth/v1/verify" -> responseSession()
                "/auth/v1/token" -> {
                    assertEquals("refresh_token",request.url.queryParameter("grant_type"))
                    assertEquals("synthetic-refresh",json?.getString("refresh_token"))
                    if(failRefresh) {status=401;"{\"error_code\":\"refresh_token_not_found\"}"} else responseSession()
                }
                else -> throw AssertionError("Unexpected endpoint: $path")
            }
            Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(status).message("fixture")
                .body(body.toResponseBody("application/json".toMediaType())).build()
        }.build()
        return NokiModel(RuntimeEnvironment.getApplication(),NokiApi(client,"https://noki-test.invalid"),store)
            .also {lifecycle.put("tested",it);await { !it.busy && it.emailAccessReady }}
    }
    private fun await(done:()->Boolean) {
        val deadline=System.nanoTime()+5_000_000_000
        while(System.nanoTime()<deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            if(done()) return
            Thread.sleep(10)
        }
        fail("Timed out waiting for local fixture")
    }
    @After fun cleanUp() {lifecycle.clear();shadowOf(Looper.getMainLooper()).idle()}

    @Test fun recoveryOffersPasswordBeforeHomeWithoutChangingAccount() {
        val model=model()
        model.verifyEmailCode("account@example.invalid","123456",true,setupPassword=true) {};await {!model.busy}
        assertTrue(model.passwordSetupRequested);assertNull(model.accessMessage)
        assertEquals(user,model.session?.userId)
        model.finishPasswordSetup()
        assertFalse(model.passwordSetupRequested);assertNotNull(model.accessMessage)
    }
    @Test fun oversizedUnicodePasswordNeverReachesServerOrChangesSession() {
        seed();val model=model();val original=store.get("session")
        val password="é".repeat(40)
        model.updatePassword(password,password) {fail("Oversized password accepted")};await {!model.busy}
        assertTrue(model.notice!!.contains("acórtala"));assertEquals(original,store.get("session"))
        assertFalse(requests.contains("/auth/v1/user"))
    }
    @Test fun positiveAndNegativeNoticesHaveExplicitDifferentKinds() {
        val model=model();model.success("Guardado");assertTrue(model.noticeSuccess)
        model.notice="Error";assertFalse(model.noticeSuccess)
    }
    @Test fun quietRefreshReflectsReceiverCompletionWithoutSenderWrite() {
        seed();val model=model();val original=store.get("session")
        assertNotNull(model.activeDelivery);deliveryStatus="completed"
        kotlinx.coroutines.runBlocking {model.refreshReceptionQuietly()}
        assertNull(model.activeDelivery);assertFalse(model.busy)
        assertTrue(co.noki.app.domain.DeliveryRules.confirmed("fixture-device",model.outgoing))
        assertEquals(original,store.get("session"))
        assertFalse(requests.any {it.startsWith("/auth/")})
    }
    @Test fun failedQuietRefreshKeepsPendingDeliveryAndSession() {
        seed();val model=model();failReception=true
        kotlinx.coroutines.runBlocking {model.refreshReceptionQuietly()}
        assertNotNull(model.activeDelivery);assertTrue(model.receptionSyncError);assertFalse(model.busy)
        failReception=false
        kotlinx.coroutines.runBlocking {model.refreshReceptionQuietly()}
        assertFalse(model.receptionSyncError);assertEquals(user,model.session?.userId)
    }
    @Test fun profileNetworkFailureNeverSignsOutOrErasesStoredSession() {
        seed();val original=store.get("session");failProfile=true
        val model=model()
        assertEquals(user,model.session?.userId);assertEquals(original,store.get("session"))
        assertFalse(model.profileLoaded);assertFalse(model.needsUsername)
        assertTrue(model.notice!!.contains("Tu sesión se conserva"))
    }
    @Test fun retryLoadsSameProfileWithoutAnotherCodeOrAccount() {
        seed();failProfile=true;val model=model();failProfile=false
        model.reload();await {!model.busy}
        assertEquals(user,model.session?.userId);assertEquals("Usuario_existente",model.username)
        assertTrue(model.profileLoaded);assertTrue(model.canSave);assertFalse(model.needsUsername)
        assertFalse(requests.any {it.startsWith("/auth/")})
    }
    @Test fun changedTermsDoNotLogOutOrLoadPrivateRecordsBeforeConsent() {
        seed(consent=false);val original=store.get("session");val model=model()
        assertEquals(original,store.get("session"));assertEquals(user,model.session?.userId)
        assertFalse(model.legalAccepted);assertFalse(model.profileLoaded)
        assertFalse(requests.contains("/rest/v1/noki_devices"))
        assertFalse(requests.contains("/rest/v1/rpc/noki_profile_action"))
    }
    @Test fun validOtpRemainsSavedEvenWhenProfileLoadFails() {
        val model=model();failProfile=true
        model.verifyEmailCode("account@example.invalid","123456",true) {};await {!model.busy}
        assertEquals(user,model.session?.userId)
        assertEquals(user,JSONObject(store.get("session")!!).getString("user"))
        assertTrue(model.legalAccepted);assertFalse(model.profileLoaded);assertFalse(model.needsUsername)
    }
    @Test fun consentNetworkFailureKeepsAuthenticatedAccountButBlocksPrivateUse() {
        val model=model();failTerms=true
        model.verifyEmailCode("account@example.invalid","123456",true) {};await {!model.busy}
        assertEquals(user,model.session?.userId);assertNotNull(store.get("session"))
        assertFalse(model.legalAccepted);assertFalse(model.canSave)
        assertFalse(ConsentStore(store).accepted(user))
    }
    @Test fun failedRefreshDoesNotSilentlyDeleteAccountOrSession() {
        seed(expires=0);val original=store.get("session");failRefresh=true;val model=model()
        assertEquals(user,model.session?.userId);assertEquals(original,store.get("session"))
        assertFalse(model.canSave)
    }
    @Test fun successfulRefreshKeepsUserIdAndStoresRotatedCredentials() {
        seed(expires=0);val model=model()
        assertEquals(user,model.session?.userId);assertEquals("synthetic-refresh-renewed",model.session?.refreshToken)
        assertEquals(user,JSONObject(store.get("session")!!).getString("user"));assertTrue(model.canSave)
    }
    @Test fun existingSessionCannotBeReplacedByAnotherLoginAttempt() {
        seed();val model=model();val original=store.get("session")
        model.verifyEmailCode("different@example.invalid","123456",true) {};await {!model.busy}
        assertEquals(original,store.get("session"));assertEquals("Usuario_existente",model.username)
        assertFalse(requests.contains("/auth/v1/verify"))
    }
    @Test fun installedVariantCannotEnterPracticeOrCreateSyntheticDeliveries() {
        assertFalse(BuildConfig.PRACTICE_ENABLED);val model=model()
        model.enterDemo(true) {fail("Practice must be unreachable")};await {!model.busy}
        assertFalse(model.demo);assertFalse(model.canSave);assertTrue(model.deliveries.isEmpty())
        assertNull(store.get("practice"));assertNull(store.get("practice_deliveries"))
    }
    @Test fun verifiedReturningAccountShowsSuccessWithoutOverwritingUsername() {
        val model=model()
        model.verifyEmailCode("account@example.invalid","123456",true) {};await {!model.busy}
        assertEquals("Usuario_existente",model.username);assertEquals("Sesión iniciada correctamente",model.accessMessage)
        assertFalse(model.needsUsername);assertFalse(model.canChangeUsername)
    }
}
