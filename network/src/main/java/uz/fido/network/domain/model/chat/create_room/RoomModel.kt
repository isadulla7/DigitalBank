package uz.fido.network.domain.model.chat.create_room

data class RoomModel(
    val room_id: Int,
    val default_img_name: String? = "",
    val last_msg_id: String? = "",
    val ord: String? = "",
    val room_name: String? = "",
    val room_type_id: String? = "",
    val state_id: String? = "",
    val user_role: String? = "",
    var counter: String? = "",
    val receiver_name: String? = "",
    val receiver_id: String? = "",
    val message: String? = "",
    val time: String? = "",
    val last_msg_text: String? = ""
)