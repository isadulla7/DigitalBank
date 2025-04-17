package uz.fido.universaldigital.ui.fragments.payment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MenuPaymentViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository,
    private val templateRepository: ITemplateRepository
) : AbstractViewModel(application) {

    var templates: MutableLiveData<List<Template>> = MutableLiveData()

    fun getOperationParams(token: String, getOperationParamRequest: GetOperationInfoRequest) = liveData(Dispatchers.IO) {
        emit(paymentRepository.getOperationParams(token, getOperationParamRequest))
    }

    fun preparePaymentRequest(token: String, preparePaymentRequest: PreparePaymentRequest) =
        liveData(
            Dispatchers.IO
        ) {
            emit(paymentRepository.preparePayment(token, preparePaymentRequest))
        }

    fun createTemplate(token: String, createTemplateRequest: CreateTemplateRequest) =
        liveData(Dispatchers.IO) {
            emit(templateRepository.createTemplate(token, createTemplateRequest))
        }

}