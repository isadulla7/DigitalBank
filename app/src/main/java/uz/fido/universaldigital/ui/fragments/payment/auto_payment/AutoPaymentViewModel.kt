package uz.fido.universaldigital.ui.fragments.payment.auto_payment

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.model.subscriptions.AutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.ChangeAutoPaymentStateRequest
import uz.fido.network.domain.model.subscriptions.DeleteAutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class AutoPaymentViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository
) : AbstractViewModel(application) {


    fun getAutoPaymentList(token: String, autoPaymentRequest: AutoPaymentRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(paymentRepository.getAutoPaymentList(token, autoPaymentRequest))
    }

    fun createAutoPayment(token: String, saveAutoPaymentModel: SaveAutoPaymentModel) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.createAutoPayment(token, saveAutoPaymentModel))
        }

    fun editAutoPayment(token: String, saveAutoPaymentModel: SaveAutoPaymentModel) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.editAutoPayment(token, saveAutoPaymentModel))
        }

    fun changeAutoPaymentState(
        token: String,
        changeAutoPaymentStateRequest: ChangeAutoPaymentStateRequest
    ) = liveData(Dispatchers.IO) {
        emit(paymentRepository.changeAutoPaymentState(token, changeAutoPaymentStateRequest))
    }

    fun deleteAutoPayment(token: String, deleteAutoPaymentRequest: DeleteAutoPaymentRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.deleteAutoPayment(token, deleteAutoPaymentRequest))
        }
}