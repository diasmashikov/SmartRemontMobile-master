package kz.cheesenology.smartremontmobile.data.rooms

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_tab")
data class RoomEntity(
        @PrimaryKey
        @ColumnInfo(name = "room_id") var roomID: Int,
        @ColumnInfo(name = "room_name") var roomName: String?,
        @ColumnInfo(name = "room_code") var roomCode: String?,
        @ColumnInfo(name = "order_num") var orderNum: Int?,
        @ColumnInfo(name = "is_fictive") var isFictive: Int?
) {
    override fun toString(): String {
        return roomName!!
    }
}