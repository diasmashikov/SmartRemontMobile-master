package kz.cheesenology.smartremontmobile.view.stagestatushistory

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.stagestatushist.StageStatusHistoryDao
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class StageStatusHistoryPresenter @Inject constructor(val stageStatusHistoryDao: StageStatusHistoryDao) : MvpPresenter<StageStatusHistoryView>() {

    var remontID: Int = 0

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        stageStatusHistoryDao.getStageHistoryByRemontID(remontID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    viewState.setList(it)
                }
    }

    fun setRemontID(i: Int?) {
        remontID = i!!
    }
}