package uz.fido.network.domain.model.payment

import java.io.Serializable

data class CreatePaymentRequest(
    val service_id: String,
    val params: HashMap<String, String>,
    val from_object_id: String,
    val amount: String,
    val command: String,
    val sms_code: String? = null,
    val keep_future_percents: String? = null,
    val loan_repayment: String? = null,
    val i_request_id: String? = null
) : Serializable