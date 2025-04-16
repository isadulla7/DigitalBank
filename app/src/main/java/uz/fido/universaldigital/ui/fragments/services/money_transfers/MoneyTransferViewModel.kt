package uz.fido.universaldigital.ui.fragments.services.money_transfers

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.model.money_transfer.create.CreateTransferRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MoneyTransferViewModel @Inject constructor(
    application: Application,
    private val p2PRepository: IP2PRepository
) : AbstractViewModel(application) {

    fun fetchTransferParams(token: String) = liveData(Dispatchers.IO) {
        emit(p2PRepository.fetchTransferParams(token))
    }

    fun fetchTransfersHistory(token: String) = liveData(Dispatchers.IO) {
        emit(p2PRepository.fetchMoneyTransferHistory(token))
    }

    fun createMoneyTransfer(token: String, createTransferRequest: CreateTransferRequest) =
        liveData(Dispatchers.IO) {
            emit(p2PRepository.createMoneyTransfer(token, createTransferRequest))
        }

}