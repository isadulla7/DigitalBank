package uz.fido.network.domain.model.p2p

import java.io.Serializable

data class P2PResponse(
    val fee_percent: String,
    val sv_request_id: String,
    val fee_amount: String,
    val request_id: String,
    val code: String,
    val msg: String
) : Serializable