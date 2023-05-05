package uz.fido.network.domain.model.contacts

data class CheckContactsResponse(
    val code: Int,
    val exists_contacts: List<String>,
    val msg: String,
    val request_id: Int
)