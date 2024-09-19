package uz.fido.universaldigital.ui.fragments.payment.download_payment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

@HiltViewModel
class DownloadPaymentViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository
) : AbstractViewModel(application) {

    var paymentGroupMutableList = MutableLiveData<ArrayList<PaymentGroup>>()

    fun downloadPayment() = liveData(Dispatchers.IO) {
        emit(paymentRepository.getPaymentFile(context.getClientToken()))
    }
}