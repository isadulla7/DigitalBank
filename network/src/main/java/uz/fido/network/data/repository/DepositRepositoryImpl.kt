package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IDepositRepository
import uz.fido.network.domain.datasource.services.DepositApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.deposits.CalculateDepositAuto
import uz.fido.network.domain.model.deposits.CreateCreditRequest
import uz.fido.network.domain.model.deposits.DepositCalculatorResponse
import uz.fido.network.domain.model.deposits.DepositListResponse
import uz.fido.network.domain.model.deposits.GetDepositListRequest
import uz.fido.network.domain.model.deposits.RenameDepositRequest
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
import javax.inject.Inject

class DepositRepositoryImpl @Inject constructor(private val depositService: DepositApiInterface) :
    IDepositRepository {

    override suspend fun getDeposits(
        token: String,
        getDepositListRequest: GetDepositListRequest
    ): Resource<DepositListResponse> = getResult {
        depositService.getDeposits(token, getDepositListRequest)
    }

    override suspend fun depositCalculator(
        token: String,
        calculateDepositAuto: CalculateDepositAuto
    ): Resource<DepositCalculatorResponse> = getResult {
        depositService.depositCalculator(token, calculateDepositAuto)
    }

    override suspend fun createDeposit(
        token: String,
        createCreditRequest: CreateCreditRequest
    ): Resource<BaseResponse> = getResult {
        depositService.createDeposit(token, createCreditRequest)
    }

    override suspend fun getClientDepositList(token: String): Resource<ClientDepositListResponse> =
        getResult {
            depositService.getClientDepositList(token)
        }


    override suspend fun partialWithdrawMoney(
        token: String,
        partialWithdrawMoneyDepositRequest: PartialWithdrawMoneyDepositRequest
    ): Resource<PartialWithdrawMoneyDepositResponse> = getResult {
        depositService.partialWithdrawMoney(token, partialWithdrawMoneyDepositRequest)
    }


    override suspend fun investMoneyToDeposit(
        token: String,
        investMoneyToDepositRequest: InvestMoneyToDepositRequest
    ): Resource<InvestMoneyToDepositResponse> = getResult {
        depositService.investMoneyToDeposit(token, investMoneyToDepositRequest)
    }


    override suspend fun earlyClosure(
        token: String,
        earlyClosureRequest: EarlyClosureRequest
    ): Resource<EarlyClosureResponse> = getResult {
        depositService.earlyClosure(token, earlyClosureRequest)
    }

    override suspend fun getDCParameters(
        token: String,
        depositConstRequest: DepositConstRequest
    ): Resource<DepositConstParamsResponse> = getResult {
        depositService.getDCParameters(token, depositConstRequest)
    }

    override suspend fun getDCPercents(
        token: String,
        depositConstRequest: DepositConstPercentRequest
    ): Resource<DepositConstPercent> = getResult {
        depositService.getDCPercents(token, depositConstRequest)
    }

    override suspend fun renameDeposit(
        token: String,
        request: RenameDepositRequest
    ): Resource<BaseResponse> =
        getResult {
            depositService.renameDeposit(token, request)
        }


    override suspend fun calculateDepositAuto(
        token: String,
        calculateDepositAuto: CalculateDepositAuto
    ): Resource<DepositCalculatorResponse> {
        return getResult { depositService.depositCalculator(token, calculateDepositAuto) }
    }

    override suspend fun closeDeposit(
        token: String,
        earlyClosureRequest: EarlyClosureRequest
    ): Resource<EarlyClosureResponse> =
        getResult { depositService.closeDeposit(token, earlyClosureRequest) }
}