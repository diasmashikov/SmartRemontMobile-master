package kz.cheesenology.smartremontmobile.view.main.remontlist

import androidx.recyclerview.widget.DiffUtil

class RemontListDiffUtil(private val oldList: List<RemontAdapterModel>, private val newList: List<RemontAdapterModel>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].remontID == newList[newItemPosition].remontID
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }


}