package uz.fido.network.domain.model.p2p

import uz.fido.network.domain.model.get_card_by_phone.CardByPhone


data class P2PHistoryResponse(
    val request_id: Int,
    val cards: ArrayList<CardByPhone>,
    val code: Int,
    val msg: String
)