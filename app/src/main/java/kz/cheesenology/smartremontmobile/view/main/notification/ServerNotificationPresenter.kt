package kz.cheesenology.smartremontmobile.view.main.notification

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.notification.NotificationDao
import kz.cheesenology.smartremontmobile.data.notification.NotificationEntity
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class ServerNotificationPresenter @Inject constructor(
        val networkApi: NetworkApi,
        val notificationDao: NotificationDao
        ) : MvpPresenter<ServerNotificationView>() {

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        updateReadStatus()
        notificationDao.getList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    viewState.setNotificationList(it)
                }

        getNotificationList()
    }

    @SuppressLint("CheckResult")
    fun getNotificationList() {
        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        networkApi.getNotificationList(login!!, password!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isSuccessful) {
                        if (it.body()!!.result.status) {
                            if (!it.body()!!.value.data.isNullOrEmpty()) {
                                val list = it.body()!!.value.data

                                val dbList: ArrayList<NotificationEntity> = ArrayList()
                                list?.forEach {
                                    dbList.add(NotificationEntity(
                                            notificationID = it.sendEmailUserID!!,
                                            title = it.title!!,
                                            text = it.detail,
                                            dateCreate = it.pushDateTime,
                                            isRead = true
                                    ))
                                }

                                notificationDao.insertAll(dbList)
                            } else
                                viewState.showToast("Новых уведомлений нет")
                        }
                    }
                }
    }

    fun updateReadStatus() {
        notificationDao.updateReadStatus()
    }
}