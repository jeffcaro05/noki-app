package co.noki.app.domain

import org.junit.Assert.*
import org.junit.Test

class OrientationTest {
    @Test fun noResultWithoutCategoryOrSafety() {
        assertNull(Orientation().recommendation())
        assertNull(Orientation(DeviceCategory.PHONE).recommendation())
    }
    @Test fun safetyTakesPriorityForAllCategoriesAndFunctionalStates() {
        for (category in DeviceCategory.entries) {
            for (safety in listOf(SafetyState.ATTENTION, SafetyState.UNKNOWN)) {
                for (function in FunctionState.entries) {
                    assertEquals(Recommendation.SAFETY_REVIEW, Orientation(category, safety, function).recommendation())
                }
            }
        }
    }
    @Test fun workingDeviceGetsSecondLife() {
        for (category in DeviceCategory.entries) assertEquals(Recommendation.SECOND_LIFE,
            Orientation(category, SafetyState.NORMAL, FunctionState.WORKS).recommendation())
    }
    @Test fun faultyOrUnknownGetsReview() {
        for (category in DeviceCategory.entries) {
            for (function in listOf(FunctionState.FAULTS, FunctionState.UNKNOWN))
                assertEquals(Recommendation.REPAIR_REVIEW, Orientation(category, SafetyState.NORMAL, function).recommendation())
        }
    }
    @Test fun normalNeedsFunctionAnswer() {
        assertNull(Orientation(DeviceCategory.COMPUTER, SafetyState.NORMAL).recommendation())
    }
    @Test fun changingSafetyClearsIncompatibleAnswer() {
        val original = Orientation(DeviceCategory.PHONE, SafetyState.NORMAL, FunctionState.WORKS)
        assertNull(original.withSafety(SafetyState.ATTENTION).function)
        assertNull(original.withSafety(SafetyState.UNKNOWN).function)
        assertNull(original.withSafety(SafetyState.UNKNOWN).withSafety(SafetyState.NORMAL).function)
    }
    @Test fun validatesTextAndPasswordsWithoutChangingPassword() {
        assertFalse(Validation.name(" "))
        assertTrue(Validation.name("  José Pérez  "))
        assertFalse(Validation.description("x"))
        assertTrue(Validation.description("Radio de la sala"))
        assertFalse(Validation.optional("x".repeat(61)))
        assertTrue(Validation.email("persona@example.com"))
        assertFalse(Validation.email("persona"))
        assertTrue(Validation.password(" 123456 "))
        assertFalse(Validation.password("1234567"))
    }
}
