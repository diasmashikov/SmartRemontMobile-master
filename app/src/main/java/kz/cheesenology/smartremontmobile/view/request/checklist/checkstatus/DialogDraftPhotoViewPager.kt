package kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.davemorrissey.labs.subscaleview.ImageSource
import kotlinx.android.synthetic.main.item_accept_draft_viewpager.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.util.ImageFilesRotatorAndResizer
import kz.cheesenology.smartremontmobile.util.ImageFilesRotatorAndResizer.bitmapToFile
import kz.cheesenology.smartremontmobile.util.SubsamplingScaleImageViewTarget
import java.io.File

class DialogDraftPhotoViewPager(
    private val images: MutableList<String?>,
    var rotatedFiles: MutableList<File?>,
    var bitmaps: MutableList<Bitmap?>
) : RecyclerView.Adapter<DialogDraftPhotoViewPager.ViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_accept_draft_viewpager, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val currentImageFile = rotatedFiles[position]
        val name = images[position]
       // val currentImageBitmap = bitmaps[position]
        
        if (currentImageFile != null) {
            //val rotatedBitmapToFile =
               // name?.let { bitmapToFile(holder.itemView.context, currentImage, it) }

            GlideApp.with(holder.itemView.context)
                .download(currentImageFile)
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(SubsamplingScaleImageViewTarget(holder.ivItemPhoto))
        }

    }

    override fun getItemCount(): Int {
        return rotatedFiles.size
    }

    class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivItemPhoto = itemView.ivItemPhotoViewPager
    }

}