package uz.fido.universaldigital.ui.fragments.monitoring.filter

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ICardRepository
import uz.fido.network.domain.datasource.interfaces.IMonitoringRepository
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MonitoringFilterViewModel @Inject constructor(
    application: Application,
    private val monitoringRepository: IMonitoringRepository,
    private val cardRepository: ICardRepository
) : AbstractViewModel(application) {


    fun getLocalMonitoringCardList(token: String) = liveData(Dispatchers.IO) {
        emit(monitoringRepository.getLocalMonitoringCardList(token))
    }

    fun getLocalMonitoringServiceList(token: String) = liveData(Dispatchers.IO) {
        emit(monitoringRepository.getLocalMonitoringServiceList(token))
    }

    fun getCardInfo(token: String, checkCardRequestP2p: CheckCardRequestP2p) = liveData(Dispatchers.IO) {
        emit(cardRepository.checkCardInfo(token, checkCardRequestP2p))
    }
}