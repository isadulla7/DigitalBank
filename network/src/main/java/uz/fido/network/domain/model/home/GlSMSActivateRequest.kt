package uz.fido.network.domain.model.home

data class GlSMSActivateRequest(
    val object_value: String,
    val sms_code: String
)