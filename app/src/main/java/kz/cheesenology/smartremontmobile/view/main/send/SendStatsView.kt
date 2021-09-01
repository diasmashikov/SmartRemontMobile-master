package kz.cheesenology.smartremontmobile.view.main.send

import kz.cheesenology.smartremontmobile.data.check.SendStatsModel
import moxy.MvpView

interface SendStatsView: MvpView {
    fun setStats(it: SendStatsModel?)
    fun setEmptySendText()
    fun showToast(toString: String)
    fun closeDialog()
    fun showDialog(s: String)
    fun dismissDialog()
}
