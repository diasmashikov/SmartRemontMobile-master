package kz.cheesenology.smartremontmobile.data.userphoto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_media_tab")
data class UserDefectMediaEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "defect_id") var defectID: Int? = null,
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "stage_id") var stage_id: Int,
        @ColumnInfo(name = "check_list_id") var checkListID: Int? = null,
        @ColumnInfo(name = "file_url") var fileUrl: String? = null,
        @ColumnInfo(name = "file_name") var fileName: String?,
        @ColumnInfo(name = "file_type") var fileType: String?,
        @ColumnInfo(name = "date_create") var dateCreate: String? = null,
        @ColumnInfo(name = "defect_status") var defectStatus: Int?,
        @ColumnInfo(name = "comment") var comment: String? = null,
        @ColumnInfo(name = "audio_url") var audioUrl: String? = null,
        @ColumnInfo(name = "audio_name") var audioName: String? = null,
        @ColumnInfo(name = "is_for_send") var isForSend: Int?
)