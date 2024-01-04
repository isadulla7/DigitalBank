package uz.fido.network.domain.model.my_id

data class MyIdGetAccessTokenResponse(
    val token_type: String,
    val expires_in: String,
    val access_token: String? = null,
    val refresh_token: String,
    val code: Int = 0,
    val msg: String
)