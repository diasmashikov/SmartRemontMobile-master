package kz.cheesenology.smartremontmobile.view.main.clientphoto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.view.main.clientphoto.expand.PhotoChildModel

abstract class BaseClientPhotoAdapter internal constructor(var photoList: List<PhotoChildModel>) : SectionedRecyclerViewAdapter<BaseClientPhotoAdapter.SubheaderHolder, BaseClientPhotoAdapter.MovieViewHolder>() {

    internal lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClicked(model: PhotoChildModel, mImgPathList: List<String>, position: Int)
        fun onSubheaderClicked(position: Int)
        fun deleteUserItem(model: PhotoChildModel)
        fun deleteServerItem(model: PhotoChildModel)
    }

    class SubheaderHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var mSubheaderText: TextView = itemView.findViewById<View>(R.id.tvHeaderClientPhotoTitle) as TextView
        //var mAddPhoto: ImageView = itemView.findViewById<View>(R.id.ivAddClientPhotoItem) as ImageView
    }

    class MovieViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById<View>(R.id.childPhotoItemView) as ImageView
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_child_photo_view, parent, false))
    }

    override fun onCreateSubheaderViewHolder(parent: ViewGroup, viewType: Int): SubheaderHolder {
        return SubheaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header_photo_view, parent, false))
    }

    @CallSuper
    override fun onBindSubheaderViewHolder(subheaderHolder: SubheaderHolder, nextItemPosition: Int) {
        /*val isSectionExpanded = isSectionExpanded(getSectionIndex(subheaderHolder.adapterPosition))
        if (isSectionExpanded) {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.context, R.drawable.ic_keyboard_arrow_up_white_24dp))
        } else {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.context, R.drawable.ic_keyboard_arrow_down_white_24dp))
        }*/
        //subheaderHolder.mAddPhoto.setOnClickListener { v -> onItemClickListener.onSubheaderClicked(subheaderHolder.adapterPosition) }
    }

    override fun getItemSize(): Int {
        return photoList.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}
