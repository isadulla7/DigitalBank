package uz.fido.network.domain.model.chat

data class ForwardMessageRequest(
    val from_room_id: String,
    val msg_ids: ArrayList<String>,
    val to_room_id: String
)