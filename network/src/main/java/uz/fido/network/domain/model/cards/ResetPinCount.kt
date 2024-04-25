package uz.fido.network.domain.model.cards

data class ResetPinCount(
    val command: String = "card",
    val from_object_id: String,
    val from_object_expire: String,
    val verify_code: String?="",
    val phone_number: String,
    val string_line:String=""
)