package kz.cheesenology.smartremontmobile.view.main.stages

import android.annotation.SuppressLint
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListEntity
import kz.cheesenology.smartremontmobile.data.groupchat.GroupChatDao
import kz.cheesenology.smartremontmobile.data.groupchat.GroupChatEntity
import kz.cheesenology.smartremontmobile.data.remont.RemontListDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomDao
import kz.cheesenology.smartremontmobile.data.stage.StageDao
import kz.cheesenology.smartremontmobile.data.stagestatus.StageStatusDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModelNew
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject


@InjectViewState
class StagesPresenter @Inject constructor(
    val networkApi: NetworkApi,
    val groupChatDao: GroupChatDao,
    val stageDao: StageDao,
    val roomDao: RoomDao,
    val remontCheckListDao: RemontCheckListDao,
    val remontListDao: RemontListDao,
    val stageStatusDao: StageStatusDao,
    val defectMediaDao: UserDefectMediaDao
) : MvpPresenter<StagesView>() {


    var remontID: Int = 0
    var activeStageID: Int = 0
    var stageStatusID: Int = 0
    var remontStatusID: Int = 0

    val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        setStageList()
        setChatStageList()

        setRoomList()

        getCheckListByActiveStageAndRoom(activeStageID)

        //getStagesList(activeStageID, true, 1)
    }

    @SuppressLint("CheckResult")
    private fun setChatStageList() {
        groupChatDao.getStageList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setChatStagesList(it)
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    @SuppressLint("CheckResult")
    fun getCheckListByActiveStageAndRoom(stageID: Int) {
        compositeDisposable.add(remontCheckListDao.getHeaderCheckList(stageID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it1 ->
                viewState.clearExpandableList()
                for (item in it1) {
                    val it =
                        remontCheckListDao.getCheckListByHeaderNew(item.checkListID!!, remontID)
                    viewState.setExpandableHeading(item, it)
                }
                if (activeStageID == stageID)
                    viewState.setStageStatus(
                        stageStatusDao.getStageNameByID(
                            stageStatusID,
                            remontID,
                            activeStageID
                        )
                    )
                else
                    viewState.clearStageStatusText()
            })
    }

    @SuppressLint("CheckResult")
    private fun setStageList() {
        stageDao.getStageList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.setFullStages(it, activeStageID)
            }
    }

    @SuppressLint("CheckResult")
    private fun setRoomList() {
        roomDao.getRoomListByRemontID(remontID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.setRooms(it)
            }
    }

    @SuppressLint("CheckResult")
    fun sendChangeStageStatus(
        status: String,
        statusComment: String,
        desc: String
    ) {
        var statusID = 0
        when (status) {
            "Имеются замечания" -> {
                statusID = 2
            }
            "Этап завершен" -> {
                statusID = 4
            }
        }

        var descCode = ""
        when (desc) {
            "ОКК проверено" -> {
                descCode = "OKK_CHECKED"
            }
            "Сдан клиенту без ОКК" -> {
                descCode = "OKK_NOT_CHECKED"
            }
            "Этап завершен с замечаниями" -> {
                descCode = "DEFECT_EXIST"
            }
            "Ложный вызов" -> {
                descCode = "FAKE_CALL"
            }
        }

        Completable.fromAction {
            remontListDao.updateRemontStageStatus(
                remontID,
                statusID,
                statusComment,
                descCode
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                if (activeStageID >= 5 && statusID == 4) {
                    viewState.navigateToRatings(remontID)
                } else {
                    viewState.closeStageScreen()
                }
            }
    }

    @SuppressLint("CheckResult")
    fun sendCheckListStatusWithData(
        model: CheckListChildModel,
        status: Int,
        roomID: Int,
        globalPosition: Int
    ) {

        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        val jsonObj = JSONObject(
            mapOf(
                "check_status" to status,
                "defect_number" to "",
                "comment" to "",
                "check_list_id" to model.checkListID,
                "room_id" to roomID,
                "remont_id" to remontID
            )
        )

        val resObj = RequestBody.create(okhttp3.MultipartBody.FORM, jsonObj.toString())

        networkApi.uploadTaskCloseDataOut(resObj, login!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showDialog() }
            .doOnTerminate { viewState.dismissDialog() }
            .subscribe({ it ->
                if (it.isSuccessful) {
                    if (it.body()!!.result.status) {
                        //viewState.setResultData(it.body()!!.value)
                        viewState.showToast("Статус изменен")
                        //viewState.updateChildOnPosition(globalPosition, status, model)
                    } else {
                        viewState.showToast(it.body()!!.result.errMsg)
                    }
                }
            }, {
                it.printStackTrace()
                viewState.showToast("Ошибка при отправке данных")
            })
    }

    @SuppressLint("CheckResult")
    fun acceptAllStagesWithoutStatus() {
        if (stageStatusID in AppConstant.STATUS_STAGE_ACTIVE) {
            try {
                remontCheckListDao.getAcceptUnstageList(remontID, activeStageID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val list: ArrayList<RemontCheckListEntity> = arrayListOf()
                        if (!it.isNullOrEmpty()) {
                            for (item in it) {
                                list.add(
                                    RemontCheckListEntity(
                                        remontID = item.remontID,
                                        checkListID = item.checkListID,
                                        roomID = item.roomID,
                                        isAccepted = item.isAccepted,
                                        description = item.description,
                                        stageID = activeStageID,
                                        isForSend = 1,
                                        isAudioForSend = 0
                                    )
                                )
                            }

                            Completable.fromAction {
                                remontCheckListDao.insertAll(list)
                                remontCheckListDao.upadateAllNullStage()
                            }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    viewState.showToast("Все чеклесты подтверждены")
                                    viewState.updateListAfterFullAccept()
                                }

                        } else {
                            viewState.showToast("Чек-листов для подтверждения нет")
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            viewState.showToast("Этап неактивен. Принятие невозможно")
        }

    }

    fun checkNewCheckList(
        info: CheckListChildModelNew,
        globalPosition: Int,
        currentStage: Int,
        currentRoom: Int
    ) {
        viewState.navigateToCheckListNew(info, globalPosition)
        /*if (info.remontCheckListID == 0 || info.remontCheckListID == null) {
            val id = remontCheckListDao.insert(RemontCheckListEntity(
                    remontID = remontID,
                    checkListID = info.checkListID,
                    checkListPID = info.checkListPID,
                    checkName = info.checkName,
                    norm = info.norm,
                    isRoom = 1,
                    stageID = currentStage,
                    roomID = currentRoom,
                    isActive = 1,
                    audioInfo = null,
                    audioName = null,
                    isAudioForSend = 0,
                    defectCnt = null,
                    description = null,
                    isAccepted = null,
                    isForSend = 0
            ))

            viewState.navigateToCheckList(info, globalPosition, id.toInt())
        } else {
            viewState.navigateToCheckList(info, globalPosition, info.remontCheckListID!!)
        }*/
    }

    fun acceptSelectedCheckList(
        info: CheckListChildModel,
        globalPosition: Int,
        currentStage: Int,
        currentRoom: Int
    ) {

        if (remontStatusID in AppConstant.STATUS_REMONT_ACTIVE) {
            if (stageStatusID in AppConstant.STATUS_STAGE_ACTIVE) {
                remontCheckListDao.insert(
                    RemontCheckListEntity(
                        remontID = remontID,
                        checkListID = info.checkListID,
                        checkListPID = info.checkListPID,
                        checkName = info.checkName,
                        norm = info.norm,
                        isRoom = 1,
                        stageID = currentStage,
                        roomID = currentRoom,
                        isActive = 1,
                        audioInfo = null,
                        audioName = null,
                        isAudioForSend = 0,
                        defectCnt = null,
                        description = null,
                        isAccepted = AppConstant.checkAccept,
                        isForSend = 1
                    )
                )
                viewState.updateChildOnPosition(
                    globalPosition,
                    1,
                    info.defectCnt,
                    info.norm,
                    info.checkName
                )
            } else {
                viewState.showToast("Этап неактивен. Принятие невозможно")
            }
        } else {
            viewState.showToast("Ремонт неактивен")
        }
    }

    @SuppressLint("CheckResult")
    fun setPlanirovkaImage() {
        remontCheckListDao.getPlanirovkaImage(remontID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.showPlanirovka(it)
            }, {
                it.printStackTrace()
            })
    }

    fun navigatToChat(it: GroupChatEntity) {
        viewState.navigateToChat(it.groupChatID, remontID, it.groupChatName!!)
    }

    fun setIntentData(
        iremontID: Int,
        iactiveStageID: Int,
        istageStatusID: Int,
        iremontStatusID: Int
    ) {
        remontID = iremontID
        activeStageID = iactiveStageID
        stageStatusID = istageStatusID
        remontStatusID = iremontStatusID
    }

    fun checkStatusChange(
        remontStatusId: Int,
        stageStatusId: Int,
        currentStage: Int
    ) {
        if (remontStatusId in AppConstant.STATUS_REMONT_ACTIVE) {
            if (activeStageID == currentStage) {
                if (stageStatusId in arrayOf(1, 3)) {
                    if (checkDefectsExist()) {
                        viewState.changeStageStatusDialog()
                    } else {
                        viewState.showToast("Чтобы изменить статус этого этапа нужно иметь фото/видео фиксацию")
                    }
                } else {
                    viewState.showToast("Текущий этап должен быть в статусе 'Готов к сдаче этапа' или 'Готов к сдаче замечаний'")
                }
            } else {
                viewState.showToast("Можно менять статус только активного этапа")
            }
        } else {
            viewState.showToast("Можно менять статус только действующего ремонта")
        }
    }

    private fun checkDefectsExist(): Boolean {
        return defectMediaDao.isDefectsExistInStage(remontID, activeStageID)
    }
}
