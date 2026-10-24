package co.noki.app

import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import org.junit.Assert.*
import org.junit.Test

class DeliveryCodeTest {
 @Test fun generatedQrRoundTripsWithoutPersonalInformation() {
  val payload="NOKI1:40000000-0000-4000-8000-000000000001:"+"a1".repeat(32)
  val matrix=MultiFormatWriter().encode(payload,BarcodeFormat.QR_CODE,640,640)
  val pixels=IntArray(640*640) {i->if(matrix[i%640,i/640]) 0xff000000.toInt() else 0xffffffff.toInt()}
  val decoded=MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(640,640,pixels))))
  assertEquals(payload,decoded.text)
  assertFalse(payload.contains("@"))
 }
}
