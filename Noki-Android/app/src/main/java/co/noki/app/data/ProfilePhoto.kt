package co.noki.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import co.noki.app.domain.PhotoFrame
import co.noki.app.domain.photoFrameRect

internal fun readBoundedProfilePhoto(input:java.io.InputStream):ByteArray=ByteArrayOutputStream().use {output->
    val buffer=ByteArray(8192)
    while(true) {val size=input.read(buffer);if(size<0) break;if(output.size()+size>524288) throw NokiApiException("La foto guardada supera el tamaño permitido.");output.write(buffer,0,size)}
    output.toByteArray()
}

internal fun decodeProfilePhoto(bytes:ByteArray):Bitmap {
    if(bytes.size>524288) throw NokiApiException("Esta foto supera el tamaño permitido.")
    if(bytes.size<3 || bytes[0]!=0xff.toByte() || bytes[1]!=0xd8.toByte() || bytes[2]!=0xff.toByte()) throw NokiApiException("No pudimos leer la foto de perfil.")
    val bounds=BitmapFactory.Options().apply {inJustDecodeBounds=true}
    BitmapFactory.decodeByteArray(bytes,0,bytes.size,bounds)
    if(bounds.outWidth !in 1..1024 || bounds.outHeight !in 1..1024) throw NokiApiException("No pudimos leer la foto de perfil.")
    return BitmapFactory.decodeByteArray(bytes,0,bytes.size) ?: throw NokiApiException("No pudimos leer la foto de perfil.")
}

/** Bounded square JPEG without original metadata; no gallery copy or broad permission. */
internal suspend fun prepareProfilePhoto(context:Context,uri:Uri):ByteArray=withContext(Dispatchers.IO) {
    val bytes=preparePlacePhoto(context,uri)
    frameProfilePhoto(bytes,PhotoFrame())
}

internal suspend fun frameProfilePhoto(bytes:ByteArray,frame:PhotoFrame):ByteArray=withContext(Dispatchers.IO) {
    val bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size) ?: throw NokiApiException("No pudimos abrir la foto. Elige otra imagen.")
    val outputBitmap=Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888)
    val r=photoFrameRect(bitmap.width,bitmap.height,512f,frame)
    android.graphics.Canvas(outputBitmap).apply {
        drawColor(android.graphics.Color.rgb(244,246,243))
        drawBitmap(bitmap,null,android.graphics.RectF(r.left,r.top,r.right,r.bottom),android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG))
    }
    try {ByteArrayOutputStream().use {output->outputBitmap.compress(Bitmap.CompressFormat.JPEG,85,output);output.toByteArray().also {if(it.size>524288) throw NokiApiException("Elige una foto de menor tamaño.")}}}
    finally {outputBitmap.recycle();bitmap.recycle()}
}
