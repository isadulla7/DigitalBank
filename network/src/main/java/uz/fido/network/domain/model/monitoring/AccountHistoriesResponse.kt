package uz.fido.network.domain.model.monitoring

import java.io.Serializable

data class AccountHistoriesResponse(
    val request_id: String,
    val code: String = "20",
    val msg: String,
    val response: ArrayList<AccountHistory>
) : Serializable