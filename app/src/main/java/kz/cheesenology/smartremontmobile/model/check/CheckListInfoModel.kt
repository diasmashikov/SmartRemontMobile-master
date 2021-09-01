package kz.cheesenology.smartremontmobile.model.check

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CheckListInfoModel(

        @field:SerializedName("result")
        val result: Result? = null,

        @field:SerializedName("value")
        val value: Value? = null
)

data class Result(

        @field:SerializedName("status")
        val status: Boolean? = null,
        @field:SerializedName("err_msg")
        var errMsg: String
)

data class Value(

        @field:SerializedName("standart")
        val standart: List<Standart>,

        @field:SerializedName("history")
        val history: List<HistoryItem>,

        @field:SerializedName("photos")
        val photos: List<PhotoUserList>,

        @field:SerializedName("info")
        val info: InfoCheck?
)

data class PhotoUserList(
        @field:SerializedName("defect_id")
        val remontCheckListPhotoID: Int? = null,
        @field:SerializedName("remont_check_list_id")
        val remontCheckListID: Int?,
        @field:SerializedName("photo_url")
        val photoUrl: String? = null,
        @field:SerializedName("date_create")
        val dateCreate: String? = null,
        @field:SerializedName("employee_id")
        val employeeID: Int? = null,
        @field:SerializedName("photo_title")
        val photoTitle: String? = null
)

data class InfoCheck(
        @field:SerializedName("remont_check_list_id")
        val remontCheckListID: Int? = null,
        @field:SerializedName("remont_id")
        val remontID: Int? = null,
        @field:SerializedName("check_list_id")
        val checkListID: Int? = null,
        @field:SerializedName("room_id")
        val roomID: Int? = null,
        @field:SerializedName("is_accepted")
        val isAccepted: Int? = null,
        @field:SerializedName("description")
        val description: String? = null,
        @field:SerializedName("defect_cnt")
        val defectCnt: Int? = null,
        @field:SerializedName("audio_info")
        val audioInfo: String? = null,
        @field:SerializedName("date_create")
        val dateCreate: String? = null,
        @field:SerializedName("employee_id")
        val employeeID: Int? = null
)

data class Standart(
        @SerializedName("check_list_standart_id")
        @Expose
        val checkListStandartId: Int,
        @SerializedName("check_list_id")
        @Expose
        val checkListId: Int,
        @SerializedName("photo_url")
        @Expose
        val photoUrl: String,
        @SerializedName("photo_comment")
        @Expose
        val photoComment: String,
        @SerializedName("title")
        @Expose
        val title: String,
        @SerializedName("is_good")
        @Expose
        val isGood: Int,
        @SerializedName("rowversion")
        @Expose
        val rowversion: String
)

data class HistoryItem(

        @field:SerializedName("remont_check_list_id")
        val remontCheckListId: Int? = null,

        @field:SerializedName("room_name")
        val roomName: String? = null,

        @field:SerializedName("is_accepted")
        val isAccepted: Int? = null,

        @field:SerializedName("remont_check_list_hist_id")
        val remontCheckListHistId: Int? = null,

        @field:SerializedName("date_create")
        val dateCreate: String? = null,

        @field:SerializedName("defect_cnt")
        val defectCnt: Int? = null,

        @field:SerializedName("employee_id")
        val employeeId: Int? = null,

        @field:SerializedName("description")
        val description: String? = null
)