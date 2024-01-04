package uz.fido.network.domain.model.sms

data class SendEmailCode(
    val email: String,
    val phone_number: String,
    val device_id: String
)