package uz.fido.network.domain.model.chat

import uz.fido.network.domain.model.chat.MessageHistory

data class MessageHistoryResponse(
    val msg_list: ArrayList<MessageHistory>? = null,
    val msg: String,
    val code: Int
)