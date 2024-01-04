package uz.fido.network.domain.model.cards

data class EditCardRequest(
    val object_name: String,
    val is_main: String,
    val object_id: String,
    val bg_icon_name: String,
    val safe_mode: String
)