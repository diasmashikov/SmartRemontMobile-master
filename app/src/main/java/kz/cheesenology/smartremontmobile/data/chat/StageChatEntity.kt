package kz.cheesenology.smartremontmobile.data.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage_chat_tab")
data class StageChatEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chat_message_id") var chatMessageID: Int? = null,
    @ColumnInfo(name = "stage_chat_id") var stageChatID: Int? = null,
    @ColumnInfo(name = "group_chat_id") var groupChatID: Int,
    @ColumnInfo(name = "employee_id") var employeeID: Int? = null,
    @ColumnInfo(name = "client_id") var client_id: Int? = null,
    @ColumnInfo(name = "remont_id") var remontID: Int? = null,
    @ColumnInfo(name = "date_chat") var dateChat: String? = null,
    @ColumnInfo(name = "message") var message: String? = null,
    @ColumnInfo(name = "chat_fio") var fio: String? = null,
    @ColumnInfo(name = "is_photo_report") var is_photo_report: Int? = null,
    @ColumnInfo(name = "request_id") var requestID: Int? = null,
    @ColumnInfo(name = "rowversion") var rowversion: Long? = null,
)