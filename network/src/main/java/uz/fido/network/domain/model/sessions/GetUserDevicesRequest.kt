package uz.fido.network.domain.model.sessions

import java.io.Serializable

class GetUserDevicesRequest(
    var user_id: String
) : Serializable