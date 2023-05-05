package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.repositories.ICreditRepository
import uz.fido.network.domain.datasource.services.CreditApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.client_info.ClientDetailedInfoResponse
import uz.fido.network.domain.model.loans.CheckHasLoanRequest
import uz.fido.network.domain.model.loans.CreateCreditAppRequest
import uz.fido.network.domain.model.loans.calculate_loan_manual.CalcLoanManualRequest
import uz.fido.network.domain.model.loans.calculate_loan_manual.CalcLoanManualResponse
import uz.fido.network.domain.model.loans.create.CreateCreditClaimByCrm
import uz.fido.network.domain.model.loans.create.CreateCreditClaimByCrmResponse
import uz.fido.network.domain.model.loans.create.CreateCreditQuestionRequest
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
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(private val creditService: CreditApiInterface) :
    ICreditRepository {

    override suspend fun createCreditClaimByCrm(
        token: String,
        createCreditClaimByCrm: CreateCreditClaimByCrm
    ): Resource<CreateCreditClaimByCrmResponse> = getResult {
        creditService.createCreditClaimByCrm(token, createCreditClaimByCrm)
    }

    override suspend fun createCreditQuestion(
        token: String,
        createCreditQuestionRequest: CreateCreditQuestionRequest
    ): Resource<BaseResponse> = getResult {
        creditService.createCreditQuestion(token, createCreditQuestionRequest)
    }


    override suspend fun getCreditGroups(token: String): Resource<CreditGroupsResponse> =
        getResult {
            creditService.getCreditGroups(token)
        }


    override suspend fun calcCreditManual(calcLoanManualRequest: CalcLoanManualRequest): Resource<CalcLoanManualResponse> =
        getResult {
            creditService.calcCreditManual(calcLoanManualRequest)
        }


    override suspend fun getAvailableAmount(getAmountRequest: GetAmountRequest): Resource<AvailableAmountResponse> =
        getResult {
            creditService.getAvailableAmount(getAmountRequest)
        }


    override suspend fun getCreditList(creditListRequest: CreditListRequest): Resource<CreditListResponse> =
        getResult {
            creditService.getCreditList(creditListRequest)
        }


    override suspend fun createCreditApplication(
        token: String,
        createCreditApplication: CreateCreditAppRequest
    ): Resource<BaseResponse> = getResult {
        creditService.createCreditApplication(token, createCreditApplication)
    }

    override suspend fun getCreditGraph(
        token: String,
        creditGraphRequest: CreditGraphRequest
    ): Resource<CreditGraphResponse> = getResult {
        creditService.getCreditGraph(token, creditGraphRequest)
    }

    override suspend fun getOnlineOverdraftLimit(
        token: String,
        creditGraphRequest: CreditGraphRequest
    ): Resource<OverdraftGraphResponse> = getResult {
        creditService.getOnlineOverdraftLimit(token, creditGraphRequest)
    }

    override suspend fun getCreditGraphSecond(
        token: String,
        creditGraphRequest: CreditGraphRequest
    ): Resource<CreditActualGraphResponse> = getResult {
        creditService.getCreditGraphSecond(token, creditGraphRequest)
    }


    override suspend fun getCustomerInfo(token: String): Resource<ClientDetailedInfoResponse> =
        getResult {
            creditService.getCustomerInfo(token)
        }


    override suspend fun getLoanParams(token: String): Resource<LoanParamsResponse> = getResult {
        creditService.getLoanParams(token)
    }


    override suspend fun getCreditProducts(token: String): Resource<CreditProductsResponse> =
        getResult {
            creditService.getCreditProducts(token)
        }


    override suspend fun checkLoanByCrm(
        token: String,
        request: CheckHasLoanRequest
    ): Resource<BaseResponse> = getResult {
        creditService.checkLoanByCrm(token, request)
    }


}