package kz.cheesenology.smartremontmobile.data.rating.remont

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RatingRemontDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(reportTypeList: ArrayList<RatingRemontEntity>)

    @Query("""DELETE FROM rating_remont_tab""")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: RatingRemontEntity)

}