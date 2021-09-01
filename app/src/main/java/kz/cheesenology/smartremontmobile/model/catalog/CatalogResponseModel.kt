package kz.cheesenology.smartremontmobile.model.catalog

import com.google.gson.annotations.SerializedName

data class CatalogResponseModel(
    @field:SerializedName("result")
    val result: Result,
    @field:SerializedName("value")
    val value: Value
)

data class Result(
    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("err_msg")
    var errMsg: String
)

data class Value(

    @field:SerializedName("check_list")
    val checkList: List<CheckListItem>? = null,

    @field:SerializedName("standart_list")
    val standartList: List<StandartListItem>? = null,

    @field:SerializedName("stage_list")
    val stageList: List<StageListItem>? = null,

    @field:SerializedName("remont_status_list")
    val remontStatusList: List<RemontStatusListItem>? = null,

    @field:SerializedName("stage_status_list")
    val stageStatusList: List<StageStatusListItem>? = null,

    @field:SerializedName("room_list")
    val roomList: List<RoomListItem>? = null,

    @field:SerializedName("group_chat_list")
    val groupChatList: List<GroupChatListItem>? = null,

    @field:SerializedName("report_type_list")
    val reportTypeList: List<ReportTypeList>? = null
)

data class ReportTypeList(
    @field:SerializedName("report_type_id") var reportTypeID: Int? = null,
    @field:SerializedName("report_type_code") var reportTypeCode: String? = null,
    @field:SerializedName("report_type_name") var reportTypeName: String? = null
)

data class GroupChatListItem(
    @field:SerializedName("group_chat_id") var groupChatID: Int? = null,
    @field:SerializedName("group_chat_name") var groupChatName: String? = null,
    @field:SerializedName("group_chat_code") var groupChatCode: String? = null,
    @field:SerializedName("group_chat_order_num") var groupChatOrderNum: Int? = null,
    @field:SerializedName("group_chat_short_name") var groupChatShortName: String? = null,
    @field:SerializedName("stage_id") var stageID: Int? = null
)

data class CheckListItem(

    @field:SerializedName("is_active")
    val isActive: Int? = null,

    @field:SerializedName("stage_id")
    val stageId: Int? = null,

    @field:SerializedName("check_list_pid")
    val checkListPid: Int? = null,

    @field:SerializedName("check_name")
    val checkName: String? = null,

    @field:SerializedName("is_room")
    val isRoom: Int? = null,

    @field:SerializedName("check_list_id")
    val checkListId: Int? = null,

    @field:SerializedName("norm")
    val norm: String? = null
)

data class RemontStatusListItem(

    @field:SerializedName("remont_status_id")
    val remontStatusId: Int? = null,

    @field:SerializedName("status_code")
    val statusCode: String? = null,

    @field:SerializedName("status_name")
    val statusName: String? = null
)


data class RoomListItem(

    @field:SerializedName("room_id")
    val roomId: Int,

    @field:SerializedName("room_name")
    val roomName: String? = null,

    @field:SerializedName("is_fictive")
    val isFictive: Int? = null,

    @field:SerializedName("room_code")
    val roomCode: String? = null,

    @field:SerializedName("order_num")
    val orderNum: Int? = null
)

data class StageListItem(

    @field:SerializedName("stage_short_name")
    val stageShortName: String? = null,

    @field:SerializedName("stage_id")
    val stageId: Int? = null,

    @field:SerializedName("stage_name")
    val stageName: String? = null,

    @field:SerializedName("stage_code")
    val stageCode: String? = null,

    @field:SerializedName("stage_order_num")
    val stageOrderNum: Int? = null
)

data class StageStatusListItem(

    @field:SerializedName("stage_status_id")
    val stageStatusId: Int? = null,

    @field:SerializedName("status_code")
    val statusCode: String? = null,

    @field:SerializedName("what")
    val what: String? = null,

    @field:SerializedName("status_name")
    val statusName: String? = null
)

data class StandartListItem(

    @field:SerializedName("photo_comment")
    val photoComment: String? = null,

    @field:SerializedName("is_good")
    val isGood: Int? = null,

    @field:SerializedName("check_list_standart_id")
    val checkListStandartId: Int? = null,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("photo_name")
    val photoName: String? = null,

    @field:SerializedName("check_list_id")
    val checkListId: Int? = null,

    @field:SerializedName("rowversion")
    val rowversion: String? = null
)