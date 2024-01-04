package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class GetPhysicalPhotoResponse(
    val msg: String,
    val code: Int,
    val photo: String,
    var passport_seria: String,
    var passport_number: String,
    var date_birth: String
): Serializable