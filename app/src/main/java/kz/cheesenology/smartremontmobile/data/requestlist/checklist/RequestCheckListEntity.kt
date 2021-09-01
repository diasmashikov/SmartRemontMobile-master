package kz.cheesenology.smartremontmobile.data.requestlist.checklist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request_check_list_tab")
data class RequestCheckListEntity(
    @PrimaryKey
    @ColumnInfo(name = "draft_check_list_id") var draft_check_list_id: Int? = null,
    @ColumnInfo(name = "draft_check_list_pid") var draft_check_list_pid: Int? = null,
    @ColumnInfo(name = "draft_check_list_name") var draft_check_list_name: String? = null,
    @ColumnInfo(name = "is_active") var is_active: Int? = null
)