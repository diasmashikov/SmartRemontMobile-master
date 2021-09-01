package kz.cheesenology.smartremontmobile.view.main.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chat_files.view.*
import kz.cheesenology.brigadierapp.view.main.remont.remontlist.remontmenu.chatlist.chat.ChatPhotoType
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity
import kz.cheesenology.smartremontmobile.util.underline


typealias ChatFilesType = StageChatFileEntity

class ChatFilesAdapter(val context: ChatActivity) : RecyclerView.Adapter<ChatFilesAdapter.ViewHolder>() {

    private var _data: MutableList<ChatFilesType> = mutableListOf()
    private val _singleData: ChatFilesType? = null
    private var mCallback: Callback? = null

    var data: List<ChatFilesType>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: ChatFilesType
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_files, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = _data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int): Unit = with(_data[position]) {
        holder.apply {

            tvFileName.text = file_name
            tvFileName.underline()

            itemView.setOnClickListener {
                mCallback!!.onClick(this@with)
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
        val tvFileName = view.tvItemChatFilesName
    }
}