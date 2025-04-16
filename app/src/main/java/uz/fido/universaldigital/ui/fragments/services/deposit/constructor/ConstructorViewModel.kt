package uz.fido.universaldigital.ui.fragments.services.deposit.constructor

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IDepositRepository
import uz.fido.network.domain.model.deposits.CreateCreditRequest
import uz.fido.network.domain.model.deposits.constructor.DepositConstPercentRequest
import uz.fido.network.domain.model.deposits.constructor.DepositConstRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ConstructorViewModel @Inject constructor(
    application: Application,
    private val depositRepository: IDepositRepository
) : AbstractViewModel(application) {


    fun getDepositConstPercents(token: String, depositConstRequest: DepositConstPercentRequest) =
        liveData(
            Dispatchers.IO
        ) {
            emit(depositRepository.getDCPercents(token, depositConstRequest))
        }

    fun getDepositConstParams(token: String, depositConstRequest: DepositConstRequest) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.getDCParameters(token, depositConstRequest))
        }

    fun createDeposit(token: String, createCreditRequest: CreateCreditRequest) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.createDeposit(token, createCreditRequest))
        }
}