package uz.fido.network.domain.model.subscriptions

data class ChangeAutoPaymentStateRequest(
    val new_state: String,
    val auto_payment_id: String
)