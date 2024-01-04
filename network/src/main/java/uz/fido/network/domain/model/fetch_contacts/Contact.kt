package uz.fido.network.domain.model.fetch_contacts

import java.io.Serializable

data class Contact(
    var phone_number: String,
    var name: String,
    var isChecked: Boolean? = false,
    var isExist: Boolean? = false,
) : Serializable