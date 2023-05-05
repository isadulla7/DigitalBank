package uz.fido.network.domain.model.fines

import java.io.Serializable

class MyCar(
    var app_phone_number: String,
    var car_model: String,
    var expire_date: String,
    var licenseplate: String,
    var modified_date: String,
    var reg_date: String,
    var sms_phone_number: String,
    var state: String,
    var tex_passport: String
) : Serializable