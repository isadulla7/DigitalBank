package uz.fido.network.domain.model.payment

import uz.fido.network.domain.model.payment.PaymentParams
import java.io.Serializable

data class PreparePaymentResponse(
    val code: Int,
    val level_position: String,
    val msg: String,
    val ora_msg: String? = null,
    val service_details: ArrayList<PaymentParams>,
    val request_id: String? = null

) : Serializable