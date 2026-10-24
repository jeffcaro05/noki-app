package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp

@Composable internal fun StatusNotice(message:String,success:Boolean=false,dismiss:(()->Unit)?=null) {
    val dark=MaterialTheme.colorScheme.background.luminance()<0.5f
    val background=if(success) {if(dark) Color(0xFF163D2A) else Color(0xFFDFF3E5)}
        else {if(dark) Color(0xFF542421) else Color(0xFFFFE4E1)}
    val foreground=if(success) {if(dark) Color(0xFFB9EFCA) else Color(0xFF15532F)}
        else {if(dark) Color(0xFFFFDAD5) else Color(0xFF8B1A13)}
    Surface(color=background,contentColor=foreground,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Icon(if(success) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel,
                if(success) "Correcto" else "No se pudo completar",Modifier.size(24.dp))
            Text(message,Modifier.weight(1f).semantics {liveRegion=LiveRegionMode.Polite},style=MaterialTheme.typography.bodyMedium)
            dismiss?.let {IconButton(onClick=it,modifier=Modifier.sizeIn(minWidth=48.dp,minHeight=48.dp)) {Icon(Icons.Outlined.Close,"Cerrar aviso")}}
        }
    }
}
