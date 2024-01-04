package uz.fido.network.domain.model.payment

import java.io.Serializable

data class CreatePaymentResponse(
    val code: String,
    val msg: String,
    val ora_msg: String? = null,
    val request_id: Int
) : Serializable