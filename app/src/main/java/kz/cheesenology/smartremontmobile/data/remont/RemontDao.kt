package kz.cheesenology.smartremontmobile.data.remont

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface RemontDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entity: List<RemontEntity>)

    @Query("""
        SELECT * FROM remont_tab
    """)
    fun getList() : Flowable<List<RemontEntity>>
}