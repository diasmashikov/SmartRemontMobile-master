package kz.cheesenology.smartremontmobile.view.stagestatushistory

import kz.cheesenology.smartremontmobile.model.StageStatusHistoryListModel
import moxy.MvpView

interface StageStatusHistoryView : MvpView {
    fun setList(it: List<StageStatusHistoryListModel>)

}