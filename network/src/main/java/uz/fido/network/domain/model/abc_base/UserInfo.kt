package uz.fido.network.domain.model.abc_base

import java.io.Serializable

data class UserInfo(
    val status: String? = "",
    val query: String? = "",
    val country: String? = "",
    val city: String? = "",
    val region: String? = "",
    val lat: String? = "",
    val lon: String? = "",
    val isp: String? = ""
) : Serializable