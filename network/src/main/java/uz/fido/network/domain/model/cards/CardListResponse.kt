package uz.fido.network.domain.model.cards

data class CardListResponse(
    val code: Int,
    val msg: String,
    val objects: ArrayList<CardResponse>? = null,
    val ora_msg: String
)