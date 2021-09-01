package kz.cheesenology.smartremontmobile.view.camerax



import kz.cheesenology.smartremontmobile.data.chat.StageChatFileDao
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class CameraPhotoReportPresenter @Inject constructor(val userUserDefectMediaDao: UserDefectMediaDao,
                                                     var stageChatFileDao: StageChatFileDao) : MvpPresenter<CameraPhotoReportView>() {

    var remontID: Int = 0
    var stageID: Int = 0

    fun setIntentData(iremontID: Int, istageID: Int) {
        remontID = iremontID
        stageID = istageID
    }

    fun addNewPhotoReportPhoto(fileName: String, absolutePath: String, type: String, messageChatID: Int) {
        stageChatFileDao.insert(
            StageChatFileEntity(
                file_name = fileName,
                    file_ext = "jpg",
                    file_url = absolutePath,
                    chatMessageID = messageChatID,
                    stageChatID = 1
            )
        )
    }

}