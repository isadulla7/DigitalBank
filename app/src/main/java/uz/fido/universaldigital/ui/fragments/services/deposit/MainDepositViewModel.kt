package uz.fido.universaldigital.ui.fragments.services.deposit

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IDepositRepository
import uz.fido.network.domain.model.deposits.CalculateDepositAuto
import uz.fido.network.domain.model.deposits.CreateCreditRequest
import uz.fido.network.domain.model.deposits.GetDepositListRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MainDepositViewModel @Inject constructor(
    application: Application,
    private val depositRepository: IDepositRepository
) : AbstractViewModel(application) {


    fun getDeposits(token: String, getDepositListRequest: GetDepositListRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(depositRepository.getDeposits(token, getDepositListRequest))
    }


    fun createDeposit(token: String, createCreditRequest: CreateCreditRequest) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.createDeposit(token, createCreditRequest))
        }

    fun calculateDepositAuto(token: String, calculateDepositAuto: CalculateDepositAuto) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.calculateDepositAuto(token, calculateDepositAuto))
        }
}