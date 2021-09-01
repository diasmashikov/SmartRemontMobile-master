package kz.cheesenology.smartremontmobile.data.remontroom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface RemontRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remontRoomList: ArrayList<RemontRoomEntity>)

    @Query("""
        DELETE FROM remont_room_tab
    """)
    fun delete()
}