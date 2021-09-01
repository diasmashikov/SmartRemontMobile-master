package kz.cheesenology.smartremontmobile.domain

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloadQueueSet
import com.liulishuo.filedownloader.FileDownloader
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.util.AppConstant
import java.io.File
import javax.inject.Inject

class FileSyncInteractor @Inject constructor(
        val userDefectMediaDao: UserDefectMediaDao,
        val standartDao: StandartPhotoDao
) {
    val listener = object : FileDownloadListener() {
        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

        override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {}

        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

        override fun blockComplete(task: BaseDownloadTask?) {
            Log.e("BLOCK COMPLETE: ", "ttt")
        }

        override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {}

        override fun completed(task: BaseDownloadTask) {
            Log.e("COMPLETE: ", task.filename.toString())
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

        override fun error(task: BaseDownloadTask, e: Throwable) {
            e.printStackTrace()
        }

        override fun warn(task: BaseDownloadTask) {}
    }
    val queueSet = FileDownloadQueueSet(listener)

    @SuppressLint("CheckResult")
    fun syncFiles() {
        userDefectMediaDao.getDownloadList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe {
                    var mob = Environment.getExternalStorageDirectory().absoluteFile
                    mob = File(mob, AppConstant.MEDIA_USER_PHOTO_PATH)
                    if (!mob.exists()) {
                        mob.mkdirs()
                    }

                    val photoList = ArrayList<BaseDownloadTask>()
                    val path = Environment.getExternalStorageDirectory().absoluteFile.toString() + AppConstant.MEDIA_USER_PHOTO_PATH
                    for (item in it) {
                        val image = File(path, item.fileName)
                        if (!image.exists()) {
                            photoList.add(
                                    FileDownloader.getImpl().create(AppConstant.getServerName() + item.fileUrl)
                                            .setPath(path + item.fileName))
                        }
                    }
                    queueSet.disableCallbackProgressTimes()
                    queueSet.setAutoRetryTimes(1)
                    queueSet.downloadTogether(photoList)
                    queueSet.start()
                }
    }

    @SuppressLint("CheckResult")
    fun syncStandarts() {
        standartDao.getList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe {
                    var mob = Environment.getExternalStorageDirectory().absoluteFile
                    mob = File(mob, AppConstant.MEDIA_USER_STANDART_PATH)
                    if (!mob.exists()) {
                        mob.mkdirs()
                    }

                    val standartList = ArrayList<BaseDownloadTask>()
                    val path = Environment.getExternalStorageDirectory().absoluteFile.toString() + AppConstant.MEDIA_USER_STANDART_PATH
                    for (item in it) {
                        val image = File(path, item.photoName)
                        if (!image.exists()) {
                            standartList.add(
                                    FileDownloader.getImpl().create(AppConstant.getServerName() + item.photoURL)
                                            .setPath(path + item.photoName))
                            /*PRDownloader.download(
                                    AppConstant.getServerName() + item.fileUrl,
                                    path,
                                    item.fileName)
                                    .build()
                                    .setOnStartOrResumeListener { }
                                    .setOnPauseListener { }
                                    .setOnCancelListener { }
                                    .setOnProgressListener { }
                                    .start(object : OnDownloadListener {
                                        override fun onDownloadComplete() {
                                            Log.e("DOWNLOAD COMPLETE", "")

                                        }

                                        override fun onError(error: Error) {
                                        }
                                    })*/
                        }
                    }
                    queueSet.disableCallbackProgressTimes()
                    queueSet.setAutoRetryTimes(1)
                    queueSet.downloadTogether(standartList)
                    queueSet.start()
                }
    }

}