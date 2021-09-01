package kz.cheesenology.smartremontmobile.data.useraudio

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "user_audio_tab")
data class AudioEntity @Ignore constructor (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "remont_check_list_audio_id") var audioID: Int?= null,
    @ColumnInfo(name = "remont_check_list_id") var remontCheckListID: Int?= null,
    @ColumnInfo(name = "audio_url") var audioURL: String?= null,
    @ColumnInfo(name = "audio_name") var audioName: String?= null,
    @ColumnInfo(name = "date_create") var dateCreate: String?= null
)