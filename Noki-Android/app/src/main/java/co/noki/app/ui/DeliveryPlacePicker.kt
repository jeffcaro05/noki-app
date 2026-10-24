package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.noki.app.data.CollectionPoint

/** Searches delivery destinations; never changes the departure address. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun DeliveryPlacePicker(query:String,change:(String)->Unit,places:List<CollectionPoint>,choose:(CollectionPoint)->Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("Elige dónde entregar",style=MaterialTheme.typography.titleLarge)
        Text("Busca por nombre o dirección, o abre la lista de lugares. Esto no cambia tu punto de partida.",style=MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(expanded=expanded,onExpandedChange={expanded=it}) {
            OutlinedTextField(value=query,onValueChange={change(it.take(180));expanded=true},label={Text("Buscar lugar de entrega")},
                singleLine=true,modifier=Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                leadingIcon={Icon(Icons.Outlined.Search,null)},trailingIcon={
                    Row {
                        if(query.isNotBlank()) IconButton(onClick={change("");expanded=true}) {Icon(Icons.Outlined.Close,"Limpiar búsqueda de lugares")}
                        IconButton(onClick={expanded=!expanded},modifier=Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable)) {
                            Icon(if(expanded) androidx.compose.material.icons.Icons.Outlined.KeyboardArrowUp else androidx.compose.material.icons.Icons.Outlined.KeyboardArrowDown,"Mostrar lugares de entrega")
                        }
                    }
                })
            ExposedDropdownMenu(expanded=expanded,onDismissRequest={expanded=false},modifier=Modifier.heightIn(max=360.dp)) {
                if(places.isEmpty()) DropdownMenuItem(text={Text("No hay lugares con esta búsqueda. Prueba otro nombre o quita los filtros.")},onClick={},enabled=false)
                places.forEach { point -> DropdownMenuItem(text={Column(Modifier.padding(vertical=6.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                    Text(point.name,style=MaterialTheme.typography.titleMedium)
                    Text(point.address,style=MaterialTheme.typography.bodyMedium)
                }},onClick={expanded=false;choose(point)}) }
            }
        }
    }
}
