package uz.fido.network.domain.model.sign_up

import java.io.Serializable

data class SignUpResponse(
    val code: Int,
    val msg: String,
    val ora_msg: Any,
    val token: String,
    val user_id: String
) : Serializable