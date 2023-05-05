package uz.fido.network.domain.model.liveness

data class LivenessModel(
    val prediction: String,
    val estimations: LivenessPrediction
)