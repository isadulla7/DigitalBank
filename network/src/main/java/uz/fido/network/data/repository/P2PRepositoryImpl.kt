package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.datasource.services.P2PApiInterface
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
import javax.inject.Inject

class P2PRepositoryImpl @Inject constructor(private val p2pService: P2PApiInterface) :
    IP2PRepository {

    override suspend fun checkCardInfo(
        token: String,
        checkCardRequestP2p: CheckCardRequestP2p
    ): Resource<CheckCardResponse> = getResult {
        p2pService.checkCardInfo(token, checkCardRequestP2p)
    }

    override suspend fun getP2pHistory(
        token: String,
        p2PHistoryRequest: P2PHistoryRequest
    ): Resource<P2PHistoryResponse> = getResult {
        p2pService.getP2pHistory(token, p2PHistoryRequest)
    }

    override suspend fun getPopularTransferList(token: String): Resource<PopularTransferResponse> = getResult {
        p2pService.getPopularTransferList(token)
    }

    override suspend fun p2pInfo(
        token: String,
        p2PInfoRequest: P2PInfoRequest
    ): Resource<P2PInfoResponse> =
        getResult {
            p2pService.p2pInfo(token, p2PInfoRequest)
        }


    override suspend fun p2p(token: String, p2PRequest: P2PRequest): Resource<P2PResponse> = getResult {
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

    override suspend fun p2pInfoRequest(
        token: String,
        p2PInfoRequest: P2PInfoRequest
    ): Resource<P2PInfoResponse> {
        return getResult { p2pService.p2pInfo(token, p2PInfoRequest) }
    }

    override suspend fun setToPopularTransfer(
        token: String,
        setPopularityRequest: SetPopularityRequest
    ): Resource<PopularTransferResponse> {
        return getResult { p2pService.setToPopularTransfer(token, setPopularityRequest) }
    }

    override suspend fun setToNonPopularTransfer(
        token: String,
        setPopularityRequest: SetPopularityRequest
    ): Resource<PopularTransferResponse> {
        return getResult { p2pService.setToNonPopularTransfer(token, setPopularityRequest) }
    }


}