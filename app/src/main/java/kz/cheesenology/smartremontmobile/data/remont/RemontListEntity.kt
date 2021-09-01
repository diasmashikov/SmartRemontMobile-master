package kz.cheesenology.smartremontmobile.data.remont

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remont_list_tab")
data class RemontListEntity(
        @PrimaryKey
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "client_request_id") var clientRequestID: Int?,
        @ColumnInfo(name = "remont_status_id") var remontStatusID: Int?,
        @ColumnInfo(name = "info") var info: String?,
        @ColumnInfo(name = "okk_status") var okkStatus: Int?,
        @ColumnInfo(name = "is_okk_status_change") var isOKKStatusChange: Int?,
        @ColumnInfo(name = "okk_employee_id") var okkEmployeeID: Int?,
        @ColumnInfo(name = "okk_send_date") var okkSendDate: String?,
        @ColumnInfo(name = "okk_answer_date") var okkAnswerDate: String?,
        @ColumnInfo(name = "contractor_send_date") var contractorSendDate: String?,
        @ColumnInfo(name = "contractor_answer_date") var contractorAnswerDate: String?,
        @ColumnInfo(name = "adress") var address: String?,
        @ColumnInfo(name = "remont_date_begin") var remontDateBegin: String?,
        @ColumnInfo(name = "price") var price: Double?,
        @ColumnInfo(name = "okk_status_text") var okkStatusText: String?,
        @ColumnInfo(name = "contractor_name") var contractorName: String?,
        @ColumnInfo(name = "client_name") var clientName: String?,
        @ColumnInfo(name = "status_name") var statusName: String?,
        @ColumnInfo(name = "active_stage_name") var activeStageName: String?,
        @ColumnInfo(name = "active_stage_id") var activeStageID: Int?,
        @ColumnInfo(name = "active_stage_status_name") var activeStageStatusName: String?,
        @ColumnInfo(name = "fio") var fio: String?,
        @ColumnInfo(name = "stage_status_id") var stageStatusID: Int?,
        @ColumnInfo(name = "stage_status_comment") var stageStatusComment: String?,
        @ColumnInfo(name = "stage_status_desc") var stageStatusDesc: String?,
        @ColumnInfo(name = "send_status") var sendStatus: Int?,
        @ColumnInfo(name = "error_text") var errorText: String?,
        @ColumnInfo(name = "is_stage_for_send") var isStageForSend: Int?,
        @ColumnInfo(name = "constructor_url") var constructorUrl: String?,
        @ColumnInfo(name = "planirovka_image") var planirovkaImage: String?,
        @ColumnInfo(name = "planirovka_image_url") var planirovkaImageUrl: String?,
        @ColumnInfo(name = "manager_fio") var managerFIO: String?,
        @ColumnInfo(name = "manager_phone") var managerPhone: String?,
        @ColumnInfo(name = "request_num") var requestNum: Int? = null,
        @ColumnInfo(name = "contractor_phone") var contractorPhone: String? = null,
        @ColumnInfo(name = "project_remont_name") var projectRemontName: String? = null,
        @ColumnInfo(name = "internal_master") var internalMaster: String? = null,
        @ColumnInfo(name = "internal_master_phone") var internalMasterPhone: String? = null,
        @ColumnInfo(name = "okk_photo_report_send_date") var okkPhotoReportSendDate: String? = null
)