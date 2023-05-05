package uz.fido.network.domain.model.chat

data class ChatUserResponse(
    val contact_list: ArrayList<ChatUser>,
    val msg: String,
    val result: Int
)