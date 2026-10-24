package co.noki.app.ui

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import co.noki.app.R

val NokiGreen = Color(0xFF10661E)
val NokiBlue = Color(0xFF0F2167)
val NokiCream = Color(0xFFF5F4F0)
val NokiMint = Color(0xFFDCEADD)
val LocalDarkAppearance = staticCompositionLocalOf { false }
val Poppins = FontFamily(Font(R.font.poppins_light, FontWeight.Light), Font(R.font.poppins_regular, FontWeight.Normal), Font(R.font.poppins_medium, FontWeight.Medium))
private fun text(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(fontFamily=Poppins,fontSize=size.sp,lineHeight=line.sp,fontWeight=weight)

@Composable
fun NokiTheme(content: @Composable () -> Unit) {
    val options = LocalAccessibility.current
    val dark = when(options.appearance) { AppAppearance.DARK -> true; AppAppearance.LIGHT -> false; else -> isSystemInDarkTheme() }
    val contrast = options.highContrast
    val target = if(dark) darkColorScheme(
        primary=Color(0xFF9CDD9D),onPrimary=Color(0xFF123C1C),primaryContainer=Color(0xFF284D33),onPrimaryContainer=Color(0xFFD7F3D8),
        secondary=Color(0xFFBBC5F5),onSecondary=Color(0xFF192B60),secondaryContainer=Color(0xFF303C60),onSecondaryContainer=Color(0xFFDEE3FF),
        background=Color(0xFF1B2420),onBackground=Color(0xFFE7EDE5),surface=Color(0xFF242F29),onSurface=Color(0xFFE7EDE5),
        surfaceVariant=Color(0xFF344138),onSurfaceVariant=if(contrast) Color.White else Color(0xFFC2CEC3),
        outline=if(contrast) Color(0xFFE7EDE5) else Color(0xFF95A398),outlineVariant=if(contrast) Color(0xFFC2CEC3) else Color(0xFF56665B),
        error=Color(0xFFFFB4AC),onError=Color(0xFF690005),errorContainer=Color(0xFF6E2C27),onErrorContainer=Color(0xFFFFDAD5),
        surfaceContainerLowest=Color(0xFF18201C),surfaceContainerLow=Color(0xFF222D26),surfaceContainer=Color(0xFF28342D),surfaceContainerHigh=Color(0xFF303D35),surfaceContainerHighest=Color(0xFF39463E)
    ) else lightColorScheme(
        primary=NokiGreen,onPrimary=Color.White,primaryContainer=NokiMint,onPrimaryContainer=Color(0xFF123718),
        secondary=NokiBlue,onSecondary=Color.White,secondaryContainer=Color(0xFFE7E9F4),onSecondaryContainer=NokiBlue,
        background=NokiCream,onBackground=Color(0xFF17211A),surface=Color.White,onSurface=Color(0xFF17211A),
        surfaceVariant=NokiMint,onSurfaceVariant=if(contrast) Color(0xFF17211A) else Color(0xFF455449),
        outline=if(contrast) Color(0xFF17211A) else Color(0xFF69766B),outlineVariant=if(contrast) Color(0xFF17211A) else Color(0xFFC5CBC6),
        error=Color(0xFF9B2525),onError=Color.White,errorContainer=Color(0xFFFFDAD5),onErrorContainer=Color(0xFF410002),
        surfaceContainerLowest=Color.White,surfaceContainerLow=Color(0xFFF1F4EE),surfaceContainer=NokiCream,surfaceContainerHigh=Color(0xFFE7ECE5),surfaceContainerHighest=Color(0xFFDFE6DD)
    )
    @Composable fun blend(color:Color):Color = animateColorAsState(color,tween(if(options.reduceMotion) 0 else 650),label="appearance").value
    val animated=target.copy(primary=blend(target.primary),onPrimary=blend(target.onPrimary),primaryContainer=blend(target.primaryContainer),onPrimaryContainer=blend(target.onPrimaryContainer),
        secondary=blend(target.secondary),onSecondary=blend(target.onSecondary),secondaryContainer=blend(target.secondaryContainer),onSecondaryContainer=blend(target.onSecondaryContainer),
        tertiary=blend(target.tertiary),onTertiary=blend(target.onTertiary),tertiaryContainer=blend(target.tertiaryContainer),onTertiaryContainer=blend(target.onTertiaryContainer),inverseSurface=blend(target.inverseSurface),inverseOnSurface=blend(target.inverseOnSurface),inversePrimary=blend(target.inversePrimary),surfaceTint=blend(target.surfaceTint),
        background=blend(target.background),onBackground=blend(target.onBackground),surface=blend(target.surface),onSurface=blend(target.onSurface),surfaceVariant=blend(target.surfaceVariant),onSurfaceVariant=blend(target.onSurfaceVariant),
        outline=blend(target.outline),outlineVariant=blend(target.outlineVariant),error=blend(target.error),onError=blend(target.onError),errorContainer=blend(target.errorContainer),onErrorContainer=blend(target.onErrorContainer),
        surfaceContainerLowest=blend(target.surfaceContainerLowest),surfaceContainerLow=blend(target.surfaceContainerLow),surfaceContainer=blend(target.surfaceContainer),surfaceContainerHigh=blend(target.surfaceContainerHigh),surfaceContainerHighest=blend(target.surfaceContainerHighest))
    val view=LocalView.current
    SideEffect { (view.context as? Activity)?.window?.let { window -> WindowCompat.getInsetsController(window,view).apply { isAppearanceLightStatusBars=!dark;isAppearanceLightNavigationBars=!dark } } }
    CompositionLocalProvider(LocalDarkAppearance provides dark) {
        MaterialTheme(colorScheme=animated,typography=Typography(headlineLarge=text(32,42,FontWeight.Medium),headlineMedium=text(28,38,FontWeight.Medium),titleLarge=text(22,30,FontWeight.Medium),titleMedium=text(18,27,FontWeight.Medium),bodyLarge=text(18,28),bodyMedium=text(16,25),bodySmall=text(16,24),labelLarge=text(16,24,FontWeight.Medium),labelMedium=text(14,20,FontWeight.Medium),labelSmall=text(14,20,FontWeight.Medium)),content=content)
    }
}
