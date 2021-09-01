package kz.cheesenology.smartremontmobile.data.request

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface RequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(requestEntity: RequestEntity): Long

    /* @Query("SELECT * FROM request_tab ORDER BY is_favorite DESC")
     fun getList() : Flowable<List<RequestEntity>>

     @Query("UPDATE request_tab SET request_str = :str WHERE id = 1")
     fun updateData(str: String) : Maybe<Int>

     @Query("UPDATE request_tab SET is_favorite = :i WHERE id = :id")
     fun addToFavorite(id: Int, i: Int): Maybe<Int>*/

    @Query(
        """
        SELECT * FROM request_tab
        WHERE request_status_id IN (1)
        ORDER BY request_id DESC"""
    )
    fun getSendData(): Maybe<List<RequestEntity>>

    @Query("""UPDATE request_tab SET request_status_id = 2 WHERE request_id = (select max(request_id) from request_tab)""")
    fun updateLast()

    @Query("""UPDATE request_tab SET request_status_id = 2 """)
    fun updateAllSuccess()

    @Query("""SELECT * FROM request_tab WHERE remont_id = :remontID AND request_id = (select max(request_id) from request_tab where request_type_id = :workCallOkk)""")
    fun getStageFinishRequests(remontID: Int, workCallOkk: Int): Flowable<RequestEntity>

    @Query(
        """
        UPDATE request_tab SET request_status_id = :requestStatus, error_msg = :errorMsg  WHERE request_id = :requestID
    """
    )
    fun updateRequestStatusByID(
        requestID: Int?,
        requestStatus: Int,
        errorMsg: String?
    )
}