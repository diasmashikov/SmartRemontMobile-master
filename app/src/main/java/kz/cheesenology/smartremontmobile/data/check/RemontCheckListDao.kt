package kz.cheesenology.smartremontmobile.data.check

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import kz.cheesenology.smartremontmobile.data.check.list.CheckListEntity
import kz.cheesenology.smartremontmobile.model.AcceptUnstageListModel
import kz.cheesenology.smartremontmobile.model.CheckListDefectSelectModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModel
import kz.cheesenology.smartremontmobile.model.expand.CheckListChildModelNew
import java.util.*

@Dao
interface RemontCheckListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remontCheckList: ArrayList<RemontCheckListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(remontCheckListEntity: RemontCheckListEntity): Long

    @Query(
        """
        SELECT * FROM remont_check_list_tab
        WHERE remont_check_list_id = :remontCheckListID
    """
    )
    fun getInfoByID(remontCheckListID: Int): Maybe<RemontCheckListEntity>

    @Query(
        """
        SELECT * FROM remont_check_list_tab WHERE audio_info IS NOT NULL
    """
    )
    fun getAudio(): Maybe<List<RemontCheckListEntity>>

    @Query(
        """
        UPDATE remont_check_list_tab SET is_accepted = :checkStatus, defect_cnt = null, description = null, is_for_send = 1
        WHERE remont_check_list_id = :remontCheckListID
    """
    )
    fun updateRemontStatus(remontCheckListID: Int?, checkStatus: Int)

    @Query(
        """
        UPDATE remont_check_list_tab SET is_accepted = :checkStatus, defect_cnt = :defectCnt, description = :defectTxt, is_for_send = 1
        WHERE remont_check_list_id = :remontCheckListID
    """
    )
    fun updateRemontStatusWithDefect(
        remontCheckListID: Int?,
        checkStatus: Int,
        defectCnt: Int,
        defectTxt: String
    )

    @Query(
        """
        UPDATE remont_check_list_tab SET audio_info = :absolutePath, audio_name = :name, is_audio_for_send = 1
        WHERE remont_check_list_id = :remontCheckListID
    """
    )
    fun updateAudio(remontCheckListID: Int?, absolutePath: String, name: String)

    @Query(
        """
        UPDATE remont_check_list_tab SET audio_info = null, audio_name = null, is_audio_for_send = 0
        WHERE remont_check_list_id = :remontCheckListID
    """
    )
    fun deleteAudio(remontCheckListID: Int?)

    @Query(
        """
        SELECT t1.photo_cnt, t2.remont_list_cnt
        FROM
        (
            SELECT count(1) photo_cnt FROM user_media_tab WHERE is_for_send = 1 AND check_list_id IS NOT NULL AND remont_id IN (:remontListID)
        )t1,
        (
            SELECT count(1) remont_list_cnt FROM remont_list_tab WHERE is_stage_for_send = 1 AND remont_id IN (:remontListID)
        )t2
    """
    )
    fun getStatsInfo(remontListID: List<Int>): Maybe<SendStatsModel>

    @Query(
        """
        SELECT t1.photo_cnt, t2.remont_list_cnt
        FROM
        (
            SELECT count(1) photo_cnt FROM user_media_tab WHERE is_for_send = 1
        )t1,
        (
            SELECT count(1) remont_list_cnt FROM remont_list_tab WHERE is_stage_for_send = 1
        )t2
    """
    )
    fun getStatsInfoModel(): SendStatsModel

    @Query(
        """
        SELECT *
        FROM check_list_tab
        WHERE check_list_pid IS NULL
        AND stage_id = :activeStageID
        AND is_active = 1
        ORDER BY check_list_id
    """
    )
    fun getHeaderCheckList(activeStageID: Int): Single<List<CheckListEntity>>

    @Query(
        """
        SELECT t.check_list_id,
        t.check_list_pid,
         t.check_name,
          t.norm,
           t1.remont_check_list_id,
            t1.remont_id,
             t1.audio_info,
              t1.audio_name,
               COUNT(t2.defect_id) as defect_cnt,
                t1.is_accepted,
                 t1.is_audio_for_send,
                  t1.is_for_send
        FROM check_list_tab t
        LEFT JOIN remont_check_list_tab t1 ON t.check_list_id = t1.check_list_id AND t1.remont_id = :remontID
        LEFT JOIN user_media_tab t2 ON t.check_list_id = t2.check_list_id AND t1.remont_id = :remontID
        WHERE t.check_list_pid = :checkListPID
        GROUP BY t.check_list_id
        ORDER BY t.check_list_id
    """
    )
    fun getCheckListByHeader(checkListPID: Int, remontID: Int): List<CheckListChildModel>

    @Query(
        """
        SELECT t.check_list_id,
        t.check_list_pid,
         t.check_name,
          t.norm,
            COUNT(t2.defect_id) as defect_cnt
        FROM check_list_tab t
        LEFT JOIN user_media_tab t2 ON t.check_list_id = t2.check_list_id AND t2.remont_id = :remontID
        WHERE t.check_list_pid = :checkListPID AND t.is_active = 1
        GROUP BY t.check_list_id
        ORDER BY t.check_list_id
    """
    )
    fun getCheckListByHeaderNew(checkListPID: Int, remontID: Int): List<CheckListChildModelNew>


    @Query(
        """
        DELETE FROM remont_check_list_tab
    """
    )
    fun delete()

    @Query(
        """
         SELECT            :remontID as remont_id,
                           t.check_list_id as check_list_id,
                           t1.room_id  as room_id,
                           'Автопринятие' as description,
                           1 as is_accepted
                    FROM check_list_tab t,
                         remont_room_tab t1
                    WHERE t.is_room = 1
                      AND t.stage_id = :activeStageID
                      AND t1.remont_id = :remontID
                      AND ('ch' || t.check_list_id || '@' || t1.room_id) NOT IN
                        ( SELECT 'ch' || a.check_list_id || '@' || a.room_id
                         FROM remont_check_list_tab a,
                              check_list_tab b
                         WHERE a.remont_id = :remontID
                           AND a.check_list_id = b.check_list_id
                           AND b.is_room = 1
                           AND b.stage_id = :activeStageID )
                    UNION
                    SELECT :remontID as remont_id,
                           t.check_list_id as check_list_id,
                           -1  as room_id,
                           'Автопринятие' as description,
                           1  as is_accepted
                    FROM check_list_tab t
                    WHERE t.is_room = 0
                      AND t.stage_id = :activeStageID
                      AND t.check_list_id NOT IN
                        ( SELECT a.check_list_id as check_list_id
                         FROM remont_check_list_tab a,
                              check_list_tab b
                         WHERE a.remont_id = :remontID
                           AND a.check_list_id = b.check_list_id
                           AND b.is_room = 0
                           AND b.stage_id = :activeStageID)
    """
    )
    fun getAcceptUnstageList(remontID: Int, activeStageID: Int): Maybe<List<AcceptUnstageListModel>>

    @Query(
        """
        UPDATE remont_check_list_tab SET is_accepted = 1 WHERE is_accepted IS NULL
    """
    )
    fun upadateAllNullStage()

    @Query(
        """
        SELECT * FROM remont_check_list_tab WHERE is_for_send = 1
    """
    )
    fun getCheckListDataForSend(): Maybe<List<RemontCheckListEntity>>

    @Query(
        """
        UPDATE remont_check_list_tab SET is_for_send = 0, is_audio_for_send = 0 WHERE is_for_send = 1 OR is_audio_for_send = 1
    """
    )
    fun updateSendedData()

    @Query(
        """
        SELECT * FROM remont_check_list_tab WHERE is_audio_for_send = 1
    """
    )
    fun getAudioForSend(): Maybe<List<RemontCheckListEntity>>

    @Query(
        """
        SELECT planirovka_image_url FROM remont_list_tab WHERE remont_id = :remont_id
    """
    )
    fun getPlanirovkaImage(remont_id: Int): Single<String>

    @Query(
        """
        SELECT t.*, t2.check_name as parent_check_name FROM check_list_tab t 
        LEFT JOIN remont_check_list_tab t1 ON t.check_list_id = t1.check_list_id AND t1.remont_id = :remontID
        LEFT JOIN check_list_tab t2 ON t.check_list_pid = t2.check_list_id
        WHERE t.stage_id = :activeStageID AND t.check_list_pid IS NOT NULL 
        AND t.is_active = 1
        GROUP BY t.check_list_id
    """
    )
    fun getCheckListByRoomForDefects(
        activeStageID: Int,
        remontID: Int
    ): Flowable<List<CheckListDefectSelectModel>>

    @Query(
        """
        UPDATE remont_check_list_tab SET is_accepted = :checkStatus, defect_cnt = 1, is_for_send = 1, audio_name = :audioFileName
        WHERE check_list_id = :checkListID AND room_id = :roomID AND remont_id = :remontID
    """
    )
    fun updateRemontStatusWithNoID(
        checkListID: Int?,
        roomID: Int?,
        checkStatus: Int,
        remontID: Int,
        audioFileName: String?
    )

    @Query("""SELECT * FROM remont_check_list_tab WHERE check_list_id = :checkListID AND remont_id = :remontID""")
    fun isCheckListExist(checkListID: Int?, remontID: Int): RemontCheckListEntity?

    /*@Query("""
        UPremonDATE
    """)
    fun updateRemontStageStatus(remontID: Int, statusID: Int, statusComment: String)*/
}