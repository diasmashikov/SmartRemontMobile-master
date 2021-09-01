package kz.cheesenology.smartremontmobile.view.main.clientphoto

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_image_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.model.CameraListModel

class ClientPhotoAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ClientPhotoAdapter.Holder>() {

    private val _data: MutableList<CameraListModel> = mutableListOf()
    private val _singleData: CameraListModel? = null
    private var _callback: (CameraListModel) -> Unit = { }
    private var mCallback: Callback? = null
    private var mBitmapList: MutableMap<Int, Bitmap>? = mutableMapOf()
    private var mImgUrlList: MutableMap<Int, String>? = mutableMapOf()
    private var mImgPathList: MutableMap<Int, String>? = mutableMapOf()

    var data: List<CameraListModel>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: CameraListModel
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
                val list = mBitmapList!!.map {
                    it.value
                }
                val urlList = mImgUrlList!!.map {
                    it.value
                }
                val pathList = mImgPathList!!.map {
                    it.value
                }
                mCallback!!.onClick(list, position, urlList, pathList)
            }

            if (isUser) {
                ivImage.setImageBitmap(imgBitMapSmall)
                mBitmapList!![position] = imgBitMapBig!!

                if (imgPath != null) {
                    mImgPathList!![position] = imgPath
                }

                ivImage.setOnCreateContextMenuListener { menu, v, _ ->
                    menu.add(0, v.id, 0, "Удалить фото").setOnMenuItemClickListener {
                        _data.removeAt(position)
                        notifyDataSetChanged()
                        true
                    }
                }
            } else if (!isUser) {
                if (imgUrl != null) {
                    mImgPathList!![position] = imgUrl
                }

                GlideApp.with(itemView)
                        .load(imgUrl)
                        .override(128, 128)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivImage)

                ivImage.setOnCreateContextMenuListener { menu, v, menuInfo ->
                    menu.add(0, v.id, 0, "Удалить фото").setOnMenuItemClickListener {
                        _data.removeAt(position)
                        notifyDataSetChanged()
                        mCallback!!.deletePhotoFromServer(remontCheckListPhotoID, imgUrl)
                        true
                    }
                }
            }
        }
    }

    fun setCallback(callback: (CameraListModel) -> Unit) {
        _callback = callback
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: List<Bitmap>, position: Int, urlList: List<String>?, pathList: List<String>?)
        fun deletePhotoFromServer(remontCheckListPhotoID: Int?, imgUrl: String?)
    }

    class Holder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val ivImage = view.ivItemImage
    }
}