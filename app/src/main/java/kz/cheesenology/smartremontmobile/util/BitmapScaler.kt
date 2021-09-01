package kz.cheesenology.smartremontmobile.util

import android.graphics.Bitmap

object BitmapScaler {
    fun scale(bitmap: Bitmap): Bitmap? {
        var width = (bitmap.width * 0.5).toInt()
        var height = (bitmap.height * 0.5).toInt()

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}