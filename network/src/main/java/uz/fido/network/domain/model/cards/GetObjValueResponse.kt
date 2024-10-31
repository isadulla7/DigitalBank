package uz.fido.network.domain.model.cards

data class GetObjValueResponse(
    val object_value: String,
    val code: Int,
    val msg: String,
    val request_id: Int
)