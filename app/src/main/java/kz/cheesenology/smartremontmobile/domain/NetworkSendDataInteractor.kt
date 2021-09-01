package kz.cheesenology.smartremontmobile.domain

import android.annotation.SuppressLint
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.chat.StageChatDao
import kz.cheesenology.smartremontmobile.data.request.RequestDao
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.PrefUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.acra.ACRA
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import javax.inject.Inject

class NetworkSendDataInteractor @Inject constructor(
    val networkApi: NetworkApi,
    val requestDao: RequestDao,
    val stageChatDao: StageChatDao
) {

    private var login: String? = null
    private var password: String? = null
    private var mobileID: String? = null

    init {
        login = PrefUtils.prefs.getString("auth_login", null)
        password = PrefUtils.prefs.getString("auth_password", null)
        mobileID = FirebaseInstanceId.getInstance().id
    }

    @SuppressLint("CheckResult")
    fun fullSync() {
        requestDao.getSendData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.forEach { item ->
                    when (item.requestTypeID) {
                        RequestConstant.REQUEST_SEND_RATINGS -> {
                            sendRequestRatings(
                                item.requestID,
                                item.randomNum,
                                item.remontID,
                                item.data
                            )
                        }
                        RequestConstant.REQUEST_SEND_MESSAGE -> {
                            sendMessage(
                                item.requestID,
                                item.randomNum,
                                item.remontID,
                                item.data
                            )
                        }
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    private fun sendMessage(requestID: Int?, randomNum: Int?, remontID: Int?, data: String?) {
        stageChatDao.getMessage(requestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->

                val jsonObj = JSONObject(
                    mapOf(
                        "remont_id" to remontID,
                        "group_chat_id" to it.groupChatID,
                        "message" to it.message
                    )
                )

                val requestBody =
                    jsonObj.toString().toRequestBody(MultipartBody.FORM)

                networkApi.sendMessage(
                    login,
                    password,
                    mobileID,
                    randomNum,
                    requestBody
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.isSuccessful) {
                            if (it.body()!!.status) {
                                if (it.body()!!.value.status_code != "ERROR") {
                                    requestDao.updateRequestStatusByID(
                                        requestID,
                                        RequestConstant.STATUS_REQUEST_SUCCESS,
                                        it.body()!!.value.error_msg
                                    )
                                } else {
                                    //ошибка
                                    requestDao.updateRequestStatusByID(
                                        requestID,
                                        RequestConstant.STATUS_REQUEST_ERROR,
                                        it.body()!!.value.error_msg
                                    )
                                }
                            }
                        }
                    }, {
                        it.printStackTrace()
                        ACRA.getErrorReporter().handleException(it)
                    })
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    private fun sendRequestRatings(
        requestID: Int?,
        randomNum: Int?,
        remontID: Int?,
        data: String?
    ) {

        val requestBody = RequestBody.create(MultipartBody.FORM, data.toString())
        networkApi.sendRatings(
            login,
            password,
            mobileID,
            randomNum,
            requestBody
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        if (it.body()!!.value.status_code != "ERROR") {
                            requestDao.updateRequestStatusByID(
                                requestID,
                                RequestConstant.STATUS_REQUEST_SUCCESS,
                                it.body()!!.value.error_msg
                            )
                        } else {
                            //ошибка
                            requestDao.updateRequestStatusByID(
                                requestID,
                                RequestConstant.STATUS_REQUEST_ERROR,
                                it.body()!!.value.error_msg
                            )
                        }
                    }
                }
            }, {
                it.printStackTrace()
                ACRA.getErrorReporter().handleException(it)
            })
    }

}