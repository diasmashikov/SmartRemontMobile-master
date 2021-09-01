package kz.cheesenology.smartremontmobile.view.request.checklist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_header_request_check_list.view.*
import kotlinx.android.synthetic.main.item_request_check_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.model.RequestCheckListRoomModel
import kz.cheesenology.smartremontmobile.model.RequestCheckListSectionModel


typealias CheckListType = RequestCheckListSectionModel

class RequestCheckListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _data: MutableList<CheckListType> = mutableListOf()
    private val _singleData: CheckListType? = null
    private var mCallback: Callback? = null

    var data: List<CheckListType>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: CheckListType
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header_request_check_list, parent, false)
            HeaderHolder(v)
        } else {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_request_check_list, parent, false)
            Holder(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        val item = _data[position]
        return if (!item.isRow) {
            0
        } else {
            1
        }
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int): Unit =
        with(_data[position]) {
            val item = _data[position]
            if (item.isRow) {
                val h = holder as Holder
                h.tvCheckName.text = item.row!!.draft_check_list_name

                if (item.row!!.is_accepted == 1) {
                    h.switchStatus.isChecked = true
                } else {
                    h.switchStatus.isChecked = false
                }

                h.switchStatus.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        mCallback!!.onSwitchChange(this@with, !h.switchStatus.isChecked)
                        true
                    } else false
                }

                h.tvCheckName.setOnClickListener {
                    mCallback!!.onClick(item.row!!)
                }



            } else {
                val h = holder as HeaderHolder
                h.tvHeader.text = item.section!!
            }
        }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: RequestCheckListRoomModel)
        fun onSwitchChange(requestCheckListSectionModel: CheckListType, checked: Boolean)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCheckName: TextView = view.tvItemRequestCheckName
        val switchStatus: SwitchCompat = view.switchItemCheckList
    }

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.tvHeaderRequestCheckList
    }
}