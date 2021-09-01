package kz.cheesenology.smartremontmobile.view.main.clientphoto.expand

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import kz.cheesenology.smartremontmobile.R

@Layout(R.layout.item_child_photo_view)
class PhotoChildView(var context: Context, var info: PhotoChildModel) {

    @View(R.id.childPhotoItemView)
    @JvmField
    var image: ImageView? = null


    @SuppressLint("SetTextI18n")
    @Resolve
    fun onResolved() {
        Glide.with(context).load(info.imageUrl).into(image!!)
    }
}