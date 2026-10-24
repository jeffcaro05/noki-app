package co.noki.app

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.noki.app.data.SecureStore
import org.junit.*
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class JourneyFlowTest {
    companion object {
        @JvmStatic @AfterClass fun restoreWelcomeForOwner() {
            if(InstrumentationRegistry.getInstrumentation().targetContext.packageName!="co.noki.app.qa") return
            InstrumentationRegistry.getInstrumentation().targetContext.getSharedPreferences("noki_preferences",android.content.Context.MODE_PRIVATE)
                .edit().putBoolean("welcomed",false).commit()
        }
    }
    @get:Rule val ui=createAndroidComposeRule<MainActivity>()
    @Before fun keepTestScreenAwake() {
        Assert.assertEquals("co.noki.app.qa",InstrumentationRegistry.getInstrumentation().targetContext.packageName)
        cleanTestRecords()
        ui.activityRule.scenario.onActivity { it.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
        ui.waitUntil(20000) { runCatching { ui.onAllNodesWithText("Inicio").fetchSemanticsNodes().isNotEmpty() || ui.onAllNodesWithText("Explorar sin cuenta").fetchSemanticsNodes().isNotEmpty() }.getOrDefault(false) }
        if(ui.onAllNodesWithText("Explorar sin cuenta").fetchSemanticsNodes().isNotEmpty()) ui.onNodeWithText("Explorar sin cuenta").performScrollTo().performClick()
        ui.waitUntil(20000) { runCatching { ui.onAllNodesWithText("Inicio").fetchSemanticsNodes().isNotEmpty() }.getOrDefault(false) }
    }
    @After fun cleanTestRecords() {
        if(InstrumentationRegistry.getInstrumentation().targetContext.packageName!="co.noki.app.qa") return
        val store=SecureStore(InstrumentationRegistry.getInstrumentation().targetContext)
        val raw=store.get("practice") ?: return
        val records=org.json.JSONArray(raw)
        val kept=org.json.JSONArray()
        for(i in 0 until records.length()) {
            val record=records.getJSONObject(i)
            if(record.optString("description") != "Teléfono de prueba automatizada") kept.put(record)
        }
        if(kept.length() != records.length()) store.put("practice",kept.toString())
        val handoffs=org.json.JSONArray(store.get("practice_deliveries") ?: "[]")
        val keptHandoffs=org.json.JSONArray()
        for(i in 0 until handoffs.length()) {
            val h=handoffs.getJSONObject(i);val items=h.getJSONArray("items")
            if((0 until items.length()).none {items.getJSONObject(it).optString("description")=="Teléfono de prueba automatizada"}) keptHandoffs.put(h)
        }
        if(keptHandoffs.length()!=handoffs.length()) store.put("practice_deliveries",keptHandoffs.toString())
    }
    private fun acceptLegal() {
        tap("Acepto los términos y condiciones")
        tap("Autorizo el tratamiento de mis datos según la política de privacidad")
        tap("Aceptar y continuar")
    }
    private fun tap(text:String) {
        ui.waitUntil(20000) { ui.onAllNodesWithTag("OperationInProgress").fetchSemanticsNodes().isEmpty() }
        waitText(text)
        val node=ui.onNodeWithText(text)
        ui.waitUntil(20000) {runCatching {node.assertIsEnabled();true}.getOrDefault(false)}
        runCatching { node.performScrollTo() }
        node.performClick(); ui.waitForIdle()
        ui.waitUntil(20000) { ui.onAllNodesWithTag("OperationInProgress").fetchSemanticsNodes().isEmpty() }
    }
    private fun waitText(text:String) { try { ui.waitUntil(25000) { ui.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() } }
        catch(e:Throwable) { capture("test-error"); ui.onRoot().printToLog("NokiJourneyTest"); throw e } }
    private fun capture(name:String) {
        ui.waitForIdle()
        val bitmap=InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        Assert.assertNotNull(bitmap)
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        File(context.getExternalFilesDir(null),"noki-$name.png").outputStream().use { bitmap!!.compress(Bitmap.CompressFormat.PNG,100,it) }
    }
    @Test fun practiceDeliveryRequiresPreparationCodeAndBothConfirmations() {
        ui.waitUntil(15000) { ui.onAllNodesWithText("Orientar mi dispositivo").fetchSemanticsNodes().isNotEmpty() }
        tap("Orientar mi dispositivo"); tap("Teléfono o tableta"); tap("Continuar")
        tap("No he notado algo así"); tap("Continuar"); tap("Sí, funciona"); tap("Ver orientación")
        tap("Guardar y preparar mi aparato")
        tap("Probar sin cuenta"); tap("Entrar a la prueba"); acceptLegal()
        waitText("Nombre del aparato")
        ui.onNodeWithText("Nombre del aparato").performTextInput("Teléfono de prueba automatizada")
        tap("Pasar a decisión")
        waitText("¿Qué te gustaría hacer?")
        tap("Llevarlo a recolección")
        tap("Elegir un punto")
        waitText("El Tesoro · Recolección Respel")
        tap("El Tesoro · Recolección Respel")
        tap("Guardar como lugar de referencia")
        waitText("¿Qué te gustaría hacer?")
        ui.onNodeWithText("Resultado").assertIsNotEnabled()
        tap("Pasar a preparación")
        ui.waitUntil(20000) { ui.onAllNodesWithTag("OperationInProgress").fetchSemanticsNodes().isEmpty() }
        ui.onNodeWithText("Generar código y QR").assertIsNotEnabled()
        listOf("BACKUP","ACCOUNTS","MEDIA","ERASURE","DESTINATION").forEach {key ->
            ui.onNode(hasText("Hecho") and hasAnyAncestor(hasTestTag("prep-$key"))).performScrollTo().performClick()
            ui.waitForIdle();ui.waitUntil(20000) { ui.onAllNodesWithTag("OperationInProgress").fetchSemanticsNodes().isEmpty() }
        }
        capture("preparacion-v05")
        tap("Generar código y QR")
        tap("Receptor de ejemplo\nLugar ficticio · no te desplaces")
        tap("Generar QR y código")
        val code=ui.onAllNodes(SemanticsMatcher("Delivery code") {node->node.config.getOrNull(androidx.compose.ui.semantics.SemanticsProperties.Text)?.any {it.text.matches(Regex("^[0-9A-F]{10}$"))}==true}).fetchSemanticsNodes().first().config[androidx.compose.ui.semantics.SemanticsProperties.Text].first().text
        ui.onNodeWithContentDescription("Código QR de esta entrega").performScrollTo().assertIsDisplayed()
        val explanation=ui.onNodeWithText("Muestra este código al receptor",substring=true).fetchSemanticsNode().boundsInRoot
        val qrBounds=ui.onNodeWithContentDescription("Código QR de esta entrega").fetchSemanticsNode().boundsInRoot
        Assert.assertTrue("El QR no puede invadir el texto anterior",qrBounds.top>=explanation.bottom)
        capture("codigo-v05")
        tap("Probar recepción de este ejemplo")
        ui.onNodeWithText("Confirmar recepción").assertIsNotEnabled()
        ui.onNodeWithText("Código de entrega").performScrollTo().performTextInput(code)
        tap("Comprobar código");tap("Confirmar recepción");tap("Sí, los recibí")
        ui.onNodeWithText("Confirmar que entregué los aparatos").assertDoesNotExist()
        capture("entrega-confirmada-v05")
        tap("Ver aparato: Teléfono de prueba automatizada")
        waitText("Entrega realizada");capture("resultado-v05")
        ui.onNodeWithText("Reabrir para corregir").assertDoesNotExist()
        tap("Salir")
        waitText("Orientar mi dispositivo")
    }
    @Test fun mapDirectoryAndAllRootTabsWork() {
        tap("Puntos")
        waitText("El Tesoro · Recolección Respel")
        ui.onNodeWithTag("NokiNativeMap").performScrollTo()
        try { ui.waitUntil(30000) { ui.onAllNodesWithTag("NokiMapRendered").fetchSemanticsNodes().isNotEmpty() } }
        catch(e:Throwable) { capture("map-error"); ui.onRoot().printToLog("NokiJourneyTest"); throw e }
        capture("mapa-v02")
        tap("Lista"); tap("Monterrey · Centro de Acopio Ambiental")
        ui.onNodeWithText("Horario publicado").assertExists()
        tap("Cerrar ficha")
        tap("Mis aparatos")
        ui.onNodeWithText("Tus pendientes y tu historial").assertExists()
        tap("Aprende")
        ui.onNodeWithText("Entiende tus opciones").assertExists()
        tap("Inicio")
        capture("inicio-v02")
    }
    @Test fun retiredDraftMovesToHistoryWithoutInventingDelivery() {
        tap("Orientar mi dispositivo");tap("Teléfono o tableta");tap("Continuar")
        tap("No he notado algo así");tap("Continuar");tap("Sí, funciona");tap("Ver orientación")
        tap("Guardar y preparar mi aparato");tap("Probar sin cuenta");tap("Entrar a la prueba");acceptLegal()
        waitText("Nombre del aparato")
        ui.onNodeWithText("Nombre del aparato").performTextInput("Teléfono de prueba automatizada")
        tap("Pasar a decisión")
        tap("Retirar de pendientes");tap("Retirar y guardar en historial")
        tap("Historial")
        ui.onNodeWithText("Retirado por ti · sin entrega").performScrollTo().assertIsDisplayed()
        capture("historial-retirado-v070")
        tap("Teléfono de prueba automatizada")
        ui.onNodeWithText("No cuenta como entrega ni reciclaje.",substring=true).assertExists()
        ui.onNodeWithText("Guardar resultado").assertDoesNotExist()
        tap("Salir")
    }
    @Test fun secureStoreRoundTripUsesSeparateKeys() {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val store=SecureStore(context)
        val name="test_encryption_${System.nanoTime()}"
        try {
            store.put(name,"temporary-noki-test")
            Assert.assertEquals("temporary-noki-test",store.get(name))
            val raw=context.getSharedPreferences("noki_secure_v1",android.content.Context.MODE_PRIVATE).getString(name,null)!!
            Assert.assertFalse(raw.contains("temporary-noki-test"))
        } finally { store.put(name,null) }
    }
    @Test fun manualOriginCalculatesRealRouteWithoutRequestingGps() {
        tap("Puntos")
        tap("Elegir punto de partida")
        tap("Confirmar punto de partida")
        waitText("Punto de partida elegido en el mapa")
        tap("El Tesoro · Recolección Respel")
        tap("Calcular ruta en carro")
        tap("Calcular")
        ui.waitUntil(30000) { ui.onAllNodesWithText("Ruta en carro a",substring=true).fetchSemanticsNodes().isNotEmpty() }
        ui.onNodeWithText("Mapa de puntos").assertExists()
        ui.onNodeWithTag("NokiNativeMap").assertIsDisplayed()
        capture("ruta-v02")
    }
    @Test fun firstWelcomeAndCancelledAccessKeepOrientation() {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("noki_preferences",android.content.Context.MODE_PRIVATE).edit().putBoolean("welcomed",false).commit()
        ui.activityRule.scenario.recreate()
        waitText("¿Tienes aparatos que ya no usas?")
        capture("bienvenida-v03")
        tap("Entrar o crear cuenta")
        waitText("Entra para guardar tu progreso")
        ui.onNodeWithContentDescription("Volver").performClick()
        tap("Orientar mi dispositivo");tap("Teléfono o tableta");tap("Continuar")
        tap("No he notado algo así");tap("Continuar");tap("Sí, funciona");tap("Ver orientación")
        tap("Guardar y preparar mi aparato")
        waitText("Entra para guardar tu progreso")
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        waitText("Guarda tu orientación")
        ui.onNodeWithText("Todavía puede tener una segunda vida",substring=true).assertExists()
        tap("Entrar o crear cuenta")
        waitText("Entra para guardar tu progreso")
        tap("Probar sin cuenta");tap("Entrar a la prueba"); acceptLegal()
        waitText("Nombre del aparato")
        ui.onNodeWithText("Todavía puede tener una segunda vida",substring=true).assertExists()
        tap("Salir")
    }
    @Test fun contextualAccessSettlesAfterRepeatedFastFlings() {
        tap("Orientar mi dispositivo");tap("Teléfono o tableta");tap("Continuar")
        tap("No he notado algo así");tap("Continuar");tap("Sí, funciona");tap("Ver orientación")
        tap("Guardar y preparar mi aparato");waitText("Entra para guardar tu progreso")
        val scroll=ui.onAllNodesWithTag("PageScroll").onLast()
        repeat(8) {scroll.performTouchInput {swipeUp(durationMillis=80)};ui.waitForIdle()}
        val before=scroll.fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.VerticalScrollAxisRange].value()
        Thread.sleep(750);ui.waitForIdle()
        val after=scroll.fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.VerticalScrollAxisRange].value()
        Assert.assertEquals("El formulario debe quedar estable al llegar al final",before,after,1f)
        ui.onNodeWithText("Acerca de Noki").assertIsDisplayed()
        capture("acceso-sin-rebote-v05")
        tap("Volver sin perder mi progreso");ui.onNodeWithText("Guarda tu orientación").assertExists()
    }
}
