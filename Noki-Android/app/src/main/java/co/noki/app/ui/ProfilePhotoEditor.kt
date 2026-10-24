package co.noki.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import co.noki.app.data.NokiModel

@Composable internal fun ProfilePhotoEditor(model:NokiModel) {
    var remove by remember {mutableStateOf(false)}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri->uri?.let(model::chooseProfilePhoto)}
    Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(10.dp)) {
        model.profilePhoto?.let {Image(it.asImageBitmap(),"Tu foto de perfil",Modifier.size(96.dp).clip(CircleShape),contentScale=ContentScale.Crop)}
            ?: Surface(shape=CircleShape,color=MaterialTheme.colorScheme.primaryContainer) {Icon(Icons.Outlined.Person,"Perfil sin foto",Modifier.padding(20.dp).size(56.dp))}
        OutlinedButton(onClick={picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},enabled=!model.busy,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)) {Text(if(model.hasProfilePhoto) "Cambiar foto de perfil" else "Añadir foto de perfil")}
        if(model.hasProfilePhoto) TextButton(onClick={remove=true},enabled=!model.busy) {Text("Quitar mi foto")}
    }
    Text("La foto es opcional y privada. Elige una imagen tuya; podrás revisarla antes de guardarla.",style=MaterialTheme.typography.bodyMedium)
    model.profileNotice?.let {Note(it,true)}
    model.photoDraft?.let {bytes->
        ProfileCropDialog(bytes,model.busy,model::saveProfilePhoto,model::discardProfilePhoto,model.notice)
    }
    if(remove) AlertDialog(onDismissRequest={remove=false},title={Text("¿Quitar tu foto de perfil?")},text={Text("Se eliminará la copia de tu cuenta. La foto de tu galería se conserva.")},confirmButton={TextButton(onClick={remove=false;model.removeProfilePhoto()},enabled=!model.busy){Text("Sí, quitar foto")}},dismissButton={TextButton(onClick={remove=false}){Text("Conservar foto")}})
}
