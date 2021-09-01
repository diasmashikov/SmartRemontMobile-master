package kz.cheesenology.smartremontmobile.view.main.checkaccept

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_image_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoEntity
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File


typealias wT = StandartPhotoEntity

class StandartWeakAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<StandartWeakAdapter.Holder>() {

    private val _data: MutableList<wT> = mutableListOf()
    private val _singleData: wT? = null
    private var _callback: (wT) -> Unit = { }
    private var mCallback: Callback? = null
    private var mBitmapList: MutableMap<Int, Bitmap>? = mutableMapOf()
    private var mImgUrlList: MutableMap<Int, String>? = mutableMapOf()
    private lateinit var mContext: Context

    var data: List<wT>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: wT
        get () = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false)
        mContext = parent.context
        return Holder(itemView)
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit = with(_data[position]) {
        holder.apply {
            Log.e("url: ", photoURL)

            mImgUrlList!![position] = photoName

            val path = File(AppConstant.FULL_MEDIA_STANDART_PATH, photoName)
            if (path.exists()) {
                ivImage.setImageBitmap(BitmapFactory.decodeFile(path.absolutePath))
            } else {
                GlideApp.with(mContext)
                        .load(AppConstant.getServerName() + photoURL)
                        .override(128, 128)
                        .thumbnail(0.5f)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivImage)
            }


            ivImage.setOnClickListener {
                val urlList = mImgUrlList!!.map {
                    it.value
                }
                mCallback!!.onClick(position, urlList)
            }
        }
    }

    fun setCallback(callback: (wT) -> Unit) {
        _callback = callback
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(position: Int, urlList: List<String>)
    }

    class Holder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val ivImage = view.ivItemImage
    }
}
