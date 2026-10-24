package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/** A fixed viewport prevents nested sheet drags from competing with the form's fling. */
@Composable internal fun AccessDialog(dismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest=dismiss,properties=DialogProperties(usePlatformDefaultWidth=false)) {
        DialogSystemBars()
        Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column {
                TextButton(onClick=dismiss) { Text("Volver sin perder mi progreso") }
                Box(Modifier.weight(1f)) { content() }
            }
        }
    }
}
