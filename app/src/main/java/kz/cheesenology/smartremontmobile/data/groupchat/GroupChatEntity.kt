package kz.cheesenology.smartremontmobile.data.groupchat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_chat_tab")
data class GroupChatEntity(
    @PrimaryKey
    @ColumnInfo(name = "group_chat_id") var groupChatID: Int,
    @ColumnInfo(name = "group_chat_name") var groupChatName: String?,
    @ColumnInfo(name = "group_chat_code") var groupChatCode: String?,
    @ColumnInfo(name = "group_chat_order_num") var groupChatOrderNum: Int?,
    @ColumnInfo(name = "group_chat_short_name") var groupChatShortName: String?,
    @ColumnInfo(name = "stage_id") var stageID: Int? = null
)