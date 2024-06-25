package uz.fido.network.domain.model.deposits.constructor

import com.google.gson.annotations.SerializedName

data class BxmListResponse(
    @SerializedName("request_id")
    val requestId: String,
    val list: List<BxmCodeAndName>? = emptyList(),
    val code: Int,
    val msg: String
)

data class BxmCodeAndName(
    val bxm_code: String,
    val name: String
)