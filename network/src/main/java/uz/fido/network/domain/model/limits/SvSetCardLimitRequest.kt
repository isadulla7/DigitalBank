package uz.fido.network.domain.model.limits

data class SvSetCardLimitRequest(
    val limit_amount: String,
    val limit_id: String,
    val object_id: String
)