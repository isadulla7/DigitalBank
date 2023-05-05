package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class Contacts(
    val email: String,
    val phone: String
) : Serializable