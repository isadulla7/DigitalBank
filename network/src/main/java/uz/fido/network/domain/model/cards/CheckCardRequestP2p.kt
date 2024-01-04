package uz.fido.network.domain.model.cards

import java.io.Serializable

data class CheckCardRequestP2p(
    val command: String,
    val to_object_value: String
) : Serializable