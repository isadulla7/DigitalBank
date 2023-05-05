package uz.fido.network.domain.model.cards

data class ResetPinCountCheck(
    val command: String = "card",
    val from_object_id: String,
    val phone_number: String,
    val app_key_hash: String,
    val device_code: String
)