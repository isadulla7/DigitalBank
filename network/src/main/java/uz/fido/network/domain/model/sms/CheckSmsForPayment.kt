package uz.fido.network.domain.model.sms

data class CheckSmsForPayment(
    val app_key_hash: String,
    val from_object_id: String,
    val amount: String,
    val service_id: String,
    val sms_confirm_counter: String? = "Y",
    val device_code:String
)