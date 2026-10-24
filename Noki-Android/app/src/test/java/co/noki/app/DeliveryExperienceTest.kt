package co.noki.app

import android.graphics.Bitmap
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModelStore
import co.noki.app.data.*
import co.noki.app.ui.*
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.*
import org.robolectric.annotation.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34],qualifiers="w412dp-h915dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DeliveryExperienceTest {
    @get:Rule val compose=createComposeRule()
    private val models=ViewModelStore()
    @Volatile private var status="pending"
    private var captureView:android.view.View?=null
    @androidx.compose.runtime.Composable private fun CaptureTheme(content:@androidx.compose.runtime.Composable ()->Unit) {
        val view=androidx.compose.ui.platform.LocalView.current
        androidx.compose.runtime.SideEffect {captureView=view.rootView}
        NokiTheme(content)
    }
    private fun model(signedIn:Boolean=false):NokiModel {
        val values=ConcurrentHashMap<String,String>()
        val store=object:KeyValueStore {
            override fun get(name:String)=values[name]
            override fun put(name:String,value:String?) {if(value==null)values.remove(name) else values[name]=value}
        }
        if(signedIn) {
            store.put("session",JSONObject().put("access","fixture").put("refresh","fixture").put("user","sender")
                .put("email","fixture@example.invalid").put("expires",System.currentTimeMillis()/1000+3600).toString())
            ConsentStore(store).accept("sender")
        }
        val client=OkHttpClient.Builder().addInterceptor {chain->
            val r=chain.request()
            val content=when(r.url.encodedPath) {
                "/rest/v1/noki_client_config" -> """[{"email_otp_enabled":true}]"""
                "/rest/v1/noki_collection_points","/rest/v1/noki_devices" -> "[]"
                "/rest/v1/rpc/noki_profile_action" -> """{"username":"Fixture","has_photo":false}"""
                "/rest/v1/rpc/noki_native_action" -> {
                    val body=Buffer().also {r.body!!.writeTo(it)}.readUtf8()
                    if(JSONObject(body).getString("op")=="token") """{"qr":"NOKI1:40000000-0000-4000-8000-000000000001:${"ab".repeat(32)}","code":"A123456789","expires_at":"9999-12-31T23:59:59Z","valid_until":"delivery_closed"}"""
                    else """{"receivers":[],"deliveries":[{"id":"delivery","sender_id":"sender","receiver_id":"receiver","receiver_name":"Lugar de prueba","address":"Dirección de prueba","status":"$status","completed_at":"2026-10-09T00:00:00Z","items":[{"device_id":"phone","description":"Teléfono anterior","category":"phone","safety_state":"normal"}]}]}"""
                }
                else -> error("Unexpected fixture endpoint")
            }
            Response.Builder().request(r).protocol(Protocol.HTTP_1_1).code(200).message("fixture")
                .body(content.toResponseBody("application/json".toMediaType())).build()
        }.build()
        val model=NokiModel(RuntimeEnvironment.getApplication(),NokiApi(client,"https://noki-fixture.invalid"),store)
        models.put("fixture",model)
        val deadline=System.nanoTime()+5_000_000_000
        while(model.busy && System.nanoTime()<deadline) {Shadows.shadowOf(Looper.getMainLooper()).idle();Thread.sleep(10)}
        assertFalse(model.busy);return model
    }
    @After fun cleanup() {models.clear()}
    private fun picture(name:String) {
        compose.runOnIdle {
            val view=checkNotNull(captureView)
            check(view.width>0 && view.height>0)
            val bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val output=File("build/outputs/ui-v072/$name.png");output.parentFile?.mkdirs()
            output.outputStream().use {check(bitmap.compress(Bitmap.CompressFormat.PNG,100,it))}
        }
    }
    @Test fun homeHasDirectPrepareAction() {
        val model=model();var clicked=false
        compose.setContent {CaptureTheme {HomeScreen(model,{},{},{},{},{},prepare={clicked=true})}}
        compose.onNodeWithText("Preparar entrega").performScrollTo().assertIsDisplayed().performClick()
        assertTrue(clicked)
        picture("inicio-preparar")
    }
    @Test fun devicesOfferPreparationEvenBeforeSignIn() {
        val model=model();var clicked=false
        compose.setContent {CaptureTheme {DevicesScreen(model,{clicked=true},{},{})}}
        compose.onNodeWithText("Preparar entrega").performScrollTo().performClick();assertTrue(clicked)
    }
    @Test fun receiverCompletionAutomaticallyShowsResultWithoutSenderConfirmation() {
        val model=model(true)
        compose.setContent {CaptureTheme {DeliveryScreen(model,"delivery",{})}}
        compose.onNodeWithText("Resultado de la entrega").assertDoesNotExist()
        compose.runOnIdle {status="completed";runBlocking {model.refreshReceptionQuietly()}}
        compose.onNodeWithText("Resultado de la entrega").assertIsDisplayed()
        compose.onNodeWithText("Entrega completada").assertExists()
        compose.onNodeWithText("Confirmar que entregué los aparatos").assertDoesNotExist()
        picture("resultado-automatico")
    }
    @Test fun pendingCodeHasNoCountdownOrRenewalAndSurvivesReload() {
        val model=model(true)
        compose.setContent {CaptureTheme {DeliveryScreen(model,"delivery",{})}}
        compose.onNodeWithText("Mostrar QR y código").performScrollTo().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();model.deliveryCode!=null && !model.busy}
        val initial=checkNotNull(model.deliveryCode)
        assertTrue(initial.validWhilePending)
        compose.onNodeWithText("A123456789").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Renovar código").assertDoesNotExist()
        compose.onNodeWithText("Código válido por",substring=true).assertDoesNotExist()
        compose.onNodeWithText("Este QR y código son los mismos",substring=true).assertExists()
        compose.runOnIdle {model.deliveryAction("token","delivery")}
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();!model.busy}
        assertEquals(initial,model.deliveryCode)
        assertEquals(co.noki.app.domain.DeliveryStatus.PENDING,model.deliveries.single().status)
        compose.runOnIdle {status="completed";runBlocking {model.refreshReceptionQuietly()}}
        compose.onNodeWithText("A123456789").assertDoesNotExist()
        compose.onNodeWithText("Entrega completada").assertExists()
    }
    @Test fun reopenedAccountRecoversTheSameQrAndManualCode() {
        fun fetch(model:NokiModel):DeliveryCode {
            model.deliveryAction("token","delivery")
            val deadline=System.nanoTime()+10_000_000_000
            while(model.busy && System.nanoTime()<deadline) {Shadows.shadowOf(Looper.getMainLooper()).idle();Thread.sleep(10)}
            assertFalse(model.busy)
            return checkNotNull(model.deliveryCode)
        }
        val first=fetch(model(true))
        val reopened=model(true)
        assertNull(reopened.deliveryCode)
        assertEquals(first,fetch(reopened))
        assertEquals("sender",reopened.session?.userId)
    }
    @Test fun loginShowsOneErrorAndPasswordOptionAtLargeText() {
        val model=model();model.notice="Correo o contraseña incorrectos."
        compose.setContent {CompositionLocalProvider(LocalDensity provides Density(1f,2f)) {CaptureTheme {
            Column(Modifier.fillMaxSize()) {
                StatusNotice(model.notice.orEmpty())
                Box(Modifier.weight(1f)) {AccountScreen(model,{})}
            }
        }}}
        compose.onAllNodesWithText("Correo o contraseña incorrectos.").assertCountEquals(1)
        compose.onNodeWithContentDescription("No se pudo completar").assertExists()
        compose.onNodeWithText("Iniciar sesión").performScrollTo().assertIsDisplayed()
        picture("acceso-texto-grande")
    }
    @Test fun successUsesCheckmarkInsteadOfErrorIcon() {
        compose.setContent {CaptureTheme {StatusNotice("Contraseña guardada",success=true)}}
        compose.onNodeWithContentDescription("Correcto").assertExists()
        compose.onNodeWithContentDescription("No se pudo completar").assertDoesNotExist()
    }
}
