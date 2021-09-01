package kz.cheesenology.smartremontmobile.data.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "stage_chat_file_tab")
data class StageChatFileEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stage_chat_file_id") var stageChatFileID: Int? = null,
    @ColumnInfo(name = "stage_chat_id") var stageChatID: Int,
    @ColumnInfo(name = "file_name") var file_name: String,
    @ColumnInfo(name = "file_ext") var file_ext: String,
    @ColumnInfo(name = "file_url") var file_url: String,
    @ColumnInfo(name = "chat_message_id") var chatMessageID: Int
)