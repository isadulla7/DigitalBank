package uz.fido.network.domain.model.chat

import uz.fido.network.domain.model.chat.create_room.RoomModel

data class RoomListResponse(
    val room_list: ArrayList<RoomModel>,
    val code: Int,
    val msg: String
)