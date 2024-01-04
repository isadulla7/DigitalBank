package uz.fido.universaldigital.ui.fragments.transfers.confirm_transfer

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ConfirmTransferViewModel @Inject constructor(
    application: Application,
    private val p2PRepository: IP2PRepository
) : AbstractViewModel(application) {

    fun p2pRequest(token: String, p2PRequest: P2PRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.p2p(token, p2PRequest))
    }

}