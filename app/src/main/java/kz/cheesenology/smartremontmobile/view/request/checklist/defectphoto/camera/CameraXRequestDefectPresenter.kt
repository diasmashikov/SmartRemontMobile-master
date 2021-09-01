package kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto.camera

import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoEntitiy
import kz.cheesenology.smartremontmobile.util.AppConstant
import moxy.MvpPresenter
import javax.inject.Inject

class CameraXRequestDefectPresenter @Inject constructor(val checkRequestPhotoDao: CheckRequestPhotoDao) : MvpPresenter<CameraXRequestDefectView>() {


    var checkID: Int? = null
    var clientRequestID: Int? = null
    var draftCheckID: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun addNewPhoto(fileName: String, absolutePath: String, s: String) {
        checkRequestPhotoDao.insert(
            CheckRequestPhotoEntitiy(
                requestCheckListID = checkID,
                clientRequestID = clientRequestID,
                requestCheckPhotoName = fileName,
                draft_check_list_id = draftCheckID,
                requestCheckPhotoUrl = absolutePath,
                requestCheckPhotoType = s,
                date_create = AppConstant.getCurrentDateFull(),
                is_for_send = 1,
            )
        )
    }

    fun setCheckID(icheckID: Int, iClientRequestID: Int, idraftCheckID: Int) {
        checkID = icheckID
        clientRequestID = iClientRequestID
        draftCheckID = idraftCheckID
    }
}