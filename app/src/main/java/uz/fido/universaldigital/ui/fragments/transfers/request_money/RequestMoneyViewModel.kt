package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.model.amount_requests.RmCreateRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class RequestMoneyViewModel @Inject constructor(
    application: Application,
    private val p2pRepository: IP2PRepository
) : AbstractViewModel(application) {

    fun createRm(token: String, createRmRequest: RmCreateRequest) = liveData(Dispatchers.IO) {
        emit(p2pRepository.rmCreate(token, createRmRequest))
    }

}