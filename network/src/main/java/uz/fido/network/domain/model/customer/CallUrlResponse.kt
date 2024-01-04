package uz.fido.network.domain.model.customer

import java.io.Serializable

data class CallUrlResponse(
    val request_id: String,
    val code: Int,
    val msg: String,
    val call_url: String? = null,
    val reward_amount: String? = null
) : Serializable