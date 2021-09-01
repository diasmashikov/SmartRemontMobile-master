package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class RequestSendCntModel(
    @ColumnInfo(name = "draft_check_cnt") var draft_check_cnt: Int,
    @ColumnInfo(name = "check_list_photo_cnt") var check_list_photo_cnt: Int,
    @ColumnInfo(name = "check_list_cnt") var check_list_cnt: Int
)