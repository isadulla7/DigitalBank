package uz.fido.network.domain.datasource.room

import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentRoom
import uz.fido.network.domain.model.payment.location.LocalPaymentType
import uz.fido.network.room.LocalPaymentDao
import uz.fido.network.room.LocalPaymentTypeDao
import javax.inject.Inject

class DbRepository @Inject constructor(
    private val dao: LocalPaymentDao,
    private val daoType: LocalPaymentTypeDao
) {

    suspend fun saveLocalPayment(localPaymentRoom: LocalPayment) =
        dao.insertLocalPayment(localPaymentRoom)

    suspend fun getLocalPayment() = dao.getLocalPaymentList()
    suspend fun saveLocalPaymentType(localPaymentType: LocalPaymentType) =
        daoType.insertLocalPayment(localPaymentType)

    suspend fun getLocalPaymentType() = daoType.getLocalPaymentList()
}