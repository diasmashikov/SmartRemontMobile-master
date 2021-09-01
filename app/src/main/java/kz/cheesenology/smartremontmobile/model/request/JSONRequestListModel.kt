package kz.cheesenology.smartremontmobile.model.request

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class JSONRequestListModel(
    @SerializedName("result") val result: Result,
    @SerializedName("value") val value: Value
)

data class ClientRequestListModel(
    @SerializedName("client_request_id") val client_request_id: Int,
    @SerializedName("remont_id") val remont_id: Int,
    @SerializedName("resident_name") val resident_name: String,
    @SerializedName("resident_id") val resident_id: Int,
    @SerializedName("flat_num") val flat_num: String,
    @SerializedName("manager_project_id") val manager_project_id: Int,
    @SerializedName("manager_project_name") val manager_project_name: String,
    @SerializedName("manager_project_date") val manager_project_date: String,
    @SerializedName("manager_project_phone") val manager_project_phone: String? = null,
    @SerializedName("flat_list_name") val flat_list_name: String? = null,
    @SerializedName("flat_list_url") val flat_list_url: String? = null,
    @SerializedName("okk_id") val okk_id: Int,
    @SerializedName("okk_name") val okk_name: String,
    @SerializedName("okk_date") val okk_date: String,
    @SerializedName("resident_delivery_date") val resident_delivery_date: String,
    @SerializedName("last_planned_date") val last_planned_date: String,
    @SerializedName("is_draft_accept") val is_draft_accept: Int,
    @SerializedName("draft_status") val draft_status: String,
    @SerializedName("okk_check_date") val okk_check_date: String?
)

data class DraftCheckHistoryModel(
    @SerializedName("client_request_draft_check_history_id") val client_request_draft_check_history_id: Int,
    @SerializedName("client_request_id") val client_request_id: Int,
    @SerializedName("employee_id") val employee_id: Int,
    @SerializedName("file_url") val file_url: String? = null,
    @SerializedName("file_name") val file_name: String? = null,
    @SerializedName("okk_comment") val okk_comment: String? = null,
    @SerializedName("check_date") val check_date: String,
    @SerializedName("draft_status") val draft_status: Int,
    @SerializedName("client_request_document_id") val client_request_document_id: Int? = null,
    @SerializedName("okk_fio") val okk_fio: String,
    @SerializedName("okk_check_date") val okk_check_date: String?
)

data class DraftCheckListModel(
    @SerializedName("draft_check_list_id") val draft_check_list_id: Int,
    @SerializedName("draft_check_list_pid") val draft_check_list_pid: Int? = null,
    @SerializedName("draft_check_list_name") val draft_check_list_name: String,
    @SerializedName("is_active") val is_active: Int
)

data class Result(
    @SerializedName("status") val status: Boolean
)

data class ClientRequestDraftCheckModel(
    @SerializedName("client_request_check_id") var client_request_check_id: Int? = null,
    @SerializedName("client_request_id") var client_request_id: Int? = null,
    @SerializedName("draft_check_list_id") var draft_check_list_id: Int? = null,
    @SerializedName("content_url") var content_url: String? = null,
    @SerializedName("content_type") var content_type: String? = null,
    @SerializedName("file_name") var file_name: String? = null,
    @SerializedName("is_accepted") var is_accepted: Int? = null,
    @SerializedName("employee_id") var employee_id: Int? = null,
    @SerializedName("comments") var comments: String? = null,
    @SerializedName("date_create") var date_create: String? = null
)

data class ClientRequestCheckFileModel(
    @SerializedName("client_request_check_file_id") var client_request_check_file_id: Int? = null,
    @SerializedName("client_request_check_id") var client_request_check_id: Int? = null,
    @SerializedName("client_request_id") var client_request_id: Int? = null,
    @SerializedName("draft_check_list_id") var draft_check_list_id: Int? = null,
    @SerializedName("content_type") var content_type: String? = null,
    @SerializedName("content_url") var content_url: String? = null,
    @SerializedName("file_name") var file_name: String? = null
)

data class Value(
    @SerializedName("client_request_list") val client_request_list: List<ClientRequestListModel?>? = null,
    @SerializedName("draft_check_list") val draft_check_list: List<DraftCheckListModel?>? = null,
    @SerializedName("draft_check_history_list") val draft_check_history_list: List<DraftCheckHistoryModel?>? = null,
    @SerializedName("client_request_draft_check") val client_request_draft_check: List<ClientRequestDraftCheckModel?>? = null,
    @SerializedName("client_request_check_file") val client_request_check_file: List<ClientRequestCheckFileModel?>? = null
)