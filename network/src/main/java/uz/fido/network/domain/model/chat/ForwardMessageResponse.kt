package uz.fido.network.domain.model.chat

data class ForwardMessageResponse(
    val code: Int,
    val msg: String,
    val msg_list: ArrayList<MessageHistory>
)