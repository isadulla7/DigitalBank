package uz.fido.network.domain.model.subscriptions

data class AutoPaymentHistory(
    val amount: String,
    val auto_payment_id: String,
    val executed_date: String,
    val state: String,
    val request_id: String
)