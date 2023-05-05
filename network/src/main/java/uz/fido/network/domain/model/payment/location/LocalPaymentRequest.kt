package uz.fido.network.domain.model.payment.location

data class LocalPaymentRequest(
    val amount: String,
    val from_object_id: String,
    val pay_onspot_id: String,
    val receiver_phone: String,
    val service_id: String,
    val command: String
)