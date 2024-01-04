package uz.fido.network.domain.model.chat

data class MessageOperationRequest(
    val message: String,
    val message_id: String,
    val state_id: Int,
    val content_type: String
)