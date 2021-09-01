package kz.cheesenology.smartremontmobile.data.requestlist.drafthistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_request_draft_check_history_tab")
data class RequestCheckListHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "client_request_draft_check_history_id") var client_request_draft_check_history_id: Int? = null,
    @ColumnInfo(name = "client_request_id") var client_request_id: Int? = null,
    @ColumnInfo(name = "employee_id") var employee_id: Int? = null,
    @ColumnInfo(name = "check_date") var check_date: String? = null,
    @ColumnInfo(name = "draft_status") var draft_status: Int? = null,
    @ColumnInfo(name = "draft_defect_file_name") var draft_defect_file_name: String? = null,
    @ColumnInfo(name = "draft_defect_file_url") var draft_defect_file_url: String? = null,
    @ColumnInfo(name = "okk_comment") var okk_comment: String? = null,
    @ColumnInfo(name = "client_request_document_id") var client_request_document_id: Int? = null,
    @ColumnInfo(name = "okk_fio") var okk_fio: String? = null,
    @ColumnInfo(name = "is_for_send") var is_for_send: Int? = null,
    @ColumnInfo(name = "is_okk_checked") var is_okk_checked: Int? = null,
    @ColumnInfo(name = "okk_check_date") var okk_check_date: String? = null
)