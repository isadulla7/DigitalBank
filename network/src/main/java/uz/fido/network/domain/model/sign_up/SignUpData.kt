package uz.fido.network.domain.model.sign_up

import java.io.Serializable

data class SignUpData(
    val phone_number: String,
    val first_name: String = "",
    val last_name: String = ""
): Serializable