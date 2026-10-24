package co.noki.app

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.core.app.ActivityOptionsCompat
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import co.noki.app.ui.*
import org.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReceiverApplicationExperienceTest {
    @get:Rule val compose = createComposeRule()
    private var submitted: JSONObject? = null
    private var submittedPhotos = emptyList<Uri>()
    private var count = 0
    private var capture: android.view.View? = null
    private val adult = "Soy mayor de edad y tengo autorización para operar este lugar"
    private val publish = "Autorizo publicar los datos del lugar y este WhatsApp para consultas sobre entregas"
    private val consent = "Confirmo que la información es correcta y autorizo usar estas fotos para revisar mi solicitud, según los términos y la privacidad"
    private val nextPlace = "Pasar a datos del lugar"
    private val nextPhotos = "Pasar a fotos y revisión"
    private val send = "Enviar solicitud"
    private fun draft(conditions: String = "Sin restricciones adicionales") = JSONObject()
        .put("full_name", "Persona Responsable").put("place_name", "Lugar de prueba")
        .put("address", "Calle 10 número 20, Medellín").put("latitude", 6.2).put("longitude", -75.5)
        .put("hours", "Lunes a viernes, 9 a 5").put("public_whatsapp", "573001234567")
        .put("categories", JSONArray().put("phone")).put("conditions", conditions)

    private fun display(draft: JSONObject? = draft(), font: Float = 1f) {
        var photo = 0
        val owner = object : ActivityResultRegistryOwner {
            override val activityResultRegistry = object : ActivityResultRegistry() {
                override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
                    dispatchResult(requestCode, Uri.parse("content://noki-fixture/photo-${++photo}"))
                }
            }
        }
        compose.setContent {
            var busy by remember { mutableStateOf(false) }
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides owner, LocalDensity provides Density(1f, font)) {
                NokiTheme {
                    capture = LocalView.current.rootView
                    ReceiverApplicationForm("fixture@example.invalid", draft, busy) { body, photos ->
                        submitted = body; submittedPhotos = photos; count++; busy = true
                    }
                }
            }
        }
    }
    private fun toPlace() {
        compose.onNodeWithText(adult).performScrollTo().performClick()
        compose.onNodeWithText(nextPlace).performScrollTo().performClick()
        compose.onNodeWithText("Datos del lugar", substring = false).assertExists()
    }
    private fun toPhotos() {
        toPlace()
        compose.onNodeWithText(publish).performScrollTo().performClick()
        compose.onNodeWithText(nextPhotos).performScrollTo().performClick()
        compose.onNodeWithText("Añade tres fotos actuales").performScrollTo().assertIsDisplayed()
    }

    @Test fun emptyResponsibleStepExplainsAndScrollsInsteadOfSilentDisabledButton() {
        display(null)
        compose.onNodeWithText("Faltan 2 requisitos").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(nextPlace).performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText("Escribe el nombre completo del responsable (mínimo 5 caracteres).").assertIsDisplayed()
        compose.onNodeWithTag("receiver-NAME").performTextInput("Persona Responsable")
        compose.onNodeWithText(nextPlace).performScrollTo().performClick()
        compose.onNodeWithText("Marca la casilla de mayoría de edad y autorización para operar el lugar.").assertIsDisplayed()
        assertNull(submitted)
    }
    @Test fun shortConditionsAreExplainedAndCorrectionAdvancesWithoutLosingValues() {
        display(draft("Ninguna")); toPlace()
        compose.onNodeWithText(publish).performScrollTo().performClick()
        compose.onNodeWithText(nextPhotos).performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText("Describe las condiciones de recepción (mínimo 10 caracteres).").assertIsDisplayed()
        compose.onNodeWithTag("receiver-CONDITIONS").performTextReplacement("Sin restricciones adicionales")
        compose.onNodeWithText(nextPhotos).performScrollTo().performClick()
        compose.onNodeWithText("Añade tres fotos actuales").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Sin restricciones adicionales").assertExists()
        assertNull(submitted)
    }
    @Test fun changingAddressExplicitlyRequiresNewMapConfirmation() {
        display(); toPlace()
        compose.onNodeWithTag("receiver-ADDRESS").performScrollTo().performTextReplacement("Calle 11 número 20, Medellín")
        compose.onNodeWithText("Cambiaste la dirección. Confirma de nuevo la ubicación en el mapa para continuar.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(publish).performScrollTo().performClick()
        compose.onNodeWithText(nextPhotos).performScrollTo().performClick()
        compose.onNodeWithText("Abre «Ubicar mi lugar en el mapa», comprueba el marcador y pulsa «Confirmar ubicación».").assertIsDisplayed()
        compose.onNodeWithText("Añade tres fotos actuales").assertDoesNotExist()
    }
    @Test fun missingConsentAndPhotosAreExplicitAndNeverSubmit() {
        display(); toPhotos()
        compose.onNodeWithText(send).performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText("Selecciona la foto de fachada y entrada.").assertIsDisplayed()
        assertNull(submitted)
        listOf("1. Fachada y entrada", "2. Área de recepción", "3. Espacio de almacenamiento").forEach {
            compose.onNodeWithText(it, substring = true).performScrollTo().performClick()
        }
        compose.onNodeWithText(send).performScrollTo().performClick()
        compose.onNodeWithText("Marca la confirmación de información correcta y la autorización para revisar las fotos.").assertIsDisplayed()
        assertNull(submitted)
        compose.onNodeWithText(consent).performScrollTo().performClick()
        compose.onNodeWithText(send).performScrollTo().performClick()
        assertEquals(1, count)
        assertEquals(3, submittedPhotos.size)
        assertEquals("Lugar de prueba", submitted!!.getString("place_name"))
        assertEquals(6.2, submitted!!.getDouble("latitude"), 0.0)
        assertTrue(submitted!!.getBoolean("accepted"))
        assertTrue(submitted!!.getBoolean("adult_authorized"))
        assertFalse(submitted!!.has("status"))
        compose.onNodeWithText("Enviando solicitud…").assertIsNotEnabled()
    }
    @Test fun summaryLinkLeadsToMissingPublicationCheckbox() {
        display(); toPlace()
        compose.onNodeWithText("Revisar: Autorización de publicación").performScrollTo().performClick()
        compose.onNodeWithText(publish).assertIsDisplayed().assertIsOff()
        compose.onNodeWithText("Marca la autorización para publicar los datos del lugar y su WhatsApp.").assertIsDisplayed()
    }
    @Test fun pendingFieldsAndActionsAreUsableAtTwoHundredPercent() {
        display(draft("Ninguna"), 2f); toPlace()
        compose.onNodeWithText(publish).performScrollTo().performClick()
        compose.onNodeWithText(nextPhotos).performScrollTo().performClick()
        compose.onNodeWithText("Describe las condiciones de recepción (mínimo 10 caracteres).").assertIsDisplayed()
        val fieldBounds = compose.onNodeWithTag("receiver-CONDITIONS").getUnclippedBoundsInRoot()
        assertTrue("The invalid field must be visible from its top: $fieldBounds", fieldBounds.top.value >= 0f)
        val labelBounds = compose.onNodeWithTag("receiver-label-CONDITIONS", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("The full label must be above the field and inside the viewport: $labelBounds", labelBounds.top.value >= 0f && labelBounds.bottom <= fieldBounds.top)
        compose.runOnIdle {
            val view = checkNotNull(capture)
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val output = File("build/outputs/ui-v077/receptor-validacion-200.png")
            output.parentFile?.mkdirs()
            output.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        compose.onNodeWithTag("receiver-CONDITIONS").performScrollTo().performTextReplacement("Sin restricciones adicionales")
        compose.onNodeWithText(nextPhotos).performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("Añade tres fotos actuales").performScrollTo().assertIsDisplayed()
    }
}
