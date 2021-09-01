package kz.cheesenology.smartremontmobile.view.request.checklist

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.requestlist.checkaccept.RequestCheckAcceptDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkaccept.RequestCheckAcceptEntity
import kz.cheesenology.smartremontmobile.data.requestlist.checklist.RequestCheckListDao
import kz.cheesenology.smartremontmobile.model.RequestCheckListRoomModel
import kz.cheesenology.smartremontmobile.model.RequestCheckListSectionModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import moxy.MvpPresenter
import javax.inject.Inject

class RequestCheckListPresenter @Inject constructor(
    val requestCheckListDao: RequestCheckListDao,
    val requestCheckAcceptDao: RequestCheckAcceptDao
) :
    MvpPresenter<RequestCheckListView>() {

    var clientRequestID : Int? = null

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        showCheckList()
    }

    @SuppressLint("CheckResult")
    private fun showCheckList() {
        val list = mutableListOf<RequestCheckListSectionModel>()
        requestCheckListDao.getCheckListPID()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it.forEach { item ->
                    list.add(RequestCheckListSectionModel.createSection(item.draft_check_list_name!!))
                    val sublist = requestCheckListDao.getSubCheckList(item.draft_check_list_id, clientRequestID)
                    sublist.forEach { subitem ->
                        list.add(RequestCheckListSectionModel.createRow(subitem))
                    }
                }

                viewState.setData(list)
            }
    }

    fun changeCheckAcceptStatus(
        requestCheckListSectionModel: RequestCheckListRoomModel,
        checked: Boolean
    ) {

        var isActive = 0
        if (checked)
            isActive = 1

        if (requestCheckAcceptDao.exist(requestCheckListSectionModel.draft_check_list_id, clientRequestID) > 0) {
            requestCheckAcceptDao.changeCheckListStatus(
                requestCheckListSectionModel.draft_check_list_id,
                isActive,
                clientRequestID!!
            )
        } else {
            requestCheckAcceptDao.insert(RequestCheckAcceptEntity(
                client_request_id = clientRequestID,
                draft_check_list_id = requestCheckListSectionModel.draft_check_list_id,
                is_accepted = isActive,
                date_create = AppConstant.getCurrentDateFullWithoutSeconds(),
                is_for_send = 1
            ))
        }

        showCheckList()
    }

    fun setRequestID(iclientRequestID: Int) {
        clientRequestID = iclientRequestID
    }
}