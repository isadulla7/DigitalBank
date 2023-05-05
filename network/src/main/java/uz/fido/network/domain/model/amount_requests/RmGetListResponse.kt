package uz.fido.network.domain.model.amount_requests

import java.io.Serializable

data class RmGetListResponse(
    val request_id: Int,
    val code: String,
    var rm_list: ArrayList<RmList>,
    val msg: String
) : Serializable