package uz.fido.network.domain.model.limits

data class LimitDeleteRequest(
    val limit_id: String,
    val main_object_value: String,
    val object_value: String
)