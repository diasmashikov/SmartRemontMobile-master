package kz.cheesenology.smartremontmobile.view.request.checklist

import kz.cheesenology.smartremontmobile.model.RequestCheckListSectionModel
import moxy.MvpView

interface RequestCheckListView : MvpView {
    fun setData(list: MutableList<RequestCheckListSectionModel>)
}