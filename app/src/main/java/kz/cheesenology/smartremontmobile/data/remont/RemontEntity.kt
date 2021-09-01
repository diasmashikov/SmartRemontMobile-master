package kz.cheesenology.smartremontmobile.data.remont

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remont_tab")
data class RemontEntity(
        @PrimaryKey
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "remont_status_id") var remontStatusID: Int?,
        @ColumnInfo(name = "info") var info: String?,
        @ColumnInfo(name = "okk_status") var okkStatus: Int?,
        @ColumnInfo(name = "okk_employee_id") var okkEmployeeID: Int?,
        @ColumnInfo(name = "okk_send_date") var okkSendDate: String?,
        @ColumnInfo(name = "okk_answer_date") var okkAnswerDate: String?,
        @ColumnInfo(name = "contractor_send_date") var contractorSendDate: String?,
        @ColumnInfo(name = "contractor_answer_date") var contractorAnswerDate: String?,
        @ColumnInfo(name = "adress") var address: String?,
        @ColumnInfo(name = "remont_date_begin") var remontDateBegin: String?,
        @ColumnInfo(name = "price") var price: String?,
        @ColumnInfo(name = "okk_status_text") var okkStatusText: String?,
        @ColumnInfo(name = "contractor_name") var contractorName: String?,
        @ColumnInfo(name = "client_name") var clientName: String?,
        @ColumnInfo(name = "status_name") var statusName: String?,
        @ColumnInfo(name = "active_stage_name") var activeStageName: String?,
        @ColumnInfo(name = "active_stage_id") var activeStageID: Int?,
        @ColumnInfo(name = "active_stage_status_name") var activeStageStatusName: String?,
        @ColumnInfo(name = "fio") var fio: String?,
        @ColumnInfo(name = "stage_status_id") var stageStatusID: Int?
)