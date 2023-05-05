package uz.fido.network.domain.model.cards

data class BlockCardResponse(
    val code: Int,
    val msg: String,
    val ora_msg: String,
    val request_id: String
)