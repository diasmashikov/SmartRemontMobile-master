package kz.cheesenology.smartremontmobile.view.main.photoreport

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.chat.StageChatDao
import kz.cheesenology.smartremontmobile.data.chat.StageChatEntity
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileDao
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.remont.RemontListDao
import kz.cheesenology.smartremontmobile.data.request.RequestDao
import kz.cheesenology.smartremontmobile.data.request.RequestEntity
import kz.cheesenology.smartremontmobile.data.rooms.RoomDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomEntity
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.domain.RequestConstant
import kz.cheesenology.smartremontmobile.model.CheckListDefectSelectModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


@InjectViewState
class PhotoReportPresenter @Inject constructor(
        var stageChatFileDao: StageChatFileDao,
        var userUserDefectMediaDao: UserDefectMediaDao,
        var roomDao: RoomDao,
        var remontCheckListDao: RemontCheckListDao,
        var userDefectMediaDao: UserDefectMediaDao,
        var stageChatDao: StageChatDao,
        var requestDao: RequestDao,
        var chatDao: StageChatDao,
        var remontListDao: RemontListDao
) : MvpPresenter<PhotoReportView>() {

    var remontID: Int = 0
    var currentStageID: Int = 0

    var photoReportViewListener: PhotoReportView? = null


    var roomList: List<RoomEntity>? = null
    var checkList: List<CheckListDefectSelectModel>? = null

    fun setIntentData(iRemontID: Int, iStageID: Int) {
        remontID = iRemontID
        currentStageID = iStageID
    }

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val chatIDs = stageChatDao.getChatIDs(remontID)
        //val list_photo_files = stageChatFileDao.getPhotoFileList(chatID.toInt())

        stageChatDao.getListik(remontID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setPhotoReportList(it, chatIDs)
                }, {
                    it.printStackTrace()
                })


    }


    @SuppressLint("CheckResult")
    fun getPhotoFiles(chatMessageID: Int?, comment: String?) {
        stageChatFileDao.getPhotoFileList(chatMessageID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.showPhotosDialog(it, comment)
                }, {
                    it.printStackTrace()
                })
    }

    @SuppressLint("CheckResult")
    fun getPhotoFilesToDelete(chatMessageID: Int?) {
        stageChatFileDao.getPhotoFileList(chatMessageID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.deletePhotoFiles(it)
                }, {
                    it.printStackTrace()
                })
    }

    fun deletePhotoReportFiles(chatMessageID: Int) {
        stageChatFileDao.deletePhotoReportFiles(chatMessageID)
    }

    fun deletePhotoReport(chatMessageID: Int) {
        stageChatDao.deletePhotoReport(chatMessageID)
    }

    fun updateMessage(comment: String, chatMessageID: Int) {
        if (chatMessageID != null) {
            stageChatDao.updateMessageWithNewComment(comment, chatMessageID)
        }
    }


    private fun createMessageRequestID(message: String): Long {
        val id = requestDao.insert(
                RequestEntity(
                        requestTypeID = RequestConstant.REQUEST_SEND_MESSAGE,
                        requestStatusID = RequestConstant.STATUS_REQUEST_CREATE,
                        data = JSONObject(
                                mapOf(
                                        "remont_id" to remontID,
                                        "group_chat_id" to currentStageID,
                                        "message" to message
                                )
                        ).toString(),
                        remontID = remontID,
                        dateCreate = AppConstant.getCurrentDateFull(),
                        randomNum = AppConstant.getRundomNumber()
                )
        )
        return id
    }

    fun updatePhotoReportDateSend(date_send: String, remontID: Int){

        remontListDao.updatePhotoReportSendDate(date_send, remontID)

    }

    fun savePhotoReport() {

        val ID = createMessageRequestID("").toInt()


        try {


            chatDao.addNewMessage(
                    StageChatEntity(
                            groupChatID = currentStageID,
                            employeeID = 1,
                            dateChat = AppConstant.getCurrentDateFull(),
                            message = "Фотоотчёт",
                            remontID = remontID,
                            fio = PrefUtils.prefs.getString("fio", "")!!,
                            requestID = ID,
                            is_photo_report = 1,

                            )
            )

            var chatMessageID = stageChatDao.getChatID(ID)
            //photoReportViewListener?.showToast(chatMessageID.toString())
            photoReportViewListener?.getChatMessageID(chatMessageID)


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun setAttachFromFile(uriList: ArrayList<String>, type: String?) {


    }


}