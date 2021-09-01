package kz.cheesenology.smartremontmobile.model

import com.google.gson.annotations.SerializedName

data class SendDataResultModel(
        @field:SerializedName("result")
        val result: SendResult,

        @field:SerializedName("value")
        val value: SendResultBody
)

data class SendResult(
        @field:SerializedName("status")
        val status: Boolean,
        @field:SerializedName("err_msg")
        var errMsg: String
)

data class SendResultBody(
        @field:SerializedName("error_list")
        val errList: List<ErrListModel>?
)

data class ErrListModel (
        @field:SerializedName("remont_id")
        val remontID: Int,
        @field:SerializedName("error_text")
        var errorText: String
)
