package uz.fido.universaldigital.ui.fragments.payment.abc_confirm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ConfirmPaymentViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository,
    private val templateRepository: ITemplateRepository
) : AbstractViewModel(application) {

    var templates: MutableLiveData<List<Template>> = MutableLiveData()

    fun updateTemplates(transfers: List<Template>) {
        this.templates.postValue(transfers)
    }

    fun getTemplateList(token: String, getTemplateListRequest: GetTemplateListRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(templateRepository.getTemplateList(token, getTemplateListRequest))
    }

    fun getOperationParams(token: String, getOperationParamRequest: GetOperationInfoRequest) =
        liveData(
            Dispatchers.IO
        ) {
            emit(paymentRepository.getOperationParams(token, getOperationParamRequest))
        }

    fun createPaymentRequest(
        token: String,
        createPaymentRequest: CreatePaymentRequest,
        path: String
    ) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.createPayment(token, createPaymentRequest, path))
        }

}