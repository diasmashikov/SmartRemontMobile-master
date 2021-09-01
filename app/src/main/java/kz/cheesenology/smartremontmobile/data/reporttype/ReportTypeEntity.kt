package kz.cheesenology.smartremontmobile.data.reporttype

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "report_type_tab")
data class ReportTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = "report_type_id") var reportTypeID: Int,
    @ColumnInfo(name = "report_type_code") var reportTypeCode: String,
    @ColumnInfo(name = "report_type_name") var reportTypeName: String
) {
    override fun toString(): String {
        return reportTypeName
    }
}