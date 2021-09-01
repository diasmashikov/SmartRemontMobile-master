package kz.cheesenology.smartremontmobile.data.check.list

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CheckListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(checkList: ArrayList<CheckListEntity>)

    @Query("""
        DELETE FROM check_list_tab
    """)
    fun delete()
}