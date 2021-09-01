package kz.cheesenology.smartremontmobile.data.requestlist.drafthistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus.PhotoDraftStatusEntity
import kz.cheesenology.smartremontmobile.model.RequestSendCntModel
import java.util.ArrayList

@Dao
interface RequestCheckListHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(requestHistoryList: ArrayList<RequestCheckListHistoryEntity>)

    @Query(
        """
        SELECT * FROM client_request_draft_check_history_tab WHERE client_request_id = :clientRequestID ORDER BY client_request_draft_check_history_id DESC
    """
    )
    fun getHistoryByClientRequestID(clientRequestID: Int): Observable<List<RequestCheckListHistoryEntity>>

    @Insert
    fun insert(requestCheckListHistoryEntity: RequestCheckListHistoryEntity) : Long

    @Query(
        """
        SELECT t1.draft_check_cnt, t2.check_list_photo_cnt, t3.check_list_cnt
        FROM 
        (
            SELECT count(1) draft_check_cnt
            FROM  client_request_draft_check_history_tab WHERE is_for_send = 1
        ) t1,
        (
            SELECT count(1) check_list_photo_cnt
            FROM request_check_photo_tab WHERE is_for_send = 1
        )t2,
        (
            SELECT count(1) check_list_cnt
            FROM client_request_draft_check_tab WHERE is_for_send = 1
        )t3
    """
    )
    fun getSendCnt(): RequestSendCntModel

    @Query(
        """
        SELECT * FROM client_request_draft_check_history_tab WHERE is_for_send = 1
    """
    )
    fun getListForSend(): Maybe<List<RequestCheckListHistoryEntity>>

    @Query(
        """
        DELETE FROM client_request_draft_check_history_tab
    """
    )
    fun delete()

    @Query(
        """
        UPDATE client_request_draft_check_history_tab SET is_for_send = 0
    """
    )
    fun updateSend()

    @Query(
        """
        SELECT count(1) FROM client_request_draft_check_history_tab WHERE client_request_id = :clientRequestID AND is_for_send = 1
    """
    )
    fun checkRequestSendExistance(clientRequestID: Int): Int



}