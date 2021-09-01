package kz.cheesenology.smartremontmobile.view.main.remontlist

import kz.cheesenology.smartremontmobile.data.chat.StageChatEntity
import kz.cheesenology.smartremontmobile.model.RemontListDBModel
import moxy.MvpView

interface RemontListView: MvpView {
    fun showDialog(s: String)
    fun dismissDialog()
    fun showToast(s: String)
    fun refreshList()
    fun setListData(value: List<RemontListDBModel>)
    fun showStatsFragment(fullListID: List<Int>)
    fun updateFilterSelectedItems(list: Int, statusFinished: Int, statusCanceled: Int, statusFinished1: Int)
    fun showStatusFilterDialog(statusRemont: Int, statusStage: Int, statusOkk: Int, stageStatus: Int)
    fun navigateToNotificationActivity()
    fun setNotificationCount(i: Int)
    fun needsUpdate(b: Boolean)
}