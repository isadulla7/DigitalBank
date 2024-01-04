package uz.fido.network.domain.model.chat

data class SendMessageRequest(
    val msg_object: String,
    val msg_text: String,
    val msg_type_id: String,
    val ref_msg_id: String,
    val ref_user_id: String,
    val room_id: String
)
