package uz.fido.network.domain.model.amount_requests

data class RmGetAllowedSumResponse(
    val object_id: String,
    val card_number: String,
    val user_name: String,
    val list_id: String,
    val allowed_sum: String,
    val request_id: String,
    val code: String,
    val card_expiry: String,
    val msg: String,

)