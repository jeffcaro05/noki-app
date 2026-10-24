package co.noki.app.domain

import kotlin.test.*

class JourneyTest {
    @Test fun allCombinationsHaveUsefulNextSteps() {
        for (c in DeviceCategory.entries) for(s in SafetyState.entries) for(f in FunctionState.entries) {
            val result=Orientation(c,s,f).recommendation()!!
            assertEquals(3,recommendedSteps(c,result).size)
            assertTrue(recommendedSteps(c,result).all { it.length > 40 })
            if (s != SafetyState.NORMAL) assertEquals(Recommendation.SAFETY_REVIEW,result)
        }
    }
    @Test fun intentNeverCompletesAnOutcome() {
        val record=DeviceRecord("id","Mi teléfono",DeviceCategory.PHONE,SafetyState.NORMAL,FunctionState.WORKS)
        for(d in Decision.entries) assertFalse(record.copy(decision=d).completed)
        assertEquals(0,record.preparedCount)
        assertEquals(5,record.copy(preparation=Preparation.entries.associateWith { CheckState.NOT_APPLICABLE }).preparedCount)
        assertFalse(record.copy(preparation=Preparation.entries.associateWith { CheckState.DONE }).completed)
        assertTrue(record.copy(outcome=Outcome.KEPT).completed)
    }
    @Test fun safetyChangeNeverKeepsFunctionAnswer() {
        val orientation=Orientation(DeviceCategory.COMPUTER,SafetyState.NORMAL,FunctionState.WORKS)
        assertNull(orientation.withSafety(SafetyState.ATTENTION).function)
        assertNull(orientation.withSafety(SafetyState.UNKNOWN).function)
    }
}
