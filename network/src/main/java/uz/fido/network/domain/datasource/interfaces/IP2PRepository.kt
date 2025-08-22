package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
import uz.fido.network.domain.model.conversion.ConversionRequest
import uz.fido.network.domain.model.conversion.ConversionResponse
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
    ): Resource<ConversionResponse>

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

    suspend fun conversionConfirmRequest(
        token: String,
        conversion: ConversionRequest
    ): Resource<BaseResponse>

}