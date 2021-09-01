package kz.cheesenology.smartremontmobile.view.stagestatushistory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_stage_status_history.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.model.StageStatusHistoryListModel

typealias statusHistoryT = StageStatusHistoryListModel

class StageStatusHistoryAdapter : RecyclerView.Adapter<StageStatusHistoryAdapter.Holder>() {

    private var _data: MutableList<StageStatusHistoryListModel> = mutableListOf()
    private val _singleData: StageStatusHistoryListModel? = null
    private var mCallback: Callback? = null

    var data: List<StageStatusHistoryListModel>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    var singleData: StageStatusHistoryListModel
        get() = _singleData!!
        set(singleValue) {
            _data.add(singleValue)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_stage_status_history, parent, false)
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
            tvTitle.text = stageName
            tvDate.text = dateCreate
            tvName.text = fio
            tvStatus.text = stageStatusName
            tvComment.text = comment
            tvRemarkName.text = remarkName
        }
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.tvItemStageStausHistoryTitle
        val tvDate = view.tvItemStageStausHistoryDate
        val tvName = view.tvItemStageStausHistoryName
        val tvStatus = view.tvItemStageStausHistoryStatus
        val tvComment = view.tvItemStageStatusHistoryComment
        val tvRemarkName = view.tvItemStageStatusHistoryRemarkName
    }
}
