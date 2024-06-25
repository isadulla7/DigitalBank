package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.deposits.CalculateDepositAuto
import uz.fido.network.domain.model.deposits.CreateCreditRequest
import uz.fido.network.domain.model.deposits.DepositCalculatorResponse
import uz.fido.network.domain.model.deposits.DepositListResponse
import uz.fido.network.domain.model.deposits.GetDepositListRequest
import uz.fido.network.domain.model.deposits.RenameDepositRequest
import uz.fido.network.domain.model.deposits.constructor.BxmListResponse
import uz.fido.network.domain.model.deposits.constructor.DepositConstParamsResponse
import uz.fido.network.domain.model.deposits.constructor.DepositConstPercent
import uz.fido.network.domain.model.deposits.constructor.DepositConstPercentRequest
import uz.fido.network.domain.model.deposits.constructor.DepositConstRequest
import uz.fido.network.domain.model.deposits.my_deposit.ClientDepositListResponse
import uz.fido.network.domain.model.deposits.operations.EarlyClosureRequest
import uz.fido.network.domain.model.deposits.operations.EarlyClosureResponse
import uz.fido.network.domain.model.deposits.operations.InvestMoneyToDepositRequest
import uz.fido.network.domain.model.deposits.operations.InvestMoneyToDepositResponse
import uz.fido.network.domain.model.deposits.operations.PartialWithdrawMoneyDepositRequest
import uz.fido.network.domain.model.deposits.operations.PartialWithdrawMoneyDepositResponse

interface IDepositRepository {

    suspend fun getDeposits(
        token: String, getDepositListRequest: GetDepositListRequest
    ): Resource<DepositListResponse>

    suspend fun depositCalculator(
        token: String, calculateDepositAuto: CalculateDepositAuto
    ): Resource<DepositCalculatorResponse>

    suspend fun createDeposit(
        token: String, createCreditRequest: CreateCreditRequest
    ): Resource<BaseResponse>

    suspend fun getClientDepositList(token: String): Resource<ClientDepositListResponse>

    suspend fun partialWithdrawMoney(
        token: String, partialWithdrawMoneyDepositRequest: PartialWithdrawMoneyDepositRequest
    ): Resource<PartialWithdrawMoneyDepositResponse>

    suspend fun investMoneyToDeposit(
        token: String, investMoneyToDepositRequest: InvestMoneyToDepositRequest
    ): Resource<InvestMoneyToDepositResponse>

    suspend fun earlyClosure(
        token: String, earlyClosureRequest: EarlyClosureRequest
    ): Resource<EarlyClosureResponse>

    suspend fun getDCParameters(
        token: String, depositConstRequest: DepositConstRequest
    ): Resource<DepositConstParamsResponse>

    suspend fun getDCPercents(
        token: String, depositConstRequest: DepositConstPercentRequest
    ): Resource<DepositConstPercent>

    suspend fun renameDeposit(
        token: String, request: RenameDepositRequest
    ): Resource<BaseResponse>

    suspend fun calculateDepositAuto(
        token: String, calculateDepositAuto: CalculateDepositAuto
    ): Resource<DepositCalculatorResponse>

    suspend fun closeDeposit(
        token: String, earlyClosureRequest: EarlyClosureRequest
    ): Resource<EarlyClosureResponse>

    suspend fun getBxmList(
        token: String
    ): Resource<BxmListResponse>

}