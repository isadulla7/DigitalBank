package uz.fido.network.domain.model.sign_in

import uz.fido.network.domain.model.abc_base.UserInfo

data class SignInRequestNew(
    val app_key_hash: String,
    val app_version: String,
    val app_version_code: String,
    val client_id: String,
    val device_code: String,
    val device_name: String,
    val device_type: String,
    val fcm_token: String,
    val imei_data: String,
    val ip: String,
    val is_pin: Int,
    val network_state: String,
    val os_system_version_api: String="A",
    val os_version: String,
    val password: String,
    val phone_number: String,
    val sim_iccd: String,
    val userInfo: UserInfo,
    val version: String,
    var string_line: String? = null
) : java.io.Serializable