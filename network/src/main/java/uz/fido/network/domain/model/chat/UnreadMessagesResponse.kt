package uz.fido.network.domain.model.chat

data class UnreadMessagesResponse(
    val counter: String,
    val admin_count: String,
    val msg: String,
    val code: Int,
    val room_id: String?
)