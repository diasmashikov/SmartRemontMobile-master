package kz.cheesenology.smartremontmobile.data.request

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request_tab")
data class RequestEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "request_id") var requestID: Int? = null,
        @ColumnInfo(name = "request_num") var requestNum: Int? = null,
        @ColumnInfo(name = "random_num") var randomNum: Int? = null,
        @ColumnInfo(name = "request_type_id") var requestTypeID: Int,
        @ColumnInfo(name = "request_status_id") var requestStatusID: Int,
        @ColumnInfo(name = "error_msg") var errorMsg: String? = null,
        @ColumnInfo(name = "data") var data: String? = null,
        @ColumnInfo(name = "remont_id") var remontID: Int? = null,
        @ColumnInfo(name = "date_create") var dateCreate: String
)