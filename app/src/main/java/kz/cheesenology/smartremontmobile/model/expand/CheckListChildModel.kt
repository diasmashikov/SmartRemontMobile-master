package kz.cheesenology.smartremontmobile.model.expand

import androidx.room.ColumnInfo

data class CheckListChildModel(
        @ColumnInfo(name = "check_list_id") var checkListID: Int,
        @ColumnInfo(name = "check_list_pid") var checkListPID: Int?,
        @ColumnInfo(name = "check_name") var checkName: String?,
        @ColumnInfo(name = "norm") var norm: String?,
        @ColumnInfo(name = "remont_check_list_id") var remontCheckListID: Int?,
        @ColumnInfo(name = "remont_id") var remontID: Int?,
        @ColumnInfo(name = "audio_info") var audioInfo: String?,
        @ColumnInfo(name = "audio_name") var audioName: String?,
        @ColumnInfo(name = "defect_cnt") var defectCnt: Int?,
        @ColumnInfo(name = "is_accepted") var isAccepted: Int?,
        @ColumnInfo(name = "is_audio_for_send") var isAudioForSend: Int?,
        @ColumnInfo(name = "is_for_send") var isForSend: Int?
)