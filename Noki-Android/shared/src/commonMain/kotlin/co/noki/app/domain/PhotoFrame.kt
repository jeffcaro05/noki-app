package co.noki.app.domain

import kotlin.math.sqrt

data class PhotoFrame(val zoom:Float=1f,val x:Float=0f,val y:Float=0f) {
    fun bounded()=PhotoFrame(zoom.takeIf {it.isFinite()}?.coerceIn(1f,4f)?:1f,x.takeIf {it.isFinite()}?.coerceIn(-.5f,.5f)?:0f,y.takeIf {it.isFinite()}?.coerceIn(-.5f,.5f)?:0f)
}
data class FrameRect(val left:Float,val top:Float,val right:Float,val bottom:Float)
fun photoFrameRect(width:Int,height:Int,size:Float,frame:PhotoFrame):FrameRect {
    require(width>0 && height>0 && size>0 && size.isFinite())
    val f=frame.bounded()
    // At zoom 1 the complete source rectangle fits inside the circular avatar.
    val scale=size/sqrt(width.toDouble()*width+height.toDouble()*height).toFloat()*f.zoom
    val w=width*scale;val h=height*scale
    val left=(size-w)/2+f.x*size;val top=(size-h)/2+f.y*size
    return FrameRect(left,top,left+w,top+h)
}
