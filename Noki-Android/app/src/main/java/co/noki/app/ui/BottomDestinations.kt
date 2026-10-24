package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Measures the complete two-line label instead of constraining it to a one-line bar. */
@Composable internal fun BottomDestinations(items:List<Triple<String,String,ImageVector>>,current:String,choose:(String)->Unit) {
    val colors=MaterialTheme.colorScheme
    Surface(color=colors.surface) {
        Row(Modifier.navigationBarsPadding().fillMaxWidth().selectableGroup().padding(top=8.dp,bottom=6.dp)) {
            items.forEach { (route,label,icon) ->
                val selected=route==current
                Column(Modifier.weight(1f).selectable(selected,role=Role.Tab,onClick={choose(route)}).padding(horizontal=2.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                    Surface(color=if(selected) colors.secondaryContainer else Color.Transparent,shape=RoundedCornerShape(24.dp)) {
                        Icon(icon,null,Modifier.padding(horizontal=16.dp,vertical=4.dp).size(24.dp),tint=if(selected) colors.onSecondaryContainer else colors.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(label,modifier=Modifier.fillMaxWidth(),style=MaterialTheme.typography.labelSmall,textAlign=TextAlign.Center,minLines=2,maxLines=2,color=if(selected) colors.secondary else colors.onSurfaceVariant)
                }
            }
        }
    }
}
