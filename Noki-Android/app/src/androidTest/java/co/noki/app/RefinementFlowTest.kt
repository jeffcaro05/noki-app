package co.noki.app

import android.content.pm.ActivityInfo
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.noki.app.ui.*
import co.noki.app.data.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class RefinementFlowTest {
    @get:Rule val ui=createAndroidComposeRule<MainActivity>()
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var initial:AccessibilityOptions
    @Before fun prepare() {
        Assert.assertEquals("co.noki.app.qa",context.packageName)
        initial=AppPreferences(context).options
        Assert.assertNull("Pruebas públicas requieren sesión cerrada; no se altera una cuenta abierta.",SecureStore(context).get("session"))
        ui.activityRule.scenario.onActivity {it.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)}
        ui.waitUntil(20000) {ui.onAllNodesWithText("Explorar sin cuenta").fetchSemanticsNodes().isNotEmpty() || ui.onAllNodesWithText("Inicio").fetchSemanticsNodes().isNotEmpty()}
        if(ui.onAllNodesWithText("Explorar sin cuenta").fetchSemanticsNodes().isNotEmpty()) tap("Explorar sin cuenta")
    }
    @After fun restore() {AppPreferences(context).update(initial)}
    private fun tap(label:String) {
        ui.waitUntil(20000) {ui.onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty()}
        val node=ui.onAllNodesWithText(label).onLast()
        runCatching {node.performScrollTo()}
        node.performClick();ui.waitForIdle()
    }
    private fun capture(name:String) {
        ui.waitForIdle()
        val screenshot=InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        Assert.assertNotNull(screenshot)
        File(context.getExternalFilesDir(null),"noki-v04-$name.png").outputStream().use {screenshot!!.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
    }
    private fun settings() {ui.onNodeWithContentDescription("Abrir menú").performClick();tap("Accesibilidad")}
    @Test fun appearancePersistsDarkAndLightWithoutRestart() {
        settings();tap("Oscuro")
        ui.onNodeWithText("Oscuro").assertIsSelected()
        Assert.assertEquals(AppAppearance.DARK,AppPreferences(context).options.appearance)
        capture("apariencia-oscura")
        ui.onNodeWithContentDescription("Volver").performClick()
        capture("inicio-oscuro")
        ui.activityRule.scenario.recreate()
        Assert.assertEquals(AppAppearance.DARK,AppPreferences(context).options.appearance)
        settings();tap("Claro")
        ui.onNodeWithContentDescription("Volver").performClick()
        capture("inicio-claro")
    }
    @Test fun consentIsExplicitAndDecliningKeepsPublicAccess() {
        ui.onNodeWithContentDescription("Mi cuenta").performClick()
        tap("Probar sin cuenta");tap("Entrar a la prueba")
        ui.onNodeWithText("Aceptar y continuar").assertIsNotEnabled()
        tap("Acepto los términos y condiciones")
        ui.onNodeWithText("Aceptar y continuar").assertIsNotEnabled()
        tap("Leer términos y privacidad")
        ui.onNodeWithText("Quiénes ofrecen Noki").assertExists()
        tap("Leer política de privacidad")
        ui.onNodeWithText("Responsables y finalidad").assertExists()
        tap("Volver a la aceptación")
        tap("Autorizo el tratamiento de mis datos según la política de privacidad")
        ui.onNodeWithText("Aceptar y continuar").assertIsEnabled()
        capture("aceptacion")
        tap("No aceptar")
        ui.onNodeWithText("No aceptaste.",substring=true).assertExists()
        ui.onNodeWithContentDescription("Volver").performClick()
        tap("Mis aparatos")
        ui.onNodeWithText("Entrar o probar").assertExists()
        tap("Aprende");ui.onNodeWithText("Entiende tus opciones").assertExists()
    }
    @Test fun expandedMapOffersThreeOriginsAndLocationIsOptional() {
        tap("Puntos");tap("Ampliar y mover el mapa")
        ui.onAllNodesWithText("Elegir punto de partida").onLast().assertIsDisplayed()
        ui.onAllNodesWithText("Partir desde mi ubicación").onLast().assertIsDisplayed()
        ui.onAllNodesWithText("Escribir dirección").onLast().assertIsDisplayed()
        tap("Partir desde mi ubicación")
        ui.onNodeWithText("Continuar con ubicación").assertExists()
        tap("Ahora no")
        tap("Escribir dirección")
        ui.onNodeWithText("Buscar dirección").assertIsNotEnabled()
        ui.onNodeWithText("Dirección y ciudad").performTextInput("Carrera 48 # 10-45, Medellín")
        ui.onNodeWithText("Buscar dirección").assertIsEnabled()
        capture("direccion")
        tap("Buscar dirección")
        ui.waitUntil(25000) {ui.onAllNodesWithText("Comprueba la coincidencia").fetchSemanticsNodes().isNotEmpty()}
        ui.onAllNodes(isSelectable()).onLast().performScrollTo().performClick()
        tap("Usar esta dirección")
        ui.onAllNodesWithText("Dirección:",substring=true).onLast().assertExists()
        tap("Elegir punto de partida")
        ui.onNodeWithText("Confirmar punto de partida").assertExists()
        tap("Cancelar selección")
        ui.waitUntil(30000) {ui.onAllNodesWithTag("NokiMapRendered").fetchSemanticsNodes().isNotEmpty()}
        capture("mapa-acciones")
    }
    @Test fun nativeGeocoderFindsPublicAddressWithoutGps() = runBlocking {
        val results=MapServices.findAddress(context,"Carrera 48 # 10-45, Medellín, Colombia")
        Assert.assertTrue("Se requieren coincidencias reales",results.isNotEmpty())
        Assert.assertTrue(results.any {it.coordinate.latitude in 6.0..6.5 && it.coordinate.longitude in -75.8..-75.3})
    }
    @Test fun portraitAndNavigationRemainReadableAt125And200Percent() {
        Assert.assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,ui.activity.requestedOrientation)
        Assert.assertEquals(android.content.res.Configuration.ORIENTATION_PORTRAIT,ui.activity.resources.configuration.orientation)
        listOf(1f,1.25f,1.3f,2f).forEach { scale ->
            ui.activityRule.scenario.onActivity {activity ->activity.setContent {
                val density=LocalDensity.current.density
                CompositionLocalProvider(LocalAccessibility provides AccessibilityOptions(appearance=AppAppearance.LIGHT),LocalDensity provides Density(density,scale)) {NokiTheme {NokiApp()}}
            }}
            ui.waitForIdle()
            ui.onNodeWithText("Mis aparatos").assertIsDisplayed()
            val layout=mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
            ui.onNodeWithText("Mis aparatos").performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) {it(layout)}
            Assert.assertTrue(layout.isNotEmpty())
            Assert.assertTrue("La etiqueta no debe recortarse: escala=$scale; ${layout.map {"${it.size}, líneas=${it.lineCount}, ancho=${it.didOverflowWidth}, alto=${it.didOverflowHeight}"}}",layout.none {it.hasVisualOverflow})
            capture("texto-${(scale*100).toInt()}")
            tap("Mis aparatos");ui.onNodeWithText("Entrar o probar").assertExists()
            tap("Puntos");ui.onNodeWithText("Encuentra dónde llevarlo").assertExists()
            tap("Aprende");ui.onNodeWithText("Entiende tus opciones").assertExists()
            tap("Inicio")
        }
    }
    @Test fun receiptIsVersionedAndRevocableInEncryptedStore() {
        val store=ConsentStore(SecureStore(context))
        val subject="verification-${System.nanoTime()}"
        try {Assert.assertFalse(store.accepted(subject));store.accept(subject);Assert.assertTrue(store.accepted(subject));store.revoke(subject);Assert.assertFalse(store.accepted(subject))}
        finally {store.revoke(subject)}
    }
    @Test fun deliveryPickerExplainsDestinationAndOpensItsSheet() {
        tap("Puntos")
        ui.onNodeWithContentDescription("Mostrar lugares de entrega").performScrollTo().performClick()
        ui.onAllNodesWithText("Monterrey · Centro de Acopio Ambiental").onLast().assertExists().performClick()
        ui.onNodeWithText("Qué recibe").assertExists()
        tap("Escribir dirección de partida")
        ui.onNodeWithText("Dirección y ciudad").assertExists()
        ui.onNodeWithContentDescription("Volver al mapa").performClick()
        ui.onNodeWithText("Qué recibe").assertExists()
        tap("Cerrar ficha")
        ui.onNodeWithText("Buscar lugar de entrega").performScrollTo().performTextInput("lugar que no existe 9382")
        ui.onNodeWithText("No hay lugares con esta búsqueda.",substring=true).assertExists()
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
        ui.waitForIdle()
        ui.onNodeWithContentDescription("Limpiar búsqueda de lugares").performScrollTo().performClick()
        ui.onAllNodesWithText("Monterrey · Centro de Acopio Ambiental").onLast().assertExists()
        capture("selector-entrega")
    }
    @Test fun receiverInformationDoesNotAskForIdentityOrGrantRoles() {
        tap("Quiero recibir aparatos")
        ui.onNodeWithText("Recibir aparatos con Noki").assertExists()
        ui.onNodeWithText("Crear mi perfil de receptor").assertIsNotEnabled()
        ui.onAllNodes(hasSetTextAction()).assertCountEquals(0)
        tap("He leído los requisitos y entiendo cómo recibir aparatos")
        ui.onNodeWithText("Crear mi perfil de receptor").assertIsNotEnabled()
        tap("Acepto las condiciones del perfil de receptor y el uso de las fotos para revisar mi solicitud")
        ui.onNodeWithText("Crear mi perfil de receptor").assertIsEnabled()
        capture("receptor")
        tap("Crear mi perfil de receptor")
        ui.onNodeWithText("Entra para guardar tu progreso").assertExists()
        tap("Volver sin perder mi progreso")
        ui.onNodeWithText("Recibir aparatos con Noki").assertExists()
        tap("Consultar términos y privacidad")
        ui.onNodeWithText("Quiénes ofrecen Noki").assertExists()
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithContentDescription("Mi cuenta").performClick()
        tap("Quiero recibir aparatos")
        ui.onNodeWithText("Recibir aparatos con Noki").assertExists()
        ui.onAllNodes(hasSetTextAction()).assertCountEquals(0)
        Assert.assertNull(SecureStore(context).get("session"))
    }
    @Test fun receiverInformationAndPickerRemainUsableAt200Percent() {
        ui.activityRule.scenario.onActivity {activity ->activity.setContent {
            val density=LocalDensity.current.density
            CompositionLocalProvider(LocalAccessibility provides AccessibilityOptions(appearance=AppAppearance.LIGHT),LocalDensity provides Density(density,2f)) {NokiTheme {NokiApp()}}
        }}
        tap("Quiero recibir aparatos")
        tap("Consultar términos y privacidad")
        ui.onNodeWithText("Quiénes ofrecen Noki").assertExists()
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithText("Cómo empezar").performScrollTo().assertIsDisplayed()
        capture("receptor-200")
        ui.onNodeWithContentDescription("Volver").performClick()
        tap("Puntos")
        ui.onNodeWithContentDescription("Mostrar lugares de entrega").performScrollTo().performClick()
        ui.onAllNodesWithText("Monterrey · Centro de Acopio Ambiental").onLast().performScrollTo().performClick()
        ui.onNodeWithText("Qué recibe").performScrollTo().assertIsDisplayed()
        capture("ficha-200")
    }
}
