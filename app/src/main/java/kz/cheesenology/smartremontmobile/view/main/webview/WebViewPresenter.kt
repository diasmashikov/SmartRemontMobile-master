package kz.cheesenology.smartremontmobile.view.main.webview

import android.annotation.SuppressLint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeDao
import kz.cheesenology.smartremontmobile.util.AppConstant
import kz.cheesenology.smartremontmobile.util.PrefUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@InjectViewState
class WebViewPresenter @Inject constructor(val reportTypeDao: ReportTypeDao) :
    MvpPresenter<WebViewDefaultView>() {

    var webViewType: String? = null

    companion object {
        val login = PrefUtils.prefs.getString("login", "")
        val password = PrefUtils.prefs.getString("password", "")
        val server = AppConstant.getServerName()

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        var currentDate1 = sdf.format(AppConstant.getTimeMonthAgo())
        var currentDate2 = sdf.format(Date(Date().time))
    }

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        reportTypeDao.getList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setSpinnerData(it.toMutableList())
            }, {
                it.printStackTrace()
            })

    }

    fun loadUrL(code: String) {
        showReport(currentDate1, currentDate2, code)
    }

    private fun showReport(date1: String, date2: String, code: String) {
        val post = """
                    report_type_code=${code}&login=${login}&password=${password}&date_begin=${date1}&date_end=${date2}
                """.trimIndent()
        //localhost/webview/okk-check-history-list?date_begin=01.01.2020&date_end=02.04.2020&login=abenov.yerzhan.86@bk.ru&password=628628
        viewState.setURL("$server/webview/report-list", post.toByteArray())
    }

    fun setFilterDates(date1: String, date2: String, code: String) {
        currentDate1 = date1
        currentDate2 = date2
        showReport(date1, date2, code)
    }
}