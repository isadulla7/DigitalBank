package uz.fido.network.domain.model.cards

import java.io.Serializable

data class AddCardRequest(
    val object_value: String,
    val object_expiry: String,
    val phone_number: String,
    val object_name: String,
    val sms_code: String,
    val is_main: String,
    val bg_icon_name: String
):Serializable