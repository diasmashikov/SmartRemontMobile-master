package kz.cheesenology.smartremontmobile.data.requestlist.checkphoto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import java.util.ArrayList

@Dao
interface CheckRequestPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(checkRequestPhotoEntitiy: CheckRequestPhotoEntitiy)

    @Query("""
        SELECT * FROM request_check_photo_tab WHERE client_request_id = :clientRequestID AND draft_check_list_id = :draftCheckID
    """)
    fun getPhotoByID(clientRequestID: Int?, draftCheckID: Int?) : Flowable<List<CheckRequestPhotoEntitiy>>

    @Query("""
        UPDATE request_check_photo_tab SET comment = :comment WHERE request_check_photo_id = :checkID
    """)
    fun updateComment(comment: String, checkID: Int?)

    @Query("""
        DELETE FROM request_check_photo_tab
    """)
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(requestCheckPhotoList: ArrayList<CheckRequestPhotoEntitiy>)

    @Query("""
        SELECT * FROM request_check_photo_tab WHERE is_for_send = 1 
    """)
    fun getDataForSend(): List<CheckRequestPhotoEntitiy>

    @Query("""
        UPDATE request_check_photo_tab SET is_for_send = 0
    """)
    fun updateSend()

}