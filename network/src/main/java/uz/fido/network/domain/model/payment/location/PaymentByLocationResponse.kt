package uz.fido.network.domain.model.payment.location

data class PaymentByLocationResponse(
    val local_payments_onspot_list: ArrayList<LocalPayment>,
    val last: String,
    val code: Int,
    val msg: String
)