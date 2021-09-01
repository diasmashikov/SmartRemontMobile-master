package kz.cheesenology.smartremontmobile.view.main.clientphoto

import android.content.Intent
import android.graphics.Bitmap
import moxy.MvpView
import java.io.File

interface ClientPhotoView: MvpView {
    fun setImagePath(absolutePath: String?)
    fun setCapturedPhoto(thumbImage: Bitmap, lBitmap: Bitmap?, mCurrentPhotoPath: String?, selectRoom: String)
    fun startCameraIntent(takePictureIntent: Intent, photoFile: File, selectRoom: String)

}
