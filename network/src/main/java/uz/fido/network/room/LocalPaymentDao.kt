package uz.fido.network.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.fido.network.domain.model.payment.location.LocalPayment

@Dao
interface LocalPaymentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocalPayment(localPayment: LocalPayment):Long

    @Query("SELECT * FROM LocalPayment")
    suspend fun getLocalPaymentList():List<LocalPayment>
}