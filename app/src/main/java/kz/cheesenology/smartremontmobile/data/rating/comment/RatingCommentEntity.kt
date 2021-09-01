package kz.cheesenology.smartremontmobile.data.rating.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rating_comment_tab")
data class RatingCommentEntity(
    @PrimaryKey
    @ColumnInfo(name = "rating_comment_id") val ratingCommentID: Int,
    @ColumnInfo(name = "remont_id") val remont_id: Int,
    @ColumnInfo(name = "role_id") val roleID: Int,
    @ColumnInfo(name = "comments") val comments: String?
)