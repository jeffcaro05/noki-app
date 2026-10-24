package co.noki.app

import co.noki.app.data.*
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import android.graphics.Bitmap
import android.net.Uri
import java.io.ByteArrayInputStream
import java.io.File
import androidx.exifinterface.media.ExifInterface

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk=[34])
class ProfileAccessTest {
    private val session=NokiSession("synthetic-access","synthetic-refresh","profile-test","sample@example.invalid")
    private fun api(body:String="{}",check:(Request)->Unit={}):NokiApi=NokiApi(OkHttpClient.Builder().addInterceptor {chain->
        check(chain.request());Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200).message("test").body(body.toResponseBody("application/json".toMediaType())).build()
    }.build(),"https://noki-test.invalid")
    private fun json(r:Request):JSONObject {val b=okio.Buffer();r.body!!.writeTo(b);return JSONObject(b.readUtf8())}
    @Test fun emailAvailabilityUsesServerFlag()=runBlocking {
        assertFalse(api("[{\"email_otp_enabled\":false}]").emailAccessEnabled())
        assertTrue(api("[{\"email_otp_enabled\":true}]").emailAccessEnabled())
        assertFalse(api("[]").emailAccessEnabled())
    }
    @Test fun consentIsExplicitVersionedAndAuthenticated()=runBlocking {
        api(check={r->assertEquals("Bearer synthetic-access",r.header("Authorization"));assertEquals("/rest/v1/rpc/noki_profile_action",r.url.encodedPath)
            val b=json(r);assertEquals("accept",b.getString("op"));val a=b.getJSONObject("args");assertEquals(LegalDocuments.VERSION,a.getString("version"));assertTrue(a.getBoolean("terms"));assertTrue(a.getBoolean("privacy"));assertFalse(a.has("user_id"))
        }).acceptAccountTerms(session)
    }
    @Test fun nameUsesAccountBoundOperation()=runBlocking {
        api(check={r->val b=json(r);assertEquals("name",b.getString("op"));assertEquals("Nombre",b.getJSONObject("args").getString("name"));assertFalse(b.getJSONObject("args").has("user_id"))}).updateName(session,"Nombre")
    }
    @Test fun photoDownloadIsPrivateNotPublic()=runBlocking {
        api(check={r->assertEquals("/storage/v1/object/authenticated/noki-profile-photos/profile-test/avatar.jpg",r.url.encodedPath);assertEquals("Bearer synthetic-access",r.header("Authorization"));assertEquals("no-cache",r.header("Cache-Control"))}).profilePhoto(session)
        Unit
    }
    @Test fun boundedDownloadRejectsOversizedObject() {
        try {readBoundedProfilePhoto(ByteArrayInputStream(ByteArray(524289)));fail("Oversized photo accepted")}
        catch(expected:NokiApiException) {assertTrue(expected.message!!.contains("tamaño"))}
        assertEquals(524288,readBoundedProfilePhoto(ByteArrayInputStream(ByteArray(524288))).size)
    }
    @Test fun photoIsSquareSmallAndStripsLocation()=runBlocking {
        val context=RuntimeEnvironment.getApplication()
        val file=File.createTempFile("noki-profile-test-",".jpg",context.cacheDir)
        try {
            val bitmap=Bitmap.createBitmap(1200,800,Bitmap.Config.ARGB_8888)
            file.outputStream().use {bitmap.compress(Bitmap.CompressFormat.JPEG,90,it)};bitmap.recycle()
            ExifInterface(file).apply {setLatLong(6.2,-75.5);saveAttributes()}
            val original=file.readBytes()
            val result=prepareProfilePhoto(context,Uri.fromFile(file))
            val decoded=decodeProfilePhoto(result)
            assertEquals(512,decoded.width);assertEquals(decoded.width,decoded.height);assertTrue(result.size<=524288)
            assertNull(ExifInterface(ByteArrayInputStream(result)).latLong)
            assertArrayEquals(original,file.readBytes());decoded.recycle()
        } finally {file.delete()}
    }
    @Test fun malformedPhotoCannotBeDecoded() {
        try {decodeProfilePhoto("not-an-image".toByteArray());fail("Malformed photo accepted")}
        catch(expected:NokiApiException) {assertTrue(expected.message!!.contains("foto"))}
    }
}
