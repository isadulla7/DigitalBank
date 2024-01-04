package uz.fido.network.domain.model.abc_base

data class UserInfo(
    val query: String,
    val country: String,
    val city: String,
    val region: String,
    val lat: String,
    val lon: String,
    val isp: String
)