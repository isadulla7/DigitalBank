package uz.fido.universaldigital.ui.fragments.services.map_payment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.room.DbRepository
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentRequest
import uz.fido.network.domain.model.payment.location.LocalPaymentType
import uz.fido.network.domain.model.payment.location.PaymentByLocationRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentBranchViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository,
    private val dao: DbRepository
) : AbstractViewModel(application) {

    val localPayment = MutableLiveData<ArrayList<LocalPayment>>()
    val localPaymentType = MutableLiveData<LocalPaymentType>()
    val clientPosition = MutableLiveData<LatLng>()
    var isCurrent = false

    fun setPaymentByLocation(paymentLocation: ArrayList<LocalPayment>) {
        localPayment.value = paymentLocation
    }

    fun setLocalPaymentType(paymentType: LocalPaymentType) {
        localPaymentType.value = paymentType
    }

    fun fetchLocalPayments(token: String, paymentByLocationRequest: PaymentByLocationRequest) =
        liveData(
            Dispatchers.IO
        ) {
            emit(paymentRepository.fetchLocalPayments(token, paymentByLocationRequest))
        }

    fun fetchLocalPaymentTypes(token: String) = liveData(Dispatchers.IO) {
        emit(paymentRepository.fetchLocalPaymentTypes(token))
    }

    fun saveLocalPayment(localPaymentRoom: LocalPayment) = liveData(Dispatchers.IO) {
        emit(dao.saveLocalPayment(localPaymentRoom))
    }

    fun getLocalPayment() = liveData(Dispatchers.IO) {
        emit(dao.getLocalPayment())
    }

    fun saveLocalPaymentType(localPaymentType: LocalPaymentType) = liveData(Dispatchers.IO) {
        emit(dao.saveLocalPaymentType(localPaymentType))
    }

    fun getLocalPaymentType() = liveData(Dispatchers.IO) {
        emit(dao.getLocalPaymentType())
    }

    fun getLocalPayment(token: String, localPaymentRequest: LocalPaymentRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.localPayment(token, localPaymentRequest))
        }
}