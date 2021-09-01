package kz.cheesenology.smartremontmobile.data.standartphoto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe

@Dao
interface StandartPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(standartList: ArrayList<StandartPhotoEntity>)

    @Query("""
        SELECT * FROM standart_tab
         ORDER BY check_list_standart_id DESC
    """)
    fun getList(): Maybe<List<StandartPhotoEntity>>

    @Query("""
        SELECT * FROM standart_tab WHERE check_list_id = :checkListID AND is_good = 1
    """)
    fun getGoodList(checkListID: Int): Maybe<List<StandartPhotoEntity>>

    @Query("""
        SELECT * FROM standart_tab WHERE check_list_id = :checkListID AND is_good = 0
    """)
    fun getWeakList(checkListID: Int): Maybe<List<StandartPhotoEntity>>

    @Query("""
        DELETE FROM standart_tab
    """)
    fun delete()
}