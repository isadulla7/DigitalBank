package uz.fido.network.domain.model.get_card_by_phone

import java.io.Serializable

data class CardByPhone(
    val phone_number: String,
    val card_number: String,
    val card_type: String,
    val empbossed_name: String? = null,
    val exp_date: String,
    val user_avatar: String = "",
    val created_date: String
) : Serializable