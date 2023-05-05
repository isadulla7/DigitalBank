package uz.fido.network.domain.model.chat

import java.io.Serializable

data class AddNewRoomResponse(
    val msg: String,
    val receiver_id: String,
    val receiver_name: String,
    val result: String,
    val room_id: String
): Serializable