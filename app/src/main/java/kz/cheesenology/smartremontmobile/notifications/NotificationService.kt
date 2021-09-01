package kz.cheesenology.smartremontmobile.notifications

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import kz.cheesenology.smartremontmobile.R

class NotificationService : IntentService("NotificationService") {
    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    @SuppressLint("NewApi")
    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = this.applicationContext
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = "Описание уведомления"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "kz.cheesenology.smartremontmobile.CHANNEL_ID"
        const val CHANNEL_NAME = "Sample Notification"
    }

    override fun onHandleIntent(intent: Intent?) {
        //Create Channel
        createChannel()

        var notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = "Напоминание об отправке данных " + "(${intent!!.getStringExtra("login")})"
        /*val message =
                """${intent!!.getIntExtra("check_list_cnt", 0)}
            ${intent.getIntExtra("remont_list_cnt", 0)}
            ${intent.getIntExtra("photo_cnt", 0)}
            ${intent.getIntExtra("audio_cnt", 0)}
        """.trimMargin()*/
        val message = "У вас есть данные по ремонтам, которые нужно отправить на сервер"

        val res = this.resources
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotification = Notification.Builder(this, CHANNEL_ID)
                //Set the intent that will fire when the user taps the notification
                .setSmallIcon(R.drawable.smart_logo_small)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_icon))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(message)
                )
                .setContentText(message).build()

            startForeground(1, mNotification)
        } else {
            mNotification = Notification.Builder(this)
                // Set the intent that will fire when the user taps the notification
                .setSmallIcon(R.drawable.smart_logo_small)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_icon))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(message)
                )
                .setSound(uri)
                .setContentText(message).build()
        }

        notificationManager.notify(mNotificationId, mNotification)
    }
}