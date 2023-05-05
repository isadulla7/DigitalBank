package uz.fido.network.domain.model.cards

import java.io.Serializable

data class GetCVVRequest(
    val cardNumber: String
) : Serializable
