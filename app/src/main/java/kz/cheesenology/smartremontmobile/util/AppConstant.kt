package kz.cheesenology.smartremontmobile.util

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import kz.cheesenology.smartremontmobile.BuildConfig
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AppConstant {

    //OKK STATUS
    /*
    не принят - 1
    отказан - 3
    принят - 2
    */
    val okkStatusAcceptedID = 2
    val okkStatusCanceledID = 3
    val okkStatusNotAcceptedID = 1

    val okkStatusAcceptedText = "Приняты"
    val okkStatusCanceledText = "Отказаны"
    val okkStatusNotAcceptedText = "Не приняты"
    val okkRemontStatusFinished = "Завершен"

    //STAGE ID

    //CHECK LIST STATUS
    val checkAccept = 1
    val checkDefect = 0
    val checkCancel = 2

    //Статус этапа
    val STATUS_STAGE_NEW = -1
    val STATUS_STAGE_ACTIVE: Array<Int> = arrayOf(1, 3)
    val STATUS_STAGE_DEFECT = 2
    val STATUS_STAGE_COMPLETE = 4

    //Статус ремонта
    /*
        1 - создан
        2 - закончен
        3 - отменен
        4 - гарантия
     */
    val STATUS_REMONT_ACTIVE: Array<Int> = arrayOf(1)
    val STATUS_REMONT_FINISHED = 2
    val STATUS_REMONT_CANCELED = 3
    val STATUS_REMONT_WARRANTY = 4

    val MEDIA_USER_STANDART_PATH = "/DCIM/SmartRemont/Standarts/"
    val MEDIA_USER_PHOTO_PATH = "/DCIM/SmartRemont/Photo/"
    val MEDIA_USER_AUDIO_PATH = "/DCIM/SmartRemont/Audio/"
    val MEDIA_USER_PLANIROVKA_PATH = "/DCIM/SmartRemont/Planirovka/"
    val FULL_MEDIA_PHOTO_PATH =
        Environment.getExternalStorageDirectory().absoluteFile.toString() + MEDIA_USER_PHOTO_PATH
    val FULL_MEDIA_STANDART_PATH =
        Environment.getExternalStorageDirectory().absoluteFile.toString() + MEDIA_USER_STANDART_PATH
    val FULL_MEDIA_AUDIO_PATH =
        Environment.getExternalStorageDirectory().absoluteFile.toString() + MEDIA_USER_AUDIO_PATH
    val FULL_MEDIA_PLANIROVKA_PATH =
        Environment.getExternalStorageDirectory().absoluteFile.toString() + MEDIA_USER_PLANIROVKA_PATH

    val PHOTO_STATUS_USER = "1"
    val PHOTO_STATUS_STANDART = "2"
    val PHOTO_STATUS_PLANIROVKA = "3"

    val PATH_PROFILE = "profile_photo"
    val PATH_DOC_FRONT = "doc_front_photo"
    val PATH_DOC_BACK = "doc_back_photo"

    //PROFILE PATH
    val PATH_PROFILE_PHOTO_FILE: File = File(
        SmartRemontApplication.instance!!.applicationContext.filesDir, PATH_PROFILE
    )


    fun isWorkWithCheckListAccepted(
        currentStageID: Int,
        activeStageID: Int,
        stageStatusID: Int
    ): Boolean {
        return if (activeStageID == currentStageID) {
            stageStatusID in STATUS_STAGE_ACTIVE
        } else {
            false
        }
    }

    fun getTimeMonthAgo(): Date {
        var cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        return cal.time
    }

    fun getServerName(): String {
        return BuildConfig.BASE_URL
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateFull(): String {
        return SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateFullWithoutSeconds(): String {
        return SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date())
    }

    fun getRundomNumber(): Int {
        return (1..1000000000).random()
    }

    fun getUriFilePath(uri: Uri?): String? {
        var path: String? = null
        var image_id: String? = null

        val resolver = SmartRemontApplication.instance?.applicationContext?.contentResolver
        val cursor = resolver?.query(uri!!, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            image_id = cursor.getString(0)
            image_id = image_id.substring(image_id.lastIndexOf(":") + 1)
            cursor.close()
        }
        val cursor2 = resolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(image_id),
            null
        )
        if (cursor2 != null) {
            cursor2.moveToFirst()
            path = cursor2.getString(cursor2.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor2.close()
        }
        return path
    }

    fun getProjectFileUrl(): String {
        return BuildConfig.BASE_URL + "rest/project-remont-get/remont_id/"
    }

    fun isBIG(): Boolean {
        return PrefUtils.prefs.getString("okk_id", "")?.toInt() == 2
    }

}