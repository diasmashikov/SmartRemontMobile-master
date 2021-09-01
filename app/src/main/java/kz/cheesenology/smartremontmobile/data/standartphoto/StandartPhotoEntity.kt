package kz.cheesenology.smartremontmobile.data.standartphoto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "standart_tab")
data class StandartPhotoEntity(
    @PrimaryKey
    @ColumnInfo(name = "check_list_standart_id") var standartID: Int,
    @ColumnInfo(name = "check_list_id") var checkListID: Int,
    @ColumnInfo(name = "photo_url") var photoURL: String,
    @ColumnInfo(name = "photo_comment") var photoComment: String,
    @ColumnInfo(name = "photo_name") var photoName: String,
    @ColumnInfo(name = "is_good") var isGood: Int
)