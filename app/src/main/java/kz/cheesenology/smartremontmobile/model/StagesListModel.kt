package kz.cheesenology.smartremontmobile.model

import com.google.gson.annotations.SerializedName

data class StagesListModel(

        @field:SerializedName("result")
        val result: StagesResult,

        @field:SerializedName("stages")
        val stages: List<StagesItem>,

        @field:SerializedName("rooms")
        val rooms: List<Rooms>,

        @field:SerializedName("value")
        val value: List<StagesValueItem>
)

data class Rooms(
        @field:SerializedName("room_id")
        val room_id: Int? = null,
        @field:SerializedName("room_name")
        val room_name: String? = null,
        @field:SerializedName("room_code")
        val room_code: String? = null,
        @field:SerializedName("order_num")
        val order_num: Int? = null,
        @field:SerializedName("is_fictive")
        val is_fictive: Int? = null
) {
    override fun toString(): String {
        return room_name!!
    }
}

data class StagesValueItem(

        @field:SerializedName("cnt_standart")
        val cntStandart: String? = null,

        @field:SerializedName("is_active")
        val isActive: Int? = null,

        @field:SerializedName("is_active_text")
        val isActiveText: String? = null,

        @field:SerializedName("level")
        val level: Int? = null,

        @field:SerializedName("stage_id")
        val stageId: Int? = null,

        @field:SerializedName("is_room_text")
        val isRoomText: String? = null,

        @field:SerializedName("defect_cnt")
        val defectCnt: Any? = null,

        @field:SerializedName("cnt")
        val cnt: String? = null,

        @field:SerializedName("is_room")
        val isRoom: Int? = null,

        @field:SerializedName("is_accept")
        val isAccept: Int? = null,

        @field:SerializedName("norm")
        val norm: Any? = null,

        @field:SerializedName("stage_name")
        val stageName: String? = null,

        @field:SerializedName("check_list_pid")
        val checkListPid: Any? = null,

        @field:SerializedName("check_name")
        val checkName: String? = null,

        @field:SerializedName("is_complete")
        val isComplete: Int? = null,

        @field:SerializedName("check_list_id")
        val checkListId: Int? = null,

        @field:SerializedName("child")
        val child: List<ChildItem?>? = null
)

data class StagesItem(

        @field:SerializedName("is_active")
        val isActive: Int? = null,

        @field:SerializedName("stage_id")
        val stageId: Int? = null,

        @field:SerializedName("stage_name")
        val stageName: String? = null,

        @field:SerializedName("stage_code")
        val stageCode: String? = null,

        @field:SerializedName("stage_order_num")
        val stageOrderNum: Int? = null,

        @field:SerializedName("active_stage_id")
        val activeStageId: Int? = null


) {
    override fun toString(): String {
        return stageName.toString()
    }
}

data class StagesResult(
        @field:SerializedName("status")
        val status: Boolean,
        @field:SerializedName("err_msg")
        val errMsg: String
)

data class ChildItem(
        @field:SerializedName("cnt_standart")
        val cntStandart: String? = null,

        @field:SerializedName("is_active")
        val isActive: Int? = null,

        @field:SerializedName("is_active_text")
        val isActiveText: String? = null,

        @field:SerializedName("level")
        var level: Int? = null,

        @field:SerializedName("stage_id")
        var stageId: Int? = null,

        @field:SerializedName("is_room_text")
        var isRoomText: String? = null,

        @field:SerializedName("defect_cnt")
        var defectCnt: Int? = null,

        @field:SerializedName("cnt")
        var cnt: String? = null,

        @field:SerializedName("is_room")
        var isRoom: Int? = null,

        @field:SerializedName("is_accept")
        var isAccept: Int? = null,

        @field:SerializedName("norm")
        var norm: Any? = null,

        @field:SerializedName("stage_name")
        var stageName: String? = null,

        @field:SerializedName("check_list_pid")
        var checkListPid: Int? = null,

        @field:SerializedName("check_name")
        var checkName: String? = null,

        @field:SerializedName("is_complete")
        var isComplete: Int? = null,

        @field:SerializedName("check_list_id")
        var checkListId: Int? = null
)