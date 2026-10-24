package co.noki.app

import android.graphics.Bitmap
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import co.noki.app.data.CollectionPoint
import co.noki.app.domain.*
import co.noki.app.ui.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34],qualifiers="w360dp-h800dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DeliveryMapContinuationTest {
    @get:Rule val compose=createComposeRule()
    private val device=DeviceRecord("d","Teléfono",DeviceCategory.PHONE,SafetyState.NORMAL,FunctionState.UNKNOWN,Decision.DELIVER,
        preparation=Preparation.entries.associateWith {CheckState.DONE})
    private val receiver=ReceiverPlace("receiver","Lugar","Dirección","Horario",listOf("phone"),"Condiciones",requestId="request")
    private val point=CollectionPoint("request","Lugar","Dirección","Noki","","2026-10-14",6.2,-75.5,listOf("phone"),null,null,receiverRequestId="request")
    private val delivery=Delivery("delivery","sender","receiver","Lugar","Dirección",DeliveryStatus.PENDING,emptyList())
    private var selected:String?=null
    private var opened:String?=null
    private var alternatives=false
    private var preparation=false
    private var capture:android.view.View?=null
    private fun display(p:CollectionPoint?=point,active:Delivery?=null,d:DeviceRecord=device,places:List<ReceiverPlace> = listOf(receiver),font:Float=1f) {
        compose.setContent {CompositionLocalProvider(LocalDensity provides Density(1f,font)){NokiTheme {Page {
            capture=LocalView.current.rootView
            DeliveryMapContinuation(p,active,d,true,places,true,{opened=it},{selected=it?.id},{alternatives=true},{preparation=true})
        }}}}
    }
    @Test fun selectedPlaceCanContinueWithoutGpsOrSuccessfulRoute() {
        display()
        compose.onNodeWithText("Elegir este destino").performScrollTo().assertIsEnabled().performClick()
        assertEquals("request",selected)
    }
    @Test fun activeDeliveryOpensSameCodeEvenWhenConsultingAnotherPlace() {
        display(p=point.copy(id="reference",receiverRequestId=null),active=delivery)
        compose.onNodeWithText("Consultar otro lugar no cambia",substring=true).assertExists()
        compose.onNodeWithText("Mostrar QR y código").performScrollTo().performClick()
        assertEquals("delivery",opened);assertNull(selected)
    }
    @Test fun referenceOnlyPlaceOffersAnExplicitAlternativeNotFakeQr() {
        display(p=point.copy(receiverRequestId=null))
        compose.onNodeWithText("Este lugar es una referencia pública",substring=true).assertExists()
        compose.onNodeWithText("Elegir este destino").assertDoesNotExist()
        compose.onNodeWithText("Ver otros lugares").performScrollTo().performClick()
        assertTrue(alternatives)
    }
    @Test fun ownOrWithdrawnReceiverCannotBeSelected() {
        display(places=emptyList())
        compose.onNodeWithText("Ver otros lugares").performScrollTo().performClick()
        assertTrue(alternatives);assertNull(selected)
    }
    @Test fun placeNameIsNeverUsedToResolveAnIdentity() {
        display(places=listOf(receiver.copy(requestId="different-request")))
        compose.onNodeWithText("Elegir este destino").assertDoesNotExist()
        compose.onNodeWithText("Ver otros lugares").assertExists()
    }
    @Test fun incompletePreparationReturnsToItsNextStep() {
        display(d=device.copy(preparation=Preparation.entries.associateWith {CheckState.PENDING}))
        compose.onNodeWithText("Continuar preparación").performScrollTo().performClick()
        assertTrue(preparation);assertNull(selected)
    }
    @Test fun completedDeliveryOpensResultNotPendingQr() {
        display(active=delivery.copy(status=DeliveryStatus.COMPLETED))
        compose.onNodeWithText("Mostrar QR y código").assertDoesNotExist()
        compose.onNodeWithText("Ver estado de la entrega").performScrollTo().performClick()
        assertEquals("delivery",opened)
    }
    @Test fun nextActionRemainsReadableAtTwoHundredPercent() {
        display(font=2f)
        compose.onNodeWithText("Elegir este destino").performScrollTo().assertIsDisplayed().assertIsEnabled()
        compose.runOnIdle {
            val view=checkNotNull(capture);val bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val out=File("build/outputs/ui-v075/continuidad-200.png");out.parentFile?.mkdirs()
            out.outputStream().use {bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
        }
    }
}
