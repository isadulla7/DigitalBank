package uz.fido.network.domain.model.subscriptions

data class AutoPaymentResponse(
    val code: Int,
    val msg: String,
    val auto_payment_list: ArrayList<AutoPayment>
)