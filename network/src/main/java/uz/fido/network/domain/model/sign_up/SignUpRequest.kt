package uz.fido.network.domain.model.sign_up

import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.utils.const.Const.USER_CLIENT_ID
import java.io.Serializable

data class SignUpRequest(
    val phone_number: String,
    val sms_code: String,
    val name: String? = "",
    val surname: String? = "",
    val device_type: String,
    val device_code: String,
    val device_name: String,
    val version: String,
    val ip: String,
    val client_id: String = USER_CLIENT_ID,
    val email: String,
    var password: String? = null,
    val fcm_token: String? = null,
    var token: String? = null,
    var user_id: String? = null,
    var sim_iccd: String? = null,
    var network_state: String? = null,
    var imei_data: String? = null,
    var os_system_version_api: String? = null,
    var invited_user_id: String? = null,
    var os_version: String,
    var userInfo: UserInfo? = null,
    var app_version_code: String,
    var app_version: String

) : Serializable