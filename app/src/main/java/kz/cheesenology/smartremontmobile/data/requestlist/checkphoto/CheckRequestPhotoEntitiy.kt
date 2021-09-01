package kz.cheesenology.smartremontmobile.data.requestlist.checkphoto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request_check_photo_tab")
data class CheckRequestPhotoEntitiy (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "request_check_photo_id") var requestCheckPhotoID: Int? = null,
    @ColumnInfo(name = "request_check_list_id") var requestCheckListID: Int? = null,
    @ColumnInfo(name = "client_request_id") var clientRequestID: Int? = null,
    @ColumnInfo(name = "draft_check_list_id") var draft_check_list_id: Int? = null,
    @ColumnInfo(name = "request_check_photo_url") var requestCheckPhotoUrl: String? = null,
    @ColumnInfo(name = "request_check_photo_name") var requestCheckPhotoName: String? = null,
    @ColumnInfo(name = "request_check_photo_type") var requestCheckPhotoType: String? = null,
    @ColumnInfo(name = "date_create") var date_create: String? = null,
    @ColumnInfo(name = "comment") var comment: String? = null,
    @ColumnInfo(name = "is_for_send") var is_for_send: Int? = null,
)