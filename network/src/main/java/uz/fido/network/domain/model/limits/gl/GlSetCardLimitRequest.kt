package uz.fido.network.domain.model.limits.gl

data class GlSetCardLimitRequest(
    val date_to: String,
    val date_from: String,
    val limit_amount: String,
    val limit_id: String,
    val limit_name: String,
    val object_id: String
)