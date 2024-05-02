package uz.fido.universaldigital.ui.fragments.services.sms_notification

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ICardRepository
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.home.CheckSMSActivateRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectSmsNotificationViewModel @Inject constructor(
    application: Application,
    private val cardRepository: ICardRepository
) : AbstractViewModel(application) {

    fun checkSMSActivate(token: String, request: CheckSMSActivateRequest) =
        liveData(Dispatchers.IO) {
            emit(cardRepository.checkSMSActivate(token, request))
        }

    fun getCardInfo(token: String, checkCardRequestP2p: CheckCardRequestP2p) =
        liveData(Dispatchers.IO) {
            emit(cardRepository.checkCardInfo(token, checkCardRequestP2p))
        }

}