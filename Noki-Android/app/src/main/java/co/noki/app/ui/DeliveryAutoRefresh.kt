package co.noki.app.ui

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import co.noki.app.data.NokiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/** Authenticated read; never confirms receipt or exposes private tables to Realtime. */
@Composable internal fun DeliveryAutoRefresh(model:NokiModel,enabled:Boolean) {
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle,enabled,model.ownId) {
        if(enabled) lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while(isActive) {
                model.refreshReceptionQuietly()
                delay(5000)
            }
        }
    }
}
