package kz.cheesenology.smartremontmobile.view.main.notification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_notification_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.notification.NotificationEntity

typealias NotificationType = NotificationEntity

class ServerNotificationAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ServerNotificationAdapter.Holder>() {

    private val _data: MutableList<NotificationType> = mutableListOf()
    private val _singleData: NotificationType? = null
    private var mCallback: Callback? = null

    var data: List<NotificationType>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: NotificationType
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification_list, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit = with(_data[position]) {
        holder.apply {
            tvTitle.text = title
            tvDetail.text = text
            tvDate.text = dateCreate
        }
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: NotificationType)
    }

    class Holder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var tvTitle = view.tvItemNotificationListTitle
        var tvDetail = view.tvItemNotificationDetail
        var tvDate = view.tvItemNotificationListDate
    }
}
