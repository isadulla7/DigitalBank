package uz.fido.network.domain.model.sessions

import java.io.Serializable
import java.util.*

data class GetUserDevicesResponse(
    val code: Int,
    val msg: String,
    val user_devices: ArrayList<UserDevices>
) : Serializable