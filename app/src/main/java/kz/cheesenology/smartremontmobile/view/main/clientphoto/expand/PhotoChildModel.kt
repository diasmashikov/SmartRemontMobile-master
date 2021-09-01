package kz.cheesenology.smartremontmobile.view.main.clientphoto.expand

import android.graphics.Bitmap

data class PhotoChildModel(
        val isUser: Boolean,
        val imageUrl: String? = null,
        val bitmap: Bitmap? = null,
        val imgPath: String? = null,
        val roomName: String? = null
) {
    override fun toString(): String {
        return roomName.toString()
    }
}