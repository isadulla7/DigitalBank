package uz.fido.network.domain.model.humo_pay

data class NfcResponse(
    val code: Int,
    val msg: String,
    val response_type: String? = null,
    val exception_type: String? = null,
    val exception_msg: String? = null,
    val resp_body: String? = null,
    val resp_code: Int? = null
)