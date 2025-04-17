package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.client_info.ClientDetailedInfoResponse
import uz.fido.network.domain.model.loans.CheckHasLoanRequest
import uz.fido.network.domain.model.loans.CreateCreditAppRequest
import uz.fido.network.domain.model.loans.CreateCreditRequestNew
import uz.fido.network.domain.model.loans.GetLoanRequest
import uz.fido.network.domain.model.loans.calculate_loan_manual.CalcLoanManualRequest
import uz.fido.network.domain.model.loans.calculate_loan_manual.CalcLoanManualResponse
import uz.fido.network.domain.model.loans.loan_available_amount.AvailableAmountResponse
import uz.fido.network.domain.model.loans.loan_available_amount.GetAmountRequest
import uz.fido.network.domain.model.loans.loan_graph.CreditActualGraphResponse
import uz.fido.network.domain.model.loans.loan_graph.CreditGraphRequest
import uz.fido.network.domain.model.loans.loan_graph.CreditGraphResponse
import uz.fido.network.domain.model.loans.loan_groups.CreditGroupsResponse
import uz.fido.network.domain.model.loans.loan_params.LoanParamsResponse
import uz.fido.network.domain.model.loans.loan_products.CreditProductsResponse
import uz.fido.network.domain.model.loans.my_loans.CreditListRequest
import uz.fido.network.domain.model.loans.my_loans.CreditListResponse
import uz.fido.network.domain.model.loans.overdraft.OverdraftGraphResponse

interface ICreditRepository {

    suspend fun getCreditGroups(token: String): Resource<CreditGroupsResponse>

    suspend fun calcCreditManual(calcLoanManualRequest: CalcLoanManualRequest): Resource<CalcLoanManualResponse>

    suspend fun getAvailableAmount(getAmountRequest: GetAmountRequest): Resource<AvailableAmountResponse>

    suspend fun getCreditList(creditListRequest: CreditListRequest): Resource<CreditListResponse>

    suspend fun createCreditApplication(
        token: String,
        createCreditApplication: CreateCreditAppRequest
    ): Resource<BaseResponse>

    suspend fun getCreditGraph(
        token: String,
        creditGraphRequest: CreditGraphRequest
    ): Resource<CreditGraphResponse>

    suspend fun getOnlineOverdraftLimit(
        token: String,
        creditGraphRequest: CreditGraphRequest
    ): Resource<OverdraftGraphResponse>

    suspend fun getCreditGraphSecond(
        token: String,
        creditGraphRequest: CreditGraphRequest
    ): Resource<CreditActualGraphResponse>


    suspend fun getCustomerInfo(token: String): Resource<ClientDetailedInfoResponse>

    suspend fun getLoanParams(token: String): Resource<LoanParamsResponse>

    suspend fun getCreditProducts(token: String): Resource<CreditProductsResponse>

    suspend fun checkLoanByCrm(token: String, request: CheckHasLoanRequest): Resource<BaseResponse>

    suspend fun getClientCreditList(token: String): Resource<CreditProductsResponse>

    suspend fun createCreditRequest(
        token: String,
        createCreditAppRequest: CreateCreditRequestNew
    ): Resource<BaseResponse>

    suspend fun confirmGetLoan(
        token: String,
        getLoanRequest: GetLoanRequest
    ): Resource<BaseResponse>

    suspend fun checkHasLoan(
        token: String,
        request: CheckHasLoanRequest
    ): Resource<BaseResponse>

    suspend fun getUserInfo(token: String): Resource<ClientDetailedInfoResponse>
}