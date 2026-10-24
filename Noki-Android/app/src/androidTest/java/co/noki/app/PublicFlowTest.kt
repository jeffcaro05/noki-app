package co.noki.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.graphics.Bitmap
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import co.noki.app.ui.NokiApp
import co.noki.app.ui.NokiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PublicFlowTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    @org.junit.Before fun keepTestScreenAwake() {
        org.junit.Assert.assertEquals("co.noki.app.qa",InstrumentationRegistry.getInstrumentation().targetContext.packageName)
        ui.activityRule.scenario.onActivity { it.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
        ui.waitUntil(20000) { runCatching { ui.onAllNodesWithText("Inicio").fetchSemanticsNodes().isNotEmpty() || ui.onAllNodesWithText("Explorar sin cuenta").fetchSemanticsNodes().isNotEmpty() }.getOrDefault(false) }
        if(ui.onAllNodesWithText("Explorar sin cuenta").fetchSemanticsNodes().isNotEmpty()) ui.onNodeWithText("Explorar sin cuenta").performScrollTo().performClick()
        ui.waitUntil(20000) { runCatching { ui.onAllNodesWithText("Inicio").fetchSemanticsNodes().isNotEmpty() }.getOrDefault(false) }
    }

    private fun tap(label: String) {
        ui.waitUntil(15000) { ui.onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty() }
        ui.onNodeWithText(label).performScrollTo().performClick()
        ui.waitForIdle()
    }
    private fun openOrientation() { tap("Orientar mi dispositivo") }
    private fun choosePhone() { tap("Teléfono o tableta"); tap("Continuar") }
    private fun screenshot(name: String) {
        ui.waitForIdle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.getExternalFilesDir(null), "noki-$name.png")
        val bitmap = ui.onRoot().captureToImage().asAndroidBitmap()
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
    @Test fun homeShowsFourWorkingSections() {
        ui.onNodeWithText("Orientar mi dispositivo").assertExists()
        ui.onNodeWithText("Mis aparatos").assertExists()
        ui.onNodeWithText("Puntos").assertExists()
        screenshot("inicio")
    }
    @Test fun workingDeviceAndBackPreserveAnswers() {
        openOrientation()
        ui.onNodeWithText("Continuar").assertIsNotEnabled()
        choosePhone()
        tap("No he notado algo así"); tap("Continuar")
        tap("Sí, funciona"); tap("Ver orientación")
        ui.onNodeWithText("Todavía puede tener una segunda vida").assertExists()
        screenshot("resultado")
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithText("¿Sabes si funciona?").assertExists()
        ui.onNodeWithText("Sí, funciona").assertIsSelected()
    }
    @Test fun safetySkipsFunctionQuestionAndCanBeRevised() {
        openOrientation(); choosePhone()
        tap("No estoy seguro"); tap("Ver orientación")
        ui.onNodeWithText("Primero, una revisión segura").assertExists()
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithText("¿Has notado alguna señal de atención?").assertExists()
        tap("No he notado algo así"); tap("Continuar")
        ui.onNodeWithText("Ver orientación").assertIsNotEnabled()
        tap("Funciona con fallas"); tap("Ver orientación")
        ui.onNodeWithText("Una revisión puede ayudarte a decidir").assertExists()
    }
    @Test fun rapidRepeatedOrientationActionCannotSkipTheResult() {
        openOrientation();choosePhone()
        tap("No he notado algo así");tap("Continuar");tap("Sí, funciona")
        val action=ui.onNodeWithText("Ver orientación").fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsActions.OnClick].action!!
        ui.runOnIdle {action();action();action()}
        ui.waitUntil(5000) {ui.onAllNodesWithText("Todavía puede tener una segunda vida").fetchSemanticsNodes().isNotEmpty()}
        ui.onNodeWithText("Todavía puede tener una segunda vida").assertExists()
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithText("¿Sabes si funciona?").assertExists()
        ui.onNodeWithText("Sí, funciona").assertIsSelected()
    }
    @Test fun answerSurvivesActivityRecreation() {
        openOrientation(); tap("Audio o video")
        ui.activityRule.scenario.recreate()
        ui.onNodeWithText("Audio o video").assertIsSelected()
        tap("Continuar")
        ui.onNodeWithText("¿Has notado alguna señal de atención?").assertExists()
    }
    @Test fun learnAndAboutAreReachableWithoutAccount() {
        ui.onNodeWithText("Aprende").performClick()
        tap("¿Qué son los RAEE?")
        ui.onNodeWithText("Fuente: Ministerio de Ambiente de Colombia").assertExists()
        screenshot("aprende")
        ui.onNodeWithText("Inicio").performClick()
        ui.onNodeWithContentDescription("Mi cuenta").performClick()
        tap("Acerca de Noki")
        ui.onNodeWithText("nokiappcorporation@gmail.com").assertExists()
    }
    @Test fun largeTextKeepsOrientationUsable() {
        ui.activityRule.scenario.onActivity { activity ->
            activity.setContent {
                val density = LocalDensity.current.density
                CompositionLocalProvider(LocalDensity provides Density(density, 2f)) { NokiTheme { NokiApp() } }
            }
        }
        ui.waitForIdle()
        screenshot("inicio-texto-200")
        openOrientation(); choosePhone()
        tap("No estoy seguro"); tap("Ver orientación")
        ui.onNodeWithText("Primero, una revisión segura").assertExists()
        tap("Consultar puntos antes de trasladarlo")
        ui.onNodeWithText("Encuentra dónde llevarlo").assertExists()
    }
    @Test fun accessibilityMenuPersistsAndRegistrationIsHonest() {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val original=co.noki.app.ui.AppPreferences(context).options
        try {
            ui.onNodeWithContentDescription("Abrir menú").performClick()
            ui.onNodeWithText("Accesibilidad").performClick()
            tap("Texto grande")
            org.junit.Assert.assertEquals(1.15f,co.noki.app.ui.AppPreferences(context).options.textScale,0.001f)
            ui.onNodeWithContentDescription("Mayor contraste").performScrollTo().performClick()
            ui.waitForIdle()
            org.junit.Assert.assertEquals(!original.highContrast,co.noki.app.ui.AppPreferences(context).options.highContrast)
            ui.onNodeWithContentDescription("Reducir movimiento").performScrollTo().performClick()
            ui.waitForIdle()
            org.junit.Assert.assertEquals(!original.reduceMotion,co.noki.app.ui.AppPreferences(context).options.reduceMotion)
            ui.activityRule.scenario.recreate()
            ui.onNodeWithText("Texto grande").assertIsSelected()
            org.junit.Assert.assertEquals(!original.highContrast,co.noki.app.ui.AppPreferences(context).options.highContrast)
            org.junit.Assert.assertEquals(!original.reduceMotion,co.noki.app.ui.AppPreferences(context).options.reduceMotion)
            screenshot("accesibilidad-v03")
            ui.onNodeWithContentDescription("Volver").performClick()
            ui.onNodeWithContentDescription("Mi cuenta").performClick()
            tap("Crear cuenta con correo")
            ui.onNodeWithText("Enviar código").assertExists()
            if(ui.onAllNodesWithText("El envío de códigos aún no está habilitado.",substring=true).fetchSemanticsNodes().isNotEmpty()) ui.onNodeWithText("Enviar código").assertIsNotEnabled()
            ui.onNodeWithText("Te enviaremos un código para entrar.",substring=true).assertExists()
        } finally {co.noki.app.ui.AppPreferences(context).update(original)}
    }
}
