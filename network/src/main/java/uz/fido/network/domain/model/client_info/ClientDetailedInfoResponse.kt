package uz.fido.network.domain.model.client_info

data class ClientDetailedInfoResponse(
    val client_info: ClientDetailedInfo,
    val code: Int,
    val msg: String
)