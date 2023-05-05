package uz.fido.network.domain.model.get_card_by_phone

import java.io.Serializable

data class GetCardByPhoneRequest(
    val phone: String,
    val command: String
) : Serializable