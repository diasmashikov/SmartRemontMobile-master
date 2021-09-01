package kz.cheesenology.smartremontmobile.data.notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_tab")
class NotificationEntity (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "notification_id") var notificationID: Int? = null,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "text") var text: String? = null,
        @ColumnInfo(name = "date_create") var dateCreate: String? = null,
        @ColumnInfo(name = "is_read") var isRead : Boolean = true
)