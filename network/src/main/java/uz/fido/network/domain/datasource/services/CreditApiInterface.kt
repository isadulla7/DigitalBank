package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

interface CreditApiInterface {

    @GET("GET_LOAN_PRODUCTS")
    suspend fun getCreditGroups(@Header("Authorization") token: String): CreditGroupsResponse

    @POST("IBS_CALC_CREDIT_MANUAL")
    suspend fun calcCreditManual(@Body calcLoanManualRequest: CalcLoanManualRequest): CalcLoanManualResponse

    @POST("GET_IBS_CREDIT_AVAILABLE_AMOUNT")
    suspend fun getAvailableAmount(@Body getAmountRequest: GetAmountRequest): AvailableAmountResponse

    @POST("GET_IBS_CREDIT_LIST")
    suspend fun getCreditList(@Body creditListRequest: CreditListRequest): CreditListResponse

    @POST("LN_CREATE_LOAN_APPLICATION")
    suspend fun createCreditApplication(
        @Header("Authorization") token: String,
        @Body createCreditApplication: CreateCreditAppRequest
    ): BaseResponse

    @POST("LN_LOAN_REPAYMENT_SCHEDULE")
    suspend fun getCreditGraph(
        @Header("Authorization") token: String,
        @Body creditGraphRequest: CreditGraphRequest
    ): CreditGraphResponse

    @POST("GET_ONLINE_OVERDRAFT_LIMIT")
    suspend fun getOnlineOverdraftLimit(
        @Header("Authorization") token: String,
        @Body creditGraphRequest: CreditGraphRequest
    ): OverdraftGraphResponse

    @POST("LN_GET_INFORMATION_OF_LOANS")
    suspend fun getCreditGraphSecond(
        @Header("Authorization") token: String,
        @Body creditGraphRequest: CreditGraphRequest
    ): CreditActualGraphResponse

    @GET("GET_IABS_CUSTOMER_INFO")
    suspend fun getCustomerInfo(
        @Header("Authorization") token: String
    ): ClientDetailedInfoResponse

    @GET("LN_LOAN_PARAMS")
    suspend fun getLoanParams(
        @Header("Authorization") token: String
    ): LoanParamsResponse

    @GET("GET_CLIENT_CREDIT_LIST")
    suspend fun getCreditProducts(@Header("Authorization") token: String): CreditProductsResponse

    @POST("LN_CHECK_HAS_LOANS_BY_CRM")
    suspend fun checkLoanByCrm(
        @Header("Authorization") token: String,
        @Body request: CheckHasLoanRequest
    ): BaseResponse

    @POST("LN_GETTING_LOAN")
    suspend fun createCreditRequest(
        @Header("Authorization") token: String,
        @Body createCreditApplication: CreateCreditRequestNew
    ): BaseResponse

    @POST("LN_LOAN_ISSUANCE")
    suspend fun confirmGetLoan(
        @Header("Authorization") token: String, @Body request: GetLoanRequest
    ): BaseResponse

}