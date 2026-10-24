package co.noki.app.domain

import kotlin.test.*

class ReceiverApplicationTest {
    private fun complete() = ReceiverApplicationInput(
        name = "Persona Responsable", adult = true, place = "Lugar de prueba",
        address = "Calle 10 número 20, Medellín", latitude = 6.2, longitude = -75.5,
        hours = "Lunes a viernes de 9 a 5", whatsapp = "573001234567", publishContact = true,
        categories = listOf("phone"), conditions = "Sin restricciones adicionales",
        photos = listOf("front", "reception", "storage"), consent = true
    )
    private fun fields(input: ReceiverApplicationInput, step: Int) = ReceiverApplicationRules.issues(input, step).map { it.field }

    @Test fun completeInputHasNoPendingRequirements() {
        assertTrue(ReceiverApplicationRules.all(complete()).isEmpty())
    }
    @Test fun emptyStepsIdentifyEveryRequirementInDisplayOrder() {
        val all = ReceiverApplicationRules.all(ReceiverApplicationInput())
        assertEquals(ReceiverField.entries, all.map { it.field })
        assertTrue(all.all { it.message.isNotBlank() })
        assertEquals(2, fields(ReceiverApplicationInput(), 0).size)
        assertEquals(8, fields(ReceiverApplicationInput(), 1).size)
        assertEquals(4, fields(ReceiverApplicationInput(), 2).size)
    }
    @Test fun shortConditionsGiveSpecificMinimumInsteadOfSilentlyBlocking() {
        val issues = ReceiverApplicationRules.issues(complete().copy(conditions = "Ninguna"), 1)
        assertEquals(ReceiverField.CONDITIONS, issues.single().field)
        assertTrue(issues.single().message.contains("10 caracteres"))
    }
    @Test fun externalSpacesNeverMeetMinimumLengths() {
        assertEquals(listOf(ReceiverField.NAME), fields(complete().copy(name = "    Ana    "), 0))
        assertEquals(listOf(ReceiverField.CONDITIONS), fields(complete().copy(conditions = "     No     "), 1))
    }
    @Test fun minimumBoundariesAndAllMaximumsAreExplicit() {
        val boundary = complete().copy(name = "12345", place = "123", address = "1234567890", hours = "12345", conditions = "1234567890")
        assertTrue(ReceiverApplicationRules.all(boundary).isEmpty())
        val short = boundary.copy(name = "1234", place = "12", address = "123456789", hours = "1234", conditions = "123456789")
        assertEquals(5, ReceiverApplicationRules.all(short).size)
        assertEquals(5, ReceiverApplicationRules.all(complete().copy(name = "x".repeat(121), place = "x".repeat(101), address = "x".repeat(241), hours = "x".repeat(201), conditions = "x".repeat(501))).size)
    }
    @Test fun locationNeedsBothRealCoordinates() {
        listOf(complete().copy(latitude = null), complete().copy(longitude = null), complete().copy(latitude = Double.NaN), complete().copy(longitude = 181.0)).forEach {
            assertEquals(listOf(ReceiverField.LOCATION), fields(it, 1))
        }
    }
    @Test fun checksAndCategoriesAreNeverAutomaticallyAccepted() {
        assertEquals(listOf(ReceiverField.ADULT), fields(complete().copy(adult = false), 0))
        assertEquals(listOf(ReceiverField.PUBLISH, ReceiverField.CATEGORIES), fields(complete().copy(publishContact = false, categories = emptyList()), 1))
        assertEquals(listOf(ReceiverField.CONSENT), fields(complete().copy(consent = false), 2))
    }
    @Test fun contactUsesTheExistingContactContract() {
        listOf("", "0000000000", "123456789", "1".repeat(16), "57300text").forEach {
            assertEquals(listOf(ReceiverField.WHATSAPP), fields(complete().copy(whatsapp = it), 1))
        }
        assertTrue(fields(complete().copy(whatsapp = "3001234567"), 1).isEmpty())
    }
    @Test fun eachMissingPhotoHasItsOwnInstruction() {
        val expectedFields = listOf(ReceiverField.FRONT_PHOTO, ReceiverField.RECEPTION_PHOTO, ReceiverField.STORAGE_PHOTO)
        expectedFields.forEachIndexed { index, field ->
            assertEquals(listOf(field), fields(complete().copy(photos = listOf("a", "b", "c").mapIndexed { i, p -> if (i == index) "" else p }), 2))
        }
        assertEquals(expectedFields, fields(complete().copy(photos = emptyList()), 2))
    }
}
