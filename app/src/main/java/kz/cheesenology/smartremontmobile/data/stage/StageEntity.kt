package kz.cheesenology.smartremontmobile.data.stage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage_tab")
data class StageEntity(
        @PrimaryKey
        @ColumnInfo(name = "stage_id") var stageID: Int,
        @ColumnInfo(name = "stage_name") var stageName: String,
        @ColumnInfo(name = "stage_code") var stageCode: String,
        @ColumnInfo(name = "stage_order_num") var stageOrderNum: Int,
        @ColumnInfo(name = "stage_short_name") var stageShortName: String
) {
    override fun toString(): String {
        return stageName.toString()
    }
}