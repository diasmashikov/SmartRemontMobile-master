package kz.cheesenology.smartremontmobile.view.main.clientphoto

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.provider.MediaStore
import android.view.WindowManager
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@InjectViewState
class ClientPhotoPresenter @Inject constructor(var network: NetworkApi) : MvpPresenter<ClientPhotoView>() {

    var remontID = 0

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        /*network.getCheckListDetailInfo(login!!, password!!, remontID, checkListID, roomID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showDialog("Получение данных") }
                .doOnTerminate { viewState.dismissDialog() }
                .subscribe({ it ->
                    if (it.isSuccessful) {
                        val goodList = mutableListOf<Standart>()
                        val weakList = mutableListOf<Standart>()
                        for (item in it.body()!!.value!!.standart) {
                            if (item.isGood == 1) {
                                goodList.add(item)
                            } else {
                                weakList.add(item)
                            }
                        }
                        viewState.setGoodStandart(goodList)
                        viewState.setWeakStandart(weakList)

                        if (it.body()!!.value!!.info != null)
                            viewState.setCheckInfo(it.body()!!.value!!.info!!)

                        viewState.setUserPhotos(it.body()!!.value!!.photos)

                        if (it.body()!!.value!!.info != null)
                            if (it.body()!!.value!!.info!!.audioInfo != null)
                                viewState.setUserRecordedAudio(it.body()!!.value!!.info!!.audioInfo)

                        viewState.setHistory(it.body()!!.value!!.history)

                    }
                }, {
                    it.printStackTrace()
                })*/
    }

    fun scaleBitmapResult(windowManager: WindowManager, mCurrentPhotoPath: String?, thumbImage: Bitmap, selectRoom: String) {
        //val sBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bitmapScaleSmall(mCurrentPhotoPath))
        val lBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bitmapScaleBig(mCurrentPhotoPath, windowManager))
        viewState.setCapturedPhoto(thumbImage, lBitmap, mCurrentPhotoPath, selectRoom)
    }

    @SuppressLint("CheckResult")
    fun deletePhotoFromServer(remontCheckListPhotoID: Int?) {

        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")

        /*network.deletePhotoFromServer(login, password, defectID!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showDialog("Удаление фотографии") }
                .doOnTerminate { viewState.dismissDialog() }
                .subscribe({ it ->
                    if (it.isSuccessful) {
                        if (it.body()!!.result.status) {
                            viewState.showToast("Фотография удалена")
                        } else {
                            viewState.showToast(it.bodFy()!!.result.errMsg)
                        }
                    }
                }, {
                    it.printStackTrace()
                    viewState.showToast("Ошибка при отправке данных")
                })*/
    }

    fun captureImage(packageManager: PackageManager, storageDir: File?, selectRoom: String) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile(storageDir)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                viewState.startCameraIntent(takePictureIntent, photoFile, selectRoom)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(storageDir: File?): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        viewState.setImagePath(image.absolutePath)
        return image
    }

    private fun bitmapScaleBig(mCurrentPhotoPath: String?, windowManager: WindowManager): BitmapFactory.Options? {
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

    fun setIntentData(iRemontID: Int) {
        remontID = iRemontID
    }

}
