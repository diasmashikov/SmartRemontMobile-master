package kz.cheesenology.smartremontmobile.data.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import kz.cheesenology.smartremontmobile.model.ChatMessageModel

@Dao
interface StageChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stageChatEntity: StageChatEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remontStageChatList: MutableList<StageChatEntity>)

    @Query("""
        SELECT t.*, t1.request_status_id, t1.error_msg
        FROM stage_chat_tab t
        LEFT JOIN request_tab t1 ON t.request_id = t1.request_id
        WHERE t.remont_id= :remontID AND t.group_chat_id = :stageID
        ORDER BY t.stage_chat_id IS NULL, t.stage_chat_id ASC
    """)
    fun getList(remontID: Int, stageID: Int) : Flowable<List<ChatMessageModel>>

    @Query("""
        SELECT t.*
        FROM stage_chat_tab t
        WHERE t.remont_id= :remontID AND (t.group_chat_id >= 3 AND t.group_chat_id < 6) AND t.message LIKE 'Фото%'
    """)
    fun getListik(remontID: Int) : Flowable<List<ChatMessageModel>>

    @Query("""DELETE FROM stage_chat_tab""")
    fun delete()

    @Query("""DELETE FROM stage_chat_tab WHERE chat_message_id = :chatMessageID""")
    fun deletePhotoReport(chatMessageID: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewMessage(stageChatEntity: StageChatEntity): Long

    @Query("""SELECT * FROM stage_chat_tab WHERE request_id = :requestID""")
    fun getMessage(requestID: Int?): Maybe<StageChatEntity>

    @Query("""SELECT * FROM stage_chat_tab WHERE request_id = :requestID""")
    fun getChatID(requestID: Int?): Long

    @Query("""SELECT * FROM stage_chat_tab WHERE remont_id = :remontID """)
    fun getChatIDs(remontID: Int?): Long

    @Query("""UPDATE stage_chat_tab SET message = :comment WHERE chat_message_id = :chatMessageID""")
    fun updateMessageWithNewComment(comment: String, chatMessageID: Int)


}