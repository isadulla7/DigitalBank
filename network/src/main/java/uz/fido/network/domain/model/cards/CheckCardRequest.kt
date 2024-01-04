package uz.fido.network.domain.model.cards

import java.io.Serializable

data class CheckCardRequest(
    val object_expiry: String,
    val object_value: String,
    val phone_number: String,
    val app_key_hash: String,
    val device_code: String
) : Serializable