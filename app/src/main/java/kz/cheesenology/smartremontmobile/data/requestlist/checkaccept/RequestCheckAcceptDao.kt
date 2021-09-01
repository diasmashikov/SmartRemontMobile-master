package kz.cheesenology.smartremontmobile.data.requestlist.checkaccept

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.ArrayList

@Dao
interface RequestCheckAcceptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(requestCheckAcceptList: ArrayList<RequestCheckAcceptEntity>)

    @Query("""DELETE FROM client_request_draft_check_tab""")
    fun delete()

    @Query("""
        UPDATE client_request_draft_check_tab 
        SET is_accepted = :active, is_for_send = 1, client_request_id = :clientRequestID
        WHERE draft_check_list_id = :draftCheckListId AND client_request_id = :clientRequestID
    """)
    fun changeCheckListStatus(draftCheckListId: Int?, active: Int, clientRequestID: Int)

    @Query("""
        SELECT count(1) FROM client_request_draft_check_tab WHERE draft_check_list_id = :draftCheckListId AND client_request_id = :clientRequestID
    """)
    fun exist(draftCheckListId: Int?, clientRequestID: Int?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(requestCheckAcceptEntity: RequestCheckAcceptEntity)

    @Query("""
        SELECT * FROM client_request_draft_check_tab WHERE is_for_send = 1
    """)
    fun getDataForSend() : List<RequestCheckAcceptEntity>

    @Query("UPDATE client_request_draft_check_tab SET is_for_send = 0")
    fun updateSend()

}