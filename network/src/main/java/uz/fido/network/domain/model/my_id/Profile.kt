package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class Profile(
    val address: Address,
    val authentication_method: String,
    val common_data: CommonData,
    val contacts: Contacts,
    val doc_data: DocData
) : Serializable