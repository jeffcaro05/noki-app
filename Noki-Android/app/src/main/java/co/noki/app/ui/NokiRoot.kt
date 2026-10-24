package co.noki.app.ui

import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import co.noki.app.R
import kotlinx.coroutines.delay

@Composable
fun NokiRoot(showLaunch: Boolean = true) {
    val context = LocalContext.current
    val settings = remember { AppPreferences(context) }
    val systemReduced = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    val options = settings.options.copy(reduceMotion = settings.options.reduceMotion || systemReduced)
    val density = LocalDensity.current
    CompositionLocalProvider(LocalAccessibility provides options, LocalAccessibilityUpdate provides settings::update,
        LocalDensity provides Density(density.density, maxOf(density.fontScale, options.textScale))) {
        NokiTheme {
            var launching by rememberSaveable { mutableStateOf(showLaunch && !options.reduceMotion) }
            var openAccount by rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(Unit) { if (launching) { delay(1450); launching = false } }
            AnimatedContent(targetState = if (launching) "launch" else if (!settings.welcomed) "welcome" else "app",
                transitionSpec = { fadeIn(tween(if(options.reduceMotion) 0 else 380)).togetherWith(fadeOut(tween(if(options.reduceMotion) 0 else 300))) }, label = "launch") { screen ->
                when(screen) {
                    "launch" -> BrandLaunch()
                    "welcome" -> Surface(color=MaterialTheme.colorScheme.background, modifier=Modifier.fillMaxSize().safeDrawingPadding()) {
                        Page {
                            Image(painterResource(if(LocalDarkAppearance.current) R.drawable.noki_wordmark_dark else R.drawable.noki_wordmark), "Noki", Modifier.width(152.dp).height(62.dp))
                            Text("¿Tienes aparatos que ya no usas?", style=MaterialTheme.typography.headlineMedium)
                            Text("Noki te ayuda a decidir si conservarlos, repararlos, darles otro uso o llevarlos a recolección en Medellín.")
                            WelcomeStep("1", "Cuéntanos cómo está", "Responde hasta tres preguntas. No necesitas encender ni abrir el aparato.")
                            WelcomeStep("2", "Elige el siguiente paso", "Recibe una orientación y consulta lugares con dirección, horario y fuente.")
                            WelcomeStep("3", "Guarda y continúa", "Entra a tu cuenta para guardar tus aparatos, preparar una entrega y registrar lo que hiciste.")
                            PrimaryButton("Entrar o crear cuenta") { openAccount=true; settings.welcomeDone() }
                            OutlinedButton(onClick={ settings.welcomeDone() }, modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) { Text("Explorar sin cuenta") }
                            Text("La orientación, los puntos y Aprende se pueden consultar sin registro.", style=MaterialTheme.typography.bodyMedium)
                        }
                    }
                    else -> NokiApp(startWithAccount=openAccount)
                }
            }
        }
    }
}
@Composable private fun WelcomeStep(number:String, title:String, description:String) {
    Row(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
        Surface(color=MaterialTheme.colorScheme.primaryContainer, shape=androidx.compose.foundation.shape.CircleShape) { Text(number,Modifier.padding(horizontal=15.dp,vertical=8.dp),color=MaterialTheme.colorScheme.onPrimaryContainer) }
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) { Text(title,style=MaterialTheme.typography.titleMedium); Text(description,style=MaterialTheme.typography.bodyMedium) }
    }
}
@Composable internal fun BrandLaunch() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible=true }
    val progress by animateFloatAsState(if(visible) 1f else 0f,tween(1300),label="brandEntrance")
    val accent=MaterialTheme.colorScheme.primary
    Surface(color=MaterialTheme.colorScheme.background,modifier=Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().safeDrawingPadding(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
            Box(Modifier.size(248.dp),contentAlignment=Alignment.Center) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    val radius=size.minDimension*.42f
                    drawCircle(accent.copy(alpha=.08f*progress),radius)
                    repeat(3) { ring ->
                        val inset=size.minDimension*(.025f+ring*.045f)
                        drawArc(accent.copy(alpha=.18f+ring*.08f),-110f+progress*140f+ring*95f,95f*progress,false,
                            topLeft=androidx.compose.ui.geometry.Offset(inset,inset),size=androidx.compose.ui.geometry.Size(size.width-inset*2,size.height-inset*2),style=androidx.compose.ui.graphics.drawscope.Stroke(width=2.dp.toPx(),cap=androidx.compose.ui.graphics.StrokeCap.Round))
                    }
                    repeat(6) { i ->
                        val angle=(i*60.0+progress*70.0)*Math.PI/180
                        val x=center.x+kotlin.math.cos(angle).toFloat()*radius
                        val y=center.y+kotlin.math.sin(angle).toFloat()*radius
                        drawCircle(accent.copy(alpha=progress*.7f),3.dp.toPx(),androidx.compose.ui.geometry.Offset(x,y))
                    }
                }
                Image(painterResource(R.drawable.noki_symbol),"Noki",Modifier.size(136.dp).scale(.86f+.14f*progress).alpha((progress*3).coerceAtMost(1f)))
            }
            Spacer(Modifier.height(24.dp))
            Text("Todavía sirve, recicla",Modifier.alpha(progress),style=MaterialTheme.typography.titleMedium,color=accent)
        }
    }
}
