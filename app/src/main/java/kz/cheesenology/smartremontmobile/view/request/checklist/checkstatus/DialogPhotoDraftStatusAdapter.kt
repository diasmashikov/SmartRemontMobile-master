package kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_accept_draft_photo.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.di.GlideApp
import java.io.File


class DialogPhotoDraftStatusAdapter(
    private val bitmaps: MutableList<Bitmap?>,
    private val imagesPath: MutableList<String?>,
) : RecyclerView.Adapter<DialogPhotoDraftStatusAdapter.Holder>() {

    var requestCheckStatusListViewListener: RequestCheckStatusListView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_accept_draft_photo, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount() = bitmaps.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bitmapa = bitmaps[position]
        val file1 = File(imagesPath[position])

        if (file1.exists()) {


            holder.ivPhoto.visibility = View.VISIBLE
            GlideApp.with(holder.itemView.context)
                .load(bitmapa)
                .override(128, 128)
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.ivPhoto)
        } else {
            //TODO сделать отображение по ссылке
            //BuildConfig.BASE_HOST +"/documents/123123sd.png"

        }

        holder.itemView.setOnLongClickListener {
                val popup = PopupMenu(holder.itemView.context, holder.itemView)
                popup.inflate(R.menu.menu_delete_photo)
                popup.setOnMenuItemClickListener { select ->
                    when (select.itemId) {
                        R.id.menu_delete_draft_photo -> {
                            //TODO добавить удаление файла
                            requestCheckStatusListViewListener?.deleteFilePath(imagesPath[position], position)
                           // imagesPath.remove(imagesPath[position])

                            File(imagesPath.toString()).delete()
                            notifyDataSetChanged()
                        }
                    }
                    false
                }
                popup.show()
                true
        }

        holder.itemView.setOnClickListener {
            requestCheckStatusListViewListener?.showViewPager(position, bitmaps, imagesPath)
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto = view.ivItemPhoto
    }
}