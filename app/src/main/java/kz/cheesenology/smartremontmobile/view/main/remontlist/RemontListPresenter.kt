package kz.cheesenology.smartremontmobile.view.main.remontlist

import android.annotation.SuppressLint
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.BuildConfig
import kz.cheesenology.smartremontmobile.data.chat.StageChatDao
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.notification.NotificationDao
import kz.cheesenology.smartremontmobile.data.remont.RemontListDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomDao
import kz.cheesenology.smartremontmobile.data.stagestatushist.StageStatusHistoryDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.domain.FileSyncInteractor
import kz.cheesenology.smartremontmobile.domain.NetworkGetDataInteractor
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.DateFormatter
import kz.cheesenology.smartremontmobile.util.PrefUtils
import kz.cheesenology.smartremontmobile.work.SyncWork
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@InjectViewState
class RemontListPresenter @Inject constructor(
    val networkApi: NetworkApi,
    val roomDao: RoomDao,
    val remontListDao: RemontListDao,
    val remontCheckListDao: RemontCheckListDao,
    val userDefectMediaDao: UserDefectMediaDao,
    val stageStatusHistoryDao: StageStatusHistoryDao,
    val networkGetDataInteractor: NetworkGetDataInteractor,
    val fileSyncInteractor: FileSyncInteractor,
    val notificationDao: NotificationDao,
    val stageChatDao: StageChatDao
) : MvpPresenter<RemontListView>() {

    private var stageID: Int? = 0

    val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        //RUN NOTIFICATION PERIOD WORK FOR DATA SEND CHECK
        setNotification()

        showRemontList()
    }

    @SuppressLint("CheckResult")
    fun showRemontList() {
        val statusRemont = PrefUtils.prefs.getInt("filter_remont", 0)
        val stage = PrefUtils.prefs.getInt("filter_stage", 0)
        val statusOkk = PrefUtils.prefs.getInt("filter_okk", 0)
        val stageStatus = PrefUtils.prefs.getInt("filter_stage_status", 0)

        val listStatusRemont = mutableListOf<Int>()
        val listStage = mutableListOf<Int>()
        val listStatusOKK = mutableListOf<Int>()
        val listStageStatus = mutableListOf<Int>()

        //СТАТУС РЕМОНТА
        when (statusRemont) {
            //ALL
            0 -> {
                listStatusRemont.add(1)
                listStatusRemont.add(2)
                listStatusRemont.add(4)
            }
            else -> {
                listStatusRemont.add(statusRemont)
            }
        }

        //Статус ОКК
        when (statusOkk) {
            //ALL
            0 -> {
                listStatusOKK.add(0)
                listStatusOKK.add(1)
                listStatusOKK.add(2)
                listStatusOKK.add(3)
            }
            else -> {
                listStatusOKK.add(statusOkk)
            }
        }

        //ЭТАП
        when (stage) {
            0 -> {
                //listStage.add(1)
                //listStage.add(2)
                listStage.add(3)
                listStage.add(4)
                listStage.add(5)
                //listStage.add(6)
            }
            else -> {
                listStage.add(stage)
            }
        }

        //СТАТУС ЭТАПА
        when (stageStatus) {
            0 -> {
                listStageStatus.add(1)
                listStageStatus.add(2)
                listStageStatus.add(3)
                listStageStatus.add(4)
                listStageStatus.add(-1)
            }
            5 -> {
                listStageStatus.add(1)
                listStageStatus.add(3)
            }
            else -> {
                listStageStatus.add(stageStatus)
            }
        }






        remontListDao.getList(listStatusRemont, listStage, listStatusOKK, listStageStatus)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                viewState.setListData(it)
                //viewState.updateFilterSelectedItems(statusInProcess, statusNotAccepted, statusCanceled, statusFinished)
            }, {
                it.printStackTrace()
                viewState.showToast(it.message.toString())
            })
    }


    fun changeFilterPref(pref: CharSequence, it: Int) {
        when (pref) {
            AppConstant.okkStatusAcceptedText -> {
                PrefUtils.editor.putInt("remont_status_in_process", it).apply()
            }
            AppConstant.okkStatusNotAcceptedText -> {
                PrefUtils.editor.putInt("remont_status_not_accepted", it).apply()
            }
            AppConstant.okkStatusCanceledText -> {
                PrefUtils.editor.putInt("remont_status_canceled", it).apply()
            }
            AppConstant.okkRemontStatusFinished -> {
                PrefUtils.editor.putInt("remont_status_finished", it).apply()
            }
        }
    }

    @SuppressLint("CheckResult")
    fun checkBeforeGetFullDataList() {
        remontCheckListDao.getStatsInfo(remontListDao.getFullListID())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when {
                    it.remontListCnt > 0 || it.photoCnt > 0 -> {
                        viewState.showToast("Получение новых данных невозможно. Есть неотправленные данные")
                        checkBeforeNavigateSend()
                    }
                    else -> getFullDataList()
                }
            }
    }

    fun checkBeforeNavigateSend() {
        viewState.showStatsFragment(remontListDao.getFullListID())
    }

    @SuppressLint("CheckResult")
    fun getFullDataList() {
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
                                    showRemontList()
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


    @SuppressLint("CheckResult")
    fun getCatalogsFromServer() {
        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        networkApi.getCatalogs(login!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showDialog("Обновление справочников") }
            .doOnTerminate { viewState.dismissDialog() }
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.result.status) {
                        networkGetDataInteractor.setCatalogsInfo(it.body()!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                viewState.showToast("Справочники обновлены")
                            }
                        fileSyncInteractor.syncStandarts()

                    } else if (!it.body()!!.result.status) {
                        viewState.showToast(it.body()!!.result.errMsg)
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    fun acceptRemontStatus(remontId: Int?, isAccept: String) {

        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        networkApi.acceptRemontStatus(login!!, password!!, remontId, isAccept)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showDialog("Изменение статуса") }
            .doOnTerminate { viewState.dismissDialog() }
            .subscribe({
            }, {
                it.printStackTrace()
            })
    }

    fun setNotification() {
        //WorkManager.getInstance().cancelAllWork()

        /*val request = OneTimeWorkRequest.Builder(SyncWork::class.java)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .setInputData(Data.Builder().putString("db", login).build())
                .build()
        WorkManager.getInstance().enqueue(request)*/
        val login = PrefUtils.prefs.getString("login", null)
        val requestUniquePeriodic =
            PeriodicWorkRequest.Builder(SyncWork::class.java, 12, TimeUnit.HOURS)
                .setInputData(Data.Builder().putString("db", login).build())
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "PERIODIC",
            ExistingPeriodicWorkPolicy.KEEP,
            requestUniquePeriodic
        )
    }

    fun showFilterDialog() {
        val statusRemont = PrefUtils.prefs.getInt("filter_remont", 0)
        val stage = PrefUtils.prefs.getInt("filter_stage", 0)
        val statusOkk = PrefUtils.prefs.getInt("filter_okk", 0)
        val stageStatus = PrefUtils.prefs.getInt("filter_stage_status", 0)

        viewState.showStatusFilterDialog(statusRemont, stage, statusOkk, stageStatus)
    }

    fun changeFilterStatusPref(prefName: String, value: Int) {
        PrefUtils.editor.putInt(prefName, value).apply()
    }

    fun setIntentInfo(stringExtra: Int?) {
        stageID = stringExtra
    }

    @SuppressLint("CheckResult")
    fun apkUpdateCheck() {
        val id = BuildConfig.VERSION_NAME
        networkApi.checkApkUpdate()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.version != null) {
                        if (id.replace(".", "").toInt() < it.body()!!.version!!.toInt()) {
                            viewState.needsUpdate(true)
                        } else {
                            viewState.needsUpdate(false)
                        }
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    fun checkNotifications(check: Boolean) {
        var isNavigate = check
        compositeDisposable.add(notificationDao.isNewNotification()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (!it.isNullOrEmpty()) {
                    if (it[0] > 0) {
                        viewState.setNotificationCount(it[0])
                        if (isNavigate)
                            viewState.navigateToNotificationActivity()
                    } else {
                        viewState.setNotificationCount(0)
                        isNavigate = false
                    }
                }
            }, {
                it.printStackTrace()
            }, {
                Log.e("NOTIFICATION ONCOMPLETE", " test")
            })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}