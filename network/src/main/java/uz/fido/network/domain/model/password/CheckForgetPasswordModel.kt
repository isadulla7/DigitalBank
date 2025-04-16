package uz.fido.network.domain.model.password

import uz.fido.network.di.Keys

data class CheckForgetPasswordModel(
    val phone_number: String,
    val device_type: String,
    val device_code: String,
    val version: String,
    val ip: String,
    val client_id: String = Keys.getClientId(),
    val app_key_hash: String
)