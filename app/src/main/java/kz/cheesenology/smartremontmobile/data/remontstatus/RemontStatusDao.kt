package kz.cheesenology.smartremontmobile.data.remontstatus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemontStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remontStatusList: ArrayList<RemontStatusEntity>)

    @Query("""
        DELETE FROM remont_status_tab
    """)
    fun delete()

}