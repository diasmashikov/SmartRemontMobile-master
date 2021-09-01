package kz.cheesenology.smartremontmobile.data.requestlist.checkaccept

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_request_draft_check_tab")
data class RequestCheckAcceptEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "client_request_check_id") var client_request_check_id: Int? = null,
    @ColumnInfo(name = "client_request_id") var client_request_id: Int? = null,
    @ColumnInfo(name = "draft_check_list_id") var draft_check_list_id: Int? = null,
    @ColumnInfo(name = "is_accepted") var is_accepted: Int? = null,
    @ColumnInfo(name = "employee_id") var employee_id: Int? = null,
    @ColumnInfo(name = "date_create") var date_create: String? = null,
    @ColumnInfo(name = "is_for_send") var is_for_send: Int? = null,
)