package kz.cheesenology.smartremontmobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_list_tab")
data class TaskListEntity(
        @ColumnInfo(name = "name") var name: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}