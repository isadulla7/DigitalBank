package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class Address(
    val permanent_address: String?,
    val permanent_registration: PermanentRegistration,
    val temporary_address: String,
    val temporary_registration: TemporaryRegistration
) : Serializable