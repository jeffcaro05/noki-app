package co.noki.app

import co.noki.app.data.*
import java.time.Instant
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34])
class StableDeliveryCodeTest {
    @Test fun pendingCodeDoesNotExpireAfterTenMinutesHoursOrDays() {
        val code=deliveryCodeFromJson("delivery",JSONObject("""{"qr":"NOKI1:fixture:secret","code":"A123456789","expires_at":"9999-12-31T23:59:59Z","valid_until":"delivery_closed"}"""))
        val start=Instant.parse("2026-10-15T00:00:00Z")
        for(seconds in listOf(599L,600L,3600L,86400L,86400L*30)) assertTrue(code.isDisplayable(start.plusSeconds(seconds)))
        assertTrue(code.validWhilePending)
        assertEquals("A123456789",code.code)
    }
    @Test fun legacyExpiredCodeIsNotIncorrectlyDeclaredPermanent() {
        val code=deliveryCodeFromJson("delivery",JSONObject("""{"qr":"legacy","code":"A123456789","expires_at":"2026-10-15T00:10:00Z"}"""))
        assertFalse(code.validWhilePending)
        assertTrue(code.isDisplayable(Instant.parse("2026-10-15T00:09:00Z")))
        assertFalse(code.isDisplayable(Instant.parse("2026-10-15T00:10:00Z")))
    }
    @Test fun malformedLegacyExpiryFailsClosed() {
        assertFalse(DeliveryCode("delivery","qr","code","not-a-date").isDisplayable())
    }
    @Test fun stableContractDoesNotRequireAClientExpiryClock() {
        val code=deliveryCodeFromJson("delivery",JSONObject("""{"qr":"qr","code":"A123456789","valid_until":"delivery_closed"}"""))
        assertTrue(code.isDisplayable(Instant.MAX))
    }
}
