package kz.cheesenology.smartremontmobile.model.fulllist

import com.google.gson.annotations.SerializedName

data class FullListDataModel(
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

    @field:SerializedName("photo_list")
    val photoList: List<PhotoListItem>? = null,

    @field:SerializedName("check_list_his")
    val checkListHis: List<CheckListHisItem>? = null,

    @field:SerializedName("remont_list")
    val remontList: List<RemontListItem>? = null,

    @field:SerializedName("check_list")
    val checkList: List<CheckListItem>? = null,

    @field:SerializedName("remont_room_list")
    val remontRoomList: List<RemontRoomList>? = null,

    @field:SerializedName("defect_list")
    val defectList: List<DefectListItem>? = null,

    @field:SerializedName("stage_chat_list")
    val stageChatList: List<StageChatItem>? = null,

    @field:SerializedName("chat_list")
    val groupChatList: List<GroupChatMessageItem>? = null,

    @field:SerializedName("stage_chat_file_list")
    val stageChatFileList: List<StageChatFileItem>? = null,

    @field:SerializedName("remont_stage_his_list")
    val stageStatusHistList: List<StageStatusHistoryItem>? = null,

    @field:SerializedName("rt_detail_list")
    val ratingDetailList: List<RatingDetailItem>? = null,

    @field:SerializedName("rt_step_list")
    val ratingStepList: List<RatingStepItem>? = null,

    @field:SerializedName("rt_remont_list")
    val ratingRemontList: List<RatingRemontItem>? = null,

    @field:SerializedName("rt_remont_comment_list")
    val ratingCommentList: List<RatingCommentItem>? = null
)

data class RatingStepItem(
    @field:SerializedName("rt_step_id") var rt_step_id: Int? = null,
    @field:SerializedName("rt_detail_id") var rt_detail_id: Int? = null,
    @field:SerializedName("rt_step_name") var rt_step_name: String? = null,
    @field:SerializedName("rt_step_order") var rt_step_order: Int? = null
)

data class RatingRemontItem(
    @field:SerializedName("rt_remont_id") var rt_remont_id: Int? = null,
    @field:SerializedName("remont_id") var remont_id: Int? = null,
    @field:SerializedName("rt_step_id") var rt_step_id: Int? = null,
    @field:SerializedName("contractor_id") var contractor_id: Int? = null,
    @field:SerializedName("is_edit") var is_edit: Boolean? = null
)

data class RatingCommentItem(
    @field:SerializedName("rt_remont_comment_id") var rt_remont_comment_id: Int? = null,
    @field:SerializedName("remont_id") var remont_id: Int? = null,
    @field:SerializedName("rt_role_id") var rt_role_id: Int? = null,
    @field:SerializedName("comments") var comments: String? = null
)

data class RatingDetailItem(
    @field:SerializedName("rt_detail_id") var rt_detail_id: Int? = null,
    @field:SerializedName("rt_role_id") var rt_role_id: Int? = null,
    @field:SerializedName("rt_detail_name") var rt_detail_name: String? = null,
    @field:SerializedName("rt_detail_code") var rt_detail_code: String? = null,
    @field:SerializedName("rt_detail_weight") var rt_detail_weight: Int? = null
)

data class GroupChatMessageItem(
    @field:SerializedName("stage_chat_id") val stage_chat_id: Int,
    @field:SerializedName("group_chat_id") val group_chat_id: Int?,
    @field:SerializedName("remont_id") val remont_id: Int?,
    @field:SerializedName("employee_id") val employee_id: Int?,
    @field:SerializedName("client_id") val client_id: Int?,
    @field:SerializedName("message") val message: String?,
    @field:SerializedName("date_chat") val date_chat: String?,
    @field:SerializedName("chat_fio") val chat_fio: String?
)

data class StageStatusHistoryItem(
    @field:SerializedName("remont_id") val remont_id: Int,
    @field:SerializedName("stage_id") val stage_id: Int,
    @field:SerializedName("stage_status_id") val stage_status_id: Int,
    @field:SerializedName("date_create") val date_create: String?,
    @field:SerializedName("fio") val fio: String?,
    @field:SerializedName("comments") val comments: String? = null,
    @field:SerializedName("remark_name") val remarkName: String? = null
)

data class DefectListItem(
    @field:SerializedName("remont_id") val remont_id: Int,
    @field:SerializedName("check_list_id") val check_list_id: Int,
    @field:SerializedName("is_accepted") val is_accepted: Int? = null,
    @field:SerializedName("file_name") val file_name: String?,
    @field:SerializedName("file_type") val file_type: String?,
    @field:SerializedName("file_url") val file_url: String?,
    @field:SerializedName("comments") val comments: String? = null,
    @field:SerializedName("audio_name") val audio_name: String? = null,
    @field:SerializedName("date_create") val date_create: String? = null,
    @field:SerializedName("audio_url") val audio_url: String? = null,
    @field:SerializedName("stage_id") val stage_id: Int? = null
)

data class StageChatFileItem(
    @field:SerializedName("file_url") val file_url: String,
    @field:SerializedName("file_ext") val file_ext: String,
    @field:SerializedName("file_name") val file_name: String,
    @field:SerializedName("stage_chat_file_id") val stage_chat_file_id: Int,
    @field:SerializedName("stage_chat_id") val stage_chat_id: Int
)

data class StageChatItem(
    @field:SerializedName("date_chat") val date_chat: String,
    @field:SerializedName("employee_name") val employee_name: String,
    @field:SerializedName("message") val message: String,
    @field:SerializedName("remont_id") val remont_id: Int,
    @field:SerializedName("stage_chat_id") val stage_chat_id: Int,
    @field:SerializedName("stage_id") val stage_id: Int
)

data class RemontRoomList(
    @field:SerializedName("remont_id")
    val remontID: Int? = null,

    @field:SerializedName("room_id")
    val roomID: Int? = null
)

data class CheckListHisItem(

    @field:SerializedName("is_accepted")
    val isAccepted: Int? = null,

    @field:SerializedName("room_id")
    val roomID: Int? = null,

    @field:SerializedName("check_list_id")
    val checkListID: Int? = null,

    @field:SerializedName("remont_id")
    val remontID: Int? = null,

    @field:SerializedName("remont_check_list_hist_id")
    val remontCheckListHistId: Int? = null,

    @field:SerializedName("date_create")
    val dateCreate: String? = null,

    @field:SerializedName("defect_cnt")
    val defectCnt: Int? = null,

    @field:SerializedName("description")
    val description: String? = null
)

data class CheckListItem(

    @field:SerializedName("room_id")
    val roomId: Int? = null,

    @field:SerializedName("is_accepted")
    val isAccepted: Int? = null,

    @field:SerializedName("is_active")
    val isActive: Int? = null,

    @field:SerializedName("audio_info")
    val audioInfo: String? = null,

    @field:SerializedName("audio_name")
    val audioName: String? = null,

    @field:SerializedName("stage_id")
    val stageId: Int? = null,

    @field:SerializedName("defect_cnt")
    val defectCnt: Int? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("is_room")
    val isRoom: Int? = null,

    @field:SerializedName("norm")
    val norm: String? = null,

    @field:SerializedName("remont_check_list_id")
    val remontCheckListId: Int? = null,

    @field:SerializedName("check_list_pid")
    val checkListPid: Int? = null,

    @field:SerializedName("remont_id")
    val remontId: Int? = null,

    @field:SerializedName("check_name")
    val checkName: String? = null,

    @field:SerializedName("check_list_id")
    val checkListId: Int? = null
)

data class PhotoListItem(

    @field:SerializedName("room_id")
    val roomID: Int? = null,

    @field:SerializedName("check_list_id")
    val checkListID: Int? = null,

    @field:SerializedName("remont_id")
    val remontID: Int? = null,

    @field:SerializedName("photo_name")
    val photoName: String? = null,

    @field:SerializedName("date_create")
    val dateCreate: String? = null,

    @field:SerializedName("remont_check_list_photo_id")
    val defectID: Int? = null,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null
)

data class RemontListItem(

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

    @field:SerializedName("constuctor_url")
    val constructorUrl: String? = null,

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
    val price: Double? = null,

    @field:SerializedName("remont_id")
    val remontId: Int? = null,

    @field:SerializedName("contractor_status")
    val contractorStatus: Int? = null,

    @field:SerializedName("active_stage_name")
    val activeStageName: String? = null,

    @field:SerializedName("client_name")
    val clientName: String? = null,

    @field:SerializedName("active_stage_id")
    val activeStageId: Int? = null,

    @field:SerializedName("okk_employee_id")
    val okkEmployeeId: Int? = null,

    @field:SerializedName("info")
    val info: String? = null,

    @field:SerializedName("orig_image_url")
    val planirovkaImage: String? = null,

    @field:SerializedName("image_url")
    val planirovkaImageURL: String? = null,

    @field:SerializedName("manager_project_fio")
    val managerFIO: String? = null,

    @field:SerializedName("manager_project_phone")
    val managerPhone: String? = null,

    @field:SerializedName("contractor_phone")
    val contractorPhone: String? = null,

    @field:SerializedName("project_remont_name")
    val projectRemontName: String? = null,

    @field:SerializedName("inner_master_name")
    val internalMaster: String? = null,

    @field:SerializedName("inner_master_phone")
    val internalMasterPhone: String? = null
)
