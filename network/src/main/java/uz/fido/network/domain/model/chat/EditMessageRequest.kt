package uz.fido.network.domain.model.chat

data class EditMessageRequest(
    val msg_id: String? = null,
    val msg_text: String? = null,
    val room_id: String,
    val msg_state_id: String? = null
)