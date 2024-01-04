package uz.fido.network.domain.model.subscriptions

data class EditAutoPaymentRequest(
    val auto_payment_id: String,
    val days: ArrayList<String>,
    val hours: Int,
    val months: ArrayList<String>,
    val name: String,
    val need_confirm: String,
    val object_value: String
)