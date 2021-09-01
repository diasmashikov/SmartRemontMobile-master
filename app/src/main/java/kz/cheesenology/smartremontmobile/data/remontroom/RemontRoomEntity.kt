package kz.cheesenology.smartremontmobile.data.remontroom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remont_room_tab")
data class RemontRoomEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "remont_room_id") var remontRoomID: Int? = null,
        @ColumnInfo(name = "remont_id") var remontID: Int?,
        @ColumnInfo(name = "room_id") var roomID: Int?
)