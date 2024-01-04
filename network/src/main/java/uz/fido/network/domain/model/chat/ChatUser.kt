package uz.fido.network.domain.model.chat

data class ChatUser(
    val contact_phone_number: String,
    val contact_user_id: String,
    val name: String,
    val user_id: String
)