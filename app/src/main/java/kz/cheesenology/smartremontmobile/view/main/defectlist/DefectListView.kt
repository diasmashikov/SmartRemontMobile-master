package kz.cheesenology.smartremontmobile.view.main.defectlist

import kz.cheesenology.smartremontmobile.data.rooms.RoomEntity
import kz.cheesenology.smartremontmobile.model.CheckListDefectSelectModel
import kz.cheesenology.smartremontmobile.model.DefectListModel
import moxy.MvpView

interface DefectListView : MvpView{
    fun setDefectList(it: List<DefectListModel>)
    fun showDefectInfoDialog(model: MutableList<T>, roomList: List<RoomEntity>?, checkList: List<CheckListDefectSelectModel>?)
    fun showItemPhoto(defectListModel: T)
    fun showItemVideo(defectListModel: T)
    fun navigateToSendDialog(remontID: Int)

}
