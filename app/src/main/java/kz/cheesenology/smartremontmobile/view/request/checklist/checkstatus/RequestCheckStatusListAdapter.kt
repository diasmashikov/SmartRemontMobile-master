package kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_request_draft_history.view.*
import kotlinx.android.synthetic.main.item_request_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryEntity
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File

typealias requestCheckType = RequestCheckListHistoryEntity

class RequestCheckStatusListAdapter : RecyclerView.Adapter<RequestCheckStatusListAdapter.Holder>() {

    private var _data: MutableList<requestCheckType> = mutableListOf()
    private val _singleData: requestCheckType? = null
    private var mCallback: Callback? = null
    var requestCheckStatusListAdapterViewListener: RequestCheckStatusListView? = null

    var data: List<requestCheckType>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: requestCheckType
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_request_draft_history, parent, false)
        return Holder(itemView)
    }

    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit = with(_data[position]) {
        holder.apply {

            when (draft_status) {
                2 -> tvStatus.text = "Статус ЧО: Имеются замечания"
                1 -> tvStatus.text = "Статус ЧО: Принята"
            }

            tvDate.text = check_date.toString()
            tvName.text = okk_fio.toString()
            /*ivPhoto.text = okk_fio.toString()*/

            if (okk_check_date.isNullOrEmpty()) {
                tvOkkCheckDate.visibility = View.GONE
            } else {
                tvOkkCheckDate.visibility = View.VISIBLE
                tvOkkCheckDate.text = "Дата повт. проверки: " + okk_check_date.toString()
            }



            if(is_for_send == 1)
            {
                itemView.setBackgroundResource(R.color.yellow_A400)
            }
            else
            {
                itemView.setBackgroundResource(android.R.color.transparent)
            }

            val file = File(AppConstant.FULL_MEDIA_PHOTO_PATH + draft_defect_file_name)
            if (file.exists()) {
                ivPhoto.visibility = View.VISIBLE
                GlideApp.with(itemView.context)
                    .load(file)
                    .override(128, 128)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPhoto)
            } else {
                when (draft_status) {
                    2 -> GlideApp.with(itemView.context)
                            .load(AppConstant.getServerName() + draft_defect_file_url)
                            .override(128, 128)
                            .skipMemoryCache(true)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivPhoto)
                    1 -> ivPhoto.visibility = View.VISIBLE
                }

            }

            itemView.setOnClickListener {
                mCallback!!.onClick(this@with)
            }



            if(draft_status == 2)
            {

            }
            else if (draft_status == 1)
            {

                ivPhoto.setOnClickListener {
                requestCheckStatusListAdapterViewListener?.getPhotoUrls(client_request_draft_check_history_id, okk_comment)
            }

            }


        }
    }


    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(requestListEntity: requestCheckType)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStatus = view.tvItemDraftHistStatus
        val tvDate = view.tvItemDraftHistDate
        val tvName = view.tvItemDraftHistName
        val ivPhoto = view.ivItemDraftHistPhoto
        val tvOkkCheckDate = view.ivItemDraftHistOkkCheckDate

    }
}