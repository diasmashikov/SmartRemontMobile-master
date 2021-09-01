package kz.cheesenology.smartremontmobile.data.check

import androidx.room.ColumnInfo

data class SendStatsModel(
        @ColumnInfo(name = "photo_cnt") var photoCnt: Int,
        @ColumnInfo(name = "remont_list_cnt") var remontListCnt: Int
)