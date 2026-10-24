package co.noki.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import co.noki.app.R
import co.noki.app.data.NokiModel

@Composable internal fun AccountHeader(model:NokiModel,open:()->Unit) {
    if(model.session!=null) {
        IconButton(onClick=open,modifier=Modifier.size(48.dp).semantics {contentDescription="Mi cuenta"}) {
            model.profilePhoto?.let {Image(it.asImageBitmap(),null,Modifier.size(40.dp).clip(CircleShape),contentScale=ContentScale.Crop)}
                ?: Icon(Icons.Outlined.AccountCircle,null)
        }
    } else {
        val large=LocalDensity.current.fontScale>=1.3f
        TextButton(onClick=open,modifier=Modifier.widthIn(max=if(large)170.dp else 154.dp).heightIn(min=48.dp)
            .semantics(mergeDescendants=true) {contentDescription="Mi cuenta";stateDescription="Registrarse o iniciar sesión"}) {
            Text(if(large) "Entrar o\nregistrarse" else if(LocalAccessibility.current.reduceMotion) "Registrarse o\niniciar sesión" else "Registrarse o iniciar sesión",
                modifier=(if(large || LocalAccessibility.current.reduceMotion) Modifier else Modifier.basicMarquee(iterations=2,initialDelayMillis=1800,repeatDelayMillis=1500,velocity=22.dp))
                    .clearAndSetSemantics {},style=MaterialTheme.typography.labelMedium,
                maxLines=if(large || LocalAccessibility.current.reduceMotion)2 else 1)
        }
    }
}

@Composable internal fun HomeBrand(username:String) {
    val large=LocalDensity.current.fontScale>=1.3f
    val logo:@Composable ()->Unit={Image(painterResource(if(LocalDarkAppearance.current) R.drawable.noki_wordmark_dark else R.drawable.noki_wordmark),"Noki",Modifier.width(82.dp).height(36.dp))}
    if(username.isBlank()) logo()
    else if(large || username.length>12) Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
        logo();Text("Bienvenido, $username",style=MaterialTheme.typography.labelMedium,maxLines=3)
    } else Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        logo();Text("Bienvenido, $username",modifier=Modifier.weight(1f),style=MaterialTheme.typography.labelMedium,maxLines=2)
    }
}
