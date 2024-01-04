package uz.fido.network.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.fido.network.domain.model.payment.location.LocalPaymentType

@Dao
interface LocalPaymentTypeDao {

    @Insert
    suspend fun insertLocalPayment(localPaymentType: LocalPaymentType): Long

    @Query("SELECT * FROM LocalPaymentType")
    suspend fun getLocalPaymentList(): List<LocalPaymentType>
}