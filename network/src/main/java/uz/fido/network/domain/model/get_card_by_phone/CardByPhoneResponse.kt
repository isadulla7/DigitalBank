package uz.fido.network.domain.model.get_card_by_phone

data class CardByPhoneResponse(
    val cards: ArrayList<CardByPhone>,
    val code: Int,
    val msg: String,
    val ora_msg: String
)