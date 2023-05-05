package uz.fido.network.domain.model.password

import uz.fido.utils.const.Const.USER_CLIENT_ID

data class CheckForgetPasswordModel(
    val phone_number: String,
    val device_type: String,
    val device_code: String,
    val version: String,
    val ip: String,
    val client_id: String = USER_CLIENT_ID,
    val app_key_hash: String
)