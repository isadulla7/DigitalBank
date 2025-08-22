package uz.fido.universaldigital.ui.fragments.transfers.conversion

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.model.conversion.ConversionRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ConversionViewModel @Inject constructor(
    application: Application,
    private val p2PRepository: IP2PRepository
) : AbstractViewModel(application) {

    fun conversion(token: String, conversionRequest: ConversionRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.conversionRequest(token, conversionRequest))
    }
    fun conversionConfirm(token: String, conversionRequest: ConversionRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.conversionConfirmRequest(token, conversionRequest))
    }


}