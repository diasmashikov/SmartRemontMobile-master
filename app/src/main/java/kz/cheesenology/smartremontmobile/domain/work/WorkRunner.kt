package kz.cheesenology.smartremontmobile.domain.work

import androidx.work.*
import kz.cheesenology.smartremontmobile.SmartRemontApplication

class WorkRunner {
    companion object {
        fun startSyncWorker() {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            //val progressData = workDataOf(Telephony.Mms.Part.CONTENT_ID to contentId)

            val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncNetworkWork>()
                .setConstraints(constraints)
                //.setInputData(progressData)
                .build()

            WorkManager.getInstance(SmartRemontApplication.instance!!.applicationContext)
                .enqueueUniqueWork("sync", ExistingWorkPolicy.REPLACE, request)
        }
    }
}