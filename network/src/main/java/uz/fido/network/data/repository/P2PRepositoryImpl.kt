package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.repositories.IP2PRepository
import uz.fido.network.domain.datasource.services.P2PApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.amount_requests.RmCreateRequest
import uz.fido.network.domain.model.amount_requests.RmGetAllowedSumRequest
import uz.fido.network.domain.model.amount_requests.RmGetAllowedSumResponse
import uz.fido.network.domain.model.amount_requests.RmGetListResponse
import uz.fido.network.domain.model.amount_requests.RmGetTransfersRequest
import uz.fido.network.domain.model.amount_requests.RmGetTransfersResponse
import uz.fido.network.domain.model.amount_requests.RmSetStateRequest
import uz.fido.network.domain.model.amount_requests.RmTransferRequest
import uz.fido.network.domain.model.amount_requests.RmTransferResponse
import uz.fido.network.domain.model.collect_split_money.CMChangeCardNumberRequest
import uz.fido.network.domain.model.collect_split_money.CMSetStateRequest
import uz.fido.network.domain.model.collect_split_money.CMUserTransferResponse
import uz.fido.network.domain.model.collect_split_money.CmAddUsersRequest
import uz.fido.network.domain.model.collect_split_money.CmCreateResponse
import uz.fido.network.domain.model.collect_split_money.CmUserProductsResponse
import uz.fido.network.domain.model.collect_split_money.CollectMoneyAddUserRequest
import uz.fido.network.domain.model.collect_split_money.CollectMoneyBindProducts
import uz.fido.network.domain.model.collect_split_money.CollectMoneyCreateRequest
import uz.fido.network.domain.model.collect_split_money.CollectMoneyDeleteUser
import uz.fido.network.domain.model.collect_split_money.CollectMoneyDetailsResponse
import uz.fido.network.domain.model.collect_split_money.CollectMoneyEditRequest
import uz.fido.network.domain.model.collect_split_money.CollectMoneyGetUserTransfers
import uz.fido.network.domain.model.collect_split_money.CollectMoneyListDetailsRequest
import uz.fido.network.domain.model.collect_split_money.CollectMoneyListResponse
import uz.fido.network.domain.model.collect_split_money.CollectMoneyManualTransfer
import uz.fido.network.domain.model.collect_split_money.CollectMoneyMenuDetete
import uz.fido.network.domain.model.collect_split_money.CollectMoneyMenuResponse
import uz.fido.network.domain.model.collect_split_money.CollectMoneyTransferRequest
import uz.fido.network.domain.model.collect_split_money.CollectMoneyUserTransferRequest
import uz.fido.network.domain.model.conversion.ConversionRequest
import uz.fido.network.domain.model.money_transfer.create.CreateTransferRequest
import uz.fido.network.domain.model.money_transfer.list.MoneyTransferHistoryResponse
import uz.fido.network.domain.model.money_transfer.receive.MoneyTransferParamsResponse
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.P2PInfoResponse
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.p2p.P2PResponse
import uz.fido.network.domain.model.popular_transfers.DeletePopularTransferRequest
import uz.fido.network.domain.model.popular_transfers.PopularTransferResponse
import uz.fido.network.domain.model.popular_transfers.SaveToPopularTransferRequest
import javax.inject.Inject

class P2PRepositoryImpl @Inject constructor(private val p2pService: P2PApiInterface) :
    IP2PRepository {
    override suspend fun getPopularTransferList(token: String): Resource<PopularTransferResponse> =
        getResult {
            p2pService.getPopularTransferList(token)
        }

    override suspend fun deletePopularTransferItem(
        token: String,
        deletePopularTransferRequest: DeletePopularTransferRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.deletePopularTransferItem(token, deletePopularTransferRequest)
    }

    override suspend fun p2pInfo(
        token: String,
        p2PInfoRequest: P2PInfoRequest
    ): Resource<P2PInfoResponse> =
        getResult {
            p2pService.p2pInfo(token, p2PInfoRequest)
        }


    override suspend fun p2p(token: String, p2PRequest: P2PRequest): Resource<P2PResponse> =
        getResult {
            p2pService.p2p(token, p2PRequest)
        }


    override suspend fun closeTarget(token: String, p2PRequest: P2PRequest): Resource<P2PResponse> =
        getResult {
            p2pService.closeTarget(token, p2PRequest)
        }


    override suspend fun targetTransfer(
        token: String,
        p2PRequest: P2PRequest
    ): Resource<P2PResponse> =
        getResult {
            p2pService.targetTransfer(token, p2PRequest)
        }


    override suspend fun fetchTransferParams(token: String): Resource<MoneyTransferParamsResponse> =
        getResult {
            p2pService.fetchTransferParams(token)
        }


    override suspend fun fetchMoneyTransferHistory(token: String): Resource<MoneyTransferHistoryResponse> =
        getResult {
            p2pService.fetchMoneyTransferHistory(token)
        }


    override suspend fun createMoneyTransfer(
        token: String,
        createTransferRequest: CreateTransferRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.createMoneyTransfer(token, createTransferRequest)
    }

    override suspend fun saveToPopularTransfer(
        token: String,
        saveToPopularTransfer: SaveToPopularTransferRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.saveToPopularTransfer(token, saveToPopularTransfer)
    }

    override suspend fun conversionRequest(
        token: String,
        conversionRequest: ConversionRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.conversionRequest(token, conversionRequest)
    }

    override suspend fun rmGetLis(token: String): Resource<RmGetListResponse> = getResult {
        p2pService.rmGetList(token)
    }

    override suspend fun rmCreate(
        token: String,
        rmCreateRequest: RmCreateRequest
    ): Resource<CmCreateResponse> = getResult {
        p2pService.rmCreate(token, rmCreateRequest)
    }

    override suspend fun rmGetTransfers(
        token: String,
        rmGetTransfersRequest: RmGetTransfersRequest
    ): Resource<RmGetTransfersResponse> = getResult {
        p2pService.rmGetTransfers(token, rmGetTransfersRequest)
    }

    override suspend fun rmTransfer(
        token: String,
        rmTransferRequest: RmTransferRequest
    ): Resource<RmTransferResponse> = getResult {
        p2pService.rmTransfer(token, rmTransferRequest)
    }

    override suspend fun getRmAllowedSum(
        token: String,
        getAllowedSumRequest: RmGetAllowedSumRequest
    ): Resource<RmGetAllowedSumResponse> = getResult {
        p2pService.getRmAllowedSum(token, getAllowedSumRequest)
    }

    override suspend fun setState(
        token: String,
        setStateRequest: RmSetStateRequest
    ): Resource<BaseResponse> =
        getResult {
            p2pService.setState(token, setStateRequest)
        }


    override suspend fun deleteRequestMoney(
        token: String,
        rmGetTransfersRequest: RmGetTransfersRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.deleteRequestMoney(token, rmGetTransfersRequest)
    }

    override suspend fun collectMoneyList(token: String): Resource<CollectMoneyListResponse> =
        getResult {
            p2pService.collectMoneyList(token)
        }


    override suspend fun getCollectMoneyListDetails(
        token: String,
        collectMoneyListDetailsRequest: CollectMoneyListDetailsRequest
    ): Resource<CollectMoneyDetailsResponse> = getResult {
        p2pService.getCollectMoneyListDetails(token, collectMoneyListDetailsRequest)
    }


    override suspend fun createCollectMoney(
        token: String,
        collectMoneyCreateRequest: CollectMoneyCreateRequest
    ): Resource<CmCreateResponse> = getResult {
        p2pService.createCollectMoney(token, collectMoneyCreateRequest)
    }

    override suspend fun getCollectMoneyUserTransfer(
        token: String,
        collectMoneyUserTransferRequest: CollectMoneyUserTransferRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.getCollectMoneyUserTransfer(token, collectMoneyUserTransferRequest)
    }

    override suspend fun collectMoneyTransfer(
        token: String,
        collectMoneyTransferRequest: CollectMoneyTransferRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyTransfer(token, collectMoneyTransferRequest)
    }

    override suspend fun collectMoneyManualTransfer(
        token: String,
        collectMoneyManualTransfer: CollectMoneyManualTransfer
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyManualTransfer(token, collectMoneyManualTransfer)
    }

    override suspend fun collectMoneyAddUsers(
        token: String,
        collectMoneyAddUserRequest: CmAddUsersRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyAddUsers(token, collectMoneyAddUserRequest)
    }

    override suspend fun collectMoneyAddUser(
        token: String,
        collectMoneyAddUserRequest: CollectMoneyAddUserRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyAddUser(token, collectMoneyAddUserRequest)
    }

    override suspend fun getCmAllowedSum(
        token: String,
        getAllowedSumRequest: RmGetAllowedSumRequest
    ): Resource<RmGetAllowedSumResponse> = getResult {
        p2pService.getCmAllowedSum(token, getAllowedSumRequest)
    }

    override suspend fun getCollectMoneyUserTransfers(
        token: String,
        collectMoneyGetUserTransfers: CollectMoneyGetUserTransfers
    ): Resource<CMUserTransferResponse> = getResult {
        p2pService.getCollectMoneyUserTransfers(token, collectMoneyGetUserTransfers)
    }


    override suspend fun collectMoneySetState(
        token: String,
        cmSetStateRequest: CMSetStateRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneySetState(token, cmSetStateRequest)
    }

    override suspend fun collectMoneyDelete(
        token: String,
        cmSetStateRequest: CMSetStateRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyDelete(token, cmSetStateRequest)
    }

    override suspend fun collectMoneyChangeCardNumber(
        token: String,
        cmChangeCardNumberRequest: CMChangeCardNumberRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyChangeCardNumber(token, cmChangeCardNumberRequest)
    }

    override suspend fun collectMoneyGetMenu(
        token: String,
        cmSetStateRequest: CMSetStateRequest
    ): Resource<CollectMoneyMenuResponse> = getResult {
        p2pService.collectMoneyGetMenu(token, cmSetStateRequest)
    }

    override suspend fun editCollectMoney(
        token: String,
        collectMoneyEditRequest: CollectMoneyEditRequest
    ): Resource<BaseResponse> = getResult {
        p2pService.editCollectMoney(token, collectMoneyEditRequest)
    }

    override suspend fun deleteCollectMoneyMenu(
        token: String,
        collectMoneyMenuDelete: CollectMoneyMenuDetete
    ): Resource<BaseResponse> = getResult {
        p2pService.deleteCollectMoneyMenu(token, collectMoneyMenuDelete)
    }

    override suspend fun collectMoneyDeleteUser(
        token: String,
        collectMoneyDeleteUser: CollectMoneyDeleteUser
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyDeleteUser(token, collectMoneyDeleteUser)
    }

    override suspend fun collectMoneyGetUserProducts(
        token: String,
        collectMoneyDeleteUser: CollectMoneyDeleteUser
    ): Resource<CmUserProductsResponse> = getResult {
        p2pService.collectMoneyGetUserProducts(token, collectMoneyDeleteUser)
    }


    override suspend fun collectMoneyBindProducts(
        token: String,
        collectMoneyBindProducts: CollectMoneyBindProducts
    ): Resource<BaseResponse> = getResult {
        p2pService.collectMoneyBindProducts(token, collectMoneyBindProducts)
    }
}