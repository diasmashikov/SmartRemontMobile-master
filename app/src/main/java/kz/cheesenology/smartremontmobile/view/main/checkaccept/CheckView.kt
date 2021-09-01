package kz.cheesenology.smartremontmobile.view.main.checkaccept

import android.content.Intent
import android.graphics.Bitmap
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListEntity
import kz.cheesenology.smartremontmobile.data.check.history.CheckListHistoryEntity
import kz.cheesenology.smartremontmobile.data.standartphoto.StandartPhotoEntity
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaEntity
import kz.cheesenology.smartremontmobile.model.check.UploadResult
import moxy.MvpView
import java.io.File

interface CheckView: MvpView {
    fun startCameraIntent(takePictureIntent: Intent, photoFile: File, absoluteFile: String)
    fun setImagePath(absolutePath: String?)
    fun setCapturedPhoto(sBitmap: Bitmap?, lBitmap: Bitmap?, mCurrentPhotoPath: String?)
    fun showDialog(s: String)
    fun dismissDialog()
    fun setHistory(history: List<CheckListHistoryEntity>)
    fun setGoodStandart(goodList: List<StandartPhotoEntity>)
    fun setWeakStandart(weakList: List<StandartPhotoEntity>)
    fun setCheckInfo(info: RemontCheckListEntity)
    fun showToast(s: String)
    fun setResultData(value: UploadResult)
    fun setUserRecordedAudio(audioInfo: String?)
    fun hideAudioPlayer()
    fun setPhotos(it: List<UserDefectMediaEntity>)
}
