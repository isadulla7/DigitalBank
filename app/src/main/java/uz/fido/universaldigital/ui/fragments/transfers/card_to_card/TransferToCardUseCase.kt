package uz.fido.universaldigital.ui.fragments.transfers.card_to_card

import uz.fido.network.data.utility.Status
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.get_card_by_phone.CardByPhone
import uz.fido.network.domain.model.p2p.P2PHistoryRequest
import uz.fido.network.domain.model.p2p.P2PInfoDto
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.SetPopularityRequest
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.utils.const.APIServiceConst.USER_CLIENT_ID
import uz.fido.utils.const.Command
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

interface TransferToCardUseCase {
    suspend fun getPopularTransferList(): ArrayList<PopularTransfers>
    suspend fun getCardInfo(checkCardRequestP2p: CheckCardRequestP2p): CardInfoDto
    suspend fun getTransferInfo(p2PInfoRequest: P2PInfoRequest): P2PInfoDto
    suspend fun getTransferHistories(): ArrayList<CardByPhone>
    suspend fun setToPopularTransfer(toObjectValue: String): ArrayList<PopularTransfers>
    suspend fun setToNonPopularTransfer(toObjectValue: String): ArrayList<PopularTransfers>
}

class TransferToCardUseCaseImpl @Inject constructor(
    private val p2PRepository: IP2PRepository
) : TransferToCardUseCase {

    override suspend fun getPopularTransferList(): ArrayList<PopularTransfers> {
        val response = p2PRepository.getPopularTransferList(getClientToken())
        return if (response.status == Status.SUCCESS) {
            response.data?.popular_transfers ?: ArrayList()
        } else ArrayList()
    }

    override suspend fun getCardInfo(checkCardRequestP2p: CheckCardRequestP2p): CardInfoDto {
        val response = p2PRepository.checkCardInfo(getClientToken(), checkCardRequestP2p)
        return if (response.status == Status.SUCCESS) {
            response.data?.mapToDto() ?: CardInfoDto()
        } else CardInfoDto()
    }

    override suspend fun getTransferInfo(p2PInfoRequest: P2PInfoRequest): P2PInfoDto {
        val response = p2PRepository.p2pInfo(getClientToken(), p2PInfoRequest)
        return if (response.status == Status.SUCCESS) {
            response.data?.mapToDto() ?: P2PInfoDto(isSuccess = false)
        } else {
            P2PInfoDto(isSuccess = false)
        }
    }

    override suspend fun getTransferHistories(): ArrayList<CardByPhone> {
        val response = p2PRepository.getP2pHistory(
            getClientToken(), P2PHistoryRequest(
                getClientId(), USER_CLIENT_ID, Command.INFO, "A"
            )
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.cards ?: ArrayList()
        } else ArrayList()
    }

    override suspend fun setToPopularTransfer(toObjectValue: String): ArrayList<PopularTransfers> {
        val response = p2PRepository.setToPopularTransfer(
            getClientToken(),
            SetPopularityRequest(to_object_value = toObjectValue)
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.popular_transfers ?: ArrayList()
        } else ArrayList()
    }

    override suspend fun setToNonPopularTransfer(toObjectValue: String): ArrayList<PopularTransfers> {
        val response = p2PRepository.setToNonPopularTransfer(
            getClientToken(),
            SetPopularityRequest(to_object_value = toObjectValue)
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.popular_transfers ?: ArrayList()
        } else ArrayList()
    }

}