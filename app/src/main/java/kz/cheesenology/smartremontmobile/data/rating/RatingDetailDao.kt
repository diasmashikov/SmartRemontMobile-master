package kz.cheesenology.smartremontmobile.data.rating

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import java.util.*

@Dao
interface RatingDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ratingDetailList: ArrayList<RatingDetailEntity>)

    @Query(
        """
        SELECT t.*
        FROM rating_detail_tab t
        ORDER BY t.rating_detail_id ASC
    """
    )
    fun getRatingsList(): Flowable<List<RatingDetailEntity>>
}