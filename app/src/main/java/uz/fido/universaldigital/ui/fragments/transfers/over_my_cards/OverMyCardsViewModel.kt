package uz.fido.universaldigital.ui.fragments.transfers.over_my_cards

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class OverMyCardsViewModel @Inject constructor(
    application: Application,
    private val p2PRepository: IP2PRepository
) : AbstractViewModel(application) {

    fun p2pInfoRequest(token: String, p2PInfoRequest: P2PInfoRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.p2pInfo(token, p2PInfoRequest))
    }

    fun p2pRequest(token: String, p2PRequest: P2PRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.p2p(token, p2PRequest))
    }

}