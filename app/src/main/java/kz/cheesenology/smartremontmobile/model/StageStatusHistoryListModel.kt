package kz.cheesenology.smartremontmobile.model

import androidx.room.ColumnInfo

data class StageStatusHistoryListModel(
    @ColumnInfo(name = "stage_status_hist_id") val stageStatusHistID: Int? = null,
    @ColumnInfo(name = "remont_id") val remontID: Int,
    @ColumnInfo(name = "stage_id") val stageID: Int,
    @ColumnInfo(name = "stage_status_id") val stageStatusID: Int,
    @ColumnInfo(name = "date_create") val dateCreate: String,
    @ColumnInfo(name = "fio") val fio: String,
    @ColumnInfo(name = "stage_name") val stageName: String,
    @ColumnInfo(name = "stage_status_name") val stageStatusName: String,
    @ColumnInfo(name = "comment") val comment: String? = null,
    @ColumnInfo(name = "remark_name") val remarkName: String? = null
)