package uz.fido.network.domain.model.inn

data class InnResponse(
    val code: Int,
    val inn: String,
    val msg: String,
    val ora_msg: String
)