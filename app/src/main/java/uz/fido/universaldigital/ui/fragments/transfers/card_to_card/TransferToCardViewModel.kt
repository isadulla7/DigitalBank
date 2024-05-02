package uz.fido.universaldigital.ui.fragments.transfers.card_to_card

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.model.p2p.P2PHistoryRequest
import uz.fido.network.domain.model.p2p.SetPopularityRequest
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

@HiltViewModel
class TransferToCardViewModel @Inject constructor(
    application: Application,
    private val p2PRepository: IP2PRepository
) : AbstractViewModel(application) {

    fun getP2PHistory(token: String, p2PHistoryRequest: P2PHistoryRequest) =
        liveData(Dispatchers.IO) {
            emit(p2PRepository.getP2pHistory(token, p2PHistoryRequest))
        }

    fun getPopularTransferList(token: String) = liveData(Dispatchers.IO) {
        emit(p2PRepository.getPopularTransferList(token))
    }

    fun setToFavoriteTransfer(objectValue: String) = liveData(Dispatchers.IO) {
        emit(
            p2PRepository.setToPopularTransfer(
                getClientToken(),
                SetPopularityRequest(to_object_value = objectValue)
            )
        )
    }

    fun setToNonFavoriteTransfer(objectValue: String) = liveData(Dispatchers.IO) {
        emit(
            p2PRepository.setToNonPopularTransfer(
                getClientToken(),
                SetPopularityRequest(to_object_value = objectValue)
            )
        )
    }
}