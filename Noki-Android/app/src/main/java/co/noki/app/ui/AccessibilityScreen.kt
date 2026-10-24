package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription

@Composable
fun AccessibilityScreen() {
    val options=LocalAccessibility.current
    val update=LocalAccessibilityUpdate.current
    Page {
        Text("Ajusta Noki a tu manera",style=MaterialTheme.typography.headlineMedium)
        Text("Los cambios se aplican al momento y se guardan en este teléfono. No cambian los ajustes de otras aplicaciones.")
        Text("Apariencia",style=MaterialTheme.typography.titleLarge)
        listOf(AppAppearance.SYSTEM to "Como el teléfono", AppAppearance.LIGHT to "Claro", AppAppearance.DARK to "Oscuro").forEach { (appearance,label) ->
            Choice(label,options.appearance == appearance) { update(options.copy(appearance=appearance)) }
        }
        Text("Tamaño del texto",style=MaterialTheme.typography.titleLarge)
        listOf(1f to "Usar tamaño del teléfono",1.15f to "Texto grande",1.3f to "Texto más grande").forEach { (scale,label) ->
            Choice(label,options.textScale == scale) { update(options.copy(textScale=scale)) }
        }
        Text("Nunca reducimos el tamaño que elegiste en los ajustes de Android.",style=MaterialTheme.typography.bodyMedium)
        SettingToggle("Mayor contraste","Refuerza los textos secundarios y los bordes en el tema claro u oscuro.",options.highContrast) { update(options.copy(highContrast=it)) }
        SettingToggle("Reducir movimiento","Quita los desplazamientos y la animación de entrada. Los botones siguen indicando que los pulsaste.",options.reduceMotion) { update(options.copy(reduceMotion=it)) }
        OutlinedCard { Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("Así se verá tu contenido",style=MaterialTheme.typography.titleMedium)
            Text("Consulta el horario y confirma qué aparatos recibe el lugar antes de ir.")
        } }
        OutlinedButton(onClick={update(AccessibilityOptions())},modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) { Text("Restablecer ajustes de Noki") }
        Text("Para activar el lector de pantalla, abre Accesibilidad en los ajustes del teléfono. Si Android tiene las animaciones desactivadas, Noki no las reactiva.",style=MaterialTheme.typography.bodyMedium)
    }
}
@Composable private fun SettingToggle(title:String,description:String,value:Boolean,changed:(Boolean)->Unit) {
    OutlinedCard { Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium)
            Switch(value,changed,modifier=Modifier.semantics { contentDescription=title })
        }
        Text(description,style=MaterialTheme.typography.bodyMedium)
    } }
}
