package kz.cheesenology.brigadierapp.view.main.remont.remontlist.remontmenu.chatlist.chat

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.item_chat_photo_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.view.main.chat.ChatActivity
import java.io.File

typealias ChatPhotoType = StageChatFileEntity

class ChatPhotoAdapter(val context: ChatActivity) : RecyclerView.Adapter<ChatPhotoAdapter.ViewHolder>() {

    private var _data: MutableList<ChatPhotoType> = mutableListOf()
    private val _singleData: ChatPhotoType? = null
    private var mCallback: Callback? = null

    var data: List<ChatPhotoType>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: ChatPhotoType
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_photo_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = _data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int): Unit = with(_data[position]) {
        holder.apply {

            var fileImage: File? = null
            if (file_name != null)
                fileImage = File(AppConstant.PATH_PROFILE_PHOTO_FILE, file_name)

            if (fileImage != null && fileImage.exists()) {
                if (fileImage.exists()) {
                    GlideApp.with(context)
                        .load(fileImage)
                        .override(128, 128)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivPhoto)
                } else {
                    GlideApp.with(context)
                        .load(Uri.parse(file_name))
                        .override(128, 128)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivPhoto)
                }
            } else {
                GlideApp.with(context)
                    .load(AppConstant.getServerName() + "$file_url")
                    .override(128, 128)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPhoto)
            }


            itemView.setOnClickListener {
                StfalconImageViewer.Builder<ChatPhotoType>(context, _data) { view, image ->
                    GlideApp.with(context)
                        .load(AppConstant.getServerName() + image.file_url)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view)
                }
                    .withStartPosition(position)
                    .show(true)
            }
        }
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: ChatPhotoType)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto = view.ivItemChatPhoto
    }
}