package kz.cheesenology.smartremontmobile.view.main.defectlist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_defect_list.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.model.DefectListModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File


typealias T = DefectListModel

class DefectListAdapter : RecyclerView.Adapter<DefectListAdapter.Holder>() {

    private var _data: MutableList<T> = mutableListOf()
    private val _singleData: T? = null
    private var mCallback: Callback? = null

    private var multiSelect = false
    private var selectedList: ArrayList<T> = arrayListOf()

    var actionMode: ActionMode? = null

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefectListAdapter.Holder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_defect_list, parent, false)
        parentContext = parent.context
        return DefectListAdapter.Holder(itemView)
    }

    fun clearData() {
        _data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = _data.size

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: DefectListAdapter.Holder, position: Int): Unit =
        with(_data[position]) {
            holder.apply {
                //SET PHOTO
                val file = File(AppConstant.FULL_MEDIA_PHOTO_PATH + fileName)
                if (file.exists()) {
                    GlideApp.with(parentContext)
                        .load(file)
                        .thumbnail(0.5f)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(ivImage)
                } else {
                    GlideApp.with(parentContext)
                        .load(AppConstant.getServerName() + fileURL)
                        .thumbnail(0.5f)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(ivImage)
                }

                //SET CHECKLISTS
                tvCheckListName.text = checkName

                //COMMENT
                tvComment.text = comment

                //SET DATE
                tvDate.text = dateCreate

                when (defectStatus) {
                    0 -> {
                        ivStatus.setImageResource(R.drawable.ic_thumb_down_red_24dp)
                    }
                    1 -> {
                        ivStatus.setImageResource(R.drawable.ic_thumb_up_green_24dp)
                    }
                    else -> {
                        ivStatus.setImageResource(R.drawable.defect_default_small)
                    }
                }

                if (!audioName.isNullOrEmpty()) {
                    ivAudioStatus.visibility = View.VISIBLE
                } else {
                    ivAudioStatus.visibility = View.GONE
                }

                if (fileType == "video") {
                    ivVideoType.visibility = View.VISIBLE
                } else {
                    ivVideoType.visibility = View.GONE
                }

                when (isForSend) {
                    1 -> {
                        ivSend.visibility = View.VISIBLE
                        ivSend.setImageResource(R.drawable.ic_done_gray_24dp)
                    }
                    else -> {
                        ivSend.visibility = View.VISIBLE
                        ivSend.setImageResource(R.drawable.ic_done_all_green_24dp)
                    }
                }

                //MULTISELECT LOGIC
                if (selectedList.contains(this@with)) {
                    itemView.setBackgroundColor(Color.LTGRAY)
                } else {
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                }

                itemView.setOnLongClickListener { view ->
                    (view.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                    selectItem(this@with, itemView)
                    true
                }

                itemView.setOnClickListener {
                    if (multiSelect)
                        selectItem(this@with, itemView)
                    else
                        mCallback!!.onClick(this@with)
                }

                ivImage.setOnClickListener {
                    mCallback!!.onFileClick(this@with)
                }

            }
        }

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
                R.id.menu_list_defect_delete -> {
                    mCallback!!.deleteSelectedList(ArrayList(selectedList))
                    true
                }
                R.id.menu_list_defect_accept -> {
                    mCallback!!.acceptDefectList(ArrayList(selectedList))
                }
                R.id.menu_list_defect_combine -> {
                    mCallback!!.setDefectInfoForList(ArrayList(selectedList))
                    mode?.finish()
                    true
                }
                R.id.menu_list_defect_share -> {
                    mCallback!!.shareDefects(ArrayList(selectedList))
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

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    fun closeActionMode() {
        if (actionMode != null) {
            actionMode?.finish()
        }
    }

    interface Callback {
        fun onClick(model: T)
        fun setRoomIDForCheckList(roomID: Int, remontCheckListPhotoID: Int?)
        fun setCheckListIDForCheckList(checkListID: Int?, remontCheckListPhotoID: Int?)
        fun setDefectInfoForList(selectedList: MutableList<T>)
        fun deleteSelectedList(arrayList: MutableList<T>)
        fun onFileClick(defectListModel: T)
        fun acceptDefectList(arrayList: java.util.ArrayList<T>)
        fun shareDefects(arrayList: java.util.ArrayList<T>)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.ivItemDefectListImage
        val tvCheckListName = view.tvItemDefectCheckListName
        val tvComment = view.tvItemDefectComment
        val tvDate = view.tvItemDefectDate
        val ivStatus = view.ivItemDefectStatus
        val ivSend = view.ivItemDefectSend
        val ivAudioStatus = view.ivItemDefectAudioStatus
        val ivVideoType = view.ivItemDefectVideo
    }
}