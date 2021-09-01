package kz.cheesenology.smartremontmobile.data.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface StageChatFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(chatFileList: MutableList<StageChatFileEntity>)

    @Query("""SELECT * FROM stage_chat_file_tab WHERE stage_chat_id = :stageChatID AND file_ext IN ("jpg","JPG", "png", "PNG", "jpeg", "JPEG") """)
    fun getPhotoFileListOnMessage(stageChatID: Int?) : List<StageChatFileEntity>

    @Query("""SELECT * FROM stage_chat_file_tab WHERE stage_chat_id = :stageChatID AND file_ext NOT IN ("jpg","JPG", "png", "PNG", "jpeg", "JPEG") """)
    fun getFilesListOnMessage(stageChatID: Int?): List<StageChatFileEntity>

    @Query("""SELECT * FROM stage_chat_file_tab WHERE chat_message_id = :chatMessageID""")
    fun getPhotoFileList(chatMessageID: Int?) : Single<List<StageChatFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatFileList: StageChatFileEntity)

    @Query("""DELETE FROM stage_chat_file_tab""")
    fun delete()

    @Query("""DELETE FROM stage_chat_file_tab WHERE chat_message_id = :chatMessageID""")
    fun deletePhotoReportFiles(chatMessageID: Int)
}