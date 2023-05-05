package uz.fido.network.domain.model.chat.create_room

data class CreatePersonalRoomResponse(
    val room_id: Int,
    val code: Int,
    val room_img_name: String,
    val room_name: String,
    val msg: String
)