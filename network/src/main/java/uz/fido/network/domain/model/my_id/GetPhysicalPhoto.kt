package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class GetPhysicalPhoto(
    val passport_seria: String,
    val passport_number: String,
    val date_birth: String,
    val agreement: Int = 1
) : Serializable