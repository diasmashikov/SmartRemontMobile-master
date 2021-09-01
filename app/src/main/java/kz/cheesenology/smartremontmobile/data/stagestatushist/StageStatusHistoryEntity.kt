package kz.cheesenology.smartremontmobile.data.stagestatushist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage_status_hist_tab")
data class StageStatusHistoryEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "stage_status_hist_id") var stageStatusHistID: Int? = null,
        @ColumnInfo(name = "remont_id") var remontID: Int,
        @ColumnInfo(name = "stage_id") var stageID: Int,
        @ColumnInfo(name = "stage_status_id") var stageStatusID: Int,
        @ColumnInfo(name = "date_create") var dateCreate: String,
        @ColumnInfo(name = "fio") var fio: String? = null,
        @ColumnInfo(name = "comments") var comment: String? = null,
        @ColumnInfo(name = "remark_name") var remarkName: String? = null
)