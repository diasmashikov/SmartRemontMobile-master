package kz.cheesenology.smartremontmobile.view.auth

import moxy.MvpView

interface AuthView: MvpView {
    fun setLoginLayoutError(s: String)
    fun setPasswordLayoutError(s: String)
    fun disableLoginLayoutError()
    fun disablePasswordLayoutError()
    fun showAuthError(errMsg: String)
    fun navigateToMainMenu(s: String)
    fun showDialog()
    fun dismissDialog()
    fun checkAuthState()
    fun setVersion(s: String)
    fun navigateToNotificationScreen()
    fun showToast(s: String)
    fun prepareForInstall()
}
