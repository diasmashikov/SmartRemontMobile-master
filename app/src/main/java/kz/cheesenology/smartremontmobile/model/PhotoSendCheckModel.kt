package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class PhotoSendCheckModel(
        @ColumnInfo(name = "defect_id") var remontCheckListPhotoID: Int? = null,
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "check_list_id") var checkListID: Int? = null,
        @ColumnInfo(name = "check_list_pid") var checkListPID: Int? = null,
        @ColumnInfo(name = "check_name") var checkName: String? = null,
        @ColumnInfo(name = "room_id") var roomID: Int? = null,
        @ColumnInfo(name = "photo_url") var photoURL: String? = null,
        @ColumnInfo(name = "photo_name") var photoName: String?,
        @ColumnInfo(name = "date_create") var dateCreate: String?,
        @ColumnInfo(name = "is_delete") var isDelete: Int?,
        @ColumnInfo(name = "is_for_send") var isForSend: Int?,
        @ColumnInfo(name = "comment") var comment: String?,
        @ColumnInfo(name = "active_stage_id") var active_stage_id: Int?
)