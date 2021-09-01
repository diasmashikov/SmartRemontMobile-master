package kz.cheesenology.smartremontmobile.data.check.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe
import java.util.*

@Dao
interface CheckListHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(historyList: ArrayList<CheckListHistoryEntity>)

    @Query("""
        SELECT t.* FROM check_list_history_tab t
         WHERE t.check_list_id = :checkListID
         AND t.remont_id = :remontID
    """)
    fun getListByID(remontID: Int?, checkListID: Int): Maybe<List<CheckListHistoryEntity>>

    @Query("""
        DELETE FROM check_list_history_tab
    """)
    fun delete()

}