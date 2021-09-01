package kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_request_draft_history.view.*
import kotlinx.android.synthetic.main.item_request_list.view.*
import kotlinx.android.synthetic.main.item_search_test.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListEntity
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryEntity
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File


class searchTestAdapter() : RecyclerView.Adapter<searchTestAdapter.Holder>() {

    //var requestCheckStatusListViewListener: RequestCheckStatusListView? = null
    private var _data: MutableList<RequestListEntity> = mutableListOf()

    var data: List<RequestListEntity>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_search_test, parent, false)
        return Holder(itemView)
    }



    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.tvOkkComment.text = data[position].manager_project_name

    }





    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOkkComment = view.tvOkkName


    }
}