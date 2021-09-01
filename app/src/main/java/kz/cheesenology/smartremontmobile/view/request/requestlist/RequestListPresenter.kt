package kz.cheesenology.smartremontmobile.view.request.requestlist

import android.annotation.SuppressLint
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.AppDatabase
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkaccept.RequestCheckAcceptDao
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoDao
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryDao
import kz.cheesenology.smartremontmobile.domain.NetworkSetRequestData
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.MvpPresenter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.acra.ACRA
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.ArrayList
import javax.inject.Inject

class RequestListPresenter @Inject constructor(
    val networkApi: NetworkApi,
    val networkSetRequestData: NetworkSetRequestData,
    val requestListDao: RequestListDao,
    val requestCheckListHistoryDao: RequestCheckListHistoryDao,
    val checkListDao: RequestCheckAcceptDao,
    val checkListPhotoDao: CheckRequestPhotoDao,
    val appDatabase: AppDatabase
) :
    MvpPresenter<RequestListView>() {


    private var login: String? = null
    private var password: String? = null
    private var mobileID: String? = null

    var requestListForSend = mutableListOf<Int>()

    init {
        login = PrefUtils.prefs.getString("auth_login", null)
        password = PrefUtils.prefs.getString("auth_password", null)
        mobileID = FirebaseInstanceId.getInstance().id
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        showRequestList()
    }

    @SuppressLint("CheckResult")
    public fun getRequestDataFromServer() {
        val count = requestCheckListHistoryDao.getSendCnt()
        if (count.check_list_cnt > 0 || count.draft_check_cnt > 0 || count.check_list_photo_cnt > 0) {
            viewState.showToast("Есть неотправленные данные. Загрузка невозможна")
        } else {
            networkApi.getRequestList(login, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isSuccessful) {
                        networkSetRequestData.setRequestDataToDB(it.body()?.value!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe { viewState.showPB() }
                            .doOnTerminate { viewState.dismissPB() }
                            .subscribe {
                                showRequestList()
                            }
                    }
                }, {
                    it.printStackTrace()
                    ACRA.getErrorReporter().handleException(it)
                })
        }
    }

    @SuppressLint("CheckResult")
    private fun showRequestList() {

        val filterStatus = PrefUtils.prefs.getInt("filter_request_status", 1)

        val listStatusRequest = mutableListOf<Int>()
        when (filterStatus) {
            //Отображениен всех статусов
            1 -> {
                listStatusRequest.add(0)
                listStatusRequest.add(2)
            }
            else -> {
                listStatusRequest.add(filterStatus)
            }
        }

        requestListDao.getActiveRequestList(listStatusRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setRequestList(it)
            }, {
                it.printStackTrace()
            })
    }

    fun checkSendData() {
        val count = requestCheckListHistoryDao.getSendCnt()
        if (count.check_list_cnt > 0 || count.draft_check_cnt > 0 || count.check_list_photo_cnt > 0) {
            viewState.showSendDialog(count)
        } else {
            viewState.showToast("Нет данных для отправки")
        }
    }

    @SuppressLint("CheckResult")
    fun sendDataToServer() {
        requestCheckListHistoryDao.getListForSend()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //Добавляем список затронутых заявок в этот список чтобы после успешной отправки скрыть отправленные заявки
                requestListForSend = it.map { it.client_request_id } as MutableList<Int>


                val photoList = ArrayList<MultipartBody.Part>()
                val defectPhoto = ArrayList<MultipartBody.Part>()
                val jsonArr = JSONArray()
                it.forEach {
                    val jsonObj = JSONObject(
                        mapOf(
                            "client_request_id" to it.client_request_id,
                            "is_draft_accept" to it.draft_status,
                            "defect_act_file_name" to it.draft_defect_file_name,
                            "okk_comment" to it.okk_comment,
                            "okk_check_date" to it.okk_check_date
                        )
                    )
                    jsonArr.put(jsonObj)

                    val file = File(AppConstant.FULL_MEDIA_PHOTO_PATH + it.draft_defect_file_name)
                    if (file.exists()) {
                        photoList.add(
                            MultipartBody.Part.createFormData(
                                "defect_act[]",
                                file.name,
                                file
                                    .asRequestBody("image*//*".toMediaTypeOrNull())
                            )
                        )
                    }
                }
                val obj = JSONObject()
                obj.put("draft_list", jsonArr)

                //Добавление данных по чек-листам
                val emptyArray = JSONArray()
                val checkList = checkListDao.getDataForSend()
                checkList?.forEach {
                    val jsonObj = JSONObject(
                        mapOf(
                            "client_request_id" to it.client_request_id,
                            "draft_check_list_id" to it.draft_check_list_id,
                            "defect_status" to it.is_accepted,
                            //"date_create" to it.date_create
                        )
                    )

                    emptyArray.put(jsonObj)
                }


                val defectPhotoArray = JSONArray()
                val defectPhotoList = checkListPhotoDao.getDataForSend()
                defectPhotoList.forEach {
                    val file = File(AppConstant.FULL_MEDIA_PHOTO_PATH + it.requestCheckPhotoName)
                    if (file.exists()) {
                        defectPhoto.add(
                            MultipartBody.Part.createFormData(
                                "image[]",
                                file.name,
                                file
                                    .asRequestBody("image*//*".toMediaTypeOrNull())
                            )
                        )

                        val jsonObject = JSONObject(
                            mapOf(
                                "client_request_id" to it.clientRequestID,
                                "draft_check_list_id" to it.requestCheckListID,
                                "file_name" to it.requestCheckPhotoName,
                                "file_type" to it.requestCheckPhotoType,
                                "comment" to it.comment,
                                "date_create" to it.date_create
                            )
                        )
                        defectPhotoArray.put(jsonObject)
                    }
                }

                obj.put("defect_list", emptyArray)
                obj.put("defect_file", defectPhotoArray)

                val requestBody =
                    obj.toString().toRequestBody(MultipartBody.FORM)

                networkApi.sendRequestListToServer(
                    login,
                    password,
                    mobileID,
                    AppConstant.getRundomNumber(),
                    requestBody,
                    photoList,
                    defectPhoto
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { viewState.showPB() }
                    .doOnTerminate { viewState.dismissPB() }
                    .subscribe({
                        if (it.isSuccessful) {
                            if (it.body()!!.status) {
                                if (it.body()!!.value.status_code != "ERROR") {
                                    appDatabase.runInTransaction {
                                        requestListDao.hideSendRequests(requestListForSend)
                                        requestCheckListHistoryDao.updateSend()
                                        checkListDao.updateSend()
                                        checkListPhotoDao.updateSend()
                                    }
                                    viewState.closeSendDialog()
                                    viewState.showToast("Данные по заявкам успешно отправлены")
                                } else {
                                    viewState.showToast(it.body()!!.value.error_msg)
                                }
                            }
                        } else {
                            viewState.showToast("Произошла ошибка при отправке данных, обратитесь к администратору")
                            //viewState.showToast(it.body()!!.result.errMsg)
                        }
                    }, {
                        it.printStackTrace()
                        viewState.showToast(it.message.toString())
                    })

            }
    }

    fun showFilter() {
        val filterStatus = PrefUtils.prefs.getInt("filter_request_status", 1)
        viewState.showRequestFilterList(filterStatus)
    }

    fun changeFilterRequestStatusPref(value: Int) {
        PrefUtils.editor.putInt("filter_request_status", value).apply()

        showRequestList()
    }
}