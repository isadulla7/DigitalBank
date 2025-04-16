package uz.fido.network.domain.model.sessions

import java.io.Serializable

class DeleteUserDeviceRequest(
    var device_type: String = "A",
    var selected_device_code: String? = null,
    var del_req_type: String,
    var current_device_code: String,
    var user_id: String,
    var string_line: String
) : Serializable