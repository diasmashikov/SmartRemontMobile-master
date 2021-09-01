package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class DefectListModel(
        @ColumnInfo(name = "defect_id") var defectID: Int? = null,
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "check_list_id") var checkListID: Int? = null,
        @ColumnInfo(name = "file_url") var fileURL: String? = null,
        @ColumnInfo(name = "file_name") var fileName: String?,
        @ColumnInfo(name = "file_type") var fileType: String?,
        @ColumnInfo(name = "date_create") var dateCreate: String?,
        @ColumnInfo(name = "is_delete") var isDelete: Int?,
        @ColumnInfo(name = "is_for_send") var isForSend: Int?,
        @ColumnInfo(name = "check_name") var checkName: String?,
        @ColumnInfo(name = "comment") var comment: String?,
        @ColumnInfo(name = "audio_url") var audioUrl: String?,
        @ColumnInfo(name = "audio_name") var audioName: String?,
        @ColumnInfo(name = "defect_status") var defectStatus: Int?
)