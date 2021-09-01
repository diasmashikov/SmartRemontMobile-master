package kz.cheesenology.smartremontmobile.data.stagestatushist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import kz.cheesenology.smartremontmobile.model.StageStatusHistoryListModel
import java.util.*

@Dao
interface StageStatusHistoryDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(stageStatusHistList: ArrayList<StageStatusHistoryEntity>)

    @Query("""
        DELETE FROM user_media_tab
    """)
    fun delete()

    @Query("""
        SELECT t.*, t1.stage_name, t2.status_name as stage_status_name
        FROM stage_status_hist_tab t
        LEFT JOIN stage_tab t1 ON t.stage_id = t1.stage_id
        LEFT JOIN stage_status_tab t2 ON t.stage_status_id = t2.stage_status_id
        WHERE t.remont_id = :remontID""")
    fun getStageHistoryByRemontID(remontID: Int) : Flowable<List<StageStatusHistoryListModel>>
}