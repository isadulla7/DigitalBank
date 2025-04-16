package uz.fido.universaldigital.ui.fragments.payment.my_home

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IMonitoringRepository
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.model.monitoring.home.HomeHistoryRequest
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.template.CreateTemplateGroupRequest
import uz.fido.network.domain.model.template.DeleteTemplateRequest
import uz.fido.network.domain.model.template.EditTemplateGroupRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MyHomeViewModel @Inject constructor(
    application: Application,
    private val templateRepository: ITemplateRepository,
    private val paymentRepository: IPaymentRepository,
    private val monitoringRepository: IMonitoringRepository
) : AbstractViewModel(application = application) {

    fun getTemplateGroups(token: String) = liveData(Dispatchers.IO) {
        emit(templateRepository.getTemplateGroup(token))
    }

    fun createTemplateGroup(token: String, createTemplateGroupRequest: CreateTemplateGroupRequest) = liveData(Dispatchers.IO) {
        emit(templateRepository.createTemplateGroup(token, createTemplateGroupRequest))
    }

    fun deleteTemplateGroup(token: String, getTemplateListRequest: GetTemplateListRequest) = liveData(Dispatchers.IO) {
        emit(templateRepository.deleteTemplateGroup(token, getTemplateListRequest))
    }

    fun editTemplateGroup(token: String, editTemplateGroupRequest: EditTemplateGroupRequest) = liveData(Dispatchers.IO) {
        emit(templateRepository.editTemplateGroup(token, editTemplateGroupRequest))
    }

    fun getTemplateList(token: String, getTemplateListRequest: GetTemplateListRequest) = liveData(Dispatchers.IO) {
        emit(templateRepository.getTemplateList(token, getTemplateListRequest))
    }

    fun getUpdatedTemplateList(token: String) = liveData(Dispatchers.IO) {
        emit(templateRepository.getUpdatedTemplateList(token))
    }

    fun deleteTemplate(token: String, deleteTemplateRequest: DeleteTemplateRequest) = liveData(Dispatchers.IO) {
        emit(templateRepository.deleteTemplate(token, deleteTemplateRequest))
    }

    fun getTemplate(token: String, getTemplateRequest: GetTemplateRequest) = liveData(Dispatchers.IO) {
        emit(templateRepository.getTemplate(token, getTemplateRequest))
    }

    fun preparePaymentRequest(token: String, preparePaymentRequest: PreparePaymentRequest) = liveData(Dispatchers.IO) {
        emit(paymentRepository.preparePayment(token, preparePaymentRequest))
    }

    fun createPaymentRequest(path: String, token: String, createPaymentRequest: CreatePaymentRequest) = liveData(Dispatchers.IO) {
        emit(paymentRepository.createPayment(token, createPaymentRequest, path))
    }

    fun getHistoryByAcc(token: String, request: HomeHistoryRequest) = liveData(Dispatchers.IO) {
        emit(monitoringRepository.getHistoryByAcc(token, request))
    }

}