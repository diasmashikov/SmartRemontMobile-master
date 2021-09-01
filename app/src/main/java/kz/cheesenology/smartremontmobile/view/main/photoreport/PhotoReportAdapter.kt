package kz.cheesenology.smartremontmobile.view.main.photoreport

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_photo_report.view.*
import kotlinx.android.synthetic.main.item_defect_list.view.*
import kotlinx.android.synthetic.main.item_photo_report.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.model.ChatMessageModel
import java.util.*


typealias T = ChatMessageModel

class PhotoReportAdapter : RecyclerView.Adapter<PhotoReportAdapter.Holder>() {

    private var _data: MutableList<T> = mutableListOf()
    private val _singleData: T? = null
    private var mCallback: Callback? = null
    var photoReportViewListener: PhotoReportView? = null
    var show_type: Int? = null

    //private var multiSelect = false
    //private var selectedList: ArrayList<T> = arrayListOf()

    //var actionMode: ActionMode? = null

    lateinit var parentContext: Context

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoReportAdapter.Holder {
        val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_photo_report, parent, false)
        parentContext = parent.context
        return PhotoReportAdapter.Holder(itemView)
    }


    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: PhotoReportAdapter.Holder, position: Int): Unit =
            with(_data[position]) {
                holder.apply {

                    if (message!!.contains("Фотоотчёт | Этап:")) {
                        itemView.setBackgroundResource(R.color.yellow_A400)
                        show_type = 1
                    } else {
                        itemView.setBackgroundResource(android.R.color.transparent)
                        show_type = 0
                    }


                    tvComment.text = message

                    tvDate.text = dateChat


                }


                // when (isForSend) {
                // 1 -> {
                //   ivSend.visibility = View.VISIBLE
                // ivSend.setImageResource(R.drawable.ic_done_gray_24dp)
                //}
                //else -> {
                //  ivSend.visibility = View.VISIBLE
                //ivSend.setImageResource(R.drawable.ic_done_all_green_24dp)
                //}
                //}


                holder.itemView.setOnClickListener {
                    mCallback!!.onFileClicked(this@with, chatMessageID, position, message, dateChat, remontID)
                }

                holder.itemView.setOnLongClickListener {
                    val popup = PopupMenu(holder.itemView.context, holder.itemView)
                    popup.inflate(R.menu.menu_delete_photo)
                    popup.setOnMenuItemClickListener { select ->
                        when (select.itemId) {
                            R.id.menu_delete_draft_photo -> {
                                //TODO добавить удаление файла
                                mCallback?.deletePhotoReport(chatMessageID)
                                _data.removeAt(position)
                                // imagesPath.remove(imagesPath[position])

                                notifyDataSetChanged()
                            }
                        }
                        false
                    }
                    popup.show()
                    true
                }


            }


    fun setCallback(callback: Callback) {
        this.mCallback = callback
        photoReportViewListener?.showToast("CALLBACK IS CALLED")
    }

    interface Callback {
        fun onFileClicked(chatMessageModel: T, chatMessageID: Int?, rvPositionItem: Int, comment: String?, dateChat: String?, remontID: Int)
        fun deletePhotoReport(chatMessageID: Int?)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvComment = view.tvItemPhotoReportComment
        val tvDate = view.tvItemPhotoReportDate

    }


}


/*
    private fun selectItem(item: T, itemView: View) {
        if (multiSelect) {
            if (selectedList.contains(item)) {
                selectedList.remove(item)
                itemView.setBackgroundColor(Color.TRANSPARENT)
                if (selectedList.isNullOrEmpty()) {
                    if (actionMode != null) {
                        actionMode?.finish()
                    }
                }
            } else {
                selectedList.add(item)
                itemView.setBackgroundColor(Color.LTGRAY)
            }
        }
    }

 */

/*
    private val actionModeCallbacks = (object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            multiSelect = true
            mode?.menuInflater?.inflate(R.menu.menu_select_defects, menu)
            actionMode = mode
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            /*for (intItem in selectedList) {
                _data.remove(intItem)
            }*/

            when (item!!.itemId) {
                R.id.menu_list_defect_share -> {
                    mCallback!!.sharePhotos(ArrayList(selectedList))
                    mode?.finish()
                    true
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            multiSelect = false
            selectedList.clear()
            notifyDataSetChanged()
        }


    })


 */


/*
    fun closeActionMode() {
        if (actionMode != null) {
            actionMode?.finish()
        }
    }

 */




