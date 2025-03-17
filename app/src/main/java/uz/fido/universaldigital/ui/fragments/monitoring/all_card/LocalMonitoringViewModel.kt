package uz.fido.universaldigital.ui.fragments.monitoring.all_card

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IMonitoringRepository
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringRequest
import uz.fido.network.domain.model.monitoring.filter.NewFilterMonitoringFilterRequest
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringRequest
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringRequest
import uz.fido.network.domain.model.payment.PrintChequeRequest
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class LocalMonitoringViewModel @Inject constructor(
    application: Application,
    private val monitoringRepository: IMonitoringRepository,
    private val utilsRepository: IUtilsRepository,
    private val paymentRepository: IPaymentRepository
) : AbstractViewModel(application) {


    fun getLocalMonitoring(token: String, localMonitoringRequest: LocalMonitoringRequest) =
        liveData(Dispatchers.IO) {
            emit(monitoringRepository.getLocalMonitoring(token, localMonitoringRequest))
        }

    fun getUzcardMonitoringOld(token: String, uzcardMonitoringRequest: UzcardMonitoringRequest) =
        liveData(Dispatchers.IO) {
            emit(monitoringRepository.getUzcardMonitoringOld(token, uzcardMonitoringRequest))
        }

    fun getHumoMonitoring(token: String, getHumoMonitoringRequest: HumoMonitoringRequest) =
        liveData(Dispatchers.IO) {
            emit(monitoringRepository.getHumoMonitoring(token, getHumoMonitoringRequest))
        }

    fun getAccountHistories(token: String, accountHistoriesRequest: AccountHistoriesRequest) =
        liveData(Dispatchers.IO) {
            emit(monitoringRepository.getAccountHistories(token, accountHistoriesRequest))
        }

    fun getCurrencyCardMonitoring(token: String, monitoringRequest: CurrencyCardMonitoringRequest) =
        liveData(Dispatchers.IO) {
            emit(monitoringRepository.getCurrencyCardMonitoring(token, monitoringRequest))
        }

    fun newFilterLocalMonitoring(
        token: String,
        filerMonitoringRequest: NewFilterMonitoringFilterRequest
    ) = liveData(Dispatchers.IO) {
        emit(monitoringRepository.newFilterLocalMonitoring(token, filerMonitoringRequest))
    }

    fun getSearchData(token: String, searchRequest: GetInfoRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getSearchData(token, searchRequest))
    }

    fun getOperationParams(token: String, getInfoRequest: GetOperationInfoRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.getOperationParams(token, getInfoRequest))
        }

    fun printCheque(token: String, printChequeRequest: PrintChequeRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.printCheque(token, printChequeRequest))
        }

}