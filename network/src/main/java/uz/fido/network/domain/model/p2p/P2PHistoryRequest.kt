package uz.fido.network.domain.model.p2p

import uz.fido.network.di.Keys

data class P2PHistoryRequest(
    val user_id: String,
    val client_id: String = Keys.getClientId(),
    val command: String,
    val device_type: String,
)