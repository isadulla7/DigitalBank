package uz.fido.network.domain.model.cards

import java.io.Serializable

data class OrderVirtualCardRequest(
    val virtual: String,
    val cardType: String,
    val contact: String,
    val orderType: String,
    val service_id: String,
    val smsMobilePhone: String,
    val secretWord: String
) : Serializable