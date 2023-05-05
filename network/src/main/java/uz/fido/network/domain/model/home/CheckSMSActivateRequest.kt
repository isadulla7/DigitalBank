package uz.fido.network.domain.model.home

data class CheckSMSActivateRequest(
    val object_value: String,
    val app_key_hash: String
)