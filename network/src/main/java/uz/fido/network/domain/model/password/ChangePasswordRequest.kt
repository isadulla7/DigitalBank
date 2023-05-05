package uz.fido.network.domain.model.password

data class ChangePasswordRequest(
    val phone_number: String? = null,
    val client_id: String? = null,
    val sms_code: String? = null,
    val password: String? = null,
    val current_password: String? = null,
    val new_password: String? = null
)