package uz.fido.network.domain.model.p2p

import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CardResponse
import java.io.Serializable

data class TransferDto(
    val senderCard: CardResponse? = null,
    val receiverCard: CardInfoDto? = null,
    val transferAmount: String? = "0",
    val commission: Double? = 0.0,
    var operation: String? = null,
    var currentRate: String? = "",
    var phoneNumber: String? = "",
    var requestId: String? = "",
    var createdDate: String? = "",
    var cardId: String? = null
) : Serializable