package uz.fido.network.domain.model.cards

import java.io.Serializable

data class CheckCardResponse(
    var to_object_value: String,
    val empbossed_name: String,
    val to_object_expire: String,
    val exp_date: String,
    val code: Int,
    val card_type: String,
    val user_id: String,
    val request_id: String,
    val to_object_type: String,
    val currency_code: String?="",
    val msg: String
) : Serializable