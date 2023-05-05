package uz.fido.network.domain.model.sign_up

import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.utils.const.Const.USER_CLIENT_ID

data class CheckUserSms(
    val phone_number: String,
    val client_id: String = USER_CLIENT_ID,
    val sms_code: String? = null,
    val device_id: String? = null,
    val sms_type: Int? = null,
    val version: String? = null,

    val device_code: String? = null,
    val device_name: String? = null,
    val ip: String? = null,
    val fcm_token: String? = null,
    val is_pin: Int? = null,
    val sim_iccd: String? = null,
    val network_state: String? = null,
    val imei_data: String? = null,
    val device_type: String? = null,
    val os_version: String? = null,
    val app_version_code: String? = null,
    var app_version: String? = null,
    var app_key_hash: String? = null,
    var string_line: String? = null,
    val os_system_version_api: String? = null,
    val password: String? = null,
    val userInfo: UserInfo? = null
)