package co.noki.app

import android.graphics.Bitmap
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.noki.app.data.*
import co.noki.app.domain.PhotoFrame
import co.noki.app.ui.*
import org.junit.*
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(AndroidJUnit4::class)
class AccountRefinementTest {
 @get:Rule val ui=createAndroidComposeRule<MainActivity>()
 @Before fun isolatedCopyOnly() {
  Assert.assertEquals("co.noki.app.qa",InstrumentationRegistry.getInstrumentation().targetContext.packageName)
  ui.activityRule.scenario.onActivity {it.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)}
 }
 private fun capture(name:String) {
  Thread.sleep(500);ui.waitForIdle()
  val context=InstrumentationRegistry.getInstrumentation().targetContext
  InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()?.let {b->File(context.getExternalFilesDir(null),"noki-$name.png").outputStream().use {b.compress(Bitmap.CompressFormat.PNG,100,it)}}
 }
 @OptIn(ExperimentalMaterial3Api::class)
 @Test fun longestUsernameFitsNextToLogoOrBelowAtLargeText() {
  val name="Usuario_con_24_caractere1"
  listOf(1f,2f).forEach {s->
   ui.activityRule.scenario.onActivity {a->a.setContent {
    CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density,s),LocalAccessibility provides AccessibilityOptions()) {NokiTheme {
     TopAppBar(title={HomeBrand(name)},expandedHeight=with(LocalDensity.current){MaterialTheme.typography.titleMedium.lineHeight.toDp()*3+16.dp},actions={IconButton(onClick={}){Text("•")};IconButton(onClick={}){Text("⋮")}})
    }}
   }}
   ui.waitForIdle()
   val layouts=mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
   ui.onNodeWithText("Bienvenido, $name").performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult){it(layouts)}
   capture("saludo-${(s*100).toInt()}-v070")
   Assert.assertTrue(layouts.isNotEmpty());Assert.assertTrue("Username must fit at $s",layouts.none {it.hasVisualOverflow})
  }
 }
 @Test fun photoCanBeReframedWithAccessibleControlsAndResetAt200Percent() {
  val bitmap=Bitmap.createBitmap(1000,600,Bitmap.Config.ARGB_8888)
  android.graphics.Canvas(bitmap).apply {drawColor(android.graphics.Color.rgb(20,95,56));drawCircle(500f,300f,100f,android.graphics.Paint().apply {color=android.graphics.Color.WHITE})}
  val bytes=ByteArrayOutputStream().also {bitmap.compress(Bitmap.CompressFormat.JPEG,90,it)}.toByteArray();bitmap.recycle()
  var saved:PhotoFrame?=null
  ui.activityRule.scenario.onActivity {a->a.setContent {CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density,2f),LocalAccessibility provides AccessibilityOptions()) {NokiTheme {ProfileCropDialog(bytes,false,{saved=it},{})}}}}
  ui.waitForIdle();capture("encuadre-200-v070")
  ui.onNodeWithContentDescription("Zoom de la foto").performScrollTo().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.SetProgress){it(2f)}
  ui.onNodeWithContentDescription("Posición horizontal de la foto").performScrollTo().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.SetProgress){it(.25f)}
  ui.onNodeWithText("Guardar foto").performScrollTo().performClick()
  Assert.assertEquals(PhotoFrame(2f,.25f,0f),saved)
  ui.onNodeWithText("Mostrar la foto completa").performScrollTo().performClick()
  ui.onNodeWithText("Guardar foto").performScrollTo().performClick()
  Assert.assertEquals(PhotoFrame(),saved)
 }
}
