package kz.cheesenology.smartremontmobile.data.check.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_list_history_tab")
data class CheckListHistoryEntity (
        @PrimaryKey
        @ColumnInfo(name = "remont_check_list_hist_id") var historyID: Int,
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "check_list_id") var checkListID: Int,
        @ColumnInfo(name = "room_id") var roomID: Int,
        @ColumnInfo(name = "defect_cnt") var defectCnt: Int?,
        @ColumnInfo(name = "is_accepted") var isAccepted: Int?,
        @ColumnInfo(name = "description") var description: String?,
        @ColumnInfo(name = "date_create") var dateCreate: String?
)