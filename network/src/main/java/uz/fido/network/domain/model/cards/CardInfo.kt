package uz.fido.network.domain.model.cards

data class CardInfo(
    val overdraft_limit: String,
    val pin_counter: Int,
    val processing_server_status: Int,
    val state: String,
    val balance: String,
    val object_id: Int,
    val object_status: String? = null
)