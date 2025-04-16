package uz.fido.network.domain.model.sign_up

import uz.fido.network.di.Keys
import uz.fido.network.domain.model.abc_base.UserInfo
import java.io.Serializable

data class SignUpCheckRequest(
    val app_key_hash: String,
    val phone_number: String,
    val client_id: String = Keys.getClientId(),
    val device_code: String,
    val userInfo: UserInfo,
    val device_id: String? = null,
    val os_system_version_api: String = "A",
    val app_version_code: String = "",
) : Serializable