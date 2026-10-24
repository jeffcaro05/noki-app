package co.noki.app

import co.noki.app.data.NokiApi
import co.noki.app.data.NokiApiException
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk=[34])
class EmailAccessTest {
    private fun api(code:Int=200,body:String="{}",check:(Request)->Unit={}):NokiApi = NokiApi(OkHttpClient.Builder().addInterceptor { chain ->
        check(chain.request())
        Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(code).message("test")
            .body(body.toResponseBody("application/json".toMediaType())).build()
    }.build(),"https://noki-test.invalid")
    private fun body(request:Request):JSONObject { val buffer=okio.Buffer();request.body!!.writeTo(buffer);return JSONObject(buffer.readUtf8()) }
    private val session="""{"access_token":"synthetic-access","refresh_token":"synthetic-refresh","expires_in":3600,"user":{"id":"test-user","email":"sample@example.com","email_confirmed_at":"2026-10-04T00:00:00Z"}}"""
    @Test fun validPasswordUsesOriginalAccountWithoutOtp()=runBlocking {
        val result=api(body=session,check={request->
            assertEquals("/auth/v1/token",request.url.encodedPath)
            assertEquals("password",request.url.queryParameter("grant_type"))
            assertEquals("sample@example.com",body(request).getString("email"))
            assertEquals(" synthetic password ",body(request).getString("password"))
        }).signIn(" Sample@Example.com "," synthetic password ")
        assertEquals("test-user",result.userId)
    }
    @Test fun numericHttpCodeDoesNotHideInvalidCredentials()=runBlocking {
        try {api(400,"""{"code":400,"error_code":"invalid_credentials","msg":"Invalid login credentials"}""")
            .signIn("sample@example.com","incorrect");fail("Accepted invalid password")}
        catch(expected:NokiApiException) {assertTrue(expected.message!!.contains("Correo o contraseña incorrectos"))}
    }
    @Test fun passwordChangeUsesAuthenticatedUserEndpoint()=runBlocking {
        api(check={request->
            assertEquals("/auth/v1/user",request.url.encodedPath);assertEquals("PUT",request.method)
            assertEquals("Bearer fixture-token",request.header("Authorization"))
            assertEquals("new fixture password",body(request).getString("password"))
            assertFalse(body(request).has("email"))
        }).updatePassword(co.noki.app.data.NokiSession("fixture-token","fixture-refresh","test-user","sample@example.com"),"new fixture password")
    }
    @Test fun reauthenticationFailureExplainsNextStep()=runBlocking {
        try {api(401,"""{"code":401,"error_code":"reauthentication_needed"}""").updatePassword(
            co.noki.app.data.NokiSession("fixture-token","","test-user","sample@example.com"),"new fixture password");fail("Accepted stale auth")}
        catch(expected:NokiApiException) {assertTrue(expected.message!!.contains("código reciente"))}
    }
    @Test fun sendUsesEmailOtpEndpointWithoutPassword()=runBlocking {
        api(check={request -> assertEquals("/auth/v1/otp",request.url.encodedPath);assertEquals("POST",request.method)
            val data=body(request);assertEquals("sample@example.com",data.getString("email"));assertTrue(data.getBoolean("create_user"));assertFalse(data.has("password"))
        }).sendEmailCode(" Sample@Example.com ")
    }
    @Test fun codeVerificationReturnsConfirmedSession()=runBlocking {
        val result=api(body=session,check={request->assertEquals("/auth/v1/verify",request.url.encodedPath);assertEquals("email",body(request).getString("type"));assertEquals("123456",body(request).getString("token"))}).verifyEmailCode("sample@example.com","123456")
        assertEquals("test-user",result.userId)
    }
    @Test fun unconfirmedUserCannotBecomeSession()=runBlocking {
        try {api(body=session.replace("\"2026-10-04T00:00:00Z\"","null")).verifyEmailCode("sample@example.com","123456");fail("Unconfirmed account accepted")}
        catch(expected:NokiApiException){assertTrue(expected.message!!.contains("verificar"))}
    }
    @Test fun expiredCodeExplainsRecovery()=runBlocking {
        try {api(403,"""{"error_code":"otp_expired"}""").verifyEmailCode("sample@example.com","123456");fail("Expired code accepted")}
        catch(expected:NokiApiException){assertTrue(expected.message!!.contains("otro código"))}
    }
    @Test fun mailRestrictionDoesNotPretendCodeWasSent()=runBlocking {
        try {api(400,"""{"error_code":"email_address_not_authorized"}""").sendEmailCode("sample@example.com");fail("Mail restriction ignored")}
        catch(expected:NokiApiException){assertTrue(expected.message!!.contains("todavía no está habilitado"))}
    }
}
