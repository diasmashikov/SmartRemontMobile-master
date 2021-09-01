package kz.cheesenology.smartremontmobile.model

import com.google.gson.annotations.SerializedName

data class ResultAuthModel(
        @SerializedName("result")
        var result: ResultKt,
        @SerializedName("value")
        var value: ValueAuth
)

data class ResultKt(
        @SerializedName("status")
        var status: Boolean,
        @SerializedName("err_msg")
        var errMsg: String
)

data class ValueAuth(
        @SerializedName("employee_id")
        var employeeID: Int,
        @SerializedName("fio")
        var fio: String,
        @SerializedName("position_name")
        var position_name: String,
        @SerializedName("city_name")
        var city_name: String,
        @SerializedName("okk_name")
        var okk_name: String,
        @SerializedName("okk_id")
        var okk_id: Int
)