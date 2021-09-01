package kz.cheesenology.smartremontmobile.data.check

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remont_check_list_tab")
data class RemontCheckListEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "remont_check_list_id") var remontCheckListID: Int? = null,
        @ColumnInfo(name = "remont_id") var remontID: Int?,
        @ColumnInfo(name = "check_list_id") var checkListID: Int?,
        @ColumnInfo(name = "check_list_pid") var checkListPID: Int? = null,
        @ColumnInfo(name = "check_name") var checkName: String? = null,
        @ColumnInfo(name = "norm") var norm: String? = null,
        @ColumnInfo(name = "is_room") var isRoom: Int? = null,
        @ColumnInfo(name = "stage_id") var stageID: Int? = null,
        @ColumnInfo(name = "is_active") var isActive: Int? = null,
        @ColumnInfo(name = "room_id") var roomID: Int? = null,
        @ColumnInfo(name = "audio_info") var audioInfo: String? = null,
        @ColumnInfo(name = "audio_name") var audioName: String? = null,
        @ColumnInfo(name = "is_audio_for_send") var isAudioForSend: Int? = null,
        @ColumnInfo(name = "defect_cnt") var defectCnt: Int? = null,
        @ColumnInfo(name = "description") var description: String?,
        @ColumnInfo(name = "is_accepted") var isAccepted: Int?,
        @ColumnInfo(name = "is_for_send") var isForSend: Int?
)