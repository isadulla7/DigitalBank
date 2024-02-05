package uz.fido.network.domain.model.cards

import java.io.Serializable

data class AddCheckCardResponse(
    var sms_length: Int,
    var otp_id: String,
    val code: Int,
    val request_id: String,
    val msg: String
) : Serializable
