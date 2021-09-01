package kz.cheesenology.smartremontmobile.data.reporttype

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import java.util.*

@Dao
interface ReportTypeDao {

    @Query("""DELETE FROM report_type_tab""")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(reportTypeList: ArrayList<ReportTypeEntity>)

    @Query("""SELECT * FROM report_type_tab""")
    fun getList() : Flowable<List<ReportTypeEntity>>

}