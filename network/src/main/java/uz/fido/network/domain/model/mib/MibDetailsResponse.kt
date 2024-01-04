package uz.fido.network.domain.model.mib

import java.util.*

data class MibDetailsResponse(
    var result_message: String? = null,
    var code: Int,
    var debets: ArrayList<MibDetail>? = null,
    var all_debet_sum: String? = null
)