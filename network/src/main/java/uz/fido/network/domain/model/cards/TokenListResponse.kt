package uz.fido.network.domain.model.cards

import java.io.Serializable

data class TokenListResponse(
    val code: Int,
    val msg: String,
    val request_id: String,
    val sv_request_id: String,
    val token_list: ArrayList<Token>,
) : Serializable

data class Token(
    val Id: String,
    val Name: String
) : Serializable