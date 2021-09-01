package kz.cheesenology.smartremontmobile.view.request.checklist.defectphoto

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.requestlist.checkphoto.CheckRequestPhotoDao
import moxy.MvpPresenter
import javax.inject.Inject

class RequestCheckListPhotoFixPresenter @Inject constructor(val checkRequestPhotoDao: CheckRequestPhotoDao) :
    MvpPresenter<RequestCheckListPhotoFixView>() {

    var checkID: Int? = null
    var draftCheckID: Int? = null
    var clientRequestID: Int? = null

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        checkRequestPhotoDao.getPhotoByID(clientRequestID, draftCheckID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.setData(it)
            }

    }

    fun setCheckID(icheckID: Int, idraftCheckID: Int, iclientRequestID: Int) {
        checkID = icheckID
        draftCheckID = idraftCheckID
        clientRequestID = iclientRequestID
    }

    fun setComment(photoID: Int, comment: String) {
        checkRequestPhotoDao.updateComment(comment, photoID)

        viewState.closeCommentDialog()
    }

}