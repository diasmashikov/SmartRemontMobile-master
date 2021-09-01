package kz.cheesenology.smartremontmobile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class NotificationListModel(

        @field:SerializedName("result")
        val result: Result,

        @field:SerializedName("value")
        val value: NotificationValue
)

data class NotificationData(
        @SerializedName("title")
        @Expose
        var title: String? = null,
        @SerializedName("detail")
        @Expose
        var detail: String? = null,
        @SerializedName("push_date_time")
        @Expose
        var pushDateTime: String? = null,
        @SerializedName("send_email_user_id")
        @Expose
        var sendEmailUserID: Int? = null
)

class NotificationValue {
        @SerializedName("data")
        @Expose
        var data: List<NotificationData>? = null

}