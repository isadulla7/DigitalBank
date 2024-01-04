package uz.fido.network.domain.model.cards

import java.io.Serializable

data class CheckCardResponse(
    var to_object_value: String,
    var to_object_id: String,
    val empbossed_name: String,
    val to_object_expire: String,
    val exp_date: String,
    val code: Int,
    val card_type: String,
    val user_id: String,
    val request_id: String,
    val to_object_type: String,
    val currency_code: String? = "",
    val msg: String
) : Serializable {
    fun mapToDto(): CardInfoDto {
        return CardInfoDto(
            card_type = to_object_type ?: "",
            card_number = to_object_value ?: "",
            card_owner = empbossed_name ?: "",
            card_expire = to_object_expire ?: "",
            card_id = to_object_id ?: ""
        )
    }
}

data class CardInfoDto(
    var card_type: String? = "",
    var card_number: String? = "",
    var card_owner: String? = "",
    var card_expire: String? = "",
    var card_id: String? = ""
) : Serializable