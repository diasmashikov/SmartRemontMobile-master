package kz.cheesenology.smartremontmobile.data.rooms

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<RoomEntity>)

    @Query("""
        SELECT * FROM room_tab
    """)
    fun getRoomList(): Maybe<List<RoomEntity>>

    @Query("""
        SELECT t.room_id, t1.room_name, t1.room_code, t1.order_num, t1.is_fictive
        FROM remont_room_tab t
        LEFT JOIN room_tab t1 ON t.room_id = t1.room_id
        WHERE t.remont_id = :remontID
    """)
    fun getRoomListByRemontID(remontID: Int): Maybe<List<RoomEntity>>

    @Query("""
        DELETE FROM room_tab
    """)
    fun delete()

    @Query("""
        SELECT count(1) room_cnt FROM room_tab LIMIT 1
    """)
    fun getCnt() : RoomCnt
}