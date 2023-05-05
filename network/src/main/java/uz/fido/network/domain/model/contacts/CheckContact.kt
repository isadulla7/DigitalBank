package uz.fido.network.domain.model.contacts

data class CheckContact(
    val phone_number: String,
    val username: String? = null
)