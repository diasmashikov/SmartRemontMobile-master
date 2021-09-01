package kz.cheesenology.smartremontmobile.view.main.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chat.view.*
import kz.cheesenology.brigadierapp.view.main.remont.remontlist.remontmenu.chatlist.chat.ChatPhotoAdapter
import kz.cheesenology.brigadierapp.view.main.remont.remontlist.remontmenu.chatlist.chat.ChatPhotoType
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.domain.RequestConstant
import kz.cheesenology.smartremontmobile.model.ChatMessageListModel
import kotlin.properties.Delegates

typealias ChatType = ChatMessageListModel

class ChatAdapter(val context: ChatActivity) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var _data: MutableList<ChatType> = mutableListOf()
    private val _singleData: ChatType? = null
    private var mCallback: Callback? = null

    private val viewPool = RecyclerView.RecycledViewPool()
    private var adapterPhoto: ChatPhotoAdapter by Delegates.notNull()
    private var adapterFiles: ChatFilesAdapter by Delegates.notNull()

    var data: List<ChatType>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: ChatType
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = _data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int): Unit = with(_data[position]) {
        holder.apply {
            tvAuthor.text = fio
            tvMessage.text = message
            tvMessageDate.text = dateChat

            when (requestStatusID) {
                RequestConstant.STATUS_REQUEST_SUCCESS -> {
                    tvMessageError.visibility = View.GONE
                    tvMessageDate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_done_all_black_16dp,
                        0
                    )
                }
                RequestConstant.STATUS_REQUEST_CREATE -> {
                    tvMessageError.visibility = View.GONE
                    tvMessageDate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_done_black_16dp,
                        0
                    )
                }
                RequestConstant.STATUS_REQUEST_ERROR -> {
                    tvMessageError.visibility = View.VISIBLE
                    tvMessageError.text = requestError
                    tvMessageDate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_done_red_16dp,
                        0
                    )
                }
                else -> {
                    tvMessageError.visibility = View.GONE
                    tvMessageDate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_done_all_black_16dp,
                        0
                    )
                }
            }

            itemView.setOnClickListener {
                mCallback!!.onClick(this@with)
            }
        }



        //PHOTO LIST
        holder.rvPhotoFiles.layoutManager = GridLayoutManager(holder.rvPhotoFiles.context, 3)
        adapterPhoto = ChatPhotoAdapter(context)
        holder.rvPhotoFiles.adapter = adapterPhoto
        holder.rvPhotoFiles.setRecycledViewPool(viewPool)
        adapterPhoto.data = photoList
        adapterPhoto.setCallback(object: ChatPhotoAdapter.Callback {
            override fun onClick(model: ChatPhotoType) {
                //mCallback!!.photoClick(position, )
            }
        })


        //FILES LIST
        holder.rvFiles.layoutManager = LinearLayoutManager(holder.rvFiles.context)
        adapterFiles = ChatFilesAdapter(context)
        holder.rvFiles.adapter = adapterFiles
        adapterFiles.data = filesList
        adapterFiles.setCallback(object: ChatFilesAdapter.Callback{
            override fun onClick(model: ChatPhotoType) {
                mCallback!!.onFilesClick(model)
            }
        })
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: ChatType)
        fun onFilesClick(model: ChatPhotoType)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAuthor = view.tvItemChatAuthor!!
        val tvMessage = view.tvItemChatMessage!!
        val tvMessageDate = view.tvItemChatMessageDate!!
        val tvMessageError = view.tvItemChatMessageError!!
        val rvPhotoFiles = view.rvChatItemPhotoList!!
        val rvFiles = view.rvChatItemFilesList!!
    }
}