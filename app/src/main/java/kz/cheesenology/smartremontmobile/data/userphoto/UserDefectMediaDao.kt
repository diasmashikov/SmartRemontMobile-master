package kz.cheesenology.smartremontmobile.data.userphoto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import kz.cheesenology.smartremontmobile.model.DefectListModel
import kz.cheesenology.smartremontmobile.model.PhotoSendCheckModel
import java.util.*

@Dao
interface UserDefectMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userDefectMediaList: ArrayList<UserDefectMediaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userDefectMediaEntity: UserDefectMediaEntity)

    @Query("""SELECT * FROM user_media_tab""")
    fun getShowList(): Flowable<List<UserDefectMediaEntity>>

    @Query("""SELECT * FROM user_media_tab""")
    fun getDownloadList(): Maybe<List<UserDefectMediaEntity>>

    @Query("""
        SELECT t.* FROM user_media_tab t
        WHERE t.check_list_id = :checkListID
        AND t.remont_id = :remontID
    """)
    fun getShowListByID(remontID: Int, checkListID: Int): Flowable<List<UserDefectMediaEntity>>


    @Query("""
        UPDATE user_media_tab SET is_for_send = 1 WHERE defect_id = :defectID
    """)
    fun disablePhoto(defectID: Int?)

    @Query("""
        DELETE FROM user_media_tab
    """)
    fun delete()

    @Query("""
        SELECT * FROM user_media_tab WHERE is_for_send = 1
    """)
    fun getNewPhotoForSend(): Maybe<List<UserDefectMediaEntity>>

    @Query("""
        SELECT t.*, t1.active_stage_id, t2.check_name as check_name, t2.check_list_pid as check_list_pid
        FROM user_media_tab t
        LEFT JOIN remont_tab t1 ON t.remont_id = t1.remont_id
        LEFT JOIN check_list_tab t2 ON t.check_list_id= t2.check_list_id
        WHERE t.is_for_send = 1
    """)
    fun getNewPhotoForSendWithStageID(): Maybe<List<PhotoSendCheckModel>>

    @Query("""
        UPDATE user_media_tab SET is_for_send = 0 WHERE remont_id IN (:remontListID)
    """)
    fun clearSendStatus(remontListID: List<Int>)

    @Query("""
        SELECT t.*, t2.check_name FROM user_media_tab t
        LEFT JOIN check_list_tab t2 ON t.check_list_id= t2.check_list_id
        WHERE t.remont_id = :remontID AND t.stage_id = :activeStageID
    """)
    fun getDefectPhotosByRemont(remontID: Int, activeStageID: Int): Flowable<List<DefectListModel>>

    @Query("""
       UPDATE user_media_tab SET check_list_id = :checkListID WHERE defect_id = :defectID
    """)
    fun setCheckList(defectID: Int?, checkListID: Int?)

    @Query("""UPDATE user_media_tab SET check_list_id = :checkListID, comment = :comment, audio_name = :audioName, is_for_send = 1, defect_status = 0 WHERE defect_id = :defectID""")
    fun setPhotoInfo(defectID: Int, checkListID: Int?, comment: String?, audioName: String?)

    @Query("""UPDATE user_media_tab SET check_list_id = :checkListID, comment = :comment, is_for_send = 1, defect_status = 0 WHERE defect_id = :defectID""")
    fun setPhotoInfoCombine(defectID: Int?, checkListID: Int?, comment: String?)

    @Query("""DELETE FROM user_media_tab WHERE defect_id = :defectID""")
    fun deleteByID(defectID: Int?)

    @Query("""
        SELECT * FROM user_media_tab WHERE is_for_send = 1 AND check_list_id IS NOT NULL AND remont_id IN (:remontListID)
    """)
    fun getDefectsForSend(remontListID: List<Int>): Single<List<UserDefectMediaEntity>>

    @Query("""UPDATE user_media_tab SET defect_status = 1, is_for_send = 1 WHERE defect_id IN (:arrayList)""")
    fun acceptByID(arrayList: List<Int>)

    @Query("""UPDATE user_media_tab SET audio_name = null, audio_url = null WHERE defect_id = :defectID""")
    fun deleteAudio(defectID: Int?)

    @Query("""SELECT EXISTS(SELECT 1 FROM user_media_tab WHERE remont_id = :remontID AND stage_id = :activeStageID)""")
    fun isDefectsExistInStage(remontID: Int, activeStageID: Int) : Boolean
}
