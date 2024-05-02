package uz.fido.network.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentType

@Database(entities = [LocalPayment::class,LocalPaymentType::class], version = 1, exportSchema = false)
abstract class LocalPaymentDatabase:RoomDatabase() {
abstract fun localPaymentDao():LocalPaymentDao
abstract fun localPaymentTypeDao():LocalPaymentTypeDao
}