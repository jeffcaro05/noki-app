package co.noki.app.ui

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import co.noki.app.data.NokiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/** Public read on entry/resume and every ten seconds, never in the background. */
@Composable internal fun DirectoryAutoRefresh(model:NokiModel) {
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle,model) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while(isActive) {model.refreshDirectoryQuietly();delay(10000)}
        }
    }
}
