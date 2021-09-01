package kz.cheesenology.smartremontmobile.view.main.send

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryDao
import kz.cheesenology.smartremontmobile.data.remont.RemontListDao
import kz.cheesenology.smartremontmobile.data.remontroom.RemontRoomDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomDao
import kz.cheesenology.smartremontmobile.data.stage.StageDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.domain.FileSyncInteractor
import kz.cheesenology.smartremontmobile.domain.NetworkGetDataInteractor
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.DateFormatter
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import javax.inject.Inject

@InjectViewState
class SendStatsPresenter @Inject constructor(
    val networkApi: NetworkApi,
    val remontCheckListDao: RemontCheckListDao,
    val roomDao: RoomDao,
    val checkListHistoryDao: CheckListHistoryDao,
    val remontListDao: RemontListDao,
    val userDefectMediaDao: UserDefectMediaDao,
    val remontRoomDao: RemontRoomDao,
    val networkGetDataInteractor: NetworkGetDataInteractor,
    val fileSyncInteractor: FileSyncInteractor
) : MvpPresenter<SendStatsView>() {

    val compositeDisposable = CompositeDisposable()

    lateinit var remontListID: List<Int>

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        compositeDisposable.add(remontCheckListDao.getStatsInfo(remontListID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when {
                    it.remontListCnt > 0 || it.photoCnt > 0 -> viewState.setStats(it)
                    else -> viewState.setEmptySendText()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    fun deleteAllData() {
        checkListHistoryDao.delete()
        remontCheckListDao.delete()
        remontListDao.delete()
        userDefectMediaDao.delete()
        remontRoomDao.delete()
    }

    @SuppressLint("CheckResult")
    fun sendAllOfflineWorkData() {

        val sendJSON = JSONObject()
        val audioList = ArrayList<MultipartBody.Part>()
        val photoList = ArrayList<MultipartBody.Part>()
        val videoList = ArrayList<MultipartBody.Part>()

        remontListDao.getRemontListDataForSend(remontListID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //REMONT STATUS
                val arr = JSONArray()
                for (item in it) {
                    val obj = JSONObject(
                        mapOf(
                            "remont_id" to item.remontID,
                            "stage_status_id" to item.stageStatusID,
                            "stage_status_id" to item.stageStatusID,
                            "remark_code" to item.stageStatusDesc,
                            "is_okk_status_change" to item.isOKKStatusChange,
                            "okk_status" to item.okkStatus,
                            "comment" to item.stageStatusComment,
                            "stage_id" to item.activeStageID
                        )
                    )
                    arr.put(obj)
                }
                sendJSON.put("remont_list", arr)
                Log.e("json: ", sendJSON.toString())

                userDefectMediaDao.getDefectsForSend(remontListID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val defectJsonArray = JSONArray()
                        it.forEach {
                            val file = File(AppConstant.FULL_MEDIA_PHOTO_PATH + it.fileName)
                            if (file.exists()) {
                                //PUT JSON DATA
                                val obj = JSONObject(
                                    mapOf(
                                        "remont_id" to it.remontID,
                                        "check_list_id" to it.checkListID,
                                        "file_name" to it.fileName,
                                        "file_type" to it.fileType,
                                        "stage_id" to it.stage_id,
                                        "audio_name" to it.audioName,
                                        "defect_status" to it.defectStatus,
                                        "comment" to it.comment,
                                        "date_create" to it.dateCreate
                                    )
                                )
                                defectJsonArray.put(obj)
                                //PUT File by type
                                when (it.fileType) {
                                    "photo" -> {
                                        photoList.add(
                                            MultipartBody.Part.createFormData(
                                                "image[]",
                                                file.name,
                                                RequestBody.create(
                                                    "image*//*".toMediaTypeOrNull(),
                                                    file
                                                )
                                            )
                                        )
                                    }
                                    "video" -> {
                                        videoList.add(
                                            MultipartBody.Part.createFormData(
                                                "video[]",
                                                file.name,
                                                RequestBody.create(
                                                    "video*//*".toMediaTypeOrNull(),
                                                    file
                                                )
                                            )
                                        )
                                    }
                                }
                                //PUT AUDIO IF EXIST
                                val audioFile =
                                    File(AppConstant.FULL_MEDIA_AUDIO_PATH + it.audioName)
                                if (audioFile.exists()) {
                                    audioList.add(
                                        MultipartBody.Part.createFormData(
                                            "audio[]",
                                            audioFile.name,
                                            RequestBody.create(
                                                "audio*//*".toMediaTypeOrNull(),
                                                audioFile
                                            )
                                        )
                                    )
                                }
                            }
                        }
                        sendJSON.put("defect_list", defectJsonArray)
                        sendData(sendJSON, photoList, audioList, videoList)
                    }, {
                        it.printStackTrace()
                    })

            }, {
                it.printStackTrace()
            })
    }


    @SuppressLint("CheckResult")
    fun sendData(
        sendJSON: JSONObject,
        photoList: ArrayList<MultipartBody.Part>,
        audioList: ArrayList<MultipartBody.Part>,
        videoList: ArrayList<MultipartBody.Part>
    ) {
        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")
        val body = sendJSON.toString().toRequestBody(okhttp3.MultipartBody.FORM)

        networkApi.sendCheckListProcessing(login, password, body, photoList, audioList, videoList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showDialog("Отправка данных") }
            .doOnTerminate { viewState.dismissDialog() }
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.result.status) {
                        if (!it.body()!!.value.errList.isNullOrEmpty()) {
                            for (item in it.body()!!.value.errList!!) {
                                remontListDao.setSuccessSendStatus(remontListID)
                                remontListDao.updateOnError(item.remontID, item.errorText)
                                clearSendStatus()
                            }
                        } else {
                            clearSendStatus()
                            remontListDao.setSuccessSendStatus(remontListID)
                            viewState.showToast("Все ремноты успешно отправлены")
                        }
                        viewState.closeDialog()

                        //Запросить заново получение данных по ремонтам
                        //getFullDataFromServer()
                    } else {
                        viewState.showToast(it.body()!!.result.errMsg)
                    }
                    Log.e("SUCCESS SEND", "-----")
                }
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    private fun getFullDataFromServer() {
        if (roomDao.getCnt().roomCnt == 0) {
            viewState.showToast("Получение ремонтов невозможно. Обновите справочники")
        } else {

            val login = PrefUtils.prefs.getString("login", "")
            val password = PrefUtils.prefs.getString("password", "")

            val dashUserStartDate = DateFormatter.strToDashDate(
                PrefUtils.prefs.getString(
                    "user_start_date",
                    "01.05.2018"
                )!!
            )
            val dashUserFinishDate = DateFormatter.strToDashDate(
                PrefUtils.prefs.getString(
                    "user_finish_date",
                    DateFormatter.pointWithYear(Date())
                )!!
            )

            networkApi.getFullDataList(login!!, password!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showDialog("Получение полного списка данных") }
                .doOnTerminate { viewState.dismissDialog() }
                .subscribe({

                    if (it.isSuccessful) {
                        if (it.body()!!.result.status) {
                            networkGetDataInteractor.setRemontDataToDB(it.body()!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    viewState.closeDialog()
                                }
                            fileSyncInteractor.syncFiles()
                        } else {
                            //ОШИБКА ПРИ ПОЛУЧЕНИИ ДАННЫХ
                            viewState.showToast(it.body()!!.result.errMsg)
                        }
                    }
                }, {
                    it.printStackTrace()
                    viewState.showToast(it.message.toString())
                })
        }

    }

    private fun clearSendStatus() {
        remontListDao.updateSendedData(remontListID)
        remontCheckListDao.updateSendedData()
        userDefectMediaDao.clearSendStatus(remontListID)
    }

    fun setRemontIDList(remontList: List<Int>) {
        remontListID = remontList
    }
}