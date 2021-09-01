package kz.cheesenology.smartremontmobile.work

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import kz.cheesenology.smartremontmobile.data.AppDatabase
import kz.cheesenology.smartremontmobile.notifications.NotificationService

class SyncWork(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    /*@Inject
    lateinit var dao: RemontCheckListDao

    init {
        (applicationContext as? SmartRemontApplication)!!.databaseComponent.inject(this)
    }*/

    @SuppressLint("CheckResult")
    override fun doWork(): Result {

        val dao = Room.databaseBuilder(applicationContext, AppDatabase::class.java,
                inputData.getString("db")!!)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build().remontCheckListDao()

        try {
            val result = dao.getStatsInfoModel()
            return when {
                result.remontListCnt > 0 || result.photoCnt > 0 -> {
                    val service = Intent(applicationContext, NotificationService::class.java)
                    service.putExtra("remont_list_cnt", result.remontListCnt)
                    service.putExtra("photo_cnt", result.photoCnt)
                    service.putExtra("login", inputData.getString("db")!!)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(service)
                    } else {
                        applicationContext.startService(service)
                    }
                    Result.success()
                }
                else -> {
                    Result.success()
                }
            }

            /*dao.getStatsInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        when {
                            it.checkListCnt > 0 || it.remontListCnt > 0 || it.photoCnt > 0 || it.audioCnt > 0 -> {
                              var builder = NotificationCompat.Builder(applicationContext, "")
                                        .setSmallIcon(R.drawable.ic_contact_phone_black_24dp)
                                        .setContentTitle("My notification")
                                        .setContentText("Much longer text that cannot fit one line...")
                                        .setStyle(NotificationCompat.BigTextStyle()
                                                .bigText("Much longer text that cannot fit one line..."))
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                kz.cheesenology.smartremontmobile.model.request.Result.success()
                            }
                            else -> {
                                kz.cheesenology.smartremontmobile.model.request.Result.success()
                            }
                        }
                    }, {
                        kz.cheesenology.smartremontmobile.model.request.Result.failure()
                    })*/

        } catch (th: Throwable) {
            th.printStackTrace()
            return Result.failure()
        }
    }
}