package kz.cheesenology.smartremontmobile.data.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import java.util.*

@Dao
interface NotificationDao {
    @Query("""SELECT * FROM notification_tab ORDER BY notification_id DESC LIMIT 100""")
    fun getList(): Flowable<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(dbList: ArrayList<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notificationEntity: NotificationEntity)

    @Query("""
        SELECT count(1) FROM notification_tab WHERE is_read = 0 LIMIT 1
    """)
    fun isNewNotification() : Flowable<List<Int>>

    @Query("""UPDATE notification_tab SET is_read = 1""")
    fun updateReadStatus()
}