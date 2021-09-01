package kz.cheesenology.smartremontmobile.model.check

import com.google.gson.annotations.SerializedName

data class UploadCheckDataModel(

        @field:SerializedName("result")
        val result: DefaultResult,

        @field:SerializedName("value")
        val value: UploadResult
)

data class UploadResult(
        @field:SerializedName("is_accepted")
        val isAccepted: Int?,
        @field:SerializedName("check_name")
        val checkName: String?,
        @field:SerializedName("defect_cnt")
        val defectCnt: Int?,
        @field:SerializedName("norm")
        val norm: String?
)

data class DefaultResult(
        @field:SerializedName("status")
        val status: Boolean,
        @field:SerializedName("err_msg")
        var errMsg: String
)