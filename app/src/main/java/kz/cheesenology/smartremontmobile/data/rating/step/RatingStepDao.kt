package kz.cheesenology.smartremontmobile.data.rating.step

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kz.cheesenology.smartremontmobile.model.ratings.RatingStepListModel

@Dao
interface RatingStepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(reportTypeList: ArrayList<RatingStepEntity>)

    @Query(
        """
        SELECT t.*, t1.rating_remont_id
        FROM rating_step_tab t 
        LEFT JOIN rating_remont_tab t1 ON t.rating_step_id = t1.step_id AND t1.remont_id = :remontID
        WHERE t.rating_detail_id = :ratingDetailID 
        ORDER BY t.step_order ASC
        """
    )
    fun getStepByDetailID(ratingDetailID: Int, remontID: Int): MutableList<RatingStepListModel>

}