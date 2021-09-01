package kz.cheesenology.smartremontmobile.view.request.requestlist

import kz.cheesenology.smartremontmobile.data.requestlist.RequestListEntity
import kz.cheesenology.smartremontmobile.model.RequestSendCntModel
import moxy.MvpView

interface RequestListView : MvpView  {
    fun setRequestList(it: List<RequestListEntity>?)
    fun showPB()
    fun dismissPB()
    fun showToast(s: String?)
    fun showSendDialog(count: RequestSendCntModel)
    fun closeSendDialog()
    fun showRequestFilterList(filterStatus: Int)
}