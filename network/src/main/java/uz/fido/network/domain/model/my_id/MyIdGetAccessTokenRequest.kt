package uz.fido.network.domain.model.my_id

data class MyIdGetAccessTokenRequest(
    val grant_type: String? = null,
    val code: String,
    val client_id: String? = null,
    val client_secret: String? = null,
    val redirect_url: String? = null
)