package uz.fido.network.domain.model.cards

data class CardInfoResponse(
    val code: Int,
    val msg: String,
    val objects: ArrayList<CardInfo>? = null
)