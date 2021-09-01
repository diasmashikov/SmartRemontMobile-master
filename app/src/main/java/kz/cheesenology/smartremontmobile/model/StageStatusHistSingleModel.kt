package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class StageStatusHistSingleModel(
        @ColumnInfo(name = "status_name") val statusName: String?,
        @ColumnInfo(name = "date_create") var dateCreate: String?
)