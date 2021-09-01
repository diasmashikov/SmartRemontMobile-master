package kz.cheesenology.smartremontmobile.model

import android.graphics.Bitmap

data class CameraListModel(
        val isUser: Boolean,
        val imgBitMapSmall: Bitmap?,
        val imgBitMapBig: Bitmap?,
        val imgPath: String?,
        val imgUrl: String?,
        val remontCheckListPhotoID: Int?
)