package uz.fido.network.domain.datasource.services

import uz.fido.network.domain.model.deposits.*
import uz.fido.network.domain.model.deposits.constructor.DepositConstParamsResponse
import uz.fido.network.domain.model.deposits.constructor.DepositConstPercent
import uz.fido.network.domain.model.deposits.constructor.DepositConstPercentRequest
import uz.fido.network.domain.model.deposits.constructor.DepositConstRequest
import uz.fido.network.domain.model.deposits.my_deposit.ClientDepositListResponse
import uz.fido.network.domain.model.deposits.operations.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse

interface DepositApiInterface {

    @POST("GET_IBS_DEPOSIT_TYPES")
    suspend fun getDeposits(
        @Header("Authorization") token: String,
        @Body getDepositListRequest: GetDepositListRequest
    ): DepositListResponse

    @POST("DEPOSIT_CALCULATOR")
    suspend fun depositCalculator(
        @Header("Authorization") token: String,
        @Body calculateDepositAuto: CalculateDepositAuto
    ): DepositCalculatorResponse

    @POST("OPEN_DEPOSIT")
    suspend fun createDeposit(
        @Header("Authorization") token: String,
        @Body createCreditRequest: CreateCreditRequest
    ): BaseResponse

    @GET("GET_CLIENT_DEPOSIT_LIST")
    suspend fun getClientDepositList(
        @Header("Authorization") token: String
    ): ClientDepositListResponse

    @POST("PARTIAL_WITH_DRAWAL_MONEY")
    suspend fun partialWithdrawMoney(
        @Header("Authorization") token: String,
        @Body partialWithdrawMoneyDepositRequest: PartialWithdrawMoneyDepositRequest
    ): PartialWithdrawMoneyDepositResponse

    @POST("INVESTING_MONEY_IN_DEPOSIT")
    suspend fun investMoneyToDeposit(
        @Header("Authorization") token: String,
        @Body investMoneyToDepositRequest: InvestMoneyToDepositRequest
    ): InvestMoneyToDepositResponse

    @POST("EARLY_CLOSURE_DEPOSIT")
    suspend fun earlyClosure(
        @Header("Authorization") token: String,
        @Body earlyClosureRequest: EarlyClosureRequest
    ): EarlyClosureResponse

    @POST("GET_DC_PARAMETERS")
    suspend fun getDCParameters(
        @Header("Authorization") token: String,
        @Body depositConstRequest: DepositConstRequest
    ): DepositConstParamsResponse

    @POST("IBS_DEP_GET_DC_PERCENTS")
    suspend fun getDCPercents(
        @Header("Authorization") token: String,
        @Body depositConstRequest: DepositConstPercentRequest
    ): DepositConstPercent

    @POST("EDIT_DEPOSIT_NAME")
    suspend fun renameDeposit(
        @Header("Authorization") token: String,
        @Body request: RenameDepositRequest
    ): BaseResponse

}