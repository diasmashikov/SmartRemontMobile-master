package kz.cheesenology.smartremontmobile.view.main.clientphoto

import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_child_photo_view.view.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.di.GlideApp
import kz.cheesenology.smartremontmobile.view.main.clientphoto.expand.PhotoChildModel

class ClientSectionAdapter(itemList: List<PhotoChildModel>) : BaseClientPhotoAdapter(itemList) {

    private var mImgPathList: MutableMap<Int, String>? = mutableMapOf()

    override fun onPlaceSubheaderBetweenItems(position: Int): Boolean {
        val currentRoom = photoList[position].roomName
        val nextRoom = photoList[position + 1].roomName

        return currentRoom != nextRoom
    }

    override fun onBindItemViewHolder(holder: MovieViewHolder, position: Int) {
        val model = photoList[position]

        holder.itemView.childPhotoItemView.setOnClickListener { v ->
            val pathList = mImgPathList!!.map {
                it.value
            }
            onItemClickListener.onItemClicked(model, pathList, position)
        }

        if (model.isUser) {
            mImgPathList!![position] = model.imgPath!!
            holder.itemView.childPhotoItemView.setImageBitmap(model.bitmap)

            holder.itemView.childPhotoItemView.setOnCreateContextMenuListener { menu, v, _ ->
                menu.add(0, v.id, 0, "Удалить фото").setOnMenuItemClickListener {
                    onItemClickListener.deleteUserItem(model)
                    true
                }
            }

        } else {
            mImgPathList!![position] = model.imageUrl!!
            GlideApp.with(holder.itemView)
                    .load(model.imageUrl)
                    .override(128, 128)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.itemView.childPhotoItemView)

            holder.itemView.childPhotoItemView.setOnCreateContextMenuListener { menu, v, _ ->
                menu.add(0, v.id, 0, "Удалить фото").setOnMenuItemClickListener {
                    onItemClickListener.deleteServerItem(model)
                    true
                }
            }
        }
    }

    override fun onBindSubheaderViewHolder(subheaderHolder: SubheaderHolder, nextItemPosition: Int) {
        super.onBindSubheaderViewHolder(subheaderHolder, nextItemPosition)
        val context = subheaderHolder.itemView.context
        val nextMovie = photoList[nextItemPosition]
        val sectionSize = getSectionSize(getSectionIndex(subheaderHolder.adapterPosition))
        val room = nextMovie.roomName
        val subheaderText = String.format(
                context.getString(R.string.subheader),
                room,
                context.resources.getQuantityString(R.plurals.item, sectionSize, sectionSize)
        )
        subheaderHolder.mSubheaderText.text = subheaderText
    }
}
