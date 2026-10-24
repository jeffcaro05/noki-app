package co.noki.app

import android.graphics.Bitmap
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModelStore
import co.noki.app.data.*
import co.noki.app.domain.*
import co.noki.app.ui.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.*
import org.robolectric.annotation.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34],qualifiers="w360dp-h800dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ContactDeliveryExperienceTest {
    @get:Rule val compose=createComposeRule()
    private val models=ViewModelStore()
    @Volatile private var created:JSONObject?=null
    @Volatile private var rejectCreation=false
    @Volatile private var rejectToken=false
    private val device=DeviceRecord("device","Computador anterior",DeviceCategory.COMPUTER,SafetyState.UNKNOWN,FunctionState.UNKNOWN,Decision.DELIVER,
        preparation=Preparation.entries.associateWith {CheckState.NOT_APPLICABLE})
    private var capture:android.view.View?=null
    private fun model(contact:Boolean=true,keep:Boolean=false,owner:Boolean=false,published:Boolean=false):NokiModel {
        val values=ConcurrentHashMap<String,String>()
        val store=object:KeyValueStore {
            override fun get(name:String)=values[name]
            override fun put(name:String,value:String?) {if(value==null)values.remove(name) else values[name]=value}
        }
        store.put("session",JSONObject().put("access","fixture").put("refresh","fixture").put("user","sender")
            .put("email","fixture@example.invalid").put("expires",System.currentTimeMillis()/1000+3600).toString())
        ConsentStore(store).accept("sender")
        val client=OkHttpClient.Builder().addInterceptor {chain->
            val r=chain.request()
            val content=when(r.url.encodedPath) {
                "/rest/v1/noki_client_config" -> "[{\"email_otp_enabled\":true}]"
                "/rest/v1/noki_collection_points" -> if(!published) "[]" else JSONArray().apply {
                    listOf("place-request" to "Lugar de prueba","reference-one" to "Referencia uno","reference-two" to "Referencia dos").forEach {(id,name)->
                        put(JSONObject().put("id",id).put("name",name).put("address","Dirección de prueba").put("source_name","Fuente de prueba")
                            .put("source_url","https://example.invalid").put("verified_at","2026-10-14").put("latitude",6.2).put("longitude",-75.5)
                            .put("accepted_categories",JSONArray().put("phone")).put("receiver_request_id",if(id=="place-request")id else JSONObject.NULL))
                    }
                }.toString()
                "/rest/v1/noki_devices" -> JSONArray().put(device.copy(decision=if(keep)Decision.KEEP else Decision.DELIVER).toJson()).put(device.copy(id="second",description="Otro aparato").toJson()).toString()
                "/rest/v1/rpc/noki_profile_action" -> "{\"username\":\"Fixture\",\"has_photo\":false}"
                "/rest/v1/rpc/noki_native_action" -> {
                    val buffer=okio.Buffer();r.body!!.writeTo(buffer);val body=JSONObject(buffer.readUtf8())
                    when(body.getString("op")) {
                        "state" -> JSONObject().put("application",if(owner)JSONObject().put("id","place-request").put("status","approved") else JSONObject.NULL)
                            .put("receivers",JSONArray().apply {if(!owner)put(JSONObject().put("id","receiver").put("request_id","place-request").put("name","Lugar de prueba").put("address","Dirección de prueba").put("hours","Lunes a viernes").put("conditions","Consultar antes de ir").put("categories",JSONArray().put("phone")).put("whatsapp",if(contact)"573001234567" else JSONObject.NULL))}).put("deliveries",JSONArray().apply {
                            created?.let {c->put(Delivery(c.getString("id"),"sender","receiver","Lugar de prueba","Dirección de prueba",DeliveryStatus.PENDING,
                                listOf(device,device.copy(id="second",description="Otro aparato")).filter {d->(0 until c.getJSONArray("device_ids").length()).any {c.getJSONArray("device_ids").getString(it)==d.id}}.map {DeliveryItem(it.id,it.description,it.category.code,it.safety.code)},contactConfirmed=c.optBoolean("contact_confirmed")).toJson())}
                        }).toString()
                        "create" -> {if(rejectCreation)throw java.io.IOException("fixture unavailable");created=body.getJSONObject("args");JSONObject().put("id",created!!.getString("id")).toString()}
                        "token" -> {if(rejectToken)throw java.io.IOException("fixture unavailable");JSONObject().put("qr","NOKI1:fixture").put("code","ABC1234567").put("expires_at","2099-01-01T00:00:00Z").toString()}
                        else -> error("Unexpected operation")
                    }
                }
                else -> error("Unexpected fixture endpoint ${r.url.encodedPath}")
            }
            Response.Builder().request(r).protocol(Protocol.HTTP_1_1).code(200).message("fixture").body(content.toResponseBody("application/json".toMediaType())).build()
        }.build()
        val model=NokiModel(RuntimeEnvironment.getApplication(),NokiApi(client,"https://noki-fixture.invalid"),store)
        models.put("tested",model)
        val deadline=System.nanoTime()+5_000_000_000
        while((model.busy || (published && model.points.none {it.id=="place-request"})) && System.nanoTime()<deadline){Shadows.shadowOf(Looper.getMainLooper()).idle();Thread.sleep(10)}
        assertTrue(model.receptionReady);return model
    }
    @After fun cleanup(){models.clear()}
    private fun display(model:NokiModel,font:Float=1f,opened:(String)->Unit={}) {
        compose.setContent {CompositionLocalProvider(LocalDensity provides Density(1f,font)){NokiTheme {
            capture=LocalView.current.rootView
            CreateDeliveryScreen(model,"device",opened)
        }}}
        compose.onNodeWithText("Lugar de prueba\nDirección de prueba").performScrollTo().performClick()
    }
    private val agreement="Confirmé con el receptor que puede recibir todos estos aparatos en su estado actual"
    private val button="Generar QR y código"
    @Test fun contactDeclarationEnablesActualCreationAndCode() {
        val model=model();var opened=false;display(model){opened=true}
        compose.onNodeWithText(button).performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().assertIsEnabled().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();opened || (!model.busy && model.notice!=null)}
        assertTrue("Creation failed: ${model.notice}; submitted=${created!=null}; busy=${model.busy}",opened)
        assertTrue(created!!.getBoolean("contact_confirmed"));assertEquals("573001234567",created!!.getString("contact_whatsapp"))
        assertEquals("ABC1234567",model.deliveryCode!!.code)
    }
    @Test fun changingTheApparatusListRequiresFreshAgreement() {
        val model=model();display(model)
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Otro aparato").performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().assertIsNotEnabled()
    }
    @Test fun noContactExplainsHowToContinueInsteadOfSilentDisabledButton() {
        display(model(contact=false))
        compose.onNodeWithText("El receptor todavía no publicó",substring=true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(agreement).assertDoesNotExist()
        compose.onNodeWithText(button).performScrollTo().assertIsNotEnabled()
    }
    @Test fun keepingShowsNoQrExplanation() {
        val model=model(keep=true)
        compose.setContent {NokiTheme {DeviceDetailScreen(model,"device",{},{},{},{})}}
        compose.onNodeWithText("Con este plan no se genera código ni QR",substring=true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Generar código y QR").assertDoesNotExist()
    }
    @Test fun contactControlsRemainReadableAtTwoHundredPercent() {
        display(model(),2f)
        compose.onNodeWithText(agreement).performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText(button).performScrollTo().assertIsDisplayed().assertIsEnabled()
        compose.runOnIdle {
            val view=checkNotNull(capture);val bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val out=File("build/outputs/ui-v073/contacto-200.png");out.parentFile?.mkdirs()
            out.outputStream().use {bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
        }
    }
    @Test fun mapChoiceSelectsExactReceiverAndOpensActualQrScreen() {
        val model=model()
        assertEquals("place-request",model.receivers.single().requestId)
        compose.setContent {NokiTheme {
            var deliveryId by remember {mutableStateOf<String?>(null)}
            if(deliveryId==null) CreateDeliveryScreen(model,"device",{deliveryId=it},requestedReceiver="place-request")
            else DeliveryScreen(model,deliveryId!!,{})
        }}
        compose.onNodeWithText("Lugar de prueba\nDirección de prueba").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();model.deliveryCode!=null}
        compose.onNodeWithText("ABC1234567").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Código QR de esta entrega").assertExists()
        compose.onNodeWithText("Ver lugar y calcular ruta").performScrollTo().assertIsDisplayed()
        assertEquals("receiver",created!!.getString("receiver_id"))
    }
    @Test fun mapRoundTripRetainsGroupedDevicesAndRequiresNewContactDeclaration() {
        val model=model();var opened=false
        compose.setContent {NokiTheme {
            val holder=rememberSaveableStateHolder()
            var map by remember {mutableStateOf(false)}
            var requested by remember {mutableStateOf("")}
            if(!map) holder.SaveableStateProvider("create") {
                CreateDeliveryScreen(model,"device",{opened=true},points={_,_->map=true},requestedReceiver=requested,receiverConsumed={requested=""})
            } else Page {
                DeliveryMapContinuation(CollectionPoint("place-request","Lugar de prueba","Dirección de prueba","Noki","","2026-10-14",6.2,-75.5,listOf("phone"),null,null,receiverRequestId="place-request"),null,device,true,model.receivers,true,{},
                    continueDelivery={requested=it!!.receiverRequestId!!;map=false},chooseAnother={},backToPreparation={})
            }
        }}
        compose.onNodeWithText("Lugar de prueba\nDirección de prueba").performScrollTo().performClick()
        compose.onNodeWithText("Otro aparato").performScrollTo().performClick()
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText("Elegir lugar en el mapa").performScrollTo().performClick()
        compose.onNodeWithText("Elegir este destino").performScrollTo().performClick()
        compose.onNodeWithText("Otro aparato").performScrollTo().assertIsOn()
        compose.onNodeWithText(button).performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();opened}
        assertEquals(2,created!!.getJSONArray("device_ids").length())
    }
    @Test fun unavailableMapChoiceDoesNotSilentlyPickAnotherReceiver() {
        val model=model()
        compose.setContent {NokiTheme {CreateDeliveryScreen(model,"device",{},requestedReceiver="removed-place")}}
        compose.onNodeWithText("Ese lugar ya no está disponible",substring=true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(button).performScrollTo().assertIsNotEnabled()
        assertNull(created)
    }

    @Test fun ownerStillSeesAllThreePublicDestinationsInsideDeliveryPreparation() {
        val model=model(owner=true,published=true)
        assertTrue(model.receivers.isEmpty());assertEquals(3,model.points.size)
        compose.setContent {NokiTheme {CreateDeliveryScreen(model,"device",{})}}
        listOf("Lugar de prueba","Referencia uno","Referencia dos").forEach {name->compose.onNodeWithText(name).performScrollTo().assertIsDisplayed()}
        compose.onNodeWithText("Tu lugar de recepción",substring=true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Receptores con código Noki").assertDoesNotExist()
        compose.onNodeWithText(button).performScrollTo().assertIsNotEnabled()
        assertNull(created)
    }

    @Test fun publishedDestinationCanBeChosenByAnotherAccountAndProducesQr() {
        val model=model(published=true)
        compose.setContent {NokiTheme {
            var id by remember {mutableStateOf<String?>(null)}
            if(id==null) CreateDeliveryScreen(model,"device",{id=it}) else DeliveryScreen(model,id!!,{})
        }}
        listOf("Lugar de prueba","Referencia uno","Referencia dos").forEach {name->compose.onNodeWithText(name).performScrollTo().assertIsDisplayed()}
        compose.onNodeWithText("Lugar de prueba").performScrollTo().performClick()
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();model.deliveryCode!=null}
        compose.onNodeWithText("ABC1234567").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Código QR de esta entrega").assertExists()
    }

    @Test fun mapPlaceCreatesGroupedDeliveryDirectlyThenShowsQrAndRouteAtLargeText() {
        val model=model();var selectedIds=emptyList<String>()
        compose.setContent {CompositionLocalProvider(LocalDensity provides Density(1f,2f)){NokiTheme {
            capture=LocalView.current.rootView
            var screen by remember {mutableStateOf("prepare")}
            var deliveryId by remember {mutableStateOf("")}
            when(screen) {
                "prepare" -> CreateDeliveryScreen(model,"device",{},points={_,ids->selectedIds=ids;screen="map"})
                "map" -> Page {
                    DeliveryMapContinuation(CollectionPoint("place-request","Lugar de prueba","Dirección de prueba","Noki","","2026-10-14",6.2,-75.5,listOf("phone"),null,null,receiverRequestId="place-request"),null,device,true,model.receivers,true,{},
                        continueDelivery={error("Must generate here, not navigate back")},chooseAnother={},backToPreparation={},
                        generateHere={receiver->DestinationDeliveryAction(model,selectedIds,receiver){deliveryId=it;screen="qr"}})
                }
                else -> DeliveryScreen(model,deliveryId,{})
            }
        }}}
        compose.onNodeWithText("Otro aparato").performScrollTo().performClick()
        compose.onNodeWithText("Elegir lugar en el mapa").performScrollTo().performClick()
        assertEquals(listOf("device","second"),selectedIds)
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().assertIsDisplayed().assertIsEnabled()
        compose.runOnIdle {
            val view=checkNotNull(capture);val bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val out=File("build/outputs/ui-v075/destino-generar-200.png");out.parentFile?.mkdirs()
            out.outputStream().use {bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
        }
        compose.onNodeWithText(button).performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();model.deliveryCode!=null}
        compose.onNodeWithText("ABC1234567").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Código QR de esta entrega").assertExists()
        compose.onNodeWithText("Ver lugar y calcular ruta").performScrollTo().assertIsDisplayed()
        assertEquals(2,created!!.getJSONArray("device_ids").length())
    }

    @Test fun creationFailureIsVisibleInsideThePlaceAndCanBeRetried() {
        val model=model();rejectCreation=true;var opened=false
        compose.setContent {NokiTheme {Page {DestinationDeliveryAction(model,listOf("device"),model.receivers.single()){opened=true}}}}
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();!model.busy && model.notice!=null}
        compose.onNodeWithText(model.notice!!).performScrollTo().assertIsDisplayed()
        assertFalse(opened);assertNull(created)
        rejectCreation=false
        compose.onNodeWithText(button).performScrollTo().assertIsEnabled().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();opened}
        assertNotNull(model.deliveryCode)
    }

    @Test fun emptyMapSelectionNeverAddsAnApparatusImplicitly() {
        val model=model()
        compose.setContent {NokiTheme {Page {DestinationDeliveryAction(model,emptyList(),model.receivers.single()) {error("Must not create")}}}}
        compose.onNodeWithText("Primero selecciona los aparatos",substring=true).assertExists()
        compose.onNodeWithText(button).performScrollTo().assertIsNotEnabled()
        assertNull(created)
    }

    @Test fun tokenFailureKeepsOnePendingDeliveryWithRetryOnTheQrScreen() {
        val model=model();rejectToken=true
        compose.setContent {NokiTheme {
            var id by remember {mutableStateOf<String?>(null)}
            if(id==null) Page {DestinationDeliveryAction(model,listOf("device"),model.receivers.single()){id=it}}
            else DeliveryScreen(model,id!!,{})
        }}
        compose.onNodeWithText(agreement).performScrollTo().performClick()
        compose.onNodeWithText(button).performScrollTo().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();created!=null && !model.busy}
        assertEquals(1,model.outgoing.size);assertNull(model.deliveryCode)
        rejectToken=false
        compose.onNodeWithText("Mostrar QR y código").performScrollTo().performClick()
        compose.waitUntil(10000){Shadows.shadowOf(Looper.getMainLooper()).idle();model.deliveryCode!=null}
        compose.onNodeWithText("ABC1234567").performScrollTo().assertIsDisplayed()
        assertEquals(1,model.outgoing.size)
    }
}
