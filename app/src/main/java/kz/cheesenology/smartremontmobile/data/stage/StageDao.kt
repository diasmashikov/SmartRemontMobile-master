package kz.cheesenology.smartremontmobile.data.stage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe

@Dao
interface StageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(stageList: List<StageEntity>)

    @Query("""
            SELECT * FROM stage_tab
    """)
    fun getStageList(): Maybe<List<StageEntity>>

    @Query("""
        DELETE FROM stage_tab
    """)
    fun delete()
}