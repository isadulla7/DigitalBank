package uz.fido.network.domain.model.p2p

import uz.fido.utils.const.Const.USER_CLIENT_ID

data class P2PHistoryRequest(
    val user_id: String,
    val client_id: String = USER_CLIENT_ID,
    val command: String,
    val device_type: String,
)