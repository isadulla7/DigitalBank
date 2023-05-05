package uz.fido.network.domain.model.fines

import java.io.Serializable

data class GubddActivateRequest(
    var command: String,
    var amount: String,
    var from_object_id: String,
    var service_id: String,
    var client_id: String,
    var sms_phone_number: String,
    var tex_passport: String,
    var licenseplate: String,
    var app_phone_number: String
) : Serializable