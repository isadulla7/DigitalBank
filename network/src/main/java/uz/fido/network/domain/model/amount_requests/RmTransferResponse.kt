package uz.fido.network.domain.model.amount_requests

import java.io.Serializable

class RmTransferResponse(
    var amount: String,
    var request_id: Int,
    val code: String,
    val msg: String
) : Serializable