package uz.fido.network.domain.model.abc_base

data class APIError(
    val code: Int,
    val message: String,
    val jsonStr: String? = null
)