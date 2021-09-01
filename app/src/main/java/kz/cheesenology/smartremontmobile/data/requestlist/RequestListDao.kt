package kz.cheesenology.smartremontmobile.data.requestlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kz.cheesenology.smartremontmobile.data.requestlist.drafthistory.RequestCheckListHistoryEntity
import java.util.ArrayList

@Dao
interface RequestListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(requestList: ArrayList<RequestListEntity>)

    @Query("""
        SELECT * FROM request_list_tab 
        WHERE (is_hide != 1 OR is_hide IS NULL) 
        AND is_draft_accept IN (:listStatusRequest)
    """)
    fun getActiveRequestList(listStatusRequest: MutableList<Int>): Flowable<List<RequestListEntity>>

    @Query("""DELETE FROM request_list_tab""")
    fun delete()

    @Query("""
        UPDATE request_list_tab SET is_hide = 1 WHERE client_request_id IN (:requestListForSend)
    """)
    fun hideSendRequests(requestListForSend: MutableList<Int>)

    // test section

    @Query("SELECT * FROM request_list_tab WHERE manager_project_name LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flowable<List<RequestListEntity>>

    @Query("SELECT * FROM request_list_tab")
    fun readData(): Flowable<List<RequestListEntity>>

}