package kz.cheesenology.smartremontmobile.view.main.photoreport


import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity
import kz.cheesenology.smartremontmobile.model.ChatMessageModel
import moxy.MvpView
import java.io.File


interface PhotoReportView : MvpView{
    fun setPhotoReportList(it: List<ChatMessageModel>, list_photo_files: Long)
    fun showPhotosDialog(list: List<StageChatFileEntity>, show_type: String?)
    fun showToast(s: String)
    fun getChatMessageID(chatMessageID: Long)
    fun showViewPager(picture_position: Int,
                      bitmaps: MutableList<File?>,
                      imagesPath: MutableList<String?>)
    fun closeViewPager(imagePosition: Int)
    fun deletePhotoFiles(list: List<StageChatFileEntity>)
    //fun showItemPhoto(defectListModel: T)

}
