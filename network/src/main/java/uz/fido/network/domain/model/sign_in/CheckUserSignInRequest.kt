package uz.fido.network.domain.model.sign_in

import uz.fido.utils.const.Const.USER_CLIENT_ID
import java.io.Serializable

data class CheckUserSignInRequest(
    val device_code: String,
    val device_type: String,
    val phone_number: String,
    val version: String,
    val client_id: String =  USER_CLIENT_ID,
    val password: String,
    val app_key_hash: String
) : Serializable