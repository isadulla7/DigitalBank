package uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.branches.GetBankNameRequest
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.branches.OneTimeInfoRequest
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class RequisitesViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository,
    private val utilsRepository: IUtilsRepository
) : AbstractViewModel(application) {

    fun getBankName(token: String, getBankNameRequest: GetBankNameRequest) = liveData(Dispatchers.IO) {
        emit(paymentRepository.getBankNameRequest(token, getBankNameRequest))
    }

    fun oneTimeInfo(token: String, oneTimeInfoRequest: OneTimeInfoRequest) = liveData(Dispatchers.IO) {
        emit(paymentRepository.oneTimeInfoRequest(token, oneTimeInfoRequest))
    }

    fun preparePaymentRequest(token: String, preparePaymentRequest: PreparePaymentRequest) = liveData(Dispatchers.IO) {
        emit(paymentRepository.preparePayment(token, preparePaymentRequest))
    }

    fun createPaymentRequest(
        token: String,
        createPaymentRequest: CreatePaymentRequest,
        path: String = ""
    ) = liveData(Dispatchers.IO) {
        emit(paymentRepository.createPayment(token, createPaymentRequest, path))
    }

    fun getBranches(token: String, getBranchListRequest: GetBranchListRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getBranchList(token, getBranchListRequest))
    }
}