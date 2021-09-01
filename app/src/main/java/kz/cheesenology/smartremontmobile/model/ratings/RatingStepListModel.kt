package kz.cheesenology.smartremontmobile.model.ratings

import androidx.room.ColumnInfo

data class RatingStepListModel(
    @ColumnInfo(name = "rating_step_id") val ratingStepID: Int,
    @ColumnInfo(name = "rating_detail_id") val ratingDetailID: Int,
    @ColumnInfo(name = "step_name") val stepName: String,
    @ColumnInfo(name = "step_order") val stepOrder: Int,
    @ColumnInfo(name = "rating_remont_id") val ratingRemontID: Int? = null
)