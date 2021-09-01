package kz.cheesenology.smartremontmobile.model.ratings

import androidx.room.ColumnInfo

data class RatingListModel(
    @ColumnInfo(name = "rating_detail_id") var ratingDetailID: Int,
    @ColumnInfo(name = "role_id") var roleID: Int,
    @ColumnInfo(name = "detail_name") var detailName: String,
    @ColumnInfo(name = "detail_code") var detailCode: String,
    @ColumnInfo(name = "detail_weight") var detailWeight: Int,
    @ColumnInfo(name = "step_name") val stepName: String
)