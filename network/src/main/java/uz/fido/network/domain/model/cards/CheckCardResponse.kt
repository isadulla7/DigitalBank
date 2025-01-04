package uz.fido.network.domain.model.cards

import java.io.Serializable
import java.util.Locale

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
    val first_name: String? = "",
    val surname: String? = "",
    val msg: String? = ""
) : Serializable {
    fun mapToDto(): CardInfoDto {
        return CardInfoDto(
            card_type = to_object_type,
            card_number = to_object_value,
            card_owner = getCardOwnerNameFormatted(first_name, surname, empbossed_name),
            card_expire = to_object_expire,
            card_id = to_object_id,
            message = msg.orEmpty()
        )
    }
}

data class CardInfoDto(
    var card_type: String? = "",
    var card_number: String? = "",
    var card_owner: String? = "",
    var card_expire: String? = "",
    var card_id: String? = "",
    var message: String? = ""
) : Serializable

fun getCardOwnerNameFormatted(firstName: String?, surname: String?, embossedName: String?): String {
    if (firstName.isNullOrEmpty() && surname.isNullOrEmpty() && embossedName.isNullOrEmpty()) return "Not Found"
    if (firstName.isNullOrEmpty() && surname.isNullOrEmpty()) return getUserNameFormatted(embossedName)
    if (!firstName.isNullOrEmpty() && !surname.isNullOrEmpty()) {
        return firstName.capitalizeWord() + " " + surname.first() + "."
    }
    return embossedName.orEmpty()
}

fun getUserNameFormatted(embossedName: String?): String {
    embossedName?.let { fullName ->
        return if (fullName.trim().contains(" ")) {
            fullName.split(" ")[0].capitalizeWord() + " " + fullName.split(" ")[1].first().uppercase() + "."
        } else fullName.capitalizeWord()
    }
    return "Not Found"
}

fun String.capitalizeWord(): String = if (this.length > 1) this[0].uppercaseChar().toString() + this.substring(1).lowercase(Locale.getDefault()) else ""