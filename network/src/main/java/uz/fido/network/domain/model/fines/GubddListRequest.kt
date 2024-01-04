package uz.fido.network.domain.model.fines

import java.io.Serializable

data class GubddListRequest(
    var command: String,
    var app_phone_number: String
) : Serializable