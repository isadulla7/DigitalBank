package uz.fido.network.domain.model.sessions

import java.io.Serializable

class DeleteUserDeviceRequest(
    var device_type: String,
    var selected_device_code: String,
    var del_req_type: String,
    var user_id: String,
    var string_line: String
) : Serializable