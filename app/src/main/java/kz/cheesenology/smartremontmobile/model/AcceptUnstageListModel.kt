package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class AcceptUnstageListModel (
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "check_list_id") var checkListID: Int?,
        @ColumnInfo(name = "room_id") var roomID: Int?,
        @ColumnInfo(name = "description") var description: String?,
        @ColumnInfo(name = "is_accepted") var isAccepted: Int?
)