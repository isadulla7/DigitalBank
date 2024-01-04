package uz.fido.network.domain.model.payment

import java.io.Serializable

data class PreparePaymentRequest(
    val service_id: String,
    val curr_level_position: String,
    val payment_detail_code: String,
    val params: HashMap<String, String>,
    val command: String
) : Serializable