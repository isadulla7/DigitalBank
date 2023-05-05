package uz.fido.network.domain.model.cards

import java.io.Serializable

data class CheckWalletResponse(
    var empbossed_name: String,
    val request_id: String,
    val code: Int,
    val msg: String
) : Serializable