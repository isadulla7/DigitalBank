package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class CommonData(
    val birth_country: String,
    val birth_date: String,
    val birth_place: String,
    val citizenship: String,
    val first_name: String,
    val gender: String,
    val inn: String,
    val last_name: String,
    val middle_name: String,
    val nationality: String,
    val pinfl: String,
    val sdk_hash: String
) : Serializable