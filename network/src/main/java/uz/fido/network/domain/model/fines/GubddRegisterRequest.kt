package uz.fido.network.domain.model.fines

import java.io.Serializable

data class GubddRegisterRequest(
    var command: String,
    var sms_phone_number: String,
    var app_phone_number: String,
    var tex_passport: String,
    var car_model: String,
    var licenseplate: String
) : Serializable