package kz.cheesenology.smartremontmobile.model

import com.google.gson.annotations.SerializedName

data class RemontListModel(

        @field:SerializedName("result")
        val result: Result,

        @field:SerializedName("value")
        val value: List<ValueItem>
)

data class Result(
        @field:SerializedName("status")
        val status: Boolean,
        @field:SerializedName("err_msg")
        var errMsg: String
)

data class ValueItem(

        @field:SerializedName("okk_send_date")
        val okkSendDate: String? = null,

        @field:SerializedName("remont_date_begin")
        val remontDateBegin: String? = null,

        @field:SerializedName("stage_status_id")
        val stageStatusId: Int? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("client_request_id")
        val clientRequestId: Int? = null,

        @field:SerializedName("status_name")
        val statusName: String? = null,

        @field:SerializedName("okk_status")
        val okkStatus: Int? = null,

        @field:SerializedName("active_stage_status_name")
        val activeStageStatusName: String? = null,

        @field:SerializedName("contractor_answer_date")
        val contractorAnswerDate: String? = null,

        @field:SerializedName("okk_status_text")
        val okkStatusText: String? = null,

        @field:SerializedName("fio")
        val fio: String? = null,

        @field:SerializedName("contractor_name")
        val contractorName: String? = null,

        @field:SerializedName("remont_status_id")
        val remontStatusId: Int? = null,

        @field:SerializedName("okk_answer_date")
        val okkAnswerDate: String? = null,

        @field:SerializedName("contractor_send_date")
        val contractorSendDate: String? = null,

        @field:SerializedName("contractor_id")
        val contractorId: Int? = null,

        @field:SerializedName("price")
        val price: String? = null,

        @field:SerializedName("remont_id")
        val remontId: Int? = null,

        @field:SerializedName("contractor_status")
        val contractorStatus: Int? = null,

        @field:SerializedName("active_stage_name")
        val activeStageName: String? = null,

        @field:SerializedName("active_stage_id")
        val activeStageID: Int? = null,

        @field:SerializedName("client_name")
        val clientName: String? = null,

        @field:SerializedName("okk_employee_id")
        val okkEmployeeId: Int? = null,

        @field:SerializedName("info")
        val info: String? = null
)