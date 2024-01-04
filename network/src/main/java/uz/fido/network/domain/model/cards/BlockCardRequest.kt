package uz.fido.network.domain.model.cards

import java.io.Serializable

data class BlockCardRequest(
    val from_object_id: String,
    val status_id: String,
    val text: String? = "",
    val command: String
) : Serializable