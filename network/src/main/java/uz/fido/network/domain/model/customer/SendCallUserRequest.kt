package uz.fido.network.domain.model.customer

import uz.fido.network.domain.model.fetch_contacts.FilteredContact
import java.io.Serializable

data class SendCallUserRequest(
    val contacts: ArrayList<FilteredContact>
) : Serializable