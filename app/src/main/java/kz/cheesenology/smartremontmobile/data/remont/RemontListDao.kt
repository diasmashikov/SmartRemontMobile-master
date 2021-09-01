package kz.cheesenology.smartremontmobile.data.remont

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single
import kz.cheesenology.smartremontmobile.model.RemontListDBModel
import java.util.*

@Dao
interface RemontListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remontList: ArrayList<RemontListEntity>)

    @Query(
        """
        SELECT t.*, t1.status_name as stage_status_name, t2.rating_remont_id,
         (select count(a1.defect_id) from user_media_tab a1 WHERE t.remont_id = a1.remont_id AND a1.defect_status = 0) as defect_cnt,
         (select count(a2.defect_id) from user_media_tab a2 WHERE t.remont_id = a2.remont_id AND a2.defect_status = 1) as defect_accept_cnt
        FROM remont_list_tab t
        LEFT JOIN stage_status_tab t1 ON t.stage_status_id = t1.stage_status_id
        LEFT JOIN rating_remont_tab t2 ON t.remont_id = t2.remont_id
        WHERE t.okk_status IN (:okk) 
        AND t.active_stage_id IN (:stage) AND t.remont_status_id IN (:remont) AND t.stage_status_id IN (:stageStatus)
        GROUP BY t.remont_id
        ORDER BY t.remont_id DESC
    """
    )
    fun getList(
        remont: MutableList<Int>,
        stage: MutableList<Int>,
        okk: MutableList<Int>,
        stageStatus: MutableList<Int>
    ): Single<List<RemontListDBModel>>



    @Query(
        """
        SELECT * FROM remont_list_tab WHERE remont_status_id in (1,2,3,4) AND okk_status IN (:list) ORDER BY remont_id DESC
    """
    )
    fun getListWithRemontFinish(list: MutableList<Int>): Flowable<List<RemontListEntity>>

    @Query(
        """
        UPDATE remont_list_tab SET okk_status = :isAccept, is_okk_status_change = 1, is_stage_for_send = 1 WHERE remont_id = :remontId
    """
    )
    fun updateRemontStatus(remontId: Int?, isAccept: Int)

    @Query(
        """
        UPDATE remont_list_tab SET stage_status_id = :statusID, stage_status_comment = :statusComment, is_stage_for_send = 1, stage_status_desc =:descCode
        WHERE remont_id = :remontID
    """
    )
    fun updateRemontStageStatus(
        remontID: Int,
        statusID: Int,
        statusComment: String,
        descCode: String
    )

    @Query(
        """
        DELETE FROM remont_list_tab
    """
    )
    fun delete()

    @Query(
        """
        SELECT * FROM remont_list_tab WHERE is_stage_for_send = 1 AND remont_id IN (:remontListID)
    """
    )
    fun getRemontListDataForSend(remontListID: List<Int>): Single<List<RemontListEntity>>

    @Query(
        """
        UPDATE remont_list_tab SET send_status = 2, error_text = :errorText WHERE remont_id = :remontID
    """
    )
    fun updateOnError(remontID: Int, errorText: String)

    @Query(
        """
        UPDATE remont_list_tab SET is_stage_for_send = 0, is_okk_status_change = 0, send_status = 1 WHERE is_stage_for_send = 1 AND remont_id IN (:remontListID)
    """
    )
    fun updateSendedData(remontListID: List<Int>)

    @Query(
        """
        UPDATE remont_list_tab SET send_status = 1 WHERE is_stage_for_send = 1 AND remont_id IN (:remontListID)
    """
    )
    fun setSuccessSendStatus(remontListID: List<Int>)

    @Query("""Select remont_id from remont_list_tab""")
    fun getFullListID(): List<Int>

    @Query("""UPDATE remont_list_tab SET okk_photo_report_send_date = :date_send WHERE remont_id = :remontID""")
    fun updatePhotoReportSendDate(date_send: String, remontID: Int)
}