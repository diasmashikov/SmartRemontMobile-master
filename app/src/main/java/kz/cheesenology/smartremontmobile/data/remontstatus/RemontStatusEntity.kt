package kz.cheesenology.smartremontmobile.data.remontstatus

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remont_status_tab")
data class RemontStatusEntity(
        @PrimaryKey
        @ColumnInfo(name = "remont_status_id") var remontStatusID: Int,
        @ColumnInfo(name = "status_name") var statusName: String?,
        @ColumnInfo(name = "status_code") var statusCode: String?
)