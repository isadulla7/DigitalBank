package uz.fido.network.domain.model.popular_transfers

data class SaveToPopularTransferRequest(
    val client_id: String,
    val phone_number: String,
    val to_object_expire: String,
    val to_object_value: String,
    val user_id: String
)