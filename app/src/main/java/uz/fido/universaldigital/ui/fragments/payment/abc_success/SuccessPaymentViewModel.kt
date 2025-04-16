package uz.fido.universaldigital.ui.fragments.payment.abc_success

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.model.payment.PrintChequeRequest
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class SuccessPaymentViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository,
    private val templateRepository: ITemplateRepository
) : AbstractViewModel(application) {


    fun printCheque(token: String, printChequeRequest: PrintChequeRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.printCheque(token, printChequeRequest))
        }

    fun createTemplate(token: String, createTemplateRequest: CreateTemplateRequest) =
        liveData(Dispatchers.IO) {
            emit(templateRepository.createTemplate(token, createTemplateRequest))
        }


}