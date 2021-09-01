package kz.cheesenology.smartremontmobile.data.rating.step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rating_step_tab")
data class RatingStepEntity(
    @PrimaryKey
    @ColumnInfo(name = "rating_step_id") val ratingStepID: Int,
    @ColumnInfo(name = "rating_detail_id") val ratingDetailID: Int,
    @ColumnInfo(name = "step_name") val stepName: String,
    @ColumnInfo(name = "step_order") val stepOrder: Int
)