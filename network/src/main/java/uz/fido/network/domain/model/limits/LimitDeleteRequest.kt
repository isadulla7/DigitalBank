package uz.fido.network.domain.model.limits

data class LimitDeleteRequest(
    val limit_id: String,
    val object_id: String
)