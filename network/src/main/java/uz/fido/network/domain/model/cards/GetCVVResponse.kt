package uz.fido.network.domain.model.cards

data class GetCVVResponse(
    val code: Int,
    val msg: String,
    val request_id: Int,
    val securityCode: String
)