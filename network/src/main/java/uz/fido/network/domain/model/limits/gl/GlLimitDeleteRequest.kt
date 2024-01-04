package uz.fido.network.domain.model.limits.gl

data class GlLimitDeleteRequest(
    val object_value: String,
    var limit_type: String
)