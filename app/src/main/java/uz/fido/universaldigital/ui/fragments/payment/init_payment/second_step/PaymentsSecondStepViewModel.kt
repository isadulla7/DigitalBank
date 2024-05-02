package uz.fido.universaldigital.ui.fragments.payment.init_payment.second_step

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentsSecondStepViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository
) : AbstractViewModel(application) {

    fun preparePaymentRequest(token: String, preparePaymentRequest: PreparePaymentRequest) =
        liveData(
            Dispatchers.IO
        ) {
            emit(paymentRepository.preparePayment(token, preparePaymentRequest))
        }

}