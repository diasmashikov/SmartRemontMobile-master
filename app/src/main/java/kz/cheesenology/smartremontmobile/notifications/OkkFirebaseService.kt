package kz.cheesenology.smartremontmobile.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.data.notification.NotificationDao
import kz.cheesenology.smartremontmobile.data.notification.NotificationEntity
import kz.cheesenology.smartremontmobile.network.NetworkApi
import kz.cheesenology.smartremontmobile.util.DateFormatter
import kz.cheesenology.smartremontmobile.util.PrefUtils
import kz.cheesenology.smartremontmobile.view.auth.AuthActivity
import java.util.*
import javax.inject.Inject


class OkkFirebaseService : FirebaseMessagingService() {

    private val channelID = "123"

    @Inject
    lateinit var networkApi: NetworkApi

    @Inject
    lateinit var notificationDao: NotificationDao

    override fun onCreate() {
        //SmartRemontApplication[this].component.inject(this)
        SmartRemontApplication.instance!!.plusDatabaseComponent(
            PrefUtils.prefs.getString(
                "login",
                ""
            )!!
        )
        SmartRemontApplication[this].databaseComponent?.inject(this)
        super.onCreate()
    }

    @SuppressLint("NewApi")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var notification: Notification

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        createChannel(title, body)

        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra("notification_str", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_CANCEL_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = Notification.Builder(this, channelID)
                //Set the intent that will fire when the user taps the notification
                .setSmallIcon(R.drawable.smart_logo_small)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(body)
                )
                .setContentText(body).build()
        } else {
            notification = Notification.Builder(this)
                // Set the intent that will fire when the user taps the notification
                .setSmallIcon(R.drawable.smart_logo_small)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(body)
                )
                .setContentText(body).build()
        }

        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.notify(321, notification)
        /*with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(NotificationID.id, notification)
        }*/

        notificationDao.insert(
            NotificationEntity(
                title = title!!,
                text = body,
                dateCreate = DateFormatter.pointWithYearAndTime(Date()),
                isRead = false
            )
        )
    }

    private fun createChannel(title: String?, body: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = this.applicationContext
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                channelID,
                "OKK", importance
            )
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = body
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}