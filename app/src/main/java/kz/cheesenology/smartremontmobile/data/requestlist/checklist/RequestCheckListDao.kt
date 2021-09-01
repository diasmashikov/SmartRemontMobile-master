package kz.cheesenology.smartremontmobile.data.requestlist.checklist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe
import kz.cheesenology.smartremontmobile.model.RequestCheckListRoomModel

@Dao
interface RequestCheckListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(requestCheckList: ArrayList<RequestCheckListEntity>)

    @Query("""
        SELECT t.* FROM request_check_list_tab t 
        WHERE t.draft_check_list_pid IS NULL
    """)
    fun getCheckListPID() : Maybe<List<RequestCheckListEntity>>

    @Query("""
        SELECT t.*, t1.is_accepted
        FROM request_check_list_tab t
        LEFT JOIN client_request_draft_check_tab t1 ON t.draft_check_list_id = t1.draft_check_list_id AND t1.client_request_id = :clientRequestID
        WHERE t.draft_check_list_pid = :draftCheckListId
        GROUP BY t.draft_check_list_id
    """)
    fun getSubCheckList(draftCheckListId: Int?, clientRequestID: Int?): List<RequestCheckListRoomModel>

    @Query("""
        DELETE FROM request_check_list_tab
    """)
    fun delete()

}