package kz.cheesenology.smartremontmobile.view.main.webview

import kz.cheesenology.smartremontmobile.data.reporttype.ReportTypeEntity
import moxy.MvpView


interface WebViewDefaultView : MvpView{
    fun setURL(s: String, toByteArray: ByteArray)
    fun setSpinnerData(it: MutableList<ReportTypeEntity>?)
}