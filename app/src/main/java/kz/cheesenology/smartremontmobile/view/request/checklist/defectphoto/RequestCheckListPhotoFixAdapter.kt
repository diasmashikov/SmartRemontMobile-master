package kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_request_check_list.view.*
import kotlinx.android.synthetic.main.item_request_check_list.view.tvItemRequestCheckName
import kotlinx.android.synthetic.main.item_request_photo_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoEntitiy
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.model.request.ClientRequestDraftCheckModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.url
import java.io.File

typealias T = CheckRequestPhotoEntitiy

class RequestCheckListPhotoFixAdapter : RecyclerView.Adapter<RequestCheckListPhotoFixAdapter.Holder>() {

    private var _data: MutableList<T> = mutableListOf()
    private val _singleData: T? = null
    private var mCallback: Callback? = null


    lateinit var parentContext : Context

    var data: List<T>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }


    var singleData: T
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_request_photo_list, parent, false)
        parentContext = parent.context
        return Holder(itemView)
    }

    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit =
        with(_data[position]) {
            holder.apply {

                tvPhotoDate.text = date_create
                etComment.text = comment

                itemView.setOnClickListener {
                    mCallback!!.onClick(this@with)
                }

                val file = File(AppConstant.FULL_MEDIA_PHOTO_PATH + requestCheckPhotoName)
                if (file.exists()) {
                    GlideApp.with(parentContext)
                        .load(file)
                        .thumbnail(0.5f)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivPhoto)
                } else {
                    GlideApp.with(parentContext)
                        .load(AppConstant.getServerName() + requestCheckPhotoUrl)
                        .thumbnail(0.5f)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivPhoto)
                }
            }
        }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: T)

    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: ImageView = view.ivItemRequestPhotoList
        val tvPhotoDate: TextView = view.tvItemRequestPhotoDate
        val etComment: TextView = view.etItemRequestPhotoComment
    }
}