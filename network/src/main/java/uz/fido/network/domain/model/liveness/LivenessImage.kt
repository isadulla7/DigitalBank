package uz.fido.network.domain.model.liveness

data class LivenessImage(
    val filename: String,
    val status: String,
    val liveness: LivenessModel?,
    val error: LivenessError
)