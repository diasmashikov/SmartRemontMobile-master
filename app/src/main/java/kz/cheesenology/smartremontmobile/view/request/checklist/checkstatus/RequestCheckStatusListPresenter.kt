package kz.cheesenology.smartremontmobile.view.request.checklist.checkstatus

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.requestlist.RequestListDao
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryDao
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryEntity
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusDao
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusEntity
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.MvpPresenter
import java.io.File
import javax.inject.Inject

class RequestCheckStatusListPresenter
@Inject constructor(
        val requestCheckListHistoryDao: RequestCheckListHistoryDao,
        val photoDraftStatusDao: PhotoDraftStatusDao,
        val requestListDao: RequestListDao,
    ) :
    MvpPresenter<RequestCheckStatusListView>() {

    var clientRequestID: Int = 0

    var okkFIO: String? = null

    init {
        okkFIO = PrefUtils.prefs.getString("fio", null)
    }

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        requestCheckListHistoryDao.getHistoryByClientRequestID(clientRequestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setData(it)
            })
    }

    fun setIntentData(id: Int) {
        clientRequestID = id
    }

    fun acceptRequest(comment: String?, dialogPhotoPathList: MutableList<String?>) {
        if (!checkSendRequestExist()) {
            val id = requestCheckListHistoryDao.insert(
                RequestCheckListHistoryEntity(
                    client_request_id = clientRequestID,
                    check_date = AppConstant.getCurrentDateFullWithoutSeconds(),
                    draft_status = 1,
                    is_for_send = 1,
                    okk_fio = okkFIO,
                    okk_comment = comment
                )
            )

            for (item in dialogPhotoPathList) {
                photoDraftStatusDao.insert(PhotoDraftStatusEntity(
                    draft_status_id = id.toInt(),
                    photo_url = item
                ))
            }

            viewState.closeAccpeptDialog()
        }
    }

    fun checkSendRequestExist(): Boolean {
        val int = requestCheckListHistoryDao.checkRequestSendExistance(clientRequestID)
        return if (int > 0) {
            viewState.showToast("У вас уже есть отправленная запись по этой заявке. Повторная проверка невозможна")
            true
        } else {
            false
        }
    }

    fun rejectRequest(currentPhotoPath: String?, comment: String?, selectedDate: String?) {
        if (!checkSendRequestExist()) {
            val file = File(currentPhotoPath)
            if (file.exists()) {
                requestCheckListHistoryDao.insert(
                    RequestCheckListHistoryEntity(
                        client_request_id = clientRequestID,
                        draft_defect_file_name = file.name,
                        check_date = AppConstant.getCurrentDateFullWithoutSeconds(),
                        draft_status = 2,
                        is_for_send = 1,
                        okk_fio = okkFIO,
                        okk_comment = comment,
                        okk_check_date = selectedDate
                    )
                )
                viewState.closeRejectDialog()
            } else {
                viewState.showToast("Прикрепите Фото дефектного акта")
            }
        }
    }

    fun getPhotoUrlsFromDB(draft_status_id: Int?) {

        if (draft_status_id != null) {
            photoDraftStatusDao.getPhotoListByID(draft_status_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewState.showPicturesDialog(it)
                    })
        }

    }

    fun searchDatabase(searchQuery: String){
        requestListDao.searchDatabase(searchQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.getDataFromDB(it)
                })
    }

    fun getData()
    {
        requestListDao.readData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.getDataFromDB(it)
                })
    }
}