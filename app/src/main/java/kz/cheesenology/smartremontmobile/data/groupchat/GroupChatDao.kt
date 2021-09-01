package kz.cheesenology.smartremontmobile.data.groupchat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import java.util.*

@Dao
interface GroupChatDao {

    @Query("""DELETE FROM group_chat_tab""")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(groupChatList: ArrayList<GroupChatEntity>)

    @Query("""SELECT * FROM group_chat_tab""")
    fun getStageList(): Flowable<List<GroupChatEntity>>

}