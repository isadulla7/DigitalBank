package uz.fido.universaldigital.ui.fragments.transfers.success

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class SuccessTransferViewModel @Inject constructor(
    application: Application,
    private val templateRepository: ITemplateRepository,
) : AbstractViewModel(application) {

    fun createTemplate(token: String, createTemplateRequest: CreateTemplateRequest) = liveData(Dispatchers.IO) {
        emit(templateRepository.createTemplate(token, createTemplateRequest))
    }
}