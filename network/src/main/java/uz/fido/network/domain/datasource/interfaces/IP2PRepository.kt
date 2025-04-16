package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
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
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
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
import uz.fido.network.domain.model.p2p.P2PHistoryRequest
import uz.fido.network.domain.model.p2p.P2PHistoryResponse
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.P2PInfoResponse
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.p2p.P2PResponse
import uz.fido.network.domain.model.p2p.SetPopularityRequest
import uz.fido.network.domain.model.popular_transfers.DeletePopularTransferRequest
import uz.fido.network.domain.model.popular_transfers.PopularTransferResponse
import uz.fido.network.domain.model.popular_transfers.SaveToPopularTransferRequest

interface IP2PRepository {

    suspend fun checkCardInfo(
        token: String,
        checkCardRequestP2p: CheckCardRequestP2p
    ): Resource<CheckCardResponse>

    suspend fun getP2pHistory(
        token: String,
        p2PHistoryRequest: P2PHistoryRequest
    ): Resource<P2PHistoryResponse>

    suspend fun getPopularTransferList(
        token: String
    ): Resource<PopularTransferResponse>

    suspend fun deletePopularTransferItem(
        token: String, deletePopularTransferRequest: DeletePopularTransferRequest
    ): Resource<BaseResponse>

    suspend fun p2pInfo(
        token: String, p2PInfoRequest: P2PInfoRequest
    ): Resource<P2PInfoResponse>

    suspend fun p2p(
        token: String, p2PRequest: P2PRequest
    ): Resource<P2PResponse>

    suspend fun closeTarget(
        token: String, p2PRequest: P2PRequest
    ): Resource<P2PResponse>

    suspend fun targetTransfer(
        token: String, p2PRequest: P2PRequest
    ): Resource<P2PResponse>

    suspend fun fetchTransferParams(
        token: String
    ): Resource<MoneyTransferParamsResponse>

    suspend fun fetchMoneyTransferHistory(
        token: String
    ): Resource<MoneyTransferHistoryResponse>

    suspend fun createMoneyTransfer(
        token: String, createTransferRequest: CreateTransferRequest
    ): Resource<BaseResponse>

    suspend fun saveToPopularTransfer(
        token: String, saveToPopularTransfer: SaveToPopularTransferRequest
    ): Resource<BaseResponse>

    suspend fun conversionRequest(
        token: String, conversionRequest: ConversionRequest
    ): Resource<BaseResponse>

    suspend fun rmGetLis(token: String): Resource<RmGetListResponse>

    suspend fun rmCreate(
        token: String, rmCreateRequest: RmCreateRequest
    ): Resource<CmCreateResponse>

    suspend fun rmGetTransfers(
        token: String, rmGetTransfersRequest: RmGetTransfersRequest
    ): Resource<RmGetTransfersResponse>

    suspend fun rmTransfer(
        token: String, rmTransferRequest: RmTransferRequest
    ): Resource<RmTransferResponse>

    suspend fun getRmAllowedSum(
        token: String, getAllowedSumRequest: RmGetAllowedSumRequest
    ): Resource<RmGetAllowedSumResponse>

    suspend fun setState(
        token: String, setStateRequest: RmSetStateRequest
    ): Resource<BaseResponse>

    suspend fun deleteRequestMoney(
        token: String, rmGetTransfersRequest: RmGetTransfersRequest
    ): Resource<BaseResponse>

    suspend fun collectMoneyList(
        token: String
    ): Resource<CollectMoneyListResponse>

    suspend fun getCollectMoneyListDetails(
        token: String, collectMoneyListDetailsRequest: CollectMoneyListDetailsRequest
    ): Resource<CollectMoneyDetailsResponse>

    suspend fun createCollectMoney(
        token: String, collectMoneyCreateRequest: CollectMoneyCreateRequest
    ): Resource<CmCreateResponse>

    suspend fun getCollectMoneyUserTransfer(
        token: String, collectMoneyUserTransferRequest: CollectMoneyUserTransferRequest
    ): Resource<BaseResponse>

    suspend fun collectMoneyTransfer(
        token: String, collectMoneyTransferRequest: CollectMoneyTransferRequest
    ): Resource<BaseResponse>

    suspend fun collectMoneyManualTransfer(
        token: String, collectMoneyManualTransfer: CollectMoneyManualTransfer
    ): Resource<BaseResponse>

    suspend fun collectMoneyAddUsers(
        token: String, collectMoneyAddUserRequest: CmAddUsersRequest
    ): Resource<BaseResponse>

    suspend fun collectMoneyAddUser(
        token: String, collectMoneyAddUserRequest: CollectMoneyAddUserRequest
    ): Resource<BaseResponse>

    suspend fun getCmAllowedSum(
        token: String, getAllowedSumRequest: RmGetAllowedSumRequest
    ): Resource<RmGetAllowedSumResponse>

    suspend fun getCollectMoneyUserTransfers(
        token: String, collectMoneyGetUserTransfers: CollectMoneyGetUserTransfers
    ): Resource<CMUserTransferResponse>

    suspend fun collectMoneySetState(
        token: String, cmSetStateRequest: CMSetStateRequest
    ): Resource<BaseResponse>

    suspend fun collectMoneyDelete(
        token: String, cmSetStateRequest: CMSetStateRequest
    ): Resource<BaseResponse>

    suspend fun collectMoneyChangeCardNumber(
        token: String, cmChangeCardNumberRequest: CMChangeCardNumberRequest
    ): Resource<BaseResponse>

    suspend fun collectMoneyGetMenu(
        token: String, cmSetStateRequest: CMSetStateRequest
    ): Resource<CollectMoneyMenuResponse>

    suspend fun editCollectMoney(
        token: String, collectMoneyEditRequest: CollectMoneyEditRequest
    ): Resource<BaseResponse>

    suspend fun deleteCollectMoneyMenu(
        token: String, collectMoneyMenuDelete: CollectMoneyMenuDetete
    ): Resource<BaseResponse>

    suspend fun collectMoneyDeleteUser(
        token: String, collectMoneyDeleteUser: CollectMoneyDeleteUser
    ): Resource<BaseResponse>

    suspend fun collectMoneyGetUserProducts(
        token: String, collectMoneyDeleteUser: CollectMoneyDeleteUser
    ): Resource<CmUserProductsResponse>

    suspend fun collectMoneyBindProducts(
        token: String, collectMoneyBindProducts: CollectMoneyBindProducts
    ): Resource<BaseResponse>

    suspend fun p2pInfoRequest(
        token: String,
        p2PInfoRequest: P2PInfoRequest
    ): Resource<P2PInfoResponse>

    suspend fun setToPopularTransfer(
        token: String,
        setPopularityRequest: SetPopularityRequest
    ): Resource<PopularTransferResponse>

    suspend fun setToNonPopularTransfer(
        token: String,
        setPopularityRequest: SetPopularityRequest
    ): Resource<PopularTransferResponse>

}