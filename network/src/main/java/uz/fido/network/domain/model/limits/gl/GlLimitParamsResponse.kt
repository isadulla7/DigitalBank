package uz.fido.network.domain.model.limits.gl

import uz.fido.network.domain.model.payment.AllServiceLists

data class GlLimitParamsResponse(
    val humo_limit_types: ArrayList<AllServiceLists>,
    val request_id: String,
    val code: Int,
    val msg: String
)

data class GlLimitParam(
    val id: Int,
    val lable: String,
    val condition: String,
    val name: String,
    val limit_type: String
)