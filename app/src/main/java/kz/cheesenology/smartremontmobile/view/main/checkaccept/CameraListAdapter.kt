package kz.cheesenology.smartremontmobile.view.main.checkaccept

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_image_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaEntity
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File

class CameraListAdapter(context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<CameraListAdapter.Holder>() {

    private val _data: MutableList<UserDefectMediaEntity> = mutableListOf()
    private val _singleData: UserDefectMediaEntity? = null
    private var mCallback: Callback? = null
    private var mImgPathList: MutableMap<Int, String>? = mutableMapOf()
    private var mContext = context

    var data: List<UserDefectMediaEntity>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: UserDefectMediaEntity
        get () = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit = with(_data[position]) {
        holder.apply {

            ivImage.setOnClickListener {
                val pathList = mImgPathList!!.map {
                    it.value
                }
                mCallback!!.onPhotoClick(position, pathList)
            }

            if (fileUrl != null) {
                mImgPathList!![position] = fileName!!
            }

            GlideApp.with(mContext)
                    .load(File(AppConstant.FULL_MEDIA_PHOTO_PATH, fileName))
                    .override(128, 128)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImage)

            ivImage.setOnCreateContextMenuListener { menu, v, _ ->
                menu.add(0, v.id, 0, "Удалить фото").setOnMenuItemClickListener {
                    _data.removeAt(position)
                    mCallback!!.onPhotoDelete(position, defectID)
                    notifyDataSetChanged()
                    true
                }
            }

            ivPhotoDate.text = dateCreate
        }
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: List<Bitmap>, position: Int, urlList: List<String>?, pathList: List<String>?)
        fun onPhotoClick(position: Int, pathList: List<String>)
        fun onPhotoDelete(position: Int, remontCheckListPhotoID: Int?)
    }

    class Holder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val ivImage = view.ivItemImage
        val ivPhotoDate = view.ivItemPhotoDate
    }
}