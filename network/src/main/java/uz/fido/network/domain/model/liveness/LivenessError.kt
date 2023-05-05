package uz.fido.network.domain.model.liveness

data class LivenessError(
    val error_code: String,
    val desc: String,
    val detail: String
)