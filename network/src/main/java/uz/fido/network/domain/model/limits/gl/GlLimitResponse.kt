package uz.fido.network.domain.model.limits.gl

data class GlLimitResponse(
    val limit_date_from: String,
    val code_owner: String,
    val limit_value: String,
    val limit_date_to: String,
    val limit_name:String,
    val key_value: String,
    val limit_type: String,
    val key_type: String,
    val code: Int
)