package uz.fido.network.domain.model.chat

data class MessageHistoryResponse(
    val msg_list: ArrayList<MessageHistory>? = null,
    val msg: String,
    val code: Int
)