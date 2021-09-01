package kz.cheesenology.smartremontmobile.domain.work

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.request.RequestDao
import kz.cheesenology.smartremontmobile.domain.NetworkSendDataInteractor
import javax.inject.Inject

class SyncNetworkWork(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    @Inject
    lateinit var requestDao: RequestDao

    @Inject
    lateinit var workSyncInteractorWithServer: NetworkSendDataInteractor

    init {
        (applicationContext as? SmartRemontApplication)!!.databaseComponent.inject(this)
    }

    @SuppressLint("CheckResult", "SimpleDateFormat")
    override fun doWork(): Result {
        Log.e("WORK MANAGER: ", " START")
        workSyncInteractorWithServer.fullSync()
        return Result.success()
    }
}