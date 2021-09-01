package kz.cheesenology.smartremontmobile.data.stagestatus

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage_status_tab")
data class StageStatusEntity (
        @PrimaryKey
        @ColumnInfo(name = "stage_status_id") var stageStatusID: Int,
        @ColumnInfo(name = "status_name") var statusName: String?,
        @ColumnInfo(name = "status_code") var statusCode: String?,
        @ColumnInfo(name = "what") var what: String?
)