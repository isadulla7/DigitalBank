package uz.fido.network.domain.model.mib

data class MibInfoRequest(
    val client_type: String,
    val doc_value: String
)