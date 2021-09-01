package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class ChatMessageModel(
    @ColumnInfo(name = "stage_chat_id") var stageChatID: Int? = null,
    @ColumnInfo(name = "group_chat_id") var groupChatID: Int,
    @ColumnInfo(name = "employee_id") var employeeID: Int? = null,
    @ColumnInfo(name = "date_chat") var dateChat: String?,
    @ColumnInfo(name = "client_id") var client_id: Int? = null,
    @ColumnInfo(name = "message") var message: String?,
    @ColumnInfo(name = "remont_id") var remontID: Int,
    @ColumnInfo(name = "chat_fio") var chatFio: String,
    @ColumnInfo(name = "request_id") var requestID: Int? = null,
    @ColumnInfo(name = "request_status_id") var requestStatusID: Int? = null,
    @ColumnInfo(name = "error_msg") var errorMsg: String? = null,
    @ColumnInfo(name = "chat_message_id") var chatMessageID: Int? = null
)