package uz.fido.network.domain.model.sign_up

import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.utils.const.APIServiceConst.USER_CLIENT_ID

data class FinishRegRequest(
    val app_version: String? = null,
    val app_version_code: String? = null,
    var client_id: String = USER_CLIENT_ID,
    val device_code: String? = null,
    val device_name: String? = null,
    val device_type: String? = null,
    val email: String? = null,
    var fcm_token: String? = null,
    val flag: Int? = null,
    val imei_data: String? = null,
    var invited_user_id: String? = null,
    val ip: String? = null,
    val name: String? = null,
    val network_state: String? = null,
    val nick_name: String? = null,
    val os_system_version_api: String? = null,
    val os_version: String? = null,
    var password: String? = null,
    val patronymic: String? = null,
    val phone_number: String? = null,
    val sim_iccd: String? = null,
    val sms_code: String? = null,
    val string_line: String? = null,
    val surname: String? = null,
    var userInfo: UserInfo? = null,
    val card_number: String? = null,
    var expire_date: String? = null,
    val version: String? = null
) : java.io.Serializable