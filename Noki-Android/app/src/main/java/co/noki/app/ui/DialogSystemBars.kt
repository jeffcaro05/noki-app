package co.noki.app.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

@Composable internal fun DialogSystemBars() {
    val view=LocalView.current
    val dark=LocalDarkAppearance.current
    SideEffect {
        var parent:android.view.ViewParent?=view.parent
        var window=(view as? DialogWindowProvider)?.window
        while(window==null && parent!=null) {window=(parent as? DialogWindowProvider)?.window;parent=parent.parent}
        window?.let {WindowCompat.getInsetsController(it,view).apply {isAppearanceLightStatusBars=!dark;isAppearanceLightNavigationBars=!dark}}
    }
}
