package uz.fido.network.domain.model.fetch_contacts

import java.io.Serializable

data class FilteredContact(
    var phone_number: String,
    var name: String,
) : Serializable