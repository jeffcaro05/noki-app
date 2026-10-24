package co.noki.app.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.noki.app.domain.*

@Composable internal fun ProfileCropDialog(bytes:ByteArray,busy:Boolean,save:(PhotoFrame)->Unit,dismiss:()->Unit,error:String?=null) {
    val bitmap=remember(bytes) {BitmapFactory.decodeByteArray(bytes,0,bytes.size)}
    var frame by remember(bytes) {mutableStateOf(PhotoFrame())}
    val density=LocalDensity.current
    DisposableEffect(bitmap) {onDispose {bitmap?.recycle()}}
    Dialog(onDismissRequest={if(!busy)dismiss()},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        CompositionLocalProvider(LocalDensity provides density) {
        DialogSystemBars()
        Surface(Modifier.fillMaxSize().safeDrawingPadding()) {Page {
            Text("Encuadra tu foto",style=MaterialTheme.typography.headlineMedium)
            error?.let {Note(it,true)}
            Text("Arrastra para moverla y junta o separa dos dedos para ajustar el zoom. El círculo muestra cómo se verá en tu perfil.")
            if(bitmap!=null) Canvas(Modifier.fillMaxWidth().widthIn(max=360.dp).aspectRatio(1f)
                .semantics {contentDescription="Vista previa circular de tu foto. También puedes usar los controles de abajo."}
                .pointerInput(busy) {if(!busy)detectTransformGestures {_,pan,zoom,_->frame=frame.copy(zoom=frame.zoom*zoom,x=frame.x+pan.x/size.width,y=frame.y+pan.y/size.height).bounded()}}) {
                val circle=Path().apply {addOval(androidx.compose.ui.geometry.Rect(0f,0f,size.width,size.height))}
                clipPath(circle) {
                    drawRect(Color(0xFFF4F6F3))
                    val r=photoFrameRect(bitmap.width,bitmap.height,size.width,frame)
                    drawIntoCanvas {it.nativeCanvas.drawBitmap(bitmap,null,android.graphics.RectF(r.left,r.top,r.right,r.bottom),android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG))}
                }
            }
            Text("Zoom",style=MaterialTheme.typography.titleMedium)
            Slider(value=frame.zoom,onValueChange={frame=frame.copy(zoom=it)},valueRange=1f..4f,enabled=!busy,modifier=Modifier.semantics {contentDescription="Zoom de la foto"})
            Text("Mover de izquierda a derecha")
            Slider(value=frame.x,onValueChange={frame=frame.copy(x=it)},valueRange=-.5f.. .5f,enabled=!busy,modifier=Modifier.semantics {contentDescription="Posición horizontal de la foto"})
            Text("Mover de arriba a abajo")
            Slider(value=frame.y,onValueChange={frame=frame.copy(y=it)},valueRange=-.5f.. .5f,enabled=!busy,modifier=Modifier.semantics {contentDescription="Posición vertical de la foto"})
            OutlinedButton(onClick={frame=PhotoFrame()},enabled=!busy,modifier=Modifier.fillMaxWidth()) {Text("Mostrar la foto completa")}
            Text("La imagen original no cambia. Solo se guardará el encuadre que confirmes.",style=MaterialTheme.typography.bodyMedium)
            PrimaryButton(if(busy) "Guardando…" else "Guardar foto",!busy && bitmap!=null) {save(frame)}
            OutlinedButton(onClick=dismiss,enabled=!busy,modifier=Modifier.fillMaxWidth()) {Text("Cancelar")}
        }}
        }
    }
}
