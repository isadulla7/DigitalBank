package uz.fido.network.domain.model.humo_pay

import com.google.gson.annotations.SerializedName

data class HumoPayRequest(
    @SerializedName("user_id")
    val userId: String,
    val method: String,
    val relativeUrl: String,
    val body: String,
    val headers: String
)