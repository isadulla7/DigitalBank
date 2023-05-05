package uz.fido.network.domain.model.cards

import java.io.Serializable

data class OrderCardTypeRequest(
    val card_type: Int
) : Serializable