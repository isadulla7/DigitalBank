package uz.fido.network.domain.model.liveness

data class LivenessPrediction(
    val probability: String,
    val quality: String
)