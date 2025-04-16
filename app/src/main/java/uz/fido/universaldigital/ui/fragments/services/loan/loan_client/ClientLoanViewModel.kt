package uz.fido.universaldigital.ui.fragments.services.loan.loan_client

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ICreditRepository
import uz.fido.network.domain.datasource.interfaces.IMonitoringRepository
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.model.loans.GetLoanRequest
import uz.fido.network.domain.model.loans.loan_graph.CreditGraphRequest
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ClientLoanViewModel @Inject constructor(
    application: Application,
    private val creditRepository: ICreditRepository,
    private val paymentRepository: IPaymentRepository,
    private val monitoringRepository: IMonitoringRepository
) : AbstractViewModel(application) {

    fun getCreditGraphSecond(token: String, creditGraphRequest: CreditGraphRequest) =
        liveData(Dispatchers.IO) {
            emit(creditRepository.getCreditGraphSecond(token, creditGraphRequest))
        }

    fun getAccountHistories(token: String, accountHistoriesRequest: AccountHistoriesRequest) =
        liveData(Dispatchers.IO) {
            emit(monitoringRepository.getAccountHistories(token, accountHistoriesRequest))
        }

    fun getCreditGraph(token: String, creditGraphRequest: CreditGraphRequest) =
        liveData(Dispatchers.IO) {
            emit(creditRepository.getCreditGraph(token, creditGraphRequest))
        }

    fun loanRepayment(token: String, createPaymentRequest: CreatePaymentRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.loanRepayment(token, createPaymentRequest))
        }

    fun confirmGetLoan(token: String, getLoanRequest: GetLoanRequest) = liveData(Dispatchers.IO) {
        emit(creditRepository.confirmGetLoan(token, getLoanRequest))
    }

}