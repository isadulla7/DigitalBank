package uz.fido.network.domain.model.sessions

import java.io.Serializable

class CheckDeviceRequest(
    var device_type: String = "A",
    var user_id: String,
    var app_key_hash: String,
    var phone_number: String,
    var current_device_code: String
) : Serializable