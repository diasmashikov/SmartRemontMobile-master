package kz.cheesenology.brigadierapp.view.main.remont.remontlist.remontmenu.chatlist.chat

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.chat.StageChatDao
import kz.cheesenology.smartremontmobile.data.chat.StageChatEntity
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileDao
import kz.cheesenology.smartremontmobile.data.chat.StageChatFileEntity
import kz.cheesenology.smartremontmobile.data.request.RequestDao
import kz.cheesenology.smartremontmobile.data.request.RequestEntity
import kz.cheesenology.smartremontmobile.domain.RequestConstant
import kz.cheesenology.smartremontmobile.domain.work.WorkRunner
import kz.cheesenology.smartremontmobile.model.ChatMessageListModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.PrefUtils
import kz.cheesenology.smartremontmobile.view.main.chat.ChatType
import kz.cheesenology.smartremontmobile.view.main.chat.ChatView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.json.JSONObject
import java.io.File
import java.util.ArrayList
import javax.inject.Inject

@InjectViewState
class ChatPresenter @Inject constructor(
    val chatDao: StageChatDao,
    val chatFileDao: StageChatFileDao,
    val requestDao: RequestDao
) : MvpPresenter<ChatView>() {

    var remontID: Int = 0
    var stageID: Int = 0

    fun setIntentData(
        iRemontID: Int,
        iStageID: Int
    ) {
        remontID = iRemontID
        stageID = iStageID
    }

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val adapterList = mutableListOf<ChatMessageListModel>()

        chatDao.getList(remontID, stageID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                Completable.merge {
                    list.forEach { item ->
                        val photoList = chatFileDao.getPhotoFileListOnMessage(item.stageChatID)
                        val filesList = chatFileDao.getFilesListOnMessage(item.stageChatID)
                        adapterList.add(
                            ChatMessageListModel(
                                item.stageChatID,
                                item.groupChatID,
                                item.chatFio,
                                item.dateChat,
                                item.message,
                                item.remontID,
                                item.requestStatusID,
                                item.errorMsg,
                                photoList,
                                filesList
                            )
                        )
                    }
                    it.onComplete()
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (list.isNullOrEmpty()) {
                            viewState.showEmptyView()
                        } else {
                            viewState.setList(adapterList)
                        }
                    }
            }
    }

    fun addMessage(message: String) {
        chatDao.addNewMessage(
            StageChatEntity(
                groupChatID = stageID,
                employeeID = 1,
                dateChat = AppConstant.getCurrentDateFull(),
                client_id = 1,
                message = message,
                remontID = remontID,
                fio = PrefUtils.prefs.getString("fio", "")!!,
                requestID = createMessageRequestID(message).toInt()
            )
        )

        viewState.clearInput()
        WorkRunner.startSyncWorker()
    }

    private fun createMessageRequestID(message: String): Long {
        val id = requestDao.insert(
            RequestEntity(
                requestTypeID = RequestConstant.REQUEST_SEND_MESSAGE,
                requestStatusID = RequestConstant.STATUS_REQUEST_CREATE,
                data = JSONObject(
                    mapOf(
                        "remont_id" to remontID,
                        "group_chat_id" to stageID,
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

    fun setAttachFromFile(
        uriPathList: ArrayList<String>,
        type: String,
        message: String?
    ) {
        var text = message
        if (text.isNullOrEmpty())
            text = " "

        val ID = createMessageRequestID(text).toInt()

        try {
            val chatID = chatDao.addNewMessage(
                StageChatEntity(
                    groupChatID = stageID,
                    employeeID = 1,
                    dateChat = AppConstant.getCurrentDateFull(),
                    client_id = 1,
                    message = text,
                    remontID = remontID,
                    fio = PrefUtils.prefs.getString("fio", "")!!,
                    requestID = ID
                )
            )

            val chatFileList: MutableList<StageChatFileEntity> = mutableListOf()
            uriPathList.forEach {
                val file = File(it)
                if (file.exists()) {
                    chatFileList.add(
                        StageChatFileEntity(
                            stageChatID = chatID.toInt(),
                            file_name = file.name,
                            file_ext = file.extension,
                            file_url = it,
                                chatMessageID = 1
                        )
                    )
                }
            }

            chatFileDao.insertAll(chatFileList)

            viewState.clearInput()
            WorkRunner.startSyncWorker()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("CheckResult")
    fun checkForFiles(model: ChatType) {

    }
}