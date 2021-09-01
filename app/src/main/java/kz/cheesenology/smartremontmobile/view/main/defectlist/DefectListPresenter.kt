package kz.cheesenology.smartremontmobile.view.main.defectlist

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.check.RemontCheckListDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomDao
import kz.cheesenology.smartremontmobile.data.rooms.RoomEntity
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaDao
import kz.cheesenology.smartremontmobile.data.userphoto.UserDefectMediaEntity
import kz.cheesenology.smartremontmobile.model.CheckListDefectSelectModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.DateFormatter
import moxy.InjectViewState
import moxy.MvpPresenter
import java.io.File
import java.util.*
import javax.inject.Inject


@InjectViewState
class DefectListPresenter @Inject constructor(
    var userUserDefectMediaDao: UserDefectMediaDao,
    var roomDao: RoomDao,
    var remontCheckListDao: RemontCheckListDao,
    var userDefectMediaDao: UserDefectMediaDao
) : MvpPresenter<DefectListView>() {

    var remontID: Int = 0
    var currentStageID: Int = 0

    var roomList: List<RoomEntity>? = null
    var checkList: List<CheckListDefectSelectModel>? = null

    fun setIntentData(iRemontID: Int, iCurrentStageID: Int) {
        remontID = iRemontID
        currentStageID = iCurrentStageID
    }

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        roomDao.getRoomListByRemontID(remontID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //viewState.setRoomList(roomList)
                    roomList = it
                }, {
                    it.printStackTrace()
                })

        remontCheckListDao.getCheckListByRoomForDefects(currentStageID, remontID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //viewState.setCheckListData(it)
                    checkList = it
                }, {
                    it.printStackTrace()
                })

        userUserDefectMediaDao.getDefectPhotosByRemont(remontID, currentStageID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setDefectList(it)
                }, {
                    it.printStackTrace()
                })

    }

    @SuppressLint("CheckResult")
    fun getCheckListsByRoom(roomID: Int, remontCheckListPhotoID: Int?) {
        //userDefectMediaDao.setRoomID(defectID, roomID)
    }

    fun setCheckListID(remontCheckListPhotoID: Int?, checkListID: Int?) {
        userDefectMediaDao.setCheckList(remontCheckListPhotoID, checkListID)
    }

    fun preDefectSet(model: T) {
        viewState.showDefectInfoDialog(arrayListOf(model), roomList, checkList)
    }

    fun setDefectPhotoInfo(
            remontCheckListPhotoID: Int,
            checkListValue: CheckListDefectSelectModel?,
            comment: String?,
            remontID: Int,
            mAudioFile: File?) {
        //Занесение фотографии в таблицу user_photo_tab
        userDefectMediaDao.setPhotoInfo(remontCheckListPhotoID, checkListValue?.checkListID, comment, mAudioFile?.name)
    }

    fun setDefectPhotoInfoCombine(
            defectID: Int,
            checkListValue: CheckListDefectSelectModel?,
            comment: String?) {
        //Занесение фотографии в таблицу user_photo_tab
        userDefectMediaDao.setPhotoInfoCombine(defectID, checkListValue?.checkListID, comment)
    }

    fun setMultiSelectDefects(selectedList: MutableList<T>) {
        viewState.showDefectInfoDialog(selectedList, roomList, checkList)
    }

    fun setDefectListInfo(list: MutableList<T>, checkListValue: CheckListDefectSelectModel?, comment: String?, mAudioFile: File?) {
        for (item in list) {
            setDefectPhotoInfoCombine(item.defectID!!, checkListValue, comment)
        }
    }

    fun deleteDefects(list: ArrayList<T>) {
        list.forEach {
            userUserDefectMediaDao.deleteByID(it.defectID)
        }
    }

    fun showMediaItem(defectListModel: T) {
        when (defectListModel.fileType) {
            "photo" -> {
                viewState.showItemPhoto(defectListModel)
            }
            "video" -> {
                viewState.showItemVideo(defectListModel)
            }
        }
    }

    fun acceptDefects(arrayList: ArrayList<T>) {
        val list: List<Int> = arrayList.map {
            it.defectID!!
        }
        userUserDefectMediaDao.acceptByID(list)
    }

    fun deleteAudio(defectID: Int?) {
        userUserDefectMediaDao.deleteAudio(defectID)
    }

    fun showSendStatsDialog() {
        viewState.navigateToSendDialog(remontID)
    }

    fun setAttachFromFile(uriList: ArrayList<String>, type: String?) {
        uriList.forEach {
            val file = File(it)
            if (file.exists()) {
                try {
                    file.copyTo(File(AppConstant.FULL_MEDIA_PHOTO_PATH, file.name), true)
                    userUserDefectMediaDao.insert(UserDefectMediaEntity(
                            remontID = remontID,
                            fileName = file.name,
                            dateCreate = DateFormatter.pointWithYearAndTime(Date()),
                            isForSend = 1,
                            fileUrl = file.absolutePath,
                            fileType = type,
                            defectStatus = 0,
                            stage_id = currentStageID
                    ))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


}