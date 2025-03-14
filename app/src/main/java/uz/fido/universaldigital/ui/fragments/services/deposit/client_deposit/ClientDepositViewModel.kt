package uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IDepositRepository
import uz.fido.network.domain.datasource.interfaces.IMonitoringRepository
import uz.fido.network.domain.model.deposits.RenameDepositRequest
import uz.fido.network.domain.model.deposits.operations.EarlyClosureRequest
import uz.fido.network.domain.model.deposits.operations.InvestMoneyToDepositRequest
import uz.fido.network.domain.model.deposits.operations.PartialWithdrawMoneyDepositRequest
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ClientDepositViewModel @Inject constructor(
    application: Application,
    private val monitoringRepository: IMonitoringRepository,
    private val depositRepository: IDepositRepository
) : AbstractViewModel(application) {


    fun getAccountHistories(token: String, accountHistoriesRequest: AccountHistoriesRequest) =
        liveData(
            Dispatchers.IO
        ) {
            emit(monitoringRepository.getAccountHistories(token, accountHistoriesRequest))
        }

    fun renameDeposit(token: String, renameDepositRequest: RenameDepositRequest) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.renameDeposit(token, renameDepositRequest))
        }

    fun getClientDepositList(token: String) = liveData(Dispatchers.IO) {
        emit(depositRepository.getClientDepositList(token))
    }

    fun investMoney(token: String, investMoneyToDepositRequest: InvestMoneyToDepositRequest) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.investMoneyToDeposit(token, investMoneyToDepositRequest))
        }

    fun earlyCloseDeposit(token: String, earlyClosureRequest: EarlyClosureRequest) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.earlyClosure(token, earlyClosureRequest))
        }

    fun closeDeposit(token: String, earlyClosureRequest: EarlyClosureRequest) =
        liveData(Dispatchers.IO) {
            emit(depositRepository.closeDeposit(token, earlyClosureRequest))
        }

    fun partialWithDraw(
        token: String,
        partialWithdrawMoneyDepositRequest: PartialWithdrawMoneyDepositRequest
    ) = liveData(Dispatchers.IO) {
        emit(depositRepository.partialWithdrawMoney(token, partialWithdrawMoneyDepositRequest))
    }
}