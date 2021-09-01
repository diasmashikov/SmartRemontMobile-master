package kz.cheesenology.smartremontmobile.data.stagestatus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kz.cheesenology.smartremontmobile.model.StageStatusHistSingleModel

@Dao
interface StageStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(stageStatusList: ArrayList<StageStatusEntity>)

    @Query("""
        DELETE FROM stage_status_tab
    """)
    fun delete()

    @Query("""
        SELECT t.status_name, max(t1.date_create) date_create
        FROM stage_status_tab t
        LEFT JOIN stage_status_hist_tab t1 ON t.stage_status_id = t1.stage_status_id AND t1.remont_id = :remontID AND t1.stage_id = :activeStageID
        WHERE t.stage_status_id = :stageStatusID
        GROUP BY t.stage_status_id
        """)
    fun getStageNameByID(stageStatusID: Int, remontID: Int, activeStageID: Int): StageStatusHistSingleModel
}