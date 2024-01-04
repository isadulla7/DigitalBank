package uz.fido.network.domain.model.limits

import uz.fido.network.domain.model.payment.AllServiceLists

data class LimitParamsResponse(
    val code: Int,
    val msg: String,
    val cycle_type: ArrayList<AllServiceLists>,
    val limit_id: ArrayList<AllServiceLists>
)