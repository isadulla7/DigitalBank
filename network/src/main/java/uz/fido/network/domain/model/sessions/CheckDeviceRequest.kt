package uz.fido.network.domain.model.sessions

import java.io.Serializable

class CheckDeviceRequest(
    var device_type: String,
    var device_code: String,
    var user_id: String,
    var app_key_hash: String,
    var current_device_code: String,
    var phone_number: String
) : Serializable