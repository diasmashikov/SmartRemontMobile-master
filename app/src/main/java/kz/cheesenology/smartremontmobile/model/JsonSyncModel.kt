package kz.cheesenology.smartremontmobile.model

import com.google.gson.annotations.SerializedName

data class JsonSyncModel(
    @field:SerializedName("status") val status: Boolean,
    @field:SerializedName("value") val value: ResponseValue
)

data class ResponseValue(
    @field:SerializedName("error_msg") val error_msg: String? = null,
    /*@field:SerializedName("request_json") val request_json: String,*/
    @field:SerializedName("request_mobile_id") val request_mobile_id: Int,
    @field:SerializedName("response_json") val response_json: Any?,
    @field:SerializedName("status_code") val status_code: String?
)

data class SendRequestAddWorkerModel(
    @field:SerializedName("master_id") val master_id: Int? = null
)