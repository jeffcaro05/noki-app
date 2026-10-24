package co.noki.app

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import co.noki.app.data.CollectionPoint
import co.noki.app.domain.*
import co.noki.app.ui.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34],qualifiers="w360dp-h800dp")
class PublishedDestinationsTest {
    @get:Rule val compose=createComposeRule()
    private val point=CollectionPoint("request","Lugar habilitado","Calle de prueba","Noki","https://example.invalid","2026-10-14",6.2,-75.5,listOf("phone"),null,null,receiverRequestId="request")
    private val points=listOf(point,point.copy(id="ref1",name="Referencia uno",receiverRequestId=null),point.copy(id="ref2",name="Referencia dos",receiverRequestId=null))
    private val receiver=ReceiverPlace("recipient","Lugar habilitado","Calle de prueba","Horario",listOf("phone"),"Condiciones",requestId="request")

    @Test fun preparationCategoryDoesNotSilentlyFilterAnyDestination() {
        assertEquals(points,visibleDeliveryPlaces(points,category="computer"))
        assertTrue(visibleDeliveryPlaces(points,category="computer",onlyPublished=true).isEmpty())
        assertEquals(points,visibleDeliveryPlaces(points,onlyPublished=false))
        assertEquals(listOf(points[1]),visibleDeliveryPlaces(points,search="Referencia uno"))
    }

    @Test fun anonymousAndOwnAccountBothSeeEveryPublishedPlace() {
        var chosen:String?=null
        compose.setContent {NokiTheme {Page {
            PublishedDeliveryPlaces(visibleDeliveryPlaces(points),emptyList(),"request"){chosen=it.id}
        }}}
        points.forEach {p->compose.onNodeWithText(p.name).performScrollTo().assertIsDisplayed()}
        compose.onNodeWithText("Tu lugar de recepción",substring=true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Lugar habilitado").performScrollTo().performClick()
        assertEquals("request",chosen)
        assertTrue(destinationDescription(point,emptyList(),null).contains("Lugar publicado"))
    }

    @Test fun recipientDisappearingFromEligibilityNeverHidesItsPublicMarkerOrCard() {
        var available by mutableStateOf(listOf(receiver))
        compose.setContent {NokiTheme {Page {PublishedDeliveryPlaces(visibleDeliveryPlaces(points),available,null){}}}}
        compose.onNodeWithText("Recibe entregas en Noki",substring=true).performScrollTo().assertIsDisplayed()
        compose.runOnIdle {available=emptyList()}
        points.forEach {p->compose.onNodeWithText(p.name).performScrollTo().assertIsDisplayed()}
        compose.onNodeWithText("Lugar publicado",substring=true).performScrollTo().assertIsDisplayed()
    }

    @Test fun ownPlaceExplainsWhyItCannotCreateASelfDeliveryWithoutHidingIt() {
        val device=DeviceRecord("d","Teléfono",DeviceCategory.PHONE,SafetyState.NORMAL,FunctionState.UNKNOWN,Decision.DELIVER,
            preparation=Preparation.entries.associateWith {CheckState.DONE})
        var generated=false
        compose.setContent {NokiTheme {Page {
            DeliveryMapContinuation(point,null,device,true,emptyList(),true,{},{generated=true},{},{},ownRequestId="request",generateHere={generated=true})
        }}}
        compose.onNodeWithText("Este es tu lugar de recepción",substring=true).assertExists()
        compose.onNodeWithText("Ver otros lugares").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Elegir receptor con código").assertDoesNotExist()
        assertFalse(generated)
    }

    @Test fun unselectedMapHasInstructionRatherThanAButtonThatReappliesAFilter() {
        compose.setContent {NokiTheme {Page {DeliveryMapContinuation(null,null,null,true,emptyList(),true,{},{},{},{})}}}
        compose.onNodeWithText("Toca un lugar en el mapa o en la lista",substring=true).assertExists()
        compose.onNodeWithText("Elegir receptor con código").assertDoesNotExist()
    }
}
