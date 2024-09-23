package uz.fido.network.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.fido.network.domain.model.search.LocalSearchDto

@Dao
interface LocalSearchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearch(searchDto: LocalSearchDto):Long

    @Query("SELECT * FROM LocalSearchDto")
    suspend fun getLocalSearchList(): List<LocalSearchDto>

}
