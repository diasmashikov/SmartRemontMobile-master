package kz.cheesenology.smartremontmobile.view.main.ratings

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.rating.RatingDetailDao
import kz.cheesenology.smartremontmobile.data.rating.remont.RatingRemontDao
import kz.cheesenology.smartremontmobile.data.rating.remont.RatingRemontEntity
import kz.cheesenology.smartremontmobile.data.rating.step.RatingStepDao
import kz.cheesenology.smartremontmobile.data.request.RequestDao
import kz.cheesenology.smartremontmobile.data.request.RequestEntity
import kz.cheesenology.smartremontmobile.domain.RequestConstant
import kz.cheesenology.smartremontmobile.domain.work.WorkRunner
import kz.cheesenology.smartremontmobile.model.ratings.RatingListAdapterModel
import kz.cheesenology.smartremontmobile.util.AppConstant
import moxy.InjectViewState
import moxy.MvpPresenter
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@InjectViewState
class RatingsPresenter @Inject constructor(
    val ratingDetailDao: RatingDetailDao,
    val ratingStepDao: RatingStepDao,
    val ratingRemontDao: RatingRemontDao,
    val requestDao: RequestDao
) :
    MvpPresenter<RatingView>() {

    var remontID = 0

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        ratingDetailDao.getRatingsList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val list = mutableListOf<RatingListAdapterModel>()
                it.forEach {
                    list.add(
                        RatingListAdapterModel(
                            ratingDetailID = it.ratingDetailID,
                            roleID = it.roleID,
                            detailName = it.detailName,
                            detailCode = it.detailCode,
                            detailWeight = it.detailWeight,
                            stepList = ratingStepDao.getStepByDetailID(it.ratingDetailID, remontID)
                        )
                    )
                    viewState.setViewData(list)
                }
            }, {
                it.printStackTrace()
            })
    }

    fun checkRatings(data: List<rT>) {
        val exist = data.any {
            it.isChecked == false
        }
        if (exist) {
            viewState.showToast("Не все рейтинги отмечены")
        } else {
            val id = requestDao.insert(
                RequestEntity(
                    requestTypeID = RequestConstant.REQUEST_SEND_RATINGS,
                    requestStatusID = RequestConstant.STATUS_REQUEST_CREATE,
                    dateCreate = AppConstant.getCurrentDateFull(),
                    randomNum = AppConstant.getRundomNumber(),
                    data = JSONObject(
                        mapOf(
                            "remont_id" to remontID,
                            "rt_step_ids" to JSONArray(data.map { it.stepCheckedID })
                        )
                    ).toString(),
                    remontID = remontID
                )
            )

            data.forEach {
                ratingRemontDao.insert(
                    RatingRemontEntity(
                        remontID = remontID,
                        stepID = it.stepCheckedID!!,
                        isEdit = false,
                        contractorID = 0,
                        requestID = id.toInt()
                    )
                )
            }

            WorkRunner.startSyncWorker()
            viewState.successPutRatings("Рейтинг поставлен")
        }
    }

    fun setIntentData(stringExtra: Int) {
        remontID = stringExtra
    }
}