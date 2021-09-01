package kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto

import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoEntitiy
import moxy.MvpView

interface RequestCheckListPhotoFixView : MvpView {
    fun setData(it: List<CheckRequestPhotoEntitiy>?)
    fun closeCommentDialog()
}