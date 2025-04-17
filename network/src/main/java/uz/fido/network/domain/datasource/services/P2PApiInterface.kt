package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
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

    @POST("GET_USER_OBJ_P2P_HIS")
    suspend fun getP2pHistory(
        @Header("Authorization") token: String,
        @Body p2PHistoryRequest: P2PHistoryRequest
    ): P2PHistoryResponse

    @POST("SET_TO_POPULAR_VALUE")
    suspend fun setToPopularTransfer(
        @Header("Authorization") token: String,
        @Body setPopularityRequest: SetPopularityRequest
    ): PopularTransferResponse

    @POST("SET_TO_NONPOPULAR_VALUE")
    suspend fun setToNonPopularTransfer(
        @Header("Authorization") token: String,
        @Body setPopularityRequest: SetPopularityRequest
    ): PopularTransferResponse

}