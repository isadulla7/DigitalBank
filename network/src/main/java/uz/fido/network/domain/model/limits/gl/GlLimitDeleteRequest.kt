package uz.fido.network.domain.model.limits.gl

data class GlLimitDeleteRequest(
    val object_id: String,
    var limit_type: String
)