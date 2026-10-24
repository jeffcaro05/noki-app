package co.noki.app

import android.os.Looper
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import co.noki.app.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.*
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk=[34])
class DirectorySyncTest {
    private val models=ViewModelStore()
    private val values=ConcurrentHashMap<String,String>()
    private val store=object:KeyValueStore {
        override fun get(name:String)=values[name]
        override fun put(name:String,value:String?) {if(value==null)values.remove(name) else values[name]=value}
    }
    @Volatile private var rows="[]"
    @Volatile private var offline=false
    private fun point(receiver:Boolean)=JSONObject().put("id",if(receiver) "request-1" else "reference-1")
        .put("name",if(receiver) "Lugar aprobado" else "Referencia pública").put("address","Dirección de prueba")
        .put("source_name","Fuente de prueba").put("source_url","https://example.invalid/place")
        .put("verified_at","2026-10-10").put("latitude",6.2762045).put("longitude",-75.552709)
        .put("accepted_categories",JSONArray().put("phone")).put("accepted_details","Solo teléfonos")
        .put("hours_text","Lunes a viernes").put("location_note","Ubicación declarada")
        .put("receiver_request_id",if(receiver) "request-1" else JSONObject.NULL)
        .put("whatsapp",if(receiver) "573001234567" else JSONObject.NULL)
    private fun api()=NokiApi(OkHttpClient.Builder().addInterceptor {chain ->
        val request=chain.request()
        val response=when(request.url.encodedPath) {
            "/rest/v1/noki_client_config" -> "[{\"email_otp_enabled\":true}]"
            "/rest/v1/noki_collection_points" -> {
                assertNull(request.header("Authorization"))
                assertEquals("no-cache",request.header("Cache-Control"))
                assertEquals("eq.true",request.url.queryParameter("active"))
                if(offline) throw IOException("offline fixture")
                rows
            }
            else -> error("Unexpected endpoint")
        }
        Response.Builder().request(request).code(200).message("fixture").protocol(Protocol.HTTP_1_1)
            .body(response.toResponseBody("application/json".toMediaType())).build()
    }.build(),"https://noki-test.invalid")
    private fun await(done:()->Boolean) {
        val deadline=System.nanoTime()+5_000_000_000
        while(System.nanoTime()<deadline) {Shadows.shadowOf(Looper.getMainLooper()).idle();if(done())return;Thread.sleep(10)}
        fail("Directory fixture timed out")
    }
    private fun model():NokiModel=NokiModel(RuntimeEnvironment.getApplication(),api(),store).also {
        models.put("tested",it)
        // Cache persistence runs on IO before the main-thread continuation releases
        // the directory mutex. Wait for startup jobs, not just the cache write,
        // before simulating a subsequent directory update.
        await {!it.busy && it.emailAccessReady && values.containsKey("points") &&
            it.viewModelScope.coroutineContext[Job]!!.children.none { job -> job.isActive }}
    }
    @After fun cleanup(){models.clear();Shadows.shadowOf(Looper.getMainLooper()).idle()}
    @Test fun publicDirectoryIncludesReceiverAndReferenceWithoutLogin()=runBlocking {
        rows=JSONArray().put(point(false)).put(point(true)).toString()
        val result=api().directory()
        assertEquals(2,result.size);assertNull(result[0].receiverRequestId)
        assertEquals("request-1",result[1].receiverRequestId)
        assertEquals("573001234567",result[1].whatsapp);assertNull(result[0].whatsapp)
        assertEquals(6.2762045,result[1].latitude,0.00000001)
        assertEquals(-75.552709,result[1].longitude,0.00000001)
    }
    @Test fun approvalAppearsWithoutRecreatingModel() {
        val model=model();assertTrue(model.points.isEmpty())
        rows=JSONArray().put(point(true)).toString();model.loadPoints()
        await {model.points.size==1};assertNull(model.session)
        assertEquals("request-1",model.points.single().receiverRequestId)
    }
    @Test fun suspensionRemovesPreviouslyPublishedMarker() {
        rows=JSONArray().put(point(true)).toString();val model=model();assertEquals(1,model.points.size)
        rows="[]";model.loadPoints();await {model.points.isEmpty()}
        assertNull(model.directoryNotice)
    }
    @Test fun offlineCacheRetainsReceiverLabelAndWarnsItMayBeStale() {
        rows=JSONArray().put(point(true)).toString();val model=model();offline=true
        model.loadPoints();await {model.directoryNotice!=null}
        assertEquals("request-1",model.points.single().receiverRequestId)
        assertTrue(model.directoryNotice!!.contains("copia guardada"))
        assertNull(model.points.single().phone)
        assertEquals("573001234567",model.points.single().whatsapp)
    }
    @Test fun repeatedUpdatesDoNotDuplicatePlaces() {
        rows=JSONArray().put(point(true)).toString();val model=model()
        repeat(3){val job=model.loadPoints();await {job.isCompleted}}
        assertEquals(1,model.points.size)
    }
}
