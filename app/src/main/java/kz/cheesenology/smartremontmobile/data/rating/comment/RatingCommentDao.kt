package kz.cheesenology.smartremontmobile.data.rating.comment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface RatingCommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ratingCommentList: ArrayList<RatingCommentEntity>)

    @Query("""DELETE FROM rating_comment_tab""")
    fun delete()

}