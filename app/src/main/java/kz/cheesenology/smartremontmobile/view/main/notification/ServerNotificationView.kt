package kz.cheesenology.smartremontmobile.view.main.notification

import kz.cheesenology.smartremontmobile.data.notification.NotificationEntity
import moxy.MvpView

interface ServerNotificationView : MvpView {
    fun setNotificationList(data: List<NotificationEntity>)
    fun showToast(s: String)
}
