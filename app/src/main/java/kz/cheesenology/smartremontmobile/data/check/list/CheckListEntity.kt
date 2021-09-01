package kz.cheesenology.smartremontmobile.data.check.list

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_list_tab")
data class CheckListEntity (
        @PrimaryKey
        @ColumnInfo(name = "check_list_id") var checkListID: Int?,
        @ColumnInfo(name = "check_list_pid") var checkListPID: Int?,
        @ColumnInfo(name = "stage_id") var stageID: Int,
        @ColumnInfo(name = "check_name") var checkName: String,
        @ColumnInfo(name = "norm") var norm: String?,
        @ColumnInfo(name = "is_room") var isRoom: Int,
        @ColumnInfo(name = "is_active") var isActive: Int
)