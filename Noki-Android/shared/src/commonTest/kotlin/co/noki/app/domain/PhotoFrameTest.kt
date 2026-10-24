package co.noki.app.domain

import kotlin.math.*
import kotlin.test.*

class PhotoFrameTest {
    @Test fun completeImageFitsCircularFrameWithoutClipping() {
        listOf(400 to 400,1600 to 900,900 to 1600).forEach {(w,h)->
            val r=photoFrameRect(w,h,512f,PhotoFrame())
            listOf(r.left to r.top,r.right to r.bottom).forEach {(x,y)->
                assertTrue(hypot(x-256f,y-256f)<=256.01f)
            }
            assertEquals(w.toFloat()/h,(r.right-r.left)/(r.bottom-r.top),.0001f)
        }
    }
    @Test fun outputAndPreviewUseSameNormalizedTransform() {
        val f=PhotoFrame(2.3f,.2f,-.1f)
        val a=photoFrameRect(1600,900,320f,f);val b=photoFrameRect(1600,900,512f,f)
        assertEquals(a.left/320,b.left/512,.0001f)
        assertEquals(a.bottom/320,b.bottom/512,.0001f)
    }
    @Test fun extremeAndNonFiniteGesturesStayBounded() {
        assertEquals(PhotoFrame(4f,.5f,-.5f),PhotoFrame(100f,10f,-10f).bounded())
        assertEquals(PhotoFrame(),PhotoFrame(Float.NaN,Float.POSITIVE_INFINITY,Float.NaN).bounded())
        assertFailsWith<IllegalArgumentException> {photoFrameRect(0,1,512f,PhotoFrame())}
    }
}
