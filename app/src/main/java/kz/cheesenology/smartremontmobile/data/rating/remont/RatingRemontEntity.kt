package kz.cheesenology.smartremontmobile.data.rating.remont

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rating_remont_tab")
data class RatingRemontEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rating_remont_id") var ratingRemontID: Int? = null,
    @ColumnInfo(name = "remont_id") var remontID: Int,
    @ColumnInfo(name = "step_id") var stepID: Int,
    @ColumnInfo(name = "contractor_id") var contractorID: Int,
    @ColumnInfo(name = "is_edit") var isEdit: Boolean,
    @ColumnInfo(name = "request_id") var requestID: Int? = null
)