package kz.cheesenology.smartremontmobile.view.camerax

import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaEntity
import kz.cheesenology.smartremontmobile.util.DateFormatter
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.*
import javax.inject.Inject

@InjectViewState
class CameraDefectListPresenter @Inject constructor(val userUserDefectMediaDao: UserDefectMediaDao) : MvpPresenter<CameraDefectListView>() {

    var remontID: Int = 0
    var stageID: Int = 0

    fun setIntentData(iremontID: Int, istageID: Int) {
        remontID = iremontID
        stageID = istageID
    }

    fun addNewDefectPhoto(fileName: String, absolutePath: String, type: String) {
        userUserDefectMediaDao.insert(
            UserDefectMediaEntity(
                remontID = remontID,
                fileName = fileName,
                dateCreate = DateFormatter.pointWithYearAndTime(Date()),
                isForSend = 1,
                fileUrl = absolutePath,
                fileType = type,
                defectStatus = 0,
                stage_id = stageID
            )
        )
    }

}