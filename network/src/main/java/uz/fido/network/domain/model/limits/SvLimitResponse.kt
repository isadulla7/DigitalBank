package uz.fido.network.domain.model.limits

data class SvLimitResponse(
    val code: Int,
    val msg: String,
    val limitInfos: ArrayList<SvLimit>
)