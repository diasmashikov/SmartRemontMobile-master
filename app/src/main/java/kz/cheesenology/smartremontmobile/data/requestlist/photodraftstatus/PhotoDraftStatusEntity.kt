package kz.cheesenology.smartremontmobile.data.requestlist.photodraftstatus

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_draft_status_tab")
class PhotoDraftStatusEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "photo_draft_status_id") var photo_draft_status_id: Int? = null,
    @ColumnInfo(name = "photo_draft_status_server_id") var photo_draft_status_server_id: Int? = null,
    @ColumnInfo(name = "draft_status_id") var draft_status_id: Int? = null,
    @ColumnInfo(name = "photo_url") var photo_url: String? = null,
    @ColumnInfo(name = "photo_date") var photo_date: String? = null,
)