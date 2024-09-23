package uz.fido.universaldigital.ui.fragments.transfers.card_to_card

import android.content.Context
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
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.Command
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

interface TransferToCardUseCase {
    suspend fun getPopularTransferList(clientToken: String): ArrayList<PopularTransfers>
    suspend fun getCardInfo(clientToken: String, checkCardRequestP2p: CheckCardRequestP2p): CardInfoDto
    suspend fun getTransferInfo(clientToken: String, p2PInfoRequest: P2PInfoRequest): P2PInfoDto
    suspend fun getTransferHistories(context: Context): ArrayList<CardByPhone>
    suspend fun setToPopularTransfer(clientToken: String, toObjectValue: String): ArrayList<PopularTransfers>
    suspend fun setToNonPopularTransfer(clientToken: String, toObjectValue: String): ArrayList<PopularTransfers>
}

class TransferToCardUseCaseImpl @Inject constructor(
    private val p2PRepository: IP2PRepository
) : TransferToCardUseCase {

    override suspend fun getPopularTransferList(clientToken: String): ArrayList<PopularTransfers> {
        val response = p2PRepository.getPopularTransferList(clientToken)
        return if (response.status == Status.SUCCESS) {
            response.data?.popular_transfers ?: ArrayList()
        } else ArrayList()
    }


    override suspend fun getCardInfo(clientToken: String, checkCardRequestP2p: CheckCardRequestP2p): CardInfoDto {
        val response = p2PRepository.checkCardInfo(clientToken, checkCardRequestP2p)
        return if (response.status == Status.SUCCESS) {
            response.data?.mapToDto() ?: CardInfoDto()
        } else CardInfoDto()
    }

    override suspend fun getTransferInfo(clientToken: String, p2PInfoRequest: P2PInfoRequest): P2PInfoDto {
        val response = p2PRepository.p2pInfo(clientToken, p2PInfoRequest)
        return if (response.status == Status.SUCCESS) {
            response.data?.mapToDto() ?: P2PInfoDto(isSuccess = false)
        } else {
            P2PInfoDto(isSuccess = false, errorMessage = response.message)
        }
    }

    override suspend fun getTransferHistories(context: Context): ArrayList<CardByPhone> {
        val response = p2PRepository.getP2pHistory(
            context.getClientToken(), P2PHistoryRequest(
                context.getClientId(), Keys.getClientId(), Command.INFO, "A"
            )
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.cards ?: ArrayList()
        } else ArrayList()
    }

    override suspend fun setToPopularTransfer(clientToken: String, toObjectValue: String): ArrayList<PopularTransfers> {
        val response = p2PRepository.setToPopularTransfer(
            clientToken,
            SetPopularityRequest(to_object_value = toObjectValue)
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.popular_transfers ?: ArrayList()
        } else ArrayList()
    }

    override suspend fun setToNonPopularTransfer(clientToken: String, toObjectValue: String): ArrayList<PopularTransfers> {
        val response = p2PRepository.setToNonPopularTransfer(
            clientToken,
            SetPopularityRequest(to_object_value = toObjectValue)
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.popular_transfers ?: ArrayList()
        } else ArrayList()
    }


}