package kz.cheesenology.smartremontmobile.model

import com.google.gson.annotations.SerializedName

data class DefaultResponseModel(

        @field:SerializedName("result")
        val result: Result,

        @field:SerializedName("value")
        val value: String
)

data class DefaultResult(
        @field:SerializedName("status")
        val status: Boolean,
        @field:SerializedName("err_msg")
        var errMsg: String
)
