package kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus

import android.graphics.Bitmap
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListEntity
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryEntity
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusEntity
import moxy.MvpView
import java.io.File

interface RequestCheckStatusListView : MvpView {
    fun setData(it: List<RequestCheckListHistoryEntity>?)
    fun closeAccpeptDialog()
    fun closeRejectDialog()
    fun showToast(s: String)
    fun showViewPager(
        position: Int,
        bitmaps: MutableList<Bitmap?>,
        imagesPath: MutableList<String?>
    )
    fun closeViewPagerFromDialog(imagePosition: Int)
    fun getPhotoUrls(clientRequestDraftCheckHistoryId: Int?, okk_comment: String?)
    fun showPicturesDialog(list: List<PhotoDraftStatusEntity>)
    fun closeViewPagerFromList(imagePosition: Int)
    fun getDataFromDB(list: List<RequestListEntity>)
    fun onBackPressed()
    fun deleteFilePath(filePath: String?, position: Int)



}