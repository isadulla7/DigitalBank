package uz.fido.network.domain.model.payment.location

data class LocalPaymentTypesResponse(
    val code: Int,
    val msg: String,
    val local_payment_types: ArrayList<LocalPaymentType>
)