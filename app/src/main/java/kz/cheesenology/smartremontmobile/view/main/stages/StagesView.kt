package kz.cheesenology.smartremontmobile.view.main.stages

import kz.cheesenology.smartremontmobile.data.check.list.CheckListEntity
import kz.cheesenology.smartremontmobile.data.groupchat.GroupChatEntity
import kz.cheesenology.smartremontmobile.data.rooms.RoomEntity
import kz.cheesenology.smartremontmobile.data.stage.StageEntity
import kz.cheesenology.smartremontmobile.model.StageStatusHistSingleModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModelNew
import moxy.MvpView

interface StagesView : MvpView {
    fun showDialog()
    fun dismissDialog()
    fun showGetListFromServerError(errMsg: String)
    fun setFullStages(stages: List<StageEntity>, activeStageID: Int)
    fun setRooms(rooms: List<RoomEntity>)
    fun setChangeStageStatusVisibility(visible: Boolean)
    fun showToast(errMsg: String)
    fun dismissStateChangeDialog()
    fun updateChildOnPosition(globalPosition: Int, isAccepted: Int, defectCnt: Int?, norm: String?, checkName: String?)
    fun setExpandableHeading(item: CheckListEntity, it: List<CheckListChildModelNew>)
    fun clearExpandableList()
    fun navigateToCheckList(info: CheckListChildModel, globalPosition: Int, remontCheckListID: Int)
    fun closeStageScreen()
    fun updateListAfterFullAccept()
    fun showPlanirovka(it: String?)
    fun navigateToChat(groupChatID: Int, remontID: Int, stageName: String)
    fun navigateToCheckListNew(info: CheckListChildModelNew, globalPosition: Int)
    fun setStageStatus(stageNameByID: StageStatusHistSingleModel)
    fun clearStageStatusText()
    fun setChatStagesList(it: List<GroupChatEntity>?)
    fun navigateToRatings(remontID: Int)
    fun changeStageStatusDialog()
}
