package uz.fido.network.domain.model.sign_up

import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.utils.const.Const.USER_CLIENT_ID

data class SignUpCheckRequest(
    val app_key_hash: String,
    val phone_number: String,
    val client_id: String = USER_CLIENT_ID,
    val device_code: String,
    val userInfo: UserInfo,
    val device_id: String? = null
)