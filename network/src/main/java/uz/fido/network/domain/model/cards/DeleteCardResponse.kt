package uz.fido.network.domain.model.cards

data class DeleteCardResponse(
    val code: Int,
    val msg: String,
    val ora_msg: String
)