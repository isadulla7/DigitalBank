package uz.fido.network.domain.model.cards

data class CardInfo(
    val overdraft_limit: String = "",
    val pin_counter: Int = 0,
    val state: String = "",
    val state_name: String = "",
    val balance: String = "0",
    val object_id: Int = 0,
    val object_status: String? = null
)