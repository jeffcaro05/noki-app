package co.noki.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/** Re-encoding strips EXIF/GPS; originals never enter storage. */
internal suspend fun preparePlacePhoto(context:Context,uri:Uri):ByteArray=withContext(Dispatchers.IO) {
    val resolver=context.contentResolver
    val bounds=BitmapFactory.Options().apply {inJustDecodeBounds=true}
    resolver.openInputStream(uri)?.use {BitmapFactory.decodeStream(it,null,bounds)}
    if(bounds.outWidth<=0 || bounds.outHeight<=0 || bounds.outWidth.toLong()*bounds.outHeight>100_000_000) throw NokiApiException("Esta foto no se puede procesar. Elige otra imagen del lugar.")
    var sample=1;while(maxOf(bounds.outWidth,bounds.outHeight)/sample>1600) sample*=2
    val bitmap=resolver.openInputStream(uri)?.use {BitmapFactory.decodeStream(it,null,BitmapFactory.Options().apply {inSampleSize=sample})}
        ?: throw NokiApiException("No pudimos leer la foto. Vuelve a seleccionarla.")
    val exif=runCatching {resolver.openInputStream(uri)?.use {ExifInterface(it)}}.getOrNull()
    val transform=Matrix().apply {if(exif?.isFlipped==true) postScale(-1f,1f);postRotate((exif?.rotationDegrees?:0).toFloat())}
    val oriented=if(transform.isIdentity) bitmap else Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,transform,true)
    try {ByteArrayOutputStream().use {output->oriented.compress(Bitmap.CompressFormat.JPEG,85,output);output.toByteArray().also {if(it.size>3145728) throw NokiApiException("La foto es demasiado grande. Elige una imagen más pequeña.")}}}
    finally {if(oriented!==bitmap) oriented.recycle();bitmap.recycle()}
}
