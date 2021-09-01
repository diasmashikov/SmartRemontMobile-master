package kz.cheesenology.smartremontmobile.model.expand

import androidx.room.ColumnInfo

data class CheckListChildModelNew(
        @ColumnInfo(name = "check_list_id") var checkListID: Int,
        @ColumnInfo(name = "check_list_pid") var checkListPID: Int?,
        @ColumnInfo(name = "check_name") var checkName: String?,
        @ColumnInfo(name = "norm") var norm: String?,
        @ColumnInfo(name = "defect_cnt") var defectCnt: Int?,
        @ColumnInfo(name = "is_accepted") var isAccepted: Int?
)