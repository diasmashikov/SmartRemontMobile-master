package kz.cheesenology.smartremontmobile.view.main.checkaccept

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_history_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryEntity

typealias T = CheckListHistoryEntity

class HistoryListAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<HistoryListAdapter.Holder>() {

    private val _data: MutableList<T> = mutableListOf()
    private val _singleData: T? = null
    private var _callback: (T) -> Unit = { }
    private var mCallback: Callback? = null

    var data: List<T>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: T
        get () = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history_list, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int): Unit = with(_data[position]) {
        holder.apply {
            tvHistoryDefectCnt.text = defectCnt.toString()
            tvHistoryDate.text = dateCreate
        }
    }

    fun setCallback(callback: (T) -> Unit) {
        _callback = callback
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun onClick(model: MutableList<T>, position: Int)
    }

    class Holder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvHistoryDefectCnt = view.tvItemHistoryListDefectCnt
        val tvHistoryDate = view.tvItemHistoryListDate
    }
}