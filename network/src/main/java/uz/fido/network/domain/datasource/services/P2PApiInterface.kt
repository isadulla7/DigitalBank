package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.amount_requests.*
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
import uz.fido.network.domain.model.collect_split_money.*
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
import uz.fido.network.domain.model.popular_transfers.DeletePopularTransferRequest
import uz.fido.network.domain.model.popular_transfers.PopularTransferResponse
import uz.fido.network.domain.model.popular_transfers.SaveToPopularTransferRequest

interface P2PApiInterface {

    @POST("CARD_INFO")
    suspend fun checkCardInfo(
        @Header("Authorization") token: String,
        @Body checkCardRequestP2p: CheckCardRequestP2p
    ): CheckCardResponse

    @GET("GET_USER_TRANSFERS_WITH_COUNT")
    suspend fun getPopularTransferList(
        @Header("Authorization") token: String
    ): PopularTransferResponse

    @POST("DELETE_OBJ_FROM_P2P_HIS")
    suspend fun deletePopularTransferItem(
        @Header("Authorization") token: String,
        @Body deletePopularTransferRequest: DeletePopularTransferRequest
    ): BaseResponse

    @POST("P2P_INFO")
    suspend fun p2pInfo(
        @Header("Authorization") token: String,
        @Body p2PInfoRequest: P2PInfoRequest
    ): P2PInfoResponse

    @POST("P2P")
    suspend fun p2p(
        @Header("Authorization") token: String,
        @Body p2PRequest: P2PRequest
    ): P2PResponse

    @POST("TR_CLOSE_TARGET")
    suspend fun closeTarget(
        @Header("Authorization") token: String,
        @Body p2PRequest: P2PRequest
    ): P2PResponse

    @POST("TR_CREATE_TRANSFER")
    suspend fun targetTransfer(
        @Header("Authorization") token: String,
        @Body p2PRequest: P2PRequest
    ): P2PResponse

    @GET("GET_LIST_TRANSFER_TYPES")
    suspend fun fetchTransferParams(
        @Header("Authorization") token: String
    ): MoneyTransferParamsResponse

    @GET("GET_INFO_MONEY_TRANSFERS_OF_CLIENTS")
    suspend fun fetchMoneyTransferHistory(
        @Header("Authorization") token: String
    ): MoneyTransferHistoryResponse

    @POST("ADDING_NEW_OFFLINE_TRANSFER")
    suspend fun createMoneyTransfer(
        @Header("Authorization") token: String,
        @Body createTransferRequest: CreateTransferRequest
    ): BaseResponse

    @POST("SAVE_TO_POPULAR_TRANSFERS")
    suspend fun saveToPopularTransfer(
        @Header("Authorization") token: String,
        @Body saveToPopularTransfer: SaveToPopularTransferRequest
    ): BaseResponse

    @POST("CONVERSION")
    suspend fun conversionRequest(
        @Header("Authorization") token: String,
        @Body conversionRequest: ConversionRequest
    ): BaseResponse

    //request and collect money api's
    @GET("RM_GET_LIST")
    suspend fun rmGetList(@Header("Authorization") token: String): RmGetListResponse

    @POST("RM_CREATE")
    suspend fun rmCreate(
        @Header("Authorization") token: String,
        @Body rmCreateRequest: RmCreateRequest
    ): CmCreateResponse

    @POST("RM_GET_TRANSFERS")
    suspend fun rmGetTransfers(
        @Header("Authorization") token: String,
        @Body rmGetTransfersRequest: RmGetTransfersRequest
    ): RmGetTransfersResponse

    @POST("RM_TRANSFER")
    suspend fun rmTransfer(
        @Header("Authorization") token: String,
        @Body rmTransferRequest: RmTransferRequest
    ): RmTransferResponse

    @POST("GET_RM_ALLOWED_SUM")
    suspend fun getRmAllowedSum(
        @Header("Authorization") token: String,
        @Body getAllowedSumRequest: RmGetAllowedSumRequest
    ): RmGetAllowedSumResponse

    @POST("SET_RM_STATE")
    suspend fun setState(
        @Header("Authorization") token: String,
        @Body setStateRequest: RmSetStateRequest
    ): BaseResponse

    @POST("DELETE_RM")
    suspend fun deleteRequestMoney(
        @Header("Authorization") token: String,
        @Body rmGetTransfersRequest: RmGetTransfersRequest
    ): BaseResponse

    @GET("CM_GET_LIST")
    suspend fun collectMoneyList(
        @Header("Authorization") token: String
    ): CollectMoneyListResponse

    @POST("CM_GET_LIST_DETAIL")
    suspend fun getCollectMoneyListDetails(
        @Header("Authorization") token: String,
        @Body collectMoneyListDetailsRequest: CollectMoneyListDetailsRequest
    ): CollectMoneyDetailsResponse

    @POST("CM_CREATE_COLLECT_MONEY")
    suspend fun createCollectMoney(
        @Header("Authorization") token: String,
        @Body collectMoneyCreateRequest: CollectMoneyCreateRequest
    ): CmCreateResponse

    @POST("CM_GET_USER_TRANSFER")
    suspend fun getCollectMoneyUserTransfer(
        @Header("Authorization") token: String,
        @Body collectMoneyUserTransferRequest: CollectMoneyUserTransferRequest
    ): BaseResponse

    @POST("CM_TRANSFER")
    suspend fun collectMoneyTransfer(
        @Header("Authorization") token: String,
        @Body collectMoneyTransferRequest: CollectMoneyTransferRequest
    ): BaseResponse

    @POST("CM_MANUL_TRANSFER")
    suspend fun collectMoneyManualTransfer(
        @Header("Authorization") token: String,
        @Body collectMoneyManualTransfer: CollectMoneyManualTransfer
    ): BaseResponse

    @POST("CM_ADD_USERS")
    suspend fun collectMoneyAddUsers(
        @Header("Authorization") token: String,
        @Body collectMoneyAddUserRequest: CmAddUsersRequest
    ): BaseResponse

    @POST("CM_ADD_USER")
    suspend fun collectMoneyAddUser(
        @Header("Authorization") token: String,
        @Body collectMoneyAddUserRequest: CollectMoneyAddUserRequest
    ): BaseResponse

    @POST("CM_GET_ALLOWED_SUM")
    suspend fun getCmAllowedSum(
        @Header("Authorization") token: String,
        @Body getAllowedSumRequest: RmGetAllowedSumRequest
    ): RmGetAllowedSumResponse

    @POST("CM_GET_USER_TRANSFER")
    suspend fun getCollectMoneyUserTransfers(
        @Header("Authorization") token: String,
        @Body collectMoneyGetUserTransfers: CollectMoneyGetUserTransfers
    ): CMUserTransferResponse

    @POST("SET_CM_STATE")
    suspend fun collectMoneySetState(
        @Header("Authorization") token: String,
        @Body cmSetStateRequest: CMSetStateRequest
    ): BaseResponse

    @POST("CM_DELETE")
    suspend fun collectMoneyDelete(
        @Header("Authorization") token: String,
        @Body cmSetStateRequest: CMSetStateRequest
    ): BaseResponse

    @POST("CM_CHANGE_OBJECT")
    suspend fun collectMoneyChangeCardNumber(
        @Header("Authorization") token: String,
        @Body cmChangeCardNumberRequest: CMChangeCardNumberRequest
    ): BaseResponse

    @POST("CM_GET_MENU")
    suspend fun collectMoneyGetMenu(
        @Header("Authorization") token: String,
        @Body cmSetStateRequest: CMSetStateRequest
    ): CollectMoneyMenuResponse

    @POST("CM_EDIT_MENU")
    suspend fun editCollectMoney(
        @Header("Authorization") token: String,
        @Body collectMoneyEditRequest: CollectMoneyEditRequest
    ): BaseResponse

    @POST("CM_DELETE_MENU")
    suspend fun deleteCollectMoneyMenu(
        @Header("Authorization") token: String,
        @Body collectMoneyMenuDelete: CollectMoneyMenuDetete
    ): BaseResponse

    @POST("CM_DELETE_USER")
    suspend fun collectMoneyDeleteUser(
        @Header("Authorization") token: String,
        @Body collectMoneyDeleteUser: CollectMoneyDeleteUser
    ): BaseResponse

    @POST("CM_GET_USER_PRODUCTS")
    suspend fun collectMoneyGetUserProducts(
        @Header("Authorization") token: String,
        @Body collectMoneyDeleteUser: CollectMoneyDeleteUser
    ): CmUserProductsResponse

    @POST("CM_BIND_PRODUCTS_TO_USER")
    suspend fun collectMoneyBindProducts(
        @Header("Authorization") token: String,
        @Body collectMoneyBindProducts: CollectMoneyBindProducts
    ): BaseResponse

    @POST("GET_USER_OBJ_P2P_HIS")
    suspend fun getP2pHistory(
        @Header("Authorization") token: String,
        @Body p2PHistoryRequest: P2PHistoryRequest
    ): P2PHistoryResponse

}