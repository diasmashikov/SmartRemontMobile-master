package kz.cheesenology.smartremontmobile.data.rating

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rating_detail_tab")
data class RatingDetailEntity(
    @PrimaryKey
    @ColumnInfo(name = "rating_detail_id") var ratingDetailID: Int,
    @ColumnInfo(name = "role_id") var roleID: Int,
    @ColumnInfo(name = "detail_name") var detailName: String,
    @ColumnInfo(name = "detail_code") var detailCode: String,
    @ColumnInfo(name = "detail_weight") var detailWeight: Int
)