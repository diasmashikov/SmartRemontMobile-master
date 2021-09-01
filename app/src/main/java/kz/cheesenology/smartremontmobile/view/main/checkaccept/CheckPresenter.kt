package kz.cheesenology.smartremontmobile.view.main.checkaccept

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Environment
import android.provider.MediaStore
import android.view.WindowManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryDao
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaEntity
import kz.cheesenology.smartremontmobile.model.CameraListModel
import kz.cheesenology.smartremontmobile.model.check.UploadResult
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.DateFormatter
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject


@InjectViewState
class CheckPresenter @Inject constructor(
    val network: NetworkApi,
    val userDefectMediaDao: UserDefectMediaDao,
    val historyDao: CheckListHistoryDao,
    val standartDao: StandartPhotoDao,
    val remontCheckListDao: RemontCheckListDao
) : MvpPresenter<CheckView>() {

    var checkName: String? = null
    var norm: String? = null
    var IS_NEW_STATUS = true

    var remontID: Int = 0
    var checkListID: Int = 0
    var sRemontCheckListID: Int = 0
    var STAGE_STATUS_ID: Int = 0
    var ACTIVE_STAGE_ID: Int = 0
    var CURRENT_STAGE_ID: Int = 0

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        //DETAILED INFO (STATUS, AUDIO, NORM, DEFECT CNT)
        getCheckListInfo()
    }

    fun setIntentData(
        iremontID: Int,
        icheckListID: Int,
        iRemontCheckListID: Int,
        iSTAGE_STATUS_ID: Int,
        iACTIVE_STAGE_ID: Int,
        iCURRENT_STAGE_ID: Int
    ) {
        remontID = iremontID
        checkListID = icheckListID
        sRemontCheckListID = iRemontCheckListID
        STAGE_STATUS_ID = iSTAGE_STATUS_ID
        ACTIVE_STAGE_ID = iACTIVE_STAGE_ID
        CURRENT_STAGE_ID = iCURRENT_STAGE_ID
    }

    @SuppressLint("CheckResult")
    private fun getCheckListInfo() {
        //USER PHOTO LIST
        getUserPhotoList()
        //GOOD STANDART
        setGoodStandart()
        //WEAK STANDART
        setWeakStandart()
        //HISTORY
        setHistory()

        remontCheckListDao.getInfoByID(sRemontCheckListID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkName = it.checkName
                norm = it.norm
                IS_NEW_STATUS = false

                if (it.audioInfo != null)
                    checkAudio(it.audioInfo, it.audioName)

                viewState.setCheckInfo(it)
            }, {
                it.printStackTrace()
            }, {
                IS_NEW_STATUS = true
            })
    }

    @SuppressLint("CheckResult")
    private fun setHistory() {
        historyDao.getListByID(remontID, checkListID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.setHistory(it)
            }
    }

    private fun checkAudio(audioInfo: String?, audioName: String?) {

        val mob = File(AppConstant.FULL_MEDIA_AUDIO_PATH, audioName)
        if (!mob.exists()) {
            mob.mkdirs()
        }

        if (!mob.exists()) {
            //Здесь был старый загрузчик аудио
        } else {
            //set already loaded audio
            viewState.setUserRecordedAudio(audioName)
        }
    }

    @SuppressLint("CheckResult")
    private fun setGoodStandart() {
        standartDao.getGoodList(checkListID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setGoodStandart(it)
            })
    }

    @SuppressLint("CheckResult")
    private fun setWeakStandart() {
        standartDao.getWeakList(checkListID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setWeakStandart(it)
            })
    }

    @SuppressLint("CheckResult")
    private fun getUserPhotoList() {
        userDefectMediaDao.getShowListByID(remontID, checkListID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.setPhotos(it)
            }
    }

    fun updateCheckStatus(checkStatus: Int?, defectTxt: String, defectCnt: Int) {
        when (checkStatus) {
            AppConstant.checkAccept -> {
                remontCheckListDao.updateRemontStatus(sRemontCheckListID, checkStatus)
            }
            AppConstant.checkCancel -> {
                remontCheckListDao.updateRemontStatus(sRemontCheckListID, checkStatus)
            }
            AppConstant.checkDefect -> {
                remontCheckListDao.updateRemontStatusWithDefect(
                    sRemontCheckListID,
                    checkStatus,
                    defectCnt,
                    defectTxt
                )
            }
        }
        viewState.setResultData(
            UploadResult(
                checkStatus,
                checkName,
                defectCnt,
                norm
            )
        )
    }

    @SuppressLint("CheckResult")
    fun sendCheckListStatusWithData(
        checkStatus: Int?,
        comment: String,
        defectNumber: String,
        imageList: List<CameraListModel>?,
        mAudioFile: File?
    ) {
        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        val imagesFileList = ArrayList<MultipartBody.Part>()
        val audioFileList = ArrayList<MultipartBody.Part>()
        for (item in imageList!!) {
            if (item.isUser) {
                val file = File(item.imgPath)
                if (file.exists())
                    imagesFileList.add(
                        MultipartBody.Part.createFormData(
                            "image[]",
                            file.name,
                            RequestBody.create("image*//*".toMediaTypeOrNull(), file)
                        )
                    )
            }
        }

        if (mAudioFile != null) {
            if (mAudioFile.exists())
                audioFileList.add(
                    MultipartBody.Part.createFormData(
                        "audio[]",
                        mAudioFile.name,
                        RequestBody.create("audio*//*".toMediaTypeOrNull(), mAudioFile)
                    )
                )
        } else {

        }

        val jsonObj = JSONObject(
            mapOf(
                "check_status" to checkStatus!!,
                "defect_number" to defectNumber,
                "comment" to comment,
                "check_list_id" to checkListID,
                "remont_id" to remontID
            )
        )

        val resObj = RequestBody.create(okhttp3.MultipartBody.FORM, jsonObj.toString())

        network.uploadTaskCloseData(imagesFileList, audioFileList, resObj, login!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showDialog("Отправка данных") }
            .doOnTerminate { viewState.dismissDialog() }
            .subscribe({ it ->
                if (it.isSuccessful) {
                    if (it.body()!!.result.status) {
                        viewState.setResultData(it.body()!!.value)
                        viewState.showToast("Данные успешно отправлены")
                    } else {
                        viewState.showToast(it.body()!!.result.errMsg)
                    }
                }
            }, {
                it.printStackTrace()
                viewState.showToast("Ошибка при отправке данных")
            })
    }

    @SuppressLint("CheckResult")
    fun deleteAudioFromServer() {
        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        network.deleteAudioFromServer(login!!, password!!, checkListID, remontID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showDialog("Удаление аудиозаписи") }
            .doOnTerminate { viewState.dismissDialog() }
            .subscribe({ it ->
                if (it.isSuccessful) {
                    if (it.body()!!.result.status) {
                        viewState.showToast("Аудиозапись удалена")
                        viewState.hideAudioPlayer()
                    } else {
                        viewState.showToast(it.body()!!.result.errMsg)
                    }
                }
            }, {
                it.printStackTrace()
                viewState.showToast("Ошибка при отправке данных")
            })
    }

    @SuppressLint("CheckResult")
    fun deletePhotoFromServer(remontCheckListPhotoID: Int?) {

        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        network.deletePhotoFromServer(login!!, password!!, remontCheckListPhotoID!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showDialog("Удаление фотографии") }
            .doOnTerminate { viewState.dismissDialog() }
            .subscribe({ it ->
                if (it.isSuccessful) {
                    if (it.body()!!.result.status) {
                        viewState.showToast("Фотография удалена")
                    } else {
                        viewState.showToast(it.body()!!.result.errMsg)
                    }
                }
            }, {
                it.printStackTrace()
                viewState.showToast("Ошибка при отправке данных")
            })
    }

    fun captureImage(packageManager: PackageManager, storageDir: File?) {
        if (AppConstant.isWorkWithCheckListAccepted(
                CURRENT_STAGE_ID,
                ACTIVE_STAGE_ID,
                STAGE_STATUS_ID
            ) || AppConstant.isBIG()
        ) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile(storageDir)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    viewState.startCameraIntent(
                        takePictureIntent,
                        photoFile,
                        photoFile.absolutePath
                    )
                }
            }
        } else {
            viewState.showToast("Этап неактивен. Добавление невозможно")
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(storageDir: File?): File {
        var mob = Environment.getExternalStorageDirectory().absoluteFile
        mob = File(mob, "/DCIM/SmartRemont/Photo/")
        if (!mob.exists()) {
            mob.mkdirs()
        }
        val uniqName = "SMR_" + Date().time + ".jpg"
        val image = File(
            Environment.getExternalStorageDirectory().absoluteFile.toString() + "/DCIM/SmartRemont/Photo/",
            uniqName
        )

        /*// Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
                imageFileName, *//* prefix *//*
                ".jpg", *//* suffix *//*
                storageDir      *//* directory *//*
        )*/

        return image
    }

    private fun bitmapScaleBig(
        mCurrentPhotoPath: String?,
        windowManager: WindowManager
    ): BitmapFactory.Options? {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val targetW = size.x
        val targetH = size.y

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        return bmOptions
    }

    private fun bitmapScaleSmall(mCurrentPhotoPath: String?): BitmapFactory.Options {
        //val width = size.x
        //val height = size.y
        val width = 300
        val height = 300

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / width, photoH / height)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        return bmOptions
    }

    fun insertAndScale(
        windowManager: WindowManager,
        mCurrentPhotoPath: String?,
        thumbImage: Bitmap,
        mCurrentPhotoName: String?
    ) {
        userDefectMediaDao.insert(
            UserDefectMediaEntity(
                remontID = remontID,
                checkListID = checkListID,
                fileUrl = mCurrentPhotoPath,
                fileName = mCurrentPhotoName,
                dateCreate = DateFormatter.pointWithYearAndTime(Date()),
                isForSend = 1,
                fileType = "photo",
                defectStatus = 0,
                stage_id = ACTIVE_STAGE_ID
            )
        )

        //val lBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bitmapScaleBig(mCurrentPhotoPath, windowManager))
        //viewState.setCapturedPhoto(thumbImage, lBitmap, mCurrentPhotoPath)
    }

    fun deletePhotoFromDB(remontCheckListPhotoID: Int?) {
        if (AppConstant.isWorkWithCheckListAccepted(
                CURRENT_STAGE_ID,
                ACTIVE_STAGE_ID,
                STAGE_STATUS_ID
            )
        ) {
            userDefectMediaDao.disablePhoto(remontCheckListPhotoID)
        } else {
            viewState.showToast("Этап неактивен")
        }
    }

    fun updateAudioRecord(mAudioFile: File?) {
        remontCheckListDao.updateAudio(
            sRemontCheckListID,
            mAudioFile!!.absolutePath,
            mAudioFile.name
        )
    }

    fun deleteAudioFromDB(mAudioFile: File) {
        mAudioFile.delete()
        remontCheckListDao.deleteAudio(sRemontCheckListID)
    }


}
