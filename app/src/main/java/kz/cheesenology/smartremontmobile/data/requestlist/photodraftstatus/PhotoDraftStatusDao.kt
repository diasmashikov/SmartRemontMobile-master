package kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface PhotoDraftStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photoDraftStatusEntity: PhotoDraftStatusEntity)

    @Query("""
        SELECT * FROM photo_draft_status_tab WHERE draft_status_id = :draftID
    """)
    fun getPhotoListByID(draftID: Int): Flowable<List<PhotoDraftStatusEntity>>
}