package uz.fido.network.domain.model.sms

data class CheckSmsForPaymentResponse(
    val code: Int? = null,
    val msg: String? = null,
    val string_line: String? = null,
    val is_sms_confirm: String? = null
)