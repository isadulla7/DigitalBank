package uz.fido.network.domain.model.sign_in

import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.utils.const.Const.USER_CLIENT_ID

data class SignInRequest(
    val phone_number: String,
    val sms_code: String? = null,
    val device_type: String,
    val device_code: String,
    val device_name: String,
    val version: String,
    val ip: String,
    val client_id: String = USER_CLIENT_ID,
    val fcm_token: String,
    val password: String,
    val is_pin: Int? = null,
    val sim_iccd: String? = null,
    val network_state: String? = null,
    val imei_data: String? = null,
    val os_system_version_api: String? = null,
    val os_version: String,
    val app_version_code: String,
    var app_version: String,
    var userInfo: UserInfo? = null
)