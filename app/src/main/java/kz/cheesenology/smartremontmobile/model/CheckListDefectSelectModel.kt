package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class CheckListDefectSelectModel (
        @ColumnInfo(name = "check_list_id") var checkListID: Int?,
        @ColumnInfo(name = "check_list_pid") var checkListPID: Int?,
        @ColumnInfo(name = "stage_id") var stageID: Int,
        @ColumnInfo(name = "check_name") var checkName: String,
        @ColumnInfo(name = "norm") var norm: String?,
        @ColumnInfo(name = "is_room") var isRoom: Int,
        @ColumnInfo(name = "parent_check_name") var parentCheckName: String
) {
    override fun toString(): String {
        return """$parentCheckName - $checkName"""
    }
}