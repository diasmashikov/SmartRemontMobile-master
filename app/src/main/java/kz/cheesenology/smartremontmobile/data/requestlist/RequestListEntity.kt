package kz.cheesenology.smartremontmobile.data.requestlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request_list_tab")
data class RequestListEntity(
    @PrimaryKey
    @ColumnInfo(name = "client_request_id") var client_request_id: Int? = null,
    @ColumnInfo(name = "remont_id") var remont_id: Int? = null,
    @ColumnInfo(name = "resident_name") var resident_name: String? = null,
    @ColumnInfo(name = "resident_id") var resident_id: Int? = null,
    @ColumnInfo(name = "flat_num") var flat_num: String? = null,
    @ColumnInfo(name = "manager_project_id") var manager_project_id: Int? = null,
    @ColumnInfo(name = "manager_project_name") var manager_project_name: String? = null,
    @ColumnInfo(name = "flat_list_name") var flat_list_name: String? = null,
    @ColumnInfo(name = "flat_list_url") var flat_list_url: String? = null,
    @ColumnInfo(name = "manager_project_phone") var manager_project_phone: String? = null,
    @ColumnInfo(name = "okk_id") var okk_id: Int? = null,
    @ColumnInfo(name = "okk_name") var okk_name: String? = null,
    @ColumnInfo(name = "okk_date") var okk_date: String? = null,
    @ColumnInfo(name = "okk_check_date") var okk_check_date: String? = null,
    @ColumnInfo(name = "resident_delivery_date") var resident_delivery_date: String? = null,
    @ColumnInfo(name = "last_planned_date") var last_planned_date: String? = null,
    @ColumnInfo(name = "is_draft_accept") var is_draft_accept: Int? = null,
    @ColumnInfo(name = "draft_status") var draft_status: String? = null,
    @ColumnInfo(name = "is_hide") var is_hide: Int? = null
)